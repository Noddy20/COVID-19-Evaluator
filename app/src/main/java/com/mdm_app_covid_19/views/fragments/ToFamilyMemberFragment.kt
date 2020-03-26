package com.mdm_app_covid_19.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.views.activities.ToWhomActivity
import io.reactivex.disposables.CompositeDisposable

class ToFamilyMemberFragment : Fragment(){

    companion object {
        private const val TAG = "ToFamilyMemberFrag"

        fun newInstance(): ToFamilyMemberFragment {
            return ToFamilyMemberFragment().also { it.arguments = bundleOf() }
        }
    }

    private lateinit var rootView: View
    private lateinit var mActivity: ToWhomActivity


    private val compositeDisposable by lazy { CompositeDisposable() }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity= context as ToWhomActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_to_family_member, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        init()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun init() {

    }

}