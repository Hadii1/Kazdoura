package com.Czynt.kazdoura;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mumayank.com.airlocationlibrary.AirLocation;


public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private BottomNavigationView nav;
    private NavController navController;
    private AirLocation airLocation;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        colorOrHideStatusBar();

        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate");

        prefs = getPreferences(MODE_PRIVATE);


        initViews();

        checkAuth();


    }

    private void checkAuth() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            assert navHost != null;
            NavController navController = navHost.getNavController();
            NavInflater navInflater = navController.getNavInflater();
            NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
            graph.setStartDestination(R.id.Login);
            navController.setGraph(graph);
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
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

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


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNoConnectionDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Home.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.internet_connection, null);
        dialogBuilder.setView(dialogView);
        TextView tryAgain = dialogView.findViewById(R.id.tvTryAgain);
        tryAgain.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        AlertDialog alertDialog = dialogBuilder.create();
        tryAgain.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                alertDialog.dismiss();
            } else {

                Snackbar snackbar = Snackbar.make(tryAgain, "No Internet Connection", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.smokyWhite));
                snackbar.setTextColor(getResources().getColor(R.color.textColor));
                snackbar.show();


            }

        });

        alertDialog.show();

    }

    private void colorOrHideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }


    public void getLocation(onLocationCallbacks locationCallback, boolean freshStartOfApp) {

        airLocation = new AirLocation(Home.this, true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NonNull Location location) {
                Log.d(TAG, "latitude " + location.getLatitude() + " longitude " + location.getLatitude());

                double lat = location.getLatitude();
                double lng = location.getLongitude();


                String latitude = Double.toString(lat);
                String longitude = Double.toString(lng);

                boolean samePlace = latitude.equals(prefs.getString("latitude", "0"))
                        && longitude.equals(prefs.getString("longitude", "0"));

                if (!samePlace) {

                    Log.d(TAG, "Not the same Address");

                    prefs.edit().putString("latitude", latitude).apply();
                    prefs.edit().putString("longitude", longitude).apply();


                    if (locationCallback != null) {
                        locationCallback.LocationSuccess();
                    }

                } else if (freshStartOfApp) {

                    Log.d(TAG, "Same address and fresh start");
                    getAddress(locationCallback, lat, lng);

                    if (locationCallback != null) {
                        locationCallback.LocationSuccess();
                    }

                } else {

                    Log.d(TAG, "Same address not fresh start");

                    if (locationCallback != null) {
                        locationCallback.SameLocation();
                    }

                }
            }

            @Override
            public void onFailed(@NonNull AirLocation.LocationFailedEnum locationFailedEnum) {
                if (locationCallback != null) {
                    locationCallback.LocationFailed();
                }
            }
        });

    }


    void getAddress(onLocationCallbacks locationCallback, double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());

        new Thread(() -> {

            try {

                String cityName = "";

                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);

                for (int i = 0; i < 5; i++) {

                    if (addresses.get(i).getLocality() != null) {

                        cityName = addresses.get(i).getLocality();
                        prefs.edit().putString("userLocation", cityName).apply();
                        break;

                    }
                }

                if (cityName.equals("")) {
                    cityName = "Unnamed Location, Lebanon";
                }

                if (locationCallback != null) {

                    locationCallback.addressSuccess(cityName);
                }

                Log.d(TAG, "getAddress: " + cityName);


            } catch (IOException e) {

                if (locationCallback != null) {

                    locationCallback.addressSuccess("Unnamed Location, Lebanon");
                }
                e.printStackTrace();

            }

        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    public interface onLocationCallbacks {

        void LocationSuccess();

        void LocationFailed();

        void SameLocation();

        void addressSuccess(String cityName);

    }
}



