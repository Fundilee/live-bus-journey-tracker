package com.livebusjourneytracker

import android.app.Application
import com.livebusjourneytracker.core.network.networkModule
import com.livebusjourneytracker.di.appModule
import com.livebusjourneytracker.feature.busroutes.di.busRoutesModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BusTrackerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@BusTrackerApplication)
            modules(
                appModule,
                networkModule,
                busRoutesModule
            )
        }
    }
}