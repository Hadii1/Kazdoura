package com.Czynt.kazdoura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class Profile extends Fragment {


    private static final String TAG = "Profile Fragment";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


}
