package com.example.flixgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton btnSearch;
    ImageView btnProfile;
    RecyclerView recyclerView;
    MovieAdapter adapter;
    ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSearch = findViewById(R.id.btnSearch);
        btnProfile = findViewById(R.id.btnProfile);
        recyclerView = findViewById(R.id.recyclerHome);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        movieList = new ArrayList<>();

        // SAMPLE MOVIES (rating is double ✔)
        movieList.add(new Movie("Batman", "2022", 8.5, "poster_url"));
        movieList.add(new Movie("Avengers", "2019", 9.0, "poster_url"));

        // IMPORTANT: use correct constructor (1 argument)
        adapter = new MovieAdapter(movieList);

        recyclerView.setAdapter(adapter);

        // 🔍 Search button
        btnSearch.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // 👤 Profile button
        btnProfile.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}