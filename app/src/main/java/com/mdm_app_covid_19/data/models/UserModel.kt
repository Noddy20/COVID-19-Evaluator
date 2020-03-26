package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import android.util.Log
import com.mdm_app_covid_19.data.local.GetSetSharedPrefs
import com.mdm_app_covid_19.httpCalls.MyMoshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserModel(
    @Json(name = "address")
    var address: String?,
    @Json(name = "mobile_no")
    var mobileNo: String?,
    @Json(name = "name")
    var name: String?,
    @Json(name = "user_id")
    var userId: String?
): Parcelable{

    companion object{

        fun saveUserModel(userModel: UserModel){
            if (!userModel.userId?.trim().isNullOrEmpty()){
                try {
                    GetSetSharedPrefs.putData("USER_MODEL", MyMoshi.getMoshiJsonObjectAdapter(UserModel::class.java).toJson(userModel))
                }catch (e: Exception){
                    Log.e("UserModel", "Exc $e")
                }
            }
        }

        fun getSavedUserModel(): UserModel?{
            try {
               return MyMoshi.getMoshiJsonObjectAdapter(UserModel::class.java).fromJson(GetSetSharedPrefs.getData("USER_MODEL"))
            }catch (e: Exception){
                Log.e("UserModel", "Exc $e")
            }
            return null
        }

    }


}