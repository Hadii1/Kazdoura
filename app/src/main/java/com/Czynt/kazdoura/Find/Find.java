package com.Czynt.kazdoura.Find;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Czynt.kazdoura.Home;
import com.Czynt.kazdoura.LocationBottomSheetFragment;
import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.Store;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class Find extends Fragment implements View.OnClickListener, Home.onLocationCallbacks {
    private static final String TAG = "FindFragment";
    private static final int NO_STORES_NO_NETWORK = 0;
    private static final int STORES_AVAILABLE = 1;
    private static final int NO_STORES = 2;
    private static final int UPDATING = 3;
    private static final int UPDATING_FINISHED = 4;
    private static final int FAILED = 5;
    private FindAdapter findAdapter;
    private NestedScrollView filterLayout;
    private BottomSheetBehavior sheetBehavior;
    private AppBarLayout appBarLayout;
    private SharedPreferences prefs;
    private Button locationBtn;
    private LottieAnimationView loadingAnim, sadAnim;
    private FindViewModel findViewModel;
    private TextView typeAndNumber;
    private RecyclerView rvFind;
    private ArrayList<Store> storesArray;
    private static String type;
    private static boolean freshStart = true;
    private LinearLayout noWifiLayout;


    public Find() {
        Log.d(TAG, "Find Constructor");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");


        prefs = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);

        //After Installation, Hamra and restaurants are the default
        if (prefs.getBoolean("firstLaunch", true)) {

            prefs.edit().putString("Latitude", "33.8966").apply();
            prefs.edit().putString("Longitude", "35.4823").apply();
            prefs.edit().putString("userLocation", "Hamra").apply();
            prefs.edit().putString("type", "Restaurants").apply();


        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: ");

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");


        View v = inflater.inflate(R.layout.find, container, false);


        findViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(FindViewModel.class);

        findViewModel.getStores().observe(getViewLifecycleOwner(), stores -> {

            storesArray = stores;

            if (storesArray.size() != 0) {

                updateUi(STORES_AVAILABLE);

            }


        });

        findViewModel.getNoStoresAvailable().observe(getViewLifecycleOwner(), noStores -> {


            if (noStores && !((Home) getActivity()).isNetworkAvailable()) {

                updateUi(NO_STORES_NO_NETWORK);

            } else if (noStores) {

                updateUi(NO_STORES);


            }
        });

        findViewModel.getIsUpdating().observe(getViewLifecycleOwner(), aBoolean -> {

            if (aBoolean) {

                updateUi(UPDATING);

            } else {

                updateUi(UPDATING_FINISHED);

            }
        });


        findViewModel.getFailureFlag().observe(getViewLifecycleOwner(), aBoolean -> {

            if (aBoolean) {

                updateUi(FAILED);

            }

        });


        initViews(v);

        if (freshStart) {
            freshStart = false;
            setLocation();
        }

        return v;
    }

    private void setLocation() {

        assert getActivity() != null;

        if (((Home) getActivity()).isNetworkAvailable()) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "Permission is granted");

                ((Home) getActivity()).getLocation(this, true);

            } else {

                findViewModel.typeFilterClicked(type);

            }

        } else {
            updateUi(NO_STORES_NO_NETWORK);
        }
    }

    private void initViews(View v) {

        Log.d(TAG, "initViews: ");

        appBarLayout = v.findViewById(R.id.barLayout);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior barBehavior = new AppBarLayout.Behavior();
        barBehavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(barBehavior);

        filterLayout = v.findViewById(R.id.nsvFilter);

        rvFind = v.findViewById(R.id.rvFind);
        rvFind.setHasFixedSize(true);
        rvFind.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (storesArray == null) {
            storesArray = new ArrayList<>();
        }

        findAdapter = new FindAdapter(getActivity(), storesArray);
        rvFind.setAdapter(findAdapter);


        sheetBehavior = BottomSheetBehavior.from(filterLayout);
        sheetBehavior.setHideable(false);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    appBarLayout.setExpanded(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        typeAndNumber = v.findViewById(R.id.typeAndNumberTv);

        loadingAnim = v.findViewById(R.id.loadingAnim);
        sadAnim = v.findViewById(R.id.sadAnim);

        noWifiLayout = v.findViewById(R.id.noWifiLayout);
        TextView tryAgain = v.findViewById(R.id.tvTryAgain);
        tryAgain.setPaintFlags(tryAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tryAgain.setOnClickListener(this);

        locationBtn = v.findViewById(R.id.locationButton);
        locationBtn.setText(prefs.getString("userLocation", "default for userLocation"));
        locationBtn.setOnClickListener(this);
        setUpImageButtons(v);


        if (type == null) {

            type = prefs.getString("type", "Restaurants");

        }


    }


    private void updateUi(int state) {
        switch (state) {
            case STORES_AVAILABLE:
                Log.d(TAG, "updateUi: Stores avaialble: " + storesArray.size());

                if (rvFind.getVisibility() == View.INVISIBLE) {
                    rvFind.setVisibility(View.VISIBLE);
                }
                findAdapter.setData(storesArray);
                typeAndNumber.setText(storesArray.size() + "+ " + type + " Nearby");
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.swip_in_right));

                break;

            case NO_STORES_NO_NETWORK:
                Log.d(TAG, "updateUi: noStores noNetwork");
                noWifiLayout.setVisibility(View.VISIBLE);
                typeAndNumber.setVisibility(View.INVISIBLE);

                break;

            case NO_STORES:

                Log.d(TAG, "updateUi: noStores");
                typeAndNumber.setText("There were no " + type + " found near you.");
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                sadAnim.setVisibility(View.VISIBLE);
                sadAnim.playAnimation();
                rvFind.setVisibility(View.INVISIBLE);

                break;

            case UPDATING:
                Log.d(TAG, "updateUi: Updating");
                typeAndNumber.setTextColor(getResources().getColor(R.color.textColor));
                if (sadAnim.getVisibility() == View.VISIBLE) {
                    sadAnim.setVisibility(View.INVISIBLE);
                    sadAnim.cancelAnimation();

                }

                if (noWifiLayout.getVisibility() == View.VISIBLE) {
                    noWifiLayout.setVisibility(View.INVISIBLE);
                }

                typeAndNumber.setVisibility(View.INVISIBLE);
                rvFind.setVisibility(View.INVISIBLE);
                loadingAnim.setVisibility(View.VISIBLE);
                loadingAnim.playAnimation();

                break;


            case UPDATING_FINISHED:
                Log.d(TAG, "updateUi: UpdatingFinished");
                appBarLayout.setExpanded(true);
                loadingAnim.cancelAnimation();
                loadingAnim.setVisibility(View.INVISIBLE);
                break;


            case FAILED:

                Log.d(TAG, "updateUi: Failed");
                showIndefiniteSnackBar("An Error Occurred");
                sadAnim.setVisibility(View.VISIBLE);
                sadAnim.playAnimation();
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.setTextColor(getResources().getColor(R.color.error));
                typeAndNumber.setText("Error");
                typeAndNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                break;


        }
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (prefs.getBoolean("firstLaunch", true)) {

            prefs.edit().putBoolean("firstLaunch", false).apply();
            showLocationBottomSheet();

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        rvFind.setVisibility(View.INVISIBLE);
    }

    private void showLocationBottomSheet() {

        LocationBottomSheetFragment locationBottomSheetFragment = new LocationBottomSheetFragment();
        assert getFragmentManager() != null;
        locationBottomSheetFragment.show(getChildFragmentManager(), locationBottomSheetFragment.getTag());

    }


    private void setUpImageButtons(View v) {

        String[] types = {"restaurants", "snacks", "bakeries", "sweets", "juices", "liquor", "seaFood", "cafes", "bars", "lounges"
                , "beachResorts", "clubs", "pubs", "libraries", "musicStores", "musicStudios", "artGalleries", "photographyStudios", "clothing"
                , "jewellery", "accessories", "perfumes", "watches", "phones", "computers", "consumer", "dvdShops", "homeAppliances", "pharmacies",
                "gyms", "barbers", "tattoos", "spas", "cosmetics", "optometrists", "stations", "parkingLots", "rental", "cars", "motorcycles", "repairServices"
                , "petStores", "vets", "zoos", "groceries", "superMarkets", "oneDollarShops"};


        for (String x : types) {

            int resID = getResources().getIdentifier(x, "id", Objects.requireNonNull(getActivity()).getPackageName());
            ImageButton typeButton = v.findViewById(resID);
            typeButton.setOnClickListener(v1 -> {

                        type = v1.getTag().toString();
                        findViewModel.typeFilterClicked(type);
                        Log.d(TAG, "setUpImageButtons: " + type + " clicked");
                        filterLayout.smoothScrollTo(0, 0);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        appBarLayout.setExpanded(true);
                        prefs.edit().putString("type", type).apply();
                    }

            );
        }


    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.locationButton) {
            showLocationBottomSheet();
        }

        if (v.getId() == R.id.tvTryAgain) {
            findViewModel.typeFilterClicked(type);
        }


    }


    private void showDefiniteSnackBar(String s) {

        Snackbar snackbar = Snackbar.make(locationBtn, s, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.smokyWhite));
        snackbar.setTextColor(getResources().getColor(R.color.textColor));
        snackbar.show();

    }

    private void showIndefiniteSnackBar(String s) {

        Snackbar snackbar = Snackbar.make(locationBtn, s, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.smokyWhite));
        snackbar.setTextColor(getResources().getColor(R.color.textColor));
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.setAction("Try again", v -> {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            findViewModel.typeFilterClicked(type);

        }).show();

    }

    @Override
    public void LocationSuccess() {
        Log.d(TAG, "LocationSuccess: ");
        findViewModel.typeFilterClicked(type);
    }

    @Override
    public void LocationFailed() {
        showDefiniteSnackBar("Location Auto Detection Failed");
    }

    @Override
    public void SameLocation() {
        showDefiniteSnackBar("Same Location");
    }


    @Override
    public void addressSuccess(String cityName) {
        locationBtn.post(() -> locationBtn.setText(cityName));
    }


}

