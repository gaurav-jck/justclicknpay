package com.justclick.clicknbook.jctPayment.Fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.adapter.AutocompleteAdapter
import com.justclick.clicknbook.jctPayment.Adapters.AepsListAdapter
import com.justclick.clicknbook.jctPayment.Models.AepsListResponseModel
import com.justclick.clicknbook.jctPayment.Utilities.URLs
import com.justclick.clicknbook.model.AgentNameModel
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.model.RblPrintResponse
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.rapipayMatm.RecyclerTransactionList
import com.justclick.clicknbook.rapipayMatm.TxnListRequestModel
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.EncryptionDecryptionClass
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.activity_txn_list.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TransactionListFragment : Fragment(), View.OnClickListener {
    private val START_DATE = 1
    private val END_DATE = 2
    private val REFUND_OTP_REQUEST = 1
    private val REFUND = 2
    private val PRINT = 1
    private val CALL_AGENT = 2
    private val STATUS_CHECK = 3
    private val SHOW_PROGRESS = true
    private val NO_PROGRESS = false
    private var recyclerView: RecyclerView? = null
    private var startDateToSend: String? = null
    private var endDateToSend: String? = null
    private var arrayList: ArrayList<AepsListResponseModel.transactionListDetail>? = null
    private var listAdapter: AepsListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var startDateTv: TextView? = null
    private var noRecordTv: TextView? = null
    private var filterDialog: Dialog? = null
    private var refundDialog: Dialog? = null
    private var startDateCalendar: Calendar?=null
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
    private var pageStart = 1
    private var pageEnd = 10
    private var totalPageCount = 0
    private var transactionListRequestModel: TxnListRequestModel? = null
    private var loginModel: LoginModel? = null
    private var otp_edt: EditText? = null
    private var txnId = ""
    private var txnStatus = ""
    private var agentName = ""
    private var agentDoneCard = ""
    private var CustMobile = ""
    private var txnStatusPosition = 0
    private var autocompleteAdapter: AutocompleteAdapter? = null
    private var agentNameModel: AgentNameModel? = null
    private var list_agent: ListView? = null
    private var statusPosition=0
    private var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayList = ArrayList()
        transactionListRequestModel = TxnListRequestModel()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        agentNameModel = AgentNameModel()
        initializeDates()
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

    fun setDate(a : Int){

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transactions_, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        view.noRecordTv.setVisibility(View.GONE)
        startDateTv = view.findViewById(R.id.startDateTv)
        noRecordTv = view.findViewById(R.id.noRecordTv)
        noRecordTv!!.setVisibility(View.GONE)

        view.findViewById<View>(R.id.lin_dateFilter).setOnClickListener(this)
        view.findViewById<View>(R.id.linFilter).setOnClickListener(this)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        //        view.findViewById(R.id.lin_sortList).setOnClickListener(this);

        //initialize date values
        setDates()

//        addValueToModel();
        listAdapter = AepsListAdapter(context, AepsListAdapter.OnRecyclerItemClickListener { view, list, data, position ->
            when (view.id) {
                R.id.print_tv -> try {
//                    Toast.makeText(context, "print", Toast.LENGTH_LONG).show()
//                    openPrintDialog(list[position])
                    openReceipt(data)
                } catch (e: Exception) {
                    Toast.makeText(context, "Enable to print data", Toast.LENGTH_SHORT).show()
                }
                R.id.statusTv->{
                    statusPosition=position
                    val statusCheck=StatusCheck()
                    statusCheck.setTransactionId(data.jckTransactionId)
                    statusCheck.setLoggedinAgentCode(loginModel!!.Data.DoneCardUser)
                    statusCheck.setAgentCode(data.agentCode)
                    getStatus(statusCheck)
                }

            }
        }, arrayList)
        layoutManager = LinearLayoutManager(context)
        view.recyclerView.setLayoutManager(layoutManager)
        view.recyclerView.setAdapter(listAdapter)
        /*if (arrayList != null && arrayList!!.size == 0) {
            getTransactions();
        } else {
            listAdapter!!.notifyDataSetChanged()
        }*/
        view.recyclerView.addOnScrollListener(recyclerViewOnScrollListener)
        return view
    }

    private fun getTransactions() {
        pageStart = 1
        pageEnd = 10
        transactionListRequestModel!!.setRowEnd(pageEnd.toString())
        transactionListRequestModel!!.setRowStart(pageStart.toString())
        transactionListRequestModel!!.setFromdate(startDateToSend + "")
        transactionListRequestModel!!.setTodate(endDateToSend + "")
        if (Common.checkInternetConnection(context)) {
            callAgent(transactionListRequestModel, SHOW_PROGRESS)
        } else {
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getStatus(statusCheck: StatusCheck) {
        MyCustomDialog.showCustomDialog(context,"Getting status...")
        NetworkCall().callAepsServiceNew(statusCheck, URLs.StatusEnquiry, context)
         { response, responseCode ->
            if (response != null) {
                responseHandler(response, STATUS_CHECK)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
            hideCustomDialog()
        }
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
        when (TYPE) {
            CALL_AGENT -> try {
                val commonResponse = Gson().fromJson(response.string(), AepsListResponseModel::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        noRecordTv!!.visibility = View.GONE
                        arrayList!!.addAll(commonResponse.transactionListDetail!!)
                        totalPageCount = commonResponse.totalCount
                        listAdapter!!.setCount(totalPageCount)
                        listAdapter!!.notifyDataSetChanged()
                        if (commonResponse.transactionListDetail != null &&
                                commonResponse.transactionListDetail!!.size == 0) {
                            noRecordTv!!.visibility = View.VISIBLE
                        }
                    } else if (commonResponse.statusCode.equals("2", ignoreCase = true)) {
                        arrayList!!.clear()
                        listAdapter!!.notifyDataSetChanged()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    } else {
                        noRecordTv!!.visibility = View.VISIBLE
                        arrayList!!.clear()
                        listAdapter!!.notifyDataSetChanged()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                } else {
                    noRecordTv!!.visibility = View.VISIBLE
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                noRecordTv!!.visibility = View.VISIBLE
                e.printStackTrace()
            }
            PRINT -> try {
                val commonResponse = Gson().fromJson(response.string(), RblPrintResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.StatusCode.equals("0", ignoreCase = true)) {
//                            openPrintDialog(commonResponse);
                    } else {
                        Toast.makeText(context, commonResponse.Status, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            STATUS_CHECK->try {
                val commonResponse = Gson().fromJson(response.string(), StatusCheckResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.getStatusCode().equals("00", ignoreCase = true)) {
                        arrayList!!.get(statusPosition).apiStatusCode="200"
                        arrayList!!.get(statusPosition).txnStatusDesc="Success"
                        listAdapter!!.notifyItemChanged(statusPosition)
                        Toast.makeText(context, commonResponse.getStatusMessage(), Toast.LENGTH_LONG).show()
                    } else if(commonResponse.getStatusCode().equals("02", ignoreCase = true)) {
                        arrayList!!.get(statusPosition).txnStatusDesc="Pending"
                        listAdapter!!.notifyItemChanged(statusPosition)
                        Toast.makeText(context, commonResponse.getStatusMessage(), Toast.LENGTH_LONG).show()
                    }else if(commonResponse.getStatusCode().equals("04", ignoreCase = true)) {
                        arrayList!!.get(statusPosition).txnStatusDesc="Failed"
                        listAdapter!!.notifyItemChanged(statusPosition)
                        Toast.makeText(context, commonResponse.getStatusMessage(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, commonResponse.getStatusMessage(), Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
            }
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

    fun call_agent(model: AgentNameRequestModel?): AgentNameModel? {
//        agent.DATA.clear()
        val apiService = APIClient.getClient().create(ApiInterface::class.java)
        val call = apiService.agentNamePost(ApiConstants.GetAgentName, model)
        call.enqueue(object : Callback<AgentNameModel?> {
            override fun onResponse(call: Call<AgentNameModel?>, response: Response<AgentNameModel?>) {
                try {
                    agentNameModel = response.body()
                    if (agentNameModel!!.StatusCode.equals("0", ignoreCase = true)) {
                        autocompleteAdapter = AutocompleteAdapter(context, agentNameModel)
                        list_agent!!.adapter = autocompleteAdapter
                        list_agent!!.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                }
            }

            override fun onFailure(call: Call<AgentNameModel?>, t: Throwable) {
                val a = 0
                //                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        })
        return agentNameModel
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount = layoutManager.getItemCount();
//            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            val visibleItemCount = recyclerView.childCount
            val totalItemCount = listAdapter!!.itemCount
            val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()

//            if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount <= totalPageCount && dy > 0) {
                if (pageEnd <= totalItemCount) {
                    pageStart = pageStart + 10
                    pageEnd = pageEnd + 10
                    transactionListRequestModel!!.setRowEnd(pageEnd.toString())
                    transactionListRequestModel!!.setRowStart(pageStart.toString())
                    transactionListRequestModel!!.setFromdate(startDateToSend + "")
                    transactionListRequestModel!!.setTodate(endDateToSend + "")
                    callAgent(transactionListRequestModel, NO_PROGRESS)
                }
            }
        }
    }

    fun callAgent(transactionListRequestModel: TxnListRequestModel?, progress: Boolean) {
        transactionListRequestModel!!.setUserType(loginModel!!.Data.UserType)
//        transactionListRequestModel!!.setUserType("OOU")
        if(loginModel!!.Data.UserType.equals("A") || loginModel!!.Data.UserType.equals("D")){
            transactionListRequestModel!!.setAgentCode(loginModel!!.Data.DoneCardUser)
        }else{
            transactionListRequestModel!!.setAgentCode("")
        }
        if (progress && !MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        NetworkCall().callAepsServiceNew(transactionListRequestModel, URLs.TransactionListAeps, context)
         { response, responseCode ->
            if (response != null) {
                responseHandler(response, CALL_AGENT)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
            hideCustomDialog()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
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
                listAdapter!!.notifyDataSetChanged()
                //set date to fragment
                startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                        dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                        dayFormat!!.format(endDateCalendar!!.time) + " " +
                        dateFormat!!.format(endDateCalendar!!.time)
                pageStart = 1
                pageEnd = 10
                transactionListRequestModel!!.setRowEnd(pageEnd.toString())
                transactionListRequestModel!!.setRowStart(pageStart.toString())
                transactionListRequestModel!!.setFromdate(startDateToSend + "")
                transactionListRequestModel!!.setTodate(endDateToSend + "")
                if (Common.checkInternetConnection(context)) {
                    callAgent(transactionListRequestModel, SHOW_PROGRESS)
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show()
                }
            }
            R.id.startDateLinear -> try {
                openStartDatePicker()
            } catch (e: Exception) {
            }
            R.id.endDateLinear -> try {
                openEndDatePicker()
            } catch (e: Exception) {
            }
            R.id.lin_dateFilter -> openFilterDialog()
            R.id.linFilter -> openListFilterDialog()
            R.id.back_arrow -> parentFragmentManager.popBackStack()
        }
    }

    private fun openListFilterDialog() {
        txnId = ""
        txnStatus = ""
        agentDoneCard = ""
        agentName = ""
        CustMobile = ""
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.jct_txn_list_filter)
        val statusSpinner = dialog.findViewById<Spinner>(R.id.statusSpinner)
        val txnEdt = dialog.findViewById<EditText>(R.id.txnEdt)
        val mobileEdt = dialog.findViewById<EditText>(R.id.mobileEdt)
        val agent_search_edt = dialog.findViewById<EditText>(R.id.agent_search_edt)
        list_agent = dialog.findViewById(R.id.list_agent)
        //        final RelativeLayout agent_search_rel = (RelativeLayout) dialog.findViewById(R.id.agent_search_rel);
//        final EditText agentEdt= dialog.findViewById(R.id.agentEdt);
        if (loginModel!!.Data.UserType.equals("A", ignoreCase = true)) {
            agent_search_edt.visibility = View.GONE
            dialog.findViewById<View>(R.id.agentLabelTv).visibility = View.GONE
        }
        val adapter = ArrayAdapter(requireContext(),
                R.layout.agent_details_spinner_item_dropdown, R.id.operator_tv, resources.getStringArray(R.array.jct_list_array))
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown)
        statusSpinner.adapter = adapter
        statusSpinner.setSelection(txnStatusPosition)
        statusSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position == 0) {
                    txnStatus = ""
                    txnStatusPosition = 0
                } else {
                    txnStatus = statusSpinner.selectedItem.toString()
                    txnStatusPosition = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        dialog.findViewById<View>(R.id.cancelTv).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.applyTv).setOnClickListener {
            dialog.dismiss()
            txnId = txnEdt.text.toString().trim { it <= ' ' }
            CustMobile = mobileEdt.text.toString().trim { it <= ' ' }
            applyFilter()
        }
        if (agentNameModel != null && agentNameModel!!.Data != null && agentNameModel!!.Data.size > 0) {
            autocompleteAdapter = AutocompleteAdapter(context, agentNameModel)
            list_agent!!.setAdapter(autocompleteAdapter)
        }
        list_agent!!.setOnItemClickListener(
                OnItemClickListener { parent, view, position, id ->
                    agentName = autocompleteAdapter!!.getItem(position).AgencyName
                    agentDoneCard = agentName.substring(agentName.indexOf("(") + 1, agentName.indexOf(")"))
                    //                        Active = autocompleteAdapter.getItem(position).Active;
                    list_agent!!.setVisibility(View.GONE)
                    //                        agent_name_tv.setText(agentName);
//                        agent_search_rel.setVisibility(View.VISIBLE);
                    agent_search_edt.setText(agentName)
                    agent_search_edt.setSelection(agent_search_edt.text.length)
                    Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
                })
        agent_search_edt.setOnClickListener {
            agent_search_edt.setText("")
            agentName = ""
            agentDoneCard = ""
            //                Active="False";
            list_agent!!.setVisibility(View.VISIBLE)
        }
        agent_search_edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Common.checkInternetConnection(context)) {
                    if (s.length >= 2) {
                        val term = s.toString()
                        val model = AgentNameRequestModel()
                        model.AgencyName = term
                        model.DeviceId = Common.getDeviceId(context)
                        model.DoneCardUser = loginModel!!.Data.DoneCardUser
                        model.LoginSessionId = EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel!!.LoginSessionId, context), context)
                        model.Type = loginModel!!.Data.UserType
                        model.RequiredType = loginModel!!.Data.UserType
                        call_agent(model)
                        //                    Toast.makeText(context, s.toString() + " " + start + " " + before + " " + count, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.show()
    }

    private fun applyFilter() {
        arrayList!!.clear()
        listAdapter!!.notifyDataSetChanged()
        //set date to fragment
        startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                dayFormat!!.format(endDateCalendar!!.time) + " " +
                dateFormat!!.format(endDateCalendar!!.time)
        pageStart = 1
        pageEnd = 10
        transactionListRequestModel!!.setRowEnd(pageEnd.toString())
        transactionListRequestModel!!.setRowStart(pageStart.toString())
        transactionListRequestModel!!.setFromdate(startDateToSend + "")
        transactionListRequestModel!!.setTodate(endDateToSend + "")
        if (Common.checkInternetConnection(context)) {
            callAgent(transactionListRequestModel, SHOW_PROGRESS)
        } else {
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setSpinnerAdapter(data: Array<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(requireContext(),
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun openStartDatePicker() {
        val datePickerDialog = DatePickerDialog(requireContext(),
                R.style.DatePickerTheme,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    startDateCalendar!![year, monthOfYear] = dayOfMonth
                    if (startDateCalendar!!.after(Calendar.getInstance())) {
                        Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show()
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
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    endDateCalendar = Calendar.getInstance()
                    endDateCalendar!!.set(year, monthOfYear, dayOfMonth)
                    if (endDateCalendar!!.after(Calendar.getInstance())) {
                        Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show()
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

    private fun openReceipt(senderResponse: AepsListResponseModel.transactionListDetail) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        dialog.findViewById<TextView>(R.id.title).text="AEPS Receipt"
        val cardHolderTv = dialog.findViewById<TextView>(R.id.cardHolderTv)
        val agentCodeTv = dialog.findViewById<TextView>(R.id.agentCodeTv)
        val bankNameTv = dialog.findViewById<TextView>(R.id.bankNameTv)
        val accountNoTv = dialog.findViewById<TextView>(R.id.accountNoTv)
        val benIdTv = dialog.findViewById<TextView>(R.id.benIdTv)
        val apiTxnIdTv = dialog.findViewById<TextView>(R.id.apiTxnIdTv)
        val jckTxnIdTv = dialog.findViewById<TextView>(R.id.jckTxnIdTv)
        val bankRefNoTv = dialog.findViewById<TextView>(R.id.bankRefNoTv)
        val remitAmountTv = dialog.findViewById<TextView>(R.id.remitAmountTv)
        val availBalTv = dialog.findViewById<TextView>(R.id.availBalTv)
        val txnTypeTv = dialog.findViewById<TextView>(R.id.txnTypeTv)
        val txnStatusTv = dialog.findViewById<TextView>(R.id.txnStatusTv)
        val txnDateTv = dialog.findViewById<TextView>(R.id.txnDateTv)
        val aadharTv = dialog.findViewById<TextView>(R.id.aadharTv)
        val cardNameLin = dialog.findViewById<LinearLayout>(R.id.cardNameLin)

        if(senderResponse.cardHolderName==null || senderResponse.cardHolderName.isEmpty()){
            cardNameLin.visibility=View.GONE
        }else{
            cardNameLin.visibility=View.VISIBLE
        }
        if(senderResponse.txnType.equals("Cash Withdrawal")){
            aadharTv.text="Aadhar No"
        }else{
            aadharTv.text="Card Holder"
        }
        cardHolderTv.text = senderResponse.cardHolderName
        agentCodeTv.text = senderResponse.agentCode
        bankNameTv.text = senderResponse.bankName
        if(senderResponse.accountNo!=null && senderResponse.accountNo.length>6){
            accountNoTv.text="xxxxxxxx"+senderResponse.accountNo.substring(senderResponse.accountNo.length-4)
        }else{
            accountNoTv.text=senderResponse.accountNo
        }
        benIdTv.text = ""
        apiTxnIdTv.text = senderResponse.transactionId
        jckTxnIdTv.text = senderResponse.jckTransactionId
        bankRefNoTv.text = senderResponse.rrn
        txnTypeTv.text = senderResponse.txnType
        txnStatusTv.text = senderResponse.txnStatusDesc
        remitAmountTv.text = senderResponse.txnAmount.toString()
        availBalTv.text=senderResponse.balanceAmount.toString()
        txnDateTv.text = senderResponse.createdDate
        dialog.findViewById<View>(R.id.back_tv).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    private fun hideSoftInputFromDialog(dialog: Dialog) {
        try {
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(dialog.window!!.currentFocus!!.windowToken, 0)
        } catch (e: NullPointerException) {
        }
    }


    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, requireContext().resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    public class StatusCheck {
        private var TransactionId: String? = null
        private var Merchant: String? = ApiConstants.MerchantId
        private var LoggedinAgentCode: String? = null
        private var AgentCode: String? = null


        fun setTransactionId(transactionId: String?) {
            TransactionId = transactionId
        }
        fun setLoggedinAgentCode(agentCode: String?) {
            LoggedinAgentCode = agentCode
        }
        fun setAgentCode(agentCode:String?){
            AgentCode=agentCode
        }
    }

    class StatusCheckResponse {
        private var statusCode: String? = null
        private var statusMessage: String? = null

        fun getStatusCode(): String? {
            return statusCode
        }
        fun getStatusMessage():String?{
            return statusMessage
        }
    }

    override fun onResume() {
        super.onResume()
        arrayList!!.clear()
        getTransactions()
        //Toast.makeText(context, "refresh", Toast.LENGTH_SHORT).show();
        recyclerView?.post(Runnable { recyclerView?.scrollTo(0, 0) })
    }

    }