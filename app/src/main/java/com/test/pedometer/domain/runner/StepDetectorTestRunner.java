package com.test.pedometer.domain.runner;

import android.content.Context;
import android.util.Log;

import com.test.pedometer.R;
import com.test.pedometer.StepApplication;
import com.test.pedometer.data.activity_recognition.ActivityRecognitionController;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.domain.fileaccess.ActivityLoggerController;
import com.test.pedometer.domain.fileaccess.PedometerLoggerController;
import com.test.pedometer.domain.pedometer.PedometerRunnerController;
import com.test.pedometer.ui.acitivty_recognition.UtilsRecognition;
import com.test.pedometer.ui.tts.SpeakManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

public class StepDetectorTestRunner {
    private static String TAG = "StepDetectorTestRunner";
    private static StepDetectorTestRunner INSTANCE;
    private final SettingsManager settingsManager;
    private final PedometerRunnerController pedometerController;
    private final PedometerLoggerController pedometerLogger;
    private final ActivityRecognitionController recognitionController;
    private final ActivityLoggerController activityLogger;
    private final String resultLogString;
    private final String activityLogString;
    private final SpeakManager speaker;
    private final Context context;
    private BehaviorSubject<Integer> currentRound = BehaviorSubject.create(0);
    private BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private Subscription testSubscription = Subscriptions.empty();
    private PublishSubject<Void> roundTick = PublishSubject.create();
    private PublishSubject<String> logEvent = PublishSubject.create();

    private StepDetectorTestRunner(Context context) {
        this.context = context;
        settingsManager = SettingsManager.getInstance(context);
        pedometerController = StepApplication.getInstance(context).getObjectInstances().getPedometerController();
        pedometerLogger = PedometerLoggerController.getInstance(context);
        resultLogString = context.getString(R.string.step_counter_result);
        activityLogString = context.getString(R.string.recognition_format);
        speaker = SpeakManager.getInstance(context);
        recognitionController = ActivityRecognitionController.getInstance(context);
        activityLogger = ActivityLoggerController.getInstance(context);
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
        isRunning.onNext(true);
        testSubscription = speaker.startSpeak(pocket)
                .concatMap(o -> roundsIntervals(roundsN))
                .doOnNext(round -> currentRound.onNext(round))
                .concatMap(round -> speaker.roundSpeak(stepsN, round))
                .concatMap(round -> startMeasureMovement(delay, pocket, round)
                        .concatMap(s -> speaker.stopSpeak(roundsN, round))
                        .doOnNext(s -> roundTick.onNext(null)))
                .onErrorResumeNext(throwable -> Observable.just(throwable.getMessage()))
                .subscribeOn(Schedulers.io())
                .subscribe(s -> {
                        },
                        throwable -> {
                        },
                        this::handleComplete);
    }

    private Observable<String> startMeasureMovement(int delay, String pocket, int round) {
        return Observable.zip(
                getStepsResult(delay, pocket, round),
                startActivityRecognition(delay), (s1, s2) -> s1)
                .take(1);
    }

    private Observable<String> startActivityRecognition(Integer delay) {
        recognitionController.requestActivityUpdatesButtonHandler();
        return recognitionController.activitiesObservable()
                .map(activities -> UtilsRecognition.mapRawActivityToString(context, activityLogString, activities))
                .buffer(delay, TimeUnit.SECONDS)
                .take(1)
                .flatMap(Observable::from)
                .collect(StringBuilder::new, (builder, s) -> builder.append(s).append("\n"))
                .map(StringBuilder::toString)
                .doOnNext(data -> {
                    activityLogger.add(data);
                    recognitionController.removeActivityUpdatesButtonHandler();
                });
    }

    private Observable<String> getStepsResult(Integer delay, String pocket, int round) {
        return pedometerController.getStepsObservable(delay)
                .map(steps -> getStepResultString(pocket, round, steps))
                .map(pedometerLogger::format)
                .doOnNext(pedometerLogger::add);
    }

    private Observable<Integer> roundsIntervals(int totalRounds) {
        return Observable.merge(
                Observable.just(1),
                Observable.zip(Observable.range(2, totalRounds - 1), roundTick,
                        (integer, aBoolean) -> integer));
    }

    private void handleComplete() {
        addToLog(pedometerLogger.saveLog());
        addToLog(activityLogger.saveLog());
        isRunning.onNext(false);
    }

    private void addToLog(String s) {
        Log.d(TAG, s);
        logEvent.onNext(s);
    }

    public void stop() {
        if (testSubscription != null && !testSubscription.isUnsubscribed()) {
            testSubscription.unsubscribe();
        }
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


    private String getStepResultString(String pocket, int round, int stepCounter) {
        return String.format(resultLogString,
                pocket.toLowerCase(),
                round,
                settingsManager.getRounds(),
                settingsManager.getSteps(),
                stepCounter);
    }
}
