package com.justclick.clicknbook.Fragment.train

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.adapter.TrainBookingListAdapterNew
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.Fragment.train.model.TrainListRequest
import com.justclick.clicknbook.Fragment.train.model.TrainListResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.AgentNameModel
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_booking_list_new.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TrainBookingListNewFragment : Fragment(), View.OnClickListener {

    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    var adapter: TrainBookingListAdapterNew?=null
    var loginModel: LoginModel?=null
    var arrayList:ArrayList<TrainListResponse.reservationlist>?=null
    var fragView:View?=null

//    date
private var filterDialog: Dialog? = null
    private var startDateCalendar: Calendar? = null
    private var endDateCalendar: Calendar? = null
    private var start_date_value_tv: TextView? = null
    private var end_date_value_tv: TextView? = null
    private var start_day_value_tv: TextView? = null
    private var end_day_value_tv: TextView? = null
    private var startDateDay = 0
    private var startDateMonth = 0
    private var startDateYear = 0
    private var endDateDay = 0
    private var endDateMonth = 0
    private var endDateYear = 0
    private var dateFormat: SimpleDateFormat? = null
    private var dayFormat: SimpleDateFormat? = null
    private var dateToServerFormat: SimpleDateFormat? = null
    private var startDateToSend: String? = null
    private var endDateToSend: String? = null
    private var startDateTv: TextView? = null

//    list filter
    private var pnr: String? = null
    private var resId: String? = null
    private var trainNo: String? = null
    private var sourceCode: String? = null
    private var destCode: String? = null
    private var agentcodeFilter: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, requireContext())
        toolBarHideFromFragmentListener=requireContext() as ToolBarHideFromFragmentListener
        initializeDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(fragView==null){
            val view = inflater.inflate(R.layout.fragment_train_booking_list_new, container, false)
            fragView=view
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            arrayList= ArrayList()

            var layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(requireContext())
                else -> GridLayoutManager(requireContext(), columnCount)
            }

            adapter = TrainBookingListAdapterNew(arrayList, object : OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(item: ArrayList<TrainListResponse.reservationlist>?, id: Int, listPosition: Int) {
                    if(id==R.id.changeBoarding){
                        changeBoardingStn(item!!.get(listPosition))
                    }else if(id==R.id.cancelTicket){
                        cancelTicket(item!!.get(listPosition))
                    }else{
                        getBookingData(item!!.get(listPosition).reservationID)
                    }
                }
            })

            view.recycleView.layoutManager=layoutManager
            view.recycleView.adapter=adapter
            view.lin_dateFilter.setOnClickListener(this)
            view.linFilter.setOnClickListener(this)
            view.back_arrow.setOnClickListener(this)
            startDateTv=view.startDateTv
            setDates()
            callBookingList()
        }

        return fragView
    }

    private fun initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat()
        dateFormat = Common.getToFromDateFormat()
        dayFormat = Common.getShortDayFormat()

        //default start Date
        startDateCalendar = Calendar.getInstance()
        startDateDay = startDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        startDateMonth = startDateCalendar!!.get(Calendar.MONTH)
        startDateYear = startDateCalendar!!.get(Calendar.YEAR)


        //default end Date
        endDateCalendar = Calendar.getInstance()
        endDateDay = endDateCalendar!!.get(Calendar.DAY_OF_MONTH)
        endDateMonth = endDateCalendar!!.get(Calendar.MONTH)
        endDateYear = endDateCalendar!!.get(Calendar.YEAR)
        startDateToSend = dateToServerFormat!!.format(startDateCalendar!!.getTime())
        endDateToSend = dateToServerFormat!!.format(endDateCalendar!!.getTime())
    }

    private fun setDates() {
        //set default date
        startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                dayFormat!!.format(endDateCalendar!!.time) + " " +
                dateFormat!!.format(endDateCalendar!!.time)
    }

    private fun changeBoardingStn(list: TrainListResponse.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getBoardingStnForChange(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/BoardingStation?Trainno="
                +list!!.trainNumber+"&Date="+journeyDate(list.departDate)+"&fromStation="+
                list.source+ "&toStation="+list.destination+"&className="+list.journeyClass,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        responseString=responseString.replace("boardingStationList\":{", "boardingStationList\":[{")
                        responseString=responseString.replace("},\"mealChoiceenable", "}],\"mealChoiceenable")
                        val boardingStnResponse = Gson().fromJson(responseString, TrainBookFragment.BoardingStnResponse::class.java)
                        if(boardingStnResponse!=null){
                            if(boardingStnResponse.boardingStationList!=null && boardingStnResponse.boardingStationList!!.size>0){

                                var arr: Array<String?> =arrayOfNulls<String>(boardingStnResponse.boardingStationList!!.size)
                                for(pos in boardingStnResponse.boardingStationList!!.indices){
                                    arr[pos]=boardingStnResponse.boardingStationList!!.get(pos).stnNameCode
                                }

//                                val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
                                val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
                                (requireContext() as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)

                            }else{
                                Toast.makeText(requireContext(), boardingStnResponse.errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cancelTicket(list: TrainListResponse.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelDetail?TransactionId="
                +list!!.reservationID,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        val response = Gson().fromJson(responseString, TrainCancelTicketDetailResponse::class.java)
                        if(response.statusCode.equals("00")){
//                            Toast.makeText(requireContext(),response.statusMessage, Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putSerializable("cancelResponse", response)
                            val fragment = TrainCancelDetailsFragment()
                            fragment.arguments = bundle
                            (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                        }else{
                            Toast.makeText(requireContext(),response.statusMessage, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getStnCode(station: String?): String? {
        try {
            return station!!.substring(station.indexOf("(")+1, station.indexOf(")"))
        }catch (e: Exception){}
        return ""
    }

    private fun journeyDate(departDate: String?): String? {
        if(departDate!!.contains("/")){
            return Common.getServerDateFormat().format(SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(departDate))
        }else{
            return Common.getServerDateFormat().format(SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(departDate))
        }
    }

    private fun callBookingList() {
        var request= TrainListRequest()
        request.usertype=loginModel!!.Data.UserType
//        request.usertype="OOU"
        request.bookuserid=loginModel!!.Data.UserId
//        request.bookuserid="px‚ju}{j\u007Fnu|k}rIpvjru7lxv"
        request.fromdate=startDateToSend
        request.todate=endDateToSend
//        filters
        request.pnr=pnr
        request.reservationid=resId
        request.trainnumber=trainNo
        request.trainsource=sourceCode
        request.traindestination=destCode
        request.agentcode=agentcodeFilter

        var json=Gson().toJson(request)

        NetworkCall().callService(NetworkCall.getTrainApiInterface().getTrainList(ApiConstants.getrailbookinglist, request),
            context,true)
        { response: ResponseBody?, responseCode: Int ->

            if (response != null) {
                responseHandlerList(response, request) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerList(response: ResponseBody, request: TrainListRequest) {
        try{
            val response = Gson().fromJson(response.string(), TrainListResponse::class.java)
            if(response!=null){
                /*if(response.statusCode.equals("00")){
                    Toast.makeText(context, response.statusMessage, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, response.statusMessage, Toast.LENGTH_LONG).show()
                }*/
                if (response != null ) {
                    if(response.statusCode.equals("00")){
                        if(response!!.reservationlist!=null && response!!.reservationlist!!.size>0){
                            arrayList!!.addAll(response!!.reservationlist)
                            adapter!!.notifyDataSetChanged()
//                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), "No booking found for these parameters", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), response!!.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilterDialog() {
        filterDialog = Dialog(requireContext())
        filterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog!!.setContentView(R.layout.filter_dialog_layout)
        start_date_value_tv = filterDialog!!.findViewById(R.id.start_date_value_tv)
        start_day_value_tv = filterDialog!!.findViewById(R.id.start_day_value_tv)
        end_date_value_tv = filterDialog!!.findViewById(R.id.end_date_value_tv)
        end_day_value_tv = filterDialog!!.findViewById(R.id.end_day_value_tv)
        filterDialog!!.findViewById<View>(R.id.cancel_tv).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.submit_tv).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.startDateLinear).setOnClickListener(this)
        filterDialog!!.findViewById<View>(R.id.endDateLinear).setOnClickListener(this)
        start_date_value_tv!!.setText(dateFormat!!.format(startDateCalendar!!.time))
        start_day_value_tv!!.setText(dayFormat!!.format(startDateCalendar!!.time))
        end_date_value_tv!!.setText(dateFormat!!.format(endDateCalendar!!.time))
        end_day_value_tv!!.setText(dayFormat!!.format(endDateCalendar!!.time))

        filterDialog!!.show()
    }


    private fun getBookingData(reservationID: String) {
        NetworkCall().callTrainServiceFinalGet(ApiConstants.GetPnrDetails, reservationID, "", "",
                requireContext(), loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody?, i: Int) {
        try {
            if (response != null) {
                val responseModel: PnrResponse = Gson().fromJson<PnrResponse>(response.string(), PnrResponse::class.java)
                if (responseModel != null) {
                    if (responseModel.statusCode == "00") {
                        val bundle = Bundle()
                        bundle.putSerializable("trainResponse", responseModel)
                        val fragment = TrainBookingDetailsFragment()
                        fragment.arguments = bundle
                        (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                    } else {
                        Toast.makeText(requireContext(), responseModel.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainListResponse.reservationlist>?, id: Int, listPosition: Int)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
            R.id.startDateLinear -> try {
                openStartDatePicker()
            } catch (e: Exception) {
            }
            R.id.endDateLinear -> try {
                openEndDatePicker()
            } catch (e: Exception) {
            }
            R.id.lin_dateFilter -> openFilterDialog()
            R.id.linFilter -> {
                if(listFilterDialog==null || (agentArray.size==0 && !loginModel!!.Data.UserType.equals("A"))){
                    openListFilterDialog()
                }else{
                    listFilterDialog!!.show()
                }
            }
            R.id.cancel_tv -> if (filterDialog != null) filterDialog!!.dismiss()
            R.id.submit_tv -> {
                val diff = endDateCalendar!!.time.time - startDateCalendar!!.time.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                if (days < 0) {
                    Toast.makeText(context, "you have selected wrong dates", Toast.LENGTH_SHORT).show()
                    return
                }
                if (days >= 15) {
                    Toast.makeText(context, "please select 15 days data", Toast.LENGTH_SHORT).show()
                    return
                }
                filterDialog!!.dismiss()
                arrayList!!.clear()
                adapter!!.notifyDataSetChanged()
                //set date to fragment
                startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                        dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                        dayFormat!!.format(endDateCalendar!!.time) + " " +
                        dateFormat!!.format(endDateCalendar!!.time)

//                fromDate=startDateToSend
//                toDate=endDateToSend

                callBookingList()
            }
        }
    }

    private var listFilterDialog:Dialog?=null
    private fun openListFilterDialog() {
//        listFilterDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        listFilterDialog = Dialog(requireContext())
        listFilterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        listFilterDialog!!.setContentView(R.layout.train_booking_list_filter)
        val pnrEdt = listFilterDialog!!.findViewById<EditText>(R.id.pnrEdt)
        val resIdEdt = listFilterDialog!!.findViewById<EditText>(R.id.resIdEdt)
        val trainNoEdt = listFilterDialog!!.findViewById<TextView>(R.id.trainNoEdt)
        val sourceEdt = listFilterDialog!!.findViewById<EditText>(R.id.sourceEdt)
        val destinationEdt = listFilterDialog!!.findViewById<EditText>(R.id.destinationEdt)
        val agentLabelTv = listFilterDialog!!.findViewById<TextView>(R.id.agentLabelTv)
        val agent_auto = listFilterDialog!!.findViewById<AutoCompleteTextView>(R.id.agent_auto)

        listFilterDialog!!.findViewById<View>(R.id.back_arrow).setOnClickListener { listFilterDialog!!.dismiss() }
        listFilterDialog!!.findViewById<View>(R.id.resetTv).setOnClickListener {
            pnrEdt.setText("")
            resIdEdt.setText("")
            trainNoEdt.setText("")
            sourceEdt.setText("")
            destinationEdt.setText("")
            agent_auto.setText("")
        }

        agent_auto.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length>1) {
                    getDistributorAgents(agent_auto, s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        if(loginModel!!.Data.UserType.equals("A")){
            agentLabelTv.visibility=View.GONE
            agent_auto.visibility=View.GONE
        }else{
            agentLabelTv.visibility=View.VISIBLE
            agent_auto.visibility=View.VISIBLE
        }

        listFilterDialog!!.findViewById<View>(R.id.applyTv).setOnClickListener {
            listFilterDialog!!.dismiss()
            pnr=pnrEdt.text.toString()
            resId=resIdEdt.text.toString()
            trainNo=trainNoEdt.text.toString()
            sourceCode=sourceEdt.text.toString()
            destCode=destinationEdt.text.toString()
            var agent=agent_auto.text.toString()
            if(agent.contains("(") && agent.contains(")")) {
                agentcodeFilter = agent.substring(agent.indexOf("( ") + 1, agent.indexOf(" )")).trim()
            }else{
                agentcodeFilter=""
            }
            applyFilter()
        }

        val window = listFilterDialog!!.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        listFilterDialog!!.show()
    }

    private fun applyFilter() {
        arrayList!!.clear()
        adapter!!.notifyDataSetChanged()
        //set date to fragment
        /*startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                dayFormat!!.format(endDateCalendar!!.time) + " " +
                dateFormat!!.format(endDateCalendar!!.time)*/
        callBookingList()
    }

    private fun getDistributorAgents(agent_auto: AutoCompleteTextView, agencyName: String) {
        val model = AgentNameRequestModel()
//        model.AgencyName = ""
//        model.DeviceId = Common.getDeviceId(context)
//        model.DoneCardUser = loginModel!!.Data.DoneCardUser
//        model.LoginSessionId = EncryptionDecryptionClass.EncryptSessionId(
//            EncryptionDecryptionClass.Decryption(loginModel!!.LoginSessionId, context), context
//        )
        model.Type = loginModel!!.Data.UserType
        model.RequiredType = loginModel!!.Data.UserType
        model.AgencyName = agencyName
        model.MerchantID = loginModel!!.Data.MerchantID
        model.RefAgency = loginModel!!.Data.RefAgency
        model.DeviceId = Common.getDeviceId(context)
        model.DoneCardUser = loginModel!!.Data.DoneCardUser
        model.LoginSessionId = EncryptionDecryptionClass.EncryptSessionId(
            EncryptionDecryptionClass.Decryption(loginModel!!.LoginSessionId, context), context
        )

        val json = Gson().toJson(model)

        val apiService = APIClient.getClient().create(ApiInterface::class.java)
        val call = apiService.agentNamePostNew(ApiConstants.GetAgentName, model)
        NetworkCall().callService(call,context,false
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerAgent(response, agent_auto)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    var agentArray: java.util.ArrayList<AgentNameModel.AgentName> = java.util.ArrayList()
    private fun responseHandlerAgent(response: ResponseBody, agent_auto: AutoCompleteTextView) {
        try {
            val commonResponse = Gson().fromJson(response.string(), AgentNameModel::class.java)
            if (commonResponse != null) {
                if (commonResponse.StatusCode.equals("0", ignoreCase = true)) {
//                    listAdapter!!.notifyDataSetChanged()
                    if (commonResponse.Data != null &&
                        commonResponse.Data.size > 0) {
//                        Toast.makeText(context, commonResponse.Status, Toast.LENGTH_LONG).show()
                        val arr = arrayOfNulls<String>(commonResponse.Data.size)
                        agentArray.clear()
                        agentArray.addAll(commonResponse.Data)
                        for (p in commonResponse.Data.indices) {
                            arr[p] = commonResponse.Data.get(p).AgencyName.replace("(","( ").
                            replace(")"," )")
                        }

                        agent_auto.setAdapter<ArrayAdapter<String>>(getSpinnerAdapter(arr))
                        agent_auto.showDropDown()
                    }else{
//                        Toast.makeText(context, "No agent found.", Toast.LENGTH_LONG).show()
                    }
                }else {
//                    Toast.makeText(context, commonResponse.Status, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Agents are enable to fetch", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Agents are enable to fetch", Toast.LENGTH_LONG).show()
        }
    }

    private fun getSpinnerAdapter(arr: Array<String?>): ArrayAdapter<String>? {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.mobile_operator_spinner_item, R.id.operator_tv, arr
        )
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)

        return adapter
    }

    private fun openStartDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                startDateCalendar!![year, monthOfYear] = dayOfMonth
                if (startDateCalendar!!.after(Calendar.getInstance())) {
                    Toast.makeText(
                        context,
                        "Can't select date after current date",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startDateDay = dayOfMonth
                    startDateMonth = monthOfYear
                    startDateYear = year
                    start_date_value_tv!!.text = dateFormat!!.format(startDateCalendar!!.time)
                    start_day_value_tv!!.text = dayFormat!!.format(startDateCalendar!!.time)
                    startDateToSend = dateToServerFormat!!.format(startDateCalendar!!.time)
                }
            }, startDateYear, startDateMonth, startDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val cal = Calendar.getInstance()
        //        cal.add(Calendar.DAY_OF_MONTH, -15);
//        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun openEndDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                endDateCalendar = Calendar.getInstance()
                endDateCalendar!!.set(year, monthOfYear, dayOfMonth)
                if (endDateCalendar!!.after(Calendar.getInstance())) {
                    Toast.makeText(
                        context,
                        "Can't select date after current date",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    endDateDay = dayOfMonth
                    endDateMonth = monthOfYear
                    endDateYear = year
                    end_date_value_tv!!.text = dateFormat!!.format(endDateCalendar!!.getTime())
                    end_day_value_tv!!.text = dayFormat!!.format(endDateCalendar!!.getTime())
                    endDateToSend = dateToServerFormat!!.format(endDateCalendar!!.getTime())
                }
            }, endDateYear, endDateMonth, endDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(requireContext(), resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

}
