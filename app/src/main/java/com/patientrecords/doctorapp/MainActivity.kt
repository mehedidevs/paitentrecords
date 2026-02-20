package com.patientrecords.doctorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.patientrecords.doctorapp.navs.DoctorAppNavigation
import com.patientrecords.doctorapp.ui.theme.HealthcarePatientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyLanguage(this, "bn")
        enableEdgeToEdge()
        setContent {
            HealthcarePatientTheme {
                // HomeScreen()
                // EditMedicineScreen()
                //MedicineDetailScreen()
                /* MedicineListScreen(
                     onMedicineClick = {},
                     onAddMedicineClick = {}
                 )*/
                //AddMedicineScreen()
                DoctorAppNavigation()
                // MainScreen()
            }
        }
    }
}


