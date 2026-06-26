package com.example.news_feed.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.news_feed.R;

/**
 * NewsUpdateWorker is a background task runner managed by WorkManager.
 * It allows the app to perform tasks (like checking for news or showing notifications)
 * even when the app is closed or the phone is idle.
 */
public class NewsUpdateWorker extends Worker{

    //Unique ID for the notification channel
    private static final String CHANNEL_ID = "news_updates";

    /**
     * Constructor for the worker.
     * context -> The application context.
     * workerParams -> Parameters for the worker.
     */
    public NewsUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
    }

    /**
     *doWork() contains the actual code that runs in the background.
     * This method is called on a background thread automatically.
     */
    @NonNull
    @Override
    public Result doWork(){
        // In a real-world app, we would fetch the news from the API and
        // notify the user if there are new articles.
        showNotification("Latest news is ready!", "Check out the newest headlines in your favourite categories.");

        // Indicate that the work finished successfully
        return Result.success();
    }

    /**
     * Helper method to create and display a system notification.
     */
    private void showNotification(String title, String message){

        // Get the system Service responsible for notifications
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel to show the notifications
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "News Updates", NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification appearance and behaviour
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)  // Icon shown in status bar
                .setContentTitle(title)                                 // Main bold text
                .setContentText(message)                                // Smaller description text
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)       // Priority for older Android version
                .setAutoCancel(true);                                   // Remove notification when clicked

        // Display the notification using a unique ID (1)
        notificationManager.notify(1, builder.build());
    }
}
