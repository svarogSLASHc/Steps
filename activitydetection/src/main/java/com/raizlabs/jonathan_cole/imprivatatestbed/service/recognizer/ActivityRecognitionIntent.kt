package com.raizlabs.jonathan_cole.imprivatatestbed.service.recognizer

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult

// Created when the Activity Recognition API dispatches an update. Broadcasts the data
// locally and then destroys itself.
class ActivityRecognitionIntent(name: String? = "ActivityRecognitionIntent") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        intent.let {
            if (ActivityRecognitionResult.hasResult(it)) {
                val result = ActivityRecognitionResult.extractResult(it)
                broadcastResult(result)
            }
        }
    }

    private fun broadcastResult(result: ActivityRecognitionResult) {
        val intent = Intent("ActivityRecognitionUpdate")
        val b = Bundle()
        b.putParcelable("activityRecognitionResult", result)
        intent.putExtra("data", b)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}