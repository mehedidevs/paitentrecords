package com.patientrecords.doctorapp.patientsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.addpaitents.components.PatientRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchPatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SearchPatientUiState>(SearchPatientUiState.Idle)

    val uiState: StateFlow<SearchPatientUiState> = _uiState

    private var searchJob: Job? = null

    fun search(query: String) {

        if (query.length < 2) {
            _uiState.value = SearchPatientUiState.Idle
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {

            delay(400) // debounce

            _uiState.value = SearchPatientUiState.Loading

            val result = repository.searchPatients(query)

            if (result.isFailure) {
                _uiState.value = SearchPatientUiState.Error(
                    result.exceptionOrNull()?.message ?: "Search failed"
                )
                return@launch
            }

            val list = result.getOrNull().orEmpty()

            _uiState.value =
                if (list.isEmpty())
                    SearchPatientUiState.Empty(query)
                else
                    SearchPatientUiState.Success(list)
        }
    }
}
