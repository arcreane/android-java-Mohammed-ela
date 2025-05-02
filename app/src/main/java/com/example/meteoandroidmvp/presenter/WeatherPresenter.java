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

public class WeatherPresenter implements WeatherContract.Presenter {
    
    private WeatherContract.View view;
    private final WeatherService weatherService;
    
    public WeatherPresenter() {
        weatherService = ApiClient.getClient().create(WeatherService.class);
    }
    
    @Override
    public void attachView(WeatherContract.View view) {
        this.view = view;
    }
    
    @Override
    public void detachView() {
        this.view = null;
    }
    
    @Override
    public void getWeatherByCity(String city) {
        if (view != null) {
            view.showLoading();
        }
        
        Call<WeatherResponse> call = weatherService.getCurrentWeatherByCity(
                city,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
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
                    view.showError("Erreur de connexion: " + t.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getWeatherByLocation(double latitude, double longitude) {
        if (view != null) {
            view.showLoading();
        }
        
        Call<WeatherResponse> call = weatherService.getCurrentWeatherByLocation(
                latitude,
                longitude,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
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
                    view.showError("Erreur de connexion: " + t.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getForecastByCity(String city) {
        Call<ForecastResponse> call = weatherService.getForecastByCity(
                city,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
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
                    view.showError("Erreur de connexion prévisions: " + t.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getForecastByLocation(double latitude, double longitude) {
        Call<ForecastResponse> call = weatherService.getForecastByLocation(
                latitude,
                longitude,
                Constants.API_KEY,
                Constants.UNITS,
                Constants.LANGUAGE
        );
        
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
                    view.showError("Erreur de connexion prévisions: " + t.getMessage());
                }
            }
        });
    }
} 