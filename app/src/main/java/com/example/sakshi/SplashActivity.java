package com.example.sakshi;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    //private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the next activity
                Intent mainIntent = new Intent(SplashActivity.this, DashBoardActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 500);
    }
}