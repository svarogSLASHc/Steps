package com.raizlabs.jonathan_cole.imprivatatestbed.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.raizlabs.jonathan_cole.imprivatatestbed.utility.SingletonHolder
import java.util.*

// Singleton for easy text-to-speech.
class TTSManager private constructor(context: Context): TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech = TextToSpeech(context, this)
    private var initialized = false

    companion object: SingletonHolder<TTSManager, Context>(::TTSManager)

    // For TextToSpeech.OnInitListener, not overall init
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
            textToSpeech.setPitch(1.0f)
            initialized = true
        } else {
            Log.e("TTSManager", "Error while initializing TextToSpeech")
            initialized = false
        }
    }

    fun speak(text: String, rate: Float = 1.0f) {
        if (initialized) {
            textToSpeech.setSpeechRate(rate)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
        }
    }

    fun onDestroy() {
        if (initialized) {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
            textToSpeech.shutdown()
        }
    }

}