package com.mdm_app_covid_19.views.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.hide
import com.mdm_app_covid_19.extFunctions.show
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
    private var refreshMenuItem: MenuItem? = null

    private lateinit var dialogMsg: DialogMsg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        intent?.let {
            toolbarTitle = it.getStringExtra(EXTRA_TITLE)?:"Corona - Government Advisory"
            webUrl = it.getStringExtra(EXTRA_URL)
        }

        SetUpToolbar.setToolbar(this, toolbarTitle, true)

        init()

    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_web_view, menu)

        refreshMenuItem = menu?.findItem(R.id.menuItemRefresh)
        refreshMenuItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //onBackPressed()
            finish()
        }
        if (item.itemId == R.id.menuItemRefresh) {
            webView.reload()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(){
        dialogMsg = DialogMsg(this)

        if(webUrl?.trim().isNullOrEmpty()){
            finish()
        }else {
            webViewLoad()
        }
    }

    private fun webViewLoad(){
        if (webUrl?.endsWith(".pdf", true) == true){
            webUrl = "https://docs.google.com/gview?embedded=true&url=$webUrl"
        }
        webView.requestFocus()
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient(){

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.show()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.hide()
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                progressBar.hide()
                dialogMsg.showGeneralError(btnTxt = "Retry", cancelable = true, onClickAction = {
                    webView.reload()
                })
                super.onReceivedError(view, request, error)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(webUrl)
                return super.shouldOverrideUrlLoading(view, request)
            }

        }

        webView.webChromeClient = WebChromeClient()

        webView.loadUrl(webUrl)
    }

}
