package com.example.news_feed.api;

import com.example.news_feed.model.Article;
import java.util.List;

/**
 * NewsResponse is a "Data Container" class.
 * When the news website sends back a list of news in JSON format,
 * this class acts as a box to hold all those articles so the app can use them.
 */
public class NewsResponse {
    // Status tells us if the download was "ok" or if there was an error
    private String status;

    // totalResults tells us how many news stories the website found in total
    private int totalResults;

    // articles is the actual list of News stories (Articles) we want to show
    private List<Article> articles;

    // Getters allow MainActivity to "look inside the box" and get the list of news
    public String getStatus() { return status; }

    public int getTotalResults() { return totalResults; }
    public List<Article> getArticles() { return articles; }
}
