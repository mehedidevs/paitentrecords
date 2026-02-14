package com.patientrecords.doctorapp.addmedicine

import com.patientrecords.doctorapp.medicinelist.MedicineUiModel
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MedicineDto(
    val id: String,
    val name: String,
    val potency: String,
    @SerialName("dosage_form")
    val dosageForm: String,
    @SerialName("duration_unit")
    val durationUnit: String,
    @SerialName("default_price")
    val defaultPrice: Double

)


fun Medicine.toDto(): MedicineUiModel {
    return MedicineUiModel(
        id = id,
        name = name.trim(),
        potency = "",
        dosageForm = TODO(),
        durationUnit = TODO(),
        defaultPrice = TODO(),
    )
}
