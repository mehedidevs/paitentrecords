package com.patientrecords.doctorapp.ui.medicinelist


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.patientrecords.doctorapp.ui.addmedicine.DefaultPriceField
import com.patientrecords.doctorapp.ui.addmedicine.DosageFormDropdown
import com.patientrecords.doctorapp.ui.addmedicine.DurationUnitDropdown
import com.patientrecords.doctorapp.ui.addmedicine.MedicineNameField
import com.patientrecords.doctorapp.ui.addmedicine.PotencySection
import com.patientrecords.doctorapp.ui.addmedicine.SaveMedicineButton
import com.patientrecords.doctorapp.ui.medicinelist.viewmodels.EditMedicineUiState
import com.patientrecords.doctorapp.ui.medicinelist.viewmodels.EditMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineScreen(
    medicineId: String = "85eade15-341c-4a0a-b7ec-390586cb04c2",
    viewModel: EditMedicineViewModel = viewModel(),
    onBack: () -> Unit = {}
) {

    val uiState = viewModel.uiState
    val state = viewModel.formState

    // Fetch medicine once
    LaunchedEffect(medicineId) {
        viewModel.loadMedicine(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Medicine") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (uiState) {
                is EditMedicineUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is EditMedicineUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is EditMedicineUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        MedicineNameField(
                            value = state.medicineName,
                            onValueChange = { state.medicineName = it }
                        )

                        PotencySection(
                            selectedPotency = state.selectedPotency,
                            onPotencySelected = { state.selectedPotency = it },
                            isCustomMode = state.isCustomPotency,
                            onCustomModeToggle = {
                                state.isCustomPotency = !state.isCustomPotency
                            }
                        )

                        DosageFormDropdown(
                            selectedForm = state.dosageForm,
                            onFormSelected = { state.dosageForm = it }
                        )

                        DurationUnitDropdown(
                            selectedUnit = state.durationUnit,
                            onUnitSelected = { state.durationUnit = it }
                        )

                        DefaultPriceField(
                            value = state.defaultPrice,
                            onValueChange = { state.defaultPrice = it }
                        )

                        SaveMedicineButton(
                            enabled = state.isValid(),
                            onClick = {
                                viewModel.updateMedicine(
                                    medicineId = medicineId,
                                    onDone = onBack
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

