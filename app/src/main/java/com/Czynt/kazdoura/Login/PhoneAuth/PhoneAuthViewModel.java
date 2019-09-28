package com.Czynt.kazdoura.Login.PhoneAuth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneAuthViewModel extends ViewModel implements PhoneAuthModel.CallbacksFinished {
    private PhoneAuthModel model;
    private String verificationId;
    private String exception;
    private MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> codeSent = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();


    public PhoneAuthViewModel() {

        model = PhoneAuthModel.getInstance();
        model.initFireBaseCallbacks(this);

    }


    LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    LiveData<Boolean> getCodeSentFlag() {
        return codeSent;
    }


    LiveData<Boolean> getRegistrationFlag() {
        return registrationSuccess;
    }

    LiveData<Boolean> getVerificationFlag() {
        return verificationSuccess;
    }

    void verifyNumber(String number) {

        isUpdating.setValue(true);
        model.phoneLogin(number);
    }

    void resendCode(String number) {
        isUpdating.setValue(true);
        model.resendVerificationCode(number);
    }


    void signInWithCredential(String code) {

        isUpdating.setValue(true);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        model.signInWithPhoneAuthCredential(credential);
    }


    @Override
    public void verificationFailed(String s) {
        isUpdating.setValue(false);
        setException(s);
        verificationSuccess.setValue(false);
    }

    @Override
    public void registerSuccess() {

        isUpdating.setValue(false);
        registrationSuccess.setValue(true);
    }

    @Override
    public void registerFailed(String s) {
        isUpdating.setValue(false);
        setException(s);

        registrationSuccess.setValue(false);
    }


    @Override
    public void CodeSent(String verificationId) {
        this.verificationId = verificationId;
        codeSent.setValue(true);
        isUpdating.setValue(false);
    }

    private void setException(String exception) {
        this.exception = exception;
    }

    String getException() {
        return exception;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        model.clearReferences();
    }
}
