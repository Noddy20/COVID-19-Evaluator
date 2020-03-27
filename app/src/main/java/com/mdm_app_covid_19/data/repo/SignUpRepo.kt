package com.mdm_app_covid_19.data.repo

import android.annotation.SuppressLint
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.httpCalls.FastNetworking
import com.mdm_app_covid_19.httpCalls.Urls
import com.mdm_app_covid_19.views.activities.SignUpActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class SignUpRepo(private val compositeDisposable: CompositeDisposable, private val mApp: Application) {

    companion object {
        private const val TAG = "SignUpRepo"

    }

    fun getSignUpObservable(params: HashMap<String, Any?>): LiveData<BaseResponse<UserModel?>> {
        val data = MutableLiveData<BaseResponse<UserModel?>>()

        val tag = "getSignUp"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        Log.v(TAG, "$tag Url ${Urls.CREATE_NEW_USER}  ${JSONObject(params)}")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallPostObservable(Urls.CREATE_NEW_USER, tag, params), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseObj<UserModel?>(response)
                            data.postValue(baseResponse)
                        }catch (e: Exception){
                            setNull(e = e)
                        }
                    }else{
                        setNull()
                    }
                }

                override fun onApiError(anError: ANError) {
                    setNull()
                }

            })

        return data
    }

    @SuppressLint("CheckResult")
    fun getAddressObservable(latitude: Double, longitude: Double): LiveData<String?> {
        val data = MutableLiveData<String?>()

        Single.create<String?> { emitter ->
            val geocoder = Geocoder(mApp, Locale.getDefault())
            var result: String? = null
            try {
                val addressList: List<Address>? = geocoder.getFromLocation(
                    latitude, longitude, 1
                )
                if (addressList != null && addressList.isNotEmpty()) {
                    val address: Address = addressList[0]
                    val sb = StringBuilder()
                    for (i in 0 until address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }
                    kotlin.runCatching {
                    address.getAddressLine(0)?.let { //address
                        sb.append(it).append("\n")
                    }
                    }
                    address.locality?.let {     //City
                        sb.append(it).append("\n")
                    }
                    address.adminArea?.let {     //State
                        sb.append(it).append("\n")
                    }
                    address.postalCode?.let {     //Postal code
                        sb.append(it).append("\n")
                    }
                    address.countryName?.let{
                        sb.append(it)
                    }

                    result = sb.toString()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Unable connect to Geocoder", e)
            } finally {
                emitter.onSuccess(result?:"")
            }

        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableSingleObserver<String?>() {
                override fun onSuccess(t: String) {
                    data.postValue(t)
                }

                override fun onError(e: Throwable) {
                   data.postValue(null)
                }

            })

        return data
    }

}