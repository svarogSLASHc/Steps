package com.test.pedometer.common.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;


public abstract class ListViewHolder<T extends ListItem> extends RecyclerView.ViewHolder {

    private ListAdapter adapter;

    public ListViewHolder(@NonNull ViewGroup parent, int resourceId, @NonNull ListAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false));
        this.adapter = adapter;
        ButterKnife.bind(this, itemView);
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    public abstract void bindData(@NonNull T data);

}
