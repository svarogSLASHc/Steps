package com.test.pedometer.ui.steps;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.test.pedometer.R;
import com.test.pedometer.common.BaseFragment;

import butterknife.BindView;

public class StepsFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    protected RecyclerView pockets;
    @BindView(R.id.steps)
    protected TextView stepsCounter;
    @BindView(R.id.total_rounds)
    protected TextView totalRounds;
    @BindView(R.id.current_round)
    protected TextView currentRound;
    @BindView(R.id.start)
    protected View start;
    @BindView(R.id.send)
    protected View send;
    @BindView(R.id.delete)
    protected View delete;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_steps;
    }

    @Override
    protected void onViewInflated(View rootView) {

    }
}
