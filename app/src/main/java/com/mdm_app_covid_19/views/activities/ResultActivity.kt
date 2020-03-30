package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.ResultModel
import com.mdm_app_covid_19.data.models.SubmitDataModel
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.httpCalls.MyMoshi
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.ResultActivityVM
import com.mdm_app_covid_19.viewModels.TravelHistoryActivityVM
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class ResultActivity : BaseActivity() {

    companion object{
        private const val TAG = "ResultActivity"
    }

    private val viewModel: ResultActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private var resultModel: ResultModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_app_recommendation), false)

        init()
    }

    override fun onBackPressed() {
        goToWhomActivity(false)
        finishAffinity()
    }

    private fun init() {
        setClickListeners()

        dialogMsg = DialogMsg(this)

        val submitDataModel = SubmitDataModel.getPrevSubmitModel()

        if (submitDataModel == null || SubmitDataModel.isInComplete(submitDataModel)) {
            dialogMsg.showGeneralError("Incomplete inputs!", btnTxt = "Retry", onClickAction = {
                goToWhomActivity(false)
                finishAffinity()
            })
        }else{
            val params = HashMap<String, Any?>()
            submitDataModel.run {
                params["city"] = city
                params["state"] = state
                params["country"] = country
                params["user_id"] = userId
                params["member_name"] = memberName
                params["member_age"] = memberAge
                params["member_relation"] = memberRelation
                params["member_type"] = memberType
                params["total_points"] = totalPoints
                params["travel_type"] = travelType
                params["checklist_que"] = checklistQue
                params["travel_que"] = travelQue
            }
            setLiveDataObserver(params)
        }


    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        btnBack.typeface = ResourcesCompat.getFont(this, R.font.font_open_sans_bold)
        clickObservable.addViewClicks(btnBack, viewLink)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnBack -> {
                goToWhomActivity(false)
                finishAffinity()
            }
            R.id.viewLink -> {
                goToWebView("Corona - Government Advisory", resultModel?.conclusionFileUrl?.trim()?:"", true)
            }
        }
    }

    private fun setLiveDataObserver(params: HashMap<String, Any?>){
        dialogMsg.showPleaseWait()
        viewModel.getChecklistQuestionsObservable(params).observeOnce(this, Observer {
            dialogMsg.dismiss()
            if (it.responseStatus == ResponseStatus.Loaded && !it.data?.conclusionMessage?.trim().isNullOrEmpty()){
                resultModel = it.data
                setResultData(it.data!!)
            }else{
                val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                dialogMsg.showGeneralError(msg, btnTxt = "Retry", onClickAction = {
                    setLiveDataObserver(params)
                })
            }
        })
    }

    private fun setResultData(resultModel: ResultModel){
        val title: String? = if (!resultModel.conclusionTitle?.trim().isNullOrEmpty()){
            resultModel.conclusionTitle?.trim()
        } else null

        val message: String? = if (!resultModel.conclusionMessage?.trim().isNullOrEmpty()){
            resultModel.conclusionMessage?.trim()
        }else null

        val strMsg = title + (if(!title.isNullOrEmpty() && !message.isNullOrEmpty()) "\n\n" else "") + message
        txtMessage.text = strMsg

        if (resultModel.conclusionFileUrl?.trim().isNullOrEmpty()){
            viewLink.hide()
        }else{
            //txtLink.text = resultModel.conclusionFileUrl?.trim()
            viewLink.show()
        }

    }

}
