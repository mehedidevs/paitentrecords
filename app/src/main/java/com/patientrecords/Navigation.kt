package com.patientrecords


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.patientrecords.doctorapp.ui.screens.addpaitents.AddPatientScreen
import com.patientrecords.doctorapp.ui.screens.HomeScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddPatient : Screen("add_patient")
}

/**
 * Main navigation graph for the Doctor App
 */
@Composable
fun DoctorAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddPatientClick = {
                    navController.navigate(Screen.AddPatient.route)
                },
                onPatientClick = { patient ->
                    // Navigate to patient details
                    // TODO: Implement patient details screen
                },
                onViewAllClick = {
                    // Navigate to all patients list
                    // TODO: Implement all patients screen
                }
            )
        }

        composable(Screen.AddPatient.route) {
            AddPatientScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}