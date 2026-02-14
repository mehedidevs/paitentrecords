package com.patientrecords.doctorapp.addmedicine


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import kotlinx.coroutines.launch

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
