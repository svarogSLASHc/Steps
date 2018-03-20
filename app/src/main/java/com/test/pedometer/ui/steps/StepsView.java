package com.test.pedometer.ui.steps;

import android.support.annotation.StringRes;

import com.test.pedometer.common.BaseView;

public interface StepsView extends BaseView {

    void setSepsCounted(int steps);

    void setSepsDetected(int steps);

    void sendClick();

    void deleteClick();

    void saveClick();

    void showError(@StringRes int errorMsg);

    void showError(String errorMsg);
}
