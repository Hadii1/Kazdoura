package com.Czynt.kazdoura.Find;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.Czynt.kazdoura.Store;
import com.Czynt.kazdoura.Utils.SharedPreferencesUtil;
import com.Czynt.kazdoura.Utils.SingleLiveEvent;
import com.Czynt.kazdoura.di.Annotations.MainScope;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

public class FindViewModel extends ViewModel {


    private FindModel model;
    private static final String TAG = "FindViewModel";
    private SharedPreferencesUtil prefs;
    private Context context;
    private final MutableLiveData<String> type = new MutableLiveData<>();
    private final SingleLiveEvent<Boolean> sameLocation = new SingleLiveEvent<>();
    private final LiveData<ArrayList<Store>> stores = Transformations.switchMap(type, type -> model.getStores(type));


    @Inject
    public FindViewModel(Application application, SharedPreferencesUtil prefs, FindModel model) {


        Log.d(TAG, "FindViewModel: " + this);

        this.model = model;
        this.context = application.getApplicationContext();
        this.prefs = prefs;


    }


    void processLocationChange(LatLng latLng) {
        if (prefs.isSameLocation(latLng.latitude, latLng.longitude)) {

            sameLocation.setValue(true);

        } else {

            model.fetchAddress(context, latLng.latitude, latLng.longitude);

            prefs.saveLocation(latLng.latitude, latLng.longitude);

            typeFilterClicked(type.getValue());
        }
    }

    void initIfFirstLaunch() {
        //After installation,Hamra and restaurants are the default settings
        if (prefs.getBoolean("firstLaunch", true)) {

            prefs.putString("Latitude", "33.8966");
            prefs.putString("Longitude", "35.4823");
            prefs.putString("userLocation", "Hamra");
            prefs.putString("type", "Restaurants");


        }
    }


    void typeFilterClicked(String type) {
        prefs.putString(prefs.type, type);
        this.type.setValue(type);
    }


    LiveData<String> getCityName() {
        return model.getCityName();
    }


    LiveData<ArrayList<Store>> getStores() {
        return stores;
    }

    LiveData<Boolean> getSameLocation() {
        return sameLocation;
    }

    LiveData<Boolean> getIsUpdating() {
        return model.getIsUpdating();
    }

    LiveData<Boolean> getFailureFlag() {
        return model.getFailed();
    }

    LiveData<Boolean> getNoStoresAvailable() {
        return model.getNoStores();
    }

    public String getException() {
        return model.getException();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: ");
    }
}
