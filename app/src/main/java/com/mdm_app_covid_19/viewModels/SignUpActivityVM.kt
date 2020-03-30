package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.BaseResponse
import com.mdm_app_covid_19.data.repo.SignUpRepo

class SignUpActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    private val signUpRepo by lazy { SignUpRepo(compositeDisposable, getApplication()) }

    private val signUpMLD = MutableLiveData<HashMap<String, Any?>>()

    fun setSignUpData(userModel: UserModel?){
        userModel?.run {
            //UserModel.saveUserModel(this)

            HashMap<String, Any?>().let {
                it["mobileNo"] = mobileNo
                it["name"] = name
                it["address"] = address
                it["age"] = age
                it["email"] = email

                signUpMLD.value = it
            }

        }
    }

    val signUpLD = Transformations.switchMap(signUpMLD, ::getSignUpObservable)

    private fun getSignUpObservable(params: HashMap<String, Any?>): LiveData<BaseResponse<UserModel?>>{
        return signUpRepo.getSignUpObservable(params)
    }

    fun getAddressObservable(lat: Double, lon: Double): LiveData<String?>{
        return signUpRepo.getAddressObservable(lat, lon)
    }

}