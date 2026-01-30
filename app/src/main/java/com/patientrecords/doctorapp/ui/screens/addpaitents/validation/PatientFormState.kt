package com.patientrecords.doctorapp.ui.screens.addpaitents.validation

import android.net.Uri
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Gender

/**
 * Represents the current state of the Add Patient form
 */
data class PatientFormState(
    // Field values
    val fullName: String = "",
    val mobileNumber: String = "",
    val age: String = "",
    val gender: Gender? = null,
    val address: String = "",
    val photoUri: Uri? = null,
    
    // Validation errors (null means no error)
    val fullNameError: String? = null,
    val mobileNumberError: String? = null,
    val ageError: String? = null,
    val genderError: String? = null,
    
    // Form state
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val uploadedPhotoUrl: String? = null,
    
    // Submission result
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Check if the form is valid (no errors)
     */
    val isValid: Boolean
        get() = fullNameError == null &&
                mobileNumberError == null &&
                ageError == null &&
                genderError == null &&
                fullName.isNotBlank() &&
                mobileNumber.isNotBlank() &&
                age.isNotBlank() &&
                gender != null
    
    /**
     * Check if form has any errors displayed
     */
    val hasErrors: Boolean
        get() = fullNameError != null ||
                mobileNumberError != null ||
                ageError != null ||
                genderError != null
    
    /**
     * Check if all required fields are filled
     */
    val isComplete: Boolean
        get() = fullName.isNotBlank() &&
                mobileNumber.isNotBlank() &&
                age.isNotBlank() &&
                gender != null
}

/**
 * Events that can be triggered from the Add Patient form
 */
sealed interface PatientFormEvent {
    data class FullNameChanged(val value: String) : PatientFormEvent
    data class MobileNumberChanged(val value: String) : PatientFormEvent
    data class AgeChanged(val value: String) : PatientFormEvent
    data class GenderSelected(val gender: Gender) : PatientFormEvent
    data class AddressChanged(val value: String) : PatientFormEvent
    data class PhotoSelected(val uri: Uri) : PatientFormEvent
    data object PhotoRemoved : PatientFormEvent
    data object Submit : PatientFormEvent
    data object ClearError : PatientFormEvent
    data object ResetForm : PatientFormEvent
}

/**
 * Result of form submission
 */
sealed interface FormSubmissionResult {
    data class Success(val patientId: String) : FormSubmissionResult
    data class Error(val message: String) : FormSubmissionResult
    data object Loading : FormSubmissionResult
}
