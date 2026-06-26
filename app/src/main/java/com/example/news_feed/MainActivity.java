package com.example.news_feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.news_feed.R;
import com.example.news_feed.SavedArticlesActivity;
import com.example.news_feed.adapter.NewsAdapter;
import com.example.news_feed.api.NewsApiService;
import com.example.news_feed.api.NewsResponse;
import com.example.news_feed.databinding.ActivityMainBinding;
import com.example.news_feed.db.AppDatabase;
import com.example.news_feed.model.Article;
import com.example.news_feed.worker.NewsUpdateWorker;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MainActivity is the "Home Screen" of the app.
 * It shows the list of news, allows switching categories, and saving articles.
 */
public class MainActivity extends AppCompatActivity implements NewsAdapter.OnArticleClickListener {

    // binding helps us access UI elements (buttons, lists, etc.) without using findViewById
    private ActivityMainBinding binding;

    // adapter acts as a bridge between our data (news list) and the UI (RecyclerView)
    private NewsAdapter adapter;

    // apiService is used to talk to the internet (NewsAPI)
    private NewsApiService apiService;

    // prefs is used to save small data like the user's last selected category
    private SharedPreferences prefs;

    // This is your "password" for the news website. Get one for free at newsapi.org
    private static final String API_KEY = "Add your API KEY";
    private static final String PREF_CATEGORY = "pref_category";

    // These are the sections of news we can show
    private final String[] categories = {"general", "business", "entertainment", "health", "science", "sports", "technology"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ViewBinding to link this Java file to activity_main.xml
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup the top blue/white bar (Toolbar)
        setSupportActionBar(binding.toolbar);

        // Load the category the user was looking at last time
        prefs = getPreferences(MODE_PRIVATE);
        String savedCategory = prefs.getString(PREF_CATEGORY, "general");

        // Initial setup for the list, tabs, and internet connection
        setupRecyclerView();
        setupTabLayout(savedCategory);
        setupRetrofit();

        // Start a background timer to check for news even when the app is closed
        scheduleBackgroundUpdates();

        // Download and show news for the current category
        fetchNews(savedCategory);

        // When the "Watch Bookmarks" button is clicked, go to the saved articles screen
        binding.fabBookmarks.setOnClickListener(v -> {
            startActivity(new Intent(this, SavedArticlesActivity.class));
        });
    }

    /**
     * Create the menu items (like the "Save" icon) in the top bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * What happens when you click a menu item in the top bar
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_saved) {
            // Open the SavedArticlesActivity screen
            startActivity(new Intent(this, SavedArticlesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Setup the list (RecyclerView) so it knows how to display items
     */
    private void setupRecyclerView() {
        adapter = new NewsAdapter(this);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNews.setAdapter(adapter);
    }

    /**
     * Setup the tabs (General, Tech, etc.) at the top
     */
    private void setupTabLayout(String selectedCategory) {
        int selectedIndex = 0;
        for (int i = 0; i < categories.length; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab().setText(categories[i]);
            binding.tabLayout.addTab(tab);
            if (categories[i].equals(selectedCategory)) selectedIndex = i;
        }

        // Select the last used category tab
        TabLayout.Tab selectedTab = binding.tabLayout.getTabAt(selectedIndex);
        if (selectedTab != null) {
            selectedTab.select();
        }

        // What happens when you click a different tab (e.g. Science)
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String category = tab.getText().toString();
                    // Save the choice and download new news
                    prefs.edit().putString(PREF_CATEGORY, category).apply();
                    fetchNews(category);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Setup the internet connection settings (Retrofit)
     */
    private void setupRetrofit() {
        // OkHttpClient adds extra info (like our API Key) to every internet request
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "NewsApp/1.0")
                            .header("X-Api-Key", API_KEY)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        // Retrofit builds the actual "caller" for the news website
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) // Converts JSON text into Java objects
                .build();
        apiService = retrofit.create(NewsApiService.class);
    }

    /**
     * Actually download news from the internet
     */
    private void fetchNews(String category) {
        // Show the loading spinner
        binding.progressBar.setVisibility(View.VISIBLE);

        // Ask the API for headlines
        apiService.getTopHeadlines("us", category, null).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                // Hide the loading spinner when done
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // Give the news to our list adapter to show them
                    adapter.setArticles(response.body().getArticles());
                } else {
                    Toast.makeText(MainActivity.this, "Error getting news", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                // Hide spinner if internet fails
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Tells Android to run a task every 15 minutes in the background
     */
    private void scheduleBackgroundUpdates() {
        // Only run if the phone has internet
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NewsUpdateWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
    }

    /**
     * When a news item is clicked, open its full website in the browser
     */
    @Override
    public void onArticleClick(Article article) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
    }

    /**
     * When the share button is clicked, open the Android "Share with..." popup
     */
    @Override
    public void onShareClick(Article article) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n" + article.getUrl());
        startActivity(Intent.createChooser(intent, "Share News"));
    }

    /**
     * When the save button is clicked, put the article into our phone's database
     */
    @Override
    public void onSaveClick(Article article) {
        // We must do database work on a "Thread" so the app doesn't freeze
        new Thread(() -> {
            AppDatabase.getInstance(getApplicationContext()).articleDao().insertArticle(article);
            // After saving, show a "Toast" message on the screen
            runOnUiThread(() -> Toast.makeText(this, "Saved to bookmarks", Toast.LENGTH_SHORT).show());
        }).start();
    }
}
