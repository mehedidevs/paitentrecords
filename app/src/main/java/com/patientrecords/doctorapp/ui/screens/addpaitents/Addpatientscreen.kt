package com.patientrecords.doctorapp.ui.screens.addpaitents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Gender
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormEvent
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormState
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AddressField
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AgeInputField
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.FormTextField
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.GenderSelector
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.PhotoPicker
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.PrimaryButton
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Add New Patient Screen
 *
 * Displays a form for adding new patient information:
 * - Photo picker (camera/gallery)
 * - Full name
 * - Mobile number (required)
 * - Age
 * - Gender selection
 * - Address (optional)
 *
 * @param onNavigateBack Callback when back navigation is triggered
 * @param viewModel ViewModel instance (injected via Koin)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddPatientViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is UiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Patient",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.systemBarsPadding()
    ) { paddingValues ->
        AddPatientContent(
            formState = formState,
            onEvent = viewModel::onEvent,
            onSubmit = { viewModel.submitForm(context) },
            onValidateField = viewModel::validateField,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Content of the Add Patient screen
 * Separated for easier testing and preview
 */
@Composable
private fun AddPatientContent(
    formState: PatientFormState,
    onEvent: (PatientFormEvent) -> Unit,
    onSubmit: () -> Unit,
    onValidateField: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Photo Picker
            PhotoPicker(
                selectedUri = formState.photoUri,
                onPhotoSelected = { uri ->
                    onEvent(PatientFormEvent.PhotoSelected(uri))
                },
                onPhotoRemoved = {
                    onEvent(PatientFormEvent.PhotoRemoved)
                },
                isLoading = formState.isUploadingPhoto
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Field
            FormTextField(
                value = formState.fullName,
                onValueChange = { onEvent(PatientFormEvent.FullNameChanged(it)) },
                label = "Full name",
                placeholder = "e.g. John Doe",
                error = formState.fullNameError,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                maxLength = 100
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mobile Number Field
            FormTextField(
                value = formState.mobileNumber,
                onValueChange = { onEvent(PatientFormEvent.MobileNumberChanged(it)) },
                label = "Mobile number",
                placeholder = "10 digit number",
                isRequired = true,
                error = formState.mobileNumberError,
                trailingIcon = Icons.Default.Phone,
                trailingIconTint = PrimaryGreen,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                maxLength = 10
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Age and Gender Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Age Field
                AgeInputField(
                    value = formState.age,
                    onValueChange = { onEvent(PatientFormEvent.AgeChanged(it)) },
                    error = formState.ageError
                )

                // Gender Selector
                GenderSelector(
                    selectedGender = formState.gender,
                    onGenderSelected = { onEvent(PatientFormEvent.GenderSelected(it)) },
                    error = formState.genderError,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Address Field
            AddressField(
                value = formState.address,
                onValueChange = { onEvent(PatientFormEvent.AddressChanged(it)) }
            )

            // Bottom spacer for button
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Save Button - Fixed at bottom
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            PrimaryButton(
                text = "Save Patient",
                onClick = onSubmit,
                isLoading = formState.isSubmitting,
                enabled = !formState.isSubmitting,
                trailingIcon = Icons.Default.Check
            )
        }
    }
}

// ============== PREVIEWS ==============

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddHealthcarePatientScreenPreview() {
    HealthcarePatientTheme {
        AddPatientContent(
            formState = PatientFormState(),
            onEvent = {},
            onSubmit = {},
            onValidateField = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddHealthcarePatientScreenFilledPreview() {
    HealthcarePatientTheme {
        AddPatientContent(
            formState = PatientFormState(
                fullName = "John Doe",
                mobileNumber = "1234567890",
                age = "35",
                gender = Gender.MALE,
                address = "123 Main Street, Apt 4B\nNew York, NY 10001"
            ),
            onEvent = {},
            onSubmit = {},
            onValidateField = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddHealthcarePatientScreenErrorsPreview() {
    HealthcarePatientTheme {
        AddPatientContent(
            formState = PatientFormState(
                fullName = "J",
                mobileNumber = "123",
                age = "0",
                gender = null,
                fullNameError = "Name must be at least 2 characters",
                mobileNumberError = "Mobile number must be 10 digits",
                ageError = "Age must be at least 1",
                genderError = "Please select a gender"
            ),
            onEvent = {},
            onSubmit = {},
            onValidateField = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddHealthcarePatientScreenLoadingPreview() {
    HealthcarePatientTheme {
        AddPatientContent(
            formState = PatientFormState(
                fullName = "John Doe",
                mobileNumber = "1234567890",
                age = "35",
                gender = Gender.MALE,
                isSubmitting = true
            ),
            onEvent = {},
            onSubmit = {},
            onValidateField = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF121212)
@Composable
private fun AddHealthcarePatientScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        AddPatientContent(
            formState = PatientFormState(
                fullName = "Jane Smith",
                mobileNumber = "9876543210",
                age = "28",
                gender = Gender.FEMALE
            ),
            onEvent = {},
            onSubmit = {},
            onValidateField = {}
        )
    }
}
