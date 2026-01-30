package com.patientrecords.doctorapp.ui.screens.addpaitents

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Gender
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormEvent
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormState
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AddPatientParams
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AddPatientResult
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AddPatientUseCase
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormValidator
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.errorOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Add Patient screen
 * Handles form state, validation, and submission
 *
 * Note: ViewModel only manages UI state - all business logic is in UseCase and Validator
 */
class AddPatientViewModel(
    private val addPatientUseCase: AddPatientUseCase,
    private val validator: PatientFormValidator
) : ViewModel() {

    private val _formState = MutableStateFlow(PatientFormState())
    val formState: StateFlow<PatientFormState> = _formState.asStateFlow()
    
    // One-time events (navigation, snackbar, etc.)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    /**
     * Handle form events
     */
    fun onEvent(event: PatientFormEvent) {
        when (event) {
            is PatientFormEvent.FullNameChanged -> {
                updateFullName(event.value)
            }
            is PatientFormEvent.MobileNumberChanged -> {
                updateMobileNumber(event.value)
            }
            is PatientFormEvent.AgeChanged -> {
                updateAge(event.value)
            }
            is PatientFormEvent.GenderSelected -> {
                updateGender(event.gender)
            }
            is PatientFormEvent.AddressChanged -> {
                updateAddress(event.value)
            }
            is PatientFormEvent.PhotoSelected -> {
                updatePhoto(event.uri)
            }
            is PatientFormEvent.PhotoRemoved -> {
                removePhoto()
            }
            is PatientFormEvent.Submit -> {
                // Will be called with context from UI
            }
            is PatientFormEvent.ClearError -> {
                clearError()
            }
            is PatientFormEvent.ResetForm -> {
                resetForm()
            }
        }
    }

    /**
     * Submit form with context (needed for photo upload)
     */
    fun submitForm(context: Context) {
        viewModelScope.launch {
            // First validate all fields
            val validationResult = validator.validateForm(
                fullName = _formState.value.fullName,
                mobileNumber = _formState.value.mobileNumber,
                age = _formState.value.age,
                gender = _formState.value.gender,
                address = _formState.value.address
            )
            
            // Update errors
            _formState.update { currentState ->
                currentState.copy(
                    fullNameError = validationResult.fullNameError,
                    mobileNumberError = validationResult.mobileNumberError,
                    ageError = validationResult.ageError,
                    genderError = validationResult.genderError
                )
            }
            
            if (!validationResult.isValid) {
                _uiEvent.send(UiEvent.ShowSnackbar("Please fix the errors in the form"))
                return@launch
            }
            
            // Execute add patient use case
            val params = AddPatientParams(
                fullName = _formState.value.fullName,
                mobileNumber = _formState.value.mobileNumber,
                age = _formState.value.age,
                gender = _formState.value.gender,
                address = _formState.value.address,
                photoUri = _formState.value.photoUri
            )
            
            addPatientUseCase.execute(params, context).collect { result ->
                handleAddPatientResult(result)
            }
        }
    }

    /**
     * Handle results from add patient use case
     */
    private suspend fun handleAddPatientResult(result: AddPatientResult) {
        when (result) {
            is AddPatientResult.Loading -> {
                _formState.update { it.copy(isSubmitting = true, errorMessage = null) }
            }
            is AddPatientResult.UploadingPhoto -> {
                _formState.update { it.copy(isUploadingPhoto = true) }
            }
            is AddPatientResult.CreatingPatient -> {
                _formState.update { it.copy(isUploadingPhoto = false) }
            }
            is AddPatientResult.ValidationError -> {
                _formState.update { currentState ->
                    currentState.copy(
                        isSubmitting = false,
                        fullNameError = result.result.fullNameError,
                        mobileNumberError = result.result.mobileNumberError,
                        ageError = result.result.ageError,
                        genderError = result.result.genderError
                    )
                }
            }
            is AddPatientResult.PhotoUploadError -> {
                _formState.update { currentState ->
                    currentState.copy(
                        isSubmitting = false,
                        isUploadingPhoto = false,
                        errorMessage = result.message
                    )
                }
                _uiEvent.send(UiEvent.ShowSnackbar("Failed to upload photo: ${result.message}"))
            }
            is AddPatientResult.Error -> {
                _formState.update { currentState ->
                    currentState.copy(
                        isSubmitting = false,
                        errorMessage = result.message
                    )
                }
                _uiEvent.send(UiEvent.ShowSnackbar("Error: ${result.message}"))
            }
            is AddPatientResult.Success -> {
                _formState.update { currentState ->
                    currentState.copy(
                        isSubmitting = false,
                        isSuccess = true
                    )
                }
                _uiEvent.send(UiEvent.ShowSnackbar("Patient added successfully!"))
                _uiEvent.send(UiEvent.NavigateBack)
            }
        }
    }

    // ============== Field Update Methods ==============

    private fun updateFullName(value: String) {
        _formState.update { currentState ->
            val error = if (currentState.fullNameError != null) {
                validator.validateFullName(value).errorOrNull()
            } else null
            currentState.copy(fullName = value, fullNameError = error)
        }
    }

    private fun updateMobileNumber(value: String) {
        // Only allow digits
        val digitsOnly = value.filter { it.isDigit() }.take(10)
        _formState.update { currentState ->
            val error = if (currentState.mobileNumberError != null) {
                validator.validateMobileNumber(digitsOnly).errorOrNull()
            } else null
            currentState.copy(mobileNumber = digitsOnly, mobileNumberError = error)
        }
    }

    private fun updateAge(value: String) {
        _formState.update { currentState ->
            val error = if (currentState.ageError != null) {
                validator.validateAge(value).errorOrNull()
            } else null
            currentState.copy(age = value, ageError = error)
        }
    }

    private fun updateGender(gender: Gender) {
        _formState.update { currentState ->
            currentState.copy(gender = gender, genderError = null)
        }
    }

    private fun updateAddress(value: String) {
        _formState.update { currentState ->
            currentState.copy(address = value)
        }
    }

    private fun updatePhoto(uri: Uri) {
        _formState.update { currentState ->
            currentState.copy(photoUri = uri)
        }
    }

    private fun removePhoto() {
        _formState.update { currentState ->
            currentState.copy(photoUri = null, uploadedPhotoUrl = null)
        }
    }

    private fun clearError() {
        _formState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    private fun resetForm() {
        _formState.value = PatientFormState()
    }

    /**
     * Validate a single field on focus lost
     */
    fun validateField(fieldName: String) {
        _formState.update { currentState ->
            when (fieldName) {
                "fullName" -> currentState.copy(
                    fullNameError = validator.validateFullName(currentState.fullName).errorOrNull()
                )
                "mobileNumber" -> currentState.copy(
                    mobileNumberError = validator.validateMobileNumber(currentState.mobileNumber).errorOrNull()
                )
                "age" -> currentState.copy(
                    ageError = validator.validateAge(currentState.age).errorOrNull()
                )
                else -> currentState
            }
        }
    }
}

/**
 * One-time UI events
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data object NavigateBack : UiEvent
}
