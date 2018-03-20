package com.test.pedometer.ui.steps.model;

import com.test.pedometer.common.list.ListItem;

public class PocketViewModel implements ListItem{

    private final String title;
    private final boolean selected;

    public PocketViewModel(String title, boolean selected) {
        this.title = title;
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }
}
