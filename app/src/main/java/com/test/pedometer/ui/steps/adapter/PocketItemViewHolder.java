package com.test.pedometer.ui.steps.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.pedometer.R;
import com.test.pedometer.common.list.ListAdapter;
import com.test.pedometer.common.list.ListViewHolder;
import com.test.pedometer.ui.steps.model.PocketViewModel;

import butterknife.BindView;

public class PocketItemViewHolder extends ListViewHolder<PocketViewModel> {
    private final PocketItemDelegate.PocketSelectListener selectListener;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.pocket_container)
    View container;

    public PocketItemViewHolder(@NonNull ViewGroup parent, int resourceId, @NonNull ListAdapter adapter, PocketItemDelegate.PocketSelectListener selectListener) {
        super(parent, resourceId, adapter);
        this.selectListener = selectListener;
    }

    @Override
    public void bindData(@NonNull final PocketViewModel data) {
        title.setText(data.getTitle());
        title.setTextColor(data.isSelected()?Color.BLACK:Color.GRAY);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectListener.onPocketSelect(data.getTitle());
            }
        });
    }
}

