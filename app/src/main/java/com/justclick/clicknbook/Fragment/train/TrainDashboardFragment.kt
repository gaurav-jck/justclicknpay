package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.R
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener

class TrainDashboardFragment : Fragment(), View.OnClickListener {
    var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_train_dashboard, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.searchTrain).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.searchPNR).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.pnrStatus).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.bookingList).setOnClickListener(this)

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                TrainDashboardFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->{
                parentFragmentManager.popBackStack()
            }
            R.id.searchTrain->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(TrainSearchFragment())
            }
            R.id.searchPNR->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(TrainPnrSearchFragment())
            }
            R.id.pnrStatus->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(TrainPnrStatusFragment())
            }
            R.id.bookingList->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(TrainBookingListFragment())
            }
        }
    }
}