package com.patientrecords.doctorapp.domain.patients

import android.content.Context
import android.net.Uri
import com.patientrecords.doctorapp.domain.models.CreatePatientRequest
import com.patientrecords.doctorapp.domain.models.Patient

/**
 * Repository interface for Patient operations
 */
interface PatientRepository {
    suspend fun uploadPhoto(uri: Uri, context: Context): Result<String>
    suspend fun createPatient(request: CreatePatientRequest): Result<Patient>
    suspend fun deletePhoto(photoUrl: String): Result<Unit>

    suspend fun getAllPatients(): Result<List<Patient>>
    suspend fun searchPatients(query: String): Result<List<Patient>>
}