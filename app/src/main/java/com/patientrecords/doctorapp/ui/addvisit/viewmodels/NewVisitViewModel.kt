package com.patientrecords.doctorapp.ui.addvisit.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.models.Prescription
import com.patientrecords.doctorapp.domain.models.Visit
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import com.patientrecords.doctorapp.ui.prescription.PrescriptionFormItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.filter
import kotlin.fold

data class NewVisitUiState(
    val date: LocalDate = LocalDate.now(),
    val symptoms: String = "",
    val diagnosis: String = "",
    val consultationFeeApplied: Boolean = true,
    val consultationFee: Double = 500.0,
    val prescriptions: List<PrescriptionFormItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

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