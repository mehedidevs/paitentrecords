package com.patientrecords.doctorapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Patient entity for database operations
 */
@Serializable
data class Patient(
    @SerialName("id")
    val id: String,

    @SerialName("full_name")
    val fullName: String,

    @SerialName("mobile_number")
    val mobileNumber: String,

    @SerialName("age")
    val age: Int,

    @SerialName("gender")
    val gender: Gender,

    @SerialName("address")
    val address: String? = null,

    @SerialName("blood_group")
    val bloodGroup: String? = null,

    @SerialName("emergency_contact")
    val emergencyContact: String? = null,

    @SerialName("photo_url")
    val photoUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * Gender enum for patient
 */

@Serializable
enum class Gender {
    @SerialName("Male")
    MALE,

    @SerialName("Female")
    FEMALE,

    @SerialName("Other")
    OTHER;

    // Helper for display
    fun displayName(): String = when (this) {
        MALE -> "Male"
        FEMALE -> "Female"
        OTHER -> "Other"
    }

    companion object {
        fun fromString(value: String): Gender = when (value.lowercase()) {
            "male" -> MALE
            "female" -> FEMALE
            else -> OTHER
        }
    }
}

/**
 * Request model for creating a patient (without auto-generated fields)
 */
@Serializable
data class CreatePatientRequest(
    @SerialName("full_name")
    val fullName: String,

    @SerialName("mobile_number")
    val mobileNumber: String,

    @SerialName("age")
    val age: Int,

    @SerialName("gender")
    val gender: Gender,

    @SerialName("address")
    val address: String? = null,

    @SerialName("photo_url")
    val photoUrl: String? = null
)

/**
 * Extension to convert Patient to CreatePatientRequest
 */
fun Patient.toCreateRequest() = CreatePatientRequest(
    fullName = fullName,
    mobileNumber = mobileNumber,
    age = age,
    gender = gender,
    address = address,
    photoUrl = photoUrl
)
