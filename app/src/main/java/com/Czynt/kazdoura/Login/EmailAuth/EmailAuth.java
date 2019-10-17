package com.Czynt.kazdoura.Login.EmailAuth;

import android.animation.Animator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.di.ViewModels.ViewModelProviderFactory;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class EmailAuth extends DaggerFragment {


    private Button bSignIn, bSignUp;
    private LottieAnimationView loaderAnim, successAnim;
    private EditText etName, etEmail, etPassword;
    private EmailAuthViewModel emailViewModel;
    private TextInputLayout passwordLayout;
    private ConstraintLayout contentLayout;
    private NavController navController;
    private String username, email, password;
    private static final String TAG = "EmailAuth";



    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            username = etName.getText().toString();
            password = etPassword.getText().toString();
            email = etEmail.getText().toString();

            bSignUp.setEnabled(password.length() >= 6 && !username.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches());
            bSignIn.setEnabled(password.length() >= 6 && Patterns.EMAIL_ADDRESS.matcher(email).matches());

            if (password.length() < 6 && password.length() > 0) {

                passwordLayout.setError("password must be at least 6 characters");

            } else {

                passwordLayout.setError("");

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    @Inject
    ViewModelProviderFactory providerFactory;



    public EmailAuth() {
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.email_login, container, false);

        emailViewModel = ViewModelProviders.of(this,providerFactory).get(EmailAuthViewModel.class);


        subscribeObservers();
        initViews(v);

        return v;

    }

    private void subscribeObservers() {
        emailViewModel.getIsUpdating().observe(getViewLifecycleOwner(), isUpdating -> {
            if (isUpdating) {
                showProgressBar();
                etEmail.setEnabled(false);
                etName.setEnabled(false);
                etPassword.setEnabled(false);
            } else {
                hideProgressBar();

                etEmail.setEnabled(true);
                etName.setEnabled(true);
                etPassword.setEnabled(true);
            }
        });


        emailViewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), registerSuccess -> {

            if (registerSuccess) {

                playAnimAndNavigate();

            } else {

                loginFailed(emailViewModel.getException());
            }

        });

    }

    private void playAnimAndNavigate() {

        contentLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
        contentLayout.setAlpha(0.05f);
        successAnim.setVisibility(View.VISIBLE);
        successAnim.playAnimation();


    }


    private void initViews(View v) {

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        contentLayout = v.findViewById(R.id.contentLayout);

        etName = v.findViewById(R.id.etUserName);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        passwordLayout = v.findViewById(R.id.passwordLayout);

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        username = etName.getText().toString();


        etName.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);

        bSignIn = v.findViewById(R.id.bSignIn);
        bSignUp = v.findViewById(R.id.bSignUp);

        bSignIn.setOnClickListener(v1 -> emailViewModel.signInPressed(email, password));
        bSignUp.setOnClickListener(v12 -> emailViewModel.signUpPressed(email, password, username));


        loaderAnim = v.findViewById(R.id.loader);
        successAnim = v.findViewById(R.id.success);

        successAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_global_Find);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }


    private void loginFailed(String e) {
        Snackbar.make(bSignUp, e, Snackbar.LENGTH_SHORT).show();

    }


    private void showProgressBar() {
        bSignIn.setEnabled(false);
        bSignUp.setEnabled(false);
        contentLayout.setAlpha(0.6f);
        loaderAnim.setVisibility(View.VISIBLE);
        loaderAnim.playAnimation();
    }


    private void hideProgressBar() {
        bSignIn.setEnabled(true);
        bSignUp.setEnabled(true);
        contentLayout.setAlpha(1);
        loaderAnim.setVisibility(View.INVISIBLE);
        loaderAnim.cancelAnimation();

    }


}
