package com.Czynt.kazdoura.Login.EmailAuth;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

class EmailLoginModel {


    interface OnAuthenticationResult {

        void signInFailed(String exception);

        void processSuccess();

        void registerFailed(String exception);
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private OnAuthenticationResult listener;
    private static EmailLoginModel model;


    static synchronized EmailLoginModel getInstance() {
        if (model == null) {
            model = new EmailLoginModel();
        }
        return model;
    }

    private EmailLoginModel() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }


    }


    void createNewUser(String email, String password, String userName, OnAuthenticationResult listener) {

        this.listener = listener;

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {

            if (task1.isSuccessful()) {
                saveUserData(userName);
                listener.processSuccess();

            } else {
                String s;
                if (task1.getException() instanceof FirebaseAuthInvalidUserException) {
                    s = ("Invalid Information");
                } else if (task1.getException() instanceof FirebaseNetworkException) {
                    s = ("Poor Internet Connection");
                } else {
                    s = ("An error occurred, Try again.");
                }

                listener.registerFailed(s);


            }
        });
    }


    void signInExistingUser(String email, String password, OnAuthenticationResult listener) {

        this.listener = listener;

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {

            if (task1.isSuccessful()) {
                listener.processSuccess();
            } else {

                String s;

                if (task1.getException() instanceof FirebaseNetworkException) {
                    s = ("Poor Connectivity");
                } else if (task1.getException() instanceof FirebaseAuthUserCollisionException) {
                    s = ("An account already exists with this email address");

                } else {
                    s = ("An Error occurred, Try again.");
                }
                listener.signInFailed(s);

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


    void clearReference() {
        if (listener != null) {
            listener = null;
        }
    }

}
