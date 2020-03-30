package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TravelHistoryDataModel(
    @Json(name = "countryList")
    var countryList: List<CountryModel>?,
    @Json(name = "quesitonsList")
    var quesitonsList: List<QuestionModel>?
) : Parcelable