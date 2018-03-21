package com.test.pedometer.domain;

public interface StepCountListener {

    void onStepDataUpdate(int stepCount);

    void onStep(int count);
}