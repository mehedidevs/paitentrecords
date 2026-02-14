package com.patientrecords.doctorapp.di

import com.patientrecords.doctorapp.addvisit.VisitDetailsViewModel
import com.patientrecords.doctorapp.patientdetils.domain.HealthcareRepository
import com.patientrecords.doctorapp.patientdetils.domain.SupabaseHealthcareRepository
import com.patientrecords.doctorapp.patientdetils.ui.screens.AddPrescriptionViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.BillSummaryViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.DoctorNotesViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.NewVisitViewModel
import com.patientrecords.doctorapp.patientdetils.ui.screens.PatientProfileViewModel
import com.patientrecords.doctorapp.addpaitents.components.PatientRepository
import com.patientrecords.doctorapp.addpaitents.components.PatientRepositoryImpl
import com.patientrecords.doctorapp.addpaitents.components.AddPatientUseCase
import com.patientrecords.doctorapp.addpaitents.validation.PatientFormValidator
import com.patientrecords.doctorapp.addpaitents.AddPatientViewModel
import com.patientrecords.doctorapp.addpaitents.components.GetPatientUseCase
import com.patientrecords.doctorapp.ui.screens.patientlist.PatientViewModel
import com.patientrecords.doctorapp.patientsearch.SearchPatientViewModel
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
    single<HealthcareRepository> { SupabaseHealthcareRepository() }
    // ============== Use Cases ==============
    single { AddPatientUseCase(get(), get()) }
    single { GetPatientUseCase(get()) }
    single { GetPatientUseCase(get()) }

    // ============== ViewModels ==============
    viewModel { AddPatientViewModel(get(), get()) }
    viewModel { PatientViewModel(get()) }
    viewModel { SearchPatientViewModel(get()) }
}

val viewModelModule = module {
    // Patient Profile ViewModel
    viewModel { (patientId: String) ->
        PatientProfileViewModel(patientId, get())
    }

    // New Visit ViewModel
    viewModel { (patientId: String) ->
        NewVisitViewModel(patientId, get(), get())
    }
    viewModel { (patientId: String, visitId: String) ->
        VisitDetailsViewModel(patientId = patientId, visitId = visitId, get())
    }

    // Add Prescription ViewModel
    viewModel { (patientId: String, patientName: String, visitDate: String, symptoms: String, diagnosis: String) ->
        AddPrescriptionViewModel(
            patientId = patientId,
            patientName = patientName,
            visitDate = visitDate,
            symptoms = symptoms,
            diagnosis = diagnosis,
            get()
        )
    }

    // Doctor Notes ViewModel
    viewModel { (patientId: String, visitId: String?) ->
        DoctorNotesViewModel(patientId, visitId, get())
    }

    // Bill Summary ViewModel
    viewModel { (patientId: String, visitId: String) ->
        BillSummaryViewModel(patientId, visitId, get())
    }
}

/**
 * All app modules combined
 */
val appModules = listOf(
    patientModule, viewModelModule
)
