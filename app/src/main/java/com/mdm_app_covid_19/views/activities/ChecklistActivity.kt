package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.data.models.SubmitDataModel
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.ChecklistActivityVM
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.ToWhomActivityVM
import com.mdm_app_covid_19.views.adapters.TravelHistoryQuestionAdapter
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_checklist.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class ChecklistActivity : BaseActivity() {

    companion object{
        private const val TAG = "ChecklistActivity"
    }

    private val viewModel: ChecklistActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private lateinit var adapter: TravelHistoryQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_checklist), true)

        init()

    }

    private fun init(){
        setClickListeners()

        dialogMsg = DialogMsg(this)

        adapter = TravelHistoryQuestionAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setLiveDataObserver()
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnNext)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                onNextCLick()
            }
        }
    }

    private fun setLiveDataObserver(){
        dialogMsg.showPleaseWait()
        viewModel.getChecklistQuestionsObservable().observeOnce(this, Observer {
            dialogMsg.dismiss()
            if(it.responseStatus == ResponseStatus.Loaded && !it.data.isNullOrEmpty()){
                adapter.setItemList(it.data!!)
            }else{
                val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                dialogMsg.showGeneralError(msg, onClickAction = {
                    setLiveDataObserver()
                }, btnTxt = "Retry")
            }
        })
    }

    private fun onNextCLick(){
        val submitModel = SubmitDataModel.getPrevSubmitModel()

        if(submitModel == null){
            dialogMsg.showGeneralError("Incomplete inputs!", btnTxt = "Retry", onClickAction = {
                goToWhomActivity()
                finishAffinity()
            })

            return
        }

        val questions = adapter.getItemList()
        val jsonArr = JSONArray()

        var totalPoints = 0

        repeat(questions.size){
            JSONObject().let { obj ->
                val question = questions[it]
                if (question.answer != "1" && question.answer != "2"){
                    adapter.updateUnAnswered(it)
                    kotlin.runCatching {
                        //recyclerView.smoothScrollToPosition(it)
                        scrollView.scrollBy(0, recyclerView.getChildAt(it).bottom)
                    }
                    return
                }
                if (question.answer == "1"){
                    totalPoints += question.quePoints?.toInt()?:0
                }

                obj.put("que_id", question.queId)
                obj.put("answer", question.answer)
            }.also {obj ->
                jsonArr.put(it, obj)
            }
        }

        submitModel.checklistQue = jsonArr.toString()
        submitModel.totalPoints = totalPoints.toString()

        SubmitDataModel.saveSubmitModel(submitModel)

        goToResultActivity()
    }

}
