package com.example.atariproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    GameSurface gameSurface;
    float angle;
    private int score = 0;

    Bitmap filledBasketBall;
    Bitmap filledBasket;
    Bitmap crashBasketBall;
    Bitmap earthwormBasket;
    Bitmap powerupBasketBall;
    Bitmap powerupBasket;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        int filledWidth = 250;
        int filledHeight = 250;

        filledBasketBall = BitmapFactory.decodeResource(getResources(), R.drawable.filledbasket);
        crashBasketBall = BitmapFactory.decodeResource(getResources(), R.drawable.crashedearthworm);
        powerupBasketBall = BitmapFactory.decodeResource(getResources(), R.drawable.tangledflowerbasket);
        filledBasket = Bitmap.createScaledBitmap(filledBasketBall, filledWidth, filledHeight, false);
        earthwormBasket = Bitmap.createScaledBitmap(crashBasketBall, filledWidth, filledHeight, false);
        powerupBasket = Bitmap.createScaledBitmap(powerupBasketBall, filledWidth, filledHeight, false);

        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();


    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        angle = sensorEvent.values[1];
        Log.d("TAG_YOURE_IT", angle + "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public class GameSurface extends SurfaceView implements Runnable, View.OnTouchListener{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap originalBall;
        Bitmap basket;
        Bitmap basket2;
        Bitmap backgroundImage;


        int ballX = 0;
        int x = 200;
        Paint paintProperty;
        Paint textPaint;
        int screenWidth;
        int screenHeight;

        private List<FallingImage> fallingFlowers;
        private Bitmap[] flowerBitmaps;

        boolean isCrashed = false;
        long crashTime = 0;
        long crashDuration = 2000;

        private SoundPool soundPool;
        private int soundFlowerCollision;
        private int soundEarthwormCollision;
        private int soundPowerupCollision;
        private int soundGameOver;


        public GameSurface(Context context) {
            super(context);
            holder = getHolder();
            backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            originalBall = BitmapFactory.decodeResource(getResources(),R.drawable.basket);

            int desiredWidth = 250;
            int desiredHeight = 250;
            basket = Bitmap.createScaledBitmap(originalBall, desiredWidth, desiredHeight, false);
            basket2 = Bitmap.createScaledBitmap(originalBall, desiredWidth, desiredHeight, false);

            fallingFlowers = new ArrayList<>();
            flowerBitmaps = new Bitmap[5];
            flowerBitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.blueflower);
            flowerBitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.pinkflower);
            flowerBitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.purpleflower);
            flowerBitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tangledflowerpowerup);
            flowerBitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.earthworm);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;
            paintProperty = new Paint();
            paintProperty.setTextSize(75);
            paintProperty.setTypeface(Typeface.create("Arial", Typeface.BOLD));
            textPaint = new Paint();
            textPaint.setTextSize(60);
            textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));

            soundPool = new SoundPool.Builder().setMaxStreams(3).build();
            soundFlowerCollision = soundPool.load(context, R.raw.flowereffect, 1);
            soundEarthwormCollision = soundPool.load(context, R.raw.earthwormeffect, 1);
            soundPowerupCollision = soundPool.load(context, R.raw.powerupeffect, 1);
            soundGameOver = soundPool.load(context, R.raw.gameovereffect, 1);

            setOnTouchListener(this);
        }

        @Override
        public void run() {
            while (running == true) {

                if (holder.getSurface().isValid() == false)
                    continue;

                Canvas canvas = holder.lockCanvas();

                canvas.drawBitmap(backgroundImage, -600, -200, null);

                if (score >= 30) {
                    GameOverScreen.drawOver(canvas, score);
                    running = false;
                    makeTransparent(basket2, 0);
                    mediaPlayer.stop();
                    playGameOverSound();
                    Log.d("Game Over", "You're finished! The score you got was: " + score);
                }

                    Paint menuPaint = new Paint();
                    menuPaint.setColor(Color.rgb(211, 137, 241));
                    canvas.drawRect((screenWidth / 2) - basket.getWidth() / 2 - 450, (screenHeight - 260), (screenWidth / 2) + basket.getWidth() / 2 + 450, (screenHeight - 70), menuPaint);


                    if (fallingFlowers.isEmpty() || fallingFlowers.get(fallingFlowers.size() - 1).getY() > screenHeight) {
                        spawnFlower();
                    }

                    for (int i = 0; i < fallingFlowers.size(); i++) {
                        FallingImage fallingFlower = fallingFlowers.get(i);
                        fallingFlower.update();

                        if (fallingFlower.getY() >= 1750) {
                            fallingFlowers.remove(i);
                            i--;
                        }

                        else {
                            fallingFlower.draw(canvas);

                            Log.d("Collision Detected?", collisionDetect(fallingFlower) + "");

                            if (!isCrashed && collisionDetect(fallingFlower)) {

                                if (fallingFlower.getImageId() == R.drawable.earthworm) {
                                    basket = earthwormBasket;
                                    Log.d("TAG_COLLIDED", "earthworm is detected");
                                    isCrashed = true;
                                    crashTime = System.currentTimeMillis();
                                    score--;
                                    playEarthwormSound();
                                }

                                else if (fallingFlower.getImageId() == R.drawable.tangledflowerpowerup) {
                                    basket = powerupBasket;
                                    Log.d("TAG_COLLIDED", "tangled flower is detected");
                                    isCrashed = true;
                                    crashTime = System.currentTimeMillis();
                                    score += 3;
                                    playPowerUpSound();
                                }

                                else {
                                    basket = filledBasket;
                                    Log.d("TAG_COLLIDED", "flower " + (fallingFlower.getImage() == flowerBitmaps[3]));
                                    isCrashed = true;
                                    crashTime = System.currentTimeMillis();
                                    score++;
                                    playFlowerSound();
                                }

                            }
                        }
                    }

                    if (isCrashed && System.currentTimeMillis() - crashTime >= crashDuration) {
                        isCrashed = false;
                        basket = basket2;
                    }

                    int newBallX = ballX;

                    if (angle < -0.05 && angle > -0.3) {
                        newBallX -= 3;
                    }

                    else if (angle > 0.05 && angle < 0.3) {
                        newBallX += 3;
                    }

                    else if (angle < -0.3) {
                        newBallX -= 7;
                    }

                    else if (angle > 0.3) {
                        newBallX += 7;
                    }

                    if (newBallX < (screenWidth / 2 - 950)) {
                        ballX = (screenWidth / 2 - 950);
                    }

                    else if (newBallX > (screenWidth / 2 - 130)) {
                        ballX = (screenWidth / 2 - 130);
                    }

                    else {
                        ballX = newBallX;
                    }

                    canvas.drawBitmap(basket, (screenWidth / 2) - basket.getWidth() / 2 + ballX, (screenHeight - 250) - basket.getHeight(), null);

                    canvas.drawText("Score: " + score, 720, 2017, paintProperty);
                    canvas.drawText("Tangled Atari Project", 45, 2010, textPaint);

                    holder.unlockCanvasAndPost(canvas);

            }

        }

        public Bitmap makeTransparent(Bitmap src, int value) {
            int width = src.getWidth();
            int height = src.getHeight();
            Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(transBitmap);
            canvas.drawARGB(0, 0, 0, 0);
            // config paint
            final Paint paint = new Paint();
            paint.setAlpha(value);
            canvas.drawBitmap(src, 0, 0, paint);
            return transBitmap;
        }
        private void playFlowerSound() {
            soundPool.play(soundFlowerCollision, 1, 1, 1, 0, 1);
        }

        private void playPowerUpSound() {
            soundPool.play(soundPowerupCollision, 2, 2, 1, 0, 1);
        }

        private void playEarthwormSound() {
            soundPool.play(soundEarthwormCollision, 1, 1, 1, 0, 1);
        }

        private void playGameOverSound() {
            soundPool.play(soundGameOver, 3, 3, 1, 0, 1);
        }

        private boolean collisionDetect(FallingImage flower) {
            int flowerLeft = flower.getX();
            int flowerRight = flowerLeft + flower.getWidth();
            int flowerUp = flower.getY();
            int flowerDown = flowerUp + flower.getHeight();

            int basketLeft = (screenWidth / 2) - basket.getWidth() / 2 + ballX;
            int basketRight = basketLeft + basket.getWidth();
            int basketTop = (screenHeight - 90) - basket.getHeight();
            int basketBottom = basketTop + basket.getHeight();

            Log.d("FlowerLeft", flowerLeft + "");
            Log.d("FlowerRight", flowerRight + "");
            Log.d("FlowerUp", flowerUp + "");
            Log.d("FlowerDown", flowerDown + "");
            Log.d("BasketLeft", basketLeft + "");
            Log.d("BasketRight", basketRight + "");
            Log.d("BasketTop", basketTop + "");
            Log.d("BasketBottom", basketBottom + "");

            return flowerRight >= basketLeft && flowerLeft <= basketRight &&
                    flowerDown >= basketTop && flowerUp <= basketBottom;
            }

        private void spawnFlower() {
            int desiredWidth = 200;
            int desiredHeight = 200;

            Bitmap flowerBitmap = flowerBitmaps[(int) (Math.random() * flowerBitmaps.length)];

            int imageId = 0;

            if (flowerBitmap == flowerBitmaps[4]) {
                imageId = R.drawable.earthworm;
            }

            if (flowerBitmap == flowerBitmaps[3]){
                imageId = R.drawable.tangledflowerpowerup;
            }

            if (flowerBitmap == flowerBitmaps[2]){
                imageId = R.drawable.purpleflower;
            }

            if (flowerBitmap == flowerBitmaps[1]){
                imageId = R.drawable.pinkflower;
            }

            if (flowerBitmap == flowerBitmaps[0]){
                imageId = R.drawable.blueflower;
            }


            Bitmap scaledBitmap = Bitmap.createScaledBitmap(flowerBitmap, desiredWidth, desiredHeight, false);
            int x = (int) (Math.random() * (screenWidth - scaledBitmap.getWidth()));
            FallingImage flower = new FallingImage(x, -scaledBitmap.getHeight(), scaledBitmap, imageId);
            fallingFlowers.add(flower);
        }

        public void resume(){
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;

            while (true) {

                try {
                    gameThread.join();
                }
                catch (InterruptedException e) {

                }
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                for (FallingImage fallingImage : fallingFlowers) {
                    fallingImage.increaseSpeed();
                }

            }
            return true;
        }

    } //GameSurface

    public class FallingImage {
        private int imageId;
        private Bitmap image;
        private int x, y;
        private int speed;

        public FallingImage(int x, int y, Bitmap image, int imageId) {
            this.x = x;
            this.y = y;
            this.imageId = imageId;
            this.image = image;
            this.speed = 10;

        }

        public void update() {
            y += speed;
        }
        

        public void draw(Canvas canvas) {
            canvas.drawBitmap(image, x, y, null);
        }

        public int getX(){
            return x;
        }

        public int getY() {
            return y;
        }

        public int getHeight(){
            return image.getHeight();
        }

        public int getWidth(){
            return image.getWidth();
        }

        public Bitmap getImage(){
            return image;
        }

        public int getImageId() {
            return imageId;
        }

        public void increaseSpeed() {
            speed *= 4;
        }

    } //FallingImage

    public static class GameOverScreen {
        private Context context;

        public GameOverScreen(Context context){
            this.context = context;
        }

        public static void drawOver(Canvas canvas, int score) {
            canvas.drawRGB(211, 137, 241);

            Paint gameOverPaint = new Paint();
            gameOverPaint.setColor(Color.BLACK);
            gameOverPaint.setTextSize(150);

            canvas.drawText("Game Over!", 150, 800, gameOverPaint);
            canvas.drawText("Score: " + score, 150, 1200, gameOverPaint);
        }

    } // GameOverScreen

}