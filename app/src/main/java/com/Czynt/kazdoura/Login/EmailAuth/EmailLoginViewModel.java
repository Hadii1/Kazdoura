package com.Czynt.kazdoura.Login.EmailAuth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EmailLoginViewModel extends ViewModel implements EmailLoginModel.OnAuthenticationResult {
    private EmailLoginModel model;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<Boolean> signInSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private String exception;


    public EmailLoginViewModel() {
        model = EmailLoginModel.getInstance();
    }

    LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    LiveData<Boolean> getSignInSuccess() {
        return signInSuccess;
    }

    LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }


    void signUpPressed(String email, String password, String userName) {
        isUpdating.setValue(true);
        model.createNewUser(email, password, userName, this);

    }


    void signInPressed(String email, String password) {
        isUpdating.setValue(true);
        model.signInExistingUser(email, password, this);
    }


    private void setException(String exception) {
        this.exception = exception;
    }

    String getException() {
        return exception;
    }


    @Override
    public void signInFailed(String e) {
        setException(e);
        isUpdating.setValue(false);
        signInSuccess.setValue(false);

    }

    @Override
    public void processSuccess() {
        isUpdating.setValue(false);
        registerSuccess.setValue(true);
    }

    @Override
    public void registerFailed(String e) {
        setException(e);
        isUpdating.setValue(false);
        registerSuccess.setValue(false);


    }

    @Override
    protected void onCleared() {
        super.onCleared();
        model.clearReference();
    }
}
