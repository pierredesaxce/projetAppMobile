package fr.uavignon.ceri.tp3.data;

public class WeatherResult {

    static void transferInfo(WeatherResponse weatherInfo, City cityInfo) {

        cityInfo.setTempKelvin(weatherInfo.main.temp);
        cityInfo.setHumidity(weatherInfo.main.humidity);
        cityInfo.setWindSpeedMPerS(weatherInfo.wind.speed);
        cityInfo.setWindDirection(weatherInfo.wind.deg);
        cityInfo.setDescription(weatherInfo.weather.get(0).description);
        cityInfo.setIcon(weatherInfo.weather.get(0).icon);
        cityInfo.setLastUpdate(weatherInfo.dt);
        cityInfo.setCloudiness(weatherInfo.clouds.all);
    }

}
