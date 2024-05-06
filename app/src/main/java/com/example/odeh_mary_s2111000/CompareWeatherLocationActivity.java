package com.example.odeh_mary_s2111000;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompareWeatherLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner locationSpinner1;
    private Spinner locationSpinner2;
    private ImageView locationImageView1;
    private ImageView locationImageView2;
    private TextView weatherDataTextView1;
    private TextView weatherDataTextView2;
    private Button backButton;

    private Map<String, Integer> locationMap;
    private Map<String, Integer> locationImageMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_weather_location);

        locationSpinner1 = findViewById(R.id.locationSpinner1);
        locationSpinner2 = findViewById(R.id.locationSpinner2);
        locationImageView1 = findViewById(R.id.locationImageView1);
        locationImageView2 = findViewById(R.id.locationImageView2);
        weatherDataTextView1 = findViewById(R.id.weatherDataTextView1);
        weatherDataTextView2 = findViewById(R.id.weatherDataTextView2);
        backButton = findViewById(R.id.backButton);

        initLocationMap();
        initLocationImageMap();
        setupLocationSpinners();

        backButton.setOnClickListener(v -> onBackPressed());
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

    private void initLocationImageMap() {
        locationImageMap = new HashMap<>();
        locationImageMap.put("Glasgow", R.drawable.day_partial_cloud);
        locationImageMap.put("London", R.drawable.sun);
        locationImageMap.put("New York", R.drawable.day_rain_thunder);
        locationImageMap.put("Oman", R.drawable.day_rain);
        locationImageMap.put("Mauritius", R.drawable.night_rain);
        locationImageMap.put("Bangladesh", R.drawable.sun);
    }

    private void setupLocationSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationMap.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner1.setAdapter(adapter);
        locationSpinner2.setAdapter(adapter);
        locationSpinner1.setOnItemSelectedListener(this);
        locationSpinner2.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedLocation1 = locationSpinner1.getSelectedItem().toString();
        String selectedLocation2 = locationSpinner2.getSelectedItem().toString();
        int locationId1 = locationMap.get(selectedLocation1);
        int locationId2 = locationMap.get(selectedLocation2);

        fetchWeatherData(selectedLocation1, locationId1, locationImageView1, weatherDataTextView1);
        fetchWeatherData(selectedLocation2, locationId2, locationImageView2, weatherDataTextView2);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private void fetchWeatherData(String location, int locationId, ImageView imageView, TextView textView) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                String urlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
                InputStream inputStream = fetchDataFromUrl(urlString);
                weatherforecast weatherData = XMLPullParserHandler.parseWeatherData(inputStream).get(0);

                runOnUiThread(() -> {
                    imageView.setImageResource(locationImageMap.get(location));

                    String weatherInfo = location + ":\n" +
                            "Weather Conditions: " + weatherData.getWeatherConditions() + "\n" +
                            "Temperature: " + weatherData.getCurrentTemperature() + "°\n" +
                            "Humidity: " + weatherData.getHumidity() + "%\n" +
                            "Wind Direction: " + weatherData.getwindDirection() + "\n" +
                            "Pressure: " + weatherData.getPressure() + " mb";
                    textView.setText(weatherInfo);
                });
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        });
    }

    private InputStream fetchDataFromUrl(String urlString) throws IOException {
        return new java.net.URL(urlString).openStream();
    }
}