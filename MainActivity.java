package com.example.whackamoleproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView[] iceCreams;
    ImageView[] powerUps;

    Button start;
    TextView time;
    private int timeValue;
    private boolean isRunning;
    Random random;
    Handler handler;
    private List<ImageView> tallyMarks = new ArrayList<>();
    private int tallyCount = 0;
    private int tallyWidth = 50;
    private int tallySpacing = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.startButton);
        time = findViewById(R.id.id_timeText);

        handler = new Handler(Looper.getMainLooper());
        random = new Random();
        timeValue = 30;
        isRunning = false;

        iceCreams = new ImageView[]{
                findViewById(R.id.darkpink),
                findViewById(R.id.lightpink),
                findViewById(R.id.teal),
                findViewById(R.id.purple),
                findViewById(R.id.brown),
                findViewById(R.id.green)
        };

        powerUps = new ImageView[]{
                findViewById(R.id.darkPinkPowerUp),
                findViewById(R.id.lightpinkPowerUp),
                findViewById(R.id.tealPowerUp),
                findViewById(R.id.purplePowerUp),
                findViewById(R.id.brownPowerUp),
                findViewById(R.id.greenPowerUp)
        };


//when button is clicked, game will start
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
                hideViews();
                startAnimations();
                startTimer();

                // Set click listeners for moles
                for (ImageView iceCream : iceCreams) {
                    iceCream.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleAndSpinView(iceCream);
                            addTallyMark();
                        }
                    });
                }

                // Set click listeners for power-ups
                for (ImageView powerUp : powerUps) {
                    powerUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleAndSpinView(powerUp);
                            addTallyMark();
                            increaseTimeByFiveSeconds();
                        }
                    });
                }
            }
        });

    }

//setting all images to invisible in the beginning
    private void hideViews(){
        for (ImageView iC : iceCreams) {
            iC.setVisibility(View.INVISIBLE);
        }
        for (ImageView pU : powerUps) {
            pU.setVisibility(View.INVISIBLE);
        }
    }

//setting up timer in thread
    private void startTimer(){
        isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning && timeValue > 0){
                    try {
                        Thread.sleep(1000);
                        timeValue--;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int minutes = timeValue / 60;
                                int seconds = timeValue % 60;
                                String timeStr = String.format("%02d:%02d", minutes, seconds);
                                time.setText("Time: " + timeStr);
                            }
                        });
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (timeValue == 0){
                    isRunning = false;
                    gameOver();
                }
            }
        }).start();
    }

//starting the animation in the thread
    private void startAnimations() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            showNextMole();
                        }
                    });
                }
            }
        }).start();
    }

// showing the next mole
    private void showNextMole() {
        // Hide all moles and power-ups first
        hideViews();

        if (random.nextInt(10) == 0) {
            int index = random.nextInt(powerUps.length);
            ImageView powerUpView = powerUps[index];
            powerUpView.setVisibility(View.VISIBLE);
            viewAnimations(powerUpView);
        }

        else {
            int index = random.nextInt(iceCreams.length);
            ImageView imageView = iceCreams[index];
            imageView.setVisibility(View.VISIBLE);
            viewAnimations(imageView);
        }
    }

//making the animations
    private void viewAnimations(ImageView image){
        Animation zoomIn = new ScaleAnimation(0.5f, 2f, 0.5f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(500);

        Animation zoomOut = new ScaleAnimation(2f, 0.5f, 2f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOut.setDuration(500);

        AnimationSet zoomInAndOut = new AnimationSet(true);
        zoomInAndOut.addAnimation(zoomIn);
        zoomInAndOut.addAnimation(zoomOut);

        zoomInAndOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        image.setVisibility(View.INVISIBLE);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        int randomDelay = getRandomDelay();
        image.postDelayed(new Runnable() {
            @Override
            public void run() {
                image.startAnimation(zoomInAndOut);
            }
        }, randomDelay);

    }

//when clicked, the mole will spin 360
    private void scaleAndSpinView(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        // Scale animation
        Animation scaleAnimation = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);

        // Spin animation
        Animation spinAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        spinAnimation.setDuration(500);
        animationSet.addAnimation(spinAnimation);

        view.startAnimation(animationSet);
    }

// random delay for more randomness
    private int getRandomDelay() {
        return random.nextInt(2000);
    }

//adding a tally image when something gets clicked onto a LinearLayout
private void addTallyMark() {
    // Increment tally count
    tallyCount++;

    LinearLayout tallyLayout = findViewById(R.id.tallyLayout);

    if (tallyLayout.getChildCount() == 0 || tallyLayout.getChildAt(tallyLayout.getChildCount() - 1) instanceof LinearLayout) {
        LinearLayout newRow = new LinearLayout(this);
        newRow.setOrientation(LinearLayout.HORIZONTAL);
        tallyLayout.addView(newRow);
        tallyLayout = newRow; // Set tallyLayout to the new row
    }

    ImageView tally = new ImageView(this);
    tally.setImageResource(R.drawable.icecreamtally);
    int tallySize = dpToPx(30);
    int margin = dpToPx(2);
    int marginTop = dpToPx(5);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tallySize, tallySize);
    layoutParams.setMargins(margin, marginTop, margin, 0);
    tally.setLayoutParams(layoutParams);
    tallyLayout.addView(tally);

    if (tallyLayout.getChildCount() == 7) {
        LinearLayout newRow = new LinearLayout(this);
        newRow.setOrientation(LinearLayout.HORIZONTAL);
        tallyLayout.addView(newRow);
    }
}

// changing from dp to px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

// making a gameOverIntent
    private void gameOver() {
        Intent intent = new Intent(MainActivity.this, GameOverActivity2.class);
        intent.putExtra("tallyCount", tallyCount);
        startActivity(intent);
    }

// power up will increase time by 5 seconds
    private void increaseTimeByFiveSeconds() {
        timeValue += 5;
        updateTimeTextView();
    }

// updating the time textView
    private void updateTimeTextView() {
        int minutes = timeValue / 60;
        int seconds = timeValue % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        time.setText("Time: " + timeStr);
    }

// when start button is pressed, the game resets
    private void resetGame() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);

        tallyCount = 0;
        timeValue = 30;
        time.setText("Time: 00:30");
        LinearLayout tallyLayout = findViewById(R.id.tallyLayout);
        tallyLayout.removeAllViews();
    }



}