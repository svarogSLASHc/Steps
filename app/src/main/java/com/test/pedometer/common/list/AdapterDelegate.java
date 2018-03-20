package com.test.pedometer.common.list;

import android.support.annotation.NonNull;
import android.view.ViewGroup;


public interface AdapterDelegate<T extends ListItem> {
    ListViewHolder<T> onCreateViewholder(@NonNull ViewGroup parent, @NonNull ListAdapter adapter);

    void onBindViewHolder(@NonNull ListViewHolder<T> viewHolder, @NonNull T data);

    boolean isDelegateForDataType(@NonNull ListItem data);
}
