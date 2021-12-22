package com.justclick.clicknbook.Fragment.train

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.Fragment.train.adapter.TrainListAdapter
import com.justclick.clicknbook.Fragment.train.adapter.TrainRouteAdapter
import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.TrainRouteModel
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.Fragment.train.viewmodel.TrainSearchViewModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_list.view.*
import kotlinx.android.synthetic.main.train_route_dialog.*
import kotlinx.android.synthetic.main.train_route_dialog.back_arrow


class TrainListsFragment : Fragment(), View.OnClickListener {

    // TODO: Customize parameters
    private var columnCount = 1
    public val General="Genaral Quota"
    val Ladies="Ladies Quota"
    public val SrCitizen="Sr. Citizen"
    public val Tatkal="Tatkal Quota"
    public val Handicapped="Handicapped"
    private var listener: OnListFragmentInteractionListener? = null
    var viewModel:TrainSearchViewModel?=null
    var adapter: TrainListAdapter?=null
    var doj:String?=null
    var trainNumber:String?=null
    var trainName:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(this).get(TrainSearchViewModel::class.java)
        viewModel!!.getTrainRouteResponseLiveData()!!.observe(this, Observer { trainResponse ->
            // update UI
            trainRouteResponseHandler(trainResponse)
        })
    }

    private fun trainRouteResponseHandler(trainResponse: TrainRouteModel?) {
        if(trainResponse!=null){
            if(trainResponse.stationList!=null && trainResponse.stationList!!.size>0){
                openTrainRouteDialog(trainResponse)
            }
        }else{

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_train_list, container, false)

        var arrayList:ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?= ArrayList()
        var trainResponse:TrainSearchDataModel?=null

        var layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("trainResponse") as TrainSearchDataModel
            arrayList = trainResponse.trainBtwnStnsList
            doj = requireArguments().getString("DOJ")

            //top title
            view.fromStationTv.text=trainResponse.fromStnName
            view.toStationTv.text=trainResponse.toStnName
            view.dateTv.text=trainResponse.date

            var list = ArrayList<String>()
            if(trainResponse.quotaList!=null && trainResponse!!.quotaList!!.size>0){
                for(i in trainResponse!!.quotaList!!.indices){
                    if(getQuota(trainResponse!!.quotaList!!.get(i)).isNotEmpty()){
                        list.add(getQuota(trainResponse!!.quotaList!!.get(i)))
                    }
                }
            }else{
                list.add(General)
            }
            if(list.indexOf(General)!=0){
                var temp=list.get(0)
                list[list.indexOf(General)]=temp
                list[0]=General

            }
            var adapter= ArrayAdapter(requireContext(), R.layout.spinner_item, R.id.name_tv, list)
//                var adapter= ArrayAdapter(requireContext(), R.layout.spinner_item, R.id.name_tv, trainResponse!!.quotaList!!)
            view.quotaSpinner!!.adapter=adapter

        }

        /*view.quotaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                quotaValue=parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/


        adapter = TrainListAdapter(doj,arrayList, view.quotaSpinner, object : OnListFragmentInteractionListener {
            override fun onListFragmentInteraction(list: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?, id: Int, listPosition: Int, type: CodeEnum, fareRuleResponse: FareRuleResponse?) {
                if(type.equals(CodeEnum.TrainDetail)){
                    var trainBookFragment=TrainBookFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("trainResponse", trainResponse)
                    bundle.putSerializable("fareRuleResponse", fareRuleResponse)
                    bundle.putString("DOJ", doj)
                    bundle.putInt("Position", listPosition)
                    trainBookFragment.arguments=bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(trainBookFragment)
                }else{
                    trainName= list!![listPosition].trainName
                    trainNumber= list!![listPosition].trainNumber
                    var loginModel= LoginModel()
                    loginModel= MyPreferences.getLoginData(loginModel,requireContext())
                    viewModel!!.findTrainRoute(doj, list!![listPosition].trainNumber, list!![listPosition].fromStnCode,context!!,
                            loginModel.Data.DoneCardUser, loginModel.Data.UserType)
                }
            }
        })

        view.recycleView.layoutManager=layoutManager
        view.recycleView.adapter=adapter

        view.back_arrow.setOnClickListener(this)

        /*viewModel!!.getTrainSearchResponseLiveData()!!.observe(viewLifecycleOwner, Observer { trainResponse ->
            // update UI
            trainSearchResponseHandler(trainResponse)
        })*/

        return view
    }

    private fun getQuota(value: String): String {
        if(value.equals("GN")){
            return General
        }else if(value.equals("LD")){
            return Ladies
        }else if(value.equals("TQ")){
            return Tatkal
        }else if(value.equals("HP")){
            return Handicapped
        }else if(value.equals("SS")){
            return SrCitizen
        }else{
            return ""
        }
    }

    private fun openTrainRouteDialog(trainResponse: TrainRouteModel) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.train_route_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.titleTv.text= "Train Route-"+trainNumber+"\n"+trainName
        dialog.routeRecycler.layoutManager=LinearLayoutManager(context)
        dialog.routeRecycler.adapter= TrainRouteAdapter(trainResponse.stationList,object : OnRouteListListener{
            override fun onListFragmentInteraction(item: ArrayList<TrainRouteModel.StationList>?, id: Int, listPosition: Int, type: CodeEnum) {
            }
        })

        dialog.back_arrow.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainSearchDataModel.TrainBtwnStnsList>?, id: Int, listPosition: Int, type: CodeEnum, fareRuleResponse: FareRuleResponse?)
    }
    interface OnRouteListListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainRouteModel.StationList>?, id: Int, listPosition: Int, type: CodeEnum)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
        }
    }

}
