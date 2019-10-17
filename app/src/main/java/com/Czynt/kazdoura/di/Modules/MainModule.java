package com.Czynt.kazdoura.di.Modules;


import android.content.Context;
import android.location.Geocoder;

import java.util.Locale;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class MainModule {


    @Provides
    static Geocoder provideGeocoder(Context context) {
        return new Geocoder(context, Locale.getDefault());
    }


}
