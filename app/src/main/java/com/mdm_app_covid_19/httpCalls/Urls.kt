package com.mdm_app_covid_19.httpCalls

import com.mdm_app_covid_19.BuildConfig

object Urls {

    //POST
    const val GET_USER_BY_MOBILE = BuildConfig.BASE_URL_API + "getUserbyMobile"

    //POST
    const val CREATE_NEW_USER = BuildConfig.BASE_URL_API + "createNewUser"

    //GET
    const val TRAVEL_HISTORY_DATA = BuildConfig.BASE_URL_API + "getTravelHistoryData"

    //GET
    const val STATE_LIST = BuildConfig.BASE_URL_API + "getStateList"

    //GET
    const val CITY_LIST = BuildConfig.BASE_URL_API + "getCityList"

    //GET
    const val CHECKLIST_QUESTIONS = BuildConfig.BASE_URL_API + "getChecklistQuestions"

    //POST
    const val SUBMIT_USER_INFO = BuildConfig.BASE_URL_API + "insertUserInfo"

}