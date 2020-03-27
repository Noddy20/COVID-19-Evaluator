package com.mdm_app_covid_19.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class StateModel(
    @Json(name = "country_id")
    var countryId: String?,
    @Json(name = "id")
    var id: String?,
    @Json(name = "name")
    var name: String?
) : Parcelable