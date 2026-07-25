package com.my.luck;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChessActivity extends AppCompatActivity {
    private ChessBoard chessBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Create layout programmatically
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(android.view.Gravity.CENTER);
            layout.setBackgroundColor(0xFF2C3E50);
            
            TextView title = new TextView(this);
            title.setText("♟️ Try Your Luck");
            title.setTextSize(28);
            title.setTextColor(0xFFFFFFFF);
            title.setPadding(0, 20, 0, 20);
            layout.addView(title);
            
            chessBoard = new ChessBoard(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1
            );
            params.setMargins(20, 20, 20, 20);
            chessBoard.setLayoutParams(params);
            layout.addView(chessBoard);
            
            setContentView(layout);
            setTitle("Try Your Luck - Chess");
            Toast.makeText(this, "♟️ Try Your Luck!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading chess: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chessBoard != null) {
            chessBoard.invalidate();
        }
    }
}
