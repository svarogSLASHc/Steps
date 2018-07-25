package com.test.pedometer.domain.pedometer;

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

    public void registerCounterListener(StepCountListener listener) {
        pedometerRepository.registerStepListener(listener);
    }

    public void reset() {
        pedometerRepository.reset();
    }

    public void onDestroy() {
        pedometerRepository.onDestroy();
    }
}
