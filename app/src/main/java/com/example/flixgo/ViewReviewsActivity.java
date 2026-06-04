package com.example.flixgo;

import android.os.Bundle;
import android.widget.ImageButton; // Add this
import androidx.appcompat.app.AppCompatActivity;

public class ViewReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        // Find the back button from your XML
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Handle the click to return to Home
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}