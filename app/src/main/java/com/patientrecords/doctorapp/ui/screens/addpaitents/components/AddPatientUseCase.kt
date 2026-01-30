package com.patientrecords.doctorapp.ui.screens.addpaitents.components

import android.content.Context
import android.net.Uri
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for adding a new patient
 * Handles business logic: validation, photo upload, and patient creation
 */
class AddPatientUseCase(
    private val repository: PatientRepository,
    private val validator: PatientFormValidator
) {
    
    /**
     * Execute the add patient flow
     * 
     * @param params Patient data parameters
     * @param context Android context for file operations
     * @return Flow emitting AddPatientResult states
     */
    fun execute(params: AddPatientParams, context: Context): Flow<AddPatientResult> = flow {
        emit(AddPatientResult.Loading)
        
        // Step 1: Validate form data
        val validationResult = validator.validateForm(
            fullName = params.fullName,
            mobileNumber = params.mobileNumber,
            age = params.age,
            gender = params.gender,
            address = params.address
        )
        
        if (!validationResult.isValid) {
            emit(AddPatientResult.ValidationError(validationResult))
            return@flow
        }
        
        // Step 2: Upload photo if provided
        var photoUrl: String? = null
        if (params.photoUri != null) {
            emit(AddPatientResult.UploadingPhoto)
            
            val uploadResult = repository.uploadPhoto(params.photoUri, context)
            if (uploadResult.isFailure) {
                emit(AddPatientResult.PhotoUploadError(
                    uploadResult.exceptionOrNull()?.message ?: "Failed to upload photo"
                ))
                return@flow
            }
            photoUrl = uploadResult.getOrNull()
        }
        
        // Step 3: Create patient record
        emit(AddPatientResult.CreatingPatient)
        
        val createRequest = CreatePatientRequest(
            fullName = params.fullName.trim(),
            mobileNumber = params.mobileNumber.trim(),
            age = params.age.toInt(),
            gender = params.gender!!,
            address = params.address.takeIf { it.isNotBlank() }?.trim(),
            photoUrl = photoUrl
        )
        
        val createResult = repository.createPatient(createRequest)
        
        if (createResult.isFailure) {
            // If patient creation fails and we uploaded a photo, try to clean up
            if (photoUrl != null) {
                repository.deletePhoto(photoUrl)
            }
            emit(AddPatientResult.Error(
                createResult.exceptionOrNull()?.message ?: "Failed to create patient"
            ))
            return@flow
        }
        
        val patient = createResult.getOrNull()!!
        emit(AddPatientResult.Success(patient))
    }
    
    /**
     * Upload photo only (for immediate feedback)
     */
    suspend fun uploadPhoto(uri: Uri, context: Context): Result<String> {
        return repository.uploadPhoto(uri, context)
    }
}

/**
 * Parameters for adding a patient
 */
data class AddPatientParams(
    val fullName: String,
    val mobileNumber: String,
    val age: String,
    val gender: Gender?,
    val address: String,
    val photoUri: Uri?
)

/**
 * Result states for add patient operation
 */
sealed interface AddPatientResult {
    data object Loading : AddPatientResult
    data object UploadingPhoto : AddPatientResult
    data object CreatingPatient : AddPatientResult
    
    data class ValidationError(
        val result: PatientFormValidator.FormValidationResult
    ) : AddPatientResult
    
    data class PhotoUploadError(val message: String) : AddPatientResult
    data class Error(val message: String) : AddPatientResult
    data class Success(val patient: Patient) : AddPatientResult
}
