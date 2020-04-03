package com.higer.lowermachinelibrary.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.higer.lowermachinelibrary.R;

public class FrontRadarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_radar);
        getSupportActionBar().hide();
    }
}
