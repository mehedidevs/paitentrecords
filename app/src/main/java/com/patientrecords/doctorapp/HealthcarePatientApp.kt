package com.patientrecords.doctorapp

import android.app.Application
import android.content.Context
import com.patientrecords.doctorapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import android.content.res.Configuration
import java.util.Locale

/**
 * Application class for Healthcare Patient app
 * Initializes Koin dependency injection
 */
class HealthcarePatientApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applyLanguage(this, "bn")
        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@HealthcarePatientApp)
            modules(appModules)
        }
    }
}


fun applyLanguage(context: Context, lang: String): Context {
    val locale = Locale.forLanguageTag(lang) // ✅ Modern non-deprecated way
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return context.createConfigurationContext(config)
}
