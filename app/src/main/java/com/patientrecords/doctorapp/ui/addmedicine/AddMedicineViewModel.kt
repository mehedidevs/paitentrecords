package com.patientrecords.doctorapp.ui.addmedicine


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.data.reposimpl.MedicineRepository
import com.patientrecords.doctorapp.domain.models.Medicine
import kotlinx.coroutines.launch
sealed interface AddMedicineUiState {
    object Idle : AddMedicineUiState
    object Loading : AddMedicineUiState
    object Success : AddMedicineUiState
    data class Error(val message: String) : AddMedicineUiState
}

class AddMedicineViewModel(
    private val repository: MedicineRepository = MedicineRepository()
) : ViewModel() {

    var uiState: AddMedicineUiState = AddMedicineUiState.Idle
        private set

    fun saveMedicine(data: Medicine) {
        uiState = AddMedicineUiState.Loading

        viewModelScope.launch {
            try {
                repository.insertMedicine(data)
                uiState = AddMedicineUiState.Success
            } catch (e: Exception) {
                uiState = AddMedicineUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }
}
