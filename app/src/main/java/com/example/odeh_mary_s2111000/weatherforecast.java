package com.example.odeh_mary_s2111000;

import java.io.Serializable;

public class weatherforecast implements Serializable {
    private String description;
    private String pubDate;
    private String minTemperature;
    private String maxTemperature;
    private String wind;
    private String uvRisk;
    private String humidity;
    private String pollution;
    private String windDirection;
    private String pressure;
    private String weatherCondition;
    private String fullPubDate;
    private String locationName;
    private String currentTemperature;
    private int locationId;
    private String dayOfWeek;
    private String geoRssPoint;
    private String visibility;
    private String sunrise;
    private String sunset;
    private long timestamp;

    public weatherforecast() {
    }

    // Getters and setters for all fields

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    public String getMinTemperature() {
        return minTemperature;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getWind() {
        return wind;
    }

    public String getUvRisk() {
        return uvRisk;
    }

    public void setUvRisk(String uvRisk) {
        this.uvRisk = uvRisk;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPollution() {
        return pollution;
    }

    public void setPollution(String pollution) {
        this.pollution = pollution;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWeatherConditions() {
        return weatherCondition;
    }

    public void setWeatherConditions(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getFullPubDate() {
        return fullPubDate;
    }

    public void setFullPubDate(String fullPubDate) {
        this.fullPubDate = fullPubDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }



    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getGeoRssPoint() {
        return geoRssPoint;
    }

    public void setGeoRssPoint(String geoRssPoint) {
        this.geoRssPoint = geoRssPoint;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getwindDirection() {
        return null;
    }

    public Object getwindspeed() {
        return null;
    }


    public Object getWindspeed() {
        return null;
    }
}