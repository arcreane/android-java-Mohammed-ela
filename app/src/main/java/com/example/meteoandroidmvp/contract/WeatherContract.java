package com.example.meteoandroidmvp.contract;

import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;

public interface WeatherContract {
    
    interface View {
        void showWeather(WeatherResponse weatherResponse);
        void showForecast(ForecastResponse forecastResponse);
        void showError(String message);
        void showLoading();
        void hideLoading();
    }
    
    interface Presenter {
        void attachView(View view);
        void detachView();
        void getWeatherByCity(String city);
        void getWeatherByLocation(double latitude, double longitude);
        void getForecastByCity(String city);
        void getForecastByLocation(double latitude, double longitude);
    }
} 