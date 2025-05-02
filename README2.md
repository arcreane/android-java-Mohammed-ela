# Application Météo Android MVP

Une application météo simple basée sur l'architecture MVP (Model-View-Presenter) qui utilise l'API OpenWeatherMap pour afficher les conditions météorologiques actuelles et les prévisions sur 5 jours.

## Fonctionnalités

- Affichage des conditions météorologiques actuelles
- Affichage des prévisions sur 5 jours
- Géolocalisation pour obtenir la météo de l'emplacement actuel
- Recherche de météo par nom de ville
- Affichage des détails météo: température, ressenti, humidité, pression, vent, visibilité, etc.
- Affichage des heures de lever et coucher du soleil

## Architecture

L'application suit l'architecture MVP (Model-View-Presenter):

- **Model**: Classes de données pour représenter les réponses de l'API
  - `WeatherResponse.java`
  - `ForecastResponse.java`

- **View**: Interface utilisateur et interactions
  - `MainActivity.java`
  - Layouts XML

- **Presenter**: Logique métier et communication avec l'API
  - `WeatherPresenter.java`
  - `WeatherContract.java` (Interface de contrat)

## Technologies utilisées

- Java
- Retrofit pour les appels API REST
- OkHttp pour la gestion des requêtes HTTP
- Picasso pour le chargement des images
- Google Play Services pour la géolocalisation
- RecyclerView pour l'affichage des listes

## API OpenWeatherMap

### Clés et URLs de base
- **URL de base de l'API**: `https://api.openweathermap.org/data/2.5/`
- **Clé API**: `XXXXXXXX`
- **URL des icônes**: `https://openweathermap.org/img/wn/%s@2x.png`

### Endpoints

1. **Météo actuelle par nom de ville**:
   ```
   https://api.openweathermap.org/data/2.5/weather?q=Paris&appid=XXXXXXXX&units=metric&lang=fr
   ```

2. **Prévisions sur 5 jours par nom de ville**:
   ```
   https://api.openweathermap.org/data/2.5/forecast?q=Paris&appid=XXXXXXXX&units=metric&lang=fr
   ```

3. **Météo actuelle par coordonnées géographiques** (Paris: latitude 48.8566, longitude 2.3522):
   ```
   https://api.openweathermap.org/data/2.5/weather?lat=48.8566&lon=2.3522&appid=XXXXXXXX&units=metric&lang=fr
   ```

4. **Prévisions sur 5 jours par coordonnées**:
   ```
   https://api.openweathermap.org/data/2.5/forecast?lat=48.8566&lon=2.3522&appid=XXXXXXXX&units=metric&lang=fr
   ```

5. **URL pour une icône météo** (exemple avec le code "10d" pour pluie):
   ```
   https://openweathermap.org/img/wn/10d@2x.png
   ```

### Paramètres communs

- `appid`: Clé API
- `units=metric`: Pour obtenir les températures en Celsius
- `lang=fr`: Pour obtenir les descriptions en français
- `q`: Nom de la ville (pour la recherche par ville)
- `lat`/`lon`: Coordonnées géographiques (pour la recherche par localisation)

## Structure du projet

- **api/**: Contient les interfaces Retrofit et le client API
  - `ApiClient.java`: Classe pour configurer Retrofit
  - `WeatherService.java`: Interface définissant les endpoints API

- **model/**: Classes de modèle de données
  - `WeatherResponse.java`: Représente les données météo actuelles
  - `ForecastResponse.java`: Représente les prévisions sur 5 jours

- **contract/**: Interfaces de contrat MVP
  - `WeatherContract.java`: Définit les interfaces View et Presenter

- **presenter/**: Implémentation des présentateurs
  - `WeatherPresenter.java`: Gère la logique métier et les appels API

- **adapter/**: Adaptateurs pour RecyclerView
  - `ForecastAdapter.java`: Adaptateur pour la liste des prévisions

- **utils/**: Classes utilitaires
  - `Constants.java`: Constantes pour l'API et l'application

- **MainActivity.java**: Activité principale qui implémente l'interface View

## Permissions

L'application nécessite les permissions suivantes:
- `INTERNET`: Pour accéder à l'API météo
- `ACCESS_FINE_LOCATION`: Pour obtenir la localisation précise de l'utilisateur
- `ACCESS_COARSE_LOCATION`: Pour obtenir une localisation approximative 