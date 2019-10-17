package com.Czynt.kazdoura;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.Czynt.kazdoura.Utils.BaseActivity;
import com.Czynt.kazdoura.Utils.LocationManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import javax.inject.Inject;

import mumayank.com.airlocationlibrary.AirLocation;


public class Home extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView nav;
    private NavController navController;
    private AirLocation airLocation;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.colorOrHideStatusBar();

        setContentView(R.layout.activity_home);

        initViews();

        checkAuth();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.Find) {
            if (this.isLocationPermissionGranted()) {
                getLocation();
            }
        }
    }


    private void initViews() {

        nav = findViewById(R.id.bottom_navigation);

        navController = Navigation.findNavController(Home.this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(nav, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.loginFrag || destination.getId() == R.id.emailLogin ||
                    destination.getId() == R.id.phoneAuth || destination.getId() == R.id.mapFragment) {

                nav.setVisibility(View.GONE);

            } else {
                nav.setVisibility(View.VISIBLE);
            }
        });


    }

    private void checkAuth() {


        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            NavInflater navInflater = navController.getNavInflater();
            NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
            graph.setStartDestination(R.id.Login);
            navController.setGraph(graph);
        }

    }

    public void getLocation() {

        airLocation = new AirLocation(this, true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {

                Log.d(TAG, "latitude " + location.getLatitude() + " longitude " + location.getLongitude());
                locationManager.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));

            }


            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
                locationManager.setLocationFetchingFailed(true);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (airLocation != null) {
            airLocation.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (airLocation != null) {
            airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {

        if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.Find) {
            return;
        }
        super.onBackPressed();
    }


}



