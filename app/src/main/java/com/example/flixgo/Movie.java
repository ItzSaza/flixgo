package com.example.flixgo;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Movie implements Serializable {

    // CRITICAL FIX: Tells GSON to look for "id" in TMDB response
    @SerializedName("id")
    private int id;

    // Matches "title" in TMDB response
    @SerializedName("title")
    private String title;

    // TMDB uses snake_case ("release_date") in its JSON API!
    @SerializedName("release_date")
    private String releaseDate;

    // TMDB uses "vote_average" for movie rating scores!
    @SerializedName("vote_average")
    private double rating;

    // TMDB uses "poster_path" for image endpoints!
    @SerializedName("poster_path")
    private String poster;

    public Movie() {
    }

    //FIX FOR FAVORITES ACTIVITY: Constructor that matches your error line
    public Movie(String title, String releaseDate, double rating, String poster) {
        this.id = 0; // Favorites saved locally don't have a TMDB ID, which is fine!
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.poster = poster;
    }

    //5-parameter Constructor for HomeActivity
    public Movie(int id, String title, String releaseDate, double rating, String poster) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.poster = poster;
    }

    // --- GETTER METHODS ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public String getPoster() {
        return poster;
    }
}