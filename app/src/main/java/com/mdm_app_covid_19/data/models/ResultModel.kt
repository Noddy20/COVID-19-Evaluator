package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ResultModel(
    @Json(name = "conclusionFileUrl")
    var conclusionFileUrl: String?,
    @Json(name = "conclusionMessage")
    var conclusionMessage: String?,
    @Json(name = "conclusionTitle")
    var conclusionTitle: String?
) : Parcelable