package com.patientrecords


import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.patientrecords.doctorapp.addvisit.VisitDetailsScreen
import com.patientrecords.doctorapp.addvisit.VisitDetailsViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.AddPrescriptionViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.BillSummaryViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.DoctorNotesViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.NewVisitViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.PatientProfileViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.bill.BillSummaryScreen
import com.patientrecords.doctorapp.patientdetils.ui.screens.notes.DoctorNotesScreen
import com.patientrecords.doctorapp.patientdetils.ui.screens.patient.PatientProfileScreen
import com.patientrecords.doctorapp.patientdetils.ui.screens.prescription.AddPrescriptionScreen
import com.patientrecords.doctorapp.patientdetils.ui.screens.visit.NewVisitScreen
import com.patientrecords.doctorapp.addpaitents.AddPatientScreen
import com.patientrecords.doctorapp.ui.screens.HomeScreen
import com.patientrecords.doctorapp.addmedicine.AddMedicineScreen
import com.patientrecords.doctorapp.medicinelist.MedicineListScreen
import com.patientrecords.doctorapp.ui.screens.patientlist.PatientListScreen
import com.patientrecords.doctorapp.patientsearch.SearchPatientScreen
import com.patientrecords.doctorapp.utils.isRouteSelected
import com.patientrecords.doctorapp.utils.navItems
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Serializable
sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    object PatientListScreen : Screen

    @Serializable
    object MedicineListScreen : Screen

    @Serializable
    object AddPatient : Screen

    @Serializable
    object SearchPatientScreen : Screen

    @Serializable
    data object PatientList : Screen

    @Serializable
    data object AddMedicineScreen : Screen

    @Serializable
    data class PatientProfile(val patientId: String, val patientName: String) : Screen

    @Serializable
    data class NewVisit(val patientId: String, val patientName: String) : Screen

    @Serializable
    data class AddPrescription(
        val patientId: String,
        val patientName: String,
        val visitDate: String,
        val symptoms: String,
        val diagnosis: String,
    ) : Screen

    @Serializable
    data class BillSummary(
        val patientId: String, val visitId: String
    ) : Screen

    @Serializable
    data class DoctorNotes(
        val patientId: String,
        val visitId: String? = null // Nullable automatically becomes an optional query param
    ) : Screen

    @Serializable
    data class EditPatient(val patientId: String) : Screen

    @Serializable
    data class VisitHistory(val patientId: String) : Screen

    @Serializable
    data class VisitDetail(val patientId: String, val visitId: String) : Screen
}

/**
 * Main navigation graph for the Doctor App
 */
@Composable
fun DoctorAppNavigation(
    navController: NavHostController = rememberNavController()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    Scaffold(
        bottomBar = {
            // Only show bottom bar on top-level destinations
            val showBottomBar = navItems.any { item ->
                currentDestination.isRouteSelected(item.route::class)
            }
            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val isSelected = currentDestination.isRouteSelected(item.route::class)

                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            selected = isSelected,
                            label = { Text(item.label) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination to avoid building a large stack
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when re-selecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onAddPatientClick = {
                        navController.navigate(Screen.AddPatient)
                    }, onAddMedicineClick = {
                        navController.navigate(Screen.AddMedicineScreen)
                    },

                    onPatientClick = { patient ->
                        // Navigate to patient details
                        navController.navigate(
                            Screen.PatientProfile(
                                patientId = patient.id, patientName = patient.fullName
                            )
                        )
                    }, onViewAllClick = {
                        // Navigate to all patients list
                        // TODO: Implement all patients screen
                    }, onSearchClick = {
                        navController.navigate(Screen.SearchPatientScreen)
                    })
            }
            composable<Screen.PatientListScreen> {
                PatientListScreen(


                    onPatientClick = { patient ->
                        // Navigate to patient details
                        navController.navigate(
                            Screen.PatientProfile(
                                patientId = patient.id, patientName = patient.fullName
                            )
                        )
                    }, onSearchClick = {
                        navController.navigate(Screen.SearchPatientScreen)
                    })
            }
            composable<Screen.MedicineListScreen> {
                MedicineListScreen(
                    onAddMedicineClick = {
                        navController.navigate(Screen.AddMedicineScreen)
                    },
                    onMedicineClick = {},
                )
            }

            composable<Screen.AddPatient> {
                AddPatientScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }
            composable<Screen.AddMedicineScreen> {
                AddMedicineScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    })
            }
            composable<Screen.SearchPatientScreen> {
                SearchPatientScreen(onPatientClick = {

                    navController.navigate(
                        Screen.PatientProfile(
                            patientId = it.id, patientName = it.fullName
                        )
                    )
                }, onBack = {
                    navController.popBackStack()
                })
            }

            // Patient Profile
            composable<Screen.PatientProfile> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.PatientProfile>()

                Log.d("patientId", "DoctorAppNavigation: ${args.patientId} ")
                val viewModel: PatientProfileViewModel =
                    koinViewModel { parametersOf(args.patientId) }
                val uiState by viewModel.uiState.collectAsState()

                PatientProfileScreen(
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(Screen.EditPatient(args.patientId)) },
                    onNewVisit = {
                        navController.navigate(
                            Screen.NewVisit(
                                args.patientId, args.patientName
                            )
                        )
                    },
                    onDoctorNotes = { navController.navigate(Screen.DoctorNotes(args.patientId)) },
                    onViewAllHistory = { navController.navigate(Screen.VisitHistory(args.patientId)) },
                    onVisitClick = { patientId, visitId ->
                        navController.navigate(
                            Screen.VisitDetail(
                                patientId = patientId, visitId = visitId
                            )
                        )
                    })
            }

            //  Visit Details
            composable<Screen.VisitDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.VisitDetail>()
                val viewModel: VisitDetailsViewModel =
                    koinViewModel { parametersOf(args.patientId, args.visitId) }
                val uiState by viewModel.uiState.collectAsState()

                VisitDetailsScreen(
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },

                    )
            }

            // New Visit
            composable<Screen.NewVisit> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.NewVisit>()
                val viewModel: NewVisitViewModel = koinViewModel { parametersOf(args.patientId) }
                val uiState by viewModel.uiState.collectAsState()

                NewVisitScreen(
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },
                    onDateChange = viewModel::updateDate,
                    onSymptomsChange = viewModel::updateSymptoms,
                    onDiagnosisChange = viewModel::updateDiagnosis,
                    onConsultationFeeToggle = viewModel::toggleConsultationFee,
                    onSave = {},
                    onAddMedicines = { visitDate, symptoms, diagnosis ->

                        navController.navigate(
                            Screen.AddPrescription(
                                patientId = args.patientId,
                                patientName = args.patientName,
                                visitDate = visitDate,
                                symptoms = symptoms ?: "",
                                diagnosis = diagnosis ?: ""
                            )
                        )
                    },
                )
            }

            // Add Prescription
            composable<Screen.AddPrescription> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.AddPrescription>()
                val viewModel: AddPrescriptionViewModel = koinViewModel {
                    parametersOf(
                        args.patientId,
                        args.patientName,
                        args.visitDate,
                        args.symptoms,
                        args.diagnosis
                    )
                }
                val uiState by viewModel.uiState.collectAsState()
                AddPrescriptionScreen(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },
                    onMedicineSearch = viewModel::searchMedicines,
                    onMedicineSelected = viewModel::selectMedicine,
                    onPotencyChange = viewModel::updatePotency,
                    onDosageChange = viewModel::updateDosage,
                    onFrequencyChange = viewModel::updateFrequency,
                    onDaysChange = viewModel::updateDays,
                    onPriceChange = viewModel::updatePrice,
                    onAddMedicine = viewModel::addMedicine,
                    onRemoveMedicine = viewModel::removeMedicine,
                    onEditDetails = {},
                    onReviewBill = {
                        // Pass prescriptions back to NewVisit screen
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "prescriptions", viewModel.getPrescriptions()
                        )
                        navController.popBackStack()
                    },
                    onCompleted = viewModel::markAsComplete,

                    )
            }

            // Bill Summary
            composable<Screen.BillSummary> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.BillSummary>()
                val viewModel: BillSummaryViewModel =
                    koinViewModel { parametersOf(args.patientId, args.visitId) }
                val uiState by viewModel.uiState.collectAsState()

                BillSummaryScreen(
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },
                    onDiscountChange = viewModel::updateDiscount,
                    onConsultationFeeToggle = viewModel::toggleConsultationFee,
                    onSaveVisit = {
                        viewModel.saveVisit {
                            navController.navigate(
                                Screen.PatientProfile(
                                    args.patientId,
                                    "Demo Name"
                                )
                            ) {
                                popUpTo(
                                    Screen.PatientProfile(
                                        args.patientId,
                                        "Demo Name"
                                    )
                                ) { inclusive = true }
                            }
                        }
                    },
                    onGeneratePrescription = { /* Generate PDF */ },
                    onPrintBill = { /* Print functionality */ },
                    onShareWhatsApp = { /* WhatsApp sharing */ })
            }

            // Doctor Notes
            composable<Screen.DoctorNotes> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.DoctorNotes>()
                val viewModel: DoctorNotesViewModel =
                    koinViewModel { parametersOf(args.patientId, args.visitId) }
                val uiState by viewModel.uiState.collectAsState()

                DoctorNotesScreen(
                    uiState = uiState,
                    onNavigateBack = { navController.popBackStack() },
                    onSave = { viewModel.saveNotes { navController.popBackStack() } },
                    onContentChange = {})
            }
        }
    }

}

