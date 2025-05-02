package com.example.meteoandroidmvp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("city")
    private City city;
    
    @SerializedName("list")
    private List<ForecastItem> forecastItems;
    
    public City getCity() {
        return city;
    }
    
    public List<ForecastItem> getForecastItems() {
        return forecastItems;
    }
    
    public static class City {
        @SerializedName("name")
        private String name;
        
        @SerializedName("country")
        private String country;
        
        @SerializedName("sunrise")
        private long sunrise;
        
        @SerializedName("sunset")
        private long sunset;
        
        public String getName() {
            return name;
        }
        
        public String getCountry() {
            return country;
        }
        
        public long getSunrise() {
            return sunrise;
        }
        
        public long getSunset() {
            return sunset;
        }
    }
    
    public static class ForecastItem {
        @SerializedName("dt")
        private long dateTime;
        
        @SerializedName("main")
        private WeatherResponse.Main main;
        
        @SerializedName("weather")
        private List<WeatherResponse.Weather> weather;
        
        @SerializedName("clouds")
        private Clouds clouds;
        
        @SerializedName("wind")
        private WeatherResponse.Wind wind;
        
        @SerializedName("visibility")
        private int visibility;
        
        @SerializedName("pop")
        private float probabilityOfPrecipitation;
        
        @SerializedName("dt_txt")
        private String dateTimeText;
        
        public long getDateTime() {
            return dateTime;
        }
        
        public WeatherResponse.Main getMain() {
            return main;
        }
        
        public List<WeatherResponse.Weather> getWeather() {
            return weather;
        }
        
        public Clouds getClouds() {
            return clouds;
        }
        
        public WeatherResponse.Wind getWind() {
            return wind;
        }
        
        public int getVisibility() {
            return visibility;
        }
        
        public float getProbabilityOfPrecipitation() {
            return probabilityOfPrecipitation;
        }
        
        public String getDateTimeText() {
            return dateTimeText;
        }
    }
    
    public static class Clouds {
        @SerializedName("all")
        private int cloudiness;
        
        public int getCloudiness() {
            return cloudiness;
        }
    }
} 