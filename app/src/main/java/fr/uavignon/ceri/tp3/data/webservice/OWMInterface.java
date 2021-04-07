package fr.uavignon.ceri.tp3.data.webservice;

import java.util.List;

import fr.uavignon.ceri.tp3.data.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OWMInterface {


    @GET("weather")
    public Call<WeatherResponse> getWeather(@Query("q") String cityInfo,
                                            @Query("appid") String APIKey);


}
