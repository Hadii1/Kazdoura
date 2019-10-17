package com.Czynt.kazdoura.Login.PhoneAuth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import javax.inject.Inject;

public class PhoneAuthViewModel extends ViewModel {
    private PhoneAuthModel model;


    @Inject
    public PhoneAuthViewModel(PhoneAuthModel model) {

        this.model = model;
        model.initFireBaseCallbacks();

    }

    void verifyNumber(String number) {
        model.phoneLogin(number);
    }

    void resendCode(String number) {
        model.resendVerificationCode(number);
    }


    void signInWithCredential(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(model.getVerificationId(), code);
        model.signInWithPhoneAuthCredential(credential);
    }


    LiveData<Boolean> getIsUpdating() {
        return model.getIsUpdating();
    }

    LiveData<Boolean> getCodeSentFlag() {
        return model.getCodeSent();
    }


    LiveData<Boolean> getRegistrationFlag() {
        return model.getRegistrationSuccess();
    }

    LiveData<Boolean> getVerificationFlag() {
        return model.getVerificationFailed();
    }


    String getException() {
        return model.getException();
    }


}
