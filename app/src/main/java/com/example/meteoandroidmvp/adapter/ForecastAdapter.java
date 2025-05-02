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

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    
    private List<ForecastResponse.ForecastItem> forecastItems = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM HH:mm", Locale.FRANCE);
    
    public void setForecastItems(List<ForecastResponse.ForecastItem> forecastItems) {
        this.forecastItems = forecastItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastResponse.ForecastItem forecastItem = forecastItems.get(position);
        
        // Format date
        String formattedDate = dateFormat.format(new Date(forecastItem.getDateTime() * 1000L));
        holder.tvDate.setText(formattedDate);
        
        // Température
        holder.tvTemperature.setText(String.format("%.1f°C", forecastItem.getMain().getTemperature()));
        
        // Description et icône météo
        if (forecastItem.getWeather() != null && !forecastItem.getWeather().isEmpty()) {
            holder.tvDescription.setText(forecastItem.getWeather().get(0).getDescription());
            
            // Chargement de l'icône
            String iconCode = forecastItem.getWeather().get(0).getIcon();
            String iconUrl = String.format(Constants.ICON_URL, iconCode);
            Picasso.get().load(iconUrl).into(holder.imgWeatherIcon);
        }
    }
    
    @Override
    public int getItemCount() {
        return forecastItems.size();
    }
    
    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        ImageView imgWeatherIcon;
        TextView tvTemperature;
        TextView tvDescription;
        
        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgWeatherIcon = itemView.findViewById(R.id.img_weather_icon);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
} 