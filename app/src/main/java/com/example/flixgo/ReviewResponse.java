package com.example.flixgo;

import java.util.List;

public class ReviewResponse {
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}
