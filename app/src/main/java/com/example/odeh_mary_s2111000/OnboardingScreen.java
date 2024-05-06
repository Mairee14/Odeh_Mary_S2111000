package com.example.odeh_mary_s2111000;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);

        Button getStartedButton = findViewById(R.id.button);
        getStartedButton.setOnClickListener(v -> {
            startActivity(new Intent(OnboardingScreen.this, AboutActivity.class));
            finish();
        });

        Button skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> {
            startActivity(new Intent(OnboardingScreen.this, SkipActivity1.class));
            finish();
        });
    }
}