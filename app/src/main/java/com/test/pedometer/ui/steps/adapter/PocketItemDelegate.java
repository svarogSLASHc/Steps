package com.test.pedometer.ui.steps.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.test.pedometer.R;
import com.test.pedometer.common.list.ListAdapter;
import com.test.pedometer.common.list.ListAdapterDelegate;
import com.test.pedometer.common.list.ListItem;
import com.test.pedometer.common.list.ListViewHolder;
import com.test.pedometer.ui.steps.model.PocketViewModel;

public class PocketItemDelegate extends ListAdapterDelegate<PocketViewModel>{
    private final PocketSelectListener selectListener;

    public PocketItemDelegate(PocketSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public ListViewHolder<PocketViewModel> onCreateViewholder(@NonNull ViewGroup parent, @NonNull ListAdapter adapter) {
        return new PocketItemViewHolder(parent, R.layout.list_item_pocket, adapter, selectListener);
    }

    @Override
    public boolean isDelegateForDataType(@NonNull ListItem data) {
        return data instanceof PocketViewModel;
    }

    public interface PocketSelectListener{
      void onPocketSelect(String title);
    }
}
