package com.Czynt.kazdoura.Login;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.Czynt.kazdoura.Utils.SingleLiveEvent;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;


public class FacebookLogin {

    private static final String TAG = "FacebookLogin";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CallbackManager callback;
    private final SingleLiveEvent<Boolean> fbLoginCanceled = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> fbLoginError = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> shouldNavigate = new SingleLiveEvent<>();



    @Inject
    public FacebookLogin(FirebaseAuth mAuth, FirebaseFirestore db) {

        this.db = db;
        this.mAuth = mAuth;

        callback = CallbackManager.Factory.create();

        initCallbacks();

    }

    LiveData<Boolean> getFbLoginCanceled() {
        return fbLoginCanceled;
    }

    LiveData<String> getFbLoginError() {
        return fbLoginError;
    }

    LiveData<Boolean> getShouldNavigate() {
        return shouldNavigate;
    }

    private void initCallbacks() {
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
                        fbLoginCanceled.setValue(true);

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "Facebook: Exception" + exception);
                        fbLoginError.setValue("An Error Occured");
                    }
                });
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

                        shouldNavigate.setValue(true);

                    } else {
                        Log.d(TAG, "signInWithCredential:failure", task.getException());

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                            fbLoginError.setValue("An account already exists with this email address");

                        } else if (task.getException() instanceof FirebaseNetworkException) {

                            fbLoginError.setValue("Poor Internet Connection");

                        } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {

                            fbLoginError.setValue("Invalid Account");

                        }
                        // If sign in fails, display a message to the user.

                        else {

                            fbLoginError.setValue("An error Occurred, Try again");

                        }
                    }

                });
    }

    CallbackManager getCallback() {
        return callback;
    }
}
