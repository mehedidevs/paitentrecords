package com.patientrecords.doctorapp.ui.medicinelist.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.medicine.MedicineRepository
import kotlinx.coroutines.launch


import com.patientrecords.doctorapp.ui.addmedicine.AddMedicineState

sealed interface EditMedicineUiState {
    object Loading : EditMedicineUiState
    object Success : EditMedicineUiState
    data class Error(val message: String) : EditMedicineUiState
}

class EditMedicineViewModel(
    private val repository: MedicineRepository = MedicineRepository()
) : ViewModel() {

    var uiState by mutableStateOf<EditMedicineUiState>(
        EditMedicineUiState.Loading
    )
        private set

    val formState = AddMedicineState()

    fun loadMedicine(medicineId: String) {
        uiState = EditMedicineUiState.Loading

        viewModelScope.launch {
            try {
                val medicine = repository.getById(medicineId)

                formState.medicineName = medicine.name
                formState.selectedPotency = medicine.potency
                formState.dosageForm = medicine.dosageForm
                formState.durationUnit = medicine.durationUnit
                formState.defaultPrice = medicine.defaultPrice.toString()

                uiState = EditMedicineUiState.Success
            } catch (e: Exception) {
                uiState = EditMedicineUiState.Error(
                    e.message ?: "Failed to load medicine"
                )
            }
        }
    }

    fun updateMedicine(
        medicineId: String,
        onDone: () -> Unit
    ) {
        uiState = EditMedicineUiState.Loading

        viewModelScope.launch {
            try {
                repository.updateAndFetch(
                    id = medicineId,
                    data = formState.toMedicineData()
                )
                val refreshed = repository.getById(medicineId)
                println("Updated medicine: $refreshed")
                uiState = EditMedicineUiState.Success
                onDone()
            } catch (e: Exception) {
                uiState = EditMedicineUiState.Error(
                    e.message ?: "Update failed"
                )
            }
        }
    }
}
