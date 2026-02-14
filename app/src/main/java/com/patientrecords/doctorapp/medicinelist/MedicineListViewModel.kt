package com.patientrecords.doctorapp.medicinelist

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.medicine.MedicineRepository
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import kotlinx.coroutines.launch

class MedicineListViewModel(
    private val repo: MedicineRepository = MedicineRepository()
) : ViewModel() {

    var medicines by mutableStateOf<List<Medicine>>(emptyList())
        private set

    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadMedicines() {
        viewModelScope.launch {
            loading = true
            try {
                medicines = repo.getAll()
                Log.d("MedicineListViewModel", "loadMedicines: ${medicines} ")
            } catch (e: Exception) {
                Log.d("MedicineListViewModel", "eroor: ${e} ")

                error = e.message
            }
            loading = false
        }
    }
}
