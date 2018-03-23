package com.test.pedometer.ui.steps;

import android.support.annotation.StringRes;

import com.test.pedometer.common.BaseView;
import com.test.pedometer.common.list.ListItem;

import java.util.List;

public interface StepsView extends BaseView {
    void setPocketList(List<ListItem> items);

    void setStepsCounted(int steps);

    void setTotalRounds(int rounds);

    void setCurrentRound(int round);

    void showError(String errorMsg);

    void disableDelete();

    void enableDelete();

    void disableStart();

    void enableStart();
}
