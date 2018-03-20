package com.test.pedometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.test.pedometer.ui.steps.StepsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment();
    }

    private void loadFragment() {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_container, StepsFragment.getInstance())
                .commit();
    }
}
