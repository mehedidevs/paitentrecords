package com.patientrecords.doctorapp.ui.screens.patientlist


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.addpaitents.components.GetPatientResult
import com.patientrecords.doctorapp.addpaitents.components.GetPatientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientViewModel(
    private val getPatientUseCase: GetPatientUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<GetPatientResult>(GetPatientResult.Loading)

    val uiState: StateFlow<GetPatientResult> = _uiState.asStateFlow()

    init {
        loadPatients()
    }

    fun loadPatients() {
        viewModelScope.launch {
            getPatientUseCase()
                .collect { result ->
                    _uiState.value = result
                }
        }
    }

    fun refreshPatients() {
        loadPatients()
    }
}
