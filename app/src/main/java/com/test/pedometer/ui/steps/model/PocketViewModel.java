package com.test.pedometer.ui.steps.model;

import com.test.pedometer.common.list.ListItem;

public class PocketViewModel implements ListItem{

    private final String title;

    public PocketViewModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
