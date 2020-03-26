package com.mdm_app_covid_19.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object GetSetSharedPrefs {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized)
            prefs = context.getSharedPreferences("App_Settings", Context.MODE_PRIVATE)
    }


    /*init {
        prefs= context.getSharedPreferences("App_Settings", Context.MODE_PRIVATE)
    }*/

    fun putData(key: String, value: String) {
        if (::prefs.isInitialized) {
            prefs.edit {
                putString(key, value)
            }
        }

    }


    fun getData(key: String): String = prefs.getString(key, "") ?: ""

    fun getDataNullable(key: String): String? = prefs.getString(key, null)

    fun putDataInt(key: String, value: Int) {
        if (::prefs.isInitialized) {
            prefs.edit {
                putInt(key, value)
            }
        }
    }

    fun getDataInt(key: String): Int = prefs.getInt(key, 0)

    fun putDataBoolean(key: String, value: Boolean) {
        prefs.edit {
            putBoolean(key, value)
        }
    }

    fun getDataBoolean(key: String): Boolean = prefs.getBoolean(key, false)
}