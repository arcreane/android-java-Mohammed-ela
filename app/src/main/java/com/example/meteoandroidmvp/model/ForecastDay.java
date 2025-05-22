package com.example.meteoandroidmvp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle représentant un jour de prévisions comprenant:
 * - Un titre pour le jour (ex: "Aujourd'hui", "Demain", "Lundi", etc.)
 * - Une liste des prévisions horaires pour ce jour
 */
public class ForecastDay {
    // Titre du jour (ex: "Aujourd'hui", "Demain", "Lundi 17 juin")
    private String dayTitle;
    
    // Liste des prévisions horaires pour ce jour
    private List<ForecastResponse.ForecastItem> hourlyForecasts;
    
    /**
     * Constructeur pour créer un nouveau jour de prévisions
     * 
     * @param dayTitle Titre du jour à afficher
     */
    public ForecastDay(String dayTitle) {
        this.dayTitle = dayTitle;
        this.hourlyForecasts = new ArrayList<>();
    }
    
    /**
     * Getter pour le titre du jour
     * 
     * @return Titre du jour
     */
    public String getDayTitle() {
        return dayTitle;
    }
    
    /**
     * Setter pour le titre du jour
     * 
     * @param dayTitle Nouveau titre du jour
     */
    public void setDayTitle(String dayTitle) {
        this.dayTitle = dayTitle;
    }
    
    /**
     * Getter pour la liste des prévisions horaires
     * 
     * @return Liste des prévisions horaires
     */
    public List<ForecastResponse.ForecastItem> getHourlyForecasts() {
        return hourlyForecasts;
    }
    
    /**
     * Setter pour la liste des prévisions horaires
     * 
     * @param hourlyForecasts Nouvelle liste des prévisions horaires
     */
    public void setHourlyForecasts(List<ForecastResponse.ForecastItem> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
    }
    
    /**
     * Ajoute une prévision horaire à la liste
     * 
     * @param forecastItem Prévision horaire à ajouter
     */
    public void addHourlyForecast(ForecastResponse.ForecastItem forecastItem) {
        if (hourlyForecasts == null) {
            hourlyForecasts = new ArrayList<>();
        }
        hourlyForecasts.add(forecastItem);
    }
} 