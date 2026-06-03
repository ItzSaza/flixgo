package com.example.flixgo;

public class Movie {

    private String title;
    private String releaseDate;
    private double rating;
    private String posterPath;

    public Movie(String title,
                 String releaseDate,
                 double rating,
                 String posterPath) {

        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.posterPath = posterPath;
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

    public String getPosterPath() {
        return posterPath;
    }
}