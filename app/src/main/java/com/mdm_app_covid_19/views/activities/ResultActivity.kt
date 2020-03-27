package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.extFunctions.addViewClicks
import com.mdm_app_covid_19.extFunctions.goToResultActivity
import com.mdm_app_covid_19.extFunctions.goToWhomActivity
import com.mdm_app_covid_19.extFunctions.plusAssign
import com.mdm_app_covid_19.utils.DisposableClickListener
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)


    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnBack)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnBack -> {
                goToWhomActivity()
            }
        }
    }
}
