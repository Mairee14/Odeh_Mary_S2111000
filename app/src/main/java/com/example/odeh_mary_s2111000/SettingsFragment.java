package com.example.odeh_mary_s2111000;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Spinner temperatureUnitSpinner;
    private Switch darkModeSwitch;
    private Spinner refreshIntervalSpinner;
    private Switch notificationSwitch;
    private RadioGroup defaultLocationRadioGroup;
    private RadioButton currentLocationRadioButton;
    private RadioButton customLocationRadioButton;

    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String KEY_TEMPERATURE_UNIT = "TemperatureUnit";
    private static final String KEY_DARK_MODE = "DarkMode";
    private static final String KEY_REFRESH_INTERVAL = "RefreshInterval";
    private static final String KEY_NOTIFICATION_ENABLED = "NotificationEnabled";
    private static final String KEY_DEFAULT_LOCATION = "DefaultLocation";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Initialize views
        temperatureUnitSpinner = view.findViewById(R.id.temperature_unit_spinner);
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        refreshIntervalSpinner = view.findViewById(R.id.refresh_interval_spinner);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        defaultLocationRadioGroup = view.findViewById(R.id.default_location_radio_group);
        currentLocationRadioButton = view.findViewById(R.id.current_location_radio_button);
        customLocationRadioButton = view.findViewById(R.id.custom_location_radio_button);

        // Set listeners
        temperatureUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTemperatureUnit = parent.getItemAtPosition(position).toString();
                updateTemperatureUnit(selectedTemperatureUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableDarkMode();
            } else {
                disableDarkMode();
            }
        });

        refreshIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRefreshInterval = parent.getItemAtPosition(position).toString();
                updateRefreshInterval(selectedRefreshInterval);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateNotificationPreference(isChecked);
            }
        });

        defaultLocationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.current_location_radio_button) {
                    updateDefaultLocation(null);
                } else if (checkedId == R.id.custom_location_radio_button) {
                    // TODO: Implement custom location selection
                    Toast.makeText(requireContext(), "Custom location selection not implemented", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set initial values based on saved preferences
        String savedTemperatureUnit = sharedPreferences.getString(KEY_TEMPERATURE_UNIT, "Celsius");
        int temperatureUnitIndex = getIndex(temperatureUnitSpinner, savedTemperatureUnit);
        temperatureUnitSpinner.setSelection(temperatureUnitIndex);

        boolean savedDarkModePreference = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        darkModeSwitch.setChecked(savedDarkModePreference);

        String savedRefreshInterval = sharedPreferences.getString(KEY_REFRESH_INTERVAL, "1 hour");
        int refreshIntervalIndex = getIndex(refreshIntervalSpinner, savedRefreshInterval);
        refreshIntervalSpinner.setSelection(refreshIntervalIndex);

        boolean savedNotificationPreference = sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true);
        notificationSwitch.setChecked(savedNotificationPreference);

        String savedDefaultLocation = sharedPreferences.getString(KEY_DEFAULT_LOCATION, "Current Location");
        if (savedDefaultLocation.equals("Current Location")) {
            currentLocationRadioButton.setChecked(true);
        } else {
            customLocationRadioButton.setChecked(true);
        }

        return view;
    }

    private void updateTemperatureUnit(String temperatureUnit) {
        editor.putString(KEY_TEMPERATURE_UNIT, temperatureUnit);
        editor.apply();

        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentById(R.id.home);
        if (homeFragment != null) {
            homeFragment.updateTemperatureUnit(temperatureUnit);
        }
    }

    private void enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor.putBoolean(KEY_DARK_MODE, true);
        editor.apply();
    }

    private void disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.putBoolean(KEY_DARK_MODE, false);
        editor.apply();
    }

    private void updateRefreshInterval(String refreshInterval) {
        editor.putString(KEY_REFRESH_INTERVAL, refreshInterval);
        editor.apply();

        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentById(R.id.home);
        if (homeFragment != null) {
            int refreshIntervalInMinutes = getRefreshIntervalInMinutes(refreshInterval);
            homeFragment.updateRefreshInterval(refreshIntervalInMinutes);
        }
    }

    private void updateNotificationPreference(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled);
        editor.apply();

        // TODO: Implement notification preference update logic
        Toast.makeText(requireContext(), "Notification preference updated: " + enabled, Toast.LENGTH_SHORT).show();
    }

    private void updateDefaultLocation(Object o) {
        String defaultLocation = (o == null) ? "Current Location" : o.toString();
        editor.putString(KEY_DEFAULT_LOCATION, defaultLocation);
        editor.apply();

        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentById(R.id.home);
        if (homeFragment != null) {
            homeFragment.updateDefaultLocation(o);
        }
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private int getRefreshIntervalInMinutes(String refreshInterval) {
        switch (refreshInterval) {
            case "30 minutes":
                return 30;
            case "1 hour":
                return 60;
            case "2 hours":
                return 120;
            case "4 hours":
                return 240;
            default:
                return 60; // Default to 1 hour
        }
    }
}