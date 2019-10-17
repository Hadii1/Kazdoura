package com.Czynt.kazdoura.ManualLocation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.di.ViewModels.ViewModelProviderFactory;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class ManualLocation extends DaggerFragment implements OnMapReadyCallback, View.OnClickListener {

    private final static String TAG = "ManualLocation";
    private LatLng userLocation;
    private GoogleMap googleMap;
    private MapView mapView;
    private LottieAnimationView marker;
    private TextView locationName;
    private ManualLocationViewModel viewModel;


    @Inject
    ViewModelProviderFactory providerFactory;

    public ManualLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        initViews(v);

        setObserver();

        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        mapView.getMapAsync(this);


        userLocation = viewModel.getLocationFromPrefs();

        return v;
    }

    private void setObserver() {

        viewModel.getCityName().observe(getViewLifecycleOwner(), s -> {

            locationName.setText(s);


        });

        viewModel.getIsUpdating().observe(getViewLifecycleOwner(), isUpdating -> {
            if (isUpdating) {

            }
        });
    }

    private void initViews(View v) {

        viewModel = ViewModelProviders.of(this, providerFactory).get(ManualLocationViewModel.class);

        mapView = v.findViewById(R.id.map);
        marker = v.findViewById(R.id.mapMarker);
        locationName = v.findViewById(R.id.locationName);

        Button locate = v.findViewById(R.id.bLocate);
        locate.setOnClickListener(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMaps) {

        this.googleMap = googleMaps;

        MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Marker Title").snippet("Marker Description"));
//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            getContext(), R.raw.map_styled));
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
//        }
        // For zooming automatically to the location of the marker


        CameraPosition cameraPosition = new CameraPosition.Builder().target(userLocation).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        marker.setVisibility(View.VISIBLE);
        googleMap.setOnCameraIdleListener(() -> {

            LatLng currentPosition = googleMap.getCameraPosition().target;

            viewModel.fetchCityName(currentPosition.latitude, currentPosition.longitude);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        googleMap.clear();
        mapView.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bLocate) {

            viewModel.locateButtonPressed(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude);
            Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment).navigate(R.id.action_mapFragment_to_Find);

        }
    }
}

