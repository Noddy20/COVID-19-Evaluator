package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData

class ToWhomActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    val nextClickMLD = MutableLiveData<Int>()

}