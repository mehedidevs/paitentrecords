package com.patientrecords.doctorapp.ui.patientdetils.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.firstOrNull
import kotlin.collections.map

data class PatientProfileUiState(
    val patient: Patient? = null,
    val totalVisits: Int = 0,
    val lastVisitDate: String? = null,
    val visitHistory: List<VisitHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class VisitHistoryItem(
    val id: String,
    val date: String,
    val diagnosis: String,
    val prescribedMedicine: String? = null
)

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