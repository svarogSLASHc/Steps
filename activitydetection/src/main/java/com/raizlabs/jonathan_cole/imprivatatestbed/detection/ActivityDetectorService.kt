package com.raizlabs.jonathan_cole.imprivatatestbed.detection

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.ActivityDetectorManager

/**
 * This is the context that will listen for sensor events,Activity Recognition API updates and broadcast those events
 * to local listeners.
 */
class ActivityDetectorService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "ActivityDetectorService"
    }

    private val activityManager = ActivityDetectorManager(this)

    override fun onCreate() {
        Log.i(TAG, "Creating ActivityDetectorService...")
        super.onCreate()
        activityManager.register()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityManager.unregisterListeners()
    }

}