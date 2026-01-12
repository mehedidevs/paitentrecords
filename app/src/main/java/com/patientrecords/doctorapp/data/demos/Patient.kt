package com.patientrecords.doctorapp.data.demos

import android.net.Uri

/**
 * Data class representing a patient in the system
 */
data class Patient(
    val id: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val age: Int? = null,
    val gender: Gender = Gender.MALE,
    val address: String = "",
    val photoUri: Uri? = null,
    val consultationDate: String = ""
)

/**
 * Enum representing patient gender
 */
enum class Gender {
    MALE,
    FEMALE
}

/**
 * Get initials from full name for avatar display
 */
fun Patient.getInitials(): String {
    return fullName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
}

/**
 * Sample patient data for previews and testing
 */
object SamplePatients {
    val patients = listOf(
        Patient(
            id = "1",
            fullName = "Ramesh Gupta",
            mobileNumber = "+91 98765 43210",
            age = 45,
            gender = Gender.MALE,
            consultationDate = "Today"
        ),
        Patient(
            id = "2",
            fullName = "Anita Desai",
            mobileNumber = "+91 91234 56789",
            age = 62,
            gender = Gender.FEMALE,
            consultationDate = "Yesterday"
        ),
        Patient(
            id = "3",
            fullName = "Rahul Kumar",
            mobileNumber = "+91 88888 88888",
            age = 28,
            gender = Gender.MALE,
            consultationDate = "12 Oct"
        ),
        Patient(
            id = "4",
            fullName = "Sneha Patel",
            mobileNumber = "+91 77766 55544",
            age = 35,
            gender = Gender.FEMALE,
            consultationDate = "10 Oct"
        )
    )
}