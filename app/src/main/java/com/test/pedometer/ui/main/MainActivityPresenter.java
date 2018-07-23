package com.test.pedometer.ui.main;

import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.data.sensors.StepDetectorTestRunner;
import com.test.pedometer.domain.whitelist.PopupController;
import com.test.pedometer.ui.tts.SpeakManager;

public class MainActivityPresenter extends BasePresenter<MainActivityView>{
    private final PopupController popupController;

    protected MainActivityPresenter(MainActivityView view) {
        super(view);
        popupController = PopupController.newInstance(view.getContext());
        SpeakManager.getInstance(view.getContext());
        StepDetectorTestRunner.getInstance(view.getContext());
    }

    public boolean isSumsung(){
        return popupController.isSamsung(view.getContext());
    }

    public void firstTimeAndShow(){
        if (isSumsung() && !popupController.showed()) {
            view.showSamsungPopup();
        }
        popupController.setShowed();
    }
}
