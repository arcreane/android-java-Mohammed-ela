package com.example.meteoandroidmvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meteoandroidmvp.R;
import com.example.meteoandroidmvp.model.ForecastResponse;
import com.example.meteoandroidmvp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur pour afficher les prévisions horaires pour un jour donné
 * dans un RecyclerView horizontal (carousel)
 */
public class ForecastHourAdapter extends RecyclerView.Adapter<ForecastHourAdapter.HourViewHolder> {
    
    // Liste des prévisions horaires à afficher
    private List<ForecastResponse.ForecastItem> forecastItems;
    
    // Format pour afficher uniquement l'heure
    private final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.FRANCE);
    
    /**
     * Constructeur par défaut
     */
    public ForecastHourAdapter() {
        this.forecastItems = new ArrayList<>();
    }
    
    /**
     * Définit la liste des prévisions horaires et notifie l'adaptateur
     * 
     * @param forecastItems Nouvelle liste des prévisions horaires
     */
    public void setForecastItems(List<ForecastResponse.ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflater la vue d'une prévision horaire
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_hour, parent, false);
        return new HourViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        // Récupérer la prévision horaire à cette position
        ForecastResponse.ForecastItem item = forecastItems.get(position);
        
        // Définir l'heure
        holder.tvHour.setText(hourFormat.format(new Date(item.getDateTime() * 1000)));
        
        // Définir la température
        String temperature = String.format(Locale.FRANCE, "%.0f°C", item.getMain().getTemperature());
        holder.tvTemperature.setText(temperature);
        
        // Définir la description
        if (item.getWeather() != null && !item.getWeather().isEmpty()) {
            holder.tvDescription.setText(item.getWeather().get(0).getDescription());
            
            // Charger l'icône météo
            String iconCode = item.getWeather().get(0).getIcon();
            String iconUrl = String.format(Constants.ICON_URL, iconCode);
            Picasso.get().load(iconUrl).into(holder.imgWeatherIcon);
        }
    }
    
    @Override
    public int getItemCount() {
        return forecastItems != null ? forecastItems.size() : 0;
    }
    
    /**
     * ViewHolder pour une prévision horaire
     */
    static class HourViewHolder extends RecyclerView.ViewHolder {
        // Heure de la prévision
        final TextView tvHour;
        
        // Icône météo
        final ImageView imgWeatherIcon;
        
        // Température
        final TextView tvTemperature;
        
        // Description météo
        final TextView tvDescription;
        
        HourViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tv_hour);
            imgWeatherIcon = itemView.findViewById(R.id.img_weather_icon);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
} 