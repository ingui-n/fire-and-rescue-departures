package com.android.fire_and_rescue_departures

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                objectBoxModule,
                repositoryModule,
                viewModelModule,
                networkModule,
                imageModule
            )
        }
    }
}
