package com.ghostdev.storekeeperhng

import android.app.Application
import com.ghostdev.storekeeperhng.di.databaseModule
import com.ghostdev.storekeeperhng.di.repositoryModule
import com.ghostdev.storekeeperhng.di.useCaseModule
import com.ghostdev.storekeeperhng.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StoreKeeperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StoreKeeperApp)
            modules(
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}