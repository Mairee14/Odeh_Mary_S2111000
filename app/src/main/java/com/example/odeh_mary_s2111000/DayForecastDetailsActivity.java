package com.example.odeh_mary_s2111000;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DayForecastDetailsActivity extends AppCompatActivity {
    private TextView locationNameTextView;
    private TextView dayOfWeekTextView;
    private TextView dateTextView;
    private ImageView weatherIconImageView;
    private TextView weatherConditionTextView;
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView windspeedTextView;
    private TextView pressureTextView;
    private TextView uvRiskTextView;
    private TextView pollutionTextView;
    private TextView visibilityTextView;
    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private Button selectDateButton;

    private Calendar selectedDate;

    @SuppressLint({"StringFormatMatches", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_forecast_details);

        // Initialize views
        locationNameTextView = findViewById(R.id.locationNameTextView);
        dayOfWeekTextView = findViewById(R.id.dayOfWeekTextView);
        dateTextView = findViewById(R.id.dateTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        weatherConditionTextView = findViewById(R.id.weatherConditionTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windspeedTextView = findViewById(R.id.windspeedTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        uvRiskTextView = findViewById(R.id.uvRiskTextView);
        pollutionTextView = findViewById(R.id.pollutionTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);
        sunriseTextView = findViewById(R.id.sunriseTextView);
        sunsetTextView = findViewById(R.id.sunsetTextView);
        selectDateButton = findViewById(R.id.selectDateButton);

        selectedDate = Calendar.getInstance();

        weatherforecast forecast = (weatherforecast) getIntent().getSerializableExtra("forecast");
        String selectedDateString = getIntent().getStringExtra("selectedDate");

        updateUI(forecast);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
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
                            updateUI(weatherForecast);
                        } else {
                            // Show error message
                            locationNameTextView.setText("Error fetching data");
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        // Show error message
                        locationNameTextView.setText("Error fetching data");
                    });
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // Show error message
                    locationNameTextView.setText("Error fetching data");
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

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = new SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                                .format(selectedDate.getTime());

                        // Fetch weather data for the selected date
                        int locationId = getIntent().getIntExtra("locationId", 0);
                        fetchWeatherData(locationId, formattedDate);
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateUI(weatherforecast forecast) {
        if (forecast != null) {
            locationNameTextView.setText(forecast.getLocationName());
            dayOfWeekTextView.setText(forecast.getDayOfWeek());
            dateTextView.setText(forecast.getFullPubDate());
            // Set the weather icon based on the weather condition
            // weatherIconImageView.setImageResource(getWeatherIconResource(forecast.getWeatherConditions()));
            weatherConditionTextView.setText(forecast.getWeatherConditions());
            temperatureTextView.setText(getString(R.string.temperature_format, forecast.getMinTemperature(), forecast.getMaxTemperature()));
            humidityTextView.setText(getString(R.string.humidity_format, forecast.getHumidity()));
            windspeedTextView.setText(getString(R.string.windspeed_format, forecast.getWindspeed()));
            pressureTextView.setText(getString(R.string.pressure_format, forecast.getPressure()));
            uvRiskTextView.setText(getString(R.string.uv_risk_format, forecast.getUvRisk()));
            pollutionTextView.setText(getString(R.string.pollution_format, forecast.getPollution()));
            visibilityTextView.setText(getString(R.string.visibility_format, forecast.getVisibility()));
            sunriseTextView.setText(getString(R.string.sunrise_format, forecast.getSunrise()));
            sunsetTextView.setText(getString(R.string.sunset_format, forecast.getSunset()));
        }
    }
}