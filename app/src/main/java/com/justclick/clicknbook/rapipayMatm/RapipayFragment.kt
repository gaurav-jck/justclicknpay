package com.justclick.clicknbook.rapipayMatm

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.MatmArrSyncActivity
import com.mposaar.rapipaymatm10arr.rapipaymatm100.activity.NewMatmArrActivity
import kotlinx.android.synthetic.main.activity_main_rapipay.*
import kotlinx.android.synthetic.main.activity_main_rapipay.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint




public class RapipayFragment : Fragment() {
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    val BAL_INQ = "Balance Enquiry"; val CASH_WITH = "Cash Withdrawal";val SYNC = "sync"
    val INQ = 1; val WITH = 2;val SYN = 3
    private val BalEnq = "BE"
    private val CashWith = "CW"
    val MiniStmt = "MS"
    val MerchantIdt = "116036";val SubMerchantIdt = "27266";val SaltDatat = "2ABCCCA12796CC8C7296CF2330CE5114"
    val MerchantIdo = "181792" ;val SubMerchantIdo = "181792" ;val SaltDatao = "1706A98F834A4C20B3D3DF57BE7A30FD"
    val MerchantId = "27266" ;val SubMerchantId = "246341" ;val SaltData = "1706A98F834A4C20B3D3DF57BE7A30FD"
    val Name = "JCKTest" ;val returnUrl = "https://matm.justclicknpay.com/api_V1/PaymentEngine/CashWithDraw"
    val ret="www.justclicknpay.in"
    protected var btAdapter: BluetoothAdapter? = null
    protected val REQUEST_BLUETOOTH = 101
    var transactionType: String? = null
    var tType=CashWith
    private var isInitiateTxn = false
    private var clientRefId: String? = null; var smId:String? = null; var jckTransactionId:String?="1234"; var mobile:String?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main_rapipay,container,false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        bluetooth()

        view.myRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                if (checkedId == R.id.cashid) {
                    assignBundleValue(0)
                } else if (checkedId == R.id.balanceid) {
                    assignBundleValue(1)
                }
            }
        })
        transactionType = CASH_WITH
        view.btn_submit_aeps.setOnClickListener{
            Common.preventFrequentClick(view.btn_submit_aeps)
            if(transactionType.equals(CASH_WITH) && view.input_amount.text.toString().isEmpty()){
                Toast.makeText(activity, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show()
            }else{
                Common.hideSoftKeyboard(activity)
                initiateMatmTxn(WITH)
//                makeTxn()
            }
//            Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
        }

        view.syc_btn.setOnClickListener {
            Common.preventFrequentClick(view.syc_btn)
            Common.hideSoftKeyboard(activity)
            if(mobile==null || mobile!!.length==0){
                initiateMatmTxn(SYN)
            }else{
                syncData()
            }
        }

        view.txn_btn.setOnClickListener {
            Common.preventFrequentClick(txn_btn)
            Common.hideSoftKeyboard(activity)
            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(MatmTransactionListFragment())
//            getTxnList()
        }

        view.back_arrow.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return view;
        }

    open fun assignBundleValue(position: Int) {
        if (position == 0) {
            transactionType = CASH_WITH
            tType=CashWith
            view?.input_amount?.setText("")
            view?.input_amount?.isEnabled = true
        } else if (position == 1) {
            transactionType = BAL_INQ
            tType=BalEnq
            view?.input_amount?.setText("0")
            view?.input_amount?.isEnabled = false
        }
    }

    fun bluetooth(){
        btAdapter= BluetoothAdapter.getDefaultAdapter()
        if(btAdapter==null){
            AlertDialog.Builder(requireContext())
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit") { dialog, which -> System.exit(0) }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        } else {
            Log.d("GoPosActivity", "bluetooth adapter is not null")
            if (!btAdapter!!.isEnabled) {
                Log.d("GoPosActivity", "bluetooth is not enable")
                val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBT, REQUEST_BLUETOOTH)
            } else {
                Log.d("GoPosActivity", "bluetooth is enable")
                accessBluetoothDetails()
            }
        }
    }

    var bluetoothDevice: BluetoothDevice? = null
    var bluetoothName: String? = null

    @SuppressLint("MissingPermission")
    private fun accessBluetoothDetails(): String? {
        if (btAdapter!!.bondedDevices != null) if (btAdapter!!.bondedDevices.size > 0) {
            val pairedDevices = btAdapter!!.bondedDevices
            val devices = ArrayList<String>()
            var isPosPaired = false
            for (device in pairedDevices) {
                if (device.name.startsWith("D180") || device.name.startsWith("MP-")) {
                    bluetoothDevice = device
                    isPosPaired = true
                    if (device.name.startsWith("MP-")) bluetoothName = device.name.substring(3) else if (device.name.startsWith("D180")) bluetoothName = device.name
                    val bluetoothAddress = device.address
                    Log.d("GoPosActivity",
                            "bluetoothName: "
                                    + bluetoothName
                                    + " ,bluetoothAddress:"
                                    + bluetoothAddress)
                    if (!TextUtils.isEmpty(bluetoothAddress)) {
                        return bluetoothAddress
                    }
                } else {
                    isPosPaired = false
                }
            }
            if (!isPosPaired) {
                AlertDialog.Builder(requireContext())
                        .setTitle("Bluetooth Enable")
                        .setMessage("Please Enable your Bluetooth by pressing OK")
                        .setPositiveButton("OK") { dialog, which ->
                            val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startActivityForResult(enableBT, REQUEST_BLUETOOTH)
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }
        } else {
            AlertDialog.Builder(requireContext())
                    .setTitle("BlueTooth Pairing")
                    .setMessage("Your bluetooth is not paired with MATM device please pair")
                    .setPositiveButton("Ok") { dialog, which ->
                        val intentOpenBluetoothSettings = Intent()
                        intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
                        startActivity(intentOpenBluetoothSettings)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
        return null
    }

    private fun initiateMatmTxn(transactionType: Int) {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = InitiateMatmTxnRequest()
        request.agentCode = loginModel.Data.DoneCardUser
//        request.agentCode = "JC0A39550"
        if(transactionType==SYN){
            request.amount=0f
            request.txnType = "SYNC"
        }else{
            request.amount = input_amount!!.text.toString().toFloat()
            request.txnType = tType
        }

        NetworkCall().callRapipayMatmService(request, ApiConstants.InitiateMatmTxn, context,
         { response, responseCode ->
            if (response != null) {
                responseHandlerCredential(response, transactionType) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                isInitiateTxn = true
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        },true)
    }

    private fun responseHandlerCredential(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), InitiateMatmTxnResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    clientRefId = senderResponse.transactionId
                    smId = senderResponse.smId
//                    smId = "246341"
                    jckTransactionId = senderResponse.jckTransactionId
                    mobile = senderResponse.mobile
//                    mobile = "9389173616";
                    if(TYPE==SYN){
                        syncData()
                    }else{
                        makeTxn()
                    }
//                    Toast.makeText(context, """${senderResponse.statusMessage}${senderResponse.transactionId}""".trimIndent(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeTxn() {
        try {
            if (!accessBluetoothDetails()!!.isEmpty() && transactionType != null && !input_amount!!.text.toString().isEmpty()) {
                val date = Calendar.getInstance().time
                val timestamp = SimpleDateFormat("yyyymmddHH").format(date)
//                val timestamps = SimpleDateFormat("yyyymmddHHmm").format(date)
//                @SuppressLint("SimpleDateFormat") val timestamps = SimpleDateFormat("yyyymmddHHmm").format(Date())
                val intent = Intent(getActivity(), NewMatmArrActivity::class.java)
                val strhasdata = Utils.sha512(MerchantId, smId, clientRefId, SaltData)  //rapi
//                val strhasdata = Utils.sha512(MerchantId, smId, timestamps, SaltData)
                Log.d("strdata", strhasdata)
                val b = Bundle()
                b.putString("Merchantid", MerchantId)
                b.putString("Submerchantid", smId) //2ABCCCA12796CC8C7296CF2330CE5114 uat
//                b.putString("Submerchantid", SubMerchantId) //rapi
//                b.putString("Submerchantid", SubMerchantId) //2ABCCCA12796CC8C7296CF2330CE5114 uat
//                b.putString("clientRefID", timestamps);   //rapi
                b.putString("clientRefID", clientRefId)
//                b.putString("timestamp", timestamp)
                b.putString("HashData", strhasdata)
                b.putString("Amount", input_amount!!.text.toString()) //
                b.putString("TransactionType", transactionType)
                //            04:23:33:2A:13:93
                b.putString("BluetoothId", accessBluetoothDetails())
                b.putString("CustomerMobile", mobile)
//                b.putString("CustomerMobile", "9078787887")
                b.putString("CustomerName", Name)
                b.putString("reverseURLtoPostRes", returnUrl)
                intent.putExtras(b)
                saveRequestAsLog(bundleToJSON(b)!!)
                startActivityForResult(intent, NEW_MATM_AEPS_Resposne)

            } else {
                Toast.makeText(activity, "First pair bluetooth and select type", Toast.LENGTH_LONG).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun syncData() {
        try {
            if (!accessBluetoothDetails()!!.isEmpty()) {
                val timestamp = SimpleDateFormat("yyyymmddHH").format(Date()).toString()
                val timestamps = SimpleDateFormat("yyyymmddHHmm").format(Date()).toString()
                val intent = Intent(activity, MatmArrSyncActivity::class.java)
                val b = Bundle()
                b.putString("Merchantid", MerchantId)
                b.putString("Submerchantid", smId)
                b.putString("clientRefID", clientRefId)
                b.putString("HashData", Utils.sha512(MerchantId, smId, clientRefId, SaltData))
//                b.putString("HashData", Utils.sha512(MerchantId, smId, timestamps, SaltData))
                b.putString("Amount", "0")
                b.putString("TransactionType", SYNC)
                b.putString("BluetoothId", accessBluetoothDetails())
                b.putString("CustomerMobile", mobile)
                b.putString("CustomerName", Name)
                //    b.putString("saltData", "2ABCCCA12796CC8C7296CF2330CE5114");
                b.putString("reverseURLtoPostRes", returnUrl)
                intent.putExtras(b)
                bundleToJSON(b)
                startActivityForResult(intent, NEW_MATM_AEPS_Resposne)
            } else {
                Toast.makeText(activity, "First pair bluetooth and select type", Toast.LENGTH_LONG).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun bundleToJSON(bundle: Bundle): String? {
        val json = JSONObject()
        val keys = bundle.keySet()
        for (key in keys) {
            try {
                 json.put(key, bundle.get(key)); //see edit below
//                json.put(key, JSONObject.wrap(bundle[key]))
            } catch (e: JSONException) {
                //Handle exception here
            }
        }
        return json.toString()
    }

    private fun saveRequestAsLog(request:String) {
//        var loginModel = LoginModel()
//        loginModel = MyPreferences.getLoginData(loginModel, context)
        val matmRequest = MatmRequestLog()
        matmRequest.Json = request
        NetworkCall().callRapipayMatmService(matmRequest, ApiConstants.MatmAppLog, context
        ,{ response, responseCode ->
            if (response != null) {
//                Toast.makeText(context, "Success "+response.string(), Toast.LENGTH_SHORT).show()
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(context, "Request log", Toast.LENGTH_SHORT).show()
            }
        },false)
    }

    private val NEW_MATM_AEPS_Resposne = 150

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                NEW_MATM_AEPS_Resposne -> {
                    val response = data.getStringExtra("Responce")
                    if (resultCode == -3) {
                        try {
                            val jsonObject = JSONObject(response)
                            error_msg(jsonObject.optString("DisplayMessage") + " " + jsonObject.optString("ResponseCode"), "")
                            //                            updateDetails("" + jsonObject.optString("DisplayMessage") + " " + jsonObject.optString("ResponseCode"), jsonObject.optString("ResponseCode"));
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (resultCode == -2) {
                        error_msg(response!!, data.getStringExtra("ResponseCode"))
                        //
                    } else if (resultCode == -1) {
                        val senderResponse = Gson().fromJson(response, MATMResponse::class.java)
                        openReceipt(senderResponse)
                        //
                    } else if (resultCode == -4) {
                        try {
                            error_msg(response!!, data.getStringExtra("ResponseCode"))
                            //
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        error_msg(response!!, data.getStringExtra("ResponseCode"))
                        //
                    }
                }
            }
        }
        else{
            Toast.makeText(context, "SYNC failed", Toast.LENGTH_SHORT).show()
        }
    }

    open fun error_msg(response: String, responsecode: String?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.errormsg_dailog)
        val dialog_title = dialog.findViewById<TextView>(R.id.dialog_title)
        val dialog_msg = dialog.findViewById<TextView>(R.id.alertmsg)
        val dialog_currentdate_text = dialog.findViewById<TextView>(R.id.dialog_currentdate)
        //        TextView VersionName = dialog.findViewById(R.id.dialog_version);
        val dialog_service = dialog.findViewById<TextView>(R.id.dialog_service)
        dialog_service.text = "MATM"
        dialog_currentdate_text.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        //        VersionName.setText("ver " + BuildConfig.VERSION_NAME);
        val btn = dialog.findViewById<Button>(R.id.okbutton)
        dialog_title.setText(R.string.Alert)
        dialog_msg.text = response.replace(",", "\n")
        btn.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.setCancelable(false)
    }

    private fun openReceipt(senderResponse: MATMResponse) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
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
        val errorTv = dialog.findViewById<TextView>(R.id.errorTv)
        val contentLin = dialog.findViewById<LinearLayout>(R.id.contentLin)
        cardHolderTv.text = senderResponse.cardHolderName
        agentCodeTv.text = MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        bankNameTv.text = senderResponse.bankName
//        accountNoTv.text = senderResponse.accountNo
        benIdTv.text = ""
        jckTxnIdTv.text = jckTransactionId
        apiTxnIdTv.text = senderResponse.clientRefID
        bankRefNoTv.text = senderResponse.rrn
        txnTypeTv.text = senderResponse.transactionType
        txnStatusTv.text = senderResponse.displayMessage
        remitAmountTv.text = senderResponse.txnAmt
        availBalTv.text = senderResponse.availableBalance
        txnDateTv.text = senderResponse.transactionDatetime
        if(senderResponse.accountNo!=null && senderResponse.accountNo.length>6){
            accountNoTv.text="##########"+senderResponse.accountNo.substring(senderResponse.accountNo.length-4)
        }else{
            accountNoTv.text=senderResponse.accountNo
        }
        dialog.findViewById<View>(R.id.back_tv).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun getTxnList() {
        val bookingDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(Calendar.getInstance().time)
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = TxnListRequestModel()
        request.setAgentCode(loginModel.Data.DoneCardUser)
        request.setFromdate(bookingDate)
        request.setTodate(bookingDate)
        request.setRowStart("1")
        request.setRowEnd("10")
        NetworkCall().callRapipayMatmService(request, ApiConstants.TransactionListMatm, context
        ,{ response, responseCode ->
            if (response != null) {
                responseHandlerList(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                isInitiateTxn = true
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        },true)
    }

    private fun responseHandlerList(response: ResponseBody, i: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), TxnListResponseModel::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    val bundle= Bundle()
                    bundle.putSerializable("ListResponse",senderResponse)
                    val txnListFragment=MatmTransactionListFragment()
                    txnListFragment.arguments=bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(txnListFragment)
//                    startActivity(Intent(context, TxnListActivity::class.java))
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    class MatmRequestLog{
        var Json: String?=null
    }
}
