package com.test.pedometer.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(view);
        onViewInflated(view);
        return view;
    }

    protected abstract int getLayoutId();

    protected abstract void onViewInflated(View rootView);

}
