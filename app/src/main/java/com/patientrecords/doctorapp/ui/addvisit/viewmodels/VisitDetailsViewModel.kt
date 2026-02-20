package com.patientrecords.doctorapp.ui.addvisit.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.domain.models.Prescription
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.fold

class VisitDetailsViewModel(
    private val patientId: String,
    private val visitId: String,
    private val repository: HealthcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitDetailsUiState())
    val uiState: StateFlow<VisitDetailsUiState> = _uiState.asStateFlow()

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
                                            consultationFeeEnabled = true,
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


    private fun calculateGrandTotal(
        medicineTotal: Double,
        consultationFee: Double,
        discount: Double
    ): Double {
        return (medicineTotal + consultationFee - discount).coerceAtLeast(0.0)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class VisitDetailsUiState(
    val patient: Patient? = null,
    val prescriptions: List<Prescription> = emptyList(),
    val medicineTotal: Double = 0.0,
    val consultationFee: Double = 500.0,
    val consultationFeeEnabled: Boolean = true,
    val discount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)