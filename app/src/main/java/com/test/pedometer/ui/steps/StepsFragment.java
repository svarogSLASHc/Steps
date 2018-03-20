package com.test.pedometer.ui.steps;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.test.pedometer.R;
import com.test.pedometer.common.BaseFragment;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.ui.steps.adapter.PocketAdapter;
import com.test.pedometer.ui.steps.adapter.PocketItemDelegate;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class StepsFragment extends BaseFragment implements StepsView, PocketItemDelegate.PocketSelectListener {
    @BindView(R.id.recyclerView)
    RecyclerView pockets;
    @BindView(R.id.steps)
    TextView stepsCounter;
    @BindView(R.id.step_detector)
    TextView stepsDetector;
    @BindView(R.id.total_rounds)
    TextView totalRounds;
    @BindView(R.id.current_round)
    TextView currentRound;
    @BindView(R.id.start)
    View start;
    @BindView(R.id.send)
    View send;
    @BindView(R.id.delete)
    View delete;

    @OnClick(R.id.start)
    void startClick() {
        presenter.startClick();
    }

    @OnClick(R.id.send)
    void sendClick() {
        presenter.sendClick();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        presenter.deleteClick();
    }

    private PocketAdapter pocketAdapter;
    private StepsPresenter presenter;

    public static StepsFragment getInstance() {
        return new StepsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_steps;
    }

    @Override
    protected void onViewInflated(View rootView) {
        initList();
        presenter = new StepsPresenter(this);
        presenter.onViewCreated();

    }

    @Override
    public void setPocketList(List<ListItem> items) {
        pocketAdapter.setItems(items);
    }

    @Override
    public void setStepsCounted(int steps) {
        stepsCounter.setText(String.valueOf(steps));
    }

    @Override
    public void setStepsDetected(int steps) {
        stepsDetector.setText(String.valueOf(steps));
    }

    @Override
    public void setTotalRounds(int rounds) {
        totalRounds.setText(String.valueOf(rounds));
    }

    @Override
    public void setCurrentRound(int round) {
        currentRound.setText(String.valueOf(round));
    }

    @Override
    public void showError(int errorMsg) {

    }

    @Override
    public void onPocketSelect(String title) {
        presenter.pocketSelected(title);
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(false);
        pockets.setLayoutManager(layoutManager);
        pockets.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        pocketAdapter = new PocketAdapter(this);
        pockets.setAdapter(pocketAdapter);
    }

}
