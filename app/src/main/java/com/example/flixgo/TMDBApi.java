package com.example.flixgo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApi {

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getReviews(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    // Used to look up a movie's TMDB id by title (for favorites loaded without an id)
    @GET("search/movie")
    Call<MovieSearchResponse> searchMovies(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );
}