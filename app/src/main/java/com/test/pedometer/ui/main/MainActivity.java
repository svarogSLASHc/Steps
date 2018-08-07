package com.test.pedometer.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.raizlabs.jonathan_cole.imprivatatestbed.ui.DetectorActivity;
import com.test.pedometer.R;
import com.test.pedometer.ui.setting.SettingsFragment;
import com.test.pedometer.ui.setting.SettingsView;
import com.test.pedometer.ui.steps.StepsFragment;
import com.test.pedometer.ui.steps.StepsView;

public class MainActivity extends AppCompatActivity implements MainActivityView, SettingsView.SaveListener, StepsView.StartListener {
    private boolean settings_enabled = true;
    private MainActivityPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainActivityPresenter(this);
        loadFirstFragment();
        presenter.firstTimeAndShow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
       if (presenter.isSumsung()){
           menu.addSubMenu(R.string.samsung_labeel);
       }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (settings_enabled){
                    loadFragment(SettingsFragment.getInstance());
                }
                return true;
            case android.R.id.home:
                backToMainScreen();
                return true;
            case R.id.detector:
                startActivity(new Intent(this, DetectorActivity.class));
                return true;
        }

        if (item.getTitle().equals(getString(R.string.samsung_labeel))){
            navigateToUnmonitored();
        }

        return super.onOptionsItemSelected(item);
    }

    private void backToMainScreen() {
        getSupportFragmentManager().popBackStack();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(fragment.getClass().getName()) == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, fragment, fragment.getClass().getName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void loadFirstFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, StepsFragment.getInstance())
                .commit();
    }

    @Override
    public void onSaveClicked() {
        backToMainScreen();
    }

    @Override
    public void onTestStart() {
        settings_enabled = false;
    }

    @Override
    public void onTestStop() {
        settings_enabled = true;
    }

    @Override
    public void showSamsungPopup() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.unmonitored_title)
                .setMessage(R.string.unmonitored_msg)
                .setPositiveButton("Add", (dialog, which) ->  navigateToUnmonitored())
                .show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void navigateToUnmonitored(){
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
