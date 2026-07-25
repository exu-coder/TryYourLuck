package com.my.luck;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChessActivity extends AppCompatActivity {
    private ChessBoard chessBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            chessBoard = new ChessBoard(this);
            setContentView(chessBoard);
            setTitle("Try Your Luck - Chess");
            Toast.makeText(this, "♟️ Try Your Luck!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading chess game", Toast.LENGTH_SHORT).show();
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
