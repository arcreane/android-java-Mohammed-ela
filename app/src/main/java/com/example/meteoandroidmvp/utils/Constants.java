package com.example.meteoandroidmvp.utils;

/**
 * Classe qui regroupe toutes les constantes utilisées dans l'application
 * Permet de centraliser les valeurs importantes et de faciliter leur modification
 */
public class Constants {
    /**
     * Clé API pour accéder aux services OpenWeatherMap
     * Dans un projet réel, cette clé devrait être stockée dans des variables d'environnement
     * ou obtenue depuis un serveur sécurisé
     */
    public static final String API_KEY = "";
    
    /**
     * Clé API pour accéder à Mistral AI
     * Dans un projet réel, cette clé devrait être stockée de manière sécurisée
     * et non en dur dans le code
     */
    public static final String MISTRAL_API_KEY = "";
    
    /**
     * Format d'URL pour récupérer les icônes météo
     * Le %s sera remplacé par le code de l'icône (ex: 01d, 02n, etc.)
     * Le @2x indique qu'on veut la version haute résolution
     */
    public static final String ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png";
    
    /**
     * Unités de mesure pour les données météo
     * 'metric' = température en Celsius, vent en m/s
     * 'imperial' = température en Fahrenheit, vent en mph
     */
    public static final String UNITS = "metric";
    
    /**
     * Code de langue pour les descriptions météo
     * Définit la langue des descriptions textuelles comme "ciel dégagé", "nuageux", etc.
     */
    public static final String LANGUAGE = "fr";
    
    /**
     * Ville utilisée par défaut si aucune ville n'est spécifiée
     * et que la localisation n'est pas disponible
     */
    public static final String DEFAULT_CITY = "Paris";
    
    /**
     * Code de requête pour les permissions de localisation
     * Utilisé pour identifier la réponse dans onRequestPermissionsResult
     */
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
} 