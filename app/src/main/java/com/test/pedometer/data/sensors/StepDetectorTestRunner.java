package com.test.pedometer.data.sensors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.test.pedometer.R;
import com.test.pedometer.StepApplication;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.domain.fileaccess.PedometerLoggerController;
import com.test.pedometer.domain.pedometer.PedometerRunnerController;
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
    private final String resultLogString;
    private final SpeakManager speaker;
    private BehaviorSubject<Integer> currentRound = BehaviorSubject.create(0);
    private BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private Subscription testSubscription = Subscriptions.empty();
    private PublishSubject<Void> roundTick = PublishSubject.create();
    private PublishSubject<String> logEvent = PublishSubject.create();

    private StepDetectorTestRunner(Context context) {
        settingsManager = SettingsManager.getInstance(context);
        pedometerController = StepApplication.getInstance(context).getObjectInstances().getPedometerController();
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
        pedometerController.reset();
        stop();
        final String pocket = settingsManager.getPocket();
        final int delay = settingsManager.getRoundTime();
        final int roundsN = settingsManager.getRounds();
        final int stepsN = settingsManager.getSteps();
        isRunning.onNext(true);
        testSubscription = startSpeak(pocket)
                .concatMap(o -> roundsIntervals())
                .concatMap(round -> roundSpeak(stepsN, round))
                .concatMap(round -> stepResults(delay)
                        .map(steps -> getStepResultString(pocket, round, steps))
                        .concatMap(s -> stopSpeak(roundsN, s)))
                .onErrorResumeNext(throwable -> Observable.just(throwable.getMessage()))
                .subscribeOn(Schedulers.io())
                .subscribe(pedometerLogger::add,
                        throwable -> {
                        },
                        this::handleComplete);
    }

    @NonNull
    private Observable<String> stopSpeak(int roundsN, String s) {
        return Observable.fromCallable(() -> {
            pedometerController.reset();

            if (currentRound.getValue() < roundsN) {
                speaker.speak("stop");
            } else {
                speaker.speak("done with all rounds");
            }
            Thread.sleep(1000);
            roundTick.onNext(null);
            return s;
        });
    }

    @NonNull
    private Observable<Integer> roundSpeak(int stepsN, Integer round) {
        return Observable.fromCallable(() -> {
            currentRound.onNext(round);
            speaker.speak(String.format("Round %d. When you hear the word Go, take %d steps",
                    round,
                    stepsN));
            Thread.sleep(4000);
            speaker.speak("Ready.");
            Thread.sleep(1000);
            speaker.speak("set");
            Thread.sleep(1000);
            speaker.speak("go");
            Thread.sleep(500);
            return round;

        });
    }

    private Observable<Integer> roundsIntervals() {
        return Observable.merge(
                Observable.just(1),
                Observable.zip(Observable.range(2, settingsManager.getRounds() - 1), roundTick,
                        (integer, aBoolean) -> integer));
    }

    private Observable<String> startSpeak(String pocket) {
        return Observable.fromCallable(() -> {
            startSpeak(pocket);

            String message;
            if (pocket.toLowerCase().contains("looking")) {
                message = "walk while looking at your phone";
            } else {
                message = "put your phone in your " + pocket;
            }
            speaker.speak(message);
            Thread.sleep(8000);
            return null;
        });
    }

    private void handleComplete() {
        addToLog(pedometerLogger.saveLog());
        isRunning.onNext(false);
    }

    private Observable<Integer> stepResults(Integer delay) {
        return Observable.<Integer>create(subscriber ->
                pedometerController.registerCounterListener(subscriber::onNext))
                .doOnNext(integer -> addToLog("Registered step: " + integer))
                .buffer(delay, TimeUnit.SECONDS)
                .take(1)
                .flatMap(Observable::from)
                .reduce(0, (integer, integer2) -> integer + integer2)
                .doOnNext(integer -> addToLog("Round value: " + integer));
    }

    private void addToLog(String s) {
        Log.v(TAG, s);
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

    public void deleteLog() {
        pedometerLogger.clear();
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
