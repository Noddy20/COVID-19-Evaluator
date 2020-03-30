package com.mdm_app_covid_19.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.SubmitDataModel
import com.mdm_app_covid_19.data.models.UserModel
import com.mdm_app_covid_19.extFunctions.*
import com.mdm_app_covid_19.utils.DisposableClickListener
import com.mdm_app_covid_19.utils.SetUpToolbar
import com.mdm_app_covid_19.viewModels.LoginActivityVM
import com.mdm_app_covid_19.viewModels.MyViewModelFactory
import com.mdm_app_covid_19.viewModels.ToWhomActivityVM
import com.mdm_app_covid_19.views.adapters.MyFragmentAdapter
import com.mdm_app_covid_19.views.dialogs.DialogMsg
import com.mdm_app_covid_19.views.fragments.ToWhomFragment
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_to_whom.*
import kotlinx.android.synthetic.main.activity_to_whom.btnNext

class ToWhomActivity : BaseActivity() {

    companion object{
        private const val TAG = "ToWhomActivity"
    }

    private val viewModel: ToWhomActivityVM by viewModels { MyViewModelFactory(application) }
    private lateinit var dialogMsg: DialogMsg

    private lateinit var adapter: MyFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_whom)

        SetUpToolbar.setCollapseToolbar(this, getString(R.string.title_to_whom), false)


        if (UserModel.getSavedUserModel()?.userId?.trim().isNullOrEmpty()){
            dialogMsg.showGeneralError("Session expired!", btnTxt = "Retry", onClickAction = {
                goToLoginActivity(false)
                finishAffinity()
            })
        }else{
            init()
        }

    }

    override fun onDestroy() {
        dialogMsg.dismiss()
        super.onDestroy()
    }

    private fun init(){
        setClickListeners()

        dialogMsg = DialogMsg(this)

        SubmitDataModel.clearSubmitModel()

        adapter = MyFragmentAdapter(this, supportFragmentManager)

        adapter.addFragment(ToWhomFragment.newInstance(true), "SELF")
        adapter.addFragment(ToWhomFragment.newInstance(false), "OTHER FAMILY MEMBER")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        adapter.highLightCurrentTab(0, tabLayout)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                adapter.highLightCurrentTab(position, tabLayout)
            }
        })

    }

    private fun setClickListeners(){
        compositeDisposable += clickObservable

        btnNext.typeface = ResourcesCompat.getFont(this, R.font.font_open_sans_bold)
        clickObservable.addViewClicks(btnNext)
    }

    private val clickObservable = DisposableClickListener<Int>(){
        when(it){
            R.id.btnNext -> {
                viewModel.nextClickMLD.value = viewPager.currentItem
            }
        }
    }
}
