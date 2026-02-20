package com.patientrecords.doctorapp.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID


// ==================== Visit Models ====================

@Serializable
data class Visit(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("patient_id")
    val patientId: String,
    val date: String,
    val symptoms: String,
    val diagnosis: String? = null,
    @SerialName("consultation_fee_applied")
    val consultationFeeApplied: Boolean = true,
    @SerialName("consultation_fee")
    val consultationFee: Double = 500.0,
    @SerialName("medicine_total")
    val medicineTotal: Double = 0.0,
    val discount: Double = 0.0,
    @SerialName("grand_total")
    val grandTotal: Double = 0.0,
    @SerialName("payment_status")
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    @SerialName("doctor_notes")
    val doctorNotes: String? = null,
    @SerialName("created_at")
    val createdAt: String = LocalDateTime.now().toString(),
    @SerialName("updated_at")
    val updatedAt: String = LocalDateTime.now().toString()
)

@Serializable
enum class PaymentStatus {
    @SerialName("pending")
    PENDING,

    @SerialName("paid")
    PAID,

    @SerialName("partial")
    PARTIAL
}

@Serializable
data class VisitWithPrescriptions(
    val visit: Visit,
    val prescriptions: List<Prescription>
)

// ==================== Medicine Models ====================

@Serializable
data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String? = null,
    @SerialName("price_per_unit")
    val pricePerUnit: Double = 0.0,
    val description: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true
)


@Serializable
data class Prescription(
    val id: String = "",
    @SerialName("visit_id")
    val visitId: String? = "",
    @SerialName("medicine_id")
    val medicineId: String = "",
    @SerialName("medicine_name")
    val medicineName: String = "",
    val potency: String? = "",
    val dosage: String? = "",
    val frequency: String? = "",
    @SerialName("duration_days")
    val durationDays: Int? = 0,
    val price: Double = 0.0,
    val instructions: String? = "",
    @SerialName("created_at")
    val createdAt: String = ""
)

// ==================== Doctor Notes Model ====================

@Serializable
data class DoctorNote(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("patient_id")
    val patientId: String,
    @SerialName("visit_id")
    val visitId: String? = null,
    val content: String,
    @SerialName("last_edited_at")
    val lastEditedAt: String = LocalDateTime.now().toString(),
    @SerialName("created_at")
    val createdAt: String = LocalDateTime.now().toString()
)

