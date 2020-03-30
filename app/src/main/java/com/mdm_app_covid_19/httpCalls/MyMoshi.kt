package com.mdm_app_covid_19.httpCalls

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MyMoshi {

    val moshi: Moshi = Moshi.Builder().build()

    val moshiKotlin: Moshi =Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun <T: Any?> getMoshiArrayAdapter(className: Class<T>): JsonAdapter<List<T>> {
        val type = Types.newParameterizedType(List::class.java, className)
        return moshi.adapter(type)
    }

    fun <T> getMoshiJsonObjectAdapter(className: Class<T>): JsonAdapter<T> = moshi.adapter(className)

}