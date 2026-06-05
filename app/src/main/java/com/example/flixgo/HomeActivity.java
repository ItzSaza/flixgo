package com.example.flixgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    Button btnWatchTrailer, btnViewReviews;
    ImageButton btnHomeSearch, btnProfile;
    ImageView imgFeatured;
    RecyclerView recyclerHome, recyclerTopRated;
    MovieAdapter adapterHome, adapterTopRated;
    List<Movie> homeMovies, topRatedMovies;

    private final String API_KEY = "ebe03d995dffa21748ee1c932f8c2eb6";

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

            // Initialize Lists
            homeMovies = new ArrayList<>();
            topRatedMovies = new ArrayList<>();

            // Setup Adapters and assign them to the Recyclerivews right away
            adapterHome = new MovieAdapter(homeMovies);
            adapterTopRated = new MovieAdapter(topRatedMovies);

            setupRecycler(recyclerHome, adapterHome);
            setupRecycler(recyclerTopRated, adapterTopRated);

            // Fetch dynamic data via API endpoints instead of hardcoding
            fetchHomeMovies();
            fetchTopRatedMovies();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecycler(RecyclerView recycler, MovieAdapter adapter) {
        recycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        recycler.setAdapter(adapter);
    }

    private void fetchHomeMovies() {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.getNowPlayingMovies(API_KEY).enqueue(new Callback <VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    homeMovies.clear();
                    homeMovies.addAll(response.body().getResults());
                    adapterHome.notifyDataSetChanged();

                    // Dynamically set the first movie from the API response as the featured banner image
                    if (!homeMovies.isEmpty()) {
                        Movie featuredMovie = homeMovies.get(0);
                        String bannerUrl = "https://image.tmdb.org/t/p/w780" + featuredMovie.getPoster();

                        Glide.with(HomeActivity.this)
                                .load(bannerUrl)
                                .placeholder(R.drawable.applogo)
                                .error(R.drawable.applogo)
                                .into(imgFeatured);

                        imgFeatured.setOnClickListener(v -> {
                            Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
                            intent.putExtra("movie", featuredMovie);
                            startActivity(intent);
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("HomeActivity", "Failed fetching home movies: " + t.getMessage());
            }
        });
    }

    private void fetchTopRatedMovies() {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.getTopRatedMovies(API_KEY).enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    topRatedMovies.clear();
                    topRatedMovies.addAll(response.body().getResults());
                    adapterTopRated.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("HomeActivity", "Failed fetching top rated movies: " + t.getMessage());
            }
        });
    }
}