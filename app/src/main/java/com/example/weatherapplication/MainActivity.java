package com.example.weatherapplication;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherData";

    // UI elements
    private EditText editCity;
    private Button btnGetWeather;
    private TextView tvFeelsLike, tvTempMax, tvTempMin, tvPressure, tvHumidity, tvSeaLevel, tvGroundLevel;
    TextView sunriseTextView, sunsetTextView, localTimeTextView;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des éléments UI
        editCity = findViewById(R.id.editCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);

        tvFeelsLike = findViewById(R.id.tvFeelsLike);
        tvTempMax = findViewById(R.id.tvTempMax);
        tvTempMin = findViewById(R.id.tvTempMin);
        tvPressure = findViewById(R.id.tvPressure);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvSeaLevel = findViewById(R.id.tvSeaLevel);
        tvGroundLevel = findViewById(R.id.tvGroundLevel);
        sunriseTextView = findViewById(R.id.sunriseTextView);
        sunsetTextView = findViewById(R.id.sunsetTextView);
        localTimeTextView = findViewById(R.id.localTimeTextView);


        // Initialisation de la file d'attente Volley
        queue = Volley.newRequestQueue(this);

        // Gestion du clic sur le bouton
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchWeatherData(city);
                } else {
                    Log.e(TAG, "Enter a valid city.");
                }
            }
        });
    }

    private void fetchWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=1190005683b09100801e34f876658cac&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject main = response.getJSONObject("main");
                            JSONObject sys = response.getJSONObject("sys");

                            double feelsLike = main.getDouble("feels_like");
                            double tempMin = main.getDouble("temp_min");
                            double tempMax = main.getDouble("temp_max");
                            int pressure = main.getInt("pressure");
                            int humidity = main.getInt("humidity");

                            double seaLevel = main.has("sea_level") ? main.getDouble("sea_level") : -1;
                            double groundLevel = main.has("grnd_level") ? main.getDouble("grnd_level") : -1;
                            long sunriseTimestamp = sys.getLong("sunrise");
                            long sunsetTimestamp = sys.getLong("sunset");

                            int timezoneOffset = response.getInt("timezone");

                            String sunriseTime = formatTime(sunriseTimestamp, timezoneOffset);
                            String sunsetTime = formatTime(sunsetTimestamp, timezoneOffset);
                            String localTime = formatTime(System.currentTimeMillis() / 1000, timezoneOffset);

                            // Mise à jour des TextViews
                            tvFeelsLike.setText("Feels like : " + feelsLike + "°C");
                            tvTempMax.setText("Maximum temperature : " + tempMax + "°C");
                            tvTempMin.setText("Minimum temperature : " + tempMin + "°C");
                            tvPressure.setText("Pressure : " + pressure + " hPa");
                            tvHumidity.setText("Humidity : " + humidity + " %");
                            tvSeaLevel.setText("Sea level : " + (seaLevel != -1 ? seaLevel + " hPa" : "N/A"));
                            tvGroundLevel.setText("Ground level : " + (groundLevel != -1 ? groundLevel + " hPa" : "N/A"));
                            sunriseTextView.setText(String.format("Sunrise: %s", sunriseTime));
                            sunsetTextView.setText(String.format("Sunset: %s", sunsetTime));
                            localTimeTextView.setText(String.format("Local Time: %s", localTime));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error with JSON : " + e.getMessage());
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error with Volley : " + error.getMessage());
                    }
                });

        queue.add(jsonObjectRequest);
    }


    private String formatTime(long timestamp, int timezoneOffset) {
        Date date = new Date((timestamp + timezoneOffset) * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }
}
