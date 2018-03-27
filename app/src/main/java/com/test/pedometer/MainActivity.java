package com.test.pedometer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.test.pedometer.ui.settings.SettingsFragment;
import com.test.pedometer.ui.settings.SettingsView;
import com.test.pedometer.ui.steps.StepsFragment;

public class MainActivity extends AppCompatActivity implements SettingsView.SaveListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFirstFragment();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                loadFragment(SettingsFragment.getInstance());
                return true;
            case android.R.id.home:
                backToMainScreen();
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }
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
}
