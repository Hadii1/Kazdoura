package com.Czynt.kazdoura.Login.PhoneAuth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.Czynt.kazdoura.Home;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

class PhoneAuthModel {


    interface CallbacksFinished {


        void verificationFailed(String s);

        void registerSuccess();

        void registerFailed(String s);

        void CodeSent(String verificationId);
    }

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private CallbacksFinished viewModelListener;
    private static  PhoneAuthModel model;
    private static final String TAG = "PhoneAuthModel";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Home home;

    static synchronized PhoneAuthModel getInstance() {
        if (model == null) {
            model = new PhoneAuthModel();
        }
        return model;
    }


    private PhoneAuthModel() {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        if (home == null) {
            home = new Home();
        }


    }


    void initFireBaseCallbacks(CallbacksFinished listener) {

        viewModelListener = listener;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
                Log.d(TAG, "state verify success");


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                String s;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    s = "Invalid Phone Number";

                } else if (e instanceof FirebaseNetworkException) {

                    s = "Poor Internet Connection";

                } else {

                    s = "An Error occurred, Try again.";
                }

                viewModelListener.verificationFailed(s);

                Log.d(TAG, "verification failed with exception " + e);


            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "code Sent");
                mResendToken = token;
                mVerificationId = verificationId;
                viewModelListener.CodeSent(mVerificationId);


            }

        };
    }

    void phoneLogin(String number) {
        Log.d(TAG, number);
        String phoneNumber = "+961" + number;
        Log.d(TAG, "code Sent");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                1,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                home,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(home, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        viewModelListener.registerSuccess();
                    } else {

                        String s;
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            s = "Invalid Code";

                        } else if (task.getException() instanceof FirebaseNetworkException) {

                            s = "Poor Internet Connection";

                        } else {

                            s = "An error occurred, Try again";


                        }

                        viewModelListener.registerFailed(s);
                    }
                });
    }

    void resendVerificationCode(String number) {
        String phoneNumber = "+961" + number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                1,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                home,               // Activity (for callback binding)
                mCallbacks,
                mResendToken
        );

    }

    void clearReferences() {
        if (viewModelListener != null) {
            viewModelListener = null;
        }
    }


}
