package com.test.pedometer.ui.steps.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.test.pedometer.common.list.ListAdapter;
import com.test.pedometer.common.list.ListAdapterDelegate;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.common.list.ListViewHolder;
import com.test.pedometer.ui.steps.model.PocketViewModel;

public class PocketItemDelegate extends ListAdapterDelegate<PocketViewModel>{
    @Override
    public ListViewHolder<PocketViewModel> onCreateViewholder(@NonNull ViewGroup parent, @NonNull ListAdapter adapter) {
        return null;
    }

    @Override
    public boolean isDelegateForDataType(@NonNull ListItem data) {
        return data instanceof PocketViewModel;
    }
}
