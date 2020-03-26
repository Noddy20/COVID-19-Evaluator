package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.utils.SetUpToolbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_personal_info.*


class SignUpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_personal_info), true)

        init()

    }

    private fun init(){
        UserModel.getSavedUserModel()?.let {
            etPhone.setText(it.mobileNo?:"")
        }



    }


}
