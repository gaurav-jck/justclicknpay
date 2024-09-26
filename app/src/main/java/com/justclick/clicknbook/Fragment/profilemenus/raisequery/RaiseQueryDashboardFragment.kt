package com.justclick.clicknbook.Fragment.profilemenus.raisequery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.Fragment.profilemenus.SupportQueryFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener

class RaiseQueryDashboardFragment : Fragment(), View.OnClickListener {
    var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_raise_query_dashboard, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.raiseQuery).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.getTicket).setOnClickListener(this)

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RaiseQueryDashboardFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->{
                parentFragmentManager.popBackStack()
            }
            R.id.raiseQuery->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                    RaiseQueryFragment()
                )
            }
            R.id.getTicket->{
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                    QueryListFragment()
                )
            }
        }
    }
}