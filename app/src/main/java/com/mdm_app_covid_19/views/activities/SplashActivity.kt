package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.data.repo.FirebaseInstances
import com.mdm_app_covid_19.extFunctions.goToLoginActivity
import com.mdm_app_covid_19.extFunctions.goToSignUpActivity
import com.mdm_app_covid_19.extFunctions.goToWhomActivity
import com.mdm_app_covid_19.extFunctions.setFullScreen
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setFullScreen()
        setContentView(R.layout.activity_splash)

        val userModel = UserModel.getSavedUserModel()

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

        Handler().postDelayed({
            if(FirebaseInstances.mAuth.currentUser != null && !userModel?.userId?.trim().isNullOrEmpty()){
                goToWhomActivity()
            }else{
                UserModel.clearSavedLogin()
                if (FirebaseInstances.mAuth.currentUser != null){
                    FirebaseInstances.mAuth.signOut()
                }
                goToLoginActivity()
            }
            finish()
        }, 1000)

    }
}
