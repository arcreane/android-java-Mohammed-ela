package com.example.meteoandroidmvp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.meteoandroidmvp.fragments.FavoritesFragment;
import com.example.meteoandroidmvp.fragments.WeatherFragment;
import com.example.meteoandroidmvp.model.FavoriteCity;
import com.example.meteoandroidmvp.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity 
        implements WeatherFragment.WeatherFragmentListener, 
                   FavoritesFragment.FavoritesFragmentListener,
                   BottomNavigationView.OnNavigationItemSelectedListener {

    private FusedLocationProviderClient fusedLocationClient;
    private BottomNavigationView bottomNavigationView;
    private List<FavoriteCity> favoriteCities = new ArrayList<>();
    private static final String PREFERENCES_NAME = "MeteoPrefs";
    private static final String FAVORITES_KEY = "favorites";
    
    private WeatherFragment weatherFragment;
    private FavoritesFragment favoritesFragment;

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
        
        // Initialisation de la navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        
        // Initialisation du client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Chargement des favoris sauvegardés
        loadFavorites();
        
        // Initialisation des fragments
        weatherFragment = new WeatherFragment();
        favoritesFragment = new FavoritesFragment();
        
        // Vérification des permissions de localisation
        checkLocationPermission();
        
        // Affichage du fragment par défaut (météo)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_weather);
        }
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
                .addOnSuccessListener(this, location -> {
                    if (location != null && weatherFragment != null) {
                        Bundle args = new Bundle();
                        args.putDouble("latitude", location.getLatitude());
                        args.putDouble("longitude", location.getLongitude());
                        weatherFragment.setArguments(args);
                        
                        // Si nous sommes sur le fragment météo, remplacer par une nouvelle instance
                        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof WeatherFragment) {
                            loadFragment(weatherFragment);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Erreur de localisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                getLocation();
            }
        }
    }
    
    // Méthode pour charger un fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    
    // Gestion de la navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        
        if (item.getItemId() == R.id.navigation_favorites) {
            fragment = favoritesFragment;
        } else if (item.getItemId() == R.id.navigation_weather) {
            fragment = weatherFragment;
        }
        
        return loadFragment(fragment);
    }
    
    // Méthodes de l'interface WeatherFragmentListener
    @Override
    public void onAddToFavorites(FavoriteCity favoriteCity) {
        // Vérifier si la ville existe déjà dans les favoris
        boolean alreadyExists = false;
        for (FavoriteCity city : favoriteCities) {
            if (city.getCityName().equals(favoriteCity.getCityName())) {
                // Mise à jour de la ville existante
                city.setLastUpdate(favoriteCity.getLastUpdate());
                city.setLatitude(favoriteCity.getLatitude());
                city.setLongitude(favoriteCity.getLongitude());
                city.setFromLocation(favoriteCity.isFromLocation());
                alreadyExists = true;
                Toast.makeText(this, "Favori mis à jour", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        
        // Si la ville n'existe pas, l'ajouter
        if (!alreadyExists) {
            favoriteCities.add(favoriteCity);
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }
        
        // Sauvegarder les favoris
        saveFavorites();
        
        // Mettre à jour le fragment des favoris si visible
        if (favoritesFragment != null && favoritesFragment.isVisible()) {
            favoritesFragment.updateFavoritesList();
        }
    }
    
    @Override
    public boolean isCityFavorite(String cityName) {
        for (FavoriteCity city : favoriteCities) {
            if (city.getCityName().equals(cityName)) {
                return true;
            }
        }
        return false;
    }
    
    // Méthodes de l'interface FavoritesFragmentListener
    @Override
    public List<FavoriteCity> getFavoriteCities() {
        return favoriteCities;
    }
    
    @Override
    public void onFavoriteCitySelected(FavoriteCity favoriteCity) {
        // Créer un bundle avec les informations de la ville
        Bundle args = new Bundle();
        if (favoriteCity.isFromLocation()) {
            args.putDouble("latitude", favoriteCity.getLatitude());
            args.putDouble("longitude", favoriteCity.getLongitude());
        } else {
            args.putString("cityName", favoriteCity.getCityName());
        }
        
        // Mettre à jour les arguments du fragment météo
        weatherFragment = new WeatherFragment();
        weatherFragment.setArguments(args);
        
        // Charger le fragment météo
        loadFragment(weatherFragment);
        
        // Sélectionner l'onglet météo dans la navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_weather);
    }
    
    @Override
    public void onFavoriteCityDeleted(FavoriteCity favoriteCity) {
        // Supprimer la ville des favoris
        favoriteCities.removeIf(city -> city.getCityName().equals(favoriteCity.getCityName()));
        
        // Sauvegarder les favoris
        saveFavorites();
        
        Toast.makeText(this, "Supprimé des favoris", Toast.LENGTH_SHORT).show();
    }
    
    // Méthodes pour la gestion des favoris en SharedPreferences
    
    private void saveFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        Gson gson = new Gson();
        String json = gson.toJson(favoriteCities);
        
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }
    
    private void loadFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FavoriteCity>>() {}.getType();
            favoriteCities = gson.fromJson(json, type);
        }
        
        if (favoriteCities == null) {
            favoriteCities = new ArrayList<>();
        }
    }
}