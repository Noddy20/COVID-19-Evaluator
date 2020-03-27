package com.mdm_app_covid_19.utils

import android.text.TextUtils
import android.util.Log
import java.util.regex.Pattern

object MyValidations {

    private val NAME_PATTERN: Pattern = Pattern.compile("^[a-zA-Z ]*$")

    private val PASSWORD_PATTERN = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$")

    private val MobilePattern = Pattern.compile("^[+]?[0-9]{10,13}$",Pattern.CASE_INSENSITIVE)

    private val PIN_CODE = Pattern.compile("^[1-9][0-9]{5}$")




    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return PASSWORD_PATTERN.matcher(password).matches()
    }

    fun isValidMobileNumber(mobile: String): Boolean {
        return MobilePattern.matcher(mobile).matches()
    }

    fun isValidAge(age: String): Boolean {
        return age.length in 1..3
    }

    fun isValidOtp(age: String): Boolean {
        return age.length == 6
    }

    fun isValidName(name: String): Boolean {
        return NAME_PATTERN.matcher(name).matches()
    }

    fun checkWebsiteUrl(url:String):Boolean{
        return android.util.Patterns.WEB_URL.matcher(url).matches()
    }
}