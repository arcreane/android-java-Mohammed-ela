package com.example.meteoandroidmvp.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.meteoandroidmvp.MainActivity;
import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.receivers.NotificationReceiver;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe utilitaire qui gère toutes les fonctionnalités liées aux notifications météo
 * Elle permet de créer, planifier, annuler et afficher des notifications
 */
public class NotificationUtils {
    
    // Identifiant du canal de notification, obligatoire depuis Android 8
    public static final String CHANNEL_ID = "weather_channel";
    
    // Nom du fichier de préférences pour stocker les villes avec notifications activées
    private static final String PREFERENCES_NAME = "MeteoNotifPrefs";
    
    // Clé pour accéder à la liste des villes dans les préférences
    private static final String NOTIFIED_CITIES_KEY = "notified_cities";
    
    /**
     * Crée le canal de notification requis par Android 8.0+
     * Cette méthode doit être appelée au démarrage de l'application
     */
    public static void createNotificationChannel(Context context) {
        // On vérifie qu'on est sur Android 8 (Oreo) ou plus récent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Météo Notifications";
            String description = "Notifications quotidiennes pour la météo";
            
            // IMPORTANCE_DEFAULT = notification avec son mais pas intrusive
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            // Enregistrement du canal auprès du système
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    /**
     * Active ou désactive les notifications pour une ville
     * 
     * @param cityName Nom de la ville
     * @param latitude Latitude pour les requêtes API précises
     * @param longitude Longitude pour les requêtes API précises
     * @return nouvel état des notifications (true = activées, false = désactivées)
     */
    public static boolean toggleNotification(Context context, String cityName, double latitude, double longitude) {
        // Récupérer la liste actuelle des villes avec notifications
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> notifiedCities = prefs.getStringSet(NOTIFIED_CITIES_KEY, new HashSet<>());
        
        // On ne peut pas modifier directement le Set retourné par SharedPreferences
        Set<String> updatedCities = new HashSet<>(notifiedCities);
        
        // Vérifier si cette ville a déjà des notifications
        boolean isNotified = notifiedCities.contains(cityName);
        
        if (isNotified) {
            // Si les notifications sont déjà activées, on les désactive
            updatedCities.remove(cityName);
            cancelNotifications(context, cityName);
        } else {
            // Sinon, on active les notifications
            updatedCities.add(cityName);
            scheduleNotification(context, cityName, latitude, longitude);
        }
        
        // Sauvegarder la liste mise à jour
        prefs.edit().putStringSet(NOTIFIED_CITIES_KEY, updatedCities).apply();
        
        // Retourne l'inverse de l'état précédent (true si on vient d'activer)
        return !isNotified;
    }
    
    /**
     * Vérifie si une ville a des notifications activées
     */
    public static boolean isNotificationEnabled(Context context, String cityName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> notifiedCities = prefs.getStringSet(NOTIFIED_CITIES_KEY, new HashSet<>());
        return notifiedCities.contains(cityName);
    }
    
    /**
     * Programme l'envoi d'une notification quotidienne pour une ville
     * Cette méthode configure une alarme qui déclenchera le NotificationReceiver
     */
    private static void scheduleNotification(Context context, String cityName, double latitude, double longitude) {
        // Création de l'intent qui sera envoyé au BroadcastReceiver
        Intent intent = new Intent(context, NotificationReceiver.class);
        // On passe les infos de la ville pour pouvoir récupérer ses données météo
        intent.putExtra("city_name", cityName);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        
        // Créer un code unique pour chaque ville
        int requestCode = cityName.hashCode();
        
        // Création d'un PendingIntent qui sera déclenché par l'alarme
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Réglage de l'alarme pour 15h30 chaque jour
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // Configuration de l'heure de déclenchement
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,15);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        
        // Si l'heure est déjà passée aujourd'hui, on programme pour demain
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        if (alarmManager != null) {
            // Différentes méthodes selon la version d'Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ : on doit vérifier qu'on a la permission pour les alarmes exactes
                if (alarmManager.canScheduleExactAlarms()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Pour Android 6+ : méthode qui permet de réveiller l'appareil
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        // Pour les versions plus anciennes
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }
                } else {
                    // Solution de secours si on n'a pas la permission d'alarmes exactes
                    // Cette méthode a moins de précision mais fonctionne sans permission spéciale
                    alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                    );
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Pour Android 6-11
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                // Pour Android 5 et moins
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
        }
    }
    
    /**
     * Annule les notifications programmées pour une ville
     */
    private static void cancelNotifications(Context context, String cityName) {
        // On récupère l'intent similaire à celui créé pour programmer
        Intent intent = new Intent(context, NotificationReceiver.class);
        int requestCode = cityName.hashCode();
        
        // Même PendingIntent que celui créé dans scheduleNotification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // On annule l'alarme avec ce PendingIntent
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    
    /**
     * Affiche une notification avec des recommandations vestimentaires
     * basées sur la météo actuelle
     */
    public static void showWeatherClothingNotification(Context context, String cityName, float temperature, String weatherDescription) {
        // Intent qui sera exécuté quand l'utilisateur clique sur la notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        
        // Titre standard de la notification
        String contentTitle = "Météo du jour à " + cityName;
        String contentText;
        
        // Définir le message en fonction de la température
        if (temperature >= 25) {
            // Conseils pour temps chaud
            contentText = "Il fait chaud aujourd'hui ! Pensez à prendre une casquette et à porter des vêtements légers (T-shirt, short).";
        } else if (temperature >= 15 && temperature < 25) {
            // Température agréable
            contentText = "Il fait bon aujourd'hui à " + cityName + ". Température agréable de " + temperature + "°C.";
        } else {
            // Conseils pour temps froid
            contentText = "Il fait froid aujourd'hui. Pensez à bien vous couvrir !";
        }
        
        // Ajout d'un conseil pour la pluie si nécessaire
        if (weatherDescription.toLowerCase().contains("pluie") || 
            weatherDescription.toLowerCase().contains("pluvieux") ||
            weatherDescription.toLowerCase().contains("averse")) {
            contentText += " N'oubliez pas votre parapluie !";
        }
        
        // Construction de la notification avec tous ses paramètres
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        // Système de gestion des notifications
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Chaque ville aura son propre ID de notification
        int notificationId = cityName.hashCode();
        
        // Sur Android 13+, on doit vérifier la permission POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId, builder.build());
            }
        } else {
            // Sur les versions plus anciennes, pas besoin de vérifier
            notificationManager.notify(notificationId, builder.build());
        }
    }
} 