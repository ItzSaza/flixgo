package com.example.flixgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerReviews;
    List<Review> reviewList;
    ReviewAdapter adapter;
    Button btnTrailer, btnFavorite;
    ImageButton btnBack;
    ImageView imgMoviePoster;
    TextView tvMovieTitle, tvMovieReleaseDate, tvMovieRating;
    Movie movie;
    FirebaseFirestore firestore;

    // Tracks the dynamic video key from TMDB
    private String trailerKey = null;
    private final String API_KEY = "ebe03d995dffa21748ee1c932f8c2eb6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Initialize Views
        recyclerReviews = findViewById(R.id.recyclerReviews);
        btnTrailer = findViewById(R.id.btnTrailer);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBack = findViewById(R.id.btnBack);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieReleaseDate = findViewById(R.id.tvMovieReleaseDate);
        tvMovieRating = findViewById(R.id.tvMovieRating);

        // UX Improvement: Disable trailer button until data finishes loading over the network
        btnTrailer.setEnabled(false);
        btnTrailer.setText("Loading Trailer...");


        firestore = FirebaseFirestore.getInstance();

        // Get Movie Data from Intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            displayMovieDetails(movie);

            // Debug Log: Check if the movie ID is arriving correctly or passing as 0
            Log.d("MovieDetailsActivity", "Loading Movie ID: " + movie.getId());

            fetchReviews(movie.getId());
            fetchMovieTrailer(movie.getId());
        } else {
            Toast.makeText(this, "Movie data error", Toast.LENGTH_SHORT).show();
            btnTrailer.setText("No Trailer");
        }

        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnTrailer.setOnClickListener(v -> {
            if (trailerKey != null && !trailerKey.isEmpty()) {
                String trailerUrl = "https://www.youtube.com/watch?v=" + trailerKey;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                startActivity(intent);
            }
        });

        btnFavorite.setOnClickListener(v -> {
            if (movie == null) return;

            Map<String, Object> movieData = new HashMap<>();
            movieData.put("title", movie.getTitle());
            movieData.put("releaseDate", movie.getReleaseDate());
            movieData.put("rating", movie.getRating());
            movieData.put("poster", movie.getPoster());

            firestore.collection("favorites")
                    .document(movie.getTitle())
                    .set(movieData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Saved to Favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void displayMovieDetails(Movie movie) {
        tvMovieTitle.setText(movie.getTitle());
        tvMovieReleaseDate.setText(movie.getReleaseDate());
        tvMovieRating.setText(String.valueOf(movie.getRating()));

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPoster();
        Glide.with(this).load(posterUrl).into(imgMoviePoster);
    }

    private void fetchReviews(int movieId) {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        Call<ReviewResponse> call = api.getReviews(movieId, API_KEY);

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Reviews fetch failure: " + t.getMessage());
            }
        });
    }

    private void fetchMovieTrailer(int movieId) {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        Call<VideoResponse> call = api.getVideos(movieId, API_KEY);

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {

                    // Step 1: Scan for an official YouTube Trailer
                    for (Video video : response.body().getResults()) {
                        if ("YouTube".equalsIgnoreCase(video.getSite()) && "Trailer".equalsIgnoreCase(video.getType())) {
                            trailerKey = video.getKey();
                            break;
                        }
                    }

                    // Step 2: Fallback to any generic YouTube segment if "Trailer" label is missing
                    if (trailerKey == null) {
                        for (Video video : response.body().getResults()) {
                            if ("YouTube".equalsIgnoreCase(video.getSite())) {
                                trailerKey = video.getKey();
                                break;
                            }
                        }
                    }
                }

                // UI Processing: Activate or flag button based on search results
                runOnUiThread(() -> {
                    if (trailerKey != null && !trailerKey.isEmpty()) {
                        btnTrailer.setEnabled(true);
                        btnTrailer.setText("Watch Trailer");
                    } else {
                        btnTrailer.setEnabled(false);
                        btnTrailer.setText("No Trailer Available");
                    }
                });
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Video API call error: " + t.getMessage());
                runOnUiThread(() -> {
                    btnTrailer.setEnabled(false);
                    btnTrailer.setText("Error Loading Trailer");
                });
            }
        });
    }
}