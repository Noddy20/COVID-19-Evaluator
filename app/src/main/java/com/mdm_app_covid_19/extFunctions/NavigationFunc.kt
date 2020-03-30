package com.mdm_app_covid_19.extFunctions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.mdm_app_covid_19.BuildConfig
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.views.activities.*
import org.jetbrains.anko.startActivity
import kotlin.reflect.jvm.internal.impl.util.Check

fun Activity.goToSignUpActivity(animate: Boolean){
    startActivity<SignUpActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToLoginActivity(animate: Boolean){
    startActivity<LoginActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToWhomActivity(animate: Boolean){
    startActivity<ToWhomActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToTravelHistoryActivity(animate: Boolean){
    startActivity<TravelHistoryActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToCheckListActivity(animate: Boolean){
    startActivity<ChecklistActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToResultActivity(animate: Boolean){
    startActivity<ResultActivity>()
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.goToWebView(title: String, url: String, animate: Boolean){
    startActivity<WebViewActivity>(
        WebViewActivity.EXTRA_TITLE to title,
        WebViewActivity.EXTRA_URL to url
        )
    if (animate) this.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.updateFromPlayStoreIntent() {
    var pkgName = packageName
    if(BuildConfig.DEBUG){
        pkgName = "com.mdm_app_covid_19"
    }

    val uri = Uri.parse("market://details?id=$pkgName")
    val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

    val flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    goToMarketIntent.addFlags(flags)

    try {
        startActivity(goToMarketIntent)
    } catch (e: ActivityNotFoundException) {
        Log.v("MyIntents", "Intent Exc ${Log.getStackTraceString(e)}")
    }
}