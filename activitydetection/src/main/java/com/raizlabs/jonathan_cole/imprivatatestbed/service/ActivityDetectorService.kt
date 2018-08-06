package com.raizlabs.jonathan_cole.imprivatatestbed.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult

/**
 * This is the service that will listen for sensor events and broadcast those events
 * to local listeners. To get Activity Recognition API updates, you'll need to set that
 * up in your activity as we have in MainActivity.
 */
class ActivityDetectorService: Service() {

    private var mBroadCastReceiver = ActivityRecognitionBroadcastReceiver()

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

        // Receive updates from any ActivityRecognitionIntent PendingIntents.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBroadCastReceiver, IntentFilter("ActivityRecognitionUpdate"))

        activityManager = ActivityDetectorManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityManager.unregisterListeners()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReceiver)
    }

    fun dispatchNewDataHistory(history: ActivityDataHistories) {
        val intent = Intent("ActivityUpdate")
        val b = Bundle()
        b.putParcelable("history", history)
        intent.putExtra("data", b)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }

    inner class ActivityRecognitionBroadcastReceiver: BroadcastReceiver() {

        // Pass results from the Activity Recognition API to the activityManager
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val b = it.getBundleExtra("data")
                val activityRecognitionData = b.getParcelable<Parcelable>("activityRecognitionResult") as ActivityRecognitionResult
                activityManager.onRegisterNewActivityData(activityRecognitionData)
            }
        }

    }

}