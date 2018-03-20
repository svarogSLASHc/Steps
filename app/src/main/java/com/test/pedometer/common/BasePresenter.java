package com.test.pedometer.common;

public abstract class BasePresenter<T extends BaseView> {
    protected final T view;

    protected BasePresenter(T view) {
        this.view = view;
    }

    public void onViewCreated(){}
}
