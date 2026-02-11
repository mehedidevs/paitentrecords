package com.patientrecords.doctorapp.patientdetils.ui.screens.prescription

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import com.patientrecords.doctorapp.patientdetils.data.MedicineSearchState
import com.patientrecords.doctorapp.patientdetils.data.PotencyOptions
import com.patientrecords.doctorapp.patientdetils.data.PrescriptionFormItem
import com.patientrecords.doctorapp.patientdetils.ui.components.DropdownField
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTextField
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTopAppBar
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.patientdetils.ui.components.PrimaryButton
import com.patientrecords.doctorapp.patientdetils.ui.screens.AddPrescriptionViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.NewVisitViewModel
import com.patientrecords.doctorapp.ui.theme.DeleteColor
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.InputBackgroundLight
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.SuccessColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AddPrescriptionUiState(
    val patientId: String = "",
    val patientName: String = "",
    val visitDate: String = "",
    val symptoms: String = "",
    val diagnosis: String = "",
    val prescriptions: List<PrescriptionFormItem> = listOf(PrescriptionFormItem()),
    val medicineSearchState: MedicineSearchState = MedicineSearchState(),
    val currentEditingIndex: Int? = null,
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrescriptionScreen(
    uiState: AddPrescriptionUiState,
    onNavigateBack: () -> Unit,
    onMedicineSearch: (Int, String) -> Unit,
    onMedicineSelected: (Int, Medicine) -> Unit,
    onPotencyChange: (Int, String) -> Unit,
    onDosageChange: (Int, String) -> Unit,
    onFrequencyChange: (Int, String) -> Unit,
    onDaysChange: (Int, Int) -> Unit,
    onPriceChange: (Int, Double) -> Unit,
    onAddMedicine: () -> Unit,
    onRemoveMedicine: (Int) -> Unit,
    onEditDetails: (Int) -> Unit,
    onReviewBill: () -> Unit,
    viewModel: AddPrescriptionViewModel? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HealthcareTopAppBar(
                title = "Add Prescription",
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            PrescriptionBottomBar(
                totalItems = uiState.prescriptions.count { it.isComplete },
                totalPrice = uiState.totalPrice,
                onUploadPrescription = {
                    viewModel?.uploadPrescription(
                        onSuccess = { msg ->
                            Log.i("uploadPrescription", "msg:$msg ")

                        },
                        onError = { errMsg ->

                            Log.i("uploadPrescription", "errMsg:$errMsg ")
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- HEADER ----------
            item {
                Column {
                    Text(
                        text = "Prescription for ${uiState.patientName}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Visit Date: ${uiState.visitDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ---------- MEDICINE CARDS ----------
            itemsIndexed(
                items = uiState.prescriptions,
                key = { _, item -> item.id }
            ) { index, prescription ->

                if (prescription.isComplete) {
                    CompletedMedicineCard(
                        index = index + 1,
                        prescription = prescription,
                        onEdit = { onEditDetails(index) },
                        onRemove = { onRemoveMedicine(index) }
                    )
                } else {

                    MedicineFormCard(
                        index = index + 1,
                        prescription = prescription,
                        onMedicineSearch = { query ->
                            onMedicineSearch(index, query)
                        },
                        onMedicineSelected = { medicine ->
                            onMedicineSelected(index, medicine)
                        },
                        onPotencyChange = { potency ->
                            onPotencyChange(index, potency)
                        },
                        onDosageChange = { dosage ->
                            onDosageChange(index, dosage)
                        },
                        onFrequencyChange = { frequency ->
                            onFrequencyChange(index, frequency)
                        },
                        onDaysChange = { days ->
                            onDaysChange(index, days)
                        },
                        onPriceChange = { price ->
                            onPriceChange(index, price)
                        },
                        onRemove = { onRemoveMedicine(index) }
                    )
                }
            }

            // ---------- ADD BUTTON ----------
            item {
                AddMedicineButton(onClick = onAddMedicine)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        LoadingOverlay(isLoading = uiState.isLoading)
    }
}


@Composable
private fun MedicineFormCard(
    index: Int,
    prescription: PrescriptionFormItem,
    onMedicineSearch: (String) -> Unit,
    onMedicineSelected: (Medicine) -> Unit,
    onPotencyChange: (String) -> Unit,
    onDosageChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onDaysChange: (Int) -> Unit,
    onPriceChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- HEADER ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medicine #$index",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove",
                        tint = DeleteColor
                    )
                }
            }

            // ---------- SEARCH FIELD ----------
            Box {

                HealthcareTextField(
                    value = prescription.searchState.query,   // SINGLE SOURCE OF TRUTH

                    onValueChange = { query ->
                        showDropdown = query.isNotEmpty()
                        onMedicineSearch(query)   // let ViewModel debounce
                    },

                    label = "Medicine Name",
                    placeholder = "e.g. Arnica Mont",

                    trailingIcon = {
                        if (prescription.searchState.isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryGreen
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded =
                        showDropdown &&
                                prescription.searchState.results.isNotEmpty(),

                    onDismissRequest = { showDropdown = false },
                    properties = PopupProperties(focusable = false),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 200.dp)
                ) {

                    prescription.searchState.results.forEach { medicine ->
                        DropdownMenuItem(
                            text = { Text(medicine.name) },
                            onClick = {
                                onMedicineSelected(medicine)
                                showDropdown = false
                            }
                        )
                    }
                }
            }


            // ---------- POTENCY ----------
            DropdownField(
                value = prescription.potency,
                options = PotencyOptions.homeopathicPotencies,
                onOptionSelected = onPotencyChange,
                label = "Potency",
                placeholder = "Select potency..."
            )

            // ---------- DOSAGE + FREQUENCY ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HealthcareTextField(
                    value = prescription.dosage,
                    onValueChange = onDosageChange,
                    label = "Dosage",
                    placeholder = "e.g. 4 pills",
                    modifier = Modifier.weight(1f)
                )

                HealthcareTextField(
                    value = prescription.frequency,
                    onValueChange = onFrequencyChange,
                    label = "Frequency",
                    placeholder = "e.g. 3x daily",
                    modifier = Modifier.weight(1f)
                )
            }

            // ---------- DAYS + PRICE ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Days",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = InputBackgroundLight,
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = prescription.durationDays.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "days",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HealthcareTextField(
                    value =
                        if (prescription.price > 0)
                            "$ ${prescription.price}"
                        else
                            "$ 0.00",

                    onValueChange = { value ->
                        val price =
                            value.replace("$", "")
                                .trim()
                                .toDoubleOrNull() ?: 0.0

                        onPriceChange(price)
                    },

                    label = "Price",
                    modifier = Modifier.weight(1f),
                    keyboardOptions =
                        KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
    }
}


@Composable
private fun CompletedMedicineCard(
    index: Int,
    prescription: PrescriptionFormItem,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SuccessColor,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize() // smooth expand/collapse
        ) {

            /** -------- COLLAPSIBLE HEADER (ALWAYS VISIBLE) -------- **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }, // toggle expand
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand"
                    )

                    Text(
                        text = "Medicine #$index",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(8.dp))

                    // Show medicine name in collapsed header
                    Text(
                        text = prescription.medicineName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove",
                            tint = DeleteColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }


            }

            /** -------- EXPANDED CONTENT -------- **/
            if (expanded) {

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MEDICINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = prescription.medicineName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(label = "POTENCY", value = prescription.potency)
                    DetailColumn(label = "DOSAGE", value = prescription.dosage)
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailColumn(
                        label = "DURATION",
                        value = "${prescription.durationDays} Days"
                    )
                    DetailColumn(
                        label = "FREQUENCY",
                        value = prescription.frequency
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = onEdit,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryGreen
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Edit Details",
                        color = PrimaryGreen,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


@Composable
private fun DetailColumn(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun AddMedicineButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp,
            brush = SolidColor(PrimaryGreen.copy(alpha = 0.5f))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = null,
                tint = PrimaryGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Another Medicine",
                style = MaterialTheme.typography.labelLarge,
                color = PrimaryGreen
            )
        }
    }
}

@Composable
private fun PrescriptionBottomBar(
    totalItems: Int,
    totalPrice: Double,
    onUploadPrescription: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Items: $totalItems",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: $${"%.2f".format(totalPrice)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Upload Prescription",
                onClick = onUploadPrescription,
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )

        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun AddPrescriptionScreenPreview() {
    HealthcarePatientTheme {
        AddPrescriptionScreen(
            uiState = AddPrescriptionUiState(
                patientName = "John Doe",
                visitDate = "Today, Oct 24",
                prescriptions = listOf(
                    PrescriptionFormItem(
                        medicineName = "",
                        potency = "",
                        dosage = "",
                        frequency = "",
                        durationDays = 7,
                        isComplete = false
                    ),
                    PrescriptionFormItem(
                        medicineName = "Nux Vomica",
                        potency = "200C",
                        dosage = "2 pills",
                        frequency = "Nightly",
                        durationDays = 3,
                        price = 15.0,
                        isComplete = true
                    )
                ),
                totalItems = 2,
                totalPrice = 15.0
            ),
            onNavigateBack = {},
            onMedicineSearch = { _, _ -> },
            onMedicineSelected = { _, _ -> },
            onPotencyChange = { _, _ -> },
            onDosageChange = { _, _ -> },
            onFrequencyChange = { _, _ -> },
            onDaysChange = { _, _ -> },
            onPriceChange = { _, _ -> },
            onAddMedicine = {},
            onRemoveMedicine = {},
            onEditDetails = {},
            onReviewBill = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddPrescriptionScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        AddPrescriptionScreen(
            uiState = AddPrescriptionUiState(
                patientName = "Jane Smith",
                visitDate = "Today, Oct 24",
                prescriptions = listOf(
                    PrescriptionFormItem(
                        medicineName = "Arnica Montana",
                        potency = "30C",
                        dosage = "3 pills",
                        frequency = "3x daily",
                        durationDays = 5,
                        price = 12.0,
                        isComplete = true
                    )
                ),
                totalItems = 1,
                totalPrice = 12.0
            ),
            onNavigateBack = {},
            onMedicineSearch = { _, _ -> },
            onMedicineSelected = { _, _ -> },
            onPotencyChange = { _, _ -> },
            onDosageChange = { _, _ -> },
            onFrequencyChange = { _, _ -> },
            onDaysChange = { _, _ -> },
            onPriceChange = { _, _ -> },
            onAddMedicine = {},
            onRemoveMedicine = {},
            onEditDetails = {},
            onReviewBill = {}
        )
    }
}
