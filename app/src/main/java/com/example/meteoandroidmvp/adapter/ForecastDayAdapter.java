package com.example.meteoandroidmvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.model.ForecastDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur pour afficher les jours de prévisions
 * dans un RecyclerView vertical
 */
public class ForecastDayAdapter extends RecyclerView.Adapter<ForecastDayAdapter.DayViewHolder> {
    
    // Liste des jours de prévisions à afficher
    private List<ForecastDay> forecastDays;
    
    // Contexte de l'application
    private final Context context;
    
    /**
     * Constructeur
     * 
     * @param context Contexte de l'application
     */
    public ForecastDayAdapter(Context context) {
        this.context = context;
        this.forecastDays = new ArrayList<>();
    }
    
    /**
     * Définit la liste des jours de prévisions et notifie l'adaptateur
     * 
     * @param forecastDays Nouvelle liste des jours de prévisions
     */
    public void setForecastDays(List<ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflater la vue d'un jour de prévisions
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_day, parent, false);
        return new DayViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        // Récupérer le jour de prévisions à cette position
        ForecastDay forecastDay = forecastDays.get(position);
        
        // Définir le titre du jour
        holder.tvDayTitle.setText(forecastDay.getDayTitle());
        
        // Configurer le RecyclerView horizontal pour les prévisions horaires
        holder.recyclerDayForecast.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        
        // Créer et configurer l'adaptateur pour les prévisions horaires
        ForecastHourAdapter hourAdapter = new ForecastHourAdapter();
        hourAdapter.setForecastItems(forecastDay.getHourlyForecasts());
        holder.recyclerDayForecast.setAdapter(hourAdapter);
    }
    
    @Override
    public int getItemCount() {
        return forecastDays != null ? forecastDays.size() : 0;
    }
    
    /**
     * ViewHolder pour un jour de prévisions
     */
    static class DayViewHolder extends RecyclerView.ViewHolder {
        // Titre du jour
        final TextView tvDayTitle;
        
        // RecyclerView horizontal pour les prévisions horaires
        final RecyclerView recyclerDayForecast;
        
        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayTitle = itemView.findViewById(R.id.tv_day_title);
            recyclerDayForecast = itemView.findViewById(R.id.recycler_day_forecast);
        }
    }
} 