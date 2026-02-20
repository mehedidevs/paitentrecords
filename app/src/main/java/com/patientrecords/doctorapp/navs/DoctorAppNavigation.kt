package com.patientrecords.doctorapp.navs


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
import com.patientrecords.doctorapp.ui.addvisit.VisitDetailsScreen
import com.patientrecords.doctorapp.ui.addvisit.viewmodels.VisitDetailsViewModel
import com.patientrecords.doctorapp.ui.patientdetils.ui.PatientProfileScreen
import com.patientrecords.doctorapp.ui.prescription.AddPrescriptionScreen
import com.patientrecords.doctorapp.ui.addvisit.NewVisitScreen
import com.patientrecords.doctorapp.ui.addpaitents.AddPatientScreen
import com.patientrecords.doctorapp.ui.HomeScreen
import com.patientrecords.doctorapp.ui.addmedicine.AddMedicineScreen
import com.patientrecords.doctorapp.ui.addvisit.viewmodels.NewVisitViewModel
import com.patientrecords.doctorapp.ui.medicinelist.MedicineListScreen
import com.patientrecords.doctorapp.ui.patientdetils.ui.PatientProfileViewModel
import com.patientrecords.doctorapp.ui.patientlist.PatientListScreen
import com.patientrecords.doctorapp.ui.patientsearch.SearchPatientScreen
import com.patientrecords.doctorapp.ui.prescription.AddPrescriptionViewModel
import com.patientrecords.doctorapp.utils.isRouteSelected
import com.patientrecords.doctorapp.utils.navItems
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


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


        }
    }

}

