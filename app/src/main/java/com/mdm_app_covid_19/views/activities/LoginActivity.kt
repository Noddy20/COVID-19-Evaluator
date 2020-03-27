package com.mdm_app_covid_19.views.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.AuthRepo
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.viewModels.LoginActivityVM
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.imgBg
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast


class LoginActivity : BaseActivity() {

    companion object{
        private const val TAG = "LoginActivity"
    }

    private val viewModel: LoginActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()

    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        super.onDestroy()
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnSendOtp, btnResendOtp)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnSendOtp -> {
                if(viewOtpEdit.isVisible){
                    verifyOtpClick()
                }else {
                    sendOtpClick()
                }
            }
            R.id.btnResendOtp -> {
                sendOtpClick()
            }
        }
    }

    private fun init(){
        dialogMsg = DialogMsg(this)

        viewOtpEdit.hide()
        txtTimer.invisible()
        btnResendOtp.hide()
        btnResendOtp.setEnableDisable(false)

        Glide.with(this)
            .load(R.drawable.img_login_bg)
            .fitCenter()
            .into(imgBg)

        setLiveDataObservers()
        setClickListeners()
        setTextChangeListener()

        etPhnNum.onFocusChange { v, hasFocus ->
            viewPhnEdit.isSelected = hasFocus
        }

        /*val params = HashMap<String, Any?>()
        params["mobileNo"] = "+918282828282"
        viewModel.serverLoginLD.observe(this, Observer {
            Log.v(TAG, "User $it")
        })

        viewModel.setUserDataParam(params)*/
    }

    private fun setTextChangeListener(){
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etPhnNum, MyTextChangeValidationUtils.VALIDATION_PHONE)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etOtp, MyTextChangeValidationUtils.VALIDATION_OTP)
    }

    private fun setLiveDataObservers(){
        viewModel.sendOtpLD.observe(this, Observer {
            Log.v(TAG, "sendOtpLD States $it")
            if (it.responseStatus == ResponseStatus.Loaded){
                dialogMsg.dismiss()
                showOtpBox()
                if (it.statusCode == 3){  //OTP sent
                    toast(R.string.otp_sent)
                    startResendTimer()
                }else if (it.data != null){       //Verification Cred generated
                    etOtp.setText(it.data?.smsCode?:"")
                    btnResendOtp.setEnableDisable(true)
                    dialogMsg.showPleaseWait("Verifying Phone No.")
                }
            }else{
                dialogMsg.showGeneralError()
                btnResendOtp.setEnableDisable(true)
            }
        })

        viewModel.verifyOtpLD.observe(this, Observer {
            Log.v(TAG, "verifyOtpLD States $it")
            if (it.data == true){
                val params = HashMap<String, Any?>()
                params["mobileNo"] = phoneNumber
                viewModel.setUserDataParam(params)
            }else{
                dialogMsg.showGeneralError()
            }
        })


        viewModel.serverLoginLD.observe(this, Observer {
            Log.v(TAG, "serverLoginLD States $it")
            if (it.data != null && !it.data?.userId.isNullOrEmpty()) {
                UserModel.saveUserModel(it.data!!)
            }else{
                UserModel.saveUserModel(UserModel("", phoneNumber, "", "", "", ""))
            }
            toast(R.string.verif_success)
            dialogMsg.dismiss()
            goToSignUpActivity()
        })

    }

    private fun startResendTimer() {
        txtTimer.show()
        etPhnNum.isEnabled = false
        //btnSendOtp.setEnableDisable(false)
        btnResendOtp.setEnableDisable(false)

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished.toInt() / 1000
                txtTimer.text = "$time"
            }

            override fun onFinish() {
                txtTimer.invisible()
                etPhnNum.isEnabled = true
               // btnSendOtp.setEnableDisable(true)
                btnResendOtp.setEnableDisable(true)
            }
        }.start()
    }

    private fun sendOtpClick(){
        var phoneNum = etPhnNum.text?.toString()?.trim()

        if (!MyTextChangeValidationUtils.applyValidation(etPhnNum, MyTextChangeValidationUtils.VALIDATION_PHONE, phoneNum, getString(R.string.err_enter_valid_phn_num))) return

        dialogMsg.showPleaseWait(getString(R.string.sending_otp))

        phoneNum = "+91$phoneNum"
        phoneNumber = phoneNum

        viewModel.sendOtpOn(phoneNum)

    }

    private fun verifyOtpClick(){
        val otpCode = etOtp.text?.toString()?.trim()

        if (!MyTextChangeValidationUtils.applyValidation(etOtp, MyTextChangeValidationUtils.VALIDATION_OTP, otpCode, getString(R.string.err_enter_valid_otp))) return

        dialogMsg.showPleaseWait(getString(R.string.verifying_otp))

        viewModel.setVerificationCode(otpCode?:"")

    }

    private fun showOtpBox(){
        TransitionManager.beginDelayedTransition(viewInputs)
        btnSendOtp.text = getString(R.string.verify_otp)
        viewOtpEdit.show()
        btnResendOtp.show()
    }


}
