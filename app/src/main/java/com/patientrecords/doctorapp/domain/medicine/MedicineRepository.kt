package com.patientrecords.doctorapp.domain.medicine

import com.patientrecords.doctorapp.domain.models.MedicineDto
import com.patientrecords.doctorapp.di.SupabaseConfig
import com.patientrecords.doctorapp.domain.models.Medicine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class MedicineRepository {
    private val supabase: SupabaseClient = SupabaseConfig.client
    private val table =
        supabase.postgrest[SupabaseConfig.MEDICINES_TABLE]

    suspend fun getAll(): List<Medicine> =
        table.select().decodeList()

    suspend fun getById(id: String): MedicineDto =
        table.select {
            filter {
                MedicineDto::id eq id
            }
        }.decodeSingle()

    private suspend fun update(id: String, data: Medicine) {
        table.update(
            {
                set("name", data.name)
                set("default_price", data.pricePerUnit)
            }
        ) {
            filter {
                MedicineDto::id eq id
            }
        }
    }

    suspend fun updateAndFetch(
        id: String,
        data: Medicine
    ): MedicineDto {
        update(id, data)
        println("Updated medicine: get $data")

        return getById(id)
    }


    suspend fun searchByName(query: String): List<MedicineDto> =
        table.select {
            filter {
                MedicineDto::name ilike "%$query%"
            }
        }.decodeList()
}