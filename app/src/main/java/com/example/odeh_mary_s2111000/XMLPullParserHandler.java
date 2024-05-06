package com.example.odeh_mary_s2111000;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XMLPullParserHandler {
    private static final String TAG = "XMLPullParserHandler";
    private static final String TAG_ITEM = "item";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_PUB_DATE = "pubDate";
    private static final String TAG_GEO_RSS_POINT = "georss:point";
    private static final String TAG_CURRENT_TEMPERATURE = "Current Temperature";
    private static final String TAG_MIN_TEMPERATURE = "Minimum Temperature";
    private static final String TAG_MAX_TEMPERATURE = "Maximum Temperature";
    private static final String TAG_WIND_DIRECTION = "Wind Direction";
    private static final String TAG_WIND_SPEED = "Wind Speed";
    private static final String TAG_VISIBILITY = "Visibility";
    private static final String TAG_PRESSURE = "Pressure";
    private static final String TAG_HUMIDITY = "Humidity";
    private static final String TAG_UV_RISK = "UV Risk";
    private static final String TAG_POLLUTION = "Pollution";

    public static List<weatherforecast> parseWeatherData(InputStream inputStream) throws XmlPullParserException, IOException {
        List<weatherforecast> weatherItems = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        weatherforecast currentItem = null;
        String cityName = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (TAG_ITEM.equals(tagName)) {
                        currentItem = new weatherforecast();
                        if (cityName != null) {
                            currentItem.setLocationName(cityName);
                        }
                    } else if (TAG_TITLE.equals(tagName)) {
                        String title = parser.nextText();
                        if (cityName == null) {
                            cityName = extractCityName(title);
                            Log.d(TAG, "City Name: " + cityName);
                        } else if (currentItem != null) {
                            parseTitle(currentItem, title);
                        }
                    } else if (currentItem != null) {
                        if (TAG_DESCRIPTION.equals(tagName)) {
                            String description = parser.nextText();
                            parseDescription(currentItem, description);
                        } else if (TAG_PUB_DATE.equals(tagName)) {
                            String pubDate = parser.nextText();
                            currentItem.setPubDate(pubDate);
                            Log.d(TAG, "Parsed pubDate: " + pubDate);

                            // Extract date in the format "Sat, 27 April 2024" from pubDate
                            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault());
                            try {
                                Date date = inputFormat.parse(pubDate);
                                String formattedDate = outputFormat.format(date);
                                currentItem.setFullPubDate(formattedDate);
                                Log.d(TAG, "Extracted date: " + formattedDate);
                            } catch (ParseException e) {
                                Log.e(TAG, "Error parsing pubDate: " + pubDate, e);
                            }
                        } else if (TAG_GEO_RSS_POINT.equals(tagName)) {
                            String geoRssPoint = parser.nextText();
                            currentItem.setGeoRssPoint(geoRssPoint);
                            Log.d(TAG, "Parsed geoRssPoint: " + geoRssPoint);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (TAG_ITEM.equals(parser.getName()) && currentItem != null) {
                        weatherItems.add(currentItem);
                        Log.d(TAG, "Parsed item: " + currentItem.toString());
                        currentItem = null;
                    }
                    break;
                default:
                    // Do nothing
            }
            eventType = parser.next();
        }
        return weatherItems;
    }

    private static void parseTitle(weatherforecast weatherForecast, String title) {

        int colonIndex = title.indexOf(':');
        if (colonIndex != -1) {
            String dayOfWeek = title.substring(0, colonIndex).trim();
            weatherForecast.setDayOfWeek(dayOfWeek);
            Log.d(TAG, "Extracted Day of Week: " + dayOfWeek);

            String[] tokens = title.substring(colonIndex + 2).split(", ");
            if (tokens.length > 0) {
                weatherForecast.setWeatherConditions(tokens[0]);
                Log.d(TAG, "Extracted Weather Condition: " + tokens[0]);
            }
        }
    }

    private static void parseDescription(weatherforecast weatherForecast, String description) {
        String[] tokens = description.split(", ");
        for (String token : tokens) {
            if (token.startsWith(TAG_CURRENT_TEMPERATURE)) {
                weatherForecast.setCurrentTemperature(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Current Temperature: " + extractValue(token));
            } else if (token.startsWith(TAG_MIN_TEMPERATURE)) {
                weatherForecast.setMinTemperature(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Min Temperature: " + extractValue(token));
            } else if (token.startsWith(TAG_MAX_TEMPERATURE)) {
                weatherForecast.setMaxTemperature(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Max Temperature: " + extractValue(token));
            } else if (token.startsWith(TAG_WIND_DIRECTION)) {
                weatherForecast.setWindDirection(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Wind Direction: " + extractValue(token));
            } else if (token.startsWith(TAG_WIND_SPEED)) {

            } else if (token.startsWith(TAG_PRESSURE)) {
                weatherForecast.setPressure(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Pressure: " + extractValue(token));
            } else if (token.startsWith(TAG_HUMIDITY)) {
                weatherForecast.setHumidity(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Humidity: " + extractValue(token));
            } else if (token.startsWith(TAG_UV_RISK)) {
                weatherForecast.setUvRisk(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted UV Risk: " + extractValue(token));
            } else if (token.startsWith(TAG_POLLUTION)) {
                weatherForecast.setPollution(extractValue(token));
                Log.d("XMLPullParserHandler", "Extracted Pollution: " + extractValue(token));
            }
        }

        String[] descriptionTokens = description.split(",");
        for (String token : descriptionTokens) {
            if (token.contains("Temperature")) {
                String temperatureValue = token.split(":")[1].trim();
                weatherForecast.setCurrentTemperature(temperatureValue);
            }
        }
    }

    private static String extractValue(String token) {
        // Extract the value part from a token
        int index = token.indexOf(':');
        return token.substring(index + 2);
    }

    private static String extractCityName(String title) {
        int startIndex = title.indexOf("Forecast for") + "Forecast for".length() + 1;
        int endIndex = title.indexOf(',', startIndex);
        return title.substring(startIndex, endIndex).trim();
    }
}