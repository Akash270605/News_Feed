package com.example.news_feed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.news_feed.adapter.NewsAdapter;
import com.example.news_feed.databinding.ActivitySavedArticlesBinding;
import com.example.news_feed.db.AppDatabase;
import com.example.news_feed.model.Article;

import java.util.ArrayList;
import java.util.List;

public class SavedArticlesActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    private ActivitySavedArticlesBinding binding;
    private NewsAdapter adapter;
    private List<Article> savedArticlesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        observeSavedArticles();

        // Button to open all saved articles in the browser
        binding.flabOpenAll.setOnClickListener(v -> {
            for (Article article : savedArticlesList) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter(this);
        binding.rvSavedNews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSavedNews.setAdapter(adapter);
    }

    private void observeSavedArticles() {
        // Observe the database for changes
        AppDatabase.getInstance(this).articleDao().getAllArticles().observe(this, articles -> {
            if (articles != null) {
                savedArticlesList = articles;
                adapter.setArticles(articles);

                // Show/hide empty state message
                if (articles.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.flabOpenAll.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.flabOpenAll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        // Open the article in the browser
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
    }

    @Override
    public void onShareClick(Article article) {
        // Share the article link
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n" + article.getUrl());
        startActivity(Intent.createChooser(intent, "Share News"));
    }

    @Override
    public void onSaveClick(Article article) {
        // In the Saved screen, clicking "Save" again will remove the article from bookmarks
        new Thread(() -> {
            AppDatabase.getInstance(getApplicationContext()).articleDao().deleteArticle(article);
            runOnUiThread(() -> Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
