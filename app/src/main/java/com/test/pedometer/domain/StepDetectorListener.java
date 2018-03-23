package com.test.pedometer.domain;

public interface StepDetectorListener {
    void onStepDetected(int stepCount);
}