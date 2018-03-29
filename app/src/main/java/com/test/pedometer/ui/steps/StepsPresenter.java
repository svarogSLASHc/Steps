package com.test.pedometer.ui.steps;

import android.os.Handler;
import android.os.Looper;

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

    public void pocketSelected(String title) {
        setPockets(title);
    }

    public void sendClick() {
        int resultsCount = getLogSize();
        try {
            final int finalResultsCount = resultsCount;
            NetworkController.getInstance(view.getContext())
                    .uploadResults(fileLog.getLogFileAsString())
                    .subscribeOn(Schedulers.io())
                    .onErrorResumeNext(throwable -> {
                        handlerMainThread.post(() -> view.showError(throwable.getMessage()));
                        return Observable.just("");
                    })
                    .subscribe(s -> handlerMainThread.post(() -> {
                        deleteLog();
                        view.showSuccess(s + "\nUploaded " + finalResultsCount + " results");
                    }));

        } catch (FileNotFoundException e) {
            view.showError(e.getMessage());
        }
    }

    public void deleteClick() {
        deleteLog();
        view.showSuccess("Data was successfully deleted");
    }

    private void deleteLog() {
        stepDetectorTestRunner.deleteLog();
        enableStart();
    }

    public void startClick() {
        subscribeOnSteps();
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
                                handlerMainThread.post(view::testIsRunning);
                            } else if (hasPreviousResults()) {
                                handlerMainThread.post(view::testIsFinished);
                            } else {
                                enableStart();
                            }
                        }));
    }

    public void unsubscribeFromSteps() {
        if (null != currentRoundSubscription && !currentRoundSubscription.isUnsubscribed()) {
            currentRoundSubscription.unsubscribe();
        }
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

    private void enableStart() {
        view.enableStart();
        view.disableDelete();
    }

    private boolean hasPreviousResults() {
        return getLogSize() > 0;
    }

    private int getLogSize() {
        int resultsCount = 0;
        try {
            for (String item : fileLog.getLogFileAsString().split("\n")) {
                if (!item.isEmpty()) {
                    resultsCount++;
                }
            }
        } catch (FileNotFoundException e) {

        }
        return resultsCount;
    }
}
