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

    private static final String API_KEY = "ebe03d995dffa21748ee1c932f8c2eb6";
    private static final String TAG = "TRAILER_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        recyclerReviews    = findViewById(R.id.recyclerReviews);
        btnTrailer         = findViewById(R.id.btnTrailer);
        btnFavorite        = findViewById(R.id.btnFavorite);
        btnBack            = findViewById(R.id.btnBack);
        imgMoviePoster     = findViewById(R.id.imgMoviePoster);
        tvMovieTitle       = findViewById(R.id.tvMovieTitle);
        tvMovieReleaseDate = findViewById(R.id.tvMovieReleaseDate);
        tvMovieRating      = findViewById(R.id.tvMovieRating);

        firestore = FirebaseFirestore.getInstance();

        reviewList = new ArrayList<>();
        adapter    = new ReviewAdapter(reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnTrailer.setEnabled(false);
        btnTrailer.setText("Loading Trailer...");

        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            Log.d(TAG, "Movie received: " + movie.getTitle() + " | id=" + movie.getId());
            displayMovieDetails(movie);

            if (movie.getId() > 0) {
                fetchTrailer(movie.getId());
                fetchReviews(movie.getId());
            } else {
                // id is 0 — movie came from Firestore favorites without an id.
                // Search TMDB by title to get the real id, then fetch trailer.
                btnTrailer.setText("Searching Trailer...");
                searchMovieAndFetchTrailer(movie.getTitle());
            }
        }

        btnFavorite.setOnClickListener(v -> {
            Map<String, Object> movieData = new HashMap<>();
            movieData.put("title",       movie.getTitle());
            movieData.put("releaseDate", movie.getReleaseDate());
            movieData.put("rating",      movie.getRating());
            movieData.put("poster",      movie.getPoster());
            movieData.put("movieId",     movie.getId()); // save id so favorites also work

            firestore.collection("favorites")
                    .document(String.valueOf(movie.getId() > 0 ? movie.getId() : movie.getTitle()))
                    .set(movieData)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Saved to Favorites", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    private void displayMovieDetails(Movie movie) {
        tvMovieTitle.setText(movie.getTitle());
        tvMovieReleaseDate.setText(movie.getReleaseDate());
        tvMovieRating.setText(String.valueOf(movie.getRating()));

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPoster();
        Glide.with(this).load(posterUrl).into(imgMoviePoster);
    }

    /**
     * Called when movie.getId() == 0 (e.g. loaded from Firestore without id).
     * Searches TMDB by title, gets the real id, then fetches the trailer.
     */
    private void searchMovieAndFetchTrailer(String title) {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.searchMovies(title, API_KEY).enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResults() != null
                        && !response.body().getResults().isEmpty()) {

                    int tmdbId = response.body().getResults().get(0).getId();
                    Log.d(TAG, "Found TMDB id via search: " + tmdbId);
                    fetchTrailer(tmdbId);
                    fetchReviews(tmdbId);
                } else {
                    Log.d(TAG, "Search returned no results");
                    btnTrailer.setText("No Trailer Available");
                    btnTrailer.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                Log.d(TAG, "Search failed: " + t.getMessage());
                btnTrailer.setText("Trailer Unavailable");
            }
        });
    }

    private void fetchTrailer(int movieId) {
        Log.d(TAG, "Fetching trailer for movieId: " + movieId);
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.getVideos(movieId, API_KEY).enqueue(new Callback<VideoResponse>() {

            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().getResults() == null
                        || response.body().getResults().isEmpty()) {
                    Log.d(TAG, "Response body null or empty");
                    btnTrailer.setText("No Trailer Available");
                    btnTrailer.setEnabled(false);
                    return;
                }

                List<Video> videos = response.body().getResults();
                Log.d(TAG, "Total videos: " + videos.size());
                for (Video v : videos) {
                    Log.d(TAG, "Site=" + v.getSite()
                            + " | Type=" + v.getType()
                            + " | Official=" + v.isOfficial()
                            + " | Key=" + v.getKey());
                }

                Video trailer  = null;
                Video teaser   = null;
                Video anyVideo = null;

                for (Video v : videos) {
                    if (v.getSite() == null || !"YouTube".equalsIgnoreCase(v.getSite())) continue;
                    String type = v.getType() != null ? v.getType() : "";
                    switch (type) {
                        case "Trailer":
                            if (trailer == null || v.isOfficial()) trailer = v;
                            break;
                        case "Teaser":
                            if (teaser == null || v.isOfficial()) teaser = v;
                            break;
                        default:
                            if (anyVideo == null) anyVideo = v;
                            break;
                    }
                }

                Video selected = trailer != null ? trailer
                        : teaser   != null ? teaser
                          : anyVideo;

                Log.d(TAG, "Selected: " + (selected != null ? selected.getKey() : "null"));

                if (selected != null) {
                    final String youtubeKey = selected.getKey();
                    runOnUiThread(() -> {
                        btnTrailer.setText("Watch Trailer");
                        btnTrailer.setEnabled(true);
                        btnTrailer.setOnClickListener(v2 -> startActivity(
                                new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey))
                        ));
                    });
                } else {
                    runOnUiThread(() -> {
                        btnTrailer.setText("No Trailer Available");
                        btnTrailer.setEnabled(false);
                    });
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.d(TAG, "FAILED: " + t.getMessage());
                runOnUiThread(() -> btnTrailer.setText("Trailer Unavailable"));
                Toast.makeText(MovieDetailsActivity.this,
                        "Failed to load trailer: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(int movieId) {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.getReviews(movieId, API_KEY).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResults() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this,
                        "Failed to load reviews: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}