package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.mdm_app_covid_19.BuildConfig
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UpdateModel
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.FirebaseInstances
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var dialogMsg: DialogMsg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setFullScreen()
        setContentView(R.layout.activity_splash)

        dialogMsg = DialogMsg(this)

        Glide.with(this)
            .load(R.drawable.img_bg_splash)
            .centerCrop()
            .error(R.color.colorPrimaryTheme)
            .into(imgBg)

        Glide.with(this)
            .load(R.drawable.img_covid_19_logo)
            .fitCenter()
            .error(R.color.colorPrimaryTheme)
            .into(imgCovidLogo)


        if (isNetConnected()){
            FirebaseFirestore.getInstance()
                .collection("AppUpdate")
                .document("AppUpdateMDMCOVID19")
                .get().addOnSuccessListener {
                    kotlin.runCatching {
                        val updateModel = it.toObject(UpdateModel::class.java)
                        Log.v("SplashActivity", "UpdateModel = $updateModel")
                        if (updateModel != null){
                            if (BuildConfig.VERSION_CODE < updateModel.versionCode?:1){
                                showUpdateDialog(updateModel)
                            }else{
                                proceedToNextScreen(800)
                            }
                        }else{
                            proceedToNextScreen(800)
                        }
                    }.onFailure {
                        proceedToNextScreen()
                    }
                }.addOnFailureListener {
                    Log.v("SplashActivity", "UpdateModel Exc $it")
                    proceedToNextScreen()
                }
        }else{
            proceedToNextScreen()
        }


    }

    private fun showUpdateDialog(updateModel: UpdateModel){
        if (updateModel.mandatory == true){
            dialogMsg.showAppUpdate(updateClick = {
                updateFromPlayStoreIntent()
            }, isMandatory = true)
        }else{
            dialogMsg.showAppUpdate(updateClick = {
                updateFromPlayStoreIntent()
            },laterClick = {
                proceedToNextScreen(800)
                dialogMsg.dismiss()
            })
        }
    }

    private fun proceedToNextScreen(delay: Long = 1000){
        val userModel = UserModel.getSavedUserModel()

        Handler().postDelayed({
            if(FirebaseInstances.mAuth.currentUser != null && !userModel?.userId?.trim().isNullOrEmpty()){
                goToWhomActivity(true)
                //goToSignUpActivity()
                // goToWebView("Instructions", "http://35.154.166.4:3000/images/conclusion/1585304299791CoronaAdvisior.pdf")
            }else{
                UserModel.clearSavedLogin()
                if (FirebaseInstances.mAuth.currentUser != null){
                    FirebaseInstances.mAuth.signOut()
                }
                goToLoginActivity(true)
            }
            finish()
        }, delay)
    }
}
