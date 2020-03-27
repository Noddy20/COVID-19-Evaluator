package com.mdm_app_covid_19.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.*
import com.mdm_app_covid_19.data.repo.ResponseStatus
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.TravelHistoryActivityVM
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import kotlinx.android.synthetic.main.activity_travel_history.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

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
        txtCountry.hint = getString(R.string.country)
    }

    private fun domesticUi(){
        isInternational = false

        //TransitionManager.beginDelayedTransition(viewRootContent)

        btnDomestic.backgroundResource = R.drawable.bg_accent_rounded
        btnDomestic.textColor = ContextCompat.getColor(this, R.color.colorWhite)

        btnInternational.setAttrAsDrawableBackground(R.attr.selectableItemBackground)
        btnInternational.textColor = ContextCompat.getColor(this, R.color.colorTextDim)

        txtCountry.hint = getString(R.string.state)
        selCity.show()
    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        clickObservable.addViewClicks(btnNext, btnInternational, btnDomestic, selCountry, selCity)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                goToCheckListActivity()
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
        })
    }

    private fun onSelStateClick(){
        if (stateList.isNullOrEmpty()) return

        val list = stateList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_state), list, isCancelable = true, onItemClick = {
            txtCountry.text = it.str
            setCitiesObservable(it.str)
        })
    }


    private fun onSelCityClick(){
        if (cityList.isNullOrEmpty()) return

        val list = cityList.map { SelectionListDialogModel(1, it.name?:"") }
        dialogMsg.showSelectionListDialog(getString(R.string.select_city), list, isCancelable = true, onItemClick = {
            txtCity.text = it.str
        })
    }

    private fun setCitiesObservable(stateName: String){
        progressBarCity.show()
        viewModel.getCitiesListObservable(stateName).observe(this, Observer {
            Log.v(TAG, "City $it")
            progressBarCity.hide()
            if (it.responseStatus == ResponseStatus.Loaded && !it.data.isNullOrEmpty()){
                cityList = it.data!!
            }else{
                dialogMsg.showGeneralError(onClickAction = {
                    setCitiesObservable(stateName)
                }, btnTxt = "Retry")
            }
        })
    }

    private fun setLiveDataObservable(){

        fun countryObservable(){
            dialogMsg.showPleaseWait()
            if (countryList.isEmpty()){
                viewModel.getCountryListObservable().observe(this, Observer {
                    Log.v(TAG, "Country $it")
                    if (it.responseStatus == ResponseStatus.Loaded && !it.data?.countryList.isNullOrEmpty() && !it.data?.quesitonsList.isNullOrEmpty()){
                        countryList = it.data!!.countryList!!
                        questionList = it.data!!.quesitonsList!!
                        dialogMsg.dismiss()
                    }else{
                        dialogMsg.showGeneralError(onClickAction = {
                            countryObservable()
                        }, btnTxt = "Retry")
                    }
                })
            }
        }

        fun stateObservable(){
            progressBarState.show()
            viewModel.getStatesListObservable().observe(this, Observer {
                Log.v(TAG, "State $it")
                progressBarState.hide()
                if (it.responseStatus == ResponseStatus.Loaded && !it.data.isNullOrEmpty()){
                    stateList = it.data!!
                }else{
                    dialogMsg.showGeneralError(onClickAction = {
                        stateObservable()
                    }, btnTxt = "Retry")
                }
            })
        }

        countryObservable()
        stateObservable()

    }

}
