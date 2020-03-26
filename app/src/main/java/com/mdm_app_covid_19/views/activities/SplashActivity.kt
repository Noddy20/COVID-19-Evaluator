package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.extFunctions.goToLoginActivity
import com.mdm_app_covid_19.extFunctions.goToSignUpActivity
import com.mdm_app_covid_19.extFunctions.setFullScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setFullScreen()
        setContentView(R.layout.activity_splash)

        val userModel = UserModel.getSavedUserModel()

        Handler().postDelayed({
            if(!userModel?.userId?.trim().isNullOrEmpty()){
                goToSignUpActivity()
            }else{
                goToLoginActivity()
            }
            finish()
        }, 1000)

    }
}
