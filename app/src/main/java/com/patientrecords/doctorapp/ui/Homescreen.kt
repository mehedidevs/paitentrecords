package com.patientrecords.doctorapp.ui


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.doctorapp.R
import com.patientrecords.doctorapp.ui.components.HomeTopBar
import com.patientrecords.doctorapp.ui.components.PatientAvatar
import com.patientrecords.doctorapp.domain.models.Patient
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme
import com.patientrecords.doctorapp.domain.usecases.GetPatientResult
import com.patientrecords.doctorapp.ui.patientlist.PatientViewModel
import com.patientrecords.doctorapp.ui.theme.PrimaryGreenDark
import com.patientrecords.doctorapp.ui.theme.TextPrimaryDark
import com.patientrecords.doctorapp.utils.toFormatedDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: PatientViewModel = koinViewModel(),
    onAddPatientClick: () -> Unit = {},
    onAddMedicineClick: () -> Unit = {},
    onPatientClick: (Patient) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { HomeTopBar() }, containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        when (state) {

            is GetPatientResult.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is GetPatientResult.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (state as GetPatientResult.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.refreshPatients()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = "আবার চেষ্টা করুন")
                        }
                    }
                }
            }

            is GetPatientResult.Success -> {
                val patients = (state as GetPatientResult.Success).patients

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {

                    item {
                        SearchSection(
                            onClick = onSearchClick
                        )
                    }

                    item {
                        AddPatientButton(onClick = onAddPatientClick)
                    }


                    item {
                        SecondaryTonalButton(onClick = onAddMedicineClick)
                    }

                    item {
                        RecentConsultationsHeader(onViewAllClick)
                    }

                    items(patients) { patient ->
                        PatientListItem(
                            patient = patient, onClick = onPatientClick
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSection(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 16.dp)
    ) {

        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false, // 🔑 important
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { onClick() },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_patient_by_name_or_mobile),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            singleLine = true
        )
    }
}


@Composable
private fun AddPatientButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, contentColor = Color(0xFF052E11)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp, pressedElevation = 2.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = TextPrimaryDark,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.add_patient),
                color = TextPrimaryDark,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SecondaryTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = stringResource(R.string.add_medicine),
    icon: ImageVector? = Icons.Default.MedicalInformation
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp)
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                PrimaryGreenDark
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = PrimaryGreenDark.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecentConsultationsHeader(onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.recent_patients),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onViewAllClick) {
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PatientListItem(
    patient: Patient, onClick: (Patient) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(vertical = 6.dp)
            .clickable(onClick = {

                onClick(patient)

                Log.d("TAG", "PatientListItem: patient${patient.id} ")
            }), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PatientAvatar(patient = patient)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = patient.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = patient.mobileNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = if (patient.createdAt == "Today") MaterialTheme.colorScheme.surfaceVariant
                else Color.Transparent, shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = patient.createdAt.toFormatedDate(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}


@Preview(name = "Home Screen - Light", showBackground = true)
@Composable
private fun HomeScreenPreviewLight() {
    HealthcarePatientTheme(darkTheme = false) {
        HomeScreen()
    }
}

@Preview(name = "Home Screen - Dark", showBackground = true)
@Composable
private fun HomeScreenPreviewDark() {
    HealthcarePatientTheme(darkTheme = true) {
        HomeScreen()
    }
}