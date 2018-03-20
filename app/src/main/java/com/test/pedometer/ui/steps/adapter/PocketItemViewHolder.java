package com.test.pedometer.ui.steps.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.test.pedometer.common.list.ListAdapter;
import com.test.pedometer.common.list.ListViewHolder;
import com.test.pedometer.ui.steps.model.PocketViewModel;

public class PocketItemViewHolder extends ListViewHolder<PocketViewModel> {

    public PocketItemViewHolder(@NonNull ViewGroup parent, int resourceId, @NonNull ListAdapter adapter) {
        super(parent, resourceId, adapter);
    }

    @Override
    public void bindData(@NonNull PocketViewModel data) {

    }
}

