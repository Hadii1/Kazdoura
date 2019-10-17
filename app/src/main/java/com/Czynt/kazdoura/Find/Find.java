package com.Czynt.kazdoura.Find;

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
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Czynt.kazdoura.Home;
import com.Czynt.kazdoura.LocationBottomSheetFragment;
import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.Store;
import com.Czynt.kazdoura.Utils.LocationManager;
import com.Czynt.kazdoura.Utils.SharedPreferencesUtil;
import com.Czynt.kazdoura.di.ViewModels.ViewModelProviderFactory;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class Find extends DaggerFragment implements View.OnClickListener {
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
    private Button locationBtn;
    private FindViewModel findViewModel;
    private LottieAnimationView loadingAnim, sadAnim;
    private TextView typeAndNumber, tryAgainError;
    private RecyclerView rvFind;
    private ArrayList<Store> storesArray;
    private static String type;
    private static boolean freshStart = true;
    private LinearLayout noWifiLayout;

    @Inject
    LocationManager locationManager;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Inject
    SharedPreferencesUtil prefs;


    public Find() {
        Log.d(TAG, "Find Constructor");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");

        findViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), providerFactory).get(FindViewModel.class);

        findViewModel.initIfFirstLaunch();

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


        setLiveDataObservers();

        initViews(v);

        return v;
    }

    private void setLiveDataObservers() {

        // Observing the location fetching process live data (it's from the activity because the getLocation()
        // needs an instance of the activity that can't be put in a singleton Model aka MainModel (Memory leak))


        locationManager.getLocationFetchingFailed().observe(getViewLifecycleOwner(), failed -> {
            if (failed) {
                Snackbar.make(locationBtn, "Location Auto Detection Failed", Snackbar.LENGTH_LONG).show();
            }
        });


        locationManager.getUserLocation().observe(getViewLifecycleOwner(), latLng -> findViewModel.processLocationChange(latLng));

        findViewModel.getSameLocation().observe(getViewLifecycleOwner(), sameLocation -> Snackbar.make(locationBtn, "Same Location", Snackbar.LENGTH_LONG).show());

        findViewModel.getCityName().observe(getViewLifecycleOwner(), s -> locationBtn.setText(s));


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

            updateUi(FAILED);

        });


        locationManager.getUserLocation().observe(getViewLifecycleOwner(), latLng -> findViewModel.typeFilterClicked(type));
        locationManager.getLocationFetchingFailed().observe(getViewLifecycleOwner(), LocFetchingFailed -> Snackbar.make(locationBtn, "Location Auto Detection failed", Snackbar.LENGTH_LONG).show());
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


        tryAgainError = v.findViewById(R.id.tryAgainText);
        tryAgainError.setPaintFlags(tryAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tryAgainError.setOnClickListener(this);


        locationBtn = v.findViewById(R.id.locationButton);
        locationBtn.setText(prefs.getString("userLocation", "default for userLocation"));
        locationBtn.setOnClickListener(this);
        setUpImageButtons(v);


        if (type == null) {

            type = prefs.getString(prefs.type, "Restaurants");

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
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.setText(storesArray.size() + "+ " + type + " Nearby");
                typeAndNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.swip_in_right));

                break;

            case NO_STORES_NO_NETWORK:
                Log.d(TAG, "updateUi: noStores noNetwork");
                noWifiLayout.setVisibility(View.VISIBLE);
                typeAndNumber.setVisibility(View.INVISIBLE);

                break;

            case NO_STORES:

                Log.d(TAG, "updateUi: noStores");
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.setText("There were no " + type + " found near you.");
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

                if (tryAgainError.getVisibility() == View.VISIBLE) {
                    tryAgainError.setVisibility(View.INVISIBLE);
                }

                typeAndNumber.setVisibility(View.GONE);
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

                sadAnim.setVisibility(View.VISIBLE);
                sadAnim.playAnimation();
                tryAgainError.setVisibility(View.VISIBLE);
                typeAndNumber.setVisibility(View.VISIBLE);
                typeAndNumber.setTextColor(getResources().getColor(R.color.error));
                typeAndNumber.setText(findViewModel.getException());
                typeAndNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));

                break;


        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (prefs.getBoolean(prefs.firstLaunch, true)) {
            prefs.putBoolean(prefs.firstLaunch, false);
            showLocationBottomSheet();

        }


    }

    private void showLocationBottomSheet() {

        LocationBottomSheetFragment locationBottomSheetFragment = new LocationBottomSheetFragment(locationManager);
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
                        filterLayout.smoothScrollTo(0, 0);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        appBarLayout.setExpanded(true);
                    }

            );
        }


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.locationButton) {
            showLocationBottomSheet();
        }

        //The first view is when the wifi is off and the second is when any other types of error occur
        if (v.getId() == R.id.tvTryAgain || v.getId() == R.id.tryAgainText) {
            findViewModel.typeFilterClicked(type);
        }


    }


}

