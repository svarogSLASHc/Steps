package com.test.pedometer.ui.steps;

import android.util.Log;

import com.test.pedometer.R;
import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.data.fileaccess.FileLoggerController;
import com.test.pedometer.data.sensors.PedometerController;
import com.test.pedometer.domain.StepCountListener;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class StepsPresenter extends BasePresenter<StepsView> {
    private static final String SPACE = ". ";
    private String pocket;
    private PedometerController pedometerController;
    private FileLoggerController fileLog;
    private int stepCounter;
    private int stepDetector;
    private final StepCountListener pedometerListener = new StepCountListener() {
        @Override
        public void onStepDataUpdate(int stepCount) {
            view.setStepsCounted(stepCount);
            stepCounter = stepCount;
        }

        @Override
        public void onStep(int count) {
            view.setStepsDetected(count);
            stepDetector = count;
        }
    };

    protected StepsPresenter(StepsView view) {
        super(view);
        pedometerController = PedometerController.getInstance(view.getContext().getApplicationContext());
        fileLog = FileLoggerController.newInstance(view.getContext());
    }

    @Override
    public void onViewCreated() {
        setPockets(null);
    }

    private void setPockets(String current) {
        final String[] pockets = view.getContext().getResources().getStringArray(R.array.pockets_list);
        if (null == current) {
            current = pockets[0];
        }
        pocket = current;

        List<ListItem> pocketViewModels = new ArrayList<>(pockets.length);
        for (String item : pockets) {
            pocketViewModels.add(new PocketViewModel(item, item.equals(current)));
        }
        view.setPocketList(pocketViewModels);
    }

    public void pocketSelected(String title) {
        setPockets(title);
    }

    public void sendClick() {
        try {
            Log.d("test_log", fileLog.getLogFileAsString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deleteClick() {
        unsubscribeFromSteps();

        fileLog.logRedometerData(getStepResultString());

        view.enableStart();
        view.disableDelete();
    }

    public void startClick() {
        stepCounter = 0;
        stepDetector = 0;
        subscribeOnSteps();
        view.disableStart();
        view.enableDelete();
    }

    public void subscribeOnSteps() {
        pedometerController.registerListener(pedometerListener);
    }

    public void unsubscribeFromSteps() {
        pedometerController.unregisterListener(pedometerListener);
    }

    private String getStepResultString() {
        return new StringBuilder()
                .append(pocket)
                .append(SPACE)
                .append(view.getContext().getString(R.string.step_counter_result, stepCounter, stepDetector))
                .toString();
    }
}
