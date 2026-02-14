package com.patientrecords.doctorapp.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatSupabaseDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return ""

    return try {
        val parsed = OffsetDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MM yy", Locale.getDefault())
        parsed.format(formatter)
    } catch (e: Exception) {
        dateString // fallback if parsing fails
    }
}

fun String?.toFormatedDate(): String {
    if (this.isNullOrBlank()) return ""

    return try {
        val parsed = OffsetDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.getDefault())
        parsed.format(formatter)
    } catch (e: Exception) {
        this
    }
}


@Composable
fun CreatedAtText(createdAt: String?) {
    val formattedDate = remember(createdAt) {
        formatSupabaseDate(createdAt)
    }

    Text(
        text = formattedDate,
        style = MaterialTheme.typography.bodyMedium
    )
}
