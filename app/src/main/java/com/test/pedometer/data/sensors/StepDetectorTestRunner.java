package com.test.pedometer.data.sensors;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.test.pedometer.R;
import com.test.pedometer.data.fileaccess.FileLoggerController;
import com.test.pedometer.data.settings.SettingsManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.Subscriptions;

public class StepDetectorTestRunner {
    private static String TAG = "StepDetectorTestRunner";
    private static StepDetectorTestRunner INSTANCE;
    private final SettingsManager settingsManager;
    private final PedometerController pedometerController;
    private final FileLoggerController loggerController;
    private final String resultLogString;
    private BehaviorSubject<Integer> currentRound = BehaviorSubject.create(0);
    private BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private Subscription testSubscription = Subscriptions.empty();

    private StepDetectorTestRunner(Context context) {
        settingsManager = SettingsManager.getInstance(context);
        pedometerController = PedometerController.getInstance(context);
        loggerController = FileLoggerController.newInstance(context);
        resultLogString = context.getString(R.string.step_counter_result);
    }

    public static StepDetectorTestRunner getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new StepDetectorTestRunner(context);
        }
        return INSTANCE;
    }

    public void start() {
        pedometerController.reset();
        final String pocket = settingsManager.getPocket();
        final int delay = settingsManager.getRoundTime();
        isRunning.onNext(true);
        testSubscription =
                Observable.merge(Observable.just(1),
                        Observable.range(2, settingsManager.getRounds() - 1)
                                .concatMap(i -> Observable.just(i).delay(delay, TimeUnit.SECONDS))
                )
                        .doOnNext(round -> {
                            currentRound.onNext(round);
                        })
                        .concatMap(round -> stepResults(delay)
                                .map(resultPair -> {
                                    pedometerController.reset();
                                    return getStepResultString(pocket, round, resultPair.first, resultPair.second);
                                }))
                        .onErrorResumeNext(throwable -> Observable.just(throwable.getMessage()))
                        .subscribeOn(Schedulers.io())
                        .doOnCompleted(this::emitFinish)
                        .subscribe(loggerController::logPedometerData);
    }

    private void emitFinish() {
        isRunning.onNext(false);
        currentRound.onNext(0);
    }

    private Observable<Pair<Integer, Integer>> stepResults(Integer delay) {
        return Observable.combineLatest(
                Observable.<Integer>create(subscriber ->
                        pedometerController.registerCounterListener(subscriber::onNext))
                        .buffer(delay, TimeUnit.SECONDS)
                        .map(integers -> integers.isEmpty() ? 0 : integers.get(integers.size()-1))
                        .doOnNext(integer ->  Log.v(TAG, "CounterListener. Value: " + integer)),
                Observable.<Integer>create(subscriber ->
                        pedometerController.registerDetectorListener(subscriber::onNext))
                        .buffer(delay, TimeUnit.SECONDS)
                        .map(integers -> integers.isEmpty() ? 0 : integers.get(integers.size()-1))
                        .doOnNext(integer ->  Log.v(TAG, "DetectorListener. Value: " + integer)),
                Pair::create)
                .take(1);
    }

    public void stop() {
        if (testSubscription != null && !testSubscription.isUnsubscribed()) {
            testSubscription.unsubscribe();
        }
    }

    public Observable<Boolean> isRunning() {
        return isRunning;
    }

    public Observable<Integer> currentRoundObservable() {
        return currentRound;
    }

    public void deleteLog() {
        loggerController.clear();
    }

    private String getStepResultString(String pocket, int round, int stepCounter, int stepDetector) {
        return String.format(resultLogString,
                pocket.toLowerCase(),
                round,
                settingsManager.getRounds(),
                settingsManager.getSteps(),
                stepCounter,
                stepDetector);
    }
}
