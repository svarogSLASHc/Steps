package com.test.pedometer.common.list;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public final class DelegateManager<T extends ListItem> {

    private ListAdapter adapter;
    private List<AdapterDelegate> delegates = new ArrayList<>();

    public DelegateManager(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public DelegateManager addDelegates(AdapterDelegate delegate) {
        delegates.add(delegate);
        return this;
    }

    public DelegateManager addDelegates(int position, AdapterDelegate delegate) {
        delegates.add(position, delegate);
        return this;
    }

    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(delegates.get(viewType) == null){
            throw new NullPointerException("There is now delegate registered vor viewType "+viewType);
        }
        return delegates.get(viewType).onCreateViewholder(parent, adapter);
    }

    public void onBindViewHolder(ListViewHolder<T> viewHolder, T data){
        int viewType = viewHolder.getItemViewType();
        delegates.get(viewType).onBindViewHolder(viewHolder, data);
    }

    public int getViewType(ListItem data){
        for(AdapterDelegate delegate : delegates){
            if(delegate.isDelegateForDataType(data)){
                return delegates.indexOf(delegate);
            }
        }
        throw new IllegalArgumentException("No AdapterDelegate found for data: " + data);
    }
}
