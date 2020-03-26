package com.mdm_app_covid_19.data.repo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "status") var statusCode : Int?=null,
    @Json(name = "message") var statusDesc:String?=null,
    @Json(name = "data") var data:T?,
    var responseStatus: ResponseStatus
){

    companion object{
        const val KEY_STATUS = "status"
        const val KEY_MSG = "message"
        const val KEY_DATA = "data"
    }

}

sealed class ResponseStatus {

    object Loading : ResponseStatus()
    object Loaded : ResponseStatus()
    object NoResult : ResponseStatus()
    object Failed : ResponseStatus()
    object Error : ResponseStatus()
    object NoInternet : ResponseStatus()

}