package com.example.flixgo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText etSearch;
    Button btnSearch;

    RecyclerView recyclerMovies;

    ArrayList<Movie> movieList;
    MovieAdapter adapter;

    // Replace with your team's TMDB API Key
    String API_KEY = "PASTE_API_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerMovies = findViewById(R.id.recyclerMovies);

        movieList = new ArrayList<>();

        adapter = new MovieAdapter(movieList);

        recyclerMovies.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerMovies.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {

            String movieName =
                    etSearch.getText().toString().trim();

            searchMovie(movieName);

        });
    }

    private void searchMovie(String movieName) {

        new Thread(() -> {

            try {

                String url =
                        "https://api.themoviedb.org/3/search/movie?api_key="
                                + API_KEY +
                                "&query=" +
                                movieName;

                OkHttpClient client =
                        new OkHttpClient();

                Request request =
                        new Request.Builder()
                                .url(url)
                                .build();

                Response response =
                        client.newCall(request).execute();

                String result =
                        response.body().string();

                JSONObject jsonObject =
                        new JSONObject(result);

                JSONArray results =
                        jsonObject.getJSONArray("results");

                movieList.clear();

                for (int i = 0; i < results.length(); i++) {

                    JSONObject movie =
                            results.getJSONObject(i);

                    String title =
                            movie.getString("title");

                    String releaseDate =
                            movie.getString("release_date");

                    double rating =
                            movie.getDouble("vote_average");

                    String poster =
                            movie.getString("poster_path");

                    movieList.add(
                            new Movie(
                                    title,
                                    releaseDate,
                                    rating,
                                    poster
                            )
                    );
                }

                runOnUiThread(() ->
                        adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}