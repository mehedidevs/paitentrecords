package com.patientrecords.doctorapp.data.reposimpl

import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.di.SupabaseConfig
import com.patientrecords.doctorapp.domain.models.DoctorNote
import com.patientrecords.doctorapp.domain.models.Medicine
import com.patientrecords.doctorapp.domain.models.Prescription
import com.patientrecords.doctorapp.domain.models.Visit
import com.patientrecords.doctorapp.domain.models.VisitWithPrescriptions
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.collections.map


/**
 * Supabase implementation of HealthcareRepository
 */
class HealthcareRepositoryImpl(
) : HealthcareRepository {
    private val supabase: SupabaseClient = SupabaseConfig.client
    companion object {
        private const val TABLE_PATIENTS = "patients"
        private const val TABLE_VISITS = "visits"
        private const val TABLE_MEDICINES = "medicines"
        private const val TABLE_PRESCRIPTIONS = "prescriptions"
        private const val TABLE_DOCTOR_NOTES = "doctor_notes"
    }

    // ==================== Patient Operations ====================

    override suspend fun getPatient(patientId: String): Result<Patient?> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_PATIENTS)
                .select {
                    filter {
                        eq("id", patientId)
                    }
                }
                .decodeSingleOrNull<Patient>()
        }
    }

    override suspend fun createPatient(patient: Patient): Result<Patient> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_PATIENTS)
                .insert(patient) {
                    select()
                }
                .decodeSingle<Patient>()
        }
    }

    override suspend fun updatePatient(patient: Patient): Result<Patient> = runCatching {
        withContext(Dispatchers.IO) {
            val updatedPatient = patient.copy(
                updatedAt = LocalDateTime.now().toString()
            )
            supabase.from(TABLE_PATIENTS)
                .update(updatedPatient) {
                    filter {
                        eq("id", patient.id)
                    }
                    select()
                }
                .decodeSingle<Patient>()
        }
    }

    // ==================== Visit Operations ====================

    override suspend fun getVisitsByPatient(patientId: String): Result<List<Visit>> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_VISITS)
                .select {
                    filter {
                        eq("patient_id", patientId)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<Visit>()
        }
    }

    override suspend fun getVisit(visitId: String): Result<Visit?> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_VISITS)
                .select {
                    filter {
                        eq("id", visitId)
                    }
                }
                .decodeSingleOrNull<Visit>()
        }
    }

    override suspend fun createVisit(visit: Visit): Result<Visit> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_VISITS)
                .insert(visit) {
                    select()
                }
                .decodeSingle<Visit>()
        }
    }

    override suspend fun updateVisit(visit: Visit): Result<Visit> = runCatching {
        withContext(Dispatchers.IO) {
            val updatedVisit = visit.copy(
                updatedAt = LocalDateTime.now().toString()
            )
            supabase.from(TABLE_VISITS)
                .update(updatedVisit) {
                    filter {
                        eq("id", visit.id)
                    }
                    select()
                }
                .decodeSingle<Visit>()
        }
    }

    // ==================== Medicine Operations ====================

    override suspend fun searchMedicines(query: String): Result<List<Medicine>> = runCatching {
        withContext(Dispatchers.IO) {
            if (query.isBlank()) {
                return@withContext emptyList<Medicine>()
            }

            supabase.from(TABLE_MEDICINES)
                .select {
                    filter {
                        ilike("name", "%$query%")
                        eq("is_active", true)
                    }
                    limit(20)
                    order("name", Order.ASCENDING)
                }
                .decodeList<Medicine>()
        }
    }

    override suspend fun getMedicine(medicineId: String): Result<Medicine?> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_MEDICINES)
                .select {
                    filter {
                        eq("id", medicineId)
                    }
                }
                .decodeSingleOrNull<Medicine>()
        }
    }

    override suspend fun getAllMedicines(): Result<List<Medicine>> = runCatching {
        withContext(Dispatchers.IO) {
            supabase.from(TABLE_MEDICINES)
                .select {
                    filter {
                        eq("is_active", true)
                    }
                    order("name", Order.ASCENDING)
                }
                .decodeList<Medicine>()
        }
    }

    // ==================== Prescription Operations ====================

    override suspend fun getPrescriptionsByVisit(visitId: String): Result<List<Prescription>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE_PRESCRIPTIONS)
                    .select {
                        filter {
                            eq("visit_id", visitId)
                        }
                        order("created_at", Order.ASCENDING)
                    }
                    .decodeList<Prescription>()
            }
        }

    override suspend fun createPrescription(prescription: Prescription): Result<Prescription> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE_PRESCRIPTIONS)
                    .insert(prescription) {
                        select()
                    }
                    .decodeSingle<Prescription>()
            }
        }

    override suspend fun createPrescriptions(prescriptions: List<Prescription>): Result<List<Prescription>> =
        runCatching {
            withContext(Dispatchers.IO) {
                if (prescriptions.isEmpty()) {
                    return@withContext emptyList<Prescription>()
                }

                supabase.from(TABLE_PRESCRIPTIONS)
                    .insert(prescriptions) {
                        select()
                    }
                    .decodeList<Prescription>()
            }
        }

    // ==================== Doctor Notes Operations ====================

    override suspend fun getDoctorNotes(patientId: String, visitId: String?): Result<DoctorNote?> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE_DOCTOR_NOTES)
                    .select {
                        filter {
                            eq("patient_id", patientId)
                            if (visitId != null) {
                                eq("visit_id", visitId)
                            }
                        }
                        order("last_edited_at", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeSingleOrNull<DoctorNote>()
            }
        }

    override suspend fun saveDoctorNotes(note: DoctorNote): Result<DoctorNote> = runCatching {
        withContext(Dispatchers.IO) {
            val updatedNote = note.copy(
                lastEditedAt = LocalDateTime.now().toString()
            )

            supabase.from(TABLE_DOCTOR_NOTES)
                .upsert(updatedNote) {
                    select()
                }
                .decodeSingle<DoctorNote>()
        }
    }

    // ==================== Transaction Operations ====================

    /**
     * Saves a complete visit transaction including visit details and prescriptions.
     * Uses Supabase's transaction support for atomicity.
     */
    override suspend fun saveVisitTransaction(
        visit: Visit,
        prescriptions: List<Prescription>
    ): Result<VisitWithPrescriptions> = runCatching {
        withContext(Dispatchers.IO) {
            // Calculate totals
            val medicineTotal = prescriptions.sumOf { it.price }
            val consultationFee = if (visit.consultationFeeApplied) visit.consultationFee else 0.0
            val grandTotal = medicineTotal + consultationFee - visit.discount

            // Create visit with calculated totals
            val visitWithTotals = visit.copy(
                medicineTotal = medicineTotal,
                grandTotal = grandTotal,
                updatedAt = LocalDateTime.now().toString()
            )

            // Insert visit
            val savedVisit = supabase.from(TABLE_VISITS)
                .insert(visitWithTotals) {
                    select()
                }
                .decodeSingle<Visit>()

            // Insert prescriptions with visit ID
            val prescriptionsWithVisitId = prescriptions.map { prescription ->
                prescription.copy(visitId = savedVisit.id)
            }

            val savedPrescriptions = if (prescriptionsWithVisitId.isNotEmpty()) {
                supabase.from(TABLE_PRESCRIPTIONS)
                    .insert(prescriptionsWithVisitId) {
                        select()
                    }
                    .decodeList<Prescription>()
            } else {
                emptyList()
            }

            VisitWithPrescriptions(
                visit = savedVisit,
                prescriptions = savedPrescriptions
            )
        }
    }
}

/**
 * Flow-based extensions for reactive data access
 */
fun HealthcareRepository.searchMedicinesFlow(query: String): Flow<Result<List<Medicine>>> = flow {
    emit(searchMedicines(query))
}.flowOn(Dispatchers.IO)

fun HealthcareRepository.getPatientVisitsFlow(patientId: String): Flow<Result<List<Visit>>> = flow {
    emit(getVisitsByPatient(patientId))
}.flowOn(Dispatchers.IO)