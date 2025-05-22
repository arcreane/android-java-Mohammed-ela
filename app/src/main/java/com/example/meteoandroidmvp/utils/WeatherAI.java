package com.example.meteoandroidmvp.utils;

/**
 * Classe d'intelligence artificielle locale pour analyser les données météorologiques
 * et générer des recommandations vestimentaires adaptées.
 * 
 * Cette IA utilise un système de règles basé sur plusieurs paramètres :
 * - Température
 * - Conditions météo (pluie, neige, etc.)
 * - Force du vent
 * - Humidité
 * - Moment de la journée
 */
public class WeatherAI {

    /**
     * Génère une recommandation vestimentaire adaptée aux conditions météo
     * 
     * @param temperature Température en degrés Celsius
     * @param weatherDescription Description de la météo (nuageux, pluie, etc.)
     * @param windSpeed Vitesse du vent en m/s
     * @param humidity Humidité en pourcentage
     * @param isDay Indique s'il fait jour ou nuit
     * @return Recommandation vestimentaire détaillée
     */
    public static String getClothingRecommendation(
            float temperature, 
            String weatherDescription, 
            float windSpeed,
            int humidity,
            boolean isDay) {
        
        StringBuilder recommendation = new StringBuilder();
        
        // Facteur de refroidissement éolien (sensation de froid augmentée par le vent)
        float windChill = calculateWindChill(temperature, windSpeed);
        
        // ===== TENUE DE BASE SELON LA TEMPÉRATURE =====
        if (temperature >= 30) {
            recommendation.append("🔥 Chaleur intense ! Privilégiez des vêtements très légers et amples en tissus naturels. ");
            recommendation.append("Un chapeau et des lunettes de soleil sont indispensables. ");
            recommendation.append("N'oubliez pas votre bouteille d'eau et la crème solaire. ");
        } 
        else if (temperature >= 25 && temperature < 30) {
            recommendation.append("☀️ Il fait chaud ! Optez pour des vêtements légers comme un t-shirt et un short/une jupe. ");
            recommendation.append("Protégez-vous du soleil avec une casquette ou un chapeau. ");
        } 
        else if (temperature >= 20 && temperature < 25) {
            recommendation.append("😎 Température agréable. Un t-shirt avec un pantalon léger ou une jupe sera parfait. ");
            if (!isDay) {
                recommendation.append("Prévoyez un petit gilet léger pour la soirée. ");
            }
        } 
        else if (temperature >= 15 && temperature < 20) {
            recommendation.append("🙂 Temps doux. Un haut à manches longues avec un pantalon est idéal. ");
            recommendation.append("Vous pouvez ajouter une veste légère ou un pull fin. ");
        } 
        else if (temperature >= 10 && temperature < 15) {
            recommendation.append("🍂 Temps frais. Prévoyez plusieurs couches : t-shirt, pull et veste légère. ");
            if (windChill < 10) {
                recommendation.append("Le vent accentue la sensation de froid, une veste coupe-vent serait utile. ");
            }
        } 
        else if (temperature >= 5 && temperature < 10) {
            recommendation.append("❄️ Il fait froid. Portez un pull chaud et une veste épaisse ou un manteau. ");
            recommendation.append("N'oubliez pas écharpe et gants si vous restez longtemps dehors. ");
        } 
        else if (temperature >= 0 && temperature < 5) {
            recommendation.append("🥶 Froid important ! Superposez les couches : sous-vêtement thermique, pull épais et manteau d'hiver. ");
            recommendation.append("Écharpe, gants et bonnet sont nécessaires. ");
        } 
        else {
            recommendation.append("⛄ Températures négatives ! Habillez-vous très chaudement avec plusieurs couches. ");
            recommendation.append("Sous-vêtements thermiques, pull en laine, doudoune ou manteau très chaud, ");
            recommendation.append("écharpe, gants, bonnet et chaussettes épaisses sont indispensables. ");
        }
        
        // ===== ADAPTATIONS SELON LES CONDITIONS MÉTÉO =====
        weatherDescription = weatherDescription.toLowerCase();
        
        // Conditions de pluie
        if (weatherDescription.contains("pluie") || 
            weatherDescription.contains("pluvieux") || 
            weatherDescription.contains("averse")) {
            
            recommendation.append("🌧️ Il pleut ! N'oubliez pas votre parapluie et portez des chaussures imperméables. ");
            
            if (temperature < 15) {
                recommendation.append("Un imperméable ou une veste avec capuche sera plus pratique qu'un parapluie si le vent est fort. ");
            } else {
                recommendation.append("Un imperméable léger ou un coupe-vent imperméable sera utile. ");
            }
        }
        
        // Conditions de neige
        if (weatherDescription.contains("neige") || weatherDescription.contains("neigeux")) {
            recommendation.append("❄️ Il neige ! Portez des bottes imperméables et antidérapantes. ");
            recommendation.append("Assurez-vous que votre manteau est vraiment chaud et imperméable. ");
            recommendation.append("Gants imperméables et bonnet sont indispensables. ");
        }
        
        // Conditions venteuses
        if (windSpeed > 8 || weatherDescription.contains("vent") || weatherDescription.contains("venteux")) {
            recommendation.append("💨 Il y a du vent ! Privilégiez des vêtements coupe-vent. ");
            
            if (temperature < 10) {
                recommendation.append("Le vent accentue la sensation de froid, habillez-vous plus chaudement que d'habitude. ");
            }
            
            if (weatherDescription.contains("pluie")) {
                recommendation.append("Avec la pluie et le vent, un imperméable sera plus pratique qu'un parapluie. ");
            }
        }
        
        // Conditions d'orage
        if (weatherDescription.contains("orage") || weatherDescription.contains("orageux")) {
            recommendation.append("⚡ Attention aux orages ! Restez à l'abri si possible. ");
            recommendation.append("Un imperméable avec capuche est recommandé si vous devez sortir. ");
        }
        
        // Conditions de brouillard
        if (weatherDescription.contains("brouillard") || weatherDescription.contains("brume")) {
            recommendation.append("🌫️ Il y a du brouillard ! Portez des vêtements visibles ou réfléchissants. ");
            if (temperature < 10) {
                recommendation.append("L'humidité du brouillard accentue la sensation de froid, habillez-vous chaudement. ");
            }
        }
        
        // Adaptations selon l'humidité
        if (humidity > 80 && temperature > 20) {
            recommendation.append("💦 L'humidité est élevée ! Privilégiez des vêtements légers et absorbants. ");
        }
        
        return recommendation.toString();
    }
    
    /**
     * Calcule l'indice de refroidissement éolien (sensation de froid augmentée par le vent)
     * @param temperature Température en degrés Celsius
     * @param windSpeed Vitesse du vent en m/s
     * @return Température ressentie en tenant compte du vent
     */
    private static float calculateWindChill(float temperature, float windSpeed) {
        // Conversion de m/s en km/h pour la formule
        float windSpeedKmh = windSpeed * 3.6f;
        
        // Si la température est trop élevée ou le vent trop faible, pas d'effet de refroidissement
        if (temperature > 10 || windSpeedKmh < 4.8) {
            return temperature;
        }
        
        // Formule de l'indice de refroidissement éolien (Environnement Canada)
        float windChill = 13.12f + 0.6215f * temperature - 11.37f * (float)Math.pow(windSpeedKmh, 0.16) 
                + 0.3965f * temperature * (float)Math.pow(windSpeedKmh, 0.16);
        
        return windChill;
    }
    
    /**
     * Vérifie si le temps est favorable pour une activité en extérieur
     * @param temperature Température en degrés Celsius
     * @param weatherDescription Description de la météo
     * @param windSpeed Vitesse du vent en m/s
     * @param humidity Humidité en pourcentage
     * @return true si les conditions sont favorables, false sinon
     */
    public static boolean isGoodForOutdoorActivity(
            float temperature, 
            String weatherDescription, 
            float windSpeed, 
            int humidity) {
        
        // Conditions défavorables
        boolean badWeather = weatherDescription.toLowerCase().contains("pluie") || 
                            weatherDescription.toLowerCase().contains("neige") ||
                            weatherDescription.toLowerCase().contains("orage") ||
                            windSpeed > 10 ||
                            temperature < 5 ||
                            temperature > 35 ||
                            (humidity > 90 && temperature > 25);
        
        return !badWeather;
    }
} 