package com.example.meteoandroidmvp.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client API qui gère la création et la configuration de Retrofit
 * pour les appels réseau vers l'API OpenWeatherMap
 */
public class ApiClient {
    // URL de base de l'API OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    
    // Instance Retrofit singleton
    private static Retrofit retrofit = null;
    
    // Configuration des paramètres réseau
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int CONNECT_TIMEOUT = 30; // Secondes
    private static final int READ_TIMEOUT = 30;    // Secondes
    private static final int WRITE_TIMEOUT = 30;   // Secondes

    /**
     * Retourne une instance singleton de Retrofit configurée 
     * pour les appels à l'API météo
     * 
     * @return Instance Retrofit prête à l'emploi
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Créer un intercepteur pour logger les requêtes (utile pour le débogage)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Construire le client OkHttp avec options avancées :
            // - Timeouts pour éviter les blocages trop longs
            // - Logging pour le débogage
            // - Réessayer en cas d'échec de connexion
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            // Construire l'instance Retrofit avec :
            // - URL de base de l'API
            // - Convertisseur Gson pour JSON → objets Java
            // - Client HTTP personnalisé
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Réinitialise le client Retrofit
     * Utile en cas de changement de configuration ou d'erreur persistante
     */
    public static void resetClient() {
        retrofit = null;
    }
} 