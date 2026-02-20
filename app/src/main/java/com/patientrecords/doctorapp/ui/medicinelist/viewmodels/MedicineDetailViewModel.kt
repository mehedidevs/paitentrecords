package com.patientrecords.doctorapp.ui.medicinelist.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.domain.medicine.MedicineRepository
import com.patientrecords.doctorapp.ui.medicinelist.MedicineUiModel
import com.patientrecords.doctorapp.ui.medicinelist.toUiModel
import kotlinx.coroutines.launch

class MedicineDetailViewModel(
    private val repo: MedicineRepository = MedicineRepository()
) : ViewModel() {

    var medicine by mutableStateOf<MedicineUiModel?>(null)
        private set

    fun load(id: String) {
        viewModelScope.launch {
            medicine = repo.getById(id).toUiModel()
        }
    }
}