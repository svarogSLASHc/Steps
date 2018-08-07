package com.test.pedometer.ui.steps

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.test.pedometer.R


class RoundResultDialog(context: Context?, attributes: AttributeSet?) : LinearLayout(context, attributes) {

    constructor(context: Context?):this(context, null)
    init{
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.dialog_scrollable_content, this, true)
    }

    fun setMessage(message: CharSequence?) {
        findViewById<TextView>(R.id.text_dialog)!!.text = message
    }
}