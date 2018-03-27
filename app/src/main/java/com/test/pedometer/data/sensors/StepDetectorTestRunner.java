package com.test.pedometer.data.sensors;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.test.pedometer.R;
import com.test.pedometer.data.fileaccess.FileLoggerController;
import com.test.pedometer.data.settings.SettingsManager;
import com.test.pedometer.ui.tts.SpeakManager;

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
    private final SpeakManager speaker;
    private BehaviorSubject<Integer> currentRound = BehaviorSubject.create(0);
    private BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private Subscription testSubscription = Subscriptions.empty();

    private StepDetectorTestRunner(Context context) {
        settingsManager = SettingsManager.getInstance(context);
        pedometerController = PedometerController.getInstance(context);
        loggerController = FileLoggerController.newInstance(context);
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
        final String pocket = settingsManager.getPocket();
        final int delay = settingsManager.getRoundTime();
        final int roundsN = settingsManager.getRounds();
        final int stepsN = settingsManager.getSteps();
        isRunning.onNext(true);
        testSubscription =
                Observable.fromCallable(() -> {
                    startSpeak(pocket);
                    return null;
                })
                        .concatMap(o -> Observable.merge(Observable.just(1),
                                Observable.range(2, settingsManager.getRounds() - 1)
                                        .concatMap(i -> Observable.just(i).delay(delay, TimeUnit.SECONDS))
                        ))
                        .concatMap(round ->   Observable.fromCallable(() -> {
                            currentRound.onNext(round);
                            startRoundSpeak(round, stepsN);
                            return round;

                        }))
                        .concatMap(round -> stepResults(delay)
                                .map(resultPair -> {
                                    pedometerController.reset();
                                    return getStepResultString(pocket, round, resultPair.first, resultPair.second);
                                }))
                        .concatMap(s ->   Observable.fromCallable(() ->  {
                            stopSpeak(roundsN);
                            return s;
                        }))
                        .onErrorResumeNext(throwable -> Observable.just(throwable.getMessage()))
                        .subscribeOn(Schedulers.io())
                        .doOnCompleted(this::emitFinish)
                        .subscribe(loggerController::logPedometerData);
    }

    private void startSpeak(String pocket) throws InterruptedException {
        String message;
        if (pocket.toLowerCase().contains("looking")) {
            message = "walk while looking at your phone";
        } else {
            message = "put your phone in your " + pocket;
        }
        speaker.speak(message);
        Thread.sleep(3000);
    }

    private void startRoundSpeak(int round, int stepsToTake) throws InterruptedException {
        speaker.speak(String.format("Round %d. When you hear the word Go, take %d steps",
                round,
                stepsToTake));
        Thread.sleep(8000);
        speaker.speak("Ready.");
        Thread.sleep(1000);
        speaker.speak("set");
        Thread.sleep(1000);
        speaker.speak("go");
        Thread.sleep(500);
    }

    private void stopSpeak(int nRounds) {
        if (currentRound.getValue() < nRounds) {
            speaker.speak("stop");
        } else {
            speaker.speak("done with all rounds");
        }
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
                        .map(integers -> integers.isEmpty() ? 0 : integers.get(integers.size() - 1))
                        .doOnNext(integer -> Log.v(TAG, "CounterListener. Value: " + integer)),
                Observable.<Integer>create(subscriber ->
                        pedometerController.registerDetectorListener(subscriber::onNext))
                        .buffer(delay, TimeUnit.SECONDS)
                        .map(integers -> integers.isEmpty() ? 0 : integers.get(integers.size() - 1))
                        .doOnNext(integer -> Log.v(TAG, "DetectorListener. Value: " + integer)),
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
