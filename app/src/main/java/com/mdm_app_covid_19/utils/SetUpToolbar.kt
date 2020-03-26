package com.mdm_app_covid_19.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mdm_app_covid_19.R

object SetUpToolbar {

    fun setToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean) {
        val toolbar = activity.findViewById<View>(R.id.toolbar) as Toolbar
        setToolbar(activity, title, backIcon, toolbar)
    }

    fun setToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean, backgroundColorRes: Int = R.color.colorPrimary) {
        val toolbar = activity.findViewById<View>(R.id.toolbar) as Toolbar
        //toolbar.setBackgroundColor(ContextCompat.getColor(activity, backgroundColorRes))
        setToolbar(activity, title, backIcon, toolbar)
    }


    fun setToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean, image: String) {
        val toolbar = activity.findViewById<View>(R.id.toolbar) as Toolbar

        setToolbar(activity, title, backIcon, toolbar)
    }

    fun setCollapseToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean) {
        val collapseToolbar = activity.findViewById<View>(R.id.collapseToolbar) as CollapsingToolbarLayout
        val toolbar = activity.findViewById<View>(R.id.toolbar) as Toolbar

        collapseToolbar.title = title
        activity.setSupportActionBar(toolbar)

        if (backIcon)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(backIcon)
    }

    fun setToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean, toolbar: Toolbar) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = title
        if (backIcon)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(backIcon)

        //applyFontForToolbarTitle(activity, toolbar)
    }

    private fun applyFontForToolbarTitle(activity: AppCompatActivity, toolbar: Toolbar) {
        /*for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = CustomFont.semiBoldFont(activity)
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    view.textSize = 16f
                    break
                }
            }
        }*/
    }

}