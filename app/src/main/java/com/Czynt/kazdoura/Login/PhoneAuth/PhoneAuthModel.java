package com.Czynt.kazdoura.Login.PhoneAuth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.Czynt.kazdoura.Home;
import com.Czynt.kazdoura.Utils.SingleLiveEvent;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

public class PhoneAuthModel {


    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private static final String TAG = "PhoneAuthModel";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Home home;
    private SingleLiveEvent <Boolean> codeSent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> verificationFailed = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> registrationSuccess = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> isUpdating = new SingleLiveEvent<>();
    private String exception;
    private FirebaseAuth mAuth;



    @Inject
    public PhoneAuthModel(FirebaseAuth mAuth,@Named("home") Home home) {

        this.mAuth = mAuth;
        this.home=home;

        Log.d(TAG, "PhoneAuthModel: " + this + "\n"+mAuth);

    }


    void initFireBaseCallbacks() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
                Log.d(TAG, "state verify success");


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    exception = "Invalid Phone Number";

                } else if (e instanceof FirebaseNetworkException) {

                    exception = "Poor Internet Connection";

                } else {

                    exception = "An Error occurred, Try again.";
                }


                isUpdating.setValue(false);
              
                verificationFailed.setValue(true);

                Log.d(TAG, "verification failed with exception " + e);


            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "code Sent");
                mResendToken = token;
                mVerificationId = verificationId;
                isUpdating.setValue(false);
                codeSent.setValue(true);


            }

        };
    }

    void phoneLogin(String number) {

        isUpdating.setValue(true);

        Log.d(TAG, number);
        String phoneNumber = "+961" + number;
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
                    isUpdating.setValue(false);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        registrationSuccess.setValue(true);

                    } else {

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            exception = "Invalid Code";

                        } else if (task.getException() instanceof FirebaseNetworkException) {

                            exception = "Poor Internet Connection";

                        } else {

                            exception = "An error occurred, Try again";


                        }

                        registrationSuccess.setValue(false);
                    }
                });
    }

    void resendVerificationCode(String number) {

        isUpdating.setValue(true);
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

     String getVerificationId() {
        return mVerificationId;
    }

    public String getException() {
        return exception;
    }

    MutableLiveData<Boolean> getVerificationFailed() {
        return verificationFailed;
    }

     MutableLiveData<Boolean> getCodeSent() {
        return codeSent;
    }

     MutableLiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    MutableLiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }


}
