package com.Czynt.kazdoura.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.facebook.login.LoginManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class Login extends DaggerFragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private TextView laterSignIn;
    private NavController navController;


    @Inject
    FacebookLogin fbLoginUtil;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);

        Button bEmail = v.findViewById(R.id.bEmail);
        Button bPhone = v.findViewById(R.id.bPhone);
        Button bFb = v.findViewById(R.id.bFb);

        laterSignIn = v.findViewById(R.id.laterSignIn);
        laterSignIn.setOnClickListener(this);

        bEmail.setOnClickListener(this);
        bFb.setOnClickListener(this);
        bPhone.setOnClickListener(this);


        setLiveDataObservers();

        return v;
    }

    private void setLiveDataObservers() {

        fbLoginUtil.getFbLoginCanceled().observe(getViewLifecycleOwner(),
                canceled -> Snackbar.make(laterSignIn, "Canceled", Snackbar.LENGTH_LONG).show());

        fbLoginUtil.getFbLoginError().observe(getViewLifecycleOwner(),
                error -> Snackbar.make(laterSignIn, error, Snackbar.LENGTH_LONG).show());

        fbLoginUtil.getShouldNavigate().observe(getViewLifecycleOwner(),
                shouldNavigate -> navController.navigate(R.id.action_global_Find));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbLoginUtil.getCallback().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.laterSignIn) {
            navController.navigate(R.id.action_global_Find);
        }

        if (v.getId() == R.id.bEmail) {
            navController.navigate(R.id.action_loginFrag_to_emailLogin);
        }

        if (v.getId() == R.id.bPhone) {
            navController.navigate(R.id.action_loginFrag_to_phoneAuth);
        }

        if (v.getId() == R.id.bFb) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
