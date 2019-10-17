package com.Czynt.kazdoura.Login.EmailAuth;

import com.Czynt.kazdoura.Utils.SingleLiveEvent;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class EmailAuthModel {


    private SingleLiveEvent<Boolean> isUpdating = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> registerSuccess = new SingleLiveEvent<>();
    private String exception;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    @Inject
    public EmailAuthModel(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.mAuth = mAuth;
    }


    void createNewUser(String email, String password, String userName) {

        isUpdating.setValue(true);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {

            isUpdating.setValue(false);

            if (task1.isSuccessful()) {
                saveUserData(userName);
                registerSuccess.setValue(true);

            } else {

                if (task1.getException() instanceof FirebaseAuthInvalidUserException) {
                    exception = ("Invalid Information");
                } else if (task1.getException() instanceof FirebaseNetworkException) {
                    exception = ("Poor Internet Connection");
                } else {
                    exception = ("An error occurred, Try again.");
                }

                registerSuccess.setValue(false);


            }
        });
    }


    void signInExistingUser(String email, String password) {

        isUpdating.setValue(true);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {

            isUpdating.setValue(false);

            if (task1.isSuccessful()) {
                registerSuccess.setValue(true);
            } else {


                if (task1.getException() instanceof FirebaseNetworkException) {
                    exception = ("Poor Connectivity");
                } else if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                    exception = ("An account already exists with this email address");

                } else {
                    exception = ("An Error occurred, Try again.");
                }
                registerSuccess.setValue(false);

            }
        });

    }


    private void saveUserData(String userName) {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("provider", "Email");
            userInfo.put("name", userName);
            userInfo.put("email", user.getEmail());
            userInfo.put("id", user.getUid());

            db.collection("Users")
                    .add(userInfo);
        }
    }


    String getException() {
        return exception;
    }

    SingleLiveEvent<Boolean> getIsUpdating() {
        return isUpdating;
    }

    SingleLiveEvent<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }


}
