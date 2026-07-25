package com.my.luck.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AudioPermission {
    private static final int REQUEST_CODE = 105;
    
    public static boolean hasPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    
    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
            Manifest.permission.RECORD_AUDIO
        }, REQUEST_CODE);
    }
}