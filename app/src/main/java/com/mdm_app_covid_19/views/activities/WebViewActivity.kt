package com.mdm_app_covid_19.views.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseActivity() {

    companion object{
        private const val TAG = "MyWebViewActivity"

        const val EXTRA_TITLE = "titleExtra"
        const val EXTRA_URL = "urlExtra"

    }

    private var toolbarTitle: String = ""
    private var webUrl: String? = ""

    private lateinit var dialogMsg: DialogMsg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        intent?.let {
            toolbarTitle = it.getStringExtra(EXTRA_TITLE)?:"Instructions"
            webUrl = it.getStringExtra(EXTRA_URL)
        }

        SetUpToolbar.setCollapseToolbar(this, toolbarTitle, true)

        init()

    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else super.onBackPressed()
    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        super.onDestroy()
    }

    private fun init(){
        dialogMsg = DialogMsg(this)

        if(webUrl?.trim().isNullOrEmpty()){
            finish()
        }else {
            swipeRefresh.setOnRefreshListener {
                if (!swipeRefresh.isRefreshing) webView.reload()
            }

            webViewLoad()
        }
    }

    private fun webViewLoad(){
        webView.loadUrl(webUrl)
        webView.webViewClient = object : WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                swipeRefresh.isRefreshing = true
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                swipeRefresh.isRefreshing = false
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                swipeRefresh.isRefreshing = false
                dialogMsg.showGeneralError(btnTxt = "Retry", cancelable = true, onClickAction = {
                    webView.reload()
                })
                super.onReceivedError(view, request, error)
            }

        }
    }

}
