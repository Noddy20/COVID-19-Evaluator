package com.mdm_app_covid_19.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_travel_history.*
import kotlinx.android.synthetic.main.activity_travel_history.btnNext
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

    private var travelType = 0

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

    override fun onBackPressed() {
        super.onBackPressed()
        SubmitDataModel.clearSubmitModel()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun init(){
        setClickListeners()


        dialogMsg = DialogMsg(this)
        dialogMsg.showPleaseWait()

        txtCountry.hint = getString(R.string.country)

        adapter = TravelHistoryQuestionAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        noTravelUi()

        setLiveDataObservable()
    }

    private fun noTravelUi(){

        //TransitionManager.beginDelayedTransition(viewRootContent)

        //btnInternational.backgroundResource = R.drawable.bg_accent_rounded
        //btnInternational.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        btnDomestic.isSelected = false
        btnInternational.isSelected = false

        // btnDomestic.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        //btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        txtCity.text = ""
        txtCountry.text = ""

        if (travelType == 1) {
            txtCountry.hint = getString(R.string.country)
            txtCountry.error = null
            txtCity.error = null
        }else if (travelType == 2){
            txtCountry.hint = getString(R.string.state)
            txtCity.hint = getString(R.string.city)

            progressBarCity.hide()

            txtCountry.error = null
            txtCity.error = null
        }

        travelType = 0
    }

    private fun internationalUi(){
        travelType = 1

        //TransitionManager.beginDelayedTransition(viewRootContent)

        //btnInternational.backgroundResource = R.drawable.bg_accent_rounded
        //btnInternational.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        btnDomestic.isSelected = false
        btnInternational.isSelected = true

       // btnDomestic.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        //btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        selCity.hide()
        txtCity.text = ""
        txtCountry.text = ""
        txtCountry.hint = "${getString(R.string.country)}*"
        txtCountry.error = null
    }

    private fun domesticUi(){
        travelType = 2

        //TransitionManager.beginDelayedTransition(viewRootContent)

       // btnDomestic.backgroundResource = R.drawable.bg_accent_rounded
        //btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        //btnInternational.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        //btnInternational.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        btnInternational.isSelected = false
        btnDomestic.isSelected = true

        txtCountry.text = ""
        txtCountry.hint = "${getString(R.string.state)}*"
        txtCity.hint = "${getString(R.string.city)}*"
        txtCountry.error = null
        txtCity.error = null
        selCity.show()
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        btnNext.typeface = ResourcesCompat.getFont(this, R.font.font_open_sans_bold)

        ResourcesCompat.getFont(this, R.font.font_open_sans_regular).let {
            btnInternational.typeface = it
            btnDomestic.typeface = it
        }

        clickObservable.addViewClicks(btnNext, btnInternational, btnDomestic, selCountry, selCity)
    }

    private val clickObservable = DisposableClickListener<Int>{
        when(it){
            R.id.btnNext -> {
                onNextClick()
            }
            R.id.btnInternational -> {
                if (travelType == 1) noTravelUi()
                else internationalUi()
            }
            R.id.btnDomestic -> {
                if (travelType == 2) noTravelUi()
                else domesticUi()
            }
            R.id.selCountry -> {
                when (travelType) {
                    0 -> {
                        dialogMsg.showGeneralError("Please select a travel type!")
                    }
                    1 -> {
                        onSelCountryClick()
                    }
                    else -> {
                        onSelStateClick()
                    }
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
        dialogMsg.showSelectionListDialog(getString(R.string.select_country), list, isCancelable = true, searchEnabled = true, compositeDisposable = compositeDisposable,
            onItemClick = {
            txtCountry.text = it.str
            txtCountry.error = null
        })
    }

    private fun onSelStateClick(){
        if (stateList.isNullOrEmpty()) return

        val list = stateList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_state), list, isCancelable = true, searchEnabled = true, compositeDisposable = compositeDisposable,
            onItemClick = {
            txtCountry.text = it.str
            txtCountry.error = null
            cityList = ArrayList()
            setCitiesObservable(it.str)
        })
    }


    private fun onSelCityClick(){
        if (txtCountry.text?.toString()?.trim().isNullOrEmpty()){
            dialogMsg.showGeneralError("Please select a state!", cancelable = true)
            return
        }
        if (cityList.isNullOrEmpty()) return

        val list = cityList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_city), list, isCancelable = true, searchEnabled = true, compositeDisposable = compositeDisposable,
            onItemClick = {
            txtCity.text = it.str
            txtCity.error = null
        })
    }

    private fun setCitiesObservable(stateName: String){
        txtCity.text = ""
        txtCity.hint = "${getString(R.string.city)}*"
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
                goToWhomActivity(false)
                finishAffinity()
            })

            return
        }

        when (travelType) {
            1 -> {
                val country = txtCountry.text?.toString()?.trim()

                if (!MyTextChangeValidationUtils.applyValidation(txtCountry, MyTextChangeValidationUtils.VALIDATION_EMPTY, country, getString(R.string.select_country))) {
                    txtCountry.parent.requestChildFocus(txtCountry, txtCountry)
                    return
                }

                submitModel.country = country
                submitModel.state = ""
                submitModel.city = ""
                submitModel.travelType = "1"

            }
            2 -> {
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
            else -> {
                submitModel.country = ""
                submitModel.state = ""
                submitModel.city = ""
                submitModel.travelType = "0"
            }
        }

        var totalPoints = 0
        val questions = adapter.getItemList()
        val jsonArr = JSONArray()
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

        submitModel.travelQue = jsonArr.toString()
        submitModel.totalPoints = totalPoints.toString()

        Log.v(TAG, "Save Model $submitModel")

        SubmitDataModel.saveSubmitModel(submitModel)

        goToCheckListActivity(true)
    }

}
