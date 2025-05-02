package com.example.meteoandroidmvp.presenter;

import com.example.meteoandroidmvp.api.ApiClient;
import com.example.meteoandroidmvp.api.WeatherService;
import com.example.meteoandroidmvp.contract.WeatherContract;
import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;
import com.example.meteoandroidmvp.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implémentation du présentateur qui gère la logique métier pour l'écran météo
 * Fait le lien entre les données (API) et la vue (fragment)
 * Selon le pattern MVP, le présentateur contient la logique métier
 * et ne dépend pas des classes Android (Activity, Fragment, etc.)
 */
public class WeatherPresenter implements WeatherContract.Presenter {
    
    // Référence à la vue (fragment)
    private WeatherContract.View view;
    
    // Service qui permet d'accéder à l'API météo
    private final WeatherService weatherService;
    
    /**
     * Constructeur du présentateur
     * Initialise le service API pour les requêtes réseau
     */
    public WeatherPresenter() {
        // Crée le service avec Retrofit via le client API
        weatherService = ApiClient.getClient().create(WeatherService.class);
    }
    
    /**
     * Attache une vue (fragment) au présentateur
     * Permet au présentateur de communiquer avec la vue
     * 
     * @param view Référence à la vue
     */
    @Override
    public void attachView(WeatherContract.View view) {
        this.view = view;
    }
    
    /**
     * Détache la vue du présentateur
     * Essentiel pour éviter les fuites mémoire quand le fragment est détruit
     */
    @Override
    public void detachView() {
        this.view = null;
    }
    
    /**
     * Récupère les données météo actuelles pour une ville spécifiée
     * 
     * @param city Nom de la ville à rechercher
     */
    @Override
    public void getWeatherByCity(String city) {
        // Afficher l'indicateur de chargement
        if (view != null) {
            view.showLoading();
        }
        
        // Préparation de l'appel API avec les paramètres
        Call<WeatherResponse> call = weatherService.getCurrentWeatherByCity(
                city,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
        // Exécution de l'appel API de manière asynchrone
        call.enqueue(new Callback<WeatherResponse>() {
            /**
             * Appelée quand la réponse de l'API est reçue
             */
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                // Masquer l'indicateur de chargement
                if (view != null) {
                    view.hideLoading();
                }
                
                // Vérifier si la réponse est valide
                if (response.isSuccessful() && response.body() != null) {
                    // Succès : mettre à jour la vue avec les données météo
                    if (view != null) {
                        view.showWeather(response.body());
                    }
                } else {
                    // Erreur HTTP : afficher un message d'erreur
                    if (view != null) {
                        view.showError("Erreur: " + response.code() + " " + response.message());
                    }
                }
            }
            
            /**
             * Appelée en cas d'échec de la requête
             * Par exemple, si pas de connexion internet
             */
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                if (view != null) {
                    view.hideLoading();
                    
                    // Créer un message d'erreur approprié selon le type d'erreur
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        // Problème de connectivité internet
                        errorMessage = "Impossible de se connecter à Internet. Vérifiez votre connexion réseau.";
                    } else {
                        // Autre type d'erreur
                        errorMessage = "Erreur de connexion: " + t.getMessage();
                    }
                    view.showError(errorMessage);
                }
            }
        });
    }
    
    /**
     * Récupère les données météo actuelles pour une position géographique
     * 
     * @param latitude Latitude en degrés décimaux
     * @param longitude Longitude en degrés décimaux
     */
    @Override
    public void getWeatherByLocation(double latitude, double longitude) {
        // Afficher l'indicateur de chargement
        if (view != null) {
            view.showLoading();
        }
        
        // Préparation de l'appel API avec les coordonnées
        Call<WeatherResponse> call = weatherService.getCurrentWeatherByLocation(
                latitude,
                longitude,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
        // Exécution de l'appel API
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (view != null) {
                    view.hideLoading();
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    if (view != null) {
                        view.showWeather(response.body());
                    }
                } else {
                    if (view != null) {
                        view.showError("Erreur: " + response.code() + " " + response.message());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                if (view != null) {
                    view.hideLoading();
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "Impossible de se connecter à Internet. Vérifiez votre connexion réseau.";
                    } else {
                        errorMessage = "Erreur de connexion: " + t.getMessage();
                    }
                    view.showError(errorMessage);
                }
            }
        });
    }
    
    /**
     * Récupère les prévisions météo sur 5 jours pour une ville spécifiée
     * Note: N'affiche pas d'indicateur de chargement pour éviter une UI trop chargée
     * 
     * @param city Nom de la ville à rechercher
     */
    @Override
    public void getForecastByCity(String city) {
        // Préparation de l'appel API
        Call<ForecastResponse> call = weatherService.getForecastByCity(
                city,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
        // Exécution de l'appel API
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (view != null) {
                        view.showForecast(response.body());
                    }
                } else {
                    if (view != null) {
                        view.showError("Erreur prévisions: " + response.code() + " " + response.message());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                if (view != null) {
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "Impossible de se connecter à Internet pour les prévisions.";
                    } else {
                        errorMessage = "Erreur de connexion prévisions: " + t.getMessage();
                    }
                    view.showError(errorMessage);
                }
            }
        });
    }
    
    /**
     * Récupère les prévisions météo sur 5 jours pour une position géographique
     * 
     * @param latitude Latitude en degrés décimaux
     * @param longitude Longitude en degrés décimaux
     */
    @Override
    public void getForecastByLocation(double latitude, double longitude) {
        // Préparation de l'appel API
        Call<ForecastResponse> call = weatherService.getForecastByLocation(
                latitude,
                longitude,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
        // Exécution de l'appel API
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (view != null) {
                        view.showForecast(response.body());
                    }
                } else {
                    if (view != null) {
                        view.showError("Erreur prévisions: " + response.code() + " " + response.message());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                if (view != null) {
                    String errorMessage;
                    if (t instanceof java.net.UnknownHostException) {
                        errorMessage = "Impossible de se connecter à Internet pour les prévisions.";
                    } else {
                        errorMessage = "Erreur de connexion prévisions: " + t.getMessage();
                    }
                    view.showError(errorMessage);
                }
            }
        });
    }
} 