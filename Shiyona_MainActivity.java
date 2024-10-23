package com.example.weatherproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Shiyona_MainActivity extends AppCompatActivity {

    TextView date;
    TextView feelsLike;
    TextView place;
    EditText zipcode;
    Button check;
    TextView temp;
    ImageView weather;
    SeekBar time_interval;
    JSONObject weatherObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zipcode = findViewById(R.id.id_zipcodE);
        place = findViewById(R.id.location);
        feelsLike = findViewById(R.id.weatherAndFeeling);
        date = findViewById(R.id.date);
        temp = findViewById(R.id.temp_image);
        check = findViewById(R.id.check_button);
        weather = findViewById(R.id.weather_image);
        time_interval = findViewById(R.id.time_seekbar);


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String zip = zipcode.getText().toString().trim();
               new ASync().execute(zip);
            }
        });

        time_interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int index = progress;
                try {
                    updateUI(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String weatherStr = null;
                try {
                    weatherStr = weatherObject.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(0).getString("description");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (weatherStr.contains("clear")){
                    weather.setImageResource(R.drawable.sun);
                }

                else if (weatherStr.contains("few")){
                    weather.setImageResource(R.drawable.fewclouds);
                }

                else if (weatherStr.contains("broken")){
                    weather.setImageResource(R.drawable.brokenclouds);
                }

                else if (weatherStr.contains("scattered")){
                    weather.setImageResource(R.drawable.scatteredclouds);
                }

                else if (weatherStr.contains("snow")){
                    weather.setImageResource(R.drawable.snow);
                }

                else if (weatherStr.contains("thunderstorm")){
                    weather.setImageResource(R.drawable.thunderstorm);
                }

                else if (weatherStr.contains("rain")){
                    weather.setImageResource(R.drawable.rain);
                }

                else if (weatherStr.contains("mist")){
                    weather.setImageResource(R.drawable.mist);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void updateUI(int index) throws JSONException {
        String dateTime = weatherObject.getJSONArray("list").getJSONObject(index).getString("dt_txt");
        String[] dateTimeParts = dateTime.split(" ");
        String date1 = dateTimeParts[0];
        String time = dateTimeParts[1].substring(0, 5);

        // Extracting temperature and weather description
        String temperature = weatherObject.getJSONArray("list").getJSONObject(index).getJSONObject("main").getString("temp");
        String description = weatherObject.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(0).getString("description");

        // Setting AM/PM period
        int hour = Integer.parseInt(time.substring(0, 2));
        String period = (hour >= 12) ? "PM" : "AM";
        if (hour > 12) {
            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }

        // Update UI elements
        place.setText(weatherObject.getJSONObject("city").getString("name"));
        date.setText(date1 + "\t\t\t\t\t\t\t" + hour + time.substring(2) + " " + period);
        temp.setText(temperature + "°F");
        feelsLike.setText(description + ", feels like " + weatherObject.getJSONArray("list").getJSONObject(index).getJSONObject("main").getString("feels_like") + "°F");
    }
    public class ASync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String info;
            try {
                Log.d("aniiakkakka", "doInBackground");
                URL link = new URL("https://api.openweathermap.org/data/2.5/forecast?zip=" + strings[0] + "&appid=58ab4a74ffd431949eb72bea6c3de6a1&units=imperial");
                //URL link = new URL("https://api.openweathermap.org/data/2.5/forecast?zip=08540&appid=58ab4a74ffd431949eb72bea6c3de6a1&units=imperial");
                Log.d("aniiakkakka", "URL");
                URLConnection connection = link.openConnection();
                Log.d("aniiakkakka", "URLConnection");
                InputStream inputStream = connection.getInputStream();
                Log.d("aniiakkakka", "InputStreAM");
                BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream));
                Log.d("aniiakkakka", "bufferefReader");
                info = buff.readLine();
                weatherObject = new JSONObject(info);


            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


            return info;
        }

        @Override
        protected void onPostExecute(String info) {
            Log.d("aniiakkakka", "Changing rnnnn");

            try {
                String dateTime = weatherObject.getJSONArray("list").getJSONObject(0).getString("dt_txt");
                String[] dateTimeParts = dateTime.split(" ");
                String date1 = dateTimeParts[0];
                String time = dateTimeParts[1].substring(0, 5);

                int hour = Integer.parseInt(time.substring(0, 2));
                String period = (hour >= 12) ? "PM" : "AM";
                if (hour > 12) {
                    hour -= 12;
                } else if (hour == 0) {
                    hour = 12;
                }


                place.setText(weatherObject.getJSONObject("city").getString("name"));
                date.setText(date1 + "\t\t\t\t\t\t\t" + hour + time.substring(2) + " " + period);
                feelsLike.setText(weatherObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description") + ", feels like " + weatherObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like")+"°F");
                temp.setText(weatherObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp")+"°F");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.d("aniiakkakka", "Changed rnnn");

            String weatherStr = null;
            try {
                weatherStr = weatherObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            if (weatherStr.contains("clear")){
                weather.setImageResource(R.drawable.sun);
            }

            else if (weatherStr.contains("few")){
                weather.setImageResource(R.drawable.fewclouds);
            }

            else if (weatherStr.contains("broken")){
                weather.setImageResource(R.drawable.brokenclouds);
            }

            else if (weatherStr.contains("scattered")){
                weather.setImageResource(R.drawable.scatteredclouds);
            }

            else if (weatherStr.contains("snow")){
                weather.setImageResource(R.drawable.snow);
            }

            else if (weatherStr.contains("thunderstorm")){
                weather.setImageResource(R.drawable.thunderstorm);
            }

            else if (weatherStr.contains("rain")){
                weather.setImageResource(R.drawable.rain);
            }

            else if (weatherStr.contains("mist")){
                weather.setImageResource(R.drawable.mist);
            }

        }
    }
}