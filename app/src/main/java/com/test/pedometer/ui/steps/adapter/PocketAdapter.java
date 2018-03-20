package com.test.pedometer.ui.steps.adapter;

import com.test.pedometer.common.list.ListAdapter;
import com.test.pedometer.common.list.ListItem;

public class PocketAdapter extends ListAdapter<ListItem> {

    public PocketAdapter(PocketItemDelegate.PocketSelectListener selectListener) {
        getDelegateManager()
                .addDelegates(new PocketItemDelegate(selectListener));
    }
}
