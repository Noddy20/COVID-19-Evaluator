package com.mdm_app_covid_19.extFunctions

import android.content.Context
import com.mdm_app_covid_19.views.activities.LoginActivity
import com.mdm_app_covid_19.views.activities.SignUpActivity
import org.jetbrains.anko.startActivity

fun Context.goToSignUpActivity(){
    startActivity<SignUpActivity>()
}

fun Context.goToLoginActivity(){
    startActivity<LoginActivity>()
}