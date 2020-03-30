package com.mdm_app_covid_19.data.repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.mdm_app_covid_19.data.models.ResultModel
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.httpCalls.FastNetworking
import com.mdm_app_covid_19.httpCalls.Urls
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject

class ResultRepo(private val compositeDisposable: CompositeDisposable, private val mApp: Application) {

    companion object {
        private const val TAG = "ResultRepo"

    }

    fun getResultObservable(params: HashMap<String, Any?>): LiveData<BaseResponse<ResultModel?>> {
        val data = MutableLiveData<BaseResponse<ResultModel?>>()

        val tag = "getResult"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        Log.v(TAG, "$tag Url ${Urls.SUBMIT_USER_INFO}  ${JSONObject(params)}")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallPostObservable(Urls.SUBMIT_USER_INFO, tag, params), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseObj<ResultModel?>(response)
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

}