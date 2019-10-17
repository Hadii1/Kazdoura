package com.Czynt.kazdoura.di;

import com.Czynt.kazdoura.Find.Find;
import com.Czynt.kazdoura.Home;
import com.Czynt.kazdoura.Login.EmailAuth.EmailAuth;
import com.Czynt.kazdoura.Login.FacebookLogin;
import com.Czynt.kazdoura.Login.Login;
import com.Czynt.kazdoura.Login.PhoneAuth.PhoneAuth;
import com.Czynt.kazdoura.di.Annotations.MainScope;
import com.Czynt.kazdoura.di.Modules.MainModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract Home contributeHomeActivity();


    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract Find contributeFindFragment();


    //Auth Fragments:
    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract FacebookLogin contributeFacebookLoginHelper();

    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract EmailAuth contributeEmailAuth();

    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract PhoneAuth contributePhoneAuth();

    @ContributesAndroidInjector(modules = MainModule.class)
    public abstract Login contributeLoginFragment();


}
