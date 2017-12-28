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
    private String date;

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_THUNDERSTORM = "Thunderstorm";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    public DailyWeatherReport(String city, String country, int temp, int min_temp, int max_temp, String weather, String rawDate) {
        this.city = city;
        this.country = country;
        this.temp = temp;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
        this.weather = weather;
        this.date = rawDatetoDate(rawDate);
    }
    //convert raw date to a nice, clean string
    public String rawDatetoDate (String rawDate){

        // get the month and day out of the raw date
        String month = rawDate.substring(rawDate.indexOf("-")+1, rawDate.indexOf("-")+3);
        String day = rawDate.substring(rawDate.indexOf("-")+4, rawDate.indexOf("-")+7);

        // set the month to text
        String monthText;
        switch (month){
            case "01":
                monthText = "January";
                break;
            case "02":
                monthText = "February";
                break;
            case "03":
                monthText = "March";
                break;
            case "04":
                monthText = "April";
                break;
            case "05":
                monthText = "May";
                break;
            case "06":
                monthText = "June";
                break;
            case "07":
                monthText = "July";
                break;
            case "08":
                monthText = "August";
                break;
            case "09":
                monthText = "September";
                break;
            case "10":
                monthText = "October";
                break;
            case "11":
                monthText = "November";
                break;
            default:
                monthText = "December";
        }


        return monthText + " " + day;
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

    public String getDate() {
        return date;
    }
}
