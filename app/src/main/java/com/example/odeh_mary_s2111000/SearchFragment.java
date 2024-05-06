package com.example.odeh_mary_s2111000;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView locationRecyclerView;
    private LocationAdapter locationAdapter;
    private ProgressBar progressBar;
    private Map<String, Integer> locationMap;
    private List<weatherforecast> weatherDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchView);
        locationRecyclerView = view.findViewById(R.id.locationListView);
        progressBar = view.findViewById(R.id.progressBar);

        weatherDataList = new ArrayList<>();
        locationAdapter = new LocationAdapter(weatherDataList);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        locationRecyclerView.setAdapter(locationAdapter);

        initLocationMap();
        setupSearchView();
        setupLocationRecyclerView();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button compareButton = view.findViewById(R.id.compareButton);
        compareButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CompareWeatherLocationActivity.class);
            startActivity(intent);
        });


        return view;
    }

    private void initLocationMap() {
        locationMap = new HashMap<>();
        locationMap.put("Glasgow", 2648579);
        locationMap.put("London", 2643743);
        locationMap.put("New York", 5128581);
        locationMap.put("Oman", 287286);
        locationMap.put("Mauritius", 934154);
        locationMap.put("Bangladesh", 1185241);

        // Fetch weather data for each location
        for (Map.Entry<String, Integer> entry : locationMap.entrySet()) {
            String location = entry.getKey();
            int locationId = entry.getValue();
            fetchWeatherData(location, locationId);
        }
    }

    private void fetchWeatherData(String locationName, int locationId) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            // Fetch weather data
            weatherforecast weatherData = new weatherforecast();
            weatherData.setLocationName(locationName);
            weatherData.setLocationId(locationId);
            weatherData.setWeatherConditions("Sunny");
            weatherData.setCurrentTemperature("25");
            weatherDataList.add(weatherData);
            locationAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            showErrorDialog("Error fetching weather data for " + locationName);
        } finally {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLocationSuggestions(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterLocationSuggestions(newText);
                return true;
            }
        });
    }

    private void filterLocationSuggestions(String query) {
        progressBar.setVisibility(View.VISIBLE);

        List<weatherforecast> filteredLocations = new ArrayList<>();

        if (query.isEmpty()) {
            // If the query is empty, show all locations
            filteredLocations.addAll(weatherDataList);
        } else {
            // Filter locations based on the user's input
            for (weatherforecast location : weatherDataList) {
                String locationName = location.getLocationName();
                if (locationName.toLowerCase().contains(query.toLowerCase())) {
                    filteredLocations.add(location);
                }
            }
        }

        if (filteredLocations.isEmpty() && !query.isEmpty()) {
            showErrorDialog("No matching locations found for '" + query + "'");
        }

        locationAdapter.setData(filteredLocations);
        locationAdapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
    }

    private void setupLocationRecyclerView() {
        locationRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(requireContext(), locationRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                weatherforecast selectedLocation = weatherDataList.get(position);
                String locationName = selectedLocation.getLocationName();
                int locationId = selectedLocation.getLocationId();

                if (getActivity() instanceof LocationSelectionListener) {
                    ((LocationSelectionListener) getActivity()).onLocationSelected(locationName, locationId);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Handle long click event if needed
            }
        }));
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public interface LocationSelectionListener {
        void onLocationSelected(String location, int locationId);
    }
}