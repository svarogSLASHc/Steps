package com.raizlabs.jonathan_cole.imprivatatestbed.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * This is the service that will listen for sensor events,Activity Recognition API updates and broadcast those events
 * to local listeners.
 */
class ActivityDetectorService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "ActivityDetectorService"
    }

    lateinit var activityManager: ActivityDetectorManager

    override fun onCreate() {
        Log.i(TAG, "Creating ActivityDetectorService...")
        super.onCreate()
        activityManager = ActivityDetectorManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityManager.unregisterListeners()
    }

}