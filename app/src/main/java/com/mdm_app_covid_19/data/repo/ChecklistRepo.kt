package com.mdm_app_covid_19.data.repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.data.models.StateModel
import com.mdm_app_covid_19.httpCalls.FastNetworking
import com.mdm_app_covid_19.httpCalls.Urls
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject

class ChecklistRepo(private val compositeDisposable: CompositeDisposable, private val mApp: Application) {

    companion object {
        private const val TAG = "ChecklistRepo"

    }

    fun getChecklistQuestionsObservable(): LiveData<BaseResponse<List<QuestionModel>?>> {
        val data = MutableLiveData<BaseResponse<List<QuestionModel>?>>()

        val tag = "ChecklistQuestions"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        Log.v(TAG, "$tag Url ${Urls.CHECKLIST_QUESTIONS}")

        FastNetworking.makeRxCallObservable(
            FastNetworking.getRxCallGetObservable(Urls.CHECKLIST_QUESTIONS, tag, HashMap()), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseArr<QuestionModel>(response)
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