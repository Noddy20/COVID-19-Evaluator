package com.mdm_app_covid_19.utils

import android.animation.AnimatorInflater
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.AppBarLayout
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
        //val appBar = activity.findViewById<View>(R.id.appbar) as AppBarLayout

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            collapseToolbar.stateListAnimator = AnimatorInflater.loadStateListAnimator(activity, R.animator.appbar_elevation)
        }*/
        collapseToolbar.title = title
        activity.setSupportActionBar(toolbar)

        if (backIcon)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(backIcon)

        applyFontForToolbarTitle(activity, collapseToolbar)
    }

    fun setToolbar(activity: AppCompatActivity, title: String, backIcon: Boolean, toolbar: Toolbar) {
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar!!.title = title
        if (backIcon)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(backIcon)

        applyFontForToolbarTitle(activity, toolbar)
    }

    private fun applyFontForToolbarTitle(activity: AppCompatActivity, toolbar: Toolbar) {
        for (i in 0 until toolbar.childCount) {
            val view = toolbar.getChildAt(i)
            if (view is TextView) {
                val titleFont = ResourcesCompat.getFont(activity, R.font.font_open_sans_regular)
                if (view.text == toolbar.title) {
                    view.typeface = titleFont
                    view.textSize = 16f
                    break
                }
            }
        }
    }

    private fun applyFontForToolbarTitle(activity: AppCompatActivity, toolbar: CollapsingToolbarLayout) {
        //val titleFont = ResourcesCompat.getFont(activity, R.font.font_open_sans_regular)
        toolbar.apply {
            setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
            setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        }
    }

}