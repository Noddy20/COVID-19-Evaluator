package com.mdm_app_covid_19.viewModels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mdm_app_covid_19.data.models.CityModel
import com.mdm_app_covid_19.data.models.CountryModel
import com.mdm_app_covid_19.data.models.StateModel
import com.mdm_app_covid_19.data.models.TravelHistoryDataModel
import com.mdm_app_covid_19.data.repo.BaseResponse
import com.mdm_app_covid_19.data.repo.TravelHistoryRepo
import org.json.JSONObject

class TravelHistoryActivityVM(application: Application, private val bundle: Bundle?): BaseAndroidViewModel(application){

    private val travelHistoryRepo by lazy { TravelHistoryRepo(compositeDisposable, getApplication()) }

    fun getCountryListObservable(): LiveData<BaseResponse<TravelHistoryDataModel?>>{
        return travelHistoryRepo.getTravelHistoryDataObservable()
    }

    fun getStatesListObservable(): LiveData<BaseResponse<List<StateModel>?>>{
        return travelHistoryRepo.getStatesListObservable()
    }

    fun getCitiesListObservable(stateName: String): LiveData<BaseResponse<List<CityModel>?>>{
        return travelHistoryRepo.getCitiesListObservable(stateName)
    }

}