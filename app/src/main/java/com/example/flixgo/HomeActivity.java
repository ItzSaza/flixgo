package com.example.flixgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    Button btnWatchTrailer, btnViewReviews;
    ImageButton btnHomeSearch, btnProfile;
    ImageView imgFeatured;
    RecyclerView recyclerHome, recyclerTopRated;
    MovieAdapter adapterHome, adapterTopRated;
    List<Movie> homeMovies, topRatedMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {

            btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
            btnViewReviews = findViewById(R.id.btnViewReviews);
            btnHomeSearch = findViewById(R.id.btnHomeSearch);
            btnProfile = findViewById(R.id.btnProfile);
            imgFeatured = findViewById(R.id.imgFeatured);

            recyclerHome = findViewById(R.id.recyclerHome);
            recyclerTopRated = findViewById(R.id.recyclerTopRated);

            // NAVIGATION SAFE
            btnWatchTrailer.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, WatchTrailerActivity.class))
            );

            btnViewReviews.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, ViewReviewsActivity.class))
            );

            btnHomeSearch.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, MainActivity.class))
            );

            btnProfile.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class))
            );

            // Load Featured Movie Image
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780/74xTEgt7R36Fpooo50r9T25onhq.jpg")
                    .placeholder(R.drawable.applogo)
                    .error(R.drawable.applogo)
                    .into(imgFeatured);

            imgFeatured.setOnClickListener(v -> {
                Movie featuredMovie = new Movie("The Batman", "2022-03-01", 7.7, "/74xTEgt7R36Fpooo50r9T25onhq.jpg");
                Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie", featuredMovie);
                startActivity(intent);
            });

            // DATA
            homeMovies = new ArrayList<>();
            homeMovies.add(new Movie("The Batman", "2022-03-01", 7.7, "/74xTEgt7R36Fpooo50r9T25onhq.jpg"));
            homeMovies.add(new Movie("Joker", "2019-10-04", 8.2, "/rzdPqYx7Um4FUZeD8wpXqjAUcEm.jpg"));
            homeMovies.add(new Movie("Inception", "2010-07-16", 8.4, "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg"));
            homeMovies.add(new Movie("Interstellar", "2014-11-07", 8.4, "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"));

            topRatedMovies = new ArrayList<>();
            topRatedMovies.add(new Movie("The Dark Knight", "2008-07-18", 8.5, "/qJ2tW6WMUDux911r6m7haRef0WH.jpg"));
            topRatedMovies.add(new Movie("Fight Club", "1999-10-15", 8.4, "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg"));
            topRatedMovies.add(new Movie("Avengers: Endgame", "2019-04-26", 8.3, "/or06FN3Dka5tukK1e9sl16pB3iy.jpg"));

            setupRecycler(recyclerHome, homeMovies);
            setupRecycler(recyclerTopRated, topRatedMovies);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecycler(RecyclerView recycler, List<Movie> data) {
        recycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recycler.setAdapter(new MovieAdapter(data));
    }
}
