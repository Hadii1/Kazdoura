package com.Czynt.kazdoura.Login.EmailAuth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class EmailLogin extends Fragment {

    private Button bSignIn, bSignUp;
    private LottieAnimationView loader;
    private EditText etName, etEmail, etPassword;
    private EmailLoginViewModel emailViewModel;
    private TextInputLayout passwordLayout;
    private ConstraintLayout contentLayout;
    private NavController navController;

    private String username, email, password;

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

    public EmailLogin() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.email_login, container, false);

        emailViewModel = ViewModelProviders.of(this).get(EmailLoginViewModel.class);

        emailViewModel.getIsUpdating().observe(getViewLifecycleOwner(), isUpdating -> {
            if (isUpdating) {
                showProgressBar();
            } else {
                hideProgressBar();
            }
        });


        emailViewModel.getSignInSuccess().observe(getViewLifecycleOwner(), signInSuccess -> {

            if (signInSuccess) {

                navController.navigate(R.id.action_global_Find);

            } else {

                loginFailed(emailViewModel.getException());

            }

        });

        emailViewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), registerSuccess -> {

            if (registerSuccess) {

                navController.navigate(R.id.action_global_Find);

            }

        });

        initViews(v);

        return v;

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


        loader = v.findViewById(R.id.loader);


    }


    private void loginFailed(String e) {
        Snackbar.make(bSignUp, e, Snackbar.LENGTH_SHORT).show();

    }


    private void showProgressBar() {
        bSignIn.setEnabled(false);
        bSignUp.setEnabled(false);
        contentLayout.setAlpha(0.6f);
        loader.setVisibility(View.VISIBLE);
        loader.playAnimation();
    }


    private void hideProgressBar() {
        bSignIn.setEnabled(true);
        bSignUp.setEnabled(true);
        contentLayout.setAlpha(1);
        loader.setVisibility(View.INVISIBLE);
        loader.cancelAnimation();

    }


}
