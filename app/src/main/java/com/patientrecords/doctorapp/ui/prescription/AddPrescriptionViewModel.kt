package com.patientrecords.doctorapp.ui.prescription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.models.Medicine
import com.patientrecords.doctorapp.domain.models.Prescription
import com.patientrecords.doctorapp.domain.models.Visit
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.filter
import kotlin.collections.toMutableList
import kotlin.fold
data class AddPrescriptionUiState(
    val patientId: String = "",
    val patientName: String = "",
    val queryName: String? = null,
    val visitDate: String = "",
    val symptoms: String = "",
    val diagnosis: String = "",
    val prescriptions: List<PrescriptionFormItem> = listOf(PrescriptionFormItem()),
    val medicineSearchState: MedicineSearchState = MedicineSearchState(),
    val currentEditingIndex: Int? = null,
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MedicineSearchState(
    val query: String = "",
    val results: List<Medicine> = emptyList(),
    val isSearching: Boolean = false,
    val selectedMedicine: Medicine? = null
)

data class PrescriptionFormItem(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String? = null,
    val medicineName: String? = null,
    val typedName: String = "",
    val potency: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val durationDays: Int = 7,
    val price: Double = 0.0,
    val isComplete: Boolean = false,

    // 🔥 NEW — row-level search state
    val searchState: MedicineSearchState = MedicineSearchState()
)


class AddPrescriptionViewModel(
    private val patientId: String,
    private val patientName: String,
    private val visitDate: String,
    private val symptoms: String,
    private val diagnosis: String,
    private val repository: HealthcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddPrescriptionUiState(
            patientName = patientName,
            visitDate = visitDate,
            patientId = patientId,
            symptoms = symptoms,
            diagnosis = diagnosis,
            prescriptions = listOf(PrescriptionFormItem())
        )
    )
    val uiState: StateFlow<AddPrescriptionUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun uploadPrescription(onSuccess: (String) -> Unit, onError: (String?) -> Unit) {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value

            // 1) Build Visit
            val visit = Visit(
                patientId = state.patientId,
                date = state.visitDate,   // or LocalDate.now().toString()
                symptoms = state.symptoms,
                diagnosis = state.diagnosis.takeIf { it.isNotBlank() },
                consultationFeeApplied = true,   // or your UI flag
                consultationFee = 500.0,         // adjust as needed
                discount = 0.0
            )

            // 2) Take only completed prescriptions
            val prescriptions = state.prescriptions
                .filter { it.isComplete }
                .map { form ->
                    Prescription(
                        visitId = "",
                        medicineId = form.medicineId ?: "ManualinputMedicine_${UUID.randomUUID()}",
                        medicineName = form.medicineName ?: _uiState.value.queryName ?: "NO NAME",
                        potency = form.potency.ifBlank { "NA" },
                        dosage = form.dosage.ifBlank { "As directed" },   // 🔥 DEFAULT
                        frequency = form.frequency.ifBlank { "Once daily" }, // 🔥 DEFAULT
                        durationDays = form.durationDays ?: 0,
                        price = form.price ?: 0.0
                    )

                }

            // 3) Call Supabase transaction
            val result = repository.saveVisitTransaction(visit, prescriptions)

            result.fold(
                onSuccess = { saved ->
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(saved.visit.id)   // pass new visit id back to UI
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    onError(error.message)
                }
            )
        }
    }


    // =========================
    // 🔹 SEARCH MEDICINE (SAFE)
    // =========================
    fun searchMedicines(index: Int, query: String) {

        updatePrescription(index) { p ->
            p.copy(
                typedName = query,   // ✅ SAVE TYPED NAME
                searchState = p.searchState.copy(
                    query = query,
                    isSearching = true
                )
            )
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)

            val result = repository.searchMedicines(query)

            result.fold(
                onSuccess = { medicines ->
                    updatePrescription(index) { p ->
                        p.copy(
                            searchState = p.searchState.copy(
                                results = medicines,
                                isSearching = false
                            )
                        )
                    }
                },
                onFailure = {
                    updatePrescription(index) { p ->
                        p.copy(
                            searchState = p.searchState.copy(
                                results = emptyList(),
                                isSearching = false
                            )
                        )
                    }
                }
            )
        }
    }


    // =========================
    // 🔹 SELECT MEDICINE (FIXED)
    // =========================

    fun selectMedicine(index: Int, medicine: Medicine) {
        updatePrescription(index) { p ->
            val updated = p.copy(
                medicineId = medicine.id,
                medicineName = medicine.name,
                price = medicine.pricePerUnit,
                searchState = p.searchState.copy(
                    query = medicine.name,
                    results = emptyList(),
                    isSearching = false
                )
            )

            // ✅ AUTO-MARK COMPLETE IF REQUIRED FIELDS EXIST
            /* val isComplete =
                 updated.medicineName.isNotBlank() &&
                         updated.potency.isNotBlank() &&
                         updated.dosage.isNotBlank() &&
                         updated.frequency.isNotBlank()*/

            updated.copy()
        }
    }


    // =========================
    // 🔹 FIELD UPDATES
    // =========================
    fun updatePotency(index: Int, potency: String) =
        updatePrescription(index) { it.copy(potency = potency) }

    fun updateDosage(index: Int, dosage: String) =
        updatePrescription(index) { it.copy(dosage = dosage) }

    fun updateFrequency(index: Int, frequency: String) =
        updatePrescription(index) { it.copy(frequency = frequency) }

    fun updateDays(index: Int, days: Int) =
        updatePrescription(index) { it.copy(durationDays = days) }

    fun updatePrice(index: Int, price: Double) =
        updatePrescription(index) { it.copy(price = price) }

    fun onCompleted(index: Int) =
        updatePrescription(index) { it.copy(isComplete = true) }

    // =========================
    // 🔹 ADD / REMOVE ROWS
    // =========================
    fun addMedicine() {
        val updated = _uiState.value.prescriptions + PrescriptionFormItem()
        updateTotals(updated)
    }

    fun removeMedicine(index: Int) {
        val list = _uiState.value.prescriptions.toMutableList()
        if (list.size > 1 && index in list.indices) {
            list.removeAt(index)
            updateTotals(list)
        }
    }

    // =========================
    // 🔹 MARK COMPLETE
    // =========================
    fun markAsComplete(index: Int) {
        updatePrescription(index) { p ->

            val finalName =
                if (!p.medicineName.isNullOrBlank())
                    p.medicineName
                else
                    p.typedName   // ✅ fallback when no search result

            val isComplete =
                finalName.isNotBlank() /*&&
                        p.potency.isNotBlank() &&
                        p.dosage.isNotBlank() &&
                        p.frequency.isNotBlank()*/

            p.copy(
                medicineName = finalName,
                medicineId = p.medicineId, // may be null → manual entry
                isComplete = isComplete
            )
        }
    }


    // =========================
    // 🔹 INTERNAL HELPERS
    // =========================
    private fun updatePrescription(
        index: Int,
        transform: (PrescriptionFormItem) -> PrescriptionFormItem
    ) {
        val list = _uiState.value.prescriptions.toMutableList()

        if (index in list.indices) {
            list[index] = transform(list[index])
            updateTotals(list)
        }
    }

    private fun updateTotals(prescriptions: List<PrescriptionFormItem>) {
        val totalPrice = prescriptions.sumOf { it.price }
        val totalItems = prescriptions.count { it.isComplete }

        _uiState.update {
            it.copy(
                prescriptions = prescriptions,
                totalPrice = totalPrice,
                totalItems = totalItems
            )
        }
    }

    fun getPrescriptions(): List<PrescriptionFormItem> =
        _uiState.value.prescriptions
}