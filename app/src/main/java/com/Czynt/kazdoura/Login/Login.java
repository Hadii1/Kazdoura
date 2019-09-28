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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Login extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private TextView laterSignIn;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CallbackManager callback;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");
        callback = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);

        //Facebook Login
        LoginManager.getInstance().registerCallback(callback,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Facebook:locationRetrievalSuccess");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Facebook:onCancel");
                        showError("Canceled");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "Facebook: Exception" + exception);
                        showError("An Error Occurred");
                    }
                });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
       //fixing a leak that's mostly cause by the os(maybe a false positive)
        if (laterSignIn != null) {
            laterSignIn = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        Log.d(TAG, "onCreateView: ");
        Button bEmail = v.findViewById(R.id.bEmail);
        Button bPhone = v.findViewById(R.id.bPhone);
        Button bFb = v.findViewById(R.id.bFb);
        laterSignIn = v.findViewById(R.id.laterSignIn);

        laterSignIn.setOnClickListener(this);
        bEmail.setOnClickListener(this);
        bFb.setOnClickListener(this);
        bPhone.setOnClickListener(this);

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callback.onActivityResult(requestCode, resultCode, data);
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


    private void showError(String error) {

        Snackbar.make(laterSignIn, error, Snackbar.LENGTH_INDEFINITE).setAction("dismiss", v -> {

        }).show();
    }


    private void handleFacebookAccessToken(AccessToken token) {

        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {

                            String personName = user.getDisplayName();
                            String personEmail = user.getEmail();
                            String personId = user.getUid();

                            Map<String, Object> Users = new HashMap<>();

                            Users.put("name", Objects.requireNonNull(personName));
                            Users.put("email", Objects.requireNonNull(personEmail));
                            Users.put("Id", personId);

                            if (user.getPhotoUrl() != null) {
                                String profilePhoto = user.getPhotoUrl().toString();
                                Users.put("photo", profilePhoto);

                            }

                            db.collection("Users").document(personId).set(Users);
                        }

                        navController.navigate(R.id.action_global_Find);

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                            showError("An account already exists with this email address");

                        } else if (task.getException() instanceof FirebaseNetworkException) {

                            showError("Poor Internet Connection");

                        } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {

                            showError("Invalid account");

                        }
                        // If sign in fails, display a message to the user.

                        else {

                            showError("An error Occurred, Try again.");

                        }
                    }

                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
