package com.test.pedometer.ui.steps;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.test.pedometer.R;
import com.test.pedometer.common.BaseFragment;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.ui.service.FStepService;
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
        getActivity().startService(new Intent(getContext(), FStepService.class));
    }

    @OnClick(R.id.send)
    void sendClick() {
        presenter.sendClick();
    }

    @OnClick(R.id.delete)
    void deleteClick() {
        presenter.deleteClick();
        getActivity().stopService(new Intent(getContext(), FStepService.class));
        clearUI();
    }

    private PocketAdapter pocketAdapter;
    private StepsPresenter presenter;

    public static StepsFragment getInstance() {
        return new StepsFragment();
    }

    @Override
    protected void setupActionBar(android.support.v7.app.ActionBar actionBar) {
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(R.string.app_name);
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
        clearUI();
        disableDelete();
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
    public void setTotalRounds(int rounds) {
        totalRounds.setText(String.valueOf(rounds));
    }

    @Override
    public void setCurrentRound(int round) {
        currentRound.setText(String.valueOf(round));
    }

    @Override
    public void showError(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void disableDelete() {
        switchDeleteState(false);
    }

    @Override
    public void enableDelete() {
        switchDeleteState(true);
    }

    @Override
    public void disableStart() {
        switchStartState(false);
    }

    @Override
    public void enableStart() {
        switchStartState(true);
    }

    @Override
    public void onPocketSelect(String title) {
        presenter.pocketSelected(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribeOnSteps();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribeFromSteps();
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(false);
        pockets.setLayoutManager(layoutManager);
        pockets.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        pocketAdapter = new PocketAdapter(this);
        pockets.setAdapter(pocketAdapter);
    }

    private void clearUI() {
        currentRound.setText("0");
    }

    private void switchDeleteState(boolean enabled) {
        delete.setEnabled(enabled);
    }

    private void switchStartState(boolean enabled) {
        start.setEnabled(enabled);
    }
}
