package com.example.news_feed.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news_feed.databinding.ItemArticleBinding;
import com.example.news_feed.model.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes list of news articles (Java data) and turns them into
 * visual items on the phone screen (UI)
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ArticleViewHolder> {

    // This List holds all the news articles we want to show
    private List<Article> articles = new ArrayList<>();

    // This listener helps us detect when a user clicks something in the list
    private final OnArticleClickListener listener;

    /**
     * This interface tells the Activity (MainActivity) that something was clicked
     */
    public interface OnArticleClickListener{
        void onArticleClick(Article article);   // Clicked the whole card
        void onShareClick(Article article);     // Clicked the share icon
        void onSaveClick(Article article);      // Clicked the save icon
    }

    public NewsAdapter(OnArticleClickListener listener){ this.listener = listener;}

    /**
     * Use this method to give the adapter a new list of news to show.
     */
    public void setArticles(List<Article> articles){
        if(articles != null){
            this.articles = articles;
        }else{
            this.articles = new ArrayList<>();
        }

        // This command tells the list to refresh itself and show the new data
        notifyDataSetChanged();
    }

    /**
     * This method creates a new "Empty Card" (ViewHolder) when the list needs one.
     */
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // We "inflate" (create) the XML layout for a single news item
        ItemArticleBinding binding = ItemArticleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ArticleViewHolder(binding);
    }

    /**
     * This method "FIlls" the empty card with real data (Title, Image, etc.)
     */
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position){
        // Get the news article for this specific position in the list
        Article article = articles.get(position);

        // Put the title and description into the TextViews
        holder.binding.tvArticleTitle.setText(article.getTitle());
        holder.binding.tvArticleDescription.setText(article.getDescription());
        holder.binding.tvArticleSource.setText(article.getSourceName());

        // Glide is a helper library that downloads the image from the internet and shows it
        Glide.with(holder.itemView.getContext())
                .load(article.getUrlToImage())
                .placeholder(android.R.drawable.ic_menu_report_image) // show this while loading
                .into(holder.binding.ivArticleImage);

        // Setup what happens when the user clicks parts of the card
        holder.itemView.setOnClickListener(v -> listener.onArticleClick(article));
        holder.binding.btnShare.setOnClickListener(v -> listener.onShareClick(article));
        holder.binding.btnSave.setOnClickListener(v -> listener.onSaveClick(article));
    }

    /**
     * This tells the list how many items are in the news collections.
     */
    @Override
    public int getItemCount(){ return articles.size(); }

    /**
     * This class "Holds" the views for a single news card so we find them quickly.
     */
    static class ArticleViewHolder extends RecyclerView.ViewHolder{
        ItemArticleBinding binding;

        public ArticleViewHolder(@NonNull ItemArticleBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
