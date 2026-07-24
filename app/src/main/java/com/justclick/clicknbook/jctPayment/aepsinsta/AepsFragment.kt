package com.jck.myjckapp.ui.fragments.aeps

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentAepsTransactionBinding
import com.justclick.clicknbook.jctPayment.aepsinsta.AepsBankListResponse
import com.justclick.clicknbook.jctPayment.aepsinsta.AepsInstaResponse
import com.justclick.clicknbook.jctPayment.aepsinsta.TxnOtpResponse
import com.justclick.clicknbook.jctPayment.newaeps.AepsConstants
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.util.Scanner
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.random.Random

class AepsFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val FINGER_CAPTURE = "Finger"
    private val FACE_CAPTURE = "Face"
    private final val CAPTURE_REQUEST_CODE = 123
    var d_type = AepsConstants.MANTRA
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var transactionType=FINGER_CAPTURE
    private var transactionMethod=FINGER_CAPTURE
    private var binding: FragmentAepsTransactionBinding?=null
    private var txnType=AepsConstants.BE
    private lateinit var loginResponse:LoginModel
    private var bankId:String?=null
    private var bankIIN:String?=null
    private var bankName:String?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var isAmount=true

    companion object {
        fun newInstance(param1: CheckCredentialResponse.credentialData, type:String) =
            AepsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, type)
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginResponse=MyPreferences.getLoginData(LoginModel(),context)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
            }
            txnType= requireArguments().getString(ARG_PARAM2).toString()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAepsTransactionBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)

        if(txnType!!.equals(AepsConstants.CW)){
            binding!!.topView.titleTv.text = "Cash Withdrawal"
            binding!!.amountEdt.isEnabled=true
            binding!!.amountEdt.setText("")
            binding!!.amount.visibility=View.VISIBLE
            transactionType="ATMCW"
            transactionMethod=ApiConstants.CashWithdrawal
            isAmount=true
        }else if(txnType!!.equals(AepsConstants.MS)){
            binding!!.topView.titleTv.text = "Mini Statement"
            binding!!.amountEdt.isEnabled=false
            binding!!.amountEdt.setText("0")
            binding!!.amount.visibility=View.GONE
            transactionType="ATMMS"
            transactionMethod=ApiConstants.MiniStatment
            isAmount=false
        }else if(txnType!!.equals(AepsConstants.BE)){
            binding!!.topView.titleTv.text = "Balance Enquiry"
            binding!!.amountEdt.isEnabled=false
            binding!!.amountEdt.setText("0")
            binding!!.amount.visibility=View.GONE
            transactionType="ATMBE"
            transactionMethod=ApiConstants.BalanceEnquiry
            isAmount=false
        }else{
            binding!!.topView.titleTv.text = "Aadhar Pay"
            binding!!.amountEdt.isEnabled=true
            binding!!.amountEdt.setText("")
            binding!!.amount.visibility=View.VISIBLE
            transactionType="ATMAP"
            transactionMethod=ApiConstants.AadharPay
            isAmount=true
        }
        binding!!.btnCapture.setOnClickListener {
            if(validateData()){
                captureType=FINGER_CAPTURE
                Common.hideSoftKeyboard(requireActivity())
                if(binding!!.amountEdt.text.toString().toFloat()>5000){
                    callOtpApi()
                }else{
                    captureData()
                }
            }
        }
        binding!!.btnCaptureFace.setOnClickListener {
            if(validateData()){
                captureType=FACE_CAPTURE
                Common.hideSoftKeyboard(requireActivity())
                if(binding!!.amountEdt.text.toString().toFloat()>5000){
                    callOtpApi()
                }else{
                    captureFaceData("")
                }

            }
        }

        getIpAddress()
        checkLocationPermission()

        binding!!.spinnerDeviceType.adapter=
            Common.getSpinnerAdapter(AepsConstants.deviceArray, requireContext())
        binding!!.spinnerDeviceType.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                when (i) {
                    0 -> d_type = AepsConstants.MANTRA
                    1 -> d_type = AepsConstants.MORPHO
                    2 -> d_type = AepsConstants.STARTEK
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        binding!!.topView.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        getBankList()

        binding!!.autoBank.setOnClickListener(
            {
                binding!!.autoBank.showDropDown()
            }
        )
        binding!!.autoBank.setOnItemClickListener { adapterView, view, pos, l ->
            val selection = adapterView.getItemAtPosition(pos) as String
            var pos = -1

            for (i in bankArray!!.indices) {
                if (bankArray!![i].BANK_NAME.equals(selection)) {
                    pos = i
                    break
                }
            }
            try {
                bankId = bankArray!![pos].BankId
                bankIIN = bankArray!![pos].IIN
                bankName = bankArray!![pos].BANK_NAME
                Toast.makeText(context, bankName, Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                bankId=null
            }
        }
        return binding!!.root
    }

    private fun callOtpApi() {
        var loginModel= MyPreferences.getLoginData(LoginModel(), context)
        val params: MutableMap<String, String> = HashMap()
        params["AadharNumber"] = binding!!.aadharEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Latitude"] = mLatitude!!
        params["Longitude"] = mLongitude!!
        params["IPAddress"] = ip!!
        params["Mobile"] = binding!!.mobileEdt.text.toString()
        params["BankIIN"] = bankIIN!!
        params["Amount"] = binding!!.amountEdt.text.toString()
        params["amount"] = binding!!.amountEdt.text.toString()

        NetworkCall().callService(
            NetworkCall.getAepsInterface().getInstaAepsHeaderMap(
                ApiConstants.aepstransactionotp, params,
                commonParams!!.userData, "Bearer " + commonParams!!.token
            ),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerOtp(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerOtp(response: ResponseBody) {
        try {
//            val responseString=
            val commonResponse = Gson().fromJson(response.string(), TxnOtpResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Toast.makeText(context, "Otp send on your registered mobile number", Toast.LENGTH_LONG).show()
                    openTxnOtpDialog(commonResponse)
                } else {
//                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "Api Response")
                }
            }else{
                Toast.makeText(context, "Error in response", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exception in response", Toast.LENGTH_SHORT).show()
        }
    }

    private var otpDialog:Dialog?=null
    private fun openTxnOtpDialog(responseModel: TxnOtpResponse) {
        otpDialog = Dialog(requireContext())
        otpDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        otpDialog!!.setContentView(R.layout.aeps_insta_txn_otp_dialog)

//        val inputMethodManager =
//            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////        inputMethodManager.showSoftInput(null,0)
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        val otpEdt = otpDialog!!.findViewById<EditText>(R.id.otpEdt1)
//        val agentCodeTv = dialog.findViewById<TextView>(R.id.agentCodeTv)

//        Toast.makeText(requireContext(), responseModel.data.referenceKey, Toast.LENGTH_SHORT).show()

        otpDialog!!.findViewById<View>(R.id.submit_btn).setOnClickListener {
            if(otpEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter otp", Toast.LENGTH_SHORT).show()
            }else if(otpEdt.text.toString().length<4){
                Toast.makeText(requireContext(), "Please enter valid otp", Toast.LENGTH_SHORT).show()
            }else{
                Common.hideSoftKeyboard(requireActivity())
                if(captureType.equals(FINGER_CAPTURE)){
                    captureOtp(otpEdt.text.toString())
                }else{
                    captureFaceData(otpEdt.text.toString())
                }
            }
        }
        otpDialog!!.findViewById<View>(R.id.close_btn).setOnClickListener { otpDialog!!.dismiss() }
        otpDialog!!.show()
    }

    private fun captureOtp(otp: String) {
        Toast.makeText(requireContext(), otp, Toast.LENGTH_SHORT).show()
        try {
            if (d_type == AepsConstants.MANTRA) {
                val pidOptXML =getPidOptionsOtp(otp)
                capture(AepsConstants.MANTRA_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.MORPHO) {
                val pidOptXML =getPidOptionsOtp(otp)
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == AepsConstants.STARTEK) {
                val pidOptXML =getPidOptionsOtp(otp)
                capture(AepsConstants.STARTEK_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }


    private fun validateData(): Boolean {
        val amount=binding!!.amountEdt.text.toString()
        if(binding!!.aadharEdt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter your Aadhar number", Toast.LENGTH_SHORT).show()
        }else if(binding!!.aadharEdt.text.toString().length<12){
            Toast.makeText(requireContext(), "Please enter valid Aadhar number", Toast.LENGTH_SHORT).show()
        }else if(binding!!.mobileEdt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter mobile number", Toast.LENGTH_SHORT).show()
        }else if(binding!!.mobileEdt.text.toString().length<10){
            Toast.makeText(requireContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
        }else if(amount.isEmpty()){
            Toast.makeText(requireContext(), "Please enter amount", Toast.LENGTH_SHORT).show()
        }else if(isAmount && !Common.isdecimalvalid(amount)){
            Toast.makeText(requireContext(), "Please enter valid amount", Toast.LENGTH_SHORT).show()
        }/*else if(isAmount && (amount.toFloat()<100 || amount.toFloat()>5000)){
            Toast.makeText(requireContext(), "Amount should be between 100 to 5000", Toast.LENGTH_SHORT).show()
        }*/else if(isAmount && amount.toFloat()<100){
            Toast.makeText(requireContext(), "Minimum amount should be 100", Toast.LENGTH_SHORT).show()
        }else if(bankId==null){
            Toast.makeText(requireContext(), "Please select bank", Toast.LENGTH_SHORT).show()
        }else if(mLatitude==null){
            Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        }else{
            return true
        }
        return false
    }

    private fun getBankList() {
        NetworkCall().callService(
            NetworkCall.getAepsInterface().getAepsInstaBankList(
                ApiConstants.getbankname,
            ),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerBankList(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private var bankArray=ArrayList<AepsBankListResponse>()
    private fun responseHandlerBankList(response: ResponseBody) {
        try {
            val listType = object : TypeToken<List<AepsBankListResponse>>() {}.type
            val bankList: List<AepsBankListResponse> = Gson().fromJson(response.string(), listType)
            if (bankList != null && bankList.isNotEmpty()) {
//                Toast.makeText(context, "Bank List Fetched", Toast.LENGTH_SHORT).show()
                bankArray!!.addAll(bankList)
                setAdapter()
            } else {
                Toast.makeText(context, "Bank list not fetched", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun setAdapter() {
        val arr = arrayOfNulls<String>(bankArray.size)
        for (p in bankArray.indices) {
            arr[p] = bankArray[p].BANK_NAME
        }
        binding!!.autoBank.setAdapter(getSpinnerAdapter(arr))
//        binding!!.autoBank.showDropDown()
    }


    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter<String?>(requireContext(), R.layout.mobile_operator_spinner_item,
            R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }
    fun captureData() {
        try {
            if (d_type == AepsConstants.MANTRA) {
                val pidOptXML =getPidOptions()
                capture(AepsConstants.MANTRA_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.MORPHO) {
                val pidOptXML =getPidOptions()
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == AepsConstants.STARTEK) {
                val pidOptXML =getPidOptions()
                capture(AepsConstants.STARTEK_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
//                    isDataCaptured=false;
//                    showMessageDialogue("OnActivity result.  resultcode="+resultCode+" Message="+data.getData().getUserInfo(), "Message");
                if (data == null) {
                    showMessageDialogue("Scan Failed/Aborted!", "Message")
                } else {
                    pidDataXML = data.getStringExtra("PID_DATA")!!
                    if (pidDataXML != null) {
                        // xml parsing
                        captureType=FINGER_CAPTURE
                        readXMLData(pidDataXML, captureType)
                    } else {
                        showMessageDialogue(
                            "NULL STRING RETURNED",
                            "Fingerprint data status"
                        )
                    }
                }
            } catch (ex: java.lang.Exception) {
                showMessageDialogue("Error:-" + ex.message, "EXCEPTION")
                ex.printStackTrace()
            }
        }
    }

    private val startForFaceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                if (data != null) {
                    val bundle = data.extras
                    if (bundle != null) {
                        pidDataXML = bundle.getString("response").toString()
                        if (pidDataXML != null) {
                            captureType=FACE_CAPTURE
                            readXMLData(pidDataXML, captureType)
//                            Common.showResponsePopUp(requireContext(), response)
                        }else{
//                    handleFailure()
                            Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
//                handleFailure()
                        Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
//            handleFailure()
                    Toast.makeText(requireContext(), "capture failed", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: java.lang.Exception) {
                showMessageDialogue("Error:-" + ex.message, "EXCEPTION")
                ex.printStackTrace()
            }
        }
    }

    fun captureFaceData(otp:String) {
        try {
            val intent = Intent("in.gov.uidai.rdservice.face.CAPTURE")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

//            Common.showCommonAlertDialog(requireContext(), createPidOptionForKUA(getRandomNumber(), "P"),"Face XML")
            intent.putExtra(
                "request",
                createPidOptionForKUA(getRandomNumber(), "P", otp)
            )

            startForFaceResult.launch(intent)
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }
    fun getRandomNumber(): String {
        val start = 10000000
        val end = 99999999
        val number = Random(System.nanoTime()).nextInt(end - start + 1) + start
        return number.toString()
    }
    fun getWADH2(): String {
        return ""
    }
    val LANGUAGE=""
    fun createPidOptionForKUA(txnId: String, buildType:String, otp:String): String {
        return createPidOptionsKUA(txnId, "auth", getWADH2(), buildType, otp)
    }
    private fun createPidOptionsKUA(txnId: String, purpose: String, wadh:String, buildType:String, otp:String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PidOptions ver=\"1.0\" env=\"${buildType}\">\n" +
                "   <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"\" otp=\"${otp}\" wadh=\"${wadh}\" posh=\"\" />\n" +
                "   <CustOpts>\n" +
                "      <Param name=\"txnId\" value=\"${txnId}\"/>\n" +
                "      <Param name=\"purpose\" value=\"$purpose\"/>\n" +
                "      <Param name=\"language\" value=\"${LANGUAGE}\"/>\n" +
                "   </CustOpts>\n" +
                "</PidOptions>"
    }

    private fun readXMLData(pidDataXML: String, type: String) {
        try {
            val inputStream: InputStream =
                ByteArrayInputStream(pidDataXML.toByteArray(charset("UTF-8")))
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inputStream)
            val element = doc.documentElement
            element.normalize()
            val nList = doc.getElementsByTagName("PidData")
            val node = nList.item(0)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element2 = node as Element
                val s_status = element2.getElementsByTagName("Resp")
                    .item(0).attributes.getNamedItem("errCode").nodeValue
                if (s_status == "0") {
//                    isDataCaptured=true;
                    if(otpDialog!=null && otpDialog!!.isShowing){
                        otpDialog!!.dismiss()
                    }
                    showTransactionAlert()
                    //                    showMessageDialogue("Data captured", "Fingerprint data status");
                } else {
                    val s_message = element2.getElementsByTagName("Resp")
                        .item(0).attributes.getNamedItem("errInfo").nodeValue
                    if(type.equals(FINGER_CAPTURE))
                    {
                        showMessageDialogue(s_message, "Fingerprint data status")
                    }else{
                        showMessageDialogue(s_message, "Face capture data status")
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("Jobs", "Exception parse xml :$e")
            //            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private fun capture(packageName: String, pidOptXML: String, requestCode: Int) {
//        sessionCheckMethod(false);
        val intent1 = Intent("in.gov.uidai.rdservice.fp.CAPTURE", null)
        //String pidOptXML = getPIDOptions(); //working
        intent1.putExtra("PID_OPTIONS", pidOptXML)
        intent1.setPackage(packageName)
        startForResult.launch(intent1)
//        startActivityForResult(intent1, requestCode)
    }

    private fun getPidOptions():String{
        return "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
    }
    private fun getPidOptionsOtp(otp:String):String{
        return "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"\" otp=\"${otp}\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
    }

    // show message
    private fun showMessageDialogue(messageTxt: String, argTitle: String) {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(argTitle)
            .setMessage(messageTxt)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> dialog.cancel() }
            .show()
    }

    // show message
    private fun showTransactionAlert() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("Please confirm your Transaction")
            .setMessage("Do you want to do this transaction")
            .setPositiveButton("CONFIRM") { dialog, which ->
                dialog.cancel()
                sendMobileTransaction()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, i -> dialog.cancel() }
            .show()
    }

    private fun sendMobileTransaction() {
        var loginModel= MyPreferences.getLoginData(LoginModel(), context)
        val params: MutableMap<String, String> = HashMap()
        params["AadharNumber"] = binding!!.aadharEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Latitude"] = mLatitude!!
        params["Lattitude"] = mLatitude!!
        params["Longitude"] = mLongitude!!
        params["CaptureType"] = captureType
        params["Mode"] = "App"
        params["Merchant"] = ApiConstants.MerchantId
        params["IPAddress"] = ip!!
        params["Ip"] = ip!!
        params["CustomerName"] = ""
        params["Mobile"] = binding!!.mobileEdt.text.toString()
        params["DeviceId"] = Common.getDeviceId(requireContext())
        params["BankIIN"] = bankIIN!!
        params["BankName"] = bankName!!
        params["Amount"] = binding!!.amountEdt.text.toString()
        params["TxnType"] = transactionType

        pidDataXML=pidDataXML.replace("\n", "")
        params["Pid"]=pidDataXML

        NetworkCall().callService(
            NetworkCall.getAepsInterface().getInstaAepsHeaderMap(
                transactionMethod, params,
                commonParams!!.userData, "Bearer " + commonParams!!.token
            ),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
//            val responseString=
            val commonResponse = Gson().fromJson(response.string(), AepsInstaResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    if(txnType!!.equals(AepsConstants.MS) || txnType!!.equals(AepsConstants.BE)){
                        openReceipt(commonResponse)
                    }else{
                        openReceipt(commonResponse)
                    }
                } else {
//                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "Api Response")

                }
            }else{
                Toast.makeText(context, "Error in response", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exception in response", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openReceipt(responseModel: AepsInstaResponse) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rapipay_matm_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        (dialog.findViewById<View>(R.id.title) as TextView).text = "AEPS Receipt"
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
        val cardNameLin = dialog.findViewById<LinearLayout>(R.id.cardNameLin)
        cardNameLin.visibility = View.GONE
        //        cardHolderTv.setText("xxxxxxxx"+str_aadhar.substring(str_aadhar.length()-4));
        val detail = responseModel!!.balEnqDetails!![0]
        agentCodeTv.text = detail.agentCode
        bankNameTv.text = detail.bankName
        if (detail.accountNumber != null && detail.accountNumber!!.length > 6) {
            accountNoTv.text =
                "XXXXXXXX" + detail.accountNumber!!.substring(detail.accountNumber!!.length - 4)
        } else {
            accountNoTv.text = detail.accountNumber
        }
        //        benIdTv.setText(detail.transactionId);
        apiTxnIdTv.text = detail.apiTxnId
        jckTxnIdTv.text = detail.jckTransactionId
        bankRefNoTv.text = detail.rrn
        txnTypeTv.text = detail.txnType
        txnStatusTv.text = detail.status
        remitAmountTv.text = detail.txnAmount + ""
        availBalTv.text = detail.availableBalance
        txnDateTv.text = detail.timeStamp
        dialog.findViewById<View>(R.id.back_tv).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                // Handle permission denied case
                Toast.makeText(requireContext(), "You have denied location permission, please allow location services to continue", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var mLatitude:String?=null
    private var mLongitude:String?=null
    private fun getLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permissions are not granted, handle this case (should be caught by checkLocationPermission)
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        mLatitude = location.latitude.toString()
                        mLongitude = location.longitude.toString()
                        // Use latitude and longitude
                    } else {
                        // Location is null, consider requesting location updates for a fresh location
                    }
                }
                .addOnFailureListener { e ->
                    // Handle location retrieval failure
                }
        }catch (e :Exception){

        }
    }

    var ip: String? = null
    fun getIpAddress() {
        requireActivity().runOnUiThread {
            try {
                val url = URL("https://api.ipify.org")
                val connection = url.openConnection()
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0/Chrome"
                ) // Set a User-Agent to avoid HTTP 403 Forbidden error
                val inputStream = connection.getInputStream()
                val s = Scanner(inputStream, "UTF-8").useDelimiter("\\A")
                ip = s.next()
                inputStream.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                ip = "103.139.75.200"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callMethod()
    }

    private var callLoc=1
    var handler=Handler(Looper.getMainLooper())
    private fun callMethod() {
        val runnable = object : Runnable {
            override fun run() {
                // Call your function here
                if(mLatitude==null){
                    getLastLocation()
                    binding!!.btnCaptureFace.text = "Capture Face Data $callLoc"
                    // Schedule the next execution in 1/10 second (1000ms)
                    handler.postDelayed(this, 100)
                }else{
                    handler.removeCallbacks(this)
                }

            }
        }
// To start the loop
        if(mLatitude==null) {
            handler.post(runnable)
        }
// To stop the loop (crucial to prevent memory leaks)
        if(mLatitude!=null){
            handler.removeCallbacks(runnable)
        }
    }

    var responseData="{\n" +
            "    \"statusCode\": \"01\",\n" +
            "    \"statusMessage\": \"ERR -- No account found for given Aadhaar number\",\n" +
            "    \"balEnqDetails\": [\n" +
            "        {\n" +
            "            \"bankName\": null,\n" +
            "            \"agentCode\": \"JC0A47068\",\n" +
            "            \"availableBalance\": \"0\",\n" +
            "            \"rrn\": \"----\",\n" +
            "            \"txnAmount\": \"00\",\n" +
            "            \"txnType\": \"BalanceEnquiry\",\n" +
            "            \"timeStamp\": \"15-05-2026 14:37:17\",\n" +
            "            \"accountNumber\": \"-------\",\n" +
            "            \"status\": \"Failed\",\n" +
            "            \"jckTransactionId\": \"BE15056DE3QJC0A47068\",\n" +
            "            \"apiTxnId\": \"1960526198377\"\n" +
            "        }\n" +
            "    ]\n" +
            "}"


    val miniResponse="{\n" +
            "  \"statusCode\": \"200\",\n" +
            "  \"status\": \"Success\",\n" +
            "  \"message\": \"Transaction Successful\",\n" +
            "  \"data\": {\n" +
            "    \"externalRef\": \"MARK25111714114752776\",\n" +
            "    \"bankName\": \"State Bank of India\",\n" +
            "    \"accountNumber\": \"xxxxxxxx1693\",\n" +
            "    \"ipayId\": \"CNA012532114114740\",\n" +
            "    \"transactionMode\": \"CR\",\n" +
            "    \"payableValue\": \"1.47\",\n" +
            "    \"transactionValue\": \"0.00\",\n" +
            "    \"openingBalance\": \"277744.00\",\n" +
            "    \"closingBalance\": \"277745.47\",\n" +
            "    \"operatorId\": \"532114399288\",\n" +
            "    \"walletIpayId\": \"1251117141149TGDZM\",\n" +
            "    \"bankAccountBalance\": \"100179.13\",\n" +
            "    \"miniStatement\": [\n" +
            "      {\n" +
            "        \"date\": \"11/11/25\",\n" +
            "        \"txnType\": \"CR\",\n" +
            "        \"amount\": \"25000.00\",\n" +
            "        \"narration\": \"BY TRANSFER\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"date\": \"11/11/25\",\n" +
            "        \"txnType\": \"CR\",\n" +
            "        \"amount\": \"22000.00\",\n" +
            "        \"narration\": \"BY TRANSFER\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"isOnusTxn\": false,\n" +
            "    \"marktxnid\": 0.0\n" +
            "  }\n" +
            "}"
}