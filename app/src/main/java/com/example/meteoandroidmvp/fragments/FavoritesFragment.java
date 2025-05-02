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

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerFavorites;
    private TextView tvNoFavorites;
    private FavoritesAdapter adapter;
    
    // Interface pour communiquer avec l'activité
    public interface FavoritesFragmentListener {
        List<FavoriteCity> getFavoriteCities();
        void onFavoriteCitySelected(FavoriteCity favoriteCity);
        void onFavoriteCityDeleted(FavoriteCity favoriteCity);
    }
    
    private FavoritesFragmentListener listener;
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FavoritesFragmentListener) {
            listener = (FavoritesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " doit implémenter FavoritesFragmentListener");
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        
        recyclerFavorites = view.findViewById(R.id.recycler_favorites);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        
        // Initialisation du RecyclerView
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoritesAdapter(this);
        recyclerFavorites.setAdapter(adapter);
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateFavoritesList();
    }
    
    /**
     * Met à jour la liste des favoris
     */
    public void updateFavoritesList() {
        if (listener != null) {
            List<FavoriteCity> favoriteCities = listener.getFavoriteCities();
            adapter.setFavoriteCities(favoriteCities);
            
            // Affichage du message si aucun favori
            if (favoriteCities.isEmpty()) {
                tvNoFavorites.setVisibility(View.VISIBLE);
                recyclerFavorites.setVisibility(View.GONE);
            } else {
                tvNoFavorites.setVisibility(View.GONE);
                recyclerFavorites.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public void onFavoriteClick(FavoriteCity favoriteCity) {
        if (listener != null) {
            listener.onFavoriteCitySelected(favoriteCity);
        }
    }
    
    @Override
    public void onDeleteClick(FavoriteCity favoriteCity) {
        if (listener != null) {
            listener.onFavoriteCityDeleted(favoriteCity);
            updateFavoritesList();
        }
    }
} 