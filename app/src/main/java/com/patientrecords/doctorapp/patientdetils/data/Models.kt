package com.patientrecords.doctorapp.patientdetils.data

import com.patientrecords.doctorapp.addpaitents.components.Patient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
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

// ==================== Bill Summary Model ====================

@Serializable
data class BillSummary(
    val patient: Patient,
    val visit: Visit,
    val prescriptions: List<Prescription>,
    @SerialName("medicine_total")
    val medicineTotal: Double,
    @SerialName("consultation_fee")
    val consultationFee: Double,
    val discount: Double,
    @SerialName("grand_total")
    val grandTotal: Double,
    @SerialName("includes_tax")
    val includesTax: Boolean = true
)

// ==================== Visit History Display Model ====================

data class VisitHistoryItem(
    val id: String,
    val date: String,
    val diagnosis: String,
    val prescribedMedicine: String? = null
)

// ==================== UI State Models ====================

data class PatientProfileUiState(
    val patient: Patient? = null,
    val totalVisits: Int = 0,
    val lastVisitDate: String? = null,
    val visitHistory: List<VisitHistoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class NewVisitUiState(
    val date: LocalDate = LocalDate.now(),
    val symptoms: String = "",
    val diagnosis: String = "",
    val consultationFeeApplied: Boolean = true,
    val consultationFee: Double = 500.0,
    val prescriptions: List<PrescriptionFormItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

data class PrescriptionFormItem(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String? = null,
    val medicineName: String? = null,
    val typedName: String = "",
    val potency: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val durationDays: Int = 7,
    val price: Double = 0.0,
    val isComplete: Boolean = false,

    // 🔥 NEW — row-level search state
    val searchState: MedicineSearchState = MedicineSearchState()
)


data class MedicineSearchState(
    val query: String = "",
    val results: List<Medicine> = emptyList(),
    val isSearching: Boolean = false,
    val selectedMedicine: Medicine? = null
)

data class DoctorNotesUiState(
    val content: String = "",
    val lastEditedAt: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

data class BillSummaryUiState(
    val patient: Patient? = null,
    val prescriptions: List<Prescription> = emptyList(),
    val medicineTotal: Double = 0.0,
    val consultationFee: Double = 500.0,
    val discount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

// ==================== Common Potency Options ====================

object PotencyOptions {
    val homeopathicPotencies = listOf(
        "6C", "12C", "30C", "200C", "1M", "10M",
        "6X", "12X", "30X",
        "Q1", "Q2", "Q3",
        "LM1", "LM2", "LM3"
    )

    val commonFrequencies = listOf(
        "Once daily",
        "Twice daily",
        "3x daily",
        "4x daily",
        "Every 4 hours",
        "Every 6 hours",
        "As needed",
        "Before meals",
        "After meals",
        "At bedtime",
        "Morning only",
        "Nightly"
    )
}
