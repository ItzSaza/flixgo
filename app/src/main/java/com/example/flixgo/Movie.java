package com.example.flixgo;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String releaseDate;
    private double rating;
    private String poster;

    public Movie(String title, String releaseDate, double rating, String poster) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
