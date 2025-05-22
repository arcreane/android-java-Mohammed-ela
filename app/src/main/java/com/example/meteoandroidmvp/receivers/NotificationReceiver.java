package com.example.meteoandroidmvp.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.meteoandroidmvp.api.ApiClient;
import com.example.meteoandroidmvp.api.WeatherService;
import com.example.meteoandroidmvp.model.WeatherResponse;
import com.example.meteoandroidmvp.utils.Constants;
import com.example.meteoandroidmvp.utils.NotificationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Récepteur qui est déclenché par l'AlarmManager pour afficher les notifications météo quotidiennes
 * Il récupère les données météo à jour et affiche une notification avec des recommandations
 */
public class NotificationReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        // Récupérer les informations de la ville depuis l'intent
        String cityName = intent.getStringExtra("city_name");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        
        Log.d("NotificationReceiver", "Notification déclenchée pour: " + cityName);
        
        // Vérifier que les données sont valides avant de continuer
        if (cityName != null && !cityName.isEmpty()) {
            // Récupérer les données météo à jour pour cette ville
            fetchWeatherData(context, cityName, latitude, longitude);
        }
    }
    
    /**
     * Récupère les données météo actuelles pour afficher une notification pertinente
     */
    private void fetchWeatherData(Context context, String cityName, double latitude, double longitude) {
        // Initialiser le service API météo
        WeatherService weatherService = ApiClient.getClient().create(WeatherService.class);
        Call<WeatherResponse> call;
        
        // Utiliser les coordonnées si disponibles, sinon utiliser le nom de la ville
        if (latitude != 0 && longitude != 0) {
            // Les coordonnées donnent des résultats plus précis
            call = weatherService.getCurrentWeatherByLocation(
                    latitude,
                    longitude,
                    Constants.API_KEY,
                    Constants.UNITS,
                    Constants.LANGUAGE
            );
        } else {
            // Recherche par nom de ville en cas de coordonnées manquantes
            call = weatherService.getCurrentWeatherByCity(
                    cityName,
                    Constants.API_KEY,
                    Constants.UNITS,
                    Constants.LANGUAGE
            );
        }
        
        // Envoyer la requête à l'API météo
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Données météo reçues avec succès
                    WeatherResponse weatherData = response.body();
                    
                    // Extraire les informations importantes pour la notification
                    float temperature = weatherData.getMain().getTemperature();
                    String description = "";
                    if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
                        description = weatherData.getWeather().get(0).getDescription();
                    }
                    
                    // Récupérer d'autres données météo pour l'IA
                    float windSpeed = 0;
                    if (weatherData.getWind() != null) {
                        windSpeed = weatherData.getWind().getSpeed();
                    }
                    
                    int humidity = 0;
                    if (weatherData.getMain() != null) {
                        humidity = weatherData.getMain().getHumidity();
                    }
                    
                    // Afficher la notification avec les conseils vestimentaires
                    // en utilisant toutes les données disponibles
                    NotificationUtils.showWeatherClothingNotification(
                        context, 
                        cityName, 
                        temperature, 
                        description, 
                        windSpeed, 
                        humidity
                    );
                    
                    // Reprogrammer la notification pour le lendemain
                    // Cela se fait automatiquement si on utilise setRepeating, mais pour setExactAndAllowWhileIdle il faut le faire manuellement
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        // Vérifier si les notifications sont toujours activées pour cette ville
                        if (NotificationUtils.isNotificationEnabled(context, cityName)) {
                            // Vérifier la permission pour les alarmes exactes sur Android 12+
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                                    // On doit désactiver puis réactiver pour reprogrammer
                                    // C'est un peu un hack, mais ça marche bien
                                    NotificationUtils.toggleNotification(context, cityName, latitude, longitude);
                                    NotificationUtils.toggleNotification(context, cityName, latitude, longitude);
                                }
                            } else {
                                // Sur les versions d'Android plus anciennes, pas besoin de vérifier la permission
                                NotificationUtils.toggleNotification(context, cityName, latitude, longitude);
                                NotificationUtils.toggleNotification(context, cityName, latitude, longitude);
                            }
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // En cas d'erreur, on log mais on ne fait rien d'autre
                // Pas de notification d'erreur pour ne pas déranger l'utilisateur
                Log.e("NotificationReceiver", "Erreur lors de la récupération des données météo", t);
            }
        });
    }
} 