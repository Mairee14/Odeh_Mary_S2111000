package com.example.odeh_mary_s2111000;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView locationNameTextView;
    private ImageView weatherIconImageView;
    private TextView humidityTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;
    private TextView weatherConditionsTextView;
    private TextView latestObservationTextView;
    private TextView currentTimeTextView;
    private TextView weatherDateTextView;
    private TextView windDirectionTextView;
    private TextView recommendationTextView;
    private LinearLayout recommendationLayout;
    private TextView lastUpdatedTextView;

    private Handler handler;
    private Runnable timeUpdateRunnable;

    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        locationNameTextView = view.findViewById(R.id.locationName);
        humidityTextView = view.findViewById(R.id.humidityTextView);
        windDirectionTextView = view.findViewById(R.id.windDirectionTextView);
        pressureTextView = view.findViewById(R.id.pressureTextView);
        weatherConditionsTextView = view.findViewById(R.id.weatherConditionsTextView);
        latestObservationTextView = view.findViewById(R.id.latestObservationTextView);
        currentTimeTextView = view.findViewById(R.id.currentTimeTextView);
        recommendationTextView = view.findViewById(R.id.recommendationTextView);
        weatherDateTextView = view.findViewById(R.id.weatherDateTextView);
        recommendationLayout = view.findViewById(R.id.recommendationLayout);
        lastUpdatedTextView = view.findViewById(R.id.lastUpdatedTextView);


        sharedPreferences = requireContext().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nextTextView = view.findViewById(R.id.nextTextView);
        nextTextView.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ThreeDayForecastActivity.class);
            intent.putExtra("locationId", 2648579);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e("NextTextViewClick", "Error starting ThreeDayForecastActivity", e);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String location = getArguments().getString("location");
            int locationId = getArguments().getInt("locationId");

            fetchData(locationId);
        } else {
            fetchData(2648579);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void fetchData(int locationId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            if (isConnectedToInternet()) {
                try {
                    String forecastUrlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
                    String observationUrlString = "https://weather-broker-cdn.api.bbci.co.uk/en/observation/rss/" + locationId;

                    InputStream forecastInputStream = fetchDataFromUrl(forecastUrlString);
                    InputStream observationInputStream = fetchDataFromUrl(observationUrlString);

                    if (forecastInputStream != null && observationInputStream != null) {
                        List<weatherforecast> weatherItems = XMLPullParserHandler.parseWeatherData(forecastInputStream);
                        weatherforecast weatherForecast = weatherItems != null && !weatherItems.isEmpty() ? weatherItems.get(0) : null;

                        List<weatherforecast> observationItems = XMLPullParserHandler.parseWeatherData(observationInputStream);
                        weatherforecast latestObservation = observationItems != null && !observationItems.isEmpty() ? observationItems.get(0) : null;

                        requireActivity().runOnUiThread(() -> {
                            if (weatherForecast != null) {
                                locationNameTextView.setText(weatherForecast.getLocationName());
                                String weatherConditions = weatherForecast.getWeatherConditions();
                                weatherConditionsTextView.setText(weatherConditions);
                                showWeatherRecommendation(weatherConditions);
                                String weatherDate = weatherForecast.getFullPubDate();
                                weatherDateTextView.setText(weatherDate);
                            } else {
                                locationNameTextView.setText("Error fetching data");
                            }

                            if (latestObservation != null) {
                                String temperature = latestObservation.getCurrentTemperature();
                                latestObservationTextView.setText(temperature);
                                String humidity = latestObservation.getHumidity();
                                String windDirection = latestObservation.getWindDirection();
                                String pressure = latestObservation.getPressure();

                                humidityTextView.setText("Humidity: " + humidity + "%");
                                windDirectionTextView.setText("Wind Direction: " + windDirection);
                                pressureTextView.setText("Pressure: " + pressure + "mb");
                            }

                            updateLastUpdatedTime();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            locationNameTextView.setText("Error fetching data");
                        });
                    }
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        locationNameTextView.setText("Error fetching data");
                    });
                }
            } else {
                requireActivity().runOnUiThread(this::showNoInternetDialog);
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

    private void updateCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        currentTimeTextView.setText("Current Time: " + currentTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdatingTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdatingTime();
    }

    private void startUpdatingTime() {
        handler = new Handler();
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                handler.postDelayed(this, 60000); // Update every 60 seconds
            }
        };
        handler.post(timeUpdateRunnable);
    }

    private void stopUpdatingTime() {
        if (handler != null && timeUpdateRunnable != null) {
            handler.removeCallbacks(timeUpdateRunnable);
        }
    }

    public void updateRefreshInterval(int refreshInterval) {
        // TODO: Implement updating refresh interval logic
    }

    public void updateTemperatureUnit(String temperatureUnit) {
        // TODO: Implement updating temperature unit logic
    }

    private void showWeatherRecommendation(String weatherConditions) {
        String recommendation = "";

        if (weatherConditions.toLowerCase().contains("rain")) {
            recommendation = "It's raining! Don't forget to bring an umbrella or wear a jacket.";
        } else if (weatherConditions.toLowerCase().contains("sunny")) {
            recommendation = "It's a sunny day! Remember to apply sunscreen and stay hydrated.";
        } else if (weatherConditions.toLowerCase().contains("snow")) {
            recommendation = "It's snowing! Dress warmly and be cautious of slippery surfaces.";
        } else if (weatherConditions.toLowerCase().contains("cloudy")) {
            recommendation = "It's a cloudy day. Consider bringing a light jacket or sweater.";
        } else if (weatherConditions.toLowerCase().contains("windy")) {
            recommendation = "It's windy outside. Be prepared for strong gusts and secure loose items.";
        } else if (weatherConditions.toLowerCase().contains("fog")) {
            recommendation = "Foggy conditions are expected. Exercise caution while driving and allow extra time for travel.";
        } else if (weatherConditions.toLowerCase().contains("thunderstorm")) {
            recommendation = "Thunderstorms are forecasted. Stay indoors and avoid outdoor activities during the storm.";
        } else if (weatherConditions.toLowerCase().contains("hail")) {
            recommendation = "Hail is expected. Take shelter and protect yourself and your property from potential damage.";
        } else if (weatherConditions.toLowerCase().contains("hot")) {
            recommendation = "It's going to be a hot day. Stay hydrated, seek shade, and avoid prolonged exposure to the sun.";
        } else if (weatherConditions.toLowerCase().contains("cold")) {
            recommendation = "Cold weather is expected. Bundle up in warm layers and protect exposed skin.";
        } else if (weatherConditions.toLowerCase().contains("clear sky")) {
            recommendation = "It's a beautiful day with clear skies! Enjoy outdoor activities and soak up some sunshine.";
        }

        if (!recommendation.isEmpty()) {
            recommendationTextView.setText(recommendation);
            recommendationLayout.setVisibility(View.VISIBLE);
        } else {
            recommendationLayout.setVisibility(View.GONE);
        }
    }

    public void updateDefaultLocation(Object o) {
        // TODO: Implement updating default location logic
    }

    private void updateLastUpdatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' hh:mm a", Locale.getDefault());
        String lastUpdatedTime = sdf.format(new Date());
        lastUpdatedTextView.setText("Last Updated: " + lastUpdatedTime);
    }
}