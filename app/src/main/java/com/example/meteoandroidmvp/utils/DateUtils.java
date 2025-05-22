package com.example.meteoandroidmvp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitaire pour gérer les opérations liées aux dates
 * et à la comparaison des jours
 */
public class DateUtils {
    
    // Format pour afficher le nom du jour de la semaine
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE", Locale.FRANCE);
    
    // Format pour afficher le jour et le mois
    private static final SimpleDateFormat DAY_MONTH_FORMAT = new SimpleDateFormat("d MMMM", Locale.FRANCE);
    
    /**
     * Vérifie si deux dates sont le même jour calendaire
     * 
     * @param date1 Première date (en millisecondes depuis l'epoch)
     * @param date2 Seconde date (en millisecondes depuis l'epoch)
     * @return true si les deux dates sont le même jour, false sinon
     */
    public static boolean isSameDay(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(date1);
        cal2.setTimeInMillis(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Obtient un titre formaté pour un jour par rapport à aujourd'hui
     * 
     * @param dateInMillis Date en millisecondes depuis l'epoch
     * @return Titre formaté (ex: "Aujourd'hui", "Demain", "Lundi 17 juin")
     */
    public static String getFormattedDayTitle(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        long todayInMillis = calendar.getTimeInMillis();
        
        // Vérifier si c'est aujourd'hui
        if (isSameDay(dateInMillis, todayInMillis)) {
            return "Aujourd'hui";
        }
        
        // Vérifier si c'est demain
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long tomorrowInMillis = calendar.getTimeInMillis();
        if (isSameDay(dateInMillis, tomorrowInMillis)) {
            return "Demain";
        }
        
        // Sinon, afficher le nom du jour et la date
        Date date = new Date(dateInMillis);
        String dayName = DAY_FORMAT.format(date);
        // En français, mettre la première lettre en majuscule
        dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
        String dayMonth = DAY_MONTH_FORMAT.format(date);
        
        return dayName + " " + dayMonth;
    }
} 