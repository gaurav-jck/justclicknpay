package com.justclick.clicknbook.paysprintMatm

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import com.justclick.clicknbook.databinding.ActivityMainRapipayBinding
import com.justclick.clicknbook.rapipayMatm.*
import com.service.finopayment.Hostnew
import java.lang.StringBuilder


public class MainMatmFragment : Fragment() {
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    val BAL_INQ = "Balance Enquiry"; val CASH_WITH = "Cash Withdrawal";val SYNC = "sync"
    val INQ = 1; val WITH = 2;val SYN = 3
    private val BalEnq = "ATMBE"
    private val CashWith = "ATMCW"
    val MiniStmt = "ATMMS"
    val MerchantId = "27266" ;val SubMerchantId = "246341" ;val SaltData = "1706A98F834A4C20B3D3DF57BE7A30FD"
    val Name = "JCKTest" ;val returnUrl = "https://matm.justclicknpay.com/api_V1/PaymentEngine/CashWithDraw"
    val ret="www.justclicknpay.in"
    var agentCode:String?=null
    var mobile:String?=null
    var customerMobile:String?=null
    protected var btAdapter: BluetoothAdapter? = null
    protected val REQUEST_BLUETOOTH = 101
    var transactionType: String? = null
    var tType=CashWith
    private var isInitiateTxn = false
    private var clientRefId: String? = null;
    var jckTransactionId:String?="1234";
//    var smId:String? = null;
    var line=""
    var partnerId = "PS0068"
    var key = "UFMwMDY4YTEyODZiZmExZWVmYzVhNTQ1MDJjYTBhN2YxNjYwNjk="
    var binding:ActivityMainRapipayBinding?=null
    var chars = charArrayOf(
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '0'
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main_rapipay,container,false)
        binding=ActivityMainRapipayBinding.bind(view)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        line="141"
        try{
            bluetooth()
        }catch (e:Exception){
            AlertDialog.Builder(requireContext())
                .setTitle("Logs")
                .setMessage("Line no="+line+"\n"+e.message+"\n"+e.toString())
//                    .setCancelable(false)
                .setPositiveButton("Ok") { dialog, which ->
                    val intentOpenBluetoothSettings = Intent()
                    intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
                    startActivity(intentOpenBluetoothSettings)
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        line="143"
        binding!!.myRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                if (checkedId == R.id.cashid) {
                    assignBundleValue(0)
                } else if (checkedId == R.id.balanceid) {
                    assignBundleValue(1)
                }
            }
        })
        transactionType = CASH_WITH
        binding!!.btnSubmitAeps.setOnClickListener{
            Common.preventFrequentClick(binding!!.btnSubmitAeps)
            customerMobile=binding!!.mobileEdt.text.toString()
            if(transactionType.equals(CASH_WITH) && binding!!.inputAmount.text.toString().isEmpty()){
                Toast.makeText(activity, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show()
            }else if(customerMobile!!.length<10){
                Toast.makeText(activity, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            }else{
                Common.hideSoftKeyboard(activity)
                initiateMatmTxn(WITH)
//                makeTxn()
            }
//            Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
        }


        binding!!.txnBtn.setOnClickListener {
            Common.preventFrequentClick(binding!!.txnBtn)
            Common.hideSoftKeyboard(activity)
            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(
                MatmTransactionListFragment()
            )
//            getTxnList()
        }

        binding!!.backArrow.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return view;
    }

    open fun assignBundleValue(position: Int) {
        if (position == 0) {
            transactionType = CASH_WITH
            tType=CashWith
            binding!!.inputAmount.setText("")
            binding!!.inputAmount.isEnabled = true
        } else if (position == 1) {
            transactionType = BAL_INQ
            tType=BalEnq
            binding!!.inputAmount.setText("0")
            binding!!.inputAmount.isEnabled = false
        }
    }

    fun bluetooth(){
        line=line+"-197"
        val bluetoothManager: BluetoothManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            line=line+"-200"
            bluetoothManager=requireContext().getSystemService(BluetoothManager::class.java)
            line=line+"-202"
            btAdapter=bluetoothManager.adapter
        }else{
            btAdapter= BluetoothAdapter.getDefaultAdapter()
        }
        if(btAdapter==null){
            AlertDialog.Builder(requireContext())
                .setTitle("Not compatible")
                .setMessage("Your phone does not support Bluetooth")
                .setPositiveButton("Exit") { dialog, which -> System.exit(0) }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        } else {
            line=line+"-215"
            var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    //granted
//                    accessBluetoothDetails()
                }else{
                    //deny
                    parentFragmentManager.popBackStack()
                }
            }
            line=line+"-226"
            val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    permissions.entries.forEach {
                        Log.d("test006", "${it.key} = ${it.value}")
                    }
                }
            line=line+"-233"
            Log.d("GoPosActivity", "bluetooth adapter is not null")
            if (!btAdapter!!.isEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestMultiplePermissions.launch(arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT))
                }
                else{
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    requestBluetooth.launch(enableBtIntent)
                }
            } else {
                Log.d("GoPosActivity", "bluetooth is enable")
//                accessBluetoothDetails()
            }
        }
    }


    var bluetoothDevice: BluetoothDevice? = null
    var bluetoothName: String? = null

    private fun initiateMatmTxn(transactionType: Int) {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = InitiateMatmTxnRequest()
        agentCode = loginModel.Data.DoneCardUser
        mobile = loginModel.Data.Mobile
        request.setMobile(customerMobile)
        request.agentCode = agentCode    // uncomment this
//        request.agentCode = "JC0A42217"
//        request.agentCode = "JC0A36575"
        if(transactionType==SYN){
            request.amount=0f
            request.txnType = "SYNC"
        }else{
            request.amount = binding!!.inputAmount.text.toString().toFloat()
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
//                    smId = senderResponse.smId
//                    smId = "246341"
                    jckTransactionId = senderResponse.jckTransactionId
//                    mobile = senderResponse.mobile
//                    mobile = "9389173616";
                    makeTxn()

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
            if (/*accessBluetoothDetails()!! &&*/ transactionType != null && !binding!!.inputAmount.text.toString().isEmpty()) {
                val intent = Intent(requireContext(), Hostnew::class.java)
                intent.putExtra("partnerId", partnerId)
                intent.putExtra("apiKey", key)
                intent.putExtra("transactionType", tType)
                intent.putExtra("amount", binding!!.inputAmount.text.toString().trim())
                intent.putExtra("merchantCode", agentCode)
//                intent.putExtra("merchantCode", "JC0A36575")
//                intent.putExtra("merchantCode", "JC0A45929")
                intent.putExtra("remarks", "JCK Transaction")
                intent.putExtra("mobileNumber", mobile)
//                intent.putExtra("referenceNumber", getRandomString(5, chars))
                intent.putExtra("referenceNumber", clientRefId)
                intent.putExtra("latitude", "22.572646")
                intent.putExtra("longitude", "88.363895")
                intent.putExtra("subMerchantId", agentCode)
//                intent.putExtra("subMerchantId", "JC0A36575")
//                intent.putExtra("subMerchantId", "JC0A45929")
                intent.putExtra("deviceManufacturerId", "3")
                startActivityForResult(intent, 999)

            } else {
                Toast.makeText(activity, "First pair bluetooth and select type", Toast.LENGTH_LONG).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getRandomString(length: Int, characterSet: CharArray): String? {
        val sb = StringBuilder()
        for (loop in 0 until length) {
            val index = Random().nextInt(characterSet.size)
            sb.append(characterSet[index])
        }
        return sb.toString()
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
                999 -> {
                    val status = data.getBooleanExtra("status", false)
                    val response1 = data.getIntExtra("response", 0)
                    val message = data.getStringExtra("message")
                    if (status) {
                        try {
                            val jsonObject =
                                JSONObject(data.getStringExtra("JSONDATA").toString())
                            var response=MATMResponse()
//                            succes.setVisibility(View.VISIBLE)
//                            fail.setVisibility(View.GONE)
                            response.status=status
                            response.response=message
                            response.message=message
                            response.transType=jsonObject.getString("transType")
                            response.type=jsonObject.getString("type")
                            response.bankrrn=jsonObject.getString("bankRrn")
                            response.balAmount=jsonObject.getString("balAmount")
                            response.transAmount=jsonObject.getString("transAmount")
                            response.txnid=jsonObject.getString("txnid")
                            response.cardNumber=jsonObject.getString("cardNumber")
                            response.cardType=jsonObject.getString("cardType")
                            response.bankName=jsonObject.getString("bankName")
                            response.terminalId=jsonObject.getString("terminalId")
                            updateResponse(response)
                            openReceipt(response)
                            Toast.makeText(
                                requireContext(),
                                "" + message,
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d(" RESULTS", jsonObject.toString())
                        } catch (jsonException: JSONException) {
                            Log.d(" RESULTS", jsonException.toString())
                            jsonException.printStackTrace()
                        }
                    }else{
                        var response=MATMResponse()
                        response.status=status
                        response.response=message
                        response.txnid=clientRefId
                        updateResponse(response)
                    }
                }
            }
        }
        else{
            Toast.makeText(context, "SYNC failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateResponse(response: MATMResponse) {
        val json = Gson().toJson(response)
        NetworkCall().callService(NetworkCall.getMATMApiInterface().getRapipayMatmCommonPost2(ApiConstants.PaysprintCashWithDraw,
            response.txnid, response), context, false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerUpdate(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerUpdate(response: ResponseBody, i: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), InitiateMatmTxnResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
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
        dialog_title.setText("Alert")
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
        val aadharTv = dialog.findViewById<TextView>(R.id.aadharTv)
        val contentLin = dialog.findViewById<LinearLayout>(R.id.contentLin)
        dialog.findViewById<LinearLayout>(R.id.accNoLin).visibility=View.GONE
        cardHolderTv.text = senderResponse.cardNumber
        agentCodeTv.text = MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        aadharTv.text = "Card Number"
        bankNameTv.text = senderResponse.bankName
        benIdTv.text = ""
        jckTxnIdTv.text = jckTransactionId
        apiTxnIdTv.text = senderResponse.txnid
        bankRefNoTv.text = senderResponse.bankrrn
        txnTypeTv.text = senderResponse.transType
        txnStatusTv.text = senderResponse.message
        remitAmountTv.text = senderResponse.transAmount
        availBalTv.text = senderResponse.balAmount
//        txnDateTv.text = senderResponse.transactionDatetime
//        if(senderResponse.accountNo!=null && senderResponse.accountNo.length>6){
//            accountNoTv.text="##########"+senderResponse.accountNo.substring(senderResponse.accountNo.length-4)
//        }else{
//            accountNoTv.text=senderResponse.accountNo
//        }
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
                    val txnListFragment= MatmTransactionListFragment()
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
