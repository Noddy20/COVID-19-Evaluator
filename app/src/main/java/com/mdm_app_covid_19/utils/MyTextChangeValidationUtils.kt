package com.mdm_app_covid_19.utils

import android.util.Log
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

object MyTextChangeValidationUtils {

    const val VALIDATION_EMPTY = 0
    const val VALIDATION_NAME = 1
    const val VALIDATION_EMAIL = 2
    const val VALIDATION_PHONE = 3
    const val VALIDATION_AGE = 4
    const val VALIDATION_MANUAL = 5
    const val VALIDATION_OTP = 6

    fun initRxValidation(compositeDisposable: CompositeDisposable, editText: EditText,
                         validationType: Int, skipCount: Long = 1, onSubscribeAction: ((input: String) -> Unit)? = null) {

        if (compositeDisposable.isDisposed) return

        compositeDisposable.add(
            RxTextView.textChangeEvents(editText)
                .skip(skipCount)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { err ->
                    Log.d("RxErr_", "" + err)
                }
                .subscribe {
                    if (validationType == VALIDATION_MANUAL){
                        onSubscribeAction?.invoke(it.text().toString().trim())
                    }else{
                        applyValidation(editText, validationType, it.text().toString().trim())
                    }
                })
    }

    fun applyValidation(editText: EditText, validationType: Int, strInput: String?, errMessage: String? = null) : Boolean {
        when (validationType) {
            VALIDATION_NAME -> {
                if (!strInput.isNullOrEmpty() && MyValidations.isValidName(strInput)) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }

            VALIDATION_EMAIL -> {
                if (!strInput.isNullOrEmpty() && MyValidations.isValidEmail(strInput)) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }

            VALIDATION_EMPTY -> {
                if (!strInput.isNullOrEmpty()) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }

            VALIDATION_PHONE -> {
                if (!strInput.isNullOrEmpty() && MyValidations.isValidMobileNumber(strInput)) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }

            VALIDATION_AGE -> {
                if (!strInput.isNullOrEmpty() && MyValidations.isValidAge(strInput)) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }
            VALIDATION_OTP -> {
                if (!strInput.isNullOrEmpty() && MyValidations.isValidOtp(strInput)) {
                    editText.error = null
                    return true
                }else{
                    errMessage?.let{
                        editText.error = it
                    }
                }
            }

        }
        return false
    }

}