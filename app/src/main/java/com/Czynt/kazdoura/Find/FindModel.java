package com.Czynt.kazdoura.Find;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.Czynt.kazdoura.Store;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.util.ArrayList;

class FindModel {

    private FirebaseFirestore db;
    private static final String TAG = "FindModel";
    private final ArrayList<Store> stores = new ArrayList<>();
    private static FindModel findModel;
    private OnStoresQueryCallback callback;

    synchronized static FindModel getInstance() {

        if (findModel == null) {
            findModel = new FindModel();
        }
        return findModel;
    }

    private FindModel() {

        db = FirebaseFirestore.getInstance();

    }


    void getStores(String type, OnStoresQueryCallback callback) {

        this.callback = callback;

        final MutableLiveData<ArrayList<Store>> data = new MutableLiveData<>();

        if (data.getValue() != null) {

            data.getValue().clear();

        }

        stores.clear();


        Task<QuerySnapshot> task = db.collection("Stores").whereEqualTo("type", type).limit(30).get();
        task.addOnSuccessListener(queryDocumentSnapshots -> {

            Log.d(TAG, "getStores Success");

            for (QueryDocumentSnapshot qds : queryDocumentSnapshots) {

                Store store = qds.toObject(Store.class);

                stores.add(store);

            }
            //The following is done on the main thread

            data.setValue(stores);

            this.callback.getStoresSuccess(data);

        });

        task.addOnFailureListener(e -> this.callback.getStoresFailed());


    }

    void clearReference() {
        if (this.callback != null) {
            this.callback = null;
        }
    }


    interface OnStoresQueryCallback {

        void getStoresSuccess(MutableLiveData<ArrayList<Store>> stores);

        void getStoresFailed();
    }
}
