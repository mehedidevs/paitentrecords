package com.patientrecords.doctorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patientrecords.DoctorAppNavigation
import com.patientrecords.doctorapp.medicinelist.EditMedicineScreen
import com.patientrecords.doctorapp.medicinelist.MedicineDetailScreen
import com.patientrecords.doctorapp.medicinelist.MedicineListScreen
import com.patientrecords.doctorapp.ui.screens.SplashScreen
import com.patientrecords.doctorapp.ui.screens.LockScreen
import com.patientrecords.doctorapp.ui.screens.addmedicine.AddMedicineScreen
import com.patientrecords.doctorapp.ui.theme.PatientRecordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PatientRecordTheme {

                EditMedicineScreen()
                //MedicineDetailScreen()
               /* MedicineListScreen(
                    onMedicineClick = {},
                    onAddMedicineClick = {}
                )*/
                //AddMedicineScreen()
                //DoctorAppNavigation()
                // MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.LOCK) }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        when (currentScreen) {
            Screen.SPLASH -> SplashScreen(
                modifier = Modifier.padding(paddingValues)
            )

            Screen.LOCK -> LockScreen(
                onPinComplete = { pin ->
                    println("PIN entered: $pin")
                },
                onFingerprintClick = {
                    println("Fingerprint clicked")
                },
                onForgotPinClick = {
                    println("Forgot PIN clicked")
                },
                modifier = Modifier.padding(paddingValues)
            )

            Screen.DEMO -> DemoScreenSelector(
                onScreenSelected = { screen ->
                    currentScreen = screen
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun DemoScreenSelector(
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Patient Record App Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onScreenSelected(Screen.SPLASH) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Splash Screen")
            }

            Button(
                onClick = { onScreenSelected(Screen.LOCK) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Lock Screen")
            }
        }
    }
}

enum class Screen {
    SPLASH,
    LOCK,
    DEMO
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PatientRecordTheme {
        MainScreen()
    }
}
