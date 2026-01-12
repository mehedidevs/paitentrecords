package com.patientrecords.doctorapp.addmedicine


sealed interface AddMedicineUiState {
    object Idle : AddMedicineUiState
    object Loading : AddMedicineUiState
    object Success : AddMedicineUiState
    data class Error(val message: String) : AddMedicineUiState
}
