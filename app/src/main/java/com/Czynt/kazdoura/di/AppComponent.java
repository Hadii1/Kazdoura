package com.Czynt.kazdoura.di;


import android.app.Application;

import com.Czynt.kazdoura.Utils.BaseApplication;
import com.Czynt.kazdoura.di.Modules.AppModule;
import com.Czynt.kazdoura.di.Modules.ViewModelModules;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AppModule.class,
                ViewModelModules.class,
                AndroidSupportInjectionModule.class,
                BuildersModule.class
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {


    @Component.Builder
    interface Builder {

        AppComponent build();

        @BindsInstance
        Builder application(Application application);


    }

}
