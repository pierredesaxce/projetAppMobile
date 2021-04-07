package fr.uavignon.ceri.tp3;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import fr.uavignon.ceri.tp3.data.City;
import fr.uavignon.ceri.tp3.data.WeatherRepository;

public class ListViewModel extends AndroidViewModel {
    private WeatherRepository repository;
    private LiveData<List<City>> allCities;

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Throwable> webServiceThrowable;

    public ListViewModel (Application application) {
        super(application);
        repository = WeatherRepository.get(application);
        allCities = repository.getAllCities();
        isLoading = new MutableLiveData<>();
        isLoading.postValue(Boolean.FALSE);
        webServiceThrowable = new MutableLiveData<>();
    }

    LiveData<List<City>> getAllCities() {
        return allCities;
    }

    public void deleteCity(long id) {
        repository.deleteCity(id);
    }

    public void loadWeatherAllCities(){
        repository.loadWeatherAllCities();
    }


    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Throwable> getWebServiceThrowable() {
        return webServiceThrowable;
    }
}
