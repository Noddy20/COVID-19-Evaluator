package com.mdm_app_covid_19.data.repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.error.ANError
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.extFunctions.isNetConnected
import com.mdm_app_covid_19.httpCalls.FastNetworking
import com.mdm_app_covid_19.httpCalls.MyMoshi
import com.mdm_app_covid_19.httpCalls.Urls
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class AuthRepo(private val compositeDisposable: CompositeDisposable, private val mApp: Application){

    companion object{
        private const val TAG = "AuthRepo"

        private const val RESEND_OTP_TIME_INTERVAL = 60L //In Seconds

    }

    private val mAuth = FirebaseInstances.mAuth
    private val mPhoneAuthProvider = PhoneAuthProvider.getInstance()

    private var mVerificationId: String? = null
    private var mResendToken: ForceResendingToken? = null


    fun sendOtp(phoneNo: String, onCodeSent: (verificationId: String?) -> Unit) : Observable<PhoneAuthCredential> {
        return Observable.create { emitter ->
            val mCallBacks: OnVerificationStateChangedCallbacks =
                object : OnVerificationStateChangedCallbacks() {
                    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                        mVerificationId = verificationId
                        mResendToken = token
                        onCodeSent(verificationId)
                    }

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        emitter.onNext(phoneAuthCredential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        emitter.onError(e)
                    }
                }

            mPhoneAuthProvider.verifyPhoneNumber(
                phoneNo,
                RESEND_OTP_TIME_INTERVAL,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBacks
            )
        }

        /*return Single.create { emitter ->

            val mCallBacks: OnVerificationStateChangedCallbacks =
                object : OnVerificationStateChangedCallbacks() {
                    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                        mVerificationId = verificationId
                        mResendToken = token
                        onCodeSent(verificationId)
                    }

                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        emitter.onSuccess(phoneAuthCredential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        emitter.onError(e)
                    }
                }

            mPhoneAuthProvider.verifyPhoneNumber(
                phoneNo,
                RESEND_OTP_TIME_INTERVAL,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBacks
            )
        }*/
    }

    fun getSendOtpObserver(onVerificationCompleted : (creds: PhoneAuthCredential?, e: Throwable?) -> Unit): Observer<PhoneAuthCredential?>? {
        return object : Observer<PhoneAuthCredential?>{
            override fun onComplete() {

            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(t: PhoneAuthCredential) {
                onVerificationCompleted(t, null)
            }

            override fun onError(e: Throwable) {
                onVerificationCompleted(null, e)
            }

        }

        /*return object : DisposableSingleObserver<PhoneAuthCredential?>() {
            override fun onSuccess(phoneAuthCredential: PhoneAuthCredential) {
                onVerificationCompleted(phoneAuthCredential, null)
            }

            override fun onError(e: Throwable) {
                onVerificationCompleted(null, e)
            }
        }*/
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?): Observable<Boolean> {

        return Observable.create { emitter ->
            mAuth.signInWithCredential(credential!!)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD,
                    OnCompleteListener { task: Task<AuthResult?> ->
                        emitter.onNext(
                            task.isSuccessful
                        )
                    }
                )
                .addOnFailureListener(
                    TaskExecutors.MAIN_THREAD,
                    OnFailureListener { t: Exception? ->
                        emitter.onError(t!!)
                    }
                )
        }

        /*return Single.create { emitter: SingleEmitter<Boolean?> ->
            mAuth.signInWithCredential(credential!!)
                .addOnCompleteListener(TaskExecutors.MAIN_THREAD,
                    OnCompleteListener { task: Task<AuthResult?> ->
                        emitter.onSuccess(
                            task.isSuccessful
                        )
                    }
                )
                .addOnFailureListener(
                    TaskExecutors.MAIN_THREAD,
                    OnFailureListener { t: Exception? -> emitter.onError(t!!) }
                )
        }*/
    }

    fun getSignInObserver(onSignInResult: (success: Boolean, e: Throwable?) -> Unit): Observer<Boolean> {
        return object : Observer<Boolean>{
            override fun onComplete() {
                onSignInResult(true, null)
            }
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(t: Boolean) {
                onSignInResult(true, null)
            }

            override fun onError(e: Throwable) {
                onSignInResult(false, e)
                Log.v(TAG, "SignInObserver failed " + e.message)
            }

        }
        /*return object : DisposableSingleObserver<Boolean?>() {
            override fun onSuccess(aBoolean: Boolean) {
                onSignInResult(true, null)
            }

            override fun onError(e: Throwable) {
                onSignInResult(false, e)
                Log.v(TAG, "SignInObserver failed " + e.message)
            }
        }*/
    }

    fun getServerLoginObservable(params: HashMap<String, Any?>): LiveData<BaseResponse<UserModel?>>{
        val data = MutableLiveData<BaseResponse<UserModel?>>()

        val tag = "getServerLogin"

        fun setNull(status: Int = 0, msg: String = "Something went wrong!", e: Exception? = null){
            Log.v(TAG, "$tag $e")
            val msg = if (mApp.isNetConnected()) msg else mApp.getString(R.string.no_internet)
            data.postValue(BaseResponse(status, msg, null, ResponseStatus.Failed))
        }

        Log.v(TAG, "Url ${Urls.GET_USER_BY_MOBILE}  ${JSONObject(params)}")

        FastNetworking.makeRxCallObservable(FastNetworking.getRxCallPostObservable(Urls.GET_USER_BY_MOBILE, tag, params), compositeDisposable, tag,
            onApiResult = object : FastNetworking.OnApiResult{
                override fun onApiSuccess(response: JSONObject?) {
                    Log.v(TAG, "$tag $response")
                    if (response != null){
                        try {
                            val baseResponse = BaseResponse.parseBaseResponseObj<UserModel?>(response)
                            data.postValue(baseResponse)
                        }catch (e: Exception){
                            setNull(e = e)
                        }
                    }else{
                        setNull()
                    }
                }

                override fun onApiError(anError: ANError) {
                    setNull()
                }

            })

        return data
    }


}