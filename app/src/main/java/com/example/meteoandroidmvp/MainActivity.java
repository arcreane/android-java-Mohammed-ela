package com.example.meteoandroidmvp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.adapter.ForecastAdapter;
import com.example.meteoandroidmvp.contract.WeatherContract;
import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;
import com.example.meteoandroidmvp.presenter.WeatherPresenter;
import com.example.meteoandroidmvp.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements WeatherContract.View {

    private WeatherContract.Presenter presenter;
    private FusedLocationProviderClient fusedLocationClient;
    private ForecastAdapter forecastAdapter;
    private String currentCity = "";
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private boolean usingLocation = false;
    
    // Vues
    private EditText editCity;
    private ImageButton btnSearch;
    private ProgressBar progressBar;
    private TextView tvCityName;
    private TextView tvUpdateTime;
    private ImageView imgWeatherIcon;
    private TextView tvWeatherDescription;
    private TextView tvTemperature;
    private TextView tvMinMaxTemp;
    private TextView tvFeelsLike;
    private TextView tvHumidity;
    private TextView tvWind;
    private TextView tvPressure;
    private TextView tvVisibility;
    private TextView tvCloudiness;
    private TextView tvSunrise;
    private TextView tvSunset;
    private RecyclerView recyclerForecast;
    private View weatherContent;
    
    // Format de date
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Configuration des insets pour le support edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialisation des vues
        initViews();
        
        // Initialisation de l'adaptateur pour les prévisions
        forecastAdapter = new ForecastAdapter();
        recyclerForecast.setLayoutManager(new LinearLayoutManager(this));
        recyclerForecast.setAdapter(forecastAdapter);
        
        // Initialisation du présentateur
        presenter = new WeatherPresenter();
        presenter.attachView(this);
        
        // Initialisation du client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Configuration des listeners
        setupListeners();
        
        // Vérification des permissions de localisation et chargement des données météo
        checkLocationPermission();
    }
    
    private void initViews() {
        editCity = findViewById(R.id.edit_city);
        btnSearch = findViewById(R.id.btn_search);
        progressBar = findViewById(R.id.progress_bar);
        tvCityName = findViewById(R.id.tv_city_name);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        imgWeatherIcon = findViewById(R.id.img_weather_icon);
        tvWeatherDescription = findViewById(R.id.tv_weather_description);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvMinMaxTemp = findViewById(R.id.tv_min_max_temp);
        tvFeelsLike = findViewById(R.id.tv_feels_like);
        tvHumidity = findViewById(R.id.tv_humidity);
        tvWind = findViewById(R.id.tv_wind);
        tvPressure = findViewById(R.id.tv_pressure);
        tvVisibility = findViewById(R.id.tv_visibility);
        tvCloudiness = findViewById(R.id.tv_cloudiness);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);
        recyclerForecast = findViewById(R.id.recycler_forecast);
        weatherContent = findViewById(R.id.weather_content);
    }
    
    private void setupListeners() {
        // Bouton de recherche
        btnSearch.setOnClickListener(v -> {
            String city = editCity.getText().toString().trim();
            if (!city.isEmpty()) {
                usingLocation = false;
                currentCity = city;
                presenter.getWeatherByCity(city);
                presenter.getForecastByCity(city);
            }
        });
        
        // Action de recherche sur le clavier
        editCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String city = editCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    usingLocation = false;
                    currentCity = city;
                    presenter.getWeatherByCity(city);
                    presenter.getForecastByCity(city);
                }
                return true;
            }
            return false;
        });
    }
    
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demande de permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions déjà accordées, obtention de la localisation
            getLocation();
        }
    }
    
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Localisation obtenue, récupération des données météo
                            usingLocation = true;
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            presenter.getWeatherByLocation(currentLatitude, currentLongitude);
                            presenter.getForecastByLocation(currentLatitude, currentLongitude);
                        } else {
                            // Localisation non disponible, utilisation de la ville par défaut
                            usingLocation = false;
                            currentCity = Constants.DEFAULT_CITY;
                            presenter.getWeatherByCity(Constants.DEFAULT_CITY);
                            presenter.getForecastByCity(Constants.DEFAULT_CITY);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Erreur de localisation, utilisation de la ville par défaut
                    usingLocation = false;
                    currentCity = Constants.DEFAULT_CITY;
                    presenter.getWeatherByCity(Constants.DEFAULT_CITY);
                    presenter.getForecastByCity(Constants.DEFAULT_CITY);
                });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                getLocation();
            } else {
                // Permission refusée, utilisation de la ville par défaut
                usingLocation = false;
                currentCity = Constants.DEFAULT_CITY;
                presenter.getWeatherByCity(Constants.DEFAULT_CITY);
                presenter.getForecastByCity(Constants.DEFAULT_CITY);
            }
        }
    }
    
    @Override
    public void showWeather(WeatherResponse weatherResponse) {
        weatherContent.setVisibility(View.VISIBLE);
        
        // Affichage du nom de la ville et du pays
        String cityAndCountry = weatherResponse.getCityName();
        if (weatherResponse.getSys() != null && weatherResponse.getSys().getCountry() != null) {
            cityAndCountry += ", " + weatherResponse.getSys().getCountry();
        }
        tvCityName.setText(cityAndCountry);
        
        // Heure de mise à jour
        String updateTime = "Mise à jour le " + dateTimeFormat.format(new Date(weatherResponse.getDateTime() * 1000));
        tvUpdateTime.setText(updateTime);
        
        // Affichage de l'icône météo et de la description
        if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
            String iconCode = weatherResponse.getWeather().get(0).getIcon();
            String iconUrl = String.format(Constants.ICON_URL, iconCode);
            Picasso.get().load(iconUrl).into(imgWeatherIcon);
            
            // Affichage de la description météo
            tvWeatherDescription.setText(weatherResponse.getWeather().get(0).getDescription());
        }
        
        // Affichage de la température actuelle et min/max
        if (weatherResponse.getMain() != null) {
            tvTemperature.setText(String.format("%.1f°C", weatherResponse.getMain().getTemperature()));
            tvMinMaxTemp.setText(String.format("Min: %.1f°C / Max: %.1f°C", 
                    weatherResponse.getMain().getTempMin(), 
                    weatherResponse.getMain().getTempMax()));
            tvFeelsLike.setText(String.format("%.1f°C", weatherResponse.getMain().getFeelsLike()));
            tvHumidity.setText(weatherResponse.getMain().getHumidity() + "%");
            tvPressure.setText(weatherResponse.getMain().getPressure() + " hPa");
        }
        
        // Affichage du vent
        if (weatherResponse.getWind() != null) {
            tvWind.setText(String.format("%.1f km/h", weatherResponse.getWind().getSpeed() * 3.6)); // Conversion de m/s en km/h
        }
        
        // Affichage de la visibilité
        int visibilityInKm = weatherResponse.getVisibility() / 1000; // Conversion de mètres en kilomètres
        tvVisibility.setText(visibilityInKm + " km");
        
        // Affichage de la nébulosité
        if (weatherResponse.getClouds() != null) {
            tvCloudiness.setText(weatherResponse.getClouds().getCloudiness() + "%");
        }
        
        // Affichage du lever et coucher du soleil
        if (weatherResponse.getSys() != null) {
            String sunrise = timeFormat.format(new Date(weatherResponse.getSys().getSunrise() * 1000));
            String sunset = timeFormat.format(new Date(weatherResponse.getSys().getSunset() * 1000));
            tvSunrise.setText(sunrise);
            tvSunset.setText(sunset);
        }
    }
    
    @Override
    public void showForecast(ForecastResponse forecastResponse) {
        if (forecastResponse.getForecastItems() != null && !forecastResponse.getForecastItems().isEmpty()) {
            forecastAdapter.setForecastItems(forecastResponse.getForecastItems());
        }
    }
    
    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        weatherContent.setVisibility(View.GONE);
    }
    
    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }
}