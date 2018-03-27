package com.test.pedometer.ui.setting;

import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.data.settings.SettingsManager;

public class SettingsPresenter extends BasePresenter<SettingsView> {
    private SettingsManager settingsManager;

    protected SettingsPresenter(SettingsView view) {
        super(view);
        settingsManager = SettingsManager.getInstance(view.getContext());
    }

    public void onViewCreated(){
        view.setSteps(settingsManager.getSteps());
        view.setRounds(settingsManager.getRounds());
        view.setRoundTime(settingsManager.getRoundTime());
    }

    public void setSteps(int steps){
        settingsManager.setSteps(steps);
    }

    void setRounds(int rounds){
        settingsManager.setRounds(rounds);
    }

    void setRoundTime(int time){
        settingsManager.setRoundTime(time);
    }
}
