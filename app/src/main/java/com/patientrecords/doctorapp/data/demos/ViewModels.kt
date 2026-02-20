/*
package com.patientrecords.doctorapp.ui.patientdetils.ui.screens

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
import com.patientrecords.doctorapp.ui.patientdetils.data.PatientProfileUiState
import com.patientrecords.doctorapp.ui.patientdetils.domain.HealthcareRepository
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
import kotlin.collections.firstOrNull
import kotlin.collections.map
import kotlin.fold

// ==================== Patient Profile ViewModel ====================



// ==================== New Visit ViewModel ====================



// ==================== Add Prescription ViewModel ====================




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
*/
