package com.example.meteoandroidmvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.model.FavoriteCity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<FavoriteCity> favoriteCities = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE);
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteCity favoriteCity);
        void onDeleteClick(FavoriteCity favoriteCity);
    }

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void setFavoriteCities(List<FavoriteCity> favoriteCities) {
        this.favoriteCities = favoriteCities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteCity favoriteCity = favoriteCities.get(position);
        
        holder.tvCityName.setText(favoriteCity.getFullName());
        
        String lastUpdate = "Dernière mise à jour: " + dateFormat.format(favoriteCity.getLastUpdate());
        holder.tvLastUpdate.setText(lastUpdate);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(favoriteCity);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(favoriteCity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteCities.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName;
        TextView tvLastUpdate;
        ImageButton btnDelete;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tv_city_name);
            tvLastUpdate = itemView.findViewById(R.id.tv_last_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
} 