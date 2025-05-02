package com.example.meteoandroidmvp.model;

import java.util.Date;

public class FavoriteCity {
    private String cityName;
    private String country;
    private Date lastUpdate;
    private double latitude;
    private double longitude;
    private boolean fromLocation;

    public FavoriteCity(String cityName, String country, Date lastUpdate, double latitude, double longitude, boolean fromLocation) {
        this.cityName = cityName;
        this.country = country;
        this.lastUpdate = lastUpdate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fromLocation = fromLocation;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(boolean fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getFullName() {
        if (country != null && !country.isEmpty()) {
            return cityName + ", " + country;
        }
        return cityName;
    }
} 