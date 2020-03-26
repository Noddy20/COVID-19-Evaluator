package com.mdm_app_covid_19.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.mdm_app_covid_19.R
import kotlinx.android.synthetic.main.row_fragment_pager_custom_tab.view.*

class MyFragmentAdapter(val context: Context, fragmentManager: FragmentManager):
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
        //return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getTabView(position: Int): View {
        val mView = LayoutInflater.from(context).inflate(R.layout.row_fragment_pager_custom_tab, null)
        mView.customTabTxt.text = mFragmentTitleList[position]
        //mView.customTabTxt.typeface = CustomFont.mediumFont(context)
        mView.customTabTxt.setTextColor(ContextCompat.getColor(context, R.color.colorTextDim))
        return mView
    }
    fun getSelectedTabView(position: Int): View {
        val mView = LayoutInflater.from(context).inflate(R.layout.row_fragment_pager_custom_tab, null)
        mView.customTabTxt.text = mFragmentTitleList[position]
       // mView.customTabTxt.typeface = CustomFont.boldFont(context)
        mView.customTabTxt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        return mView
    }

    fun highLightCurrentTab(position: Int, tabLayout: TabLayout) {
        repeat(tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(it)
            tab?.customView = null
            tab?.customView = getTabView(it)
        }
        val tab = tabLayout.getTabAt(position)
        tab?.customView = null
        tab?.customView = getSelectedTabView(position)
    }

}