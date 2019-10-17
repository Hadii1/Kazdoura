package com.Czynt.kazdoura.di.Modules;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.Czynt.kazdoura.Find.FindViewModel;
import com.Czynt.kazdoura.Login.EmailAuth.EmailAuthViewModel;
import com.Czynt.kazdoura.Login.PhoneAuth.PhoneAuthViewModel;
import com.Czynt.kazdoura.di.ViewModels.ViewModelKey;
import com.Czynt.kazdoura.di.ViewModels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModules {


    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);


    @Binds
    @IntoMap
    @ViewModelKey(FindViewModel.class)
    abstract ViewModel provideFindViewModel(FindViewModel viewModel);


    @Binds
    @IntoMap
    @ViewModelKey(EmailAuthViewModel.class)
    abstract ViewModel provideEmailAuthViewModel(EmailAuthViewModel viewModel);


    @Binds
    @IntoMap
    @ViewModelKey(PhoneAuthViewModel.class)
    abstract ViewModel providePhoneAuthViewModel(PhoneAuthViewModel viewModel);



}


