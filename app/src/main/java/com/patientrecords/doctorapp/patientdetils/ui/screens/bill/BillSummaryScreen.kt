package com.patientrecords.doctorapp.patientdetils.ui.screens.bill

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.patientrecords.doctorapp.patientdetils.data.BillSummaryUiState
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTextField
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTopAppBar
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.patientdetils.ui.components.PrimaryButton
import com.patientrecords.doctorapp.patientdetils.ui.components.SecondaryButton
import com.patientrecords.doctorapp.addpaitents.components.Gender
import com.patientrecords.doctorapp.addpaitents.components.Patient
import com.patientrecords.doctorapp.ui.theme.BackgroundDark
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen
import com.patientrecords.doctorapp.ui.theme.PrimaryGreenContainer
import com.patientrecords.doctorapp.ui.theme.PrimaryGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSummaryScreen(
    uiState: BillSummaryUiState,
    onNavigateBack: () -> Unit,
    onDiscountChange: (Double) -> Unit,
    onConsultationFeeToggle: (Boolean) -> Unit,
    onSaveVisit: () -> Unit,
    onGeneratePrescription: () -> Unit,
    onPrintBill: () -> Unit = {},
    onShareWhatsApp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDiscountDialog by remember { mutableStateOf(false) }
    var consultationFeeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            HealthcareTopAppBar(
                title = "Bill Summary",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Patient Info Card
                PatientInfoCard(
                    patient = uiState.patient
                )

                // Payment Details Card
                PaymentDetailsCard(
                    medicineTotal = uiState.medicineTotal,
                    consultationFee = uiState.consultationFee,
                    consultationFeeEnabled = consultationFeeEnabled,
                    discount = uiState.discount,
                    grandTotal = uiState.grandTotal,
                    onConsultationFeeToggle = { enabled ->
                        consultationFeeEnabled = enabled
                        onConsultationFeeToggle(enabled)
                    },
                    onDiscountClick = { showDiscountDialog = true }
                )

                // Warning Text
                Text(
                    text = "Verify all charges before saving. This action cannot be undone easily.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryActionButton(
                        icon = Icons.Outlined.Print,
                        text = "Print",
                        onClick = onPrintBill,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryActionButton(
                        icon = Icons.Filled.Whatsapp,
                        text = "WhatsApp",
                        onClick = onShareWhatsApp,
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFF25D366)
                    )
                }

                // Save Visit Button
                PrimaryButton(
                    text = "Save Visit",
                    onClick = onSaveVisit,
                    icon = Icons.Filled.Save,
                    isLoading = uiState.isLoading
                )

                // Generate Prescription Button
                SecondaryButton(
                    text = "Generate Prescription",
                    onClick = onGeneratePrescription,
                    icon = Icons.Outlined.Description
                )
            }
        }

        // Discount Dialog
        if (showDiscountDialog) {
            DiscountDialog(
                currentDiscount = uiState.discount,
                onDismiss = { showDiscountDialog = false },
                onConfirm = { discount ->
                    onDiscountChange(discount)
                    showDiscountDialog = false
                }
            )
        }

        // Loading Overlay
        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun PatientInfoCard(
    patient: Patient?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Patient Avatar
            if (patient?.photoUrl != null) {
                AsyncImage(
                    model = patient.photoUrl,
                    contentDescription = "Patient photo",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Patient",
                        modifier = Modifier.size(32.dp),
                        tint = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Patient Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient?.fullName ?: "Unknown Patient",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "ID: #${patient?.id?.takeLast(4) ?: "0000"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Verified Badge
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Verified",
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun PaymentDetailsCard(
    medicineTotal: Double,
    consultationFee: Double,
    consultationFeeEnabled: Boolean,
    discount: Double,
    grandTotal: Double,
    onConsultationFeeToggle: (Boolean) -> Unit,
    onDiscountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "PAYMENT DETAILS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Medicine Total
            PaymentRow(
                label = "Medicine Total",
                amount = medicineTotal,
                currencySymbol = "₹"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Consultation Fee with Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Consultation Fee",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = consultationFeeEnabled,
                        onCheckedChange = onConsultationFeeToggle,
                        modifier = Modifier.height(24.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                Text(
                    text = "₹${if (consultationFeeEnabled) consultationFee.toInt() else 0}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (consultationFeeEnabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Discount Row (clickable)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Discount",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDiscountClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit discount",
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryGreen
                        )
                    }
                }
                Text(
                    text = "-₹${discount.toInt()}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(16.dp))

            // Grand Total
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BackgroundDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${grandTotal.toInt()}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = PrimaryGreenDark
                        )
                        Text(
                            text = "Includes all taxes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(
    label: String,
    amount: Double,
    currencySymbol: String = "$"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$currencySymbol${amount.toInt()}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun SecondaryActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// Custom WhatsApp Icon (since it's not in Material Icons)
private val Icons.Filled.Whatsapp: ImageVector
    get() = Icons.Filled.Share // Placeholder - replace with actual WhatsApp icon

@Composable
private fun DiscountDialog(
    currentDiscount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var discountValue by remember { mutableStateOf(currentDiscount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Apply Discount",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter discount amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                HealthcareTextField(
                    value = discountValue,
                    onValueChange = { discountValue = it },
                    placeholder = "0",
                    leadingIcon = {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val discount = discountValue.toDoubleOrNull() ?: 0.0
                    onConfirm(discount)
                }
            ) {
                Text("Apply", color = PrimaryGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun BillSummaryScreenPreview() {
    HealthcarePatientTheme {
        BillSummaryScreen(
            uiState = BillSummaryUiState(
                patient = Patient(
                    id = "4021",
                    fullName = "Rahul Sharma",
                    age = 32,
                    gender = Gender.MALE,
                    mobileNumber = "+91 98765 43210"
                ),
                medicineTotal = 450.0,
                consultationFee = 500.0,
                discount = 0.0,
                grandTotal = 950.0
            ),
            onNavigateBack = {},
            onDiscountChange = {},
            onConsultationFeeToggle = {},
            onSaveVisit = {},
            onGeneratePrescription = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BillSummaryWithDiscountPreview() {
    HealthcarePatientTheme {
        BillSummaryScreen(
            uiState = BillSummaryUiState(
                patient = Patient(
                    id = "1234",
                    fullName = "Priya Patel",
                    age = 28,
                    gender = Gender.FEMALE,
                    mobileNumber = "+91 99887 76655"
                ),
                medicineTotal = 800.0,
                consultationFee = 500.0,
                discount = 100.0,
                grandTotal = 1200.0
            ),
            onNavigateBack = {},
            onDiscountChange = {},
            onConsultationFeeToggle = {},
            onSaveVisit = {},
            onGeneratePrescription = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BillSummaryScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        BillSummaryScreen(
            uiState = BillSummaryUiState(
                patient = Patient(
                    id = "5678",
                    fullName = "Amit Kumar",
                    age = 45,
                    gender = Gender.MALE,
                    mobileNumber = "+91 88776 65544"
                ),
                medicineTotal = 650.0,
                consultationFee = 500.0,
                discount = 50.0,
                grandTotal = 1100.0
            ),
            onNavigateBack = {},
            onDiscountChange = {},
            onConsultationFeeToggle = {},
            onSaveVisit = {},
            onGeneratePrescription = {}
        )
    }
}
