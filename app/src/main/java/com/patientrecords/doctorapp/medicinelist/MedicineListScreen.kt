package com.patientrecords.doctorapp.medicinelist


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MedicineListScreen(
    viewModel: MedicineListViewModel = viewModel(),
    onMedicineClick: (String) -> Unit,
    onAddMedicineClick: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadMedicines() }

    val query = remember { mutableStateOf("") }

    val filtered = viewModel.medicines.filter {
        it.name.contains(query.value, true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedicineClick) { Text("+") }
        }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {

            OutlinedTextField(
                value = query.value,
                onValueChange = { query.value = it },
                placeholder = { Text("Search medicine") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (viewModel.loading) CircularProgressIndicator()

            LazyColumn {
                items(filtered) {
                    MedicineListItem(it) {
                        onMedicineClick(it.id)
                    }
                }
            }
        }
    }
}


@Composable
fun MedicineListItem(
    medicine: MedicineUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = medicine.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "${medicine.potency} • ${medicine.dosageForm}",
                fontSize = 13.sp
            )
            Text(
                text = "₹ ${medicine.defaultPrice}",
                fontSize = 13.sp
            )
        }
    }
}
