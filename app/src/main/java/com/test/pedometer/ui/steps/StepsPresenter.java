package com.test.pedometer.ui.steps;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.test.pedometer.R;
import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.data.network.NetworkController;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.domain.fileaccess.ActivityLoggerController;
import com.test.pedometer.domain.fileaccess.DebugLoggerController;
import com.test.pedometer.domain.fileaccess.PedometerLoggerController;
import com.test.pedometer.domain.runner.StepDetectorTestRunner;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class StepsPresenter extends BasePresenter<StepsView> {
    private static final String UPLOAD_ERROR = "UPLOAD_ERROR";
    private final StepDetectorTestRunner stepDetectorTestRunner;
    private final SettingsManager settingsManager;
    private final DebugLoggerController debugLogger;
    private PedometerLoggerController pedometerLogger;
    private ActivityLoggerController activityLogger;
    private CompositeSubscription currentRoundSubscription = new CompositeSubscription();
    private Handler handlerMainThread = new Handler(Looper.getMainLooper());

    public StepsPresenter(StepsView view) {
        super(view);
        stepDetectorTestRunner = StepDetectorTestRunner.getInstance(view.getContext().getApplicationContext());
        pedometerLogger = PedometerLoggerController.getInstance(view.getContext());
        settingsManager = SettingsManager.getInstance(view.getContext());
        debugLogger = DebugLoggerController.getInstance(view.getContext());
        activityLogger = ActivityLoggerController.getInstance(view.getContext());
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
        final int resultsCount = getLogSize();
        final NetworkController networkController = NetworkController.getInstance(view.getContext());
        networkController.uploadResults(pedometerLogger.getLogFileAsString())
                .delay(3, TimeUnit.SECONDS)
                .concatMap(s -> networkController.uploadResults(activityLogger.getLogFileAsString()))
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(this::handleUploadError)
                .filter(response -> !UPLOAD_ERROR.equals(response))
                .subscribe(s -> handlerMainThread.post(() -> {
                    deleteLog();
                    view.showSuccess(s + "\nUploaded " + resultsCount + " results");
                }));
    }

    public void deleteClick() {
        deleteLog();
        view.showSuccess("Data was successfully deleted");
    }

    public void subscribeOnSteps() {
        currentRoundSubscription.add(Subscriptions.from(
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
                        }),
                stepDetectorTestRunner.logs().subscribe(s ->
                        handlerMainThread.post(() -> {
                            debugLogger.save(s);
                            view.showStepResult(s);
                        })
                )
        ));
    }

    public void unsubscribeFromSteps() {
        currentRoundSubscription.clear();
    }

    @NonNull
    private Observable<? extends String> handleUploadError(Throwable throwable) {
        String errorMsg;
        if (throwable instanceof com.android.volley.TimeoutError |
                throwable instanceof com.android.volley.NoConnectionError) {
            errorMsg = view.getContext().getString(R.string.error_msg_timeout);
        } else {
            errorMsg = throwable.getMessage();
        }
        handlerMainThread.post(() -> view.showError(errorMsg));
        return Observable.just(UPLOAD_ERROR);
    }

    private void deleteLog() {
        pedometerLogger.clear();
        activityLogger.clear();
        enableStart();
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
        for (String item : pedometerLogger.getLogFileAsString().split("\n")) {
            if (!item.isEmpty()) {
                resultsCount++;
            }
        }
        return resultsCount;
    }


    public void getLogInternal() {
        view.showLog(debugLogger.getLogFileAsString());
    }

    public void clearLogInternal() {
        debugLogger.clear();
    }
}
