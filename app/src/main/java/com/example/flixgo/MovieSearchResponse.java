package com.example.flixgo;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieSearchResponse {

    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() { return results; }
}