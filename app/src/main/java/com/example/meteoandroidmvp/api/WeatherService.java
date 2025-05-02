package com.example.meteoandroidmvp.api;

import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface qui définit les points d'accès à l'API OpenWeatherMap
 * Utilisée par Retrofit pour générer automatiquement les méthodes d'appel
 */
public interface WeatherService {
    
    /**
     * Récupère les données météo actuelles pour une ville spécifiée par son nom
     * 
     * @param city Nom de la ville (ex: "Paris", "London,uk")
     * @param apiKey Clé API OpenWeatherMap
     * @param units Unités de mesure (metric, imperial, standard)
     * @param language Code langue pour les descriptions (fr, en, etc.)
     * @return Objet Call contenant la réponse météo
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByCity(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
    );
    
    /**
     * Récupère les données météo actuelles pour une position géographique
     * Cette méthode est plus précise que la recherche par nom de ville
     * 
     * @param latitude Latitude en degrés décimaux
     * @param longitude Longitude en degrés décimaux
     * @param apiKey Clé API OpenWeatherMap
     * @param units Unités de mesure (metric, imperial, standard)
     * @param language Code langue pour les descriptions (fr, en, etc.)
     * @return Objet Call contenant la réponse météo
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByLocation(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
    );
    
    /**
     * Récupère les prévisions météo sur 5 jours pour une ville spécifiée par son nom
     * Les prévisions sont retournées par intervalles de 3 heures
     * 
     * @param city Nom de la ville (ex: "Paris", "London,uk")
     * @param apiKey Clé API OpenWeatherMap
     * @param units Unités de mesure (metric, imperial, standard)
     * @param language Code langue pour les descriptions (fr, en, etc.)
     * @return Objet Call contenant la réponse des prévisions
     */
    @GET("forecast")
    Call<ForecastResponse> getForecastByCity(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
    );
    
    /**
     * Récupère les prévisions météo sur 5 jours pour une position géographique
     * Les prévisions sont retournées par intervalles de 3 heures
     * 
     * @param latitude Latitude en degrés décimaux
     * @param longitude Longitude en degrés décimaux
     * @param apiKey Clé API OpenWeatherMap
     * @param units Unités de mesure (metric, imperial, standard)
     * @param language Code langue pour les descriptions (fr, en, etc.)
     * @return Objet Call contenant la réponse des prévisions
     */
    @GET("forecast")
    Call<ForecastResponse> getForecastByLocation(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
    );
} 