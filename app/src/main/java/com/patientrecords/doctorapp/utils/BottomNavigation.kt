package com.patientrecords.doctorapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.patientrecords.doctorapp.navs.Screen
import kotlin.reflect.KClass

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Screen
)

val navItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Screen.Home),
    BottomNavItem("Patients", Icons.Default.People, Screen.PatientListScreen),
    BottomNavItem("Medicine", Icons.Default.Medication, Screen.MedicineListScreen)
)

fun NavDestination?.isRouteSelected(route: KClass<*>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } == true
}