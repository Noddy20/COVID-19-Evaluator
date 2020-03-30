package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.data.models.ResultModel
import com.mdm_app_covid_19.data.repo.BaseResponse
import com.mdm_app_covid_19.data.repo.ChecklistRepo
import com.mdm_app_covid_19.data.repo.ResultRepo

class ResultActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    private val resultRepo by lazy { ResultRepo(compositeDisposable, getApplication()) }


    fun getChecklistQuestionsObservable(params: HashMap<String, Any?>): LiveData<BaseResponse<ResultModel?>> {
        return resultRepo.getResultObservable(params)
    }

}