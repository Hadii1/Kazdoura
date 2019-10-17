package com.Czynt.kazdoura.Find;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.Czynt.kazdoura.Store;
import com.Czynt.kazdoura.Utils.GeoCoderUseCase;
import com.Czynt.kazdoura.Utils.SharedPreferencesUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

import javax.inject.Inject;

public class FindModel {


    private static final String TAG = "MainModel";
    private final ArrayList<Store> stores = new ArrayList<>();
    private final SharedPreferencesUtil prefs;
    private final FirebaseFirestore db;
    private String exception;
    private final GeoCoderUseCase geoCoder;
    private final MutableLiveData<ArrayList<Store>> data = new MutableLiveData<>();
    private final MutableLiveData<Boolean> failed = new MutableLiveData<>();
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> noStores = new MutableLiveData<>();


    @Inject
    public FindModel(FirebaseFirestore db, SharedPreferencesUtil prefs,GeoCoderUseCase useCase) {
        this.geoCoder = useCase;
        this.prefs = prefs;
        this.db = db;

        Log.d(TAG, "FindModel: "+ this);

    }

//    public static synchronized MainModel getInstance() {
//        if (model == null) {
//            model = new MainModel();
//        }
//        return model;
//    }


    LiveData<ArrayList<Store>> getStores(String type) {

        isUpdating.setValue(true);
        stores.clear();

        Task<QuerySnapshot> task = db.collection("Stores").whereEqualTo("type", type).limit(25).get(Source.SERVER);

        task.addOnCompleteListener(task1 -> {

            isUpdating.setValue(false);
            assert task1.getResult() != null;

            if (task1.isSuccessful()) {

                for (QueryDocumentSnapshot queryDocumentSnapshot : task1.getResult()) {
                    Store store = queryDocumentSnapshot.toObject(Store.class);
                    stores.add(store);
                }
                if (stores.size() == 0) {
                    noStores.setValue(true);
                }
                data.setValue(stores);

            } else {

                if (task1.getException() instanceof FirebaseNetworkException) {
                    exception = "Poor Internet Connection";
                } else {
                    exception = "Error Loading data";
                }

                data.setValue(new ArrayList<>());
                failed.setValue(true);

            }
        });

        return data;

    }

    public String getException() {
        return exception;
    }

    LiveData<Boolean> getNoStores() {
        return noStores;
    }

    LiveData<Boolean> getFailed() {
        return failed;
    }

    LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    LiveData<String> getCityName() {
        return address;
    }


    void fetchAddress(Context context, double latitude, double longitude) {

        address.postValue(geoCoder.fetchAddress(latitude, longitude));

    }


}
