package com.test.pedometer.domain.pedometer

interface StepCountListener {
    fun onStepCount(stepCount: Int)
}