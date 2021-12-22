package com.justclick.clicknbook.Fragment.train

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel
import com.justclick.clicknbook.Fragment.train.viewmodel.TrainSearchViewModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import com.justclick.clicknbook.utils.enums.TrainQuotaEnum
import kotlinx.android.synthetic.main.fragment_train_search2.*
import kotlinx.android.synthetic.main.fragment_train_search2.view.*
import java.text.SimpleDateFormat
import java.util.*


class TrainSearchFragment : Fragment(), View.OnClickListener, MyTrainStationDialog.OnCityDialogResult {
    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    var viewModel:TrainSearchViewModel?=null
    private var startDateCalendar: Calendar? = null
    private var dateToSend: String? = null
    private var startDateDay = 0
    private var startDateMonth = 0
    private var startDateYear = 0
    private var dateFormat: SimpleDateFormat? = null
    private var monthFormat: SimpleDateFormat? = null
    private var dayFormat: SimpleDateFormat? = null
    private var dateToServerFormat: SimpleDateFormat? = null
    private var fromStnCode:String?=null
    private var fromStnName:String?=null
    private var toStnCode:String?=null
    private var toStnName:String?=null
    private var quota:String?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel= ViewModelProvider(this).get(TrainSearchViewModel::class.java)
        viewModel!!.getTrainSearchResponseLiveData()!!.observe(this, Observer { trainResponse ->
            // update UI
            trainSearchResponseHandler(trainResponse)
        })
        initializeDates()
    }

    private fun trainSearchResponseHandler(trainResponse: TrainSearchDataModel?) {
        if (trainResponse != null && trainResponse.trainBtwnStnsList!=null) {
//            Toast.makeText(context, trainResponse.trainBtwnStnsList!!.size.toString(),Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            trainResponse.fromStnCode=fromStnCodeTv.text.toString()
            trainResponse.fromStnName=fromStnNameTv.text.toString()
            trainResponse.toStnCode=toStnCodeTv.text.toString()
            trainResponse.toStnName=toStnNameTv.text.toString()
            trainResponse.date=departDayNameTv.text.toString()+", "+departDayTv.text.toString()+" "+departMonthTv.text.toString()
            trainResponse.doj=dateToSend
            trainResponse.quota=quota
            bundle.putSerializable("trainResponse", trainResponse)
            bundle.putString("DOJ", dateToSend)
            val trainListFragment= TrainListsFragment()
            trainListFragment.arguments=bundle
            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(trainListFragment)
        }else{
            Toast.makeText(context, "No train found for given parameters",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_train_search2, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        view.tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position==0){
                    Toast.makeText(activity,"tab click"+tab!!.position, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity,"2nd tab click"+tab!!.position, Toast.LENGTH_SHORT).show()
                }
            }

        })

        setDates(view)
        setStation(view)
        view.searchTrainsTv.setOnClickListener(this)
        view.fromStnCodeTv.setOnClickListener(this)
        view.fromStnNameTv.setOnClickListener(this)
        view.toStnNameTv.setOnClickListener(this)
        view.toStnCodeTv.setOnClickListener(this)
        view.back_arrow.setOnClickListener(this)
        view.dateConst.setOnClickListener(this)
        view.arrowImg.setOnClickListener(this)

        setQuota(view)

        return view
    }

    private fun setQuota(view: View) {
        var list = TrainQuotaEnum.values()
        var adapter= ArrayAdapter<TrainQuotaEnum>(requireContext(), R.layout.spinner_item, R.id.name_tv, list)
        view.quotaSpinner!!.adapter=adapter
    }

    private fun setStation(view: View) {
        view.fromStnCodeTv.text=fromStnCode
        view.fromStnNameTv.text=fromStnName
        view.toStnCodeTv.text=toStnCode
        view.toStnNameTv.text=toStnName
    }

    private fun initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat()
        dateFormat = Common.getDayFormat()
        monthFormat = Common.getMonthYearFormat()
        dayFormat = Common.getFullDayFormat()

        //default start Date
        startDateCalendar = Calendar.getInstance()
        startDateDay = startDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        startDateMonth = startDateCalendar!!.get(Calendar.MONTH)
        startDateYear = startDateCalendar!!.get(Calendar.YEAR)

        dateToSend = dateToServerFormat!!.format(startDateCalendar!!.getTime())
    }

    private fun setDates(view: View) {
        //set default date
        view.departDayTv!!.text = dateFormat!!.format(startDateCalendar!!.time)
        view.departMonthTv!!.text = monthFormat!!.format(startDateCalendar!!.time)
        view.departDayNameTv!!.text = dayFormat!!.format(startDateCalendar!!.time)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.searchTrainsTv->
                searchTrains()
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
            R.id.fromStnCodeTv->
                selectStn("From station",1)
            R.id.fromStnNameTv->
                selectStn("From station",1)
            R.id.toStnCodeTv->
                selectStn("To station",2)
            R.id.toStnNameTv->
                selectStn("To station",2)
            R.id.arrowImg->
                swapStn()
            R.id.dateConst->
                selectDate()
        }
    }

    private fun selectDate() {
        val datePickerDialog = DatePickerDialog(requireContext(),
                R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    startDateCalendar!![year, monthOfYear] = dayOfMonth
                    /*if (startDateCalendar!!.before(Calendar.getInstance())) {
                        Toast.makeText(context, "Can't select date before current date", Toast.LENGTH_SHORT).show()
                    } else {*/
                        startDateDay = dayOfMonth
                        startDateMonth = monthOfYear
                        startDateYear = year
                        departDayTv!!.text = dateFormat!!.format(startDateCalendar!!.time)
                        departMonthTv!!.text = monthFormat!!.format(startDateCalendar!!.time)
                        departDayNameTv!!.text = dayFormat!!.format(startDateCalendar!!.time)
                        dateToSend = dateToServerFormat!!.format(startDateCalendar!!.time)
//                    }
                }, startDateYear, startDateMonth, startDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun selectStn(msg:String, key:Int) {
//        MyStationSearchDialog().showCustomDialog(context,msg,key,this)
        MyTrainStationDialog.showCustomDialog(context,msg,key,this)
    }

    fun searchTrains(){
        if(fromStnCode==null || fromStnCode.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please select From station", Toast.LENGTH_SHORT).show()
        }else if(toStnCode==null || toStnCode.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please select destination station", Toast.LENGTH_SHORT).show()
        }else{
            var loginModel=LoginModel()
            loginModel=MyPreferences.getLoginData(loginModel,requireContext())
            var quota1=quotaSpinner.selectedItem
            var quotaPos=quotaSpinner.selectedItemPosition
            quota=TrainQuotaEnum.values().get(quotaPos).name
            viewModel!!.searchTrains(fromStnCodeTv.text.toString(), toStnCodeTv.text.toString(), dateToSend, requireContext(),
                    loginModel.Data.DoneCardUser, loginModel.Data.UserType)
        }
    }

    override fun setResult(type: Int, data: TrainStationModel.Items) {
        if(type==1){
            fromStnCode= data.station_code
            fromStnName=data.station_name
            fromStnCodeTv.text= fromStnCode
            fromStnNameTv.text=fromStnName
        }else{
            toStnCode=data.station_code
            toStnName=data.station_name
            toStnCodeTv.text=toStnCode
            toStnNameTv.text=toStnName
        }
    }

    private fun swapStn() {
        var fromStn=fromStnName
        var fromCode=fromStnCode
        fromStnName=toStnName
        fromStnCode=toStnCode
        toStnName=fromStn
        toStnCode=fromCode
        fromStnCodeTv.text=fromStnCode
        fromStnNameTv.text=fromStnName
        toStnCodeTv.text=toStnCode
        toStnNameTv.text=toStnName
        if(arrowImg.rotationX==0f){
            arrowImg.rotationX=180f
        }else{
            arrowImg.rotationX=0f
        }
    }


}
