package com.patientrecords.doctorapp.addmedicine


import com.patientrecords.doctorapp.database.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest

class MedicineRepository {

    suspend fun insertMedicine(medicine: MedicineDto) {
        SupabaseProvider.client
            .postgrest["medicines"]
            .insert(medicine)
    }
}
