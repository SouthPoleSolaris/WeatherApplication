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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherData";

    // UI elements
    private EditText editCity;
    private Button btnGetWeather;
    private TextView tvFeelsLike, tvTempMax, tvTempMin, tvPressure, tvHumidity, tvSeaLevel, tvGroundLevel;

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
                    Log.e(TAG, "Veuillez entrer une ville valide.");
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
                            double feelsLike = main.getDouble("feels_like");
                            double tempMin = main.getDouble("temp_min");
                            double tempMax = main.getDouble("temp_max");
                            int pressure = main.getInt("pressure");
                            int humidity = main.getInt("humidity");

                            double seaLevel = main.has("sea_level") ? main.getDouble("sea_level") : -1;
                            double groundLevel = main.has("grnd_level") ? main.getDouble("grnd_level") : -1;

                            // Mise à jour des TextViews
                            tvFeelsLike.setText("Température ressentie : " + feelsLike + "°C");
                            tvTempMax.setText("Température max : " + tempMax + "°C");
                            tvTempMin.setText("Température min : " + tempMin + "°C");
                            tvPressure.setText("Pression : " + pressure + " hPa");
                            tvHumidity.setText("Humidité : " + humidity + " %");
                            tvSeaLevel.setText("Niveau de la mer : " + (seaLevel != -1 ? seaLevel + " hPa" : "N/A"));
                            tvGroundLevel.setText("Niveau du sol : " + (groundLevel != -1 ? groundLevel + " hPa" : "N/A"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Erreur JSON : " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Erreur Volley : " + error.getMessage());
                    }
                });

        queue.add(jsonObjectRequest);
    }
}
