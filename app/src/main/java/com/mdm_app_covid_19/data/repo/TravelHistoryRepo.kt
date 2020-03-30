package com.mdm_app_covid_19.data.repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.mdm_app_covid_19.data.models.CityModel
import com.mdm_app_covid_19.data.models.StateModel
import com.mdm_app_covid_19.data.models.TravelHistoryDataModel
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.httpCalls.FastNetworking
import com.mdm_app_covid_19.httpCalls.Urls
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject

class TravelHistoryRepo(private val compositeDisposable: CompositeDisposable, private val mApp: Application) {

    companion object {
        private const val TAG = "TravelHistoryRepo"

    }

    fun getTravelHistoryDataObservable(): LiveData<BaseResponse<TravelHistoryDataModel?>> {
        val data = MutableLiveData<BaseResponse<TravelHistoryDataModel?>>()

        val tag = "TravelHistoryData"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        Log.v(TAG, "$tag Url ${Urls.TRAVEL_HISTORY_DATA}")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallGetObservable(Urls.TRAVEL_HISTORY_DATA, tag, HashMap()), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseObj<TravelHistoryDataModel?>(response)
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

    fun getStatesListObservable(): LiveData<BaseResponse<List<StateModel>?>> {
        val data = MutableLiveData<BaseResponse<List<StateModel>?>>()

        val tag = "StatesList"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        val url = "${Urls.STATE_LIST}/India"
        Log.v(TAG, "$tag Url $url")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallGetObservable(url, tag, HashMap()), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseArr<StateModel>(response)
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

    fun getCitiesListObservable(stateName: String): LiveData<BaseResponse<List<CityModel>?>> {
        val data = MutableLiveData<BaseResponse<List<CityModel>?>>()

        val tag = "CitiesList"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        val url = "${Urls.CITY_LIST}/$stateName"
        Log.v(TAG, "$tag Url $url")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallGetObservable(url, tag, HashMap()), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseArr<CityModel>(response)
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