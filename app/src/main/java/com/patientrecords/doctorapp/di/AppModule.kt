package com.patientrecords.doctorapp.di

import com.patientrecords.doctorapp.data.reposimpl.HealthcareRepositoryImpl
import com.patientrecords.doctorapp.data.reposimpl.PatientRepositoryImpl
import com.patientrecords.doctorapp.domain.patients.PatientRepository
import com.patientrecords.doctorapp.domain.repos.HealthcareRepository
import com.patientrecords.doctorapp.ui.addvisit.viewmodels.VisitDetailsViewModel
import com.patientrecords.doctorapp.domain.usecases.AddPatientUseCase
import com.patientrecords.doctorapp.ui.addpaitents.validation.PatientFormValidator
import com.patientrecords.doctorapp.ui.addpaitents.AddPatientViewModel
import com.patientrecords.doctorapp.domain.usecases.GetPatientUseCase
import com.patientrecords.doctorapp.ui.addvisit.viewmodels.NewVisitViewModel
import com.patientrecords.doctorapp.ui.patientdetils.ui.PatientProfileViewModel
import com.patientrecords.doctorapp.ui.patientlist.PatientViewModel
import com.patientrecords.doctorapp.ui.patientsearch.SearchPatientViewModel
import com.patientrecords.doctorapp.ui.prescription.AddPrescriptionViewModel
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
    single<HealthcareRepository> { HealthcareRepositoryImpl() }
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


}

/**
 * All app modules combined
 */
val appModules = listOf(
    patientModule, viewModelModule
)
