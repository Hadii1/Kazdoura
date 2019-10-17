package com.Czynt.kazdoura.Utils;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class GeoCoderUseCase {

    private final Geocoder geocoder;
    private static final String TAG = "GeoCoderUseCase";
    private String cityName;

    @Inject
    public GeoCoderUseCase(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public String fetchAddress(double latitude, double longitude) {

        new Thread(() -> {

            try {

                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 3);

                for (int i = 0; i < addresses.size(); i++) {

                    if (addresses.get(i).getLocality() != null) {

                        cityName = (addresses.get(i).getLocality());

                        break;

                    }
                }


            } catch (IOException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }).start();

        if (cityName != null) return cityName;

        else {
            return "Unnamed Location";
        }
    }


}
