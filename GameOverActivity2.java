package com.example.whackamoleproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over2);

        TextView score = findViewById(R.id.id_score);
        Button restartButton = findViewById(R.id.id_restart);

        int tallyCount = getIntent().getIntExtra("tallyCount", 0);
        score.setText("Tally Count: " + tallyCount);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
    }

    private void restartGame() {
        Intent intent = new Intent(GameOverActivity2.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}