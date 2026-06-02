package com.example.flixgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoView);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.splash_sc;
        videoView.setVideoURI(Uri.parse(path));

        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
