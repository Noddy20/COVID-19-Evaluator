package com.mdm_app_covid_19.views.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.EmojiFilter
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.SignUpActivityVM
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_personal_info.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast


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

        if (UserModel.getSavedUserModel()?.mobileNo?.trim().isNullOrEmpty()){
            dialogMsg.showGeneralError("Session expired!", btnTxt = "Retry", onClickAction = {
                goToLoginActivity(false)
                finishAffinity()
            })
        }else{
            init()
        }
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

        etPhone.isEnabled = false
        etPhone.textColor = ContextCompat.getColor(this, R.color.colorText)

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

        if (userModel?.address.isNullOrEmpty())
            retrieveAddress()

    }

    private fun setTextChangeListener(){
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etName, MyTextChangeValidationUtils.VALIDATION_EMPTY)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAge, MyTextChangeValidationUtils.VALIDATION_AGE)
        //MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etPhone, MyTextChangeValidationUtils.VALIDATION_PHONE)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL)
        MyTextChangeValidationUtils.initRxValidation(compositeDisposable, etAddress, MyTextChangeValidationUtils.VALIDATION_EMPTY)


        etName.filters = EmojiFilter.getFilter(100)
        etAge.filters = EmojiFilter.getFilter(3)
        //etPhone.filters = EmojiFilter.getFilter(13)
        etMail.filters = EmojiFilter.getFilter(100)
        etAddress.filters = EmojiFilter.getFilter(1000)

        etAddress.setOnTouchListener{ v, event ->
            if (v.id == R.id.etAddress) {
                v.parent.requestDisallowInterceptTouchEvent(true);
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        btnNext.typeface = ResourcesCompat.getFont(this, R.font.font_open_sans_bold)

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
                goToWhomActivity(true)
                finish()
            }else{
                val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                dialogMsg.showGeneralError(msg, cancelable = true)
            }

        })
    }

    private fun onNextClick(){
        val name = etName.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etName, MyTextChangeValidationUtils.VALIDATION_NAME, name, getString(R.string.err_enter_valid_name))) return

        val age = etAge.text?.toString()?.trim()
        if (!MyTextChangeValidationUtils.applyValidation(etAge, MyTextChangeValidationUtils.VALIDATION_AGE, age, getString(R.string.err_enter_valid_age))) return

        val phone = etPhone.text?.toString()?.trim()
        //if (!MyTextChangeValidationUtils.applyValidation(etPhone, MyTextChangeValidationUtils.VALIDATION_EMPTY, phone, getString(R.string.err_enter_valid_phn_num))) return

        val mail = etMail.text?.toString()?.trim()
        if (!mail.isNullOrEmpty() && !MyTextChangeValidationUtils.applyValidation(etMail, MyTextChangeValidationUtils.VALIDATION_EMAIL, mail, getString(R.string.err_enter_valid_mail))) return

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

    private fun retrieveAddress(){
        if (hasPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )){
            Log.v(TAG, "retrieveAddress")
            val client: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    setLocationObserver(location)
                }
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }

    private fun setLocationObserver(location: Location){
        Log.v(TAG, "setLocationObserver ${location.latitude}")
        viewModel.getAddressObservable(location.latitude, location.longitude).observe(this, Observer {
            etAddress.setText(it?:"")
        })
    }

    fun hasPermissions(vararg permissions: String): Boolean{
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1001 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            retrieveAddress()
        }
    }

}
