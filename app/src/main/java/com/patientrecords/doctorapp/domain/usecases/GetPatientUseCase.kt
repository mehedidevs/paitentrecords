package com.patientrecords.doctorapp.domain.usecases

import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.domain.patients.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for adding a new patient
 * Handles business logic: validation, photo upload, and patient creation
 */


/**
 * Use case for fetching all patients
 */
class GetPatientUseCase(
    private val repository: PatientRepository
) {

    operator fun invoke(): Flow<GetPatientResult> = flow {

        emit(GetPatientResult.Loading)

        val result = repository.getAllPatients()

        if (result.isFailure) {
            emit(
                GetPatientResult.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load patients"
                )
            )
            return@flow
        }

        emit(
            GetPatientResult.Success(
                result.getOrNull().orEmpty()
            )
        )
    }
}


/**
 * Result states for add patient operation
 */
sealed interface GetPatientResult {

    data object Loading : GetPatientResult

    data class Success(
        val patients: List<Patient>
    ) : GetPatientResult

    data class Error(
        val message: String
    ) : GetPatientResult
}

