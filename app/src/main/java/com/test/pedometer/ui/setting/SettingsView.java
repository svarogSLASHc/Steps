package com.test.pedometer.ui.setting;

import com.test.pedometer.common.BaseView;

public interface SettingsView  extends BaseView{

    void setSteps(int steps);

    void setRounds(int rounds);

    void setRoundTime(int time);

    void showSuccess();

    interface SaveListener{
        void onSaveClicked();
    }
}
