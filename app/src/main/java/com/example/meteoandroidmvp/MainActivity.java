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

/**
 * Activité principale de l'application Météo
 * Elle gère la navigation entre les fragments, la géolocalisation
 * et le stockage des villes favorites
 */
public class MainActivity extends AppCompatActivity 
        implements WeatherFragment.WeatherFragmentListener, 
                   FavoritesFragment.FavoritesFragmentListener,
                   BottomNavigationView.OnNavigationItemSelectedListener {

    // Client pour accéder à la localisation de l'appareil
    private FusedLocationProviderClient fusedLocationClient;
    
    // Barre de navigation en bas de l'écran
    private BottomNavigationView bottomNavigationView;
    
    // Liste des villes favorites de l'utilisateur
    private List<FavoriteCity> favoriteCities = new ArrayList<>();
    
    // Nom du fichier de préférences pour stocker les données
    private static final String PREFERENCES_NAME = "MeteoPrefs";
    
    // Clé pour accéder aux favoris dans les préférences
    private static final String FAVORITES_KEY = "favorites";
    
    // Instances des fragments principaux
    private WeatherFragment weatherFragment;
    private FavoritesFragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Active le mode edge-to-edge pour un affichage plein écran moderne
        setContentView(R.layout.activity_main);
        
        // Configuration pour que l'interface s'adapte aux barres système (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialisation de la barre de navigation et configuration de son listener
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        
        // Initialisation du service de localisation Google Play
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Récupération des villes favorites depuis le stockage local
        loadFavorites();
        
        // Création des instances de fragments
        weatherFragment = new WeatherFragment();
        favoritesFragment = new FavoritesFragment();
        
        // Vérification si l'app a les permissions de localisation
        checkLocationPermission();
        
        // Au premier démarrage, afficher le fragment météo par défaut
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_weather);
        }
    }
    
    /**
     * Vérifie si l'application a les permissions de localisation
     * Si non, les demande à l'utilisateur
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si on n'a pas les permissions, on les demande
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // On a déjà les permissions, récupérer la position
            getLocation();
        }
    }
    
    /**
     * Récupère la dernière position connue de l'utilisateur
     * et met à jour le fragment météo avec ces coordonnées
     */
    private void getLocation() {
        // Double vérification des permissions (exigence d'Android)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        // Récupération de la dernière position connue
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null && weatherFragment != null) {
                        // Si on a une position, préparation des arguments pour le fragment
                        Bundle args = new Bundle();
                        args.putDouble("latitude", location.getLatitude());
                        args.putDouble("longitude", location.getLongitude());
                        weatherFragment.setArguments(args);
                        
                        // Si le fragment météo est actuellement affiché, le mettre à jour
                        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof WeatherFragment) {
                            loadFragment(weatherFragment);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    // En cas d'erreur, afficher un message
                    Toast.makeText(this, "Erreur de localisation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    /**
     * Gestion du résultat de la demande de permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur a accepté, récupérer sa position
                getLocation();
            }
        }
    }
    
    /**
     * Charge un fragment dans le conteneur principal
     * 
     * @param fragment Le fragment à afficher
     * @return true si le chargement a réussi
     */
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
    
    /**
     * Gère les clics sur les items de la barre de navigation
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        
        // Déterminer quel fragment charger selon l'item cliqué
        if (item.getItemId() == R.id.navigation_favorites) {
            fragment = favoritesFragment;
        } else if (item.getItemId() == R.id.navigation_weather) {
            fragment = weatherFragment;
        }
        
        return loadFragment(fragment);
    }
    
    //
    // Implémentations des interfaces de communication avec les fragments
    //
    
    /**
     * Appelée quand l'utilisateur ajoute/retire une ville des favoris
     * depuis le fragment météo
     */
    @Override
    public void onAddToFavorites(FavoriteCity favoriteCity) {
        // Vérifier si la ville est déjà dans les favoris
        boolean alreadyExists = false;
        for (FavoriteCity city : favoriteCities) {
            if (city.getCityName().equals(favoriteCity.getCityName())) {
                // Si oui, mettre à jour ses informations
                city.setLastUpdate(favoriteCity.getLastUpdate());
                city.setLatitude(favoriteCity.getLatitude());
                city.setLongitude(favoriteCity.getLongitude());
                city.setFromLocation(favoriteCity.isFromLocation());
                alreadyExists = true;
                Toast.makeText(this, "Favori mis à jour", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        
        // Si la ville n'est pas dans les favoris, l'ajouter
        if (!alreadyExists) {
            favoriteCities.add(favoriteCity);
            Toast.makeText(this, "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }
        
        // Sauvegarder les changements
        saveFavorites();
        
        // Si le fragment des favoris est visible, lui demander de se mettre à jour
        if (favoritesFragment != null && favoritesFragment.isVisible()) {
            favoritesFragment.updateFavoritesList();
        }
    }
    
    /**
     * Vérifie si une ville est dans les favoris
     * Utilisée par le fragment météo pour afficher l'état du bouton favori
     */
    @Override
    public boolean isCityFavorite(String cityName) {
        for (FavoriteCity city : favoriteCities) {
            if (city.getCityName().equals(cityName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Renvoie la liste des villes favorites
     * Utilisée par le fragment des favoris pour afficher la liste
     */
    @Override
    public List<FavoriteCity> getFavoriteCities() {
        return favoriteCities;
    }
    
    /**
     * Appelée quand l'utilisateur clique sur une ville dans la liste des favoris
     */
    @Override
    public void onFavoriteCitySelected(FavoriteCity favoriteCity) {
        // Préparation des arguments pour le fragment météo
        Bundle args = new Bundle();
        if (favoriteCity.isFromLocation()) {
            // Si la ville a été ajoutée via géolocalisation, utiliser ses coordonnées
            args.putDouble("latitude", favoriteCity.getLatitude());
            args.putDouble("longitude", favoriteCity.getLongitude());
        } else {
            // Sinon, utiliser le nom de la ville
            args.putString("cityName", favoriteCity.getCityName());
        }
        
        // Créer une nouvelle instance du fragment météo avec ces arguments
        weatherFragment = new WeatherFragment();
        weatherFragment.setArguments(args);
        
        // Afficher le fragment météo
        loadFragment(weatherFragment);
        
        // Sélectionner l'onglet météo dans la navigation
        bottomNavigationView.setSelectedItemId(R.id.navigation_weather);
    }
    
    /**
     * Appelée quand l'utilisateur supprime une ville des favoris
     * depuis le fragment des favoris
     */
    @Override
    public void onFavoriteCityDeleted(FavoriteCity favoriteCity) {
        // Supprimer la ville de la liste
        favoriteCities.removeIf(city -> city.getCityName().equals(favoriteCity.getCityName()));
        
        // Sauvegarder les changements
        saveFavorites();
        
        Toast.makeText(this, "Supprimé des favoris", Toast.LENGTH_SHORT).show();
    }
    
    //
    // Méthodes de gestion du stockage des favoris
    //
    
    /**
     * Sauvegarde la liste des favoris dans les SharedPreferences
     * en utilisant Gson pour convertir la liste en JSON
     */
    private void saveFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        Gson gson = new Gson();
        String json = gson.toJson(favoriteCities);
        
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }
    
    /**
     * Charge la liste des favoris depuis les SharedPreferences
     */
    private void loadFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<FavoriteCity>>() {}.getType();
            favoriteCities = gson.fromJson(json, type);
        }
    }
}