package com.test.pedometer.domain.whitelist;

import android.content.Context;

public interface PopupRepository {
    String SHARED_NAME = "PopupRepository";
    String FIELD_SHOWED = "SHOWED";
    boolean DEFAULT_FIELD_SHOWED = false;

    boolean showed();

    boolean isSumsung(Context context);

    void setShowed();
}
