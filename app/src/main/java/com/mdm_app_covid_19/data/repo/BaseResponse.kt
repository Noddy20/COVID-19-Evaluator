package com.mdm_app_covid_19.data.repo

import android.util.Log
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.httpCalls.MyMoshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject
import java.lang.Exception

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

        inline fun <reified T> parseBaseResponseObj(json: JSONObject?): BaseResponse<T?>{
            var status: Int
            var msg: String
            try {
                json?.run {
                    status = optInt(KEY_STATUS)
                    msg = optString(KEY_MSG, "Something went wrong!")
                    val data = MyMoshi.getMoshiJsonObjectAdapter(T::class.java)
                        .fromJson(getJSONObject(KEY_DATA).toString())
                    return BaseResponse(status, msg, data, ResponseStatus.Loaded)
                }
            }catch (e: Exception){
                Log.e("BaseResponse", "Exc $e")
            }
           return BaseResponse(0, "Something went wrong!", null, ResponseStatus.Failed)
        }

        inline fun <reified T> parseBaseResponseArr(json: JSONObject?): BaseResponse<List<T>?>{
            var status: Int
            var msg: String
            try {
                json?.run {
                    status = optInt(KEY_STATUS)
                    msg = optString(KEY_MSG, "Something went wrong!")
                    val data = MyMoshi.getMoshiArrayAdapter(T::class.java)
                        .fromJson(getJSONArray(KEY_DATA).toString())
                    return BaseResponse(status, msg, data, ResponseStatus.Loaded)
                }
            }catch (e: Exception){
                Log.e("BaseResponse", "Exc $e")
            }
            return BaseResponse(0, "Something went wrong!", null, ResponseStatus.Failed)
        }

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