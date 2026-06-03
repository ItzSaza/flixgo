package com.example.flixgo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerReviews;
    List<Review> reviewList;
    ReviewAdapter adapter;
    Button btnTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);

        recyclerReviews = findViewById(R.id.recyclerReviews);
        btnTrailer = findViewById(R.id.btnTrailer);

        reviewList = new ArrayList<>();

        adapter = new ReviewAdapter(reviewList);

        recyclerReviews.setLayoutManager(
                new LinearLayoutManager(this));

        recyclerReviews.setAdapter(adapter);

        TMDBApi api =
                RetrofitClient
                        .getClient()
                        .create(TMDBApi.class);

        Call<ReviewResponse> call =
                api.getReviews(
                        550,
                        "ebe03d995dffa21748ee1c932f8c2eb6"
                );

        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(
                    Call<ReviewResponse> call,
                    Response<ReviewResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    reviewList.clear();

                    reviewList.addAll(
                            response.body().getResults()
                    );

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(
                    Call<ReviewResponse> call,
                    Throwable t) {

                Toast.makeText(
                        MovieDetailsActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        btnTrailer.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                            "https://www.youtube.com/watch?v=SUXWAEX2jlg"
                    )
            );

            startActivity(intent);
        });
    }
}