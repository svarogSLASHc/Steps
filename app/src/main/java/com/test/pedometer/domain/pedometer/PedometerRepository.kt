package com.test.pedometer.domain.pedometer

interface PedometerRepository {
    fun registerStepListener(listener: StepCountListener)

    fun onDestroy()

    fun reset()
}
