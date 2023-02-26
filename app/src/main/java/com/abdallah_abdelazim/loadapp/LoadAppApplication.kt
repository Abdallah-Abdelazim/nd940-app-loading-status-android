package com.abdallah_abdelazim.loadapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class LoadAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupKoinDi()
    }

    private fun setupKoinDi() {
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@LoadAppApplication)
            modules(

            )
        }
    }

}