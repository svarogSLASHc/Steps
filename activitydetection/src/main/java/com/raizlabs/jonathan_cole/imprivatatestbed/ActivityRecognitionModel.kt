package com.raizlabs.jonathan_cole.imprivatatestbed

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ActivityRecognitionModel(val activity: Int, val name: String, var confidence: Int = 0): Parcelable