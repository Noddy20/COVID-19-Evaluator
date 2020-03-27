package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.extFunctions.addViewClicks
import com.mdm_app_covid_19.extFunctions.goToResultActivity
import com.mdm_app_covid_19.extFunctions.goToTravelHistoryActivity
import com.mdm_app_covid_19.extFunctions.plusAssign
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.ToWhomActivityVM
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_to_whom.*

class ChecklistActivity : BaseActivity() {

    companion object{
        private const val TAG = "ChecklistActivity"
    }

    private val viewModel: ToWhomActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        init()

    }

    private fun init(){
        setClickListeners()

        dialogMsg = DialogMsg(this)



    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnNext)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                goToResultActivity()
            }
        }
    }

}
