package com.patientrecords.doctorapp.addmedicine

import com.patientrecords.doctorapp.ui.screens.addmedicine.MedicineData
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


fun MedicineData.toDto(): MedicineDto {
    return MedicineDto(
        id = id,
        name = name.trim(),
        potency = potency,
        dosageForm = dosageForm,
        durationUnit = durationUnit,
        defaultPrice = defaultPrice,
    )
}
