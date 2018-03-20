package com.test.pedometer.ui.steps;

import com.test.pedometer.R;
import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.data.PedomenetController;
import com.test.pedometer.ui.service.FStepService;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import java.util.ArrayList;
import java.util.List;

public class StepsPresenter extends BasePresenter<StepsView> {
    private String pocket;
    private PedomenetController pedomenetController;
    private final FStepService.StepCountListener pedometerListener = new FStepService.StepCountListener() {
        @Override
        public void onStepDataUpdate(int stepCount) {
            view.setStepsCounted(stepCount);
        }

        @Override
        public void onStep(int count) {
            view.setStepsDetected(count);
        }
    };

    protected StepsPresenter(StepsView view) {
        super(view);
        pedomenetController = PedomenetController.getInstance(view.getContext().getApplicationContext());
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

    }

    public void deleteClick() {
        pedomenetController.unregisterListener(pedometerListener);
    }

    public void startClick() {
        pedomenetController.registerListener(pedometerListener);
    }


}
