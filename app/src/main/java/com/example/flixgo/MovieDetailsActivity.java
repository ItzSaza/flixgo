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

        // Get Movie Data from Intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie != null) {
            displayMovieDetails(movie);
            fetchReviews(550); // Using 550 as a placeholder or you could add id to Movie class
        }

        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnTrailer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=SUXWAEX2jlg"));
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> {

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
        Call<ReviewResponse> call = api.getReviews(movieId, "ebe03d995dffa21748ee1c932f8c2eb6");

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
                Toast.makeText(MovieDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
