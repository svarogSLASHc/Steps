package com.test.pedometer.ui.steps;

import com.test.pedometer.R;
import com.test.pedometer.common.BasePresenter;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import java.util.ArrayList;
import java.util.List;

public class StepsPresenter extends BasePresenter<StepsView>{
    private String pocket;

    protected StepsPresenter(StepsView view) {
        super(view);
    }

    @Override
    public void onViewCreated() {
        setPockets(null);
    }

    private void setPockets(String current) {
        final String[] pockets =  view.getContext().getResources().getStringArray(R.array.pockets_list);
        if (null == current){
            current = pockets[0];
        }
        pocket = current;

        List<ListItem> pocketViewModels = new ArrayList<>(pockets.length);
        for (String item:pockets){
            pocketViewModels.add(new PocketViewModel(item, item.equals(current)));
        }
        view.setPocketList(pocketViewModels);
    }

    public void pocketSelected(String title){
        setPockets(title);
    }

    public void sendClick() {

    }

    public void deleteClick() {

    }

    public void startClick() {

    }
}
