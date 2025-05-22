package com.example.meteoandroidmvp.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.meteoandroidmvp.utils.Constants;

/**
 * Service qui communique avec l'API Mistral AI pour générer des recommandations
 * vestimentaires basées sur les données météorologiques.
 */
public class MistralAIService {
    
    // URL de l'API Mistral
    private static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    
    // Clé API (récupérée depuis la classe Constants)
    private static final String API_KEY = Constants.MISTRAL_API_KEY;
    
    // Type de média pour les requêtes JSON
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    // Client HTTP
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    
    // Gson pour la sérialisation/désérialisation JSON
    private static final Gson gson = new Gson();
    
    /**
     * Interface pour récupérer la réponse de l'IA de manière asynchrone
     */
    public interface AIResponseCallback {
        void onResponse(String recommendation);
        void onFailure(String errorMessage);
    }
    
    /**
     * Génère une recommandation vestimentaire en utilisant l'API Mistral AI
     * 
     * @param cityName Nom de la ville
     * @param temperature Température en degrés Celsius
     * @param weatherDescription Description de la météo
     * @param windSpeed Vitesse du vent en m/s
     * @param humidity Taux d'humidité en pourcentage
     * @param callback Callback pour récupérer la réponse
     */
    public static void getClothingRecommendation(
            String cityName,
            float temperature,
            String weatherDescription,
            float windSpeed,
            int humidity,
            AIResponseCallback callback) {
        
        // Construction du prompt pour l'IA
        String prompt = String.format(
                "Tu es un assistant météo spécialisé dans les recommandations vestimentaires. " +
                "Voici les données météo actuelles pour %s : " +
                "Température : %.1f°C, " +
                "Conditions météo : %s, " +
                "Vitesse du vent : %.1f m/s, " +
                "Humidité : %d%%. " +
                "En te basant uniquement sur ces informations, donne-moi une recommandation " +
                "détaillée sur comment m'habiller aujourd'hui. Sois précis, pratique et " +
                "adapte ta réponse aux conditions spécifiques. Ajoute également une brève " +
                "recommandation sur les activités d'extérieur. " +
                "IMPORTANT: Ne répète PAS les informations météo au début de ta réponse. " +
                "Commence DIRECTEMENT par ta recommandation vestimentaire. " +
                "Utilise des émojis pour rendre ta réponse visuellement plus attrayante. Sois concis et direct.",
                cityName, temperature, weatherDescription, windSpeed, humidity);
        
        // Construction de l'objet message pour l'API
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        
        JsonArray messages = new JsonArray();
        messages.add(message);
        
        // Construction de la requête complète
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "mistral-small");
        requestBody.add("messages", messages);
        requestBody.addProperty("max_tokens", 500);
        requestBody.addProperty("temperature", 0.7);
        
        // Conversion en JSON
        String json = gson.toJson(requestBody);
        
        // Construction de la requête HTTP
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        
        // Exécution de la requête de manière asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MistralAI", "Erreur lors de la communication avec l'API", e);
                // En cas d'erreur, utiliser l'IA locale comme fallback
                String localRecommendation = WeatherAI.getClothingRecommendation(
                        temperature,
                        weatherDescription,
                        windSpeed,
                        humidity,
                        true // On suppose qu'il fait jour
                );
                
                // Informer que nous utilisons un fallback
                String fallbackMessage = "⚠️ Impossible de contacter l'IA en ligne. Voici une recommandation générée localement :\n\n" 
                        + localRecommendation;
                
                callback.onFailure(fallbackMessage);
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Réponse API non réussie: " + response.code());
                    }
                    
                    // Extraction de la réponse JSON
                    String responseBody = response.body().string();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    
                    // Extraction du texte de la réponse (structure spécifique à l'API Mistral)
                    String aiRecommendation = jsonResponse
                            .getAsJsonArray("choices")
                            .get(0)
                            .getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content")
                            .getAsString();
                    
                    // Transmission de la réponse au callback
                    callback.onResponse(aiRecommendation);
                    
                } catch (Exception e) {
                    Log.e("MistralAI", "Erreur lors du traitement de la réponse", e);
                    // En cas d'erreur, utiliser l'IA locale comme fallback
                    String localRecommendation = WeatherAI.getClothingRecommendation(
                            temperature,
                            weatherDescription,
                            windSpeed,
                            humidity,
                            true // On suppose qu'il fait jour
                    );
                    
                    // Informer que nous utilisons un fallback
                    String fallbackMessage = "⚠️ Erreur lors du traitement de la réponse de l'IA. Voici une recommandation générée localement :\n\n" 
                            + localRecommendation;
                    
                    callback.onFailure(fallbackMessage);
                }
            }
        });
    }
} 