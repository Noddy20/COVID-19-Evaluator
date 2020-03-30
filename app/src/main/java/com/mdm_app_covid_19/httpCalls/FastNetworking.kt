package com.mdm_app_covid_19.httpCalls

import android.util.Log
import com.androidnetworking.error.ANError
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


object FastNetworking {

    private const val TAG = "FastNetworking"

    fun makeRxCallObservable(jsonObservable: Observable<JSONObject>, compositeDisposable: CompositeDisposable, apiTag: String, onApiResult: OnApiResult){

        jsonObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { json -> json }
            .subscribe(object : Observer<JSONObject> {
                override fun onComplete() {
                    Log.v(TAG, "$apiTag makeRxCall RxNetworkCompleted")
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(json: JSONObject) {
                    Log.v(TAG, "$apiTag makeRxCall onNext $json")
                    onApiResult.onApiSuccess(json)

                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "$apiTag makeRxCall onError $e")
                    if (e is ANError) {
                        Log.e(TAG, "$apiTag makeRxCall onError ${e.message}" + e.errorCode + "\n" + e.errorBody)
                        onApiResult.onApiError(e)

                    }
                }

            })
    }

    fun getRxCallGetObservable(url: String, tag: String, hashParams: HashMap<String, Any?> = HashMap()): Observable<JSONObject> {

        val reqBuilder = Rx2AndroidNetworking.get(url)

        reqBuilder.addQueryParameter(hashParams)

        return reqBuilder.build().jsonObjectObservable
    }


    fun getRxCallPostObservable(url: String, tag: String, hashParams: HashMap<String, Any?>): Observable<JSONObject> {

        val reqBuilder = Rx2AndroidNetworking.post(url)

        reqBuilder.addBodyParameter(hashParams)
        Log.i(TAG, "Rx_Params_$tag" + JSONObject(hashParams))

        return reqBuilder.build().jsonObjectObservable
    }

    interface OnApiResult {
        fun onApiSuccess(response: JSONObject?)
        fun onApiError(anError: ANError)
    }

}