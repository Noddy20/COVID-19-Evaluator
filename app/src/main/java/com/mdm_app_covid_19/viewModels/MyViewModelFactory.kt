package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.Constructor

class MyViewModelFactory(private val application: Application?= null, private val args: Bundle? = null) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            return if (application != null){
                val constructor: Constructor<T> = modelClass.getDeclaredConstructor(Application::class.java, Bundle::class.java)
                constructor.newInstance(application, args)
            }else{
                val constructor: Constructor<T> = modelClass.getDeclaredConstructor(Bundle::class.java)
                constructor.newInstance(args)
            }
        } catch (e: Exception) {
            Log.e("MyViewModelFactory", "Could not create new instance of class ${modelClass.canonicalName}")
            throw e
        }
    }
}