package com.patientrecords.doctorapp.medicinelist


import androidx.compose.foundation.border
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
import com.patientrecords.doctorapp.patientdetils.data.Medicine
import com.patientrecords.doctorapp.ui.components.AddMedicineFab
import com.patientrecords.doctorapp.ui.components.HomeTopBar
import com.patientrecords.doctorapp.ui.screens.SecondaryTonalButton
import com.patientrecords.doctorapp.ui.theme.PrimaryGreen

@Composable
fun MedicineListScreen(
    viewModel: MedicineListViewModel = viewModel(),
    onMedicineClick: (String) -> Unit,
    onAddMedicineClick: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadMedicines() }

    val query = remember { mutableStateOf("") }

    val filtered = viewModel.medicines

    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = {
            AddMedicineFab(
                onClick = {
                    onAddMedicineClick()
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
        ) {


            Spacer(Modifier.height(16.dp))

            if (viewModel.loading) CircularProgressIndicator()

            LazyColumn {
                items(filtered) {
                    MedicineListItem(it) {
                        onMedicineClick(it.id)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun MedicineListItem(
    medicine: Medicine,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp, color = PrimaryGreen, shape = RoundedCornerShape(12.dp)
            ),
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
                text = "${medicine.pricePerUnit}",
                fontSize = 13.sp
            )
        }
    }
}
