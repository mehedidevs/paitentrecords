package com.patientrecords.doctorapp.patientsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patientrecords.doctorapp.ui.components.PatientAvatar
import com.patientrecords.doctorapp.addpaitents.components.Patient
import com.patientrecords.doctorapp.ui.screens.PatientListItem
import io.ktor.websocket.Frame
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchPatientScreen(
    viewModel: SearchPatientViewModel = koinViewModel(),
    onPatientClick: (Patient) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.search(it)
                },
                onBack = onBack
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (state) {

                SearchPatientUiState.Idle -> {
                    EmptyHint("Search by name or mobile number")
                }

                SearchPatientUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SearchPatientUiState.Empty -> {
                    EmptyHint("No patient found")
                }

                is SearchPatientUiState.Error -> {
                    ErrorView(
                        message = (state as SearchPatientUiState.Error).message
                    )
                }

                is SearchPatientUiState.Success -> {
                    PatientList(
                        patients = (state as SearchPatientUiState.Success).patients,
                        onClick = onPatientClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search patient") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )

        }
    )
}

@Composable
private fun PatientList(
    patients: List<Patient>,
    onClick: (Patient) -> Unit
) {
    LazyColumn {
        items(patients) { patient ->
            PatientListItem(
                patient = patient,
                onClick = onClick
            )
        }
    }
}


@Composable
fun EmptyHint(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}



