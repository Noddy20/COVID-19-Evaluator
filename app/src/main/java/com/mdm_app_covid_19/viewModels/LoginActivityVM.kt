package com.mdm_app_covid_19.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.AuthRepo
import com.mdm_app_covid_19.data.repo.BaseResponse
import com.mdm_app_covid_19.data.repo.ResponseStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    private val authRepo by lazy { AuthRepo(compositeDisposable, getApplication()) }


    /**
     *  Send OTP
     */

    private val phnNumMLD = MutableLiveData<String>()

    fun sendOtpOn(phnNum: String){
        phnNumMLD.value = phnNum
    }

    val sendOtpLD = Transformations.switchMap(phnNumMLD, ::sendOtpObserver)

    @SuppressLint("CheckResult")
    private fun sendOtpObserver(phoneNum: String): LiveData<BaseResponse<PhoneAuthCredential?>>{
        val data = MutableLiveData<BaseResponse<PhoneAuthCredential?>>()

        authRepo.sendOtp(phoneNum){
            verificationId = it
            data.postValue(BaseResponse(3, "Sent", null, ResponseStatus.Loaded))
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(authRepo.getSendOtpObserver{ creds, e ->
                if (creds != null){
                    data.postValue(BaseResponse(1, "Success", creds, ResponseStatus.Loaded))
                    phnCredentialsMLD.postValue(creds)
                }else{
                    data.postValue(BaseResponse(0, "${e?.message}", null, ResponseStatus.Error))
                }
            })

        return data
    }

    /**
     *  Verify OTP
     */

    private val phnCredentialsMLD = MutableLiveData<PhoneAuthCredential>()
    private var verificationId: String? = null

    fun setVerificationCode(verifCode: String){
        phnCredentialsMLD.value = PhoneAuthProvider.getCredential(verificationId?:"", verifCode)
    }

    val verifyOtpLD = Transformations.switchMap(phnCredentialsMLD, ::verifyOtpObserver)

    @SuppressLint("CheckResult")
    private fun verifyOtpObserver(creds: PhoneAuthCredential): LiveData<BaseResponse<Boolean>>{
        val data = MutableLiveData<BaseResponse<Boolean>>()

        authRepo.signInWithPhoneAuthCredential(creds).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(authRepo.getSignInObserver { success, e ->
                data.postValue(BaseResponse(1, "", success, ResponseStatus.Loaded))
            })

        return data
    }

    /**
     *  Server Login
     */

    private val userDataHashMLD = MutableLiveData<HashMap<String, Any?>>()

    fun setUserDataParam(param: HashMap<String, Any?>){
        userDataHashMLD.value = param
    }

    val serverLoginLD = Transformations.switchMap(userDataHashMLD, ::loginWithServer)

    private fun loginWithServer(param: HashMap<String, Any?>): LiveData<BaseResponse<UserModel?>>{
        return authRepo.getServerLoginObservable(param)
    }


}