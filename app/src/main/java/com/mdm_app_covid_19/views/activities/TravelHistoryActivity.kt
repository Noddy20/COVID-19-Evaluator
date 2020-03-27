package com.mdm_app_covid_19.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.*
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.MyTextChangeValidationUtils
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.TravelHistoryActivityVM
import com.mdm_app_covid_19.views.adapters.TravelHistoryQuestionAdapter
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_travel_history.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject

class TravelHistoryActivity : BaseActivity() {

    companion object{
        private const val TAG = "TravelHistoryAct"
    }

    private val viewModel: TravelHistoryActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private var isInternational = true

    private var countryList: List<CountryModel> = ArrayList()
    private var stateList: List<StateModel> = ArrayList()
    private var cityList: List<CityModel> = ArrayList()
    private var questionList: List<QuestionModel> = ArrayList()

    private lateinit var adapter: TravelHistoryQuestionAdapter
    //private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_history)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_travel_history), true)

        init()

    }

    private fun init(){
        setClickListeners()


        dialogMsg = DialogMsg(this)
        dialogMsg.showPleaseWait()

        adapter = TravelHistoryQuestionAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        internationalUi()

        setLiveDataObservable()
    }

    private fun internationalUi(){
        isInternational = true

        //TransitionManager.beginDelayedTransition(viewRootContent)

        btnInternational.backgroundResource = R.drawable.bg_accent_rounded
        btnInternational.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        btnDomestic.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        selCity.hide()
        txtCity.text = ""
        txtCountry.text = ""
        txtCountry.hint = getString(R.string.country)
        txtCountry.error = null
    }

    private fun domesticUi(){
        isInternational = false

        //TransitionManager.beginDelayedTransition(viewRootContent)

        btnDomestic.backgroundResource = R.drawable.bg_accent_rounded
        btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        btnInternational.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        btnInternational.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        txtCountry.text = ""
        txtCountry.hint = getString(R.string.state)
        txtCountry.error = null
        txtCity.error = null
        selCity.show()
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnNext, btnInternational, btnDomestic, selCountry, selCity)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                onNextClick()
            }
            R.id.btnInternational -> {
                internationalUi()
            }
            R.id.btnDomestic -> {
                domesticUi()
            }
            R.id.selCountry -> {
                if(isInternational) {
                    onSelCountryClick()
                }else{
                    onSelStateClick()
                }
            }
            R.id.selCity -> {
                onSelCityClick()
            }
        }
    }

    private fun onSelCountryClick(){
        if (countryList.isNullOrEmpty()) return

        val list = countryList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_country), list, isCancelable = true, onItemClick = {
            txtCountry.text = it.str
            txtCountry.error = null
        })
    }

    private fun onSelStateClick(){
        if (stateList.isNullOrEmpty()) return

        val list = stateList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_state), list, isCancelable = true, onItemClick = {
            txtCountry.text = it.str
            txtCountry.error = null
            setCitiesObservable(it.str)
        })
    }


    private fun onSelCityClick(){
        if (cityList.isNullOrEmpty()) return

        val list = cityList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_city), list, isCancelable = true, onItemClick = {
            txtCity.text = it.str
            txtCity.error = null
        })
    }

    private fun setCitiesObservable(stateName: String){
        progressBarCity.show()
        viewModel.getCitiesListObservable(stateName).observeOnce(this, Observer {
            Log.v(TAG, "City $it")
            progressBarCity.hide()
            if (it.responseStatus == ResponseStatus.Loaded && !it.data.isNullOrEmpty()){
                cityList = it.data!!
            }else{
                val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                dialogMsg.showGeneralError(msg, onClickAction = {
                    setCitiesObservable(stateName)
                }, btnTxt = "Retry")
            }
        })
    }

    private fun setLiveDataObservable(){

        fun countryObservable(){
            dialogMsg.showPleaseWait()
            if (countryList.isEmpty()){
                viewModel.getCountryListObservable().observeOnce(this, Observer {
                    Log.v(TAG, "Country $it")
                    if (it.responseStatus == ResponseStatus.Loaded && !it.data?.countryList.isNullOrEmpty() && !it.data?.quesitonsList.isNullOrEmpty()){
                        countryList = it.data!!.countryList!!
                        questionList = it.data!!.quesitonsList!!

                        adapter.setItemList(questionList)

                        dialogMsg.dismiss()
                    }else{
                        val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                        dialogMsg.showGeneralError(msg, onClickAction = {
                            countryObservable()
                        }, btnTxt = "Retry")
                    }
                })
            }
        }

        fun stateObservable(){
            progressBarState.show()
            viewModel.getStatesListObservable().observeOnce(this, Observer {
                Log.v(TAG, "State $it")
                progressBarState.hide()
                if (it.responseStatus == ResponseStatus.Loaded && !it.data.isNullOrEmpty()){
                    stateList = it.data!!
                }else{
                    val msg = if (isNetConnected()) "Something went wrong!" else getString(R.string.no_internet)
                    dialogMsg.showGeneralError(msg, onClickAction = {
                        stateObservable()
                    }, btnTxt = "Retry")
                }
            })
        }

        countryObservable()
        stateObservable()

    }

    private fun onNextClick(){
        val submitModel = SubmitDataModel.getPrevSubmitModel()

        if(submitModel == null){
            dialogMsg.showGeneralError("Incomplete inputs!", btnTxt = "Retry", onClickAction = {
                goToWhomActivity()
                finishAffinity()
            })

            return
        }

        if (isInternational){
            val country = txtCountry.text?.toString()?.trim()

            if (!MyTextChangeValidationUtils.applyValidation(txtCountry, MyTextChangeValidationUtils.VALIDATION_EMPTY, country, getString(R.string.select_country))) {
                txtCountry.parent.requestChildFocus(txtCountry, txtCountry)
                return
            }

            submitModel.country = country
            submitModel.state = ""
            submitModel.city = ""
            submitModel.travelType = "1"

        }else {
            val state = txtCountry.text?.toString()?.trim()
            if (!MyTextChangeValidationUtils.applyValidation(txtCountry, MyTextChangeValidationUtils.VALIDATION_EMPTY, state, getString(R.string.select_state))) {
                txtCountry.parent.requestChildFocus(txtCountry, txtCountry)
                return
            }

            val city = txtCity.text?.toString()?.trim()
            if (!MyTextChangeValidationUtils.applyValidation(txtCity, MyTextChangeValidationUtils.VALIDATION_EMPTY, city, getString(R.string.select_state))) {
                txtCity.parent.requestChildFocus(txtCity, txtCity)
                return
            }

            submitModel.country = "India"
            submitModel.state = state
            submitModel.city = city
            submitModel.travelType = "2"
        }

        val questions = adapter.getItemList()
        val jsonArr = JSONArray()
        repeat(questions.size){
            JSONObject().let { obj ->
                val question = questions[it]
                if (question.answer != "1" && question.answer != "2"){
                    adapter.updateUnAnswered(it)
                    kotlin.runCatching {
                        //recyclerView.getChildAt(it).parent.requestChildFocus(recyclerView.getChildAt(it), recyclerView.getChildAt(it))
                        scrollView.scrollBy(0, recyclerView.getChildAt(it).bottom)
                    }
                    return
                }
                obj.put("que_id", question.queId)
                obj.put("answer", question.answer)
            }.also {obj ->
                jsonArr.put(it, obj)
            }
        }

        submitModel.travelQue = jsonArr.toString()

        Log.v(TAG, "Save Model $submitModel")

        SubmitDataModel.saveSubmitModel(submitModel)

        goToCheckListActivity()
    }

}
