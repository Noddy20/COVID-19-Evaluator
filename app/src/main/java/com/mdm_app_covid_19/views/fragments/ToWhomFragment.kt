package com.mdm_app_covid_19.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.data.models.SelectionListDialogModel
import com.mdm_app_covid_19.data.models.SubmitDataModel
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.EmojiFilter
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.ToWhomActivityVM
import com.mdm_app_covid_19.views.activities.ToWhomActivity
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_personal_info.*
import kotlinx.android.synthetic.main.content_personal_info.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.textColor

class ToWhomFragment : Fragment(){

    companion object {
        private const val TAG = "ToWhomFragment"
        private const val EXTRA_IS_SELF = "isSelf"

        fun newInstance(isSelf: Boolean): ToWhomFragment {
            return ToWhomFragment().also { it.arguments = bundleOf(EXTRA_IS_SELF to isSelf) }
        }
    }

    private lateinit var rootView: View
    private lateinit var mActivity: ToWhomActivity
    private lateinit var dialogMsg: DialogMsg

    private lateinit var viewModel: ToWhomActivityVM

    private var isSelf = true

    private val compositeDisposable by lazy { CompositeDisposable() }

    private lateinit var relationsList: List<SelectionListDialogModel>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity= context as ToWhomActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSelf = arguments?.getBoolean(EXTRA_IS_SELF, true)?:true
        viewModel = ViewModelProvider(mActivity, MyViewModelFactory(mActivity.application))[ToWhomActivityVM::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_to_whom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        rootView.init()
    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun View.init() {
        dialogMsg = DialogMsg(mActivity)

        selRelation.isVisible = !isSelf
        etPhone.hide()
        etMail.hide()
        etAddress.hide()


        setTextChangeListener()

        if (!isSelf){
            val relList = ArrayList<SelectionListDialogModel>()
            val relStrList = resources.getStringArray(R.array.arr_relations).toList()
            repeat(relStrList.size) {
                relList.add(SelectionListDialogModel(it, relStrList[it], false))
            }
            relationsList = relList

            setClickListeners()
        }else{
            UserModel.getSavedUserModel()?.let {
                etName.setText(it.name?:"")
                etAge.setText(it.age?:"")
                /*etPhone.setText(it.mobileNo?:"")
                etMail.setText(it.email?:"")
                etAddress.setText(it.address?:"")*/
            }
            etName.isEnabled = false
            etAge.isEnabled = false
            ContextCompat.getColor(mActivity, R.color.colorText).let {
                etName.textColor = it
                etAge.textColor = it
            }
            etName.backgroundResource = R.drawable.bg_grey_rounded_edittext_normal
            etAge.backgroundResource = R.drawable.bg_grey_rounded_edittext_normal

        }

        setLiveDataObservers()

    }

    private fun View.setTextChangeListener(){
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etName, MyTextChangeValidationUtils.VALIDATION_EMPTY)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAge, MyTextChangeValidationUtils.VALIDATION_AGE)
        //MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etPhone, MyTextChangeValidationUtils.VALIDATION_PHONE)
        //MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL)
        //MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAddress, MyTextChangeValidationUtils.VALIDATION_EMPTY)

        etName.filters = EmojiFilter.getFilter(100)
        etAge.filters = EmojiFilter.getFilter(3)
        //etPhone.filters = EmojiFilter.getFilter(15)
        //etMail.filters = EmojiFilter.getFilter(100)
        //etAddress.filters = EmojiFilter.getFilter(1000)
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(rootView.selRelation)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.selRelation -> {
                onSelRelationClick()
            }
        }
    }

    private fun onSelRelationClick(){
        dialogMsg.showSelectionListDialog(getString(R.string.select_relation), relationsList, isCancelable = true, onItemClick = {
            rootView.txtRelation.text = it.str
        })
    }

    private fun setLiveDataObservers(){

        viewModel.nextClickMLD.observe(viewLifecycleOwner, Observer {
            if (isSelf){
                if(it == 0) onNextClick()
            }else{
                if(it == 1) onNextClick()
            }
        })

    }

    private fun onNextClick(){
        var uId = ""
        UserModel.getSavedUserModel()?.let {
            if (it.userId?.trim().isNullOrEmpty()){
                    dialogMsg.showGeneralError("Incomplete inputs!", btnTxt = "Retry", onClickAction = {
                        UserModel.clearSavedLogin()
                        mActivity.goToLoginActivity(false)
                        mActivity.finishAffinity()
                    })
                    return
            }else{
                uId = it.userId!!
            }
        }

        val name = etName.text?.toString()?.trim()
        if (!isSelf && !MyTextChangeValidationUtils.applyValidation(etName, MyTextChangeValidationUtils.VALIDATION_NAME, name, getString(R.string.err_enter_valid_name))) return

        val age = etAge.text?.toString()?.trim()
        if (!isSelf && !MyTextChangeValidationUtils.applyValidation(etAge, MyTextChangeValidationUtils.VALIDATION_AGE, age, getString(R.string.err_enter_valid_age))) return

        val relation = if (isSelf) "Self" else txtRelation.text?.toString()?.trim()
        if (!isSelf && !MyTextChangeValidationUtils.applyValidation(txtRelation, MyTextChangeValidationUtils.VALIDATION_EMPTY, relation, getString(R.string.err_select_relation))) return

        /*val phone = etPhone.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etPhone, MyTextChangeValidationUtils.VALIDATION_PHONE, phone, getString(R.string.err_enter_valid_phn_num))) return

        val mail = etMail.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL, mail, getString(R.string.err_enter_valid_mail))) return

        val address = etAddress.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etAddress, MyTextChangeValidationUtils.VALIDATION_EMPTY, address, getString(R.string.err_enter_valid_address))) return*/

        val submitDataModel = SubmitDataModel()
        submitDataModel.apply {
            userId = uId
            memberName = name
            memberAge = age
            memberRelation = relation
            memberType = if (isSelf) "1" else "2"
        }

        SubmitDataModel.saveSubmitModel(submitDataModel)

        mActivity.goToTravelHistoryActivity(true)

    }

}