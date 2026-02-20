package com.patientrecords.doctorapp.data.reposimpl

import android.util.Log
import com.patientrecords.doctorapp.di.SupabaseConfig
import com.patientrecords.doctorapp.domain.models.Medicine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class MedicineRepository {
    private val supabase: SupabaseClient = SupabaseConfig.client
    suspend fun insertMedicine(medicine: Medicine) {
        Log.d("insertMedicine", "name: ${medicine.name}")
        try {
            supabase
                .postgrest[SupabaseConfig.MEDICINES_TABLE]
                .insert(medicine)
        } catch (e: Exception) {

            Log.d("insertMedicine", "error: ${e}")
        }
    }
}