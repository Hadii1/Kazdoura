package com.Czynt.kazdoura.Find;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Czynt.kazdoura.Store;

import java.util.ArrayList;

public class FindViewModel extends ViewModel implements FindModel.OnStoresQueryCallback {

    private final static String TAG = "FindViewModel";
    private MutableLiveData<ArrayList<Store>> stores = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<Boolean> noStores = new MutableLiveData<>();
    private MutableLiveData<Boolean> failed = new MutableLiveData<>();
    private FindModel model;


    public FindViewModel() {

        model = FindModel.getInstance();

    }


    LiveData<ArrayList<Store>> getStores() {


        if (stores.getValue() == null) {
            stores.setValue(new ArrayList<>());
        }



        return stores;
    }

    LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    LiveData<Boolean> getFailureFlag() {
        return failed;
    }

    LiveData<Boolean> getNoStoresAvailable() {
        return noStores;
    }

    void typeFilterClicked(String type) {

        isUpdating.setValue(true);

        model.getStores(type, this);

    }

    @Override
    public void getStoresSuccess(MutableLiveData<ArrayList<Store>> stores) {

        noStores.setValue(false);
        isUpdating.setValue(false);

        if (stores.getValue() != null && stores.getValue().size() == 0) {

            noStores.setValue(true);

            Log.d(TAG, "getStoresSuccess: no Stores");

        } else {

            Log.d(TAG, "getStoresSuccess: stores available");

            this.stores.setValue(stores.getValue());
        }



    }

    @Override
    public void getStoresFailed() {

        Log.d(TAG, "getStoresFailed: ");

        noStores.setValue(false);

        isUpdating.setValue(false);

        failed.setValue(true);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: ");


        model.clearReference();

        if (stores != null) {

            stores = null;
        }

    }
}
