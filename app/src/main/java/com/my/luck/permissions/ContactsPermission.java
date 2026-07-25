package com.my.luck.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ContactsPermission {
    private static final int REQUEST_CODE = 103;
    
    public static boolean hasPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    
    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
            Manifest.permission.READ_CONTACTS
        }, REQUEST_CODE);
    }
}