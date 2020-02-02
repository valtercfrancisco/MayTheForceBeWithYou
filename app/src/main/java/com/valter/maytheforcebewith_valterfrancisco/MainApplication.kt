package com.valter.maytheforcebewith_valterfrancisco

import android.app.Application
import com.valter.maytheforcebewith_valterfrancisco.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(DataModule.module))
        }
    }
}