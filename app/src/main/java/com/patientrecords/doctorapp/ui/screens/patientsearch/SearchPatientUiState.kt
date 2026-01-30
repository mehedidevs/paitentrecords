package com.patientrecords.doctorapp.ui.screens.patientsearch

import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Patient

sealed interface SearchPatientUiState {

    data object Idle : SearchPatientUiState

    data object Loading : SearchPatientUiState

    data class Success(
        val patients: List<Patient>
    ) : SearchPatientUiState

    data class Empty(
        val query: String
    ) : SearchPatientUiState

    data class Error(
        val message: String
    ) : SearchPatientUiState
}
