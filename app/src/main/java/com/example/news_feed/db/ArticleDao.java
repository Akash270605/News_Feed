package com.example.news_feed.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.news_feed.model.Article;
import java.util.List;

/**
 * ArticleDao stands for "Data Access Object".
 * It is an interface that defines the "Commands" (SQL) we can use to save,
 * delete, or read news articles from our phone's internal storage.
 */
@Dao
public interface ArticleDao {
    /**
     * Save an article to the database.
     * OnConflictStrategy.REPlACE means if we save the same news twice,
     * it will just update the old one with the new information.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    /**
     * Get a list of all articles saved in the database.
     * We use "LiveData" so that if the list changes (like deleting a news),
     * the screen updates itself automatically without us doing anything.
     */
    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

    /**
     * Remove a specific article from the database.
     */
    @Delete
    void deleteArticle(Article article);

    /**
     * A helper method to check if a specific news link is already saved.
     * Returns true if it exits, false if it doesn't.
     */
    @Query("SELECT EXISTS(SELECT * FROM articles WHERE url = :url)")
    boolean isArticleSaved(String url);
}
