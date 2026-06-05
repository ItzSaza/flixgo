package com.example.flixgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    MovieDatabaseHelper dbHelper;
    Movie movie;
    FirebaseFirestore firestore;

    private static final String API_KEY = "ebe03d995dffa21748ee1c932f8c2eb6";

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

        dbHelper = new MovieDatabaseHelper(this);
        firestore = FirebaseFirestore.getInstance();

        // Reviews RecyclerView setup
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(adapter);

        // Get Movie Data from Intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            displayMovieDetails(movie);
            fetchReviews(movie.getId());
            fetchTrailer(movie.getId());
        }

        btnBack.setOnClickListener(v -> finish());

        // Trailer button starts disabled until API responds
        btnTrailer.setEnabled(false);
        btnTrailer.setText("Loading Trailer...");

        btnFavorite.setOnClickListener(v -> {
            Map<String, Object> movieData = new HashMap<>();
            movieData.put("title", movie.getTitle());
            movieData.put("releaseDate", movie.getReleaseDate());
            movieData.put("rating", movie.getRating());
            movieData.put("poster", movie.getPoster());

            firestore.collection("favorites")
                    .document(movie.getTitle())
                    .set(movieData)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Saved to Favorites", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }

    private void displayMovieDetails(Movie movie) {
        tvMovieTitle.setText(movie.getTitle());
        tvMovieReleaseDate.setText(movie.getReleaseDate());
        tvMovieRating.setText(String.valueOf(movie.getRating()));

        String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPoster();
        Glide.with(this).load(posterUrl).into(imgMoviePoster);
    }

    private void fetchTrailer(int movieId) {
        TMDBApi api = RetrofitClient.getClient().create(TMDBApi.class);
        api.getVideos(movieId, API_KEY).enqueue(new Callback<VideoResponse>() {

            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getResults() == null) {
                    btnTrailer.setText("No Trailer Available");
                    return;
                }

                // Prefer official YouTube Trailer; fall back to any YouTube video
                Video selected = null;
                for (Video v : response.body().getResults()) {
                    if (!"YouTube".equals(v.getSite())) continue;

                    if ("Trailer".equals(v.getType())) {
                        // Prefer official; keep first match if none marked official yet
                        if (selected == null || v.isOfficial()) {
                            selected = v;
                        }
                    } else if (selected == null) {
                        // Fallback: any YouTube video if no trailer found yet
                        selected = v;
                    }
                }

                if (selected != null) {
                    final String youtubeKey = selected.getKey();
                    btnTrailer.setText("Watch Trailer");
                    btnTrailer.setEnabled(true);
                    btnTrailer.setOnClickListener(v -> {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey)
                        );
                        startActivity(intent);
                    });
                } else {
                    btnTrailer.setText("No Trailer Available");
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                btnTrailer.setText("Trailer Unavailable");
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
                if (response.isSuccessful() && response.body() != null
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