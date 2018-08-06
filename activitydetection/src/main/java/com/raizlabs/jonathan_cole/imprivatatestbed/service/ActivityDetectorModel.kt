package com.raizlabs.jonathan_cole.imprivatatestbed.service

import android.hardware.Sensor
import android.os.Parcelable
import com.google.android.gms.location.ActivityRecognitionResult
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ReadingBase: Parcelable {
    // Using timestamp from System instead of `event.timestamp` because vendors will
    // implement it differently
    val timestampMS: Long = System.currentTimeMillis()
}

data class EventReading(val sensor: Sensor?, val values: FloatArray): ReadingBase()
data class ActivityReading(val event: ActivityRecognitionResult): ReadingBase()

@Parcelize
data class ActivityDataHistories(val stepCounter: List<EventReading>,
                                 val stepDetector: List<EventReading>,
                                 val significantMotion: List<EventReading>,
                                 val activityRecognition: List<ActivityReading>,
                                 val proximityDetector: List<EventReading>
                                ) : Parcelable