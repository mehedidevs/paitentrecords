// VisitDetailsScreen.kt
package com.patientrecords.doctorapp.addvisit

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.patientrecords.doctorapp.patientdetils.data.Prescription
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTextField
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTopAppBar
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Gender
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Patient
import com.patientrecords.doctorapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDetailsScreen(
    uiState: VisitDetailsUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDiscountDialog by remember { mutableStateOf(false) }
    var consultationFeeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            HealthcareTopAppBar(
                title = "Visit Details",
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

                // Prescriptions Card
                PrescriptionsCard(
                    prescriptions = uiState.prescriptions
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

                    },
                    onDiscountClick = { showDiscountDialog = true }
                )


            }

            Spacer(modifier = Modifier.weight(1f))
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
private fun PrescriptionsCard(
    prescriptions: List<Prescription>,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRESCRIPTIONS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                // Medicine count badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryGreenContainer
                ) {
                    Text(
                        text = "${prescriptions.size} items",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (prescriptions.isEmpty()) {
                // Empty State
                EmptyPrescriptionsState()
            } else {
                // Prescriptions List
                prescriptions.forEachIndexed { index, prescription ->
                    PrescriptionItem(prescription = prescription)

                    if (index < prescriptions.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPrescriptionsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No prescriptions added",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PrescriptionItem(
    prescription: Prescription,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Medicine Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Medication,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Medicine Details
        Column(modifier = Modifier.weight(1f)) {
            // Medicine Name and Potency
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prescription.medicineName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (prescription.potency?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = prescription.potency,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dosage and Frequency
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buildString {
                        if (prescription.dosage?.isNotBlank() == true) {
                            append(prescription.dosage)
                        }
                        if (prescription.frequency?.isNotBlank() == true) {
                            if (isNotEmpty()) append(" • ")
                            append(prescription.frequency)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Duration
            prescription.durationDays?.let {
                if (it > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${prescription.durationDays} days",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Instructions
            if (prescription.instructions?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        prescription.instructions?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Price
        Text(
            text = "${prescription.price.toInt()}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = PrimaryGreen
        )
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
                }
                Text(
                    text = "${if (consultationFeeEnabled) consultationFee.toInt() else 0}",
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

                }
                Text(
                    text = "-${discount.toInt()}",
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
                color = PrimaryGreenContainerDark
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
                            color = TextPrimaryDark
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
    currencySymbol: String = ""
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


// ==================== Previews ====================

private val samplePrescriptions = listOf(
    Prescription(
        id = "1",
        medicineName = "Arnica Montana",
        potency = "30C",
        dosage = "4 pills",
        frequency = "3 times daily",
        durationDays = 7,
        price = 150.0,
        instructions = "Take before meals"
    ),
    Prescription(
        id = "2",
        medicineName = "Bryonia Alba",
        potency = "200C",
        dosage = "2 pills",
        frequency = "Twice daily",
        durationDays = 5,
        price = 200.0,
        instructions = "Dissolve under tongue"
    ),
    Prescription(
        id = "3",
        medicineName = "Nux Vomica",
        potency = "6X",
        dosage = "3 pills",
        frequency = "After meals",
        durationDays = 10,
        price = 100.0,
        instructions = ""
    )
)

@Preview(showBackground = true)
@Composable
private fun VisitDetailsScreenPreview() {
    HealthcarePatientTheme {
        VisitDetailsScreen(
            uiState = VisitDetailsUiState(
                patient = Patient(
                    id = "4021",
                    fullName = "Rahul Sharma",
                    age = 32,
                    gender = Gender.MALE,
                    mobileNumber = "+91 98765 43210"
                ),
                prescriptions = samplePrescriptions,
                medicineTotal = 450.0,
                consultationFee = 500.0,
                discount = 0.0,
                grandTotal = 950.0
            ),
            onNavigateBack = {},

            )
    }
}

@Preview(showBackground = true)
@Composable
private fun VisitDetailsEmptyPrescriptionsPreview() {
    HealthcarePatientTheme {
        VisitDetailsScreen(
            uiState = VisitDetailsUiState(
                patient = Patient(
                    id = "1234",
                    fullName = "Priya Patel",
                    age = 28,
                    gender = Gender.FEMALE,
                    mobileNumber = "+91 99887 76655"
                ),
                prescriptions = emptyList(),
                medicineTotal = 0.0,
                consultationFee = 500.0,
                discount = 0.0,
                grandTotal = 500.0
            ),
            onNavigateBack = {},

            )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun VisitDetailsScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        VisitDetailsScreen(
            uiState = VisitDetailsUiState(
                patient = Patient(
                    id = "5678",
                    fullName = "Amit Kumar",
                    age = 45,
                    gender = Gender.MALE,
                    mobileNumber = "+91 88776 65544"
                ),
                prescriptions = samplePrescriptions,
                medicineTotal = 450.0,
                consultationFee = 500.0,
                discount = 50.0,
                grandTotal = 900.0
            ),
            onNavigateBack = {},

            )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrescriptionItemPreview() {
    HealthcarePatientTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            PrescriptionItem(
                prescription = Prescription(
                    id = "1",
                    medicineName = "Arnica Montana",
                    potency = "30C",
                    dosage = "4 pills",
                    frequency = "3 times daily",
                    durationDays = 7,
                    price = 150.0,
                    instructions = "Take before meals with warm water"
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}