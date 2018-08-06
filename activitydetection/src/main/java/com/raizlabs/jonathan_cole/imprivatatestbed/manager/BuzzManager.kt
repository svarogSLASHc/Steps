package com.raizlabs.jonathan_cole.imprivatatestbed.manager

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.raizlabs.jonathan_cole.imprivatatestbed.utility.SingletonHolder

// Singleton for easy device vibration.
class BuzzManager private constructor(context: Context) {
    private var vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    companion object: SingletonHolder<BuzzManager, Context>(::BuzzManager)

    fun buzz(lengthMS: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(lengthMS.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(lengthMS.toLong())
        }
    }

}
