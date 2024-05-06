package com.example.odeh_mary_s2111000;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements SearchFragment.LocationSelectionListener {
    BottomNavigationView bottomNavigationView;
   HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    MapFragment mapFragment = new MapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";
                if (item.getItemId() == R.id.home) {
                    selectedFragment = homeFragment;
                    title = "Home";
                } else if (item.getItemId() == R.id.search) {
                    selectedFragment = searchFragment;
                    title = "Search";
                } else if (item.getItemId() == R.id.map) {
                    selectedFragment = mapFragment;
                    title = "Map";
                } else if (item.getItemId() == R.id.settings) {
                    selectedFragment = settingsFragment;
                    title = "Settings";
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
                setTitle(title);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.favorite) {
            // Handle favorite menu item click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationSelected(String location, int locationId) {
        HomeFragment newHomeFragment = new HomeFragment();



















































        Bundle bundle = new Bundle();
        bundle.putString("location", location);
        bundle.putInt("locationId", locationId);
        newHomeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, newHomeFragment)
                .commit();
    }
}