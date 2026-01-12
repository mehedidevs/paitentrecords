package com.patientrecords.doctorapp.database


import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://xwgbglqgqxlxuwkyomxr.supabase.co",
        supabaseKey = "sb_publishable_jQSXUv-tsMXNsMdEA4QgeA_iHiUtq-g"
    ) {
        install(Postgrest)
    }
}
