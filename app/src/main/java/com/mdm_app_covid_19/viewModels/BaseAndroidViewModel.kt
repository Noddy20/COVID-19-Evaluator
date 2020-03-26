package com.mdm_app_covid_19.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseAndroidViewModel(application: Application): AndroidViewModel(application){

    protected val compositeDisposable by lazy { CompositeDisposable() }



    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}