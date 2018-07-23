package com.test.pedometer.domain.whitelist;

import android.content.Context;

import com.test.pedometer.data.whitelist.PopupRepositoryImpl;

public class PopupController {

    private final PopupRepository repository;

    private PopupController(Context context){
        repository = PopupRepositoryImpl.getInstance(context);
    }

    public static PopupController newInstance(Context context) {
        return new PopupController(context);
    }

    public boolean showed(){
        return repository.showed();
    }

    public void setShowed(){
        repository.setShowed();
    }

    public boolean isSamsung(Context context){
        return repository.isSumsung(context);
    }
}
