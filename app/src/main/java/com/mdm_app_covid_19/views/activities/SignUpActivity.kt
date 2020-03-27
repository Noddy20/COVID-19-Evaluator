package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.AuthRepo
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.addViewClicks
import com.mdm_app_covid_19.extFunctions.goToWhomActivity
import com.mdm_app_covid_19.extFunctions.hide
import com.mdm_app_covid_19.extFunctions.plusAssign
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.SignUpActivityVM
import com.mdm_app_covid_19.viewModels.ToWhomActivityVM
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_personal_info.*


class SignUpActivity : BaseActivity() {

    companion object{
        private const val TAG = "SignUpActivity"

    }

    private val viewModel: SignUpActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private var userModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_personal_info), true)

        init()

    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        super.onDestroy()
    }

    private fun init(){
        setClickListeners()
        setTextChangeListener()

        dialogMsg = DialogMsg(this)

        userModel = UserModel("", "", "", "", "", "")

        UserModel.getSavedUserModel()?.let {
            userModel = it

            etName.setText(it.name?:"")
            etAge.setText(it.age?:"")
            etPhone.setText(it.mobileNo?:"")
            etMail.setText(it.email?:"")
            etAddress.setText(it.address?:"")

        }

        selRelation.hide()

        /*val params = HashMap<String, Any?>()
        params["mobileNo"] = "+918282828282"
        AuthRepo(compositeDisposable, application).getServerLoginObservable(params).observe(this, Observer {
            Log.v(TAG, "User $it")
        })*/

        setLiveDataObservers()

    }

    private fun setTextChangeListener(){
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etName, MyTextChangeValidationUtils.VALIDATION_EMPTY)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAge, MyTextChangeValidationUtils.VALIDATION_AGE)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etPhone, MyTextChangeValidationUtils.VALIDATION_PHONE)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAddress, MyTextChangeValidationUtils.VALIDATION_EMPTY)
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnNext)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                onNextClick()
            }
        }
    }

    private fun setLiveDataObservers(){
        viewModel.signUpLD.observe(this, Observer {
            dialogMsg.dismiss()

            if (it.responseStatus == ResponseStatus.Loaded && !it.data?.userId.isNullOrEmpty()){
                UserModel.saveUserModel(it.data!!)
                goToWhomActivity()
            }else{
                dialogMsg.showGeneralError()
            }

        })
    }

    private fun onNextClick(){
        val name = etName.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etName, MyTextChangeValidationUtils.VALIDATION_EMPTY, name, getString(R.string.err_enter_valid_name))) return

        val age = etAge.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etAge, MyTextChangeValidationUtils.VALIDATION_AGE, age, getString(R.string.err_enter_valid_age))) return

        val phone = etPhone.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etPhone, MyTextChangeValidationUtils.VALIDATION_PHONE, phone, getString(R.string.err_enter_valid_phn_num))) return

        val mail = etMail.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL, mail, getString(R.string.err_enter_valid_mail))) return

        val address = etAddress.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etAddress, MyTextChangeValidationUtils.VALIDATION_EMPTY, address, getString(R.string.err_enter_valid_address))) return

        dialogMsg.showPleaseWait("Signing up...")

        userModel?.let {
            it.name = name
            it.age = age
            it.mobileNo = phone
            it.email = mail
            it.address = address

            viewModel.setSignUpData(it)
        }
    }

}
