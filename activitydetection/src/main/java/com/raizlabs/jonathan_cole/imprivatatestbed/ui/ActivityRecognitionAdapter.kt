package com.raizlabs.jonathan_cole.imprivatatestbed.ui

import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.gms.location.ActivityRecognitionResult
import com.raizlabs.jonathan_cole.imprivatatestbed.R

class ActivityRecognitionAdapter(val data: List<ActivityRecognitionModel>): RecyclerView.Adapter<ActivityRecognitionAdapter.ViewHolder>() {

    class ViewHolder(someView: View) : RecyclerView.ViewHolder(someView) {
        val nameText: TextView = someView.findViewById(R.id.readingNameLabel)
        val confidenceText: TextView = someView.findViewById(R.id.readingConfidenceLabel)
        val progress: ProgressBar = someView.findViewById(R.id.progressBar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_reading, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = data[position]
        holder.nameText.text = activity.name
        holder.confidenceText.text = "${activity.confidence}%"
        holder.progress.progress = activity.confidence

        if (activity.confidence > 50) {
            holder.progress.progressDrawable.setColorFilter(0xFF02CC12.toInt(), PorterDuff.Mode.SRC_IN)
        } else {
            holder.progress.progressDrawable.setColorFilter(0xFF0271CC.toInt(), PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    fun updateDataWithResult(result: ActivityRecognitionResult) {
        Log.d("activityUpdate", result.probableActivities.toString())

        ActivityCategories.mapping.keys.forEach { category ->
            data.find { it.activity == category }?.confidence = result.getActivityConfidence(category)
        }

        notifyDataSetChanged()
    }


}