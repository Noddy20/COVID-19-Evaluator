package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import android.util.Log
import com.mdm_app_covid_19.data.local.GetSetSharedPrefs
import com.mdm_app_covid_19.httpCalls.MyMoshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class SubmitDataModel(
    @Json(name = "city")
    var city: String? = null,
    @Json(name = "country")
    var country: String? = null,
    @Json(name = "member_age")
    var memberAge: String? = null,
    @Json(name = "member_name")
    var memberName: String? = null,
    @Json(name = "member_relation")
    var memberRelation: String? = null,
    @Json(name = "member_type")
    var memberType: String? = null,
    @Json(name = "state")
    var state: String? = null,
    @Json(name = "total_points")
    var totalPoints: String? = null,
    @Json(name = "travel_type")
    var travelType: String? = null,
    @Json(name = "user_id")
    var userId: String? = null,
    @Json(name = "checklist_que")
    var checklistQue: String? = null,
    @Json(name = "travel_que")
    var travelQue: String? = null
) : Parcelable {

    companion object{

        fun saveSubmitModel(submitDataModel: SubmitDataModel){
            kotlin.runCatching {
                GetSetSharedPrefs.putData("SUBMIT_DATA_MODEL", MyMoshi.getMoshiJsonObjectAdapter(SubmitDataModel::class.java).toJson(submitDataModel))
            }.onFailure {
                Log.e("SubmitDataModel", "save $it")
            }
        }

        fun getPrevSubmitModel(): SubmitDataModel?{
            kotlin.runCatching {
                return MyMoshi.getMoshiJsonObjectAdapter(SubmitDataModel::class.java).fromJson(GetSetSharedPrefs.getData("SUBMIT_DATA_MODEL"))
            }.onFailure {
                Log.e("SubmitDataModel", "save $it")
            }
            return null
        }

        fun clearSubmitModel(){
            GetSetSharedPrefs.putData("SUBMIT_DATA_MODEL", "")
        }

        fun isInComplete(submitDataModel: SubmitDataModel?): Boolean{
            kotlin.runCatching {
                return submitDataModel.toString().contains("null")
            }
            return true
        }

    }

}