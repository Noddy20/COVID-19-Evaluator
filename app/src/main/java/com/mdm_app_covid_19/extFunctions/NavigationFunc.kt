package com.mdm_app_covid_19.extFunctions

import android.content.Context
import com.mdm_app_covid_19.views.activities.*
import org.jetbrains.anko.startActivity
import kotlin.reflect.jvm.internal.impl.util.Check

fun Context.goToSignUpActivity(){
    startActivity<SignUpActivity>()
}

fun Context.goToLoginActivity(){
    startActivity<LoginActivity>()
}

fun Context.goToWhomActivity(){
    startActivity<ToWhomActivity>()
}

fun Context.goToTravelHistoryActivity(){
    startActivity<TravelHistoryActivity>()
}

fun Context.goToCheckListActivity(){
    startActivity<ChecklistActivity>()
}

fun Context.goToResultActivity(){
    startActivity<ResultActivity>()
}