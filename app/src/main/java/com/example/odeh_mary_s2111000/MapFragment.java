package com.example.odeh_mary_s2111000;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1001;

    private GoogleMap mMap;
    private AutoCompleteTextView searchView;
    private ImageView irv;
    private Button zoomInButton;
    private Button zoomOutButton;
    private ArrayAdapter<String> autoCompleteAdapter;
    private Map<String, Integer> locationIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        searchView = view.findViewById(R.id.searchView);
        ImageView micButton = view.findViewById(R.id.micButton);
        irv = view.findViewById(R.id.irv);
        zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomOutButton = view.findViewById(R.id.zoom_out_button);

        List<String> locations = new ArrayList<>();
        locations.add("Glasgow");
        locations.add("London");
        locations.add("New York");
        locations.add("Oman");
        locations.add("Mauritius");
        locations.add("Bangladesh");

        autoCompleteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);

        locationIds = new HashMap<>();
        locationIds.put("Glasgow", 2648579);
        locationIds.put("London", 2643743);
        locationIds.put("New York", 5128581);
        locationIds.put("Oman", 287286);
        locationIds.put("Mauritius", 934154);
        locationIds.put("Bangladesh", 1185241);
        locationIds.put("Default", -1); // Add a default case

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        irv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        zoomInButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        searchView.setAdapter(autoCompleteAdapter);
        searchView.setThreshold(1);
        searchView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLocation = autoCompleteAdapter.getItem(position);
            searchLocation(selectedLocation);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers for different locations
        addMarkers();

        // Set the initial camera position to the user's current location (Mauritius)
        LatLng userLocation = new LatLng(-20.1619, 57.4989);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 5));

        // Set up marker click listener
        mMap.setOnMarkerClickListener(this);

        // Show the user's location on the map
        showUserLocation();
    }

    private void addMarkers() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(55.8652, -4.2576)).title("Glasgow"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(51.5085, -0.1257)).title("London"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.7143, -74.006)).title("New York"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(23.6139, 58.5922)).title("Oman"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-20.1619, 57.4989)).title("Mauritius"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(23.7104, 90.4074)).title("Bangladesh"));
    }

    private void searchLocation(String query) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                List<Address> addresses = geocoder.getFromLocationName(query, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                    // Clear previous marker on the main thread
                    requireActivity().runOnUiThread(() -> mMap.clear());

                    // Add a marker for the searched location on the main thread
                    requireActivity().runOnUiThread(() -> {
                        mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(query));

                        // Move the camera to the searched location on the main thread
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                    });
                } else {
                    // Display a message when the location is not found
                    showLocationNotFoundDialog(query);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Display an error message when an exception occurs
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error searching location", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLocationNotFoundDialog(String location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Location Not Found")
                .setMessage("The location '" + location + "' could not be found.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String locationName = marker.getTitle();
        Integer locationId = locationIds.get(locationName);
        if (locationId != null) {
            fetchWeatherData(locationName, locationId); // Fetch weather data for the clicked marker
        } else {
            Log.e(TAG, "onMarkerClick: locationId not found for location: " + locationName);
        }
        return true;
    }

    private void fetchWeatherData(String locationName, int locationId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String urlString = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + locationId;
            InputStream inputStream = fetchDataFromUrl(urlString);
            try {
                List<weatherforecast> weatherItems = XMLPullParserHandler.parseWeatherData(inputStream);
                Log.d(TAG, "fetchWeatherData: WeatherItems size: " + weatherItems.size());
                weatherforecast weatherForecast = !weatherItems.isEmpty() ? weatherItems.get(0) : null;
                if (weatherForecast != null) {
                    Log.d(TAG, "fetchWeatherData: WeatherForecast: " + weatherForecast);
                    requireActivity().runOnUiThread(() -> showWeatherDialog(locationName, weatherForecast));
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "No weather data available", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error parsing weather data", Toast.LENGTH_SHORT).show();
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

    private void showWeatherDialog(String locationName, weatherforecast weatherForecast) {
        Log.d(TAG, "showWeatherDialog: LocationName: " + locationName);
        Log.d(TAG, "showWeatherDialog: WeatherForecast: " + weatherForecast);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Weather Forecast")
                .setMessage(
                        "Location: " + locationName + "\n" +
                                "Min Temp: " + weatherForecast.getMinTemperature() + "°C\n" +
                                "Max Temp: " + weatherForecast.getMaxTemperature() + "°C\n" +
                                "Humidity: " + weatherForecast.getHumidity() + "%"
                )
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showUserLocation() {
        // Check if the app has the necessary location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the location permissions if they are not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            // Location permissions are granted, so we can get the user's current location
            getLocationAndDisplayOnMap();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndDisplayOnMap() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // Clear any existing marker
                mMap.clear();

                // Add a marker for the user's location
                mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .title("Your Location"));

                // Move the camera to the user's location with a suitable zoom level
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions are granted, so we can get the user's current location
                getLocationAndDisplayOnMap();
            } else {
                // Location permissions are not granted, so we can't display the user's location
                Toast.makeText(requireContext(), "Location permission is required to display your location on the map.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}