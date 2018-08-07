package com.raizlabs.jonathan_cole.imprivatatestbed.manager

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult
import com.raizlabs.jonathan_cole.imprivatatestbed.service.ActivityDataHistories
import com.raizlabs.jonathan_cole.imprivatatestbed.service.recognizer.ActivityRecognitionIntent
import com.raizlabs.jonathan_cole.imprivatatestbed.utility.SingletonHolder

// Singleton for easy register/unregister broadcasts.
class BroadcastManager private constructor(var context: Context) {
    private var localBroadcastManager = LocalBroadcastManager.getInstance(context)
    private lateinit var recognitionBroadcastReceiver: ActivityRecognitionBroadcastReceiver
    private lateinit var historyBroadcastReceiver: ActivityHistoryBroadcastReceiver

    companion object : SingletonHolder<BroadcastManager, Context>(::BroadcastManager)


    object IntentConst {
        const val RECOGNITION_ACTION = "ActivityRecognitionUpdate"
        const val RECOGNITION_BUNDLE_KEY = "activityRecognitionResult"
        const val RECOGNITION_EXTRA_NAME = "data"

        const val HISTORY_ACTION = "ActivityUpdate"
        const val HISTORY_BUNDLE_KEY = "history"
        const val HISTORY_EXTRA_NAME = "data"
    }


    /**
     * Receive updates from any ActivityRecognitionIntent PendingIntents.
     */
    fun registerForHistoryUpdates(action: (newData: ActivityDataHistories) -> Unit) {
        historyBroadcastReceiver = ActivityHistoryBroadcastReceiver(action)
        localBroadcastManager.registerReceiver(
                historyBroadcastReceiver, IntentFilter(IntentConst.HISTORY_ACTION))
    }


    fun dispatchNewDataHistory(history: ActivityDataHistories) {
        val intent = Intent(IntentConst.HISTORY_ACTION)
        val b = Bundle()
        b.putParcelable(IntentConst.HISTORY_BUNDLE_KEY, history)
        intent.putExtra(IntentConst.HISTORY_EXTRA_NAME, b)
        localBroadcastManager.sendBroadcast(intent)

    }

    fun unregisterFromNewDataHistory() {
        historyBroadcastReceiver?.let{
            localBroadcastManager.unregisterReceiver(it)
        }
    }


    /**
     * Receive updates from any ActivityRecognitionIntent PendingIntents.
     */
    fun registerForActivityRecognitionUpdates(action: (newData: ActivityRecognitionResult) -> Unit) {
        recognitionBroadcastReceiver = ActivityRecognitionBroadcastReceiver(action)
        localBroadcastManager.registerReceiver(
                recognitionBroadcastReceiver, IntentFilter(IntentConst.RECOGNITION_ACTION))
    }


    fun broadcastActivityRecognitionResult(result: ActivityRecognitionResult) {
        val intent = Intent(IntentConst.RECOGNITION_ACTION)
        val b = Bundle()
        b.putParcelable(IntentConst.RECOGNITION_BUNDLE_KEY, result)
        intent.putExtra(IntentConst.RECOGNITION_EXTRA_NAME, b)
        localBroadcastManager.sendBroadcast(intent)
    }

    fun unregisterFromActivityRecognitionUpdates() {
        recognitionBroadcastReceiver?.let{
            localBroadcastManager.unregisterReceiver(it)
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity recognition update.
     */
    fun getActivityDetectionPendingIntent(): PendingIntent {
        val intent = Intent(context, ActivityRecognitionIntent::class.java)

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    inner class ActivityRecognitionBroadcastReceiver(private val action: (newData: ActivityRecognitionResult) -> Unit) : BroadcastReceiver() {

        // Pass results from the Activity Recognition API to the activityManager
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val b = it.getBundleExtra(IntentConst.RECOGNITION_EXTRA_NAME)
                val activityRecognitionData = b.getParcelable<Parcelable>(IntentConst.RECOGNITION_BUNDLE_KEY) as ActivityRecognitionResult
                action(activityRecognitionData)
            }
        }
    }

    inner class ActivityHistoryBroadcastReceiver(private val action: (newData: ActivityDataHistories) -> Unit) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val b = it.getBundleExtra(IntentConst.HISTORY_EXTRA_NAME)
                val history = b.getParcelable<Parcelable>(IntentConst.HISTORY_BUNDLE_KEY) as ActivityDataHistories
                action(history)
            }
        }

    }
}
