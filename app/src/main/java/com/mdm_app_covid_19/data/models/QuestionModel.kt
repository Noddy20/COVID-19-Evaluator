package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class QuestionModel(
    @Json(name = "que_id")
    var queId: String?,
    @Json(name = "que_points")
    var quePoints: String?,
    @Json(name = "que_type")
    var queType: String?,
    @Json(name = "question")
    var question: String?
) : Parcelable