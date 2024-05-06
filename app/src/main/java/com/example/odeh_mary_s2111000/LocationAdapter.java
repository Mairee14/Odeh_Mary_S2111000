package com.example.odeh_mary_s2111000;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<weatherforecast> locationList;

    public LocationAdapter(List<weatherforecast> locationList) {
        this.locationList = locationList;
    }

    public void setData(List<weatherforecast> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        weatherforecast weatherData = locationList.get(position);
        holder.locationNameTextView.setText(weatherData.getLocationName());
        holder.weatherConditionsTextView.setText(weatherData.getWeatherConditions());
        holder.minTemperatureTextView.setText(weatherData.getMinTemperature());
        holder.weatherIconImageView.setImageResource(getWeatherIconResource(weatherData.getWeatherConditions()));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    private int getWeatherIconResource(String weatherCondition) {
        // Map weather conditions to appropriate icon resources
        // Replace with your own logic to determine the weather icon based on the condition
        if (weatherCondition != null) {
            if (weatherCondition.equalsIgnoreCase("Sunny")) {
                return R.drawable.sun;
            } else if (weatherCondition.equalsIgnoreCase("Rainy")) {
                return R.drawable.rain;
            } else if (weatherCondition.equalsIgnoreCase("Cloudy")) {
                return R.drawable.day_partial_cloud;
            }
        }
        // Default icon
        return R.drawable.rain;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationNameTextView;
        TextView weatherConditionsTextView;
        TextView minTemperatureTextView;
        ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.locationName);
            weatherConditionsTextView = itemView.findViewById(R.id.weatherConditionsTextView);
            minTemperatureTextView = itemView.findViewById(R.id.minTemperature);
            weatherIconImageView = itemView.findViewById(R.id.weatherIcon);
        }
    }
}