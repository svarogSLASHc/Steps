package com.test.pedometer.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        onViewInflated(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            setupActionBar(actionBar);
        }
    }

    protected abstract void setupActionBar(ActionBar actionBar);

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected abstract int getLayoutId();

    protected abstract void onViewInflated(View rootView);

}
