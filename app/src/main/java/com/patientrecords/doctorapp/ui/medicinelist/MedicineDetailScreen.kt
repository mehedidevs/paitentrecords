package com.patientrecords.doctorapp.ui.medicinelist


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patientrecords.doctorapp.ui.medicinelist.viewmodels.MedicineDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: String="6056849e-aa23-4a78-84b9-e01be36282de",
    viewModel: MedicineDetailViewModel = viewModel(),
    onEdit: (MedicineUiModel) -> Unit={},
    onBack: () -> Unit={}
) {
    LaunchedEffect(medicineId) { viewModel.load(medicineId) }

    val medicine = viewModel.medicine ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Details") },
                navigationIcon = { IconButton(onClick = onBack) { Text("<") } },
                actions = {
                    IconButton(onClick = { onEdit(medicine) }) {
                        Icon(Icons.Default.Edit, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            DetailItem("Name", medicine.name)
            DetailItem("Potency", medicine.potency)
            DetailItem("Form", medicine.dosageForm)
            DetailItem("Duration", medicine.durationUnit)
            DetailItem("Price", "₹ ${medicine.defaultPrice}")
        }
    }
}


@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 13.sp)
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
