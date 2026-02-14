package com.patientrecords.doctorapp.patientdetils.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.patientdetils.data.BillSummaryUiState
import com.patientrecords.doctorapp.patientdetils.data.DoctorNote
import com.patientrecords.doctorapp.patientdetils.data.DoctorNotesUiState
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import com.patientrecords.doctorapp.patientdetils.data.MedicineSearchState
import com.patientrecords.doctorapp.patientdetils.data.NewVisitUiState
import com.patientrecords.doctorapp.patientdetils.data.PatientProfileUiState
import com.patientrecords.doctorapp.patientdetils.data.PaymentStatus
import com.patientrecords.doctorapp.patientdetils.data.Prescription
import com.patientrecords.doctorapp.patientdetils.data.PrescriptionFormItem
import com.patientrecords.doctorapp.patientdetils.data.Visit
import com.patientrecords.doctorapp.patientdetils.data.VisitHistoryItem
import com.patientrecords.doctorapp.patientdetils.domain.HealthcareRepository
import com.patientrecords.doctorapp.patientdetils.ui.screens.prescription.AddPrescriptionUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

// ==================== Patient Profile ViewModel ====================

class PatientProfileViewModel(
    private val patientId: String,
    private val repository: HealthcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientProfileUiState())
    val uiState: StateFlow<PatientProfileUiState> = _uiState.asStateFlow()

    init {
        loadPatientData()
    }

    fun loadPatientData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load patient
            val patientResult = repository.getPatient(patientId)

            // Load visits
            val visitsResult = repository.getVisitsByPatient(patientId)

            patientResult.fold(
                onSuccess = { patient ->
                    visitsResult.fold(
                        onSuccess = { visits ->
                            val visitHistory = visits.map { visit ->
                                VisitHistoryItem(
                                    id = visit.id,
                                    date = formatDate(visit.date),
                                    diagnosis = visit.diagnosis ?: visit.symptoms.take(50),
                                    prescribedMedicine = null // Would need to fetch prescriptions
                                )
                            }

                            _uiState.update {
                                it.copy(
                                    patient = patient,
                                    totalVisits = visits.size,
                                    lastVisitDate = visits.firstOrNull()
                                        ?.let { v -> formatDate(v.date) },
                                    visitHistory = visitHistory,
                                    isLoading = false
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    patient = patient,
                                    isLoading = false,
                                    error = error.message
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
            )
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString.take(10))
            date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e: Exception) {
            dateString
        }
    }
}

// ==================== New Visit ViewModel ====================

class NewVisitViewModel(
    private val patientId: String,
    private val repository: HealthcareRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewVisitUiState())
    val uiState: StateFlow<NewVisitUiState> = _uiState.asStateFlow()

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateSymptoms(symptoms: String) {
        _uiState.update { it.copy(symptoms = symptoms) }
    }

    fun updateDiagnosis(diagnosis: String) {
        _uiState.update { it.copy(diagnosis = diagnosis) }
    }

    fun toggleConsultationFee(applied: Boolean) {
        _uiState.update { it.copy(consultationFeeApplied = applied) }
    }

    fun updatePrescriptions(prescriptions: List<PrescriptionFormItem>) {
        _uiState.update { it.copy(prescriptions = prescriptions) }
    }

    fun saveVisit(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value

            val visit = Visit(
                patientId = patientId,
                date = state.date.toString(),
                symptoms = state.symptoms,
                diagnosis = state.diagnosis.takeIf { it.isNotBlank() },
                consultationFeeApplied = state.consultationFeeApplied,
                consultationFee = state.consultationFee
            )

            val prescriptions = state.prescriptions
                .filter { it.isComplete }
                .map { form ->
                    Prescription(
                        visitId = "", // Will be set by repository
                        medicineId = form.medicineId ?: "",
                        medicineName = form?.medicineName ?: "",
                        potency = form.potency,
                        dosage = form.dosage,
                        frequency = form.frequency,
                        durationDays = form.durationDays,
                        price = form.price
                    )
                }

            val result = repository.saveVisitTransaction(visit, prescriptions)

            result.fold(
                onSuccess = { savedData ->
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                    onSuccess(savedData.visit.id)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}

// ==================== Add Prescription ViewModel ====================

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


// ==================== Doctor Notes ViewModel ====================

class DoctorNotesViewModel(
    private val patientId: String,
    private val visitId: String?,
    private val repository: HealthcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorNotesUiState())
    val uiState: StateFlow<DoctorNotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            val result = repository.getDoctorNotes(patientId, visitId)

            result.fold(
                onSuccess = { note ->
                    _uiState.update {
                        it.copy(
                            content = note?.content ?: "",
                            lastEditedAt = note?.lastEditedAt
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun saveNotes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val note = DoctorNote(
                patientId = patientId,
                visitId = visitId,
                content = _uiState.value.content
            )

            val result = repository.saveDoctorNotes(note)

            result.fold(
                onSuccess = { savedNote ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            isSaved = true,
                            lastEditedAt = savedNote.lastEditedAt
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.message) }
                }
            )
        }
    }
}

// ==================== Bill Summary ViewModel ====================

class BillSummaryViewModel(
    private val patientId: String,
    private val visitId: String,
    private val repository: HealthcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillSummaryUiState())
    val uiState: StateFlow<BillSummaryUiState> = _uiState.asStateFlow()

    init {
        loadBillData()
    }

    private fun loadBillData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val patientResult = repository.getPatient(patientId)
            val visitResult = repository.getVisit(visitId)
            val prescriptionsResult = repository.getPrescriptionsByVisit(visitId)

            patientResult.fold(
                onSuccess = { patient ->
                    visitResult.fold(
                        onSuccess = { visit ->
                            prescriptionsResult.fold(
                                onSuccess = { prescriptions ->
                                    val medicineTotal = prescriptions.sumOf { it.price }
                                    val consultationFee = visit?.consultationFee ?: 500.0
                                    val discount = visit?.discount ?: 0.0
                                    val grandTotal = medicineTotal + consultationFee - discount

                                    _uiState.update {
                                        it.copy(
                                            patient = patient,
                                            prescriptions = prescriptions,
                                            medicineTotal = medicineTotal,
                                            consultationFee = consultationFee,
                                            discount = discount,
                                            grandTotal = grandTotal,
                                            isLoading = false
                                        )
                                    }
                                },
                                onFailure = { error ->
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            error = error.message
                                        )
                                    }
                                }
                            )
                        },
                        onFailure = { error ->
                            _uiState.update { it.copy(isLoading = false, error = error.message) }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun updateDiscount(discount: Double) {
        val state = _uiState.value
        val newGrandTotal = state.medicineTotal + state.consultationFee - discount
        _uiState.update {
            it.copy(discount = discount, grandTotal = newGrandTotal)
        }
    }

    fun toggleConsultationFee(enabled: Boolean) {
        val state = _uiState.value
        val consultationFee = if (enabled) 500.0 else 0.0
        val newGrandTotal = state.medicineTotal + consultationFee - state.discount
        _uiState.update {
            it.copy(consultationFee = consultationFee, grandTotal = newGrandTotal)
        }
    }

    fun saveVisit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value

            // Update visit with final totals
            val visitResult = repository.getVisit(visitId)

            visitResult.fold(
                onSuccess = { visit ->
                    if (visit != null) {
                        val updatedVisit = visit.copy(
                            medicineTotal = state.medicineTotal,
                            consultationFee = state.consultationFee,
                            discount = state.discount,
                            grandTotal = state.grandTotal,
                            paymentStatus = PaymentStatus.PAID
                        )

                        val updateResult = repository.updateVisit(updatedVisit)

                        updateResult.fold(
                            onSuccess = {
                                _uiState.update { it.copy(isLoading = false, isSaved = true) }
                                onSuccess()
                            },
                            onFailure = { error ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = error.message
                                    )
                                }
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
