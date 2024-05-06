package com.example.odeh_mary_s2111000;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeeklyForecastAdapter extends RecyclerView.Adapter<WeeklyForecastAdapter.ViewHolder> {
    private List<weatherforecast> forecastList;
    private OnItemClickListener itemClickListener;

    public WeeklyForecastAdapter(List<weatherforecast> forecastList, OnItemClickListener itemClickListener) {
        this.forecastList = forecastList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_three_day_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        weatherforecast forecast = forecastList.get(position);
        holder.bind(forecast);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(forecast);
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dateTextView;
        private TextView dayOfWeekTextView;
        private ImageView weatherIconImageView;
        private TextView weatherConditionTextView;
        private TextView minTemperatureTextView;
        private TextView maxTemperatureTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeeks);
            weatherIconImageView = itemView.findViewById(R.id.weatherIcon);
            weatherConditionTextView = itemView.findViewById(R.id.weatherConditionsTextView);
            minTemperatureTextView = itemView.findViewById(R.id.minTemperature);
            maxTemperatureTextView = itemView.findViewById(R.id.maxTemperature);
        }

        public void bind(weatherforecast forecast) {
            dateTextView.setText(forecast.getFullPubDate());
            dayOfWeekTextView.setText(forecast.getDayOfWeek());
            weatherConditionTextView.setText(forecast.getWeatherConditions());
            minTemperatureTextView.setText(String.valueOf(forecast.getMinTemperature()));
            maxTemperatureTextView.setText(String.valueOf(forecast.getMaxTemperature()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(weatherforecast forecast);
    }
}