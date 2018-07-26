package com.test.pedometer.domain.pedometer;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class PedometerRunnerController {
    private static PedometerRunnerController INSTANCE;
    private final PedometerRepository pedometerRepository;

    private PedometerRunnerController(PedometerRepository repository) {
        this.pedometerRepository = repository;
    }

    public static PedometerRunnerController getInstance(PedometerRepository repository) {
        if (INSTANCE == null) {
            INSTANCE = new PedometerRunnerController(repository);
        }
        return INSTANCE;
    }

    public Observable<Integer> getStepsObservable(Integer delay) {
        return Observable.<Integer>create(subscriber ->
                registerCounterListener(subscriber::onNext))
                .buffer(delay, TimeUnit.SECONDS)
                .take(1)
                .flatMap(Observable::from)
                .reduce(0, (integer, integer2) -> integer + integer2);
    }

    private void registerCounterListener(StepCountListener listener) {
        reset();
        pedometerRepository.registerStepListener(listener);
    }

    public void reset() {
        pedometerRepository.reset();
    }

    public void onDestroy() {
        pedometerRepository.onDestroy();
    }
}
