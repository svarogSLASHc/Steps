package com.test.pedometer.ui.setting;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.test.pedometer.R;
import com.test.pedometer.common.BaseFragment;

import java.util.Arrays;

import butterknife.BindView;

public class SettingsFragment extends BaseFragment implements SettingsView {
    private static final String[] settings_round_duration_values
            = new String[]{"5","8","10","12","15","20"};
    private static final String[] settings_number_of_rounds_values
            = new String[]{"1","2","3","4","5"};
    private static final String[] settings_number_of_steps_values
            = new String[]{"5","6","7","8","9"};
    @BindView(R.id.steps_to_make)
    Spinner stepsToTake;
    @BindView(R.id.rounds_to_make)
    Spinner roundsToTake;
    @BindView(R.id.round_time)
    Spinner roundTime;
    @BindView(R.id.save)
    View save;
    private SettingsView.SaveListener saveListener;
    private SettingsPresenter presenter;


    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsView.SaveListener) {
            saveListener = (SettingsView.SaveListener) context;
        }
    }

    @Override
    protected void setupActionBar(ActionBar actionBar) {
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.settings);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }


    @Override
    protected void onViewInflated(View rootView) {
        presenter = new SettingsPresenter(this);
        stepsToTake.setAdapter(getAdapter(getStepsValues()));
        roundsToTake.setAdapter(getAdapter(getRoundsValues()));
        roundTime.setAdapter(getAdapter(getRoundTimeValues()));

        save.setOnClickListener(v -> {
            final String steps = stepsToTake.getSelectedItem().toString();
            final String rounds = roundsToTake.getSelectedItem().toString();
            final String time = roundTime.getSelectedItem().toString();
            presenter.setSteps(Integer.parseInt(steps));
            presenter.setRounds(Integer.parseInt(rounds));
            presenter.setRoundTime(Integer.parseInt(time));

            showSuccess();
        });

        presenter.onViewCreated();
    }

    @Override
    public void setSteps(int steps) {
        stepsToTake.setSelection(getIndex(getStepsValues(), steps));
    }

    @Override
    public void setRounds(int rounds) {
        roundsToTake.setSelection(getIndex(getRoundsValues(), rounds));
    }

    @Override
    public void setRoundTime(int time) {
        roundTime.setSelection(getIndex(getRoundTimeValues(), time));
    }

    @Override
    public void showSuccess() {
        Toast.makeText(getContext(), "Settings were saved", Toast.LENGTH_SHORT).show();
        if (saveListener != null) {
            saveListener.onSaveClicked();
        }
    }

    private String[] getStepsValues() {
        return settings_number_of_steps_values;
    }

    private String[] getRoundsValues() {
        return settings_number_of_rounds_values;
    }

    private String[] getRoundTimeValues() {
        return settings_round_duration_values;
    }

    private int getIndex(String[] array, int element) {
        return getIndex(array, String.valueOf(element));
    }

    private int getIndex(String[] array, String element) {
        return Arrays.asList(array).indexOf(element);
    }

    private ArrayAdapter<String> getAdapter(String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
