package com.mdm_app_covid_19.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
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
import kotlinx.android.synthetic.main.activity_checklist.btnNext
import kotlinx.android.synthetic.main.activity_checklist.recyclerView
import kotlinx.android.synthetic.main.activity_checklist.scrollView
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_travel_history.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference

class ChecklistActivity : BaseActivity() {

    companion object{
        private const val TAG = "ChecklistActivity"

        var mActivity: WeakReference<ChecklistActivity>? = null

        private fun initialize(activity: ChecklistActivity) {
            mActivity = WeakReference(activity)
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
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

        btnNext.typeface = ResourcesCompat.getFont(this, R.font.font_open_sans_bold)

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
                goToWhomActivity(false)
                finishAffinity()
            })

            return
        }

        val questions = adapter.getItemList()
        val jsonArr = JSONArray()

        var totalPoints = 0
            kotlin.runCatching {
                totalPoints =  submitModel.totalPoints?.toInt()?:0
            }

        repeat(questions.size){
            JSONObject().let { obj ->
                val question = questions[it]
                if (question.answer != "1" && question.answer != "2"){
                    adapter.updateUnAnswered(it)
                    kotlin.runCatching {
                        recyclerView.getChildAt(it).parent.requestChildFocus(recyclerView.getChildAt(it), recyclerView.getChildAt(it))
                        //scrollView.scrollBy(0, recyclerView.getChildAt(it).bottom)
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

        goToResultActivity(true)

        /*Intent(this, ResultActivity::class.java).let {
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also {
            startActivity(it)
        }*/

    }

}
