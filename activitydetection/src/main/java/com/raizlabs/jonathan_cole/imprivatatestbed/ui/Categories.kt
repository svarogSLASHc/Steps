package com.raizlabs.jonathan_cole.imprivatatestbed.ui

import com.google.android.gms.location.DetectedActivity

object ActivityCategories {
    val mapping: HashMap<Int, String> = hashMapOf(
        DetectedActivity.STILL to "Still",
        DetectedActivity.WALKING to "Walking",
        DetectedActivity.RUNNING to "Running",
        DetectedActivity.ON_FOOT to "On Foot",
//        DetectedActivity.IN_VEHICLE to "In Vehicle",
//        DetectedActivity.ON_BICYCLE to "On Bicycle",
//        DetectedActivity.TILTING to "Tilting",
        DetectedActivity.UNKNOWN to "Unknown"
    )
}