package com.example.sebastian.sunshine.Model;

import java.util.ArrayList;

/**
 * Created by Sebastian on 23.12.2017.
 */

public class DailyWeatherReport {
    private String city;
    private String country;
    private int temp;
    private int min_temp;
    private int max_temp;
    private String weather;
    private String weatherDescription;
    private String date;

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    public DailyWeatherReport(String city, String country, int temp, int min_temp, int max_temp, String weather, String weatherDescription, String rawDate) {
        this.city = city;
        this.country = country;
        this.temp = temp;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.weather = weather;
        this.weatherDescription = weatherDescription;
        this.date = rawDatetoDate(rawDate);
    }

    public String rawDatetoDate (String rawDate){
        //convert raw date to a nice, clean string
        return "May 1";
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getTemp() {
        return temp;
    }

    public int getMin_temp() {
        return min_temp;
    }

    public int getMax_temp() {
        return max_temp;
    }

    public String getWeather() {
        return weather;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getDate() {
        return date;
    }
}