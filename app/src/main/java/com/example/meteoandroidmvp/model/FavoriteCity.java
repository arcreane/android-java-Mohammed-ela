package com.example.meteoandroidmvp.model;

import java.util.Date;

/**
 * Modèle de données représentant une ville favorite pour l'utilisateur
 * Stocke toutes les informations nécessaires pour retrouver les données météo de cette ville
 */
public class FavoriteCity {
    // Nom de la ville (ex: "Paris")
    private String cityName;
    
    // Code pays (ex: "FR")
    private String country;
    
    // Date de la dernière consultation des données météo pour cette ville
    private Date lastUpdate;
    
    // Coordonnées géographiques
    private double latitude;
    private double longitude;
    
    // Indique si cette ville a été ajoutée via la localisation de l'appareil
    private boolean fromLocation;

    /**
     * Constructeur complet avec tous les champs
     * 
     * @param cityName Nom de la ville
     * @param country Code pays à 2 lettres
     * @param lastUpdate Date de dernière mise à jour
     * @param latitude Latitude en degrés décimaux
     * @param longitude Longitude en degrés décimaux
     * @param fromLocation True si ajoutée via localisation GPS
     */
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

    /**
     * Retourne le nom complet de la ville avec son pays
     * Format: "Paris, FR"
     * 
     * @return Nom formaté de la ville
     */
    public String getFullName() {
        if (country != null && !country.isEmpty()) {
            return cityName + ", " + country;
        }
        return cityName;
    }
} 