package com.patientrecords.doctorapp.ui.screens.addpaitents.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Patient entity for database operations
 */
@Serializable
data class Patient(
    @SerialName("id")
    val id: String = UUID.randomUUID().toString(),
    
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
    @SerialName("male")
    MALE,
    
    @SerialName("female")
    FEMALE;
    
    fun toDisplayString(): String = when (this) {
        MALE -> "Male"
        FEMALE -> "Female"
    }
    
    companion object {
        fun fromString(value: String): Gender? = when (value.lowercase()) {
            "male" -> MALE
            "female" -> FEMALE
            else -> null
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
