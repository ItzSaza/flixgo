package com.example.flixgo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApi {

    @GET("movie/now_playing")
    Call<VideoResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey
    );

    @GET("movie/top_rated")
    Call<VideoResponse> getTopRatedMovies(
            @Query("api_key") String apiKey
    );

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
}