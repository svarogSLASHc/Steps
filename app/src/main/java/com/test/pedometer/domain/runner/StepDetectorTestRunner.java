package com.test.pedometer.domain.runner;

import android.content.Context;
import android.util.Log;

import com.raizlabs.jonathan_cole.imprivatatestbed.detection.ActivityDataHistories;
import com.raizlabs.jonathan_cole.imprivatatestbed.manager.ActivityDetectorManager;
import com.raizlabs.jonathan_cole.imprivatatestbed.utility.ActivityDetectionModelAdapter;
import com.test.pedometer.R;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.domain.fileaccess.PedometerLoggerController;
import com.test.pedometer.ui.tts.SpeakManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class StepDetectorTestRunner {
    private static String TAG = "StepDetectorTestRunner";
    private static StepDetectorTestRunner INSTANCE;
    private final SettingsManager settingsManager;
    private final ActivityDetectorManager detectorManager;
    private final PedometerLoggerController pedometerLogger;
    private final SpeakManager speaker;
    private final String resultLogString;
    private BehaviorSubject<Integer> currentRound = BehaviorSubject.create(0);
    private BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private CompositeSubscription testSubscription = new CompositeSubscription();
    private BehaviorSubject<Integer> roundTick = BehaviorSubject.create();
    private PublishSubject<String> logEvent = PublishSubject.create();

    private StepDetectorTestRunner(Context context) {
        this.detectorManager = new ActivityDetectorManager(context);
        settingsManager = SettingsManager.getInstance(context);
        pedometerLogger = PedometerLoggerController.getInstance(context);
        resultLogString = context.getString(R.string.step_counter_result);
        speaker = SpeakManager.getInstance(context);
    }

    public static StepDetectorTestRunner getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new StepDetectorTestRunner(context);
        }
        return INSTANCE;
    }

    public void start() {
        stop();
        final String pocket = settingsManager.getPocket();
        final int delay = settingsManager.getRoundTime();
        final int roundsN = settingsManager.getRounds();
        final int stepsN = settingsManager.getSteps();
        roundTick.onNext(1);
        isRunning.onNext(true);
        testSubscription.add(
                speaker.startSpeak(pocket)
                        .concatMap(o -> roundsIntervals(roundsN))
                        .doOnNext(round -> currentRound.onNext(round))
                        .concatMap(round -> speaker.roundSpeak(stepsN, round))
                        .doOnNext(round -> pedometerLogger.add(pedometerLogger.formatStart(getRoundString(pocket, round))))
                        .concatMap(round -> startMeasureMovement(delay, pocket, round)
                                .concatMap(s -> speaker.stopSpeak(roundsN, round))
                                .doOnNext(s -> roundTick.onNext(1 + round)))
                        .onErrorResumeNext(throwable -> Observable.just(throwable.getClass().getCanonicalName() + " " + throwable.getMessage()))
//                .subscribeOn(Schedulers.io())
                        .subscribe(s -> {
                                },
                                throwable -> {
                                },
                                this::handleComplete)
        );
    }

    private Observable<String> startMeasureMovement(int delay, String pocket, int round) {
        return startActivityDetection(delay)
                .map(s -> getRoundString(pocket, round))
                .collect(StringBuilder::new, StringBuilder::append)
                .map(StringBuilder::toString)
                .doOnNext(data -> detectorManager.unregisterListeners());
    }

    private Observable<String> startActivityDetection(Integer delay) {
        return Observable.<ActivityDataHistories>create(subscriber ->
                detectorManager.registerWithDispatch(subscriber::onNext))
                .take(delay, TimeUnit.SECONDS)
                .map(ActivityDetectionModelAdapter::new)
                .map(ActivityDetectionModelAdapter::formatHistoryData)
                .doOnNext(pedometerLogger::add);
    }

    private Observable<Integer> roundsIntervals(int totalRounds) {
        return roundTick.take(totalRounds);
    }

    private void handleComplete() {
        addToLog(pedometerLogger.saveLog());
        isRunning.onNext(false);
        stop();
    }

    private void addToLog(String s) {
        Log.d(TAG, s);
        logEvent.onNext(s);
    }

    public void stop() {
        testSubscription.clear();
    }

    public Observable<String> logs() {
        return logEvent;
    }

    public Observable<Boolean> isRunning() {
        return isRunning;
    }

    public Observable<Integer> currentRoundObservable() {
        return currentRound;
    }


    private String getRoundString(String pocket, int round) {
        return String.format(resultLogString,
                pocket.toLowerCase(),
                round,
                settingsManager.getRounds(),
                settingsManager.getSteps());
    }
}
