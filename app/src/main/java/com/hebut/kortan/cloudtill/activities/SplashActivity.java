package com.hebut.kortan.cloudtill.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hebut.kortan.cloudtill.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    public void goToMain(View view) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToWifi(View view) {
        Intent intent = new Intent(SplashActivity.this, SensorActivity.class);
        startActivity(intent);
        finish();
    }
}
