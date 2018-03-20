package com.test.pedometer.common.list;

import android.support.annotation.NonNull;

public abstract class ListAdapterDelegate<T extends ListItem> implements AdapterDelegate<T> {
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder<T> viewHolder, @NonNull T data) { viewHolder.bindData(data); }
}
