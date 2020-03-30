package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import com.mdm_app_covid_19.data.models.CityModel
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.data.models.StateModel
import com.mdm_app_covid_19.data.models.TravelHistoryDataModel
import com.mdm_app_covid_19.data.repo.BaseResponse
import com.mdm_app_covid_19.data.repo.ChecklistRepo
import com.mdm_app_covid_19.data.repo.TravelHistoryRepo

class ChecklistActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    private val checklistRepo by lazy { ChecklistRepo(compositeDisposable, getApplication()) }


    fun getChecklistQuestionsObservable(): LiveData<BaseResponse<List<QuestionModel>?>> {
        return checklistRepo.getChecklistQuestionsObservable()
    }

}