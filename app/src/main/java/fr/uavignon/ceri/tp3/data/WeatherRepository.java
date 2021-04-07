package fr.uavignon.ceri.tp3.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fr.uavignon.ceri.tp3.data.database.CityDao;
import fr.uavignon.ceri.tp3.data.database.WeatherRoomDatabase;
import fr.uavignon.ceri.tp3.data.webservice.OWMInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static fr.uavignon.ceri.tp3.data.database.WeatherRoomDatabase.databaseWriteExecutor;

public class WeatherRepository {

    private static final String TAG = WeatherRepository.class.getSimpleName();

    private LiveData<List<City>> allCities;
    private MutableLiveData<City> selectedCity;

    private CityDao cityDao;
    private volatile MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Throwable> webServiceThrowable;
    private volatile int nbAPIloads=0;

    private static volatile WeatherRepository INSTANCE;
    private final OWMInterface api;

    public synchronized static WeatherRepository get(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new WeatherRepository(application);
        }

        return INSTANCE;
    }

    public WeatherRepository(Application application) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/").addConverterFactory(MoshiConverterFactory.create()).build();

        api = retrofit.create(OWMInterface.class);

        WeatherRoomDatabase db = WeatherRoomDatabase.getDatabase(application);
        cityDao = db.cityDao();
        allCities = cityDao.getAllCities();
        selectedCity = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        isLoading.postValue(Boolean.FALSE);
        webServiceThrowable = new MutableLiveData<>();
    }

    public LiveData<List<City>> getAllCities() {
        return allCities;
    }

    public MutableLiveData<City> getSelectedCity() {
        return selectedCity;
    }




    public long insertCity(City newCity) {
        Future<Long> flong = databaseWriteExecutor.submit(() -> {
            return cityDao.insert(newCity);
        });
        long res = -1;
        try {
            res = flong.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (res != -1)
            selectedCity.setValue(newCity);
        return res;
    }

    public int updateCity(City city) {
        Future<Integer> fint = databaseWriteExecutor.submit(() -> {
            return cityDao.update(city);
        });
        int res = -1;
        try {
            res = fint.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (res != -1)
            selectedCity.setValue(city);
        return res;
    }

    public void deleteCity(long id) {
        databaseWriteExecutor.execute(() -> {
            cityDao.deleteCity(id);
        });
    }

    public void getCity(long id)  {
        Future<City> fcity = databaseWriteExecutor.submit(() -> {
            Log.d(TAG,"selected id="+id);
            return cityDao.getCityById(id);
        });
        try {
            selectedCity.setValue(fcity.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadWeatherCity(City city){
        isLoading.postValue(Boolean.TRUE);
        api.getWeather(city.getName()+",,"+city.getCountryCode().toString(),"45f89cc5cbd9e5a1213e5c13811e4c7d").enqueue(
                new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        WeatherResult.transferInfo(response.body(),city);
                        updateCity(city);
                        if(nbAPIloads==0)
                            isLoading.postValue(Boolean.FALSE);
                        else
                            nbAPIloads-=1;
                        Log.d("API",response.toString());

                        if (nbAPIloads<0)
                            nbAPIloads=0;
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.d("Failure",t.getMessage());
                        if(nbAPIloads==0)
                            isLoading.postValue(Boolean.FALSE);
                        else
                            nbAPIloads-=1;
                        webServiceThrowable.postValue(t);

                        if (nbAPIloads<0)
                            nbAPIloads=0;
                    }


                }


        );


    }

    public void loadWeatherAllCities(){
        allCities = new LiveData<List<City>>(cityDao.getSynchrAllCities()) {

        };
        nbAPIloads = allCities.getValue().size();
        for (int i= 0; i<allCities.getValue().size(); i++){

            loadWeatherCity(allCities.getValue().get(i));

        }

    }

    public MutableLiveData<Boolean> getIsLoading() {
        return this.isLoading;
    }
    public MutableLiveData<Throwable> getWebServiceThrowable() {
        return this.webServiceThrowable;
    }
}
