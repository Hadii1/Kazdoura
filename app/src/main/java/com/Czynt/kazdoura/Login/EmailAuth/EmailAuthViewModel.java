package com.Czynt.kazdoura.Login.EmailAuth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class EmailAuthViewModel extends ViewModel {

    private EmailAuthModel model;


    @Inject
    public EmailAuthViewModel(EmailAuthModel model) {
      this.model = model;
    }

    LiveData<Boolean> getIsUpdating() {
        return model.getIsUpdating();
    }

    LiveData<Boolean> getRegisterSuccess() {
        return model.getRegisterSuccess();
    }


    void signUpPressed(String email, String password, String userName) {
        model.createNewUser(email, password, userName);

    }


    void signInPressed(String email, String password) {
        model.signInExistingUser(email, password);
    }


    String getException() {
        return model.getException();
    }


}
