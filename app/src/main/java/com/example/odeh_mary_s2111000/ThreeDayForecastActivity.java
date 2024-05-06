package com.example.odeh_mary_s2111000;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreeDayForecastActivity extends AppCompatActivity implements WeeklyForecastAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "ThreeDayForecast";

    private TextView locationTextView;
    private TextView currentTemperatureTextView;
    private TextView weatherConditionsTextView;
    private ImageView weatherIconImageView;
    private RecyclerView weeklyForecastRecyclerView;
    private WeeklyForecastAdapter weeklyForecastAdapter;
    private Spinner locationSpinner;
    private Map<String, Integer> locationMap;

    private String selectedDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_day_forecast);

        locationTextView = findViewById(R.id.locationTextView);
        currentTemperatureTextView = findViewById(R.id.currentTemperatureTextView);
        weatherConditionsTextView = findViewById(R.id.weatherConditionsTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        weeklyForecastRecyclerView = findViewById(R.id.weeklyForecastRecyclerView);
        locationSpinner = findViewById(R.id.locationSpinner);

        initLocationMap();
        setupLocationSpinner();

        selectedDate = getIntent().getStringExtra("selectedDate");

        int initialLocationId = getIntent().getIntExtra("locationId", 0);
        fetchWeatherData(initialLocationId, selectedDate);
    }

    private void initLocationMap() {
        locationMap = new HashMap<>();
        locationMap.put("Glasgow", 2648579);
        locationMap.put("London", 2643743);
        locationMap.put("New York", 5128581);
        locationMap.put("Oman", 287286);
        locationMap.put("Mauritius", 934154);
        locationMap.put("Bangladesh", 1185241);
    }

    private void setupLocationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);
        locationSpinner.setOnItemSelectedListener(this);
    }

    private void fetchWeatherData(int locationId, String selectedDate) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                String urlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
                if (selectedDate != null) {
                    urlString += "?date=" + selectedDate;
                }
                InputStream inputStream = fetchDataFromUrl(urlString);
                if (inputStream != null) {
                    List<weatherforecast> weatherItems = XMLPullParserHandler.parseWeatherData(inputStream);
                    weatherforecast weatherForecast = !weatherItems.isEmpty() ? weatherItems.get(0) : null;

                    runOnUiThread(() -> {
                        if (weatherForecast != null) {
                            // Update UI with weather data
                            locationTextView.setText(weatherForecast.getLocationName());
                            currentTemperatureTextView.setText(weatherForecast.getCurrentTemperature() + "°C");
                            weatherConditionsTextView.setText(weatherForecast.getWeatherConditions());
                            int weatherIconResId = getWeatherIconResource(weatherForecast.getWeatherConditions());
                            weatherIconImageView.setImageResource(weatherIconResId);
                            setupRecyclerView(weatherItems);
                        } else {
                            locationTextView.setText("Error fetching data");
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        locationTextView.setText("Error fetching data");
                    });
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    locationTextView.setText("Error fetching data");
                });
            }
        });
    }

    private InputStream fetchDataFromUrl(String urlString) {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private void setupRecyclerView(List<weatherforecast> forecastList) {
        weeklyForecastAdapter = new WeeklyForecastAdapter(forecastList, this);
        weeklyForecastRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        weeklyForecastRecyclerView.setAdapter(weeklyForecastAdapter);
    }

    private int getWeatherIconResource(String weatherCondition) {
        if (weatherCondition != null) {
            if (weatherCondition.equalsIgnoreCase("Sunny")) {
                return R.drawable.sun;
            } else if (weatherCondition.equalsIgnoreCase("Rainy")) {
                return R.drawable.rain;
            } else if (weatherCondition.equalsIgnoreCase("Cloudy")) {
                return R.drawable.day_partial_cloud;
            }
        }
        return R.drawable.day_partial_cloud;
    }

    @Override
    public void onItemClick(weatherforecast forecast) {
        Intent intent = new Intent(this, DayForecastDetailsActivity.class);
        intent.putExtra("forecast", forecast);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedLocation = parent.getItemAtPosition(position).toString();
        int locationId = locationMap.get(selectedLocation);
        fetchWeatherData(locationId, selectedDate);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}