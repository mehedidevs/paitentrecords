package com.patientrecords.doctorapp.addmedicine


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.ui.screens.addmedicine.MedicineData
import kotlinx.coroutines.launch

class AddMedicineViewModel(
    private val repository: MedicineRepository = MedicineRepository()
) : ViewModel() {

    var uiState: AddMedicineUiState = AddMedicineUiState.Idle
        private set

    fun saveMedicine(data: MedicineData) {
        uiState = AddMedicineUiState.Loading

        viewModelScope.launch {
            try {
                repository.insertMedicine(data.toDto())
                uiState = AddMedicineUiState.Success
            } catch (e: Exception) {
                uiState = AddMedicineUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }
}
