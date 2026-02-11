package com.patientrecords.doctorapp.patientdetils.ui.screens.visit

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.patientdetils.data.NewVisitUiState
import com.patientrecords.doctorapp.patientdetils.ui.components.DatePickerField
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTextField
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.patientdetils.ui.components.PrimaryButton
import com.patientrecords.doctorapp.patientdetils.ui.components.SwitchRow
import com.patientrecords.doctorapp.patientdetils.ui.screens.NewVisitViewModel
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewVisitScreen(

    uiState: NewVisitUiState,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onSymptomsChange: (String) -> Unit,
    onDiagnosisChange: (String) -> Unit,
    onConsultationFeeToggle: (Boolean) -> Unit,
    onAddMedicines: (visitDate: String, symptoms: String?, diagnosis: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "New Visit", style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }, navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            text = "Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }, actions = {
                    TextButton(
                        onClick = onSave, enabled = uiState.symptoms.isNotBlank()
                    ) {
                        Text(
                            text = "Save", color = if (uiState.symptoms.isNotBlank()) PrimaryGreen
                            else MaterialTheme.colorScheme.outline, fontWeight = FontWeight.SemiBold
                        )
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Date Field
                DatePickerField(
                    selectedDate = uiState.date.format(dateFormatter),
                    onDateSelected = { dateString ->
                        try {
                            val parsed = LocalDate.parse(dateString, dateFormatter)
                            onDateChange(parsed)
                        } catch (e: Exception) {
                            // Handle parsing error
                        }
                    },
                    label = "Date"
                )

                // Symptoms Field
                HealthcareTextField(
                    value = uiState.symptoms,
                    onValueChange = onSymptomsChange,
                    label = "Symptoms",
                    placeholder = "Enter patient complaints...",
                    singleLine = false,
                    minLines = 5,
                    maxLines = 8
                )

                // Diagnosis Field
                HealthcareTextField(
                    value = uiState.diagnosis,
                    onValueChange = onDiagnosisChange,
                    label = "Diagnosis (Optional)",
                    placeholder = "Enter diagnosis..."
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Consultation Fee Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                SwitchRow(
                    title = "Consultation Fee",
                    subtitle = "Standard charge applies",
                    checked = uiState.consultationFeeApplied,
                    onCheckedChange = onConsultationFeeToggle
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Add Medicines Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                PrimaryButton(
                    text = "Add Medicines", onClick = {
                        onAddMedicines(
                            "${uiState.date}", "${uiState.symptoms}", "${uiState.diagnosis}",
                        )
                    }, icon = Icons.Filled.Add
                )
            }
        }

        // Loading Overlay
        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

// ==================== ViewModel ====================

/*
class NewVisitViewModel(
    private val patientId: String,
    private val createVisitUseCase: CreateVisitUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewVisitUiState())
    val uiState: StateFlow<NewVisitUiState> = _uiState.asStateFlow()
    
    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }
    
    fun updateSymptoms(symptoms: String) {
        _uiState.update { it.copy(symptoms = symptoms) }
    }
    
    fun updateDiagnosis(diagnosis: String) {
        _uiState.update { it.copy(diagnosis = diagnosis) }
    }
    
    fun toggleConsultationFee(applied: Boolean) {
        _uiState.update { it.copy(consultationFeeApplied = applied) }
    }
    
    fun saveVisit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = createVisitUseCase(
                patientId = patientId,
                date = _uiState.value.date,
                symptoms = _uiState.value.symptoms,
                diagnosis = _uiState.value.diagnosis.takeIf { it.isNotBlank() },
                consultationFeeApplied = _uiState.value.consultationFeeApplied,
                prescriptions = _uiState.value.prescriptions
            )
            
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }
}
*/

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun NewVisitScreenPreview() {
    HealthcarePatientTheme {
        NewVisitScreen(
            uiState = NewVisitUiState(
                date = LocalDate.of(2023, 10, 24),
                symptoms = "",
                diagnosis = "",
                consultationFeeApplied = true
            ),
            onNavigateBack = {},
            onSave = {},
            onDateChange = {},
            onSymptomsChange = {},
            onDiagnosisChange = {},
            onConsultationFeeToggle = {},
            onAddMedicines = {} as (visitDate: String, symptoms: String?, diagnosis: String?) -> Unit)
    }
}

@Preview(showBackground = true)
@Composable
private fun NewVisitScreenFilledPreview() {
    HealthcarePatientTheme {
        NewVisitScreen(
            uiState = NewVisitUiState(
                date = LocalDate.of(2023, 10, 24),
                symptoms = "Patient complains of recurring headaches, especially in the morning. Reports difficulty sleeping.",
                diagnosis = "Tension headache",
                consultationFeeApplied = true
            ),
            onNavigateBack = {},
            onSave = {},
            onDateChange = {},
            onSymptomsChange = {},
            onDiagnosisChange = {},
            onConsultationFeeToggle = {},
            onAddMedicines = {} as (visitDate: String, symptoms: String?, diagnosis: String?) -> Unit)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NewVisitScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        NewVisitScreen(
            uiState = NewVisitUiState(
                date = LocalDate.of(2023, 10, 24),
                symptoms = "Mild fever and body aches",
                diagnosis = "",
                consultationFeeApplied = false
            ),
            onNavigateBack = {},
            onSave = {},
            onDateChange = {},
            onSymptomsChange = {},
            onDiagnosisChange = {},
            onConsultationFeeToggle = {},
            onAddMedicines = {} as (visitDate: String, symptoms: String?, diagnosis: String?) -> Unit)
    }
}
