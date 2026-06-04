package com.example.flixgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    Button btnWatchTrailer, btnViewReviews;
    RecyclerView recyclerHome, recyclerTopRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {

            btnWatchTrailer = findViewById(R.id.btnWatchTrailer);
            btnViewReviews = findViewById(R.id.btnViewReviews);

            recyclerHome = findViewById(R.id.recyclerHome);
            recyclerTopRated = findViewById(R.id.recyclerTopRated);

            // NAVIGATION SAFE
            btnWatchTrailer.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, WatchTrailerActivity.class))
            );

            btnViewReviews.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, ViewReviewsActivity.class))
            );

            // DATA
            ArrayList<String> movies = new ArrayList<>();
            movies.add("Batman (2022)");
            movies.add("Joker");
            movies.add("Inception");
            movies.add("Interstellar");

            ArrayList<String> topRated = new ArrayList<>();
            topRated.add("The Dark Knight");
            topRated.add("Fight Club");
            topRated.add("Avengers Endgame");

            setupRecycler(recyclerHome, movies);
            setupRecycler(recyclerTopRated, topRated);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecycler(RecyclerView recycler, ArrayList<String> data) {

        recycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        recycler.setAdapter(new RecyclerView.Adapter<VH>() {

            @Override
            public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
                android.widget.TextView tv = new android.widget.TextView(parent.getContext());
                tv.setPadding(40, 40, 40, 40);
                tv.setTextColor(android.graphics.Color.WHITE);
                tv.setBackgroundColor(0xFF1A1A1A);
                return new VH(tv);
            }

            @Override
            public void onBindViewHolder(VH holder, int position) {
                holder.tv.setText(data.get(position));

                holder.itemView.setOnClickListener(v ->
                        Toast.makeText(HomeActivity.this,
                                data.get(position),
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        android.widget.TextView tv;

        VH(android.view.View itemView) {
            super(itemView);
            tv = (android.widget.TextView) itemView;
        }
    }
}