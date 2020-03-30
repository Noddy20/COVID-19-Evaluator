package com.mdm_app_covid_19.views.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.SelectionListDialogModel
import com.mdm_app_covid_19.extFunctions.hide
import com.mdm_app_covid_19.extFunctions.show
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.views.adapters.SelectionListDialogAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_empty_layout.*
import kotlinx.android.synthetic.main.dialog_info_common.*
import kotlinx.android.synthetic.main.dialog_progress.*
import kotlinx.android.synthetic.main.dialog_selection_list.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

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

    fun showGeneralError(strMsg: String = "Something went wrong!", cancelable: Boolean = false, onClickAction: (() -> Unit)? = null, btnTxt: String = "OK"){
        mDialog = initNewDialog(R.layout.dialog_info_common)

        mDialog?.apply {
            textView.text = strMsg
            setCancelable(cancelable)

            btnLayout.show()
            btnRight.hide()
           // btnLeft.backgroundResource = R.drawable.bg_blue_rounded_btn
            btnLeft.textColor = ContextCompat.getColor(mActivity, R.color.colorPrimaryTheme)
            btnLeft.text = btnTxt

            ResourcesCompat.getFont(mActivity, R.font.font_open_sans_regular).let {
                btnLeft.typeface = it
                btnRight.typeface = it
            }

            btnLeft.setOnClickListener{
                this@DialogMsg.dismiss()
                onClickAction?.invoke()
            }
        }

        show()
    }

    fun showAppUpdate(strMsg: String = "App update available!", updateClick: (() -> Unit)? = null, laterClick: (() -> Unit)? = null, isMandatory: Boolean = false){
        mDialog = initNewDialog(R.layout.dialog_info_common)

        mDialog?.apply {
            textView.text = strMsg
            setCancelable(!isMandatory)

            btnLayout.show()

            btnLeft.text = "Update Now"
            btnLeft.textColor = ContextCompat.getColor(mActivity, R.color.colorPrimaryTheme)

            if (isMandatory){
                btnRight.hide()
            }else{
                btnRight.text = "Later"
                btnRight.textColor = ContextCompat.getColor(mActivity, R.color.colorTextDim)
                btnRight.show()
            }

            // btnLeft.backgroundResource = R.drawable.bg_blue_rounded_btn

            ResourcesCompat.getFont(mActivity, R.font.font_open_sans_regular).let {
                btnLeft.typeface = it
                btnRight.typeface = it
            }

            btnLeft.setOnClickListener{
                updateClick?.invoke()
            }

            btnRight.setOnClickListener {
                laterClick?.invoke()
            }
        }

        show()
    }

    fun showSelectionListDialog(strTitle: String?, dataArr: List<SelectionListDialogModel>, selDataArr: List<SelectionListDialogModel>? = null,
                                isMultiSel: Boolean = false, searchEnabled: Boolean = false, compositeDisposable: CompositeDisposable? = null, isCancelable: Boolean = false,
                                dialogDismissListener: DialogInterface.OnDismissListener? = null, onItemClick:((pos: SelectionListDialogModel) -> Unit)? = null,
                                onMultiSelect:((selected: List<SelectionListDialogModel>) -> Unit)? = null){
        try {
            dismiss()
            mDialog = initNewDialog(R.layout.dialog_selection_list)
            mDialog?.run {
                setOnDismissListener(dialogDismissListener)
                setCancelable(isCancelable)
                imgClose.isVisible = isCancelable
                btnSelect.isVisible = isMultiSel
                btnSelect.typeface = ResourcesCompat.getFont(mActivity, R.font.font_open_sans_regular)

                imgClose.setOnClickListener { dismiss() }

                val adapter = SelectionListDialogAdapter(mActivity, isMultiSel)
                {
                    dismiss()
                    onItemClick?.invoke(it)
                }
                recyclerView.layoutManager = LinearLayoutManager(mActivity)
                recyclerView.adapter = adapter

                adapter.setDataList(dataArr)
                selDataArr?.let { adapter.setSelectedList(ArrayList(it)) }

                btnSelect.setOnClickListener {
                    onMultiSelect?.invoke(adapter.getSelectedModels())
                    dismiss()
                }

                if (strTitle != null) {
                    txtTitle.text = strTitle
                    txtTitle.show()
                } else
                    txtTitle.hide()

                if (searchEnabled) {
                    etSearchHere.show()

                    compositeDisposable?.let {
                        MyTextChangeValidationUtils.initRxValidation(it, etSearchHere, validationType = MyTextChangeValidationUtils.VALIDATION_MANUAL) { str ->
                            if (str.isNotEmpty()) {
                                val arrayFiltered = dataArr.filter { selModel -> selModel.str.contains(str, true) }
                                if (arrayFiltered.isNullOrEmpty()){
                                    recyclerView.hide()
                                    viewEmpty.show()
                                    txtEmpty.text = mActivity.getString(R.string.no_search_results)
                                }else{
                                    viewEmpty.hide()
                                    recyclerView.show()
                                }
                                adapter.setDataList(arrayFiltered)
                            } else {
                                adapter.setDataList(dataArr)
                            }
                        }
                    }

                } else etSearchHere.hide()
            }

            show()

        }catch (e: Exception){
            Log.e("DialogMsg", "Exc $e")
        }
    }


}