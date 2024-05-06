package com.example.odeh_mary_s2111000;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRefreshReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "WeatherPrefs";
    private static final String KEY_LOCATION_ID = "LocationId";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int locationId = sharedPreferences.getInt(KEY_LOCATION_ID, 0);

        if (locationId != 0) {
            fetchWeatherData(context, locationId);
        }
    }

    private void fetchWeatherData(Context context, int locationId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                String forecastUrlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
                String observationUrlString = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationId;

                InputStream forecastInputStream = fetchDataFromUrl(forecastUrlString);
                List<weatherforecast> weatherItems = XMLPullParserHandler.parseWeatherData(forecastInputStream);
                weatherforecast weatherForecast = weatherItems != null && !weatherItems.isEmpty() ? weatherItems.get(0) : null;

                InputStream observationInputStream = fetchDataFromUrl(observationUrlString);
                List<weatherforecast> observationItems = XMLPullParserHandler.parseWeatherData(observationInputStream);
                weatherforecast latestObservation = observationItems != null && !observationItems.isEmpty() ? observationItems.get(0) : null;

                // Save the updated weather data to SharedPreferences or perform any other necessary actions
                if (weatherForecast != null && latestObservation != null) {
                    saveWeatherData(context, weatherForecast, latestObservation);
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                Log.e("DataRefreshReceiver", "Error fetching weather data", e);
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

    private void saveWeatherData(Context context, weatherforecast weatherForecast, weatherforecast latestObservation) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the weather data to SharedPreferences
        editor.putString("LocationName", weatherForecast.getLocationName());
        editor.putString("WeatherConditions", weatherForecast.getWeatherConditions());
        editor.putString("WeatherDate", weatherForecast.getFullPubDate());
        editor.putString("Temperature", latestObservation.getCurrentTemperature());
        editor.putString("Humidity", latestObservation.getHumidity());
        editor.putString("WindDirection", latestObservation.getwindDirection());
        editor.putString("Pressure", latestObservation.getPressure());

        editor.apply();
    }
}