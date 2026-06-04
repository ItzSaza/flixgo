package com.example.flixgo;

import android.os.Bundle;
import android.widget.Button;      // Add this
import android.widget.ImageButton; // Add this
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WatchTrailerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_trailer);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnPlay = findViewById(R.id.btnPlay);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnPlay != null) {
            btnPlay.setOnClickListener(v ->
                    Toast.makeText(this, "Playing Trailer...", Toast.LENGTH_SHORT).show()
            );
        }
    }
}