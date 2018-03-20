package com.test.pedometer.common.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ListAdapter<T extends ListItem> extends RecyclerView.Adapter<ListViewHolder<T>> {

    private final static String TAG = ListAdapter.class.getCanonicalName();

    private List<T> items;
    private DelegateManager<T> delegateManager;

    public ListAdapter() {
        this.delegateManager = new DelegateManager<>(this);
        this.items = new ArrayList<>();
    }

    public DelegateManager<T> getDelegateManager() {
        return delegateManager;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        items = data;
        notifyDataSetChanged();
    }

    public void addItems(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        items.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addItemAt(T item, int position) {
        if (position < 0 || position >= items.size()) {
            Log.d(TAG, "position was " + position + " but array length was only " + items.size());
            throw new IllegalArgumentException("Position was " + position + " but array length was only " + items.size());
        }

        items.add(position, item);
        notifyItemInserted(position);
    }

    public T getItemAt(int position) {
        if (position < items.size() && position >= 0) {
            return items.get(position);
        } else {
            throw new IllegalArgumentException("Item position should be from 0 to items size");
        }
    }

    public void removeItem(T item) {
        int position = items.indexOf(item);
        if (position >= 0 && position < items.size()) {
            items.remove(item);
            notifyItemRemoved(position);
        }
    }

    /**
     * removeItemFrom - romoves item from adapter at specific position
     *
     * @param position
     */
    public void removeItemAt(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        } else {
            throw new IllegalArgumentException("Item position should be from 0 to items size");
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(ListViewHolder<T> holder, int position) {
        delegateManager.onBindViewHolder(holder, items.get(position));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegateManager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return delegateManager.getViewType(items.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(ListViewHolder<T> holder) {
        super.onViewDetachedFromWindow(holder);
        // remove any animation
        holder.itemView.clearAnimation();
    }
}
