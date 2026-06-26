package com.example.news_feed.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * NewsApiService defines the "Rules" for talking to the News website.
 * Each method here is like a specific question we can ask the news server.
 */
public interface NewsApiService {

    /**
     * getTopHeadlines asks for the biggest news stories right now.
     * country -> The country code (e.g., "us" for USA).
     * category -> The type of news (e.g., "tech", "sports", etc).
     * apiKey -> Personal "entry key" for the news website.
     *
     * It returns "Call" object that Retrofit will use to start the downloading.
     */
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    /**
     * getEverything lets us search for any topic we want.
     * query -> The word we want to find news about (e.g., "Android").
     */
    @GET("everything")
    Call<NewsResponse> getEverything(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
