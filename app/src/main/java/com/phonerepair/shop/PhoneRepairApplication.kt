package com.phonerepair.shop

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class PhoneRepairApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
    
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR) {
                // TODO: Send to crash reporting service when configured
            }
        }
    }
}