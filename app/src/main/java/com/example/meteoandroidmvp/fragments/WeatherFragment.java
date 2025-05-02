package com.example.meteoandroidmvp.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.adapter.ForecastAdapter;
import com.example.meteoandroidmvp.contract.WeatherContract;
import com.example.meteoandroidmvp.model.FavoriteCity;
import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.model.WeatherResponse;
import com.example.meteoandroidmvp.presenter.WeatherPresenter;
import com.example.meteoandroidmvp.utils.Constants;
import com.example.meteoandroidmvp.utils.NotificationUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment implements WeatherContract.View {

    private WeatherContract.Presenter presenter;
    private ForecastAdapter forecastAdapter;
    private String currentCity = "";
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private boolean usingLocation = false;
    private WeatherResponse currentWeather;
    
    // Vues
    private EditText editCity;
    private ImageButton btnSearch;
    private ImageButton btnFavorite;
    private ImageButton btnNotification;
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
    private TextView tvErrorMessage;
    
    // Gestionnaire de permission pour les notifications (Android 13+)
    private ActivityResultLauncher<String> requestPermissionLauncher;
    
    // Format de date
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE);
    
    // Interface pour communiquer avec l'activité
    public interface WeatherFragmentListener {
        void onAddToFavorites(FavoriteCity favoriteCity);
        boolean isCityFavorite(String cityName);
    }
    
    private WeatherFragmentListener listener;
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WeatherFragmentListener) {
            listener = (WeatherFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " doit implémenter WeatherFragmentListener");
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new WeatherPresenter();
        presenter.attachView(this);
        forecastAdapter = new ForecastAdapter();
        
        // Initialiser le lanceur de demande de permission
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // La permission est accordée, continuer avec les notifications
                        toggleNotification();
                    } else {
                        // Permission refusée
                        Toast.makeText(getContext(), "Permission de notification refusée", Toast.LENGTH_SHORT).show();
                    }
                });
        
        // Créer le canal de notification
        if (getContext() != null) {
            NotificationUtils.createNotificationChannel(getContext());
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        
        // Initialisation des vues
        initViews(view);
        
        // Configuration du RecyclerView
        recyclerForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerForecast.setAdapter(forecastAdapter);
        
        // Configuration des listeners
        setupListeners();
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Si un cityName est passé en argument, charger cette ville
        Bundle args = getArguments();
        if (args != null && args.containsKey("cityName")) {
            currentCity = args.getString("cityName", "");
            if (!currentCity.isEmpty()) {
                editCity.setText(currentCity);
                presenter.getWeatherByCity(currentCity);
                presenter.getForecastByCity(currentCity);
                return;
            }
        }
        
        // Si des coordonnées sont passées en argument, charger cette position
        if (args != null && args.containsKey("latitude") && args.containsKey("longitude")) {
            currentLatitude = args.getDouble("latitude", 0);
            currentLongitude = args.getDouble("longitude", 0);
            if (currentLatitude != 0 && currentLongitude != 0) {
                usingLocation = true;
                presenter.getWeatherByLocation(currentLatitude, currentLongitude);
                presenter.getForecastByLocation(currentLatitude, currentLongitude);
                return;
            }
        }
        
        // Par défaut, utiliser la ville par défaut
        currentCity = Constants.DEFAULT_CITY;
        presenter.getWeatherByCity(Constants.DEFAULT_CITY);
        presenter.getForecastByCity(Constants.DEFAULT_CITY);
    }
    
    private void initViews(View view) {
        editCity = view.findViewById(R.id.edit_city);
        btnSearch = view.findViewById(R.id.btn_search);
        btnFavorite = view.findViewById(R.id.btn_favorite);
        btnNotification = view.findViewById(R.id.btn_notification);
        progressBar = view.findViewById(R.id.progress_bar);
        tvCityName = view.findViewById(R.id.tv_city_name);
        tvUpdateTime = view.findViewById(R.id.tv_update_time);
        imgWeatherIcon = view.findViewById(R.id.img_weather_icon);
        tvWeatherDescription = view.findViewById(R.id.tv_weather_description);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvMinMaxTemp = view.findViewById(R.id.tv_min_max_temp);
        tvFeelsLike = view.findViewById(R.id.tv_feels_like);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvWind = view.findViewById(R.id.tv_wind);
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvVisibility = view.findViewById(R.id.tv_visibility);
        tvCloudiness = view.findViewById(R.id.tv_cloudiness);
        tvSunrise = view.findViewById(R.id.tv_sunrise);
        tvSunset = view.findViewById(R.id.tv_sunset);
        recyclerForecast = view.findViewById(R.id.recycler_forecast);
        weatherContent = view.findViewById(R.id.weather_content);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
    }
    
    private void setupListeners() {
        // Rechercher une ville lorsque l'utilisateur clique sur le bouton de recherche
        btnSearch.setOnClickListener(v -> {
            String city = editCity.getText().toString().trim();
            if (!city.isEmpty()) {
                currentCity = city;
                usingLocation = false;
                presenter.getWeatherByCity(city);
                presenter.getForecastByCity(city);
            }
        });
        
        // Rechercher une ville lorsque l'utilisateur appuie sur la touche "Entrée"
        editCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                btnSearch.performClick();
                return true;
            }
            return false;
        });
        
        // Ajouter/supprimer des favoris
        btnFavorite.setOnClickListener(v -> {
            if (currentWeather != null) {
                // Créer un nouvel objet FavoriteCity
                String cityName = currentWeather.getCityName();
                String country = (currentWeather.getSys() != null) ? currentWeather.getSys().getCountry() : "";
                Date lastUpdate = new Date(currentWeather.getDateTime() * 1000);
                
                FavoriteCity favoriteCity = new FavoriteCity(
                        cityName, 
                        country, 
                        lastUpdate, 
                        currentLatitude, 
                        currentLongitude, 
                        usingLocation);
                
                // Notifier l'activité
                if (listener != null) {
                    listener.onAddToFavorites(favoriteCity);
                    updateFavoriteButton();
                }
            }
        });
        
        // Activer/désactiver les notifications pour cette ville
        btnNotification.setOnClickListener(v -> {
            if (currentWeather != null) {
                // Vérifier les permissions pour Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) 
                            != PackageManager.PERMISSION_GRANTED) {
                        // Demander la permission
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        return;
                    }
                }
                
                // Permission déjà accordée, activer/désactiver les notifications
                toggleNotification();
            }
        });
    }
    
    // Activer ou désactiver les notifications pour la ville actuelle
    private void toggleNotification() {
        if (currentWeather != null && getContext() != null) {
            // Vérifier si nous avons besoin de la permission SCHEDULE_EXACT_ALARM (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Si on n'a pas la permission, ouvrir directement les paramètres système
                    // L'utilisateur devra activer manuellement cette permission
                    Toast.makeText(getContext(), "Veuillez autoriser les alarmes exactes dans les paramètres", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(intent);
                    return;
                }
            }
            
            // Si toutes les permissions sont ok, on peut activer/désactiver les notifications
            String cityName = currentWeather.getCityName();
            
            // Cette méthode va activer les notifs si elles sont désactivées, et vice versa
            // Elle renvoie le nouvel état des notifications
            boolean isNotificationEnabled = NotificationUtils.toggleNotification(
                    getContext(), 
                    cityName, 
                    currentLatitude, 
                    currentLongitude
            );
            
            // Mettre à jour l'icône du bouton selon l'état actif/inactif
            updateNotificationButton(isNotificationEnabled);
            
            // Informer l'utilisateur du changement d'état
            String message = isNotificationEnabled ? 
                    "Notifications activées pour " + cityName : 
                    "Notifications désactivées pour " + cityName;
            
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            
            // Si on vient d'activer les notifications, montrer tout de suite
            // une notification d'exemple pour que l'utilisateur voie le résultat
            if (isNotificationEnabled) {
                float temperature = currentWeather.getMain().getTemperature();
                String description = "";
                if (currentWeather.getWeather() != null && !currentWeather.getWeather().isEmpty()) {
                    description = currentWeather.getWeather().get(0).getDescription();
                }
                
                // Afficher une notification test immédiatement
                NotificationUtils.showWeatherClothingNotification(getContext(), cityName, temperature, description);
            }
        }
    }
    
    private void updateFavoriteButton() {
        if (currentWeather != null && listener != null) {
            boolean isFavorite = listener.isCityFavorite(currentWeather.getCityName());
            btnFavorite.setImageResource(isFavorite ? 
                    android.R.drawable.btn_star_big_on : 
                    android.R.drawable.btn_star_big_off);
        }
    }
    
    private void updateNotificationButton(boolean isNotificationEnabled) {
        btnNotification.setImageResource(isNotificationEnabled ? 
                android.R.drawable.ic_popup_reminder : 
                android.R.drawable.ic_menu_recent_history);
    }
    
    @Override
    public void showWeather(WeatherResponse weatherResponse) {
        if (weatherResponse == null) return;
        
        currentWeather = weatherResponse;
        weatherContent.setVisibility(View.VISIBLE);
        if (tvErrorMessage != null) {
            tvErrorMessage.setVisibility(View.GONE);
        }
        
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
        
        // Affichage de la température
        if (weatherResponse.getMain() != null) {
            String temperature = String.format(Locale.FRANCE, "%.0f°C", weatherResponse.getMain().getTemperature());
            tvTemperature.setText(temperature);
            
            // Affichage des températures min et max
            String minMaxTemp = String.format(Locale.FRANCE, 
                    "Min: %.0f°C / Max: %.0f°C", 
                    weatherResponse.getMain().getTempMin(), 
                    weatherResponse.getMain().getTempMax());
            tvMinMaxTemp.setText(minMaxTemp);
            
            // Affichage du ressenti
            String feelsLike = String.format(Locale.FRANCE, "%.0f°C", weatherResponse.getMain().getFeelsLike());
            tvFeelsLike.setText(feelsLike);
            
            // Affichage de l'humidité
            String humidity = weatherResponse.getMain().getHumidity() + "%";
            tvHumidity.setText(humidity);
            
            // Affichage de la pression
            String pressure = weatherResponse.getMain().getPressure() + " hPa";
            tvPressure.setText(pressure);
        }
        
        // Affichage du vent
        if (weatherResponse.getWind() != null) {
            String wind = String.format(Locale.FRANCE, "%.1f km/h", weatherResponse.getWind().getSpeed() * 3.6);
            tvWind.setText(wind);
        }
        
        // Affichage de la visibilité
        String visibility = String.format(Locale.FRANCE, "%.1f km", weatherResponse.getVisibility() / 1000.0f);
        tvVisibility.setText(visibility);
        
        // Affichage de la nébulosité
        if (weatherResponse.getClouds() != null) {
            String cloudiness = weatherResponse.getClouds().getCloudiness() + "%";
            tvCloudiness.setText(cloudiness);
        }
        
        // Affichage du lever et coucher du soleil
        if (weatherResponse.getSys() != null) {
            String sunrise = timeFormat.format(new Date(weatherResponse.getSys().getSunrise() * 1000));
            tvSunrise.setText(sunrise);
            
            String sunset = timeFormat.format(new Date(weatherResponse.getSys().getSunset() * 1000));
            tvSunset.setText(sunset);
        }
        
        // Mise à jour du bouton favori
        updateFavoriteButton();
        
        // Mise à jour de l'état des notifications
        if (getContext() != null) {
            boolean isNotificationEnabled = NotificationUtils.isNotificationEnabled(getContext(), weatherResponse.getCityName());
            updateNotificationButton(isNotificationEnabled);
        }
    }
    
    @Override
    public void showForecast(ForecastResponse forecastResponse) {
        if (forecastResponse != null && forecastResponse.getForecastItems() != null) {
            forecastAdapter.setForecastItems(forecastResponse.getForecastItems());
        }
    }
    
    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            
            if (tvErrorMessage != null) {
                tvErrorMessage.setText(message);
                tvErrorMessage.setVisibility(View.VISIBLE);
                
                // Si c'est une erreur de connexion, cacher le contenu météo
                if (message.contains("Impossible de se connecter")) {
                    weatherContent.setVisibility(View.GONE);
                }
            }
        }
    }
    
    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        weatherContent.setVisibility(View.GONE);
        if (tvErrorMessage != null) {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }
} 