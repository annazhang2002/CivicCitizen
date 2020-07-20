package com.example.votingapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.votingapp.activities.MainActivity;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.votingapp.ParseApplication.CHANNEL_ID;

public class ReminderBroadcast extends BroadcastReceiver {
    public static final String CHANNEL_ID = "electionReminder";
    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        Intent newIntent = new Intent(context, MainActivity.class);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.question_menu_icon)
                .setContentTitle("Register to Vote!")
                .setContentText("Make sure you get your forms in before the deadline!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
