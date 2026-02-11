package com.patientrecords.doctorapp.patientdetils.ui.screens.patient

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.patientdetils.data.PatientProfileUiState
import com.patientrecords.doctorapp.patientdetils.data.VisitHistoryItem
import com.patientrecords.doctorapp.patientdetils.ui.components.HealthcareTopAppBar
import com.patientrecords.doctorapp.patientdetils.ui.components.InfoCard
import com.patientrecords.doctorapp.patientdetils.ui.components.LoadingOverlay
import com.patientrecords.doctorapp.patientdetils.ui.components.PrimaryButton
import com.patientrecords.doctorapp.patientdetils.ui.components.ProfileAvatar
import com.patientrecords.doctorapp.patientdetils.ui.components.SecondaryButton
import com.patientrecords.doctorapp.patientdetils.ui.components.VisitHistoryCard
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Gender
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.Patient
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    uiState: PatientProfileUiState,
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onNewVisit: () -> Unit,
    onDoctorNotes: () -> Unit,
    onViewAllHistory: () -> Unit,
    onVisitClick: (
        patientId: String, visitId: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HealthcareTopAppBar(
                title = "Patient Profile", onNavigateBack = onNavigateBack, actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Edit, contentDescription = "Edit profile"
                        )
                    }
                })
        }) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    patient = uiState.patient, modifier = Modifier.fillMaxWidth()
                )
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        title = "Total Visits",
                        value = uiState.totalVisits.toString(),
                        icon = Icons.Outlined.History,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        title = "Last Visit",
                        value = uiState.lastVisitDate ?: "N/A",
                        icon = Icons.Outlined.CalendarMonth,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Action Buttons
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "New Visit", onClick = onNewVisit, icon = Icons.Filled.Add
                    )

                /*    SecondaryButton(
                        text = "Doctor Notes",
                        onClick = onDoctorNotes,
                        icon = Icons.Outlined.Description
                    )*/
                }
            }

            // Visit History Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visit History", style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    TextButton(onClick = onViewAllHistory) {
                        Text(
                            text = "View All", color = PrimaryGreen
                        )
                    }
                }
            }

            // Visit History Items
            items(uiState.visitHistory.take(5)) { visit ->
                VisitHistoryCard(
                    date = visit.date,
                    diagnosis = visit.diagnosis,
                    prescribedMedicine = visit.prescribedMedicine,
                    onClick = { uiState.patient?.id?.let { onVisitClick(it, visit.id) } })
            }

            // Empty State
            if (uiState.visitHistory.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyVisitHistory()
                }
            }

            // Bottom Spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Loading Overlay
        LoadingOverlay(isLoading = uiState.isLoading)
    }
}

@Composable
private fun ProfileHeader(
    patient: Patient?, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        ProfileAvatar(
            imageUrl = patient?.photoUrl, size = 120.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = patient?.fullName ?: "Unknown",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Age & Gender
        Text(
            text = "${patient?.age ?: 0} Years • ${
            patient?.gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        }",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        // Phone Number Chip
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = patient?.mobileNumber ?: "No phone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun EmptyVisitHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No visit history",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Start by creating a new visit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

// ==================== Preview ====================

@Preview(showBackground = true)
@Composable
private fun PatientProfileScreenPreview() {
    HealthcarePatientTheme {
        PatientProfileScreen(
            uiState = PatientProfileUiState(
            patient = Patient(
                id = "1",
                fullName = "Jane Doe",
                age = 34,
                gender = Gender.FEMALE,
                mobileNumber = "+1 (555) 123-4567",
                photoUrl = null
            ), totalVisits = 12, lastVisitDate = "Oct 24, 2023", visitHistory = listOf(
                VisitHistoryItem(
                    id = "1",
                    date = "OCT 24, 2023",
                    diagnosis = "Migraine - Follow up",
                    prescribedMedicine = "Natrum Mur 200"
                ), VisitHistoryItem(
                    id = "2",
                    date = "SEP 12, 2023",
                    diagnosis = "Chronic Headache",
                    prescribedMedicine = "Belladonna 30"
                ), VisitHistoryItem(
                    id = "3",
                    date = "AUG 05, 2023",
                    diagnosis = "General Checkup",
                    prescribedMedicine = null
                )
            )
        ),
            onNavigateBack = {},
            onEditProfile = {},
            onNewVisit = {},
            onDoctorNotes = {},
            onViewAllHistory = {},
            onVisitClick = { _, _ -> })
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PatientProfileScreenDarkPreview() {
    HealthcarePatientTheme(darkTheme = true) {
        PatientProfileScreen(
            uiState = PatientProfileUiState(
            patient = Patient(
                id = "1",
                fullName = "Jane Doe",
                age = 34,
                gender = Gender.FEMALE,
                mobileNumber = "+1 (555) 123-4567"
            ), totalVisits = 12, lastVisitDate = "Oct 24, 2023", visitHistory = listOf(
                VisitHistoryItem(
                    id = "1",
                    date = "OCT 24, 2023",
                    diagnosis = "Migraine - Follow up",
                    prescribedMedicine = "Natrum Mur 200"
                )
            )
        ),
            onNavigateBack = {},
            onEditProfile = {},
            onNewVisit = {},
            onDoctorNotes = {},
            onViewAllHistory = {},
            onVisitClick = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun PatientProfileEmptyPreview() {
    HealthcarePatientTheme {
        PatientProfileScreen(
            uiState = PatientProfileUiState(
            patient = Patient(
                id = "1",
                fullName = "New Patient",
                age = 28,
                gender = Gender.MALE,
                mobileNumber = "+1 (555) 999-8888"
            ), totalVisits = 0, lastVisitDate = null, visitHistory = emptyList()
        ),
            onNavigateBack = {},
            onEditProfile = {},
            onNewVisit = {},
            onDoctorNotes = {},
            onViewAllHistory = {},
            onVisitClick = { _, _ -> })
    }
}
