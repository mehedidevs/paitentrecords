package com.patientrecords.doctorapp.ui.screens.addpaitents.components

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

/**
 * Supabase client singleton for database and storage operations
 */
object SupabaseConfig {

    // TODO: Replace with your Supabase credentials
    private const val SUPABASE_URL = "https://xwgbglqgqxlxuwkyomxr.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_jQSXUv-tsMXNsMdEA4QgeA_iHiUtq-g"

    // Storage bucket name for patient photos
    const val PATIENT_PHOTOS_BUCKET = "patient_records"

    // Table name
    const val PATIENTS_TABLE = "patients"

    /**
     * Supabase client instance
     */
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}

/**
 * Helper object for Supabase operations
 */
object SupabaseHelper {

    /**
     * Generate a unique file name for patient photo
     */
    fun generatePhotoFileName(patientId: String): String {
        val timestamp = System.currentTimeMillis()
        return "patient_${patientId}_$timestamp.jpg"
    }

    /**
     * Get public URL for a stored photo
     */
    fun getPhotoPublicUrl(fileName: String): String {
        return "${SupabaseConfig.client.supabaseUrl}/storage/v1/object/public/${SupabaseConfig.PATIENT_PHOTOS_BUCKET}/$fileName"
    }
}
