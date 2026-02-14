package com.patientrecords.doctorapp.addmedicine


import android.util.Log
import com.patientrecords.doctorapp.database.SupabaseProvider
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class MedicineRepository {
    private val supabase: SupabaseClient = SupabaseConfig.client
    suspend fun insertMedicine(medicine: Medicine) {
        Log.d("insertMedicine", "name: ${medicine.name}")
       try {
           supabase
               .postgrest["medicines"]
               .insert(medicine)
       }catch (e: Exception){

           Log.d("insertMedicine", "error: ${e}")
       }
    }
}
