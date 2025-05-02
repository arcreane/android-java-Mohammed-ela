package com.example.meteoandroidmvp.contract;

import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;

/**
 * Contrat définissant les interactions entre la Vue et le Présentateur
 * selon le pattern MVP (Model-View-Presenter)
 * 
 * Ce contrat assure une séparation claire des responsabilités :
 * - La Vue s'occupe uniquement de l'affichage et des interactions utilisateur
 * - Le Présentateur contient la logique métier et contrôle les données
 */
public interface WeatherContract {
    
    /**
     * Interface que la Vue (fragment ou activité) doit implémenter
     * Elle définit comment le Présentateur peut communiquer avec la Vue
     */
    interface View {
        /**
         * Affiche les données météo actuelles
         * @param weatherResponse Réponse contenant les données météo
         */
        void showWeather(WeatherResponse weatherResponse);
        
        /**
         * Affiche les prévisions météo sur 5 jours
         * @param forecastResponse Réponse contenant les prévisions
         */
        void showForecast(ForecastResponse forecastResponse);
        
        /**
         * Affiche un message d'erreur à l'utilisateur
         * @param message Message d'erreur à afficher
         */
        void showError(String message);
        
        /**
         * Affiche un indicateur de chargement
         */
        void showLoading();
        
        /**
         * Cache l'indicateur de chargement
         */
        void hideLoading();
    }
    
    /**
     * Interface que le Présentateur doit implémenter
     * Elle définit comment la Vue peut communiquer avec le Présentateur
     */
    interface Presenter {
        /**
         * Attache une Vue au Présentateur
         * @param view Instance de la Vue
         */
        void attachView(View view);
        
        /**
         * Détache la Vue du Présentateur
         * Important pour éviter les fuites mémoire
         */
        void detachView();
        
        /**
         * Récupère les données météo actuelles pour une ville
         * @param city Nom de la ville
         */
        void getWeatherByCity(String city);
        
        /**
         * Récupère les données météo actuelles pour une position
         * @param latitude Latitude
         * @param longitude Longitude
         */
        void getWeatherByLocation(double latitude, double longitude);
        
        /**
         * Récupère les prévisions météo sur 5 jours pour une ville
         * @param city Nom de la ville
         */
        void getForecastByCity(String city);
        
        /**
         * Récupère les prévisions météo sur 5 jours pour une position
         * @param latitude Latitude
         * @param longitude Longitude
         */
        void getForecastByLocation(double latitude, double longitude);
    }
} 