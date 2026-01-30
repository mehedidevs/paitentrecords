package com.patientrecords.doctorapp

import android.app.Application
import com.patientrecords.doctorapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for Healthcare Patient app
 * Initializes Koin dependency injection
 */
class HealthcarePatientApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@HealthcarePatientApp)
            modules(appModules)
        }
    }
}
