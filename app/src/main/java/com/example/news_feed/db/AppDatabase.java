package com.example.news_feed.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.news_feed.model.Article;

@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    //INSTANCE holds our single database object
    private static volatile AppDatabase INSTANCE;

    // This connects the Database to the Dao commands
    public abstract ArticleDao articleDao();

    /**
     * This method is like a "Door" to the database.
     * Every time we want to save or read news, we call this method.
     */
    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){

            // "synchronized" ensures two threads don't accidentally create two database at once
            synchronized (AppDatabase.class){
                if(INSTANCE == null){

                    // This line actually creates database file on the phone
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "news_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
