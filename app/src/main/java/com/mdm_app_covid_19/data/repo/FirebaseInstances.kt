package com.mdm_app_covid_19.data.repo

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class FirebaseInstances(private val mApp: Application){

    companion object{
        lateinit var mAuth: FirebaseAuth
    }

    init {
        FirebaseApp.initializeApp(mApp)
        mAuth = FirebaseAuth.getInstance()
    }

    fun initAuth(): FirebaseAuth{

        return mAuth
    }

}