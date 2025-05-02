package com.example.meteoandroidmvp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.adapter.FavoritesAdapter;
import com.example.meteoandroidmvp.model.FavoriteCity;

import java.util.List;

/**
 * Fragment qui affiche la liste des villes favorites de l'utilisateur
 * Permet de sélectionner une ville pour voir sa météo ou de supprimer un favori
 */
public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    // Vue qui affiche la liste des villes favorites
    private RecyclerView recyclerFavorites;
    
    // Message affiché quand aucune ville n'est en favori
    private TextView tvNoFavorites;
    
    // Adaptateur qui gère l'affichage des éléments de la liste
    private FavoritesAdapter adapter;
    
    /**
     * Interface que l'activité hôte doit implémenter pour communiquer avec ce fragment
     * Permet de récupérer les données et de notifier des interactions utilisateur
     */
    public interface FavoritesFragmentListener {
        // Récupère la liste des villes favorites depuis l'activité
        List<FavoriteCity> getFavoriteCities();
        
        // Appelée quand l'utilisateur clique sur une ville pour voir sa météo
        void onFavoriteCitySelected(FavoriteCity favoriteCity);
        
        // Appelée quand l'utilisateur supprime une ville des favoris
        void onFavoriteCityDeleted(FavoriteCity favoriteCity);
    }
    
    // Référence vers l'activité qui implémente l'interface
    private FavoritesFragmentListener listener;
    
    /**
     * Attachement du fragment à l'activité
     * Vérifie que l'activité implémente bien l'interface requise
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FavoritesFragmentListener) {
            listener = (FavoritesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " doit implémenter FavoritesFragmentListener");
        }
    }
    
    /**
     * Création de la vue du fragment
     * Initialise les composants d'interface et les listeners
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        
        // Récupération des vues
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        
        // Configuration du RecyclerView avec un layout vertical
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Création de l'adaptateur avec ce fragment comme listener pour les clics
        adapter = new FavoritesAdapter(this);
        recyclerFavorites.setAdapter(adapter);
        
        return view;
    }
    
    /**
     * Appelée quand le fragment devient visible
     * Assure que la liste des favoris est à jour
     */
    @Override
    public void onResume() {
        super.onResume();
        updateFavoritesList();
    }
    
    /**
     * Met à jour l'affichage de la liste des favoris
     * Affiche un message si aucun favori n'est disponible
     */
    public void updateFavoritesList() {
        if (listener != null) {
            // Récupération des villes favorites depuis l'activité
            List<FavoriteCity> favoriteCities = listener.getFavoriteCities();
            
            // Mise à jour de l'adaptateur avec les données
            adapter.setFavoriteCities(favoriteCities);
            
            // Gestion de l'affichage selon qu'il y a des favoris ou non
            if (favoriteCities.isEmpty()) {
                // Aucun favori : afficher le message
                tvNoFavorites.setVisibility(View.VISIBLE);
                recyclerFavorites.setVisibility(View.GONE);
            } else {
                // Des favoris existent : afficher la liste
                tvNoFavorites.setVisibility(View.GONE);
                recyclerFavorites.setVisibility(View.VISIBLE);
            }
        }
    }
    
    /**
     * Appelée quand l'utilisateur clique sur une ville de la liste
     * Redirige vers l'affichage de la météo pour cette ville
     */
    @Override
    public void onFavoriteClick(FavoriteCity favoriteCity) {
        if (listener != null) {
            listener.onFavoriteCitySelected(favoriteCity);
        }
    }
    
    /**
     * Appelée quand l'utilisateur clique sur le bouton de suppression
     * Supprime la ville des favoris et met à jour l'affichage
     */
    @Override
    public void onDeleteClick(FavoriteCity favoriteCity) {
        if (listener != null) {
            listener.onFavoriteCityDeleted(favoriteCity);
            updateFavoritesList();
        }
    }
} 