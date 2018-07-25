package com.test.pedometer.domain.pedometer;

public interface StepDetectorListener {
    void onStepDetected(int stepCount);
}