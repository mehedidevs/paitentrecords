package com.patientrecords.doctorapp.navs

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    object PatientListScreen : Screen

    @Serializable
    object MedicineListScreen : Screen

    @Serializable
    object AddPatient : Screen

    @Serializable
    object SearchPatientScreen : Screen

    @Serializable
    data object PatientList : Screen

    @Serializable
    data object AddMedicineScreen : Screen

    @Serializable
    data class PatientProfile(val patientId: String, val patientName: String) : Screen

    @Serializable
    data class NewVisit(val patientId: String, val patientName: String) : Screen

    @Serializable
    data class AddPrescription(
        val patientId: String,
        val patientName: String,
        val visitDate: String,
        val symptoms: String,
        val diagnosis: String,
    ) : Screen

    @Serializable
    data class BillSummary(
        val patientId: String, val visitId: String
    ) : Screen

    @Serializable
    data class DoctorNotes(
        val patientId: String,
        val visitId: String? = null // Nullable automatically becomes an optional query param
    ) : Screen

    @Serializable
    data class EditPatient(val patientId: String) : Screen

    @Serializable
    data class VisitHistory(val patientId: String) : Screen

    @Serializable
    data class VisitDetail(val patientId: String, val visitId: String) : Screen
}
