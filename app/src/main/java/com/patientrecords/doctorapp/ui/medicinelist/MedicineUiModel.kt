package com.patientrecords.doctorapp.ui.medicinelist

import com.patientrecords.doctorapp.domain.models.MedicineDto

data class MedicineUiModel(
    val id: String,
    val name: String,
    val potency: String,
    val dosageForm: String,
    val durationUnit: String,
    val defaultPrice: Double
)


fun MedicineDto.toUiModel(): MedicineUiModel {
    return MedicineUiModel(
        id = id,
        name = name.trim(),
        potency = potency,
        dosageForm = dosageForm,
        durationUnit = durationUnit,
        defaultPrice = defaultPrice,
    )
}
