package com.mdm_app_covid_19.extFunctions

import android.os.Build
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat

fun TextView.setHtmlText(string: String){
    text = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun TextView.setStyle(@StyleRes styleRes: Int){
    TextViewCompat.setTextAppearance(this, styleRes)
}

fun String.toHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun View.hide(){
    this.visibility = View.GONE
}

fun View.invisible(){
    this.visibility = View.INVISIBLE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.setEnableDisable(isEnb: Boolean){
    if (isEnb){
        isEnabled = true
        alpha = 1f
    }else{
        isEnabled = true
        alpha = 0.4f
    }
}

fun View.keepVisible(keepVisible: Boolean){
    this.visibility = if (keepVisible) View.VISIBLE else View.GONE
}

fun View.setFullScreen(){
    var visibilityFlags = (View.SYSTEM_UI_FLAG_LOW_PROFILE
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        visibilityFlags = visibilityFlags or View.SYSTEM_UI_FLAG_IMMERSIVE
    }
    this.systemUiVisibility = visibilityFlags
}

fun View.exitFullScreen(){
    this.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}