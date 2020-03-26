package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.utils.SetUpToolbar

class ToWhomActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_whom)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_to_whom), true)



    }
}
