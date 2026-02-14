package com.patientrecords.doctorapp.addpaitents.components

import android.content.Context
import android.net.Uri
import com.patientrecords.doctorapp.addmedicine.MedicineDto
import com.patientrecords.doctorapp.addmedicine.MedicineData
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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

/**
 * Implementation of PatientRepository using Supabase
 */
class PatientRepositoryImpl : PatientRepository {

    private val supabase = SupabaseConfig.client
    private val storage = supabase.storage
    private val postgrest = supabase.postgrest

    private val patientTable = supabase.postgrest[SupabaseConfig.PATIENTS_TABLE]

    /**
     * Upload patient photo to Supabase Storage
     *
     * @param uri Local URI of the image (from camera or gallery)
     * @param context Android context for content resolver
     * @return Result containing the public URL of uploaded photo
     */

    override suspend fun uploadPhoto(
        uri: Uri,
        context: Context
    ): Result<String> = withContext(Dispatchers.IO) {

        runCatching {

            val fileName =
                SupabaseHelper.generatePhotoFileName(UUID.randomUUID().toString())

            val bytes = context.contentResolver
                .openInputStream(uri)
                ?.readBytes()
                ?: throw Exception("Unable to read image")

            val bucket = storage.from(SupabaseConfig.PATIENT_PHOTOS_BUCKET)

            bucket.upload(path = fileName, data = bytes) {
                upsert = false
            }


            // ✅ THIS is the correct URL
            bucket.publicUrl(fileName)
        }
    }


    /**
     * Create a new patient in the database
     *
     * @param request Patient data to insert
     * @return Result containing the created Patient
     */
    override suspend fun createPatient(request: CreatePatientRequest): Result<Patient> {
        return withContext(Dispatchers.IO) {
            try {
                val response = postgrest
                    .from(SupabaseConfig.PATIENTS_TABLE)
                    .insert(request) {
                        select()
                    }
                    .decodeSingle<Patient>()

                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Delete photo from Supabase Storage
     *
     * @param photoUrl Public URL of the photo to delete
     */
    override suspend fun deletePhoto(photoUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Extract file name from URL
                val fileName = photoUrl.substringAfterLast("/")

                val bucket = storage.from(SupabaseConfig.PATIENT_PHOTOS_BUCKET)
                bucket.delete(fileName)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    override suspend fun getAllPatients(): Result<List<Patient>> =
        runCatching {
            val result = patientTable
                .select {
                    order("created_at", Order.DESCENDING)
                }

            println("Raw response: ${result.data}")
            result.decodeList<Patient>()
        }


    override suspend fun searchPatients(
        query: String
    ): Result<List<Patient>> =
        runCatching {
            val result = patientTable
                .select {
                    filter {
                        or {
                            Patient::fullName ilike "%$query%"
                            Patient::mobileNumber ilike "%$query%"
                        }
                    }
                }

            println("Raw response: Search ${result.data}") // Log raw JSON

            result.decodeList<Patient>()
        }

    suspend fun getById(id: String): MedicineDto =
        patientTable.select {
            filter {
                MedicineDto::id eq id
            }
        }.decodeSingle()

    private suspend fun update(id: String, data: MedicineData) {
        patientTable.update(
            {
                set("name", data.name)
                set("potency", data.potency)
                set("dosage_form", data.dosageForm)
                set("duration_unit", data.durationUnit)
                set("default_price", data.defaultPrice)
            }
        ) {
            filter {
                MedicineDto::id eq id
            }
        }
    }


    suspend fun updateAndFetch(
        id: String,
        data: MedicineData
    ): MedicineDto {
        update(id, data)
        println("Updated medicine: get $data")

        return getById(id)
    }


    suspend fun searchByName(query: String): List<MedicineDto> =
        patientTable.select {
            filter {
                MedicineDto::name ilike "%$query%"
            }
        }.decodeList()
}

/**
 * Helper class for image file operations
 */
object ImageFileHelper {

    /**
     * Create a temporary file for camera capture
     */
    fun createTempImageFile(context: Context): File {
        val fileName = "patient_photo_${System.currentTimeMillis()}"
        return File.createTempFile(fileName, ".jpg", context.cacheDir)
    }

    /**
     * Copy URI content to a temporary file
     */
    suspend fun copyUriToFile(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val tempFile = createTempImageFile(context)

                FileOutputStream(tempFile).use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()

                tempFile
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Get image bytes from URI
     */
    suspend fun getImageBytes(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val bytes = inputStream.readBytes()
                inputStream.close()
                bytes
            } catch (e: Exception) {
                null
            }
        }
    }
}
