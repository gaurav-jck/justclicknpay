package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentAccountStatementBinding
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
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import jxl.write.biff.RowsExceededException
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AccountStatementListFragment : Fragment(), View.OnClickListener {
    private var mView:View?=null
    private var recyclerView: RecyclerView? = null
    private var startDateToSend: String? = null
    private var endDateToSend: String? = null
    private var arrayList: ArrayList<AccountStmtResponse.accountStatementList>? = null
    private var listAdapter: AccountStatementAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var startDateTv: TextView? = null
    private var noRecordTv: TextView? = null
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
    private var accountStmtRequest: AccountStmtRequest? = null
    private var loginModel: LoginModel? = null
    private var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    private var listFilterDialog:Dialog?=null
    private var txnTypeArray:Array<String?> = emptyArray()
    private var confirmationId=""
    private var transactionType="1"
    private var updatedBy=""
    private var filterDoneCardUser=""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayList = ArrayList()
        accountStmtRequest = AccountStmtRequest()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_account_statement, container, false)
            var binding=FragmentAccountStatementBinding.bind(mView!!)
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            binding.noRecordTv.setVisibility(View.GONE)

            startDateTv = mView!!.findViewById(R.id.startDateTv)
            noRecordTv = mView!!.findViewById(R.id.noRecordTv)
            noRecordTv!!.setVisibility(View.GONE)

            mView!!.findViewById<TextView>(R.id.titleTv).setText("Account Statement")
            mView!!.findViewById<View>(R.id.lin_dateFilter).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.linFilter).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
            mView!!.findViewById<View>(R.id.exportExcelTv).setOnClickListener(this);

            //initialize date values
            setDates()

//        addValueToModel();
            listAdapter =
                AccountStatementAdapter(
                    requireContext(),
                    { view, list, data, position ->
                        when (view.id) {
                            R.id.confirmIdTv -> try {
//                                Toast.makeText(context, "print", Toast.LENGTH_LONG).show()
                                getReceiptData(data)
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "Enable to print data", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                    },
                    arrayList
                )
            layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.setLayoutManager(layoutManager)
            binding.recyclerView.setAdapter(listAdapter)

            getTxnType()
            getAccountStmt()
        }

        return mView
    }

    private fun getReceiptData(data: AccountStmtResponse.accountStatementList?) {
        if(data!!.transactionType.equals(TxnType.DMTBooking) || data!!.transactionType.equals(TxnType.DMTRefund) ||
            data!!.transactionType.equals(TxnType.DMTValidation) || data!!.transactionType.equals(TxnType.DMTValidationRefund)){
            getDMTTxnDetail(data.referenceid)
        }else if(data!!.transactionType.equals(TxnType.MATMBooking) || data!!.transactionType.equals(TxnType.MATMQRTXN) ||
            data!!.transactionType.equals(TxnType.MATMOneTime) || data!!.transactionType.equals(TxnType.MATMQRActivate) ||
            data!!.transactionType.equals(TxnType.AepsAMSBooking)||
            data!!.transactionType.equals(TxnType.AepsPayout) || data!!.transactionType.equals(TxnType.AepsACWBooking)){
            getMATMTxnDetail(data.referenceid)
        }else if(data!!.transactionType.equals(TxnType.AepsACPBooking)  || data!!.transactionType.equals(TxnType.AepsMiniStatement) ||
             data!!.transactionType.equals(TxnType.AepsPayoutReversal) ||
            data!!.transactionType.equals(TxnType.AepsOneTime)){
            getAEPSTxnDetail(data.referenceid)
        }else if(data!!.transactionType.equals(TxnType.Recharge) || data!!.transactionType.equals(TxnType.RechargeInsurance) ||
            data!!.transactionType.equals(TxnType.RechargeFastagPayment) || data!!.transactionType.equals(TxnType.RechargeFastagRefund) ||
            data!!.transactionType.equals(TxnType.RechargePolicy) || data!!.transactionType.equals(TxnType.RechargePolicyRejected) ||
            data!!.transactionType.equals(TxnType.RechargeLICPayment) || data!!.transactionType.equals(TxnType.RechargeLICRefund) ||
            data!!.transactionType.equals(TxnType.RechargePancardRefund) || data!!.transactionType.equals(TxnType.RechargeUtilityPayment) ||
            data!!.transactionType.equals(TxnType.RechargePancardPayment) || data!!.transactionType.equals(TxnType.RechargeUtilityRefund)){
            getRechargeTxnDetail(data.referenceid)
        }
    }

    private fun getDMTTxnDetail(referenceid: String?) {
        var detailReceiptRequest = DetailReceiptRequest()
        detailReceiptRequest.jcktransactionid=referenceid
        val json = Gson().toJson(detailReceiptRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_DETAIL).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.getDMTTransactionbyID, detailReceiptRequest)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerDmt(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun responseHandlerDmt(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), DmtDetailReceiptResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    if(commonResponse.data!=null){
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(DMTTxnDetailFragment.newInstance(commonResponse.data.get(0)));
                    }
                }else {
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
    }

    private fun getMATMTxnDetail(referenceid: String?) {
        var detailReceiptRequest = DetailReceiptRequest()
        detailReceiptRequest.jcktransactionid=referenceid
        val json = Gson().toJson(detailReceiptRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_DETAIL).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.getMATMTransactionbyID, detailReceiptRequest)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerMatm(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun responseHandlerMatm(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), MatmDetailReceiptResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    if(commonResponse.data!=null){
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(MATMTxnDetailFragment.newInstance(commonResponse.data.get(0)));
                    }
                }else {
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
    }

    private fun getAEPSTxnDetail(referenceid: String?) {
        var detailReceiptRequest = DetailReceiptRequest()
        detailReceiptRequest.jcktransactionid=referenceid
        val json = Gson().toJson(detailReceiptRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_DETAIL).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.getAEPSTransactionbyid, detailReceiptRequest)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerAeps(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun responseHandlerAeps(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), AepsDetailReceiptResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    if(commonResponse.data!=null){
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(AEPSTxnDetailFragment.newInstance(commonResponse.data.get(0)));
                    }
                }else {
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
    }

    private fun getRechargeTxnDetail(referenceid: String?) {
        var detailReceiptRequest = DetailReceiptRequest()
        detailReceiptRequest.jcktransactionid=referenceid
        val json = Gson().toJson(detailReceiptRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_DETAIL).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.getRechargeTransactionbyid, detailReceiptRequest)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerRecharge(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun responseHandlerRecharge(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), RechargeDetailReceiptResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    if(commonResponse.data!=null){
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(RechargeTxnDetailFragment.newInstance(commonResponse.data.get(0)));
                    }
                }else {
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
//        val call = apiService.testUatService(ApiConstants.GetAgentName, model)
        NetworkCall().callService(call,context,false
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerAgent(response, agent_auto)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    var agentArray:ArrayList<AgentNameModel.AgentName> = ArrayList()
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

//                        agent_auto.setAdapter<ArrayAdapter<String>>(getSpinnerAdapter(arr))
                        agent_auto.setAdapter<ArrayAdapter<String>>(Common.getAutocompleteAdapter(arr,requireContext()))
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

    fun getTxnType() {
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_STMT).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.TransactionType, "")
        NetworkCall().callServiceWithoutDialog(call,context
        ) { response, responseCode ->
            if (response != null && responseCode==200) {
                val txnTypeResponse = Gson().fromJson(response!!.string(), TxnTypeResponse::class.java)
                if(txnTypeResponse!=null){
                    txnTypeArray= arrayOfNulls(txnTypeResponse.ttype.size+1)
                    txnTypeArray[0]="All"
                    for(i in 1..txnTypeResponse.ttype.size){
                        txnTypeArray[i]=txnTypeResponse.ttype.get(i-1)
                    }
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAccountStmt() {
        accountStmtRequest!!.FromDate=startDateToSend
        accountStmtRequest!!.UptoDate=endDateToSend
        accountStmtRequest!!.UserType=loginModel!!.Data.UserType
//        accountStmtRequest!!.UserType="OOU"
        accountStmtRequest!!.TransactionType=transactionType
        accountStmtRequest!!.AgentID=loginModel!!.Data.UserId
        accountStmtRequest!!.RefId=confirmationId
        accountStmtRequest!!.RefAgency=loginModel!!.Data.RefAgency
        accountStmtRequest!!.JUpdatedBy=updatedBy
        accountStmtRequest!!.distributordid=""
        if(accountStmtRequest!!.UserType.equals("O") || accountStmtRequest!!.UserType.equals("OOU")){
            accountStmtRequest!!.Donecarduser=filterDoneCardUser
        }else if(accountStmtRequest!!.UserType.equals("D")){
            accountStmtRequest!!.distributordid=loginModel!!.Data.DoneCardUser
            accountStmtRequest!!.Donecarduser=filterDoneCardUser
        }else{
            accountStmtRequest!!.Donecarduser=loginModel!!.Data.DoneCardUser
        }

        val json = Gson().toJson(accountStmtRequest)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_ACCOUNT_STMT).create(ApiInterface::class.java)
        val call = apiService.accountStmtPost(ApiConstants.AccountStatementList, accountStmtRequest)
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandler(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), AccountStmtResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    noRecordTv!!.visibility = View.GONE
                    arrayList!!.clear()
                    arrayList!!.addAll(commonResponse.accountStatementList)
//                    totalPageCount = commonResponse.totalCount
//                    listAdapter!!.setCount(totalPageCount)
                    listAdapter!!.notifyDataSetChanged()
                    if (commonResponse.accountStatementList != null &&
                        commonResponse.accountStatementList.size == 0) {
                        noRecordTv!!.visibility = View.VISIBLE
                    }
                }else {
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

                accountStmtRequest!!.FromDate=startDateToSend
                accountStmtRequest!!.UptoDate=endDateToSend

                getAccountStmt()
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
            R.id.exportExcelTv -> {
                if(arrayList!!.size==0){
                    Toast.makeText(requireContext(), "No data for export to excel", Toast.LENGTH_SHORT).show()
                }else{
                    askSelfPermission()
                }
//                askSelfPermission()
            }
            R.id.linFilter -> {
                if(listFilterDialog==null || (agentArray.size==0 && !loginModel!!.Data.UserType.equals("A"))){
                    openListFilterDialog()
                }else{
                    listFilterDialog!!.show()
                }
            }
            R.id.back_arrow -> parentFragmentManager.popBackStack()
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app
                createExcelSheet()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    lateinit var readImagePermission:String
    private fun askSelfPermission(){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                readImagePermission = Manifest.permission.READ_MEDIA_IMAGES
            else
                readImagePermission = Manifest.permission.READ_EXTERNAL_STORAGE

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                readImagePermission
            ) -> {
                // You can use the API that requires the permission.
                createExcelSheet()
            }
            else -> {
                requestPermissionLauncher.launch(
                    readImagePermission
                )

            }
        }
    }

//    https://stackoverflow.com/questions/43510418/generate-excel-from-android-listview
//    https://medium.com/geekculture/creating-an-excel-in-android-cd9c22198619

    private fun createExcelSheet() {
        val fileName = "jck_accountStmt" + System.currentTimeMillis() + ".xls"
        var myFile= File(requireContext().getExternalFilesDir(null)!!.path, fileName)
        val wbSettings = WorkbookSettings()
        wbSettings.setLocale(Locale("en", "EN"))
        val workbook: WritableWorkbook
        try {
            val a = 1
            workbook = Workbook.createWorkbook(myFile, wbSettings)
            //workbook.createSheet("Report", 0);
            val sheet: WritableSheet = workbook.createSheet("Account Statement", 0)

            var labelList:ArrayList<Label> = ArrayList()
            labelList.add(Label(0, 0, "SNo."))
            labelList.add(Label(1, 0, "Confirmation Id"))
            labelList.add(Label(2, 0, "Booking Ref No."))
            labelList.add(Label(3, 0, "Transaction Date"))
            labelList.add(Label(4, 0, "Debit"))
            labelList.add(Label(5, 0, "Credit"))
            labelList.add(Label(6, 0, "Balance"))
            labelList.add(Label(7, 0, "Transaction Type"))
            labelList.add(Label(8, 0, "Current Credit Amount"))
            labelList.add(Label(9, 0, "Remarks"))
            labelList.add(Label(10, 0, "Updated by"))

            for(i in 0 until arrayList!!.size){
                var r=i+1
                labelList.add(Label(0, r, r.toString()))
                labelList.add(Label(1, r, arrayList!!.get(i).referenceid))
                labelList.add(Label(2, r, ""))
                labelList.add(Label(3, r, arrayList!!.get(i).txndate))
                labelList.add(Label(4, r, arrayList!!.get(i).txnAMTD))
                labelList.add(Label(5, r, arrayList!!.get(i).txnAMTC))
                labelList.add(Label(6, r, arrayList!!.get(i).balance))
                labelList.add(Label(7, r, arrayList!!.get(i).transactionType))
                labelList.add(Label(8, r, arrayList!!.get(i).currentCreditAmount))
                labelList.add(Label(9, r, arrayList!!.get(i).remarks))
                labelList.add(Label(10, r, arrayList!!.get(i).updatedBy))
            }

            try {
                for(label in labelList){
                    sheet.addCell(label)
                }
            } catch (e: RowsExceededException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: WriteException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            workbook.write()
            try {
                workbook.close()
            } catch (e: WriteException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
//            Toast.makeText(requireContext(), "File is created!!!", Toast.LENGTH_SHORT).show()
            openGeneratedFile(myFile)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun openGeneratedFile(myFile: File) {
        if (myFile.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
//            val uri = Uri.fromFile(myFile)
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider", myFile
            )
            val myMime = MimeTypeMap.getSingleton()
            val mimeType =
                myMime.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(photoURI.toString())) //It will return the mimetype

            intent.setDataAndType(photoURI, mimeType)
//            intent.setDataAndType(photoURI, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "No Application available to view excel file",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(requireContext(), "No file exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openListFilterDialog() {
        listFilterDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
//        listFilterDialog = Dialog(requireContext())
        listFilterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        listFilterDialog!!.setContentView(R.layout.account_stmt_filter)
        val typeSpinner = listFilterDialog!!.findViewById<Spinner>(R.id.typeSpinner)
        val confirmIdEdt = listFilterDialog!!.findViewById<EditText>(R.id.confirmIdEdt)
        val updatedByEdt = listFilterDialog!!.findViewById<EditText>(R.id.updatedByEdt)
        val agentLabelTv = listFilterDialog!!.findViewById<TextView>(R.id.agentLabelTv)
        val agent_auto = listFilterDialog!!.findViewById<AutoCompleteTextView>(R.id.agent_auto)

        val adapter = ArrayAdapter(requireContext(),
            R.layout.agent_details_spinner_item_dropdown, R.id.operator_tv, txnTypeArray)
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown)
        typeSpinner.adapter = adapter
//        typeSpinner.setSelection(txnStatusPosition)
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position == 0) {
                    transactionType="1"
                } else {
                    transactionType=txnTypeArray.get(position)!!
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        listFilterDialog!!.findViewById<View>(R.id.back_arrow).setOnClickListener { listFilterDialog!!.dismiss() }
        listFilterDialog!!.findViewById<View>(R.id.resetTv).setOnClickListener {
            typeSpinner.setSelection(0)
            confirmIdEdt.setText("")
            updatedByEdt.setText("")
            agent_auto.setText("")
        }
        listFilterDialog!!.findViewById<View>(R.id.applyTv).setOnClickListener {
            listFilterDialog!!.dismiss()
            confirmationId = confirmIdEdt.text.toString().trim { it <= ' ' }
            updatedBy = updatedByEdt.text.toString().trim { it <= ' ' }
            var agent=agent_auto.text.toString()
            if(agent.contains("(") && agent.contains(")")) {
                filterDoneCardUser = agent.substring(agent.indexOf("( ") + 1, agent.indexOf(" )")).trim()
            }else{
                filterDoneCardUser=""
            }
            applyFilter()
        }

//        agent_auto.addTextChangedListener(TextWatcher)
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
//            getDistributorAgents(agent_auto, "")
        }

        val window = listFilterDialog!!.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        listFilterDialog!!.show()
    }

    private fun applyFilter() {
        arrayList!!.clear()
        listAdapter!!.notifyDataSetChanged()
        //set date to fragment
        startDateTv!!.text = dayFormat!!.format(startDateCalendar!!.time) + " " +
                dateFormat!!.format(startDateCalendar!!.time) + "   -   " +
                dayFormat!!.format(endDateCalendar!!.time) + " " +
                dateFormat!!.format(endDateCalendar!!.time)
        getAccountStmt()
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


    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, requireContext().resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    override fun onResume() {
        super.onResume()
    }

}