package com.patientrecords.doctorapp.ui.screens.addmedicine


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patientrecords.doctorapp.addmedicine.AddMedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: AddMedicineViewModel = viewModel(),
    state: AddMedicineState = remember { AddMedicineState() },
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToPatients: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {

    val uiState = viewModel.uiState

    // Handle success navigation
    LaunchedEffect(uiState) {
        if (uiState is AddMedicineUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Medicine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.ADD_MEDICINE,
                onHomeClick = onNavigateToHome,
                onPatientsClick = onNavigateToPatients,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
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

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DosageFormDropdown(
                        selectedForm = state.dosageForm,
                        onFormSelected = { state.dosageForm = it },
                        modifier = Modifier.weight(1f)
                    )
                    DurationUnitDropdown(
                        selectedUnit = state.durationUnit,
                        onUnitSelected = { state.durationUnit = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                DefaultPriceField(
                    value = state.defaultPrice,
                    onValueChange = { state.defaultPrice = it }
                )

                SaveMedicineButton(
                    enabled = state.isValid() && uiState !is AddMedicineUiState.Loading,
                    onClick = {
                        viewModel.saveMedicine(state.toMedicineData())
                    }
                )

                if (uiState is AddMedicineUiState.Error) {
                    Text(
                        text = uiState.message,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            // Loading Overlay
            if (uiState is AddMedicineUiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

/* ---------------- STATE ---------------- */

class AddMedicineState {
    var medicineName by mutableStateOf("")
    var selectedPotency by mutableStateOf("30C")
    var isCustomPotency by mutableStateOf(false)
    var dosageForm by mutableStateOf("Drops")
    var durationUnit by mutableStateOf("Days")
    var defaultPrice by mutableStateOf("")
    var id by mutableStateOf("")

    fun isValid(): Boolean = medicineName.isNotBlank()

    fun toMedicineData(): MedicineData {
        return MedicineData(
            name = medicineName.trim(),
            potency = selectedPotency,
            dosageForm = dosageForm,
            durationUnit = durationUnit,
            defaultPrice = defaultPrice.toDoubleOrNull() ?: 0.0,
            id = id
        )
    }
}

/* ---------------- MODELS ---------------- */

data class MedicineData(
    val id: String,
    val name: String,
    val potency: String,
    val dosageForm: String,
    val durationUnit: String,
    val defaultPrice: Double
)

enum class BottomNavItem {
    HOME, ADD_MEDICINE, PATIENTS, SETTINGS
}

/* ---------------- UI STATE ---------------- */

sealed interface AddMedicineUiState {
    object Idle : AddMedicineUiState
    object Loading : AddMedicineUiState
    object Success : AddMedicineUiState
    data class Error(val message: String) : AddMedicineUiState
}

/* ---------------- COMPONENTS ---------------- */

@Composable
fun SaveMedicineButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Save, null)
        Spacer(Modifier.width(8.dp))
        Text("Save Medicine", fontWeight = FontWeight.SemiBold)
    }
}

/* ---- keep your existing components below ---- */
/* MedicineNameField, PotencySection, Dropdowns,
   BottomNavigationBar, etc.
   NO CHANGES REQUIRED */


@Composable
fun MedicineNameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Medicine Name",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "e.g., Nux Vomica",
                    color = Color(0xFFAAAAAA)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "Medicine",
                    tint = Color(0xFFAAAAAA)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF00D66B)
            ),
            singleLine = true
        )
    }
}

@Composable
fun PotencySection(
    selectedPotency: String,
    onPotencySelected: (String) -> Unit,
    isCustomMode: Boolean,
    onCustomModeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Potency",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D2D2D)
            )

            Text(
                text = "Custom",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00D66B),
                modifier = Modifier.clickable { onCustomModeToggle() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val potencies = listOf("30C", "200C", "1M", "10M", "Q")
            potencies.forEach { potency ->
                PotencyChip(
                    text = potency,
                    isSelected = selectedPotency == potency,
                    onClick = { onPotencySelected(potency) }
                )
            }
        }
    }
}

@Composable
fun PotencyChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color(0xFF00D66B) else Color.White,
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF2D2D2D)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosageFormDropdown(
    selectedForm: String,
    onFormSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dosageForms = listOf("Drops", "Tablets", "Capsules", "Syrup", "Injection", "Cream")

    Column(modifier = modifier) {
        Text(
            text = "Dosage Form",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedForm,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF00D66B)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                dosageForms.forEach { form ->
                    DropdownMenuItem(
                        text = { Text(form) },
                        onClick = {
                            onFormSelected(form)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationUnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val durationUnits = listOf("Days", "Weeks", "Months", "Years")

    Column(modifier = modifier) {
        Text(
            text = "Duration Unit",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedUnit,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF00D66B)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                durationUnits.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultPriceField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Default Price",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Only allow numbers and decimal point
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "$ 0.00",
                    color = Color(0xFFAAAAAA)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF00D66B)
            ),
            singleLine = true
        )

        Text(
            text = "Set the default billing price for this medicine.",
            fontSize = 12.sp,
            color = Color(0xFF888888),
            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
        )
    }
}

@Composable
fun SaveMedicineButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00D66B),
            disabledContainerColor = Color(0xFF80EB9D)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = "Save",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Save Medicine",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: BottomNavItem,
    onHomeClick: () -> Unit,
    onPatientsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavButton(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = selectedItem == BottomNavItem.HOME,
                onClick = onHomeClick
            )

            // Add Medicine FAB
            FloatingActionButton(
                onClick = { /* Already on this screen */ },
                containerColor = Color.Black,
                contentColor = Color(0xFF00D66B),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Medicine",
                    modifier = Modifier.size(28.dp)
                )
            }

            BottomNavButton(
                icon = Icons.Default.People,
                label = "Patients",
                isSelected = selectedItem == BottomNavItem.PATIENTS,
                onClick = onPatientsClick
            )

            BottomNavButton(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = selectedItem == BottomNavItem.SETTINGS,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun BottomNavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF00D66B) else Color(0xFFAAAAAA),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF00D66B) else Color(0xFFAAAAAA)
        )
    }
}


// Preview Functions
@Preview(name = "Add Medicine Screen - Light", showBackground = true)
@Composable
fun PreviewAddMedicineScreen() {
    MaterialTheme {
        AddMedicineScreen()
    }
}

@Preview(name = "Add Medicine Screen - With Data", showBackground = true)
@Composable
fun PreviewAddMedicineScreenWithData() {
    MaterialTheme {
        val state = AddMedicineState().apply {
            medicineName = "Nux Vomica"
            selectedPotency = "30C"
            dosageForm = "Drops"
            durationUnit = "Days"
            defaultPrice = "25.50"
        }
        AddMedicineScreen(state = state)
    }
}

@Preview(name = "Potency Section", showBackground = true)
@Composable
fun PreviewPotencySection() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            PotencySection(
                selectedPotency = "200C",
                onPotencySelected = {},
                isCustomMode = false,
                onCustomModeToggle = {}
            )
        }
    }
}

@Preview(name = "Medicine Name Field", showBackground = true)
@Composable
fun PreviewMedicineNameField() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MedicineNameField(
                value = "Nux Vomica",
                onValueChange = {}
            )
        }
    }
}

@Preview(name = "Bottom Navigation", showBackground = true)
@Composable
fun PreviewBottomNavigation() {
    MaterialTheme {
        BottomNavigationBar(
            selectedItem = BottomNavItem.ADD_MEDICINE,
            onHomeClick = {},
            onPatientsClick = {},
            onSettingsClick = {}
        )
    }
}