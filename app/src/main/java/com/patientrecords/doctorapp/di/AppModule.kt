package com.patientrecords.doctorapp.di

import com.patientrecords.doctorapp.ui.screens.addpaitents.components.PatientRepository
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.PatientRepositoryImpl
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.AddPatientUseCase
import com.patientrecords.doctorapp.ui.screens.addpaitents.validation.PatientFormValidator
import com.patientrecords.doctorapp.ui.screens.addpaitents.AddPatientViewModel
import com.patientrecords.doctorapp.ui.screens.addpaitents.components.GetPatientUseCase
import com.patientrecords.doctorapp.ui.screens.patientlist.PatientViewModel
import com.patientrecords.doctorapp.ui.screens.patientsearch.SearchPatientViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency injection module for the Add Patient feature
 */
val patientModule = module {

    // ============== Validators ==============
    single { PatientFormValidator() }

    // ============== Repositories ==============
    single<PatientRepository> { PatientRepositoryImpl() }

    // ============== Use Cases ==============
    single { AddPatientUseCase(get(), get()) }
    single { GetPatientUseCase(get()) }
    single { GetPatientUseCase(get()) }

    // ============== ViewModels ==============
    viewModel { AddPatientViewModel(get(), get()) }
    viewModel { PatientViewModel(get()) }
    viewModel { SearchPatientViewModel(get()) }
}

/**
 * All app modules combined
 */
val appModules = listOf(
    patientModule
)
