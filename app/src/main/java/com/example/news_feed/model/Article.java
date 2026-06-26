package com.example.news_feed.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Article is a "Data Model".
 * It is a blueprint that tells the app what information a single news story contains
 * (like title, image URL, and description).
 *
 * It is also an "Entity", meaning Room will create a database table based on this class.
 */

@Entity(tableName = "articles")     // Tells the database to create a table named 'articles'
public class Article implements Serializable{

    // The URL is unique for every news story, so we can use it as the "PrimaryKey" (ID)
    @PrimaryKey
    @NonNull
    private String url;

    private String title;
    private String description;
    private String author;
    private String urlToImage;
    private String publishedAt;
    private String content;

    // We store the name of the news source (like "BBC" or "CNN")
    private String sourceName;

    /**
     * Empty constructor required by the app's internal tools (Room and GSON).
     */
    public Article(){ this.url = ""; }

    //  ------------ Getters and Setters---------------

    @NonNull
    public String getUrl() { return url; }
    public void setUrl(@NonNull String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title;}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getUrlToImage() { return urlToImage; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }

    public String getPublishedAt(){ return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getContent() { return content;}
    public void setContent(String content) { this.content = content; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }


    /**
     * Source is a small helper class because the news website sends us the
     * source info as a nested object in JSON.
     */
    public static class Source{
        private String id;

        private String name;
        public String getName() { return name; }
    }

    /**
     * @Ignore tells the database "Don't try to save this specific field into a column",
     * because the database doesn't know how to store custom objects like "Source".
     */
    @Ignore
    private Source source;

    public Source getSource() { return source; }

    /**
     * Special setter that extracts just the name form the source object
     * and saves it into 'sourceName' for us.
     */
    public void setSource(Source source){
        this.source = source;
        if(source != null) this.sourceName = source.getName();
    }
}
