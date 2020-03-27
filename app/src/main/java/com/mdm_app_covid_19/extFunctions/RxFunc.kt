package com.mdm_app_covid_19.extFunctions

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import java.util.concurrent.TimeUnit

operator fun CompositeDisposable.plusAssign(disposable: Disposable?) {
    if (disposable != null && !isDisposed)
        add(disposable)
}

fun DisposableObserver<Int>.addViewClicks(vararg views : View, throttle: Long = 500, unit: TimeUnit = TimeUnit.MILLISECONDS){
    kotlin.runCatching {
        var observable = RxView.clicks(views[0]).map { views[0].id }
        for (i in 1 until views.size) {
            observable = observable.mergeWith(RxView.clicks(views[i]).map { views[i].id })
        }
        observable.throttleFirst(throttle, unit).subscribe(this)
    }
}
