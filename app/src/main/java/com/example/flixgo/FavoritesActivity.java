package com.example.flixgo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerFavorites;
    ArrayList<Movie> favoriteMovies;
    MovieAdapter adapter;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerFavorites = findViewById(R.id.recyclerFavorites);
        btnBack = findViewById(R.id.btnBack);

        favoriteMovies = new ArrayList<>();

        adapter = new MovieAdapter(favoriteMovies);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerFavorites.setAdapter(adapter);

        loadFavorites();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    favoriteMovies.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String title = doc.getString("title");
                        String releaseDate = doc.getString("releaseDate");
                        Double rating = doc.getDouble("rating");
                        String poster = doc.getString("poster");

                        favoriteMovies.add(
                                new Movie(
                                        title,
                                        releaseDate,
                                        rating != null ? rating : 0,
                                        poster
                                )
                        );
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }
}
