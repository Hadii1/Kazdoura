package com.Czynt.kazdoura;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.Czynt.kazdoura.Find.Find;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;


public class LocationBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private final static String TAG = "LocationBottomFragment";

    public LocationBottomSheetFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_locatipn, container, false);

        TextView useCurrentLoc = v.findViewById(R.id.useCurrentLocation);
        TextView manuallySelectLoc = v.findViewById(R.id.manuallySelectLocation);

        useCurrentLoc.setOnClickListener(this);
        manuallySelectLoc.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.manuallySelectLocation) {
            Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment)
                    .navigate(R.id.action_Find_to_mapFragment);

            this.dismiss();
        }

        if (v.getId() == R.id.useCurrentLocation) {

            ((Home) Objects.requireNonNull(getActivity())).getLocation((Find) getParentFragment(),false);
            this.dismiss();

        }


    }


}
