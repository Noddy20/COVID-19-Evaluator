package com.mdm_app_covid_19

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.androidnetworking.AndroidNetworking
import com.google.firebase.FirebaseApp
import com.mdm_app_covid_19.data.local.GetSetSharedPrefs
import com.mdm_app_covid_19.data.repo.FirebaseInstances
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.Executors

class MyApp : MultiDexApplication() {


    companion object{
        var ANDROID_ID=""
        lateinit var appContext: Context
        //var executor = Executors.newFixedThreadPool(5)
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        appContext = this

        FirebaseInstances(this)
        GetSetSharedPrefs.init(applicationContext)



        AndroidNetworking.initialize(applicationContext)

        ANDROID_ID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)


        RxJavaPlugins.setErrorHandler {
            if( it is UndeliverableException){
                Log.e("MyApp", "UndeliverableException $it")
                return@setErrorHandler
            }
        }

    }


}