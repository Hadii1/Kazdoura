package com.Czynt.kazdoura.di.Modules;


import android.app.Application;
import android.content.Context;

import com.Czynt.kazdoura.Home;
import com.Czynt.kazdoura.Utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class AppModule {



    @Named("home")
    @Provides
    static Home provideHome() {
        return new Home();
    }

    @Singleton
    @Provides
    static Context provideContext(Application application) {
        return application.getApplicationContext();
    }


    @Singleton
    @Provides
    static FirebaseFirestore provideFirebaseFirestoreDb() {
        return FirebaseFirestore.getInstance();
    }


    @Singleton
    @Provides
    static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    static SharedPreferencesUtil providePreferencesUtil(Context context) {
        return new SharedPreferencesUtil(context);
    }

}
