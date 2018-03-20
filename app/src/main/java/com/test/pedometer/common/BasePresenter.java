package com.test.pedometer.common;

public abstract class BasePresenter<T extends BaseView> {
    private final T view;

    protected BasePresenter(T view) {
        this.view = view;
    }

    private void onViewCreated(){}
}
