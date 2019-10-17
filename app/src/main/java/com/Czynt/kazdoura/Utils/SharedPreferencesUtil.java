package com.Czynt.kazdoura.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class SharedPreferencesUtil {


    private SharedPreferences prefs;
    public final String firstLaunch = "firstLaunch";
    private final String latitude = "latitude";
    private final String longitude = "longitude";
    public final String type = "type";
    final String userLocation = "userLocation";


    @Inject
    public SharedPreferencesUtil(Context context) {

        prefs = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

    }



    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, Boolean value) {
        prefs.edit().putBoolean(key, value).apply();

    }


    public Boolean getBoolean(String key, Boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);

    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);

    }

    public LatLng getUserLocation() {
        return new LatLng(Double.parseDouble(getString(latitude, "0")), Double.parseDouble(getString(longitude, "0")));
    }

    public boolean isSameLocation(double lat, double lng) {
        return Double.toString(lat).equals(getString(latitude, "0"))
                && Double.toString(lng).equals(getString(longitude, "0"));
    }

    public void saveLocation(double lat, double lng) {

        String latitude = Double.toString(lat);
        String longitude = Double.toHexString(lng);

        putString(this.latitude, latitude);
        putString(this.longitude, longitude);
    }

}
