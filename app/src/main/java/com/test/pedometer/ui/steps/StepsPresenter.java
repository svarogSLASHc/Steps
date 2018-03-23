package com.test.pedometer.ui.steps;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.test.pedometer.R;
import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.data.fileaccess.FileLoggerController;
import com.test.pedometer.data.network.NetworkController;
import com.test.pedometer.data.sensors.StepDetectorTestRunner;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class StepsPresenter extends BasePresenter<StepsView> {
    private final StepDetectorTestRunner stepDetectorTestRunner;
    private final SettingsManager settingsManager;
    private FileLoggerController fileLog;
    private Subscription currentRoundSubscription = Subscriptions.empty();
    private Handler handlerMainThread = new Handler(Looper.getMainLooper());

    protected StepsPresenter(StepsView view) {
        super(view);
        stepDetectorTestRunner = StepDetectorTestRunner.getInstance(view.getContext().getApplicationContext());
        fileLog = FileLoggerController.newInstance(view.getContext());
        settingsManager = SettingsManager.getInstance(view.getContext());
    }

    @Override
    public void onViewCreated() {
        setPockets(null);
        view.setStepsCounted(settingsManager.getSteps());
        view.setTotalRounds(settingsManager.getRounds());
    }

    private void setPockets(String current) {
        final String[] pockets = view.getContext().getResources().getStringArray(R.array.pockets_list);
        if (null == current) {
            current = pockets[0];
        }

        List<ListItem> pocketViewModels = new ArrayList<>(pockets.length);
        for (String item : pockets) {
            pocketViewModels.add(new PocketViewModel(item, item.equals(current)));
        }
        view.setPocketList(pocketViewModels);
        settingsManager.setPocket(current);
    }

    public void pocketSelected(String title) {
        setPockets(title);
    }

    public void sendClick() {
        try {
            final int resultsCount = fileLog.getLogFileAsString().split("\n").length;
            if (resultsCount == 0){
                view.showError(view.getContext().getString(R.string.nothing));
                return;
            }
            NetworkController.getInstance(view.getContext())
                    .uploadResults(fileLog.getLogFileAsString())
                    .subscribeOn(Schedulers.io())
                    .onErrorResumeNext(throwable -> {
                        handlerMainThread.post(() -> view.showError(throwable.getMessage()));
                        return Observable.just("");
                    })
                    .subscribe(s -> {
                        handlerMainThread.post(() -> view.showSuccess(s + "\nUploaded " + resultsCount + " results"));
                        fileLog.clear();
                    });

        } catch (FileNotFoundException e) {
            view.showError(e.getMessage());
        }
    }

    public void deleteClick() {
        unsubscribeFromSteps();
        stepDetectorTestRunner.deleteLog();
        enableStart();
    }

    private void enableStart() {
        view.enableStart();
        view.disableDelete();
    }

    public void startClick() {
        subscribeOnSteps();
        disableStart();
    }

    private void disableStart() {
        view.disableStart();
        view.enableDelete();
    }

    public void subscribeOnSteps() {
        currentRoundSubscription = Subscriptions.from(
                stepDetectorTestRunner
                        .currentRoundObservable()
                        .subscribe(round -> handlerMainThread.post(() -> view.setCurrentRound(round))),
                stepDetectorTestRunner
                        .isRunning()
                        .subscribe(running -> {
                            if (running) {
                                handlerMainThread.post(this::disableStart);
                            } else {
                                handlerMainThread.post(this::enableStart);
                            }
                        }));
    }

    public void unsubscribeFromSteps() {
        if (null != currentRoundSubscription && !currentRoundSubscription.isUnsubscribed()) {
            currentRoundSubscription.unsubscribe();
        }
    }
}
