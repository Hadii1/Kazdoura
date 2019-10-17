package com.Czynt.kazdoura.ManualLocation;

import android.app.Application;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Czynt.kazdoura.Utils.GeoCoderUseCase;
import com.Czynt.kazdoura.Utils.SharedPreferencesUtil;
import com.Czynt.kazdoura.Utils.SingleLiveEvent;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class ManualLocationViewModel extends ViewModel {

    final private GeoCoderUseCase geoCoder;
    final private SingleLiveEvent<Boolean> isUpdating = new SingleLiveEvent<>();
    private final MutableLiveData<String> cityName = new MutableLiveData<>();
    private SharedPreferencesUtil prefs;


    @Inject
    public ManualLocationViewModel(GeoCoderUseCase geocoder, SharedPreferencesUtil prefs) {

        this.geoCoder = geocoder;
        this.prefs = prefs;

    }

    LatLng getLocationFromPrefs() {
        return prefs.getUserLocation();
    }

    void fetchCityName(double lat, double lng) {

        isUpdating.setValue(true);

        cityName.postValue(geoCoder.fetchAddress(lat, lng));

    }

    void locateButtonPressed(double lat, double lng) {
        prefs.saveLocation(lat, lng);
    }

    LiveData<String> getCityName() {
        return cityName;
    }

    public SingleLiveEvent<Boolean> getIsUpdating() {
        return isUpdating;
    }
}
