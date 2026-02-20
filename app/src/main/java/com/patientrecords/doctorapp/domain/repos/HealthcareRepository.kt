package com.patientrecords.doctorapp.domain.repos

import com.patientrecords.doctorapp.domain.models.DoctorNote
import com.patientrecords.doctorapp.domain.models.Medicine
import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.domain.models.Prescription
import com.patientrecords.doctorapp.domain.models.Visit
import com.patientrecords.doctorapp.domain.models.VisitWithPrescriptions

/**
 * Repository interface for healthcare data operations
 */
interface HealthcareRepository {
    // Patient operations
    suspend fun getPatient(patientId: String): Result<Patient?>
    suspend fun createPatient(patient: Patient): Result<Patient>
    suspend fun updatePatient(patient: Patient): Result<Patient>

    // Visit operations
    suspend fun getVisitsByPatient(patientId: String): Result<List<Visit>>
    suspend fun getVisit(visitId: String): Result<Visit?>
    suspend fun createVisit(visit: Visit): Result<Visit>
    suspend fun updateVisit(visit: Visit): Result<Visit>

    // Medicine operations
    suspend fun searchMedicines(query: String): Result<List<Medicine>>
    suspend fun getMedicine(medicineId: String): Result<Medicine?>
    suspend fun getAllMedicines(): Result<List<Medicine>>

    // Prescription operations
    suspend fun getPrescriptionsByVisit(visitId: String): Result<List<Prescription>>
    suspend fun createPrescription(prescription: Prescription): Result<Prescription>
    suspend fun createPrescriptions(prescriptions: List<Prescription>): Result<List<Prescription>>

    // Doctor Notes operations
    suspend fun getDoctorNotes(patientId: String, visitId: String?): Result<DoctorNote?>
    suspend fun saveDoctorNotes(note: DoctorNote): Result<DoctorNote>

    // Transaction operations
    suspend fun saveVisitTransaction(
        visit: Visit,
        prescriptions: List<Prescription>
    ): Result<VisitWithPrescriptions>
}
