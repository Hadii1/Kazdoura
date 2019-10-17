package com.Czynt.kazdoura.Utils;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;


@SuppressLint("KotlinPropertyAccess")
public class LocationManager {


    private final SharedPreferencesUtil prefs;
    private static final String TAG = "LocationManager";
    private MutableLiveData<LatLng> userLocation = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> locationFetchingFailed = new SingleLiveEvent<>();

    @Inject
    public LocationManager(SharedPreferencesUtil prefs) {
        this.prefs = prefs;
    }


    public void setUserLocation(LatLng userLocation) {
        this.userLocation.setValue(userLocation);
    }


    public void setLocationFetchingFailed(Boolean locationFetchingFailed) {
        this.locationFetchingFailed.setValue(true);
    }



    public LiveData<LatLng> getUserLocation() {
        return userLocation;
    }

    public LiveData<Boolean> getLocationFetchingFailed() {
        return locationFetchingFailed;
    }

}
