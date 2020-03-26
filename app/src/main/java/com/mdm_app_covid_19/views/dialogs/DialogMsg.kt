package com.mdm_app_covid_19.views.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import androidx.annotation.LayoutRes
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.extFunctions.hide
import com.mdm_app_covid_19.extFunctions.show
import kotlinx.android.synthetic.main.dialog_info_common.*
import kotlinx.android.synthetic.main.dialog_progress.*

class DialogMsg(private val mActivity: Activity){

    companion object{
        private const val TAG = "DialogMsg"
    }

    private var mDialog: Dialog? = null

    private fun initNewDialog(@LayoutRes layout: Int): Dialog {
        dismiss()
        return Dialog(mActivity).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(layout)
        }
    }

    fun dismiss(){
        kotlin.runCatching {
            mDialog?.apply {
                if (isShowing)
                    dismiss()

                mDialog = null
            }
        }.onFailure {
            Log.e(TAG, "Exc Dismiss $it")
        }
    }

    private fun show(){
        kotlin.runCatching {
            mDialog?.apply {
                if (!mActivity.isFinishing){
                    show()
                }else mDialog = null
            }
        }.onFailure {
            Log.e(TAG, "Exc Show $it")
        }
    }

    fun showPleaseWait(strMsg: String = "Please wait...", cancelable: Boolean = false){
        mDialog = initNewDialog(R.layout.dialog_progress)

        mDialog?.apply {
            txtProgress.text = strMsg
            setCancelable(cancelable)
        }

        show()
    }

    fun showGeneralError(strMsg: String = "Something went wrong!", cancelable: Boolean = false, onClickAction: (() -> Unit)? = null, btnTxt: String = "Ok"){
        mDialog = initNewDialog(R.layout.dialog_info_common)

        mDialog?.apply {
            textView.text = strMsg
            setCancelable(cancelable)

            if (onClickAction != null){
                btnLayout.show()
                btnRight.hide()
                btnLeft.text = btnTxt
                btnLeft.setOnClickListener{
                    onClickAction.invoke()
                    this@DialogMsg.dismiss()
                }
            }
        }

        show()
    }

}