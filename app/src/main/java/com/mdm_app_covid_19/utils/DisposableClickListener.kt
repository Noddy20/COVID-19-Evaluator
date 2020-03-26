package com.mdm_app_covid_19.utils

import android.util.Log
import io.reactivex.observers.DisposableObserver

class DisposableClickListener<T>(private val onRxClick: (t: T) -> Unit) : DisposableObserver<T>(){

    override fun onComplete() {
        Log.v("DisposableClickListener", "onComplete")
    }

    override fun onNext(t: T) {
        Log.v("DisposableClickListener", "onNext")
        onRxClick(t)
    }

    override fun onError(e: Throwable) {
        Log.e("DisposableClickListener", "onError $e")
    }

    //abstract fun onRxClick(t: T)

}