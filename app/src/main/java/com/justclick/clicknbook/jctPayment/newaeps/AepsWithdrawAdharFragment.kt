package com.justclick.clicknbook.jctPayment.newaeps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.ActivityAepsWithdrawBinding
import com.justclick.clicknbook.jctPayment.Adapters.MiniStatementAdapter
import com.justclick.clicknbook.jctPayment.Models.AepsBankListModel
import com.justclick.clicknbook.jctPayment.Models.GetAadharRequest
import com.justclick.clicknbook.jctPayment.Models.UpdateLocationRequest
import com.justclick.clicknbook.jctPayment.Utilities.GetAepsCredential
import com.justclick.clicknbook.jctPayment.Utilities.URLs
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.random.Random


public class AepsWithdrawAdharFragment(Type: String, aepsPipe: String) : Fragment() {
    //https://developers.google.com/identity/sign-in/android/sign-in
    private val CashWith = "CW"
    private val Finger = "Finger"
    private val Face = "Face"
    private val CAPTURE_REQUEST_CODE = 123
    private val FINO_AEPS_CODE: Int = 12
    private var context: Context? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    var str_aadhar: String? = null
    var str_amount: String? = null
    var mobileNo: String? = null
    var pidDataXML = ""
    var d_type = AepsConstants.MANTRA
    var adharType:String? = AepsConstants.ADHAR_UID
    var captureType = Finger
    var TYPE = Type
    var aepsPipe = aepsPipe
    var URL: String? = null
    private var isGetAgain = false
    val PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLatitude = "29.9319558"
    var mLongitude= "77.5334789"
    lateinit var binding:ActivityAepsWithdrawBinding
    private var bankId:String?=null
    private var bankName:String?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context=context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        //        location find
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // method to get the location
        getLastLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= ActivityAepsWithdrawBinding.inflate(inflater)

        if (TYPE.equals(CashWith)) {
            binding.titleTv.text="Cash Withdraw"
            URL = URLs.WithdrawCashAuthentication
        } else {
            binding.titleTv.text="Aadhar pay"
            URL = URLs.AadharPayAeps
            binding.faceBtn.visibility=View.GONE
        }
        binding.rbVirtualId.setVisibility(View.GONE)
//        binding.txtAadharno.setText(MyPreferences.getAgentAdhar(context))
        binding.txtMobileno.setText(MyPreferences.getAgentMobile(context))
        if (binding.txtAadharno.text.toString().length == 12) {
            showCapture()
        } else {
            hideCapture()
        }

        binding.fingerBtn.setOnClickListener { v: View? ->
            Common.preventFrequentClick(binding.fingerBtn)
            str_aadhar = binding.txtAadharno.text.toString().trim { it <= ' ' }
            str_amount = binding.txtAmount.text.toString().trim { it <= ' ' }
            mobileNo = binding.txtMobileno.getText().toString().trim { it <= ' ' }
            if (bankId==null) {
                Toast.makeText(context, "Please select bank", Toast.LENGTH_SHORT).show()
            }else if (!Common.isMobileValid(mobileNo)) {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(str_amount)) {
                binding!!.txtAmount.setError("Please enter Amount")
            } else if (!Common.isdecimalvalid(str_amount) || str_amount!!.toFloat() == 0.toFloat()) {
                binding!!.txtAmount.setError("Please enter valid amount")
            }else{
                Common.hideSoftKeyboard(requireActivity())
                if (!isGetAgain) {
                    captureType=Finger
                    checkAepsCredential()
                } else {
                    captureData();
                }
            }
        }

        binding.faceBtn.setOnClickListener { v: View? ->
            Common.preventFrequentClick(binding.faceBtn)
            str_aadhar = binding.txtAadharno.text.toString().trim { it <= ' ' }
            str_amount = binding.txtAmount.text.toString().trim { it <= ' ' }
            mobileNo = binding.txtMobileno.getText().toString().trim { it <= ' ' }
            if (bankId==null) {
                Toast.makeText(context, "Please select bank", Toast.LENGTH_SHORT).show()
            }else if (!Common.isMobileValid(mobileNo)) {
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            }else if (TextUtils.isEmpty(str_amount)) {
                binding!!.txtAmount.setError("Please enter Amount")
            } else if (!Common.isdecimalvalid(str_amount) || str_amount!!.toFloat() == 0.toFloat()) {
                binding!!.txtAmount.setError("Please enter valid amount")
            }else{
                Common.hideSoftKeyboard(requireActivity())
                if (!isGetAgain) {
                    captureType=Face
                    checkAepsCredential()
                } else {
                    captureFaceData()
                }
            }
        }



        binding.txtAadharno.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && adharType == AepsConstants.ADHAR_UID) {
                if (binding.txtAadharno.text.toString().length < 12) {
                    binding.txtAadharno.error = getResources().getString(R.string.aadharNoError)
                }
            } else if (!hasFocus && adharType == AepsConstants.VIRTUAL_ID) {
                if (binding.txtAadharno.text.toString().length < 16) {
                    binding.txtAadharno.error = getResources().getString(R.string.virtualIdError)
                }
            }
        }

        binding.txtAadharno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (adharType == AepsConstants.ADHAR_UID && binding.txtAadharno.text.toString().length < 12) {
                    hideCapture()
                } else if (adharType == AepsConstants.VIRTUAL_ID && binding.txtAadharno.text.toString().length < 16) {
                    hideCapture()
                } else {
                    if (validation()) {
                        showCapture()
                    } else {
                        hideCapture()
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })

        binding.backArrow.setOnClickListener( {
            getParentFragmentManager().popBackStack();
        })

        binding!!.spinnerDeviceType.adapter=
            Common.getSpinnerAdapter(AepsConstants.deviceArrayNew, requireContext())

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

        getBankList()

        binding!!.updateLocationTv.setOnClickListener(
            {
                updateLocation()
            }
        )

        binding!!.autoBank.setOnClickListener(
            {
                binding!!.autoBank.showDropDown()
            }
        )
        binding!!.autoBank.setOnItemClickListener { adapterView, view, pos, l ->
            val selection = adapterView.getItemAtPosition(pos) as String
            var pos = -1

            for (i in bankArray.indices) {
                if (bankArray[i].bankName.equals(selection)) {
                    pos = i
                    break
                }
            }
            try {
                bankId = bankArray[pos].iinno
                bankName = bankArray[pos].bankName
                Toast.makeText(context, bankArray[pos].bankName+"  "+bankArray[pos].iinno, Toast.LENGTH_SHORT).show();
            } catch (e: java.lang.Exception) {
                bankId=null
                bankName=null
            }
        }

        return binding.root
    }

    private fun getBankList() {
        val loginModel = LoginModel()
        val request = GetAadharRequest()
        request.AgentCode = MyPreferences.getLoginData(loginModel, context).Data.DoneCardUser

        val json = Gson().toJson(request)

        NetworkCall().callService(
            NetworkCall.getAepsInterface().getAepsBankList(URLs.GetBankList),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response)
            } else {
//                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

//    var bankArray: java.util.ArrayList<String> = java.util.ArrayList()
    var bank_iin: java.util.HashMap<String, String> = java.util.HashMap()
    private var bankArray: ArrayList<AepsBankListModel.data> = ArrayList()
    private fun responseHandler(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                AepsBankListModel::class.java
            )
            if (senderResponse?.banklist != null) {
                if (senderResponse.banklist.data?.size!! >0) {
                    bankArray= senderResponse.banklist.data!!
                    val array=ArrayList<String>()
                    for(i in bankArray){
                        array.add(i.bankName)
                    }
                    binding!!.autoBank.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,array))
                }
            } else {
                Toast.makeText(context, resources.getString(R.string.bankListError), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, resources.getString(R.string.bankListError), Toast.LENGTH_LONG).show()
        }
    }

    fun captureData() {
        isGetAgain = true
        try {
            if (d_type == AepsConstants.STARTEK && validation()) {
                val pidOptXML =getPidOptXml()
                capture(AepsConstants.STARTEK_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == AepsConstants.MANTRA && validation()) {
                val pidOptXML =getPidOptXml()
                capture(AepsConstants.MANTRA_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.MORPHO && validation()) {
                val pidOptXML =getPidOptXml()
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }
        } catch (e: Exception) {
            showMessageDialogue("EXCEPTION- " + e.message, "EXCEPTION")
        }
    }

    private fun getPidOptXml():String {
        val pidOptXML =
            "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
        return pidOptXML
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
                        captureType=Finger
                        readXMLData(pidDataXML)
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
                            captureType=Face
                            readXMLData(pidDataXML)
//                            Common.showResponsePopUp(requireContext(), response)
                        }else{
//                    handleFailure()
                            Toast.makeText(context, "capture failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
//                handleFailure()
                        Toast.makeText(context, "capture failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
//            handleFailure()
                    Toast.makeText(context, "capture failed", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: java.lang.Exception) {
                showMessageDialogue("Error:-" + ex.message, "EXCEPTION")
                ex.printStackTrace()
            }
        }
    }

    fun captureFaceData() {
        try {
            val intent = Intent("in.gov.uidai.rdservice.face.CAPTURE")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            intent.putExtra(
                "request",
                createPidOptionForKUA(getRandomNumber(), "P")
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
//        return "mtDVz0PM/HvMAWSkCkjcxW+KhNWk2nfbUhfZwLl2faw="
        return ""
    }
    val LANGUAGE=""
    fun createPidOptionForKUA(txnId: String, buildType:String): String {
        return createPidOptionsKUA(txnId, "auth", getWADH2(), buildType)
    }
    private fun createPidOptionsKUA(txnId: String, purpose: String, wadh:String, buildType:String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PidOptions ver=\"1.0\" env=\"${buildType}\">\n" +
                "   <Opts fCount=\"1\" fType=\"1\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"\" otp=\"\" wadh=\"${wadh}\" posh=\"\" />\n" +
                "   <CustOpts>\n" +
                "      <Param name=\"txnId\" value=\"${txnId}\"/>\n" +
                "      <Param name=\"purpose\" value=\"$purpose\"/>\n" +
                "      <Param name=\"language\" value=\"${LANGUAGE}\"/>\n" +
                "   </CustOpts>\n" +
                "</PidOptions>"
    }

    private fun hideCapture() {
        binding.fingerBtn.isEnabled = false
        binding.fingerBtn.setTextColor(getResources().getColor(R.color.black_text_color))
        binding.fingerBtn.setBackgroundResource(R.color.gray_color)
        binding.fingerBtn.alpha = 0.4f

        binding.faceBtn.isEnabled = false
        binding.faceBtn.setTextColor(getResources().getColor(R.color.black_text_color))
        binding.faceBtn.setBackgroundResource(R.color.gray_color)
        binding.faceBtn.alpha = 0.4f

    }

    private fun showCapture() {
        binding.fingerBtn.isEnabled = true
        binding.fingerBtn.setTextColor(getResources().getColor(R.color.color_white))
        binding.fingerBtn.setBackgroundResource(R.drawable.button_shep)
        binding.fingerBtn.alpha = 1f

        binding.faceBtn.isEnabled = true
        binding.faceBtn.setTextColor(getResources().getColor(R.color.color_white))
        binding.faceBtn.setBackgroundResource(R.drawable.button_shep)
        binding.faceBtn.alpha = 1f

        binding.txtAadharno.error = null
    }

    private fun capture(packageName: String, pidOptXML: String, requestCode: Int) {
        val selectedPackage = packageName
        val intent = Intent("in.gov.uidai.rdservice.fp.CAPTURE", null)
        intent.putExtra("PID_OPTIONS", pidOptXML)
        intent.setPackage(selectedPackage)
        startForResult.launch(intent)
    }

    class AepsResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
        var balEnqDetails: ArrayList<balEnQDetails>? = null

        inner class balEnQDetails {
            var bankName: String? = null
            var availableBalance: String? = null
            var rrn: String? = null
            var accountNumber: String? = null
            var status: String? = null
            var transactionId: String? = null
            var txnAmount: String? = null
            var agentCode: String? = null
            var timeStamp: String? = null
            var jckTransactionId: String? = null
            var apiTxnId: String? = null
            var txnType: String? = null
        } //{"statusCode":"00","statusMessage":"Success","balEnQDetails":[{"bankName":"India Post Payment Bank","":"JC0A13387","availableBalance":"306.2","rrn":"113119689889","txnAmount":"00","":"BalanceEnquiry","":"5/11/2021 7:39:51 PM","accountNumber":"XXXXXXXX5016","status":"SUCCESS","":"MA11051TUEKJC0A13387","":"130707547032"}]}
    }

    private fun sendMobileTransaction() {
//        var loginModel= MyPreferences.getLoginData(LoginModel(),context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["BankIIN"] = bankId!!
        params["BankName"] = bankName!!
        params["AgentCode"] = MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        params["Amount"] = str_amount!!
        params["Mobile"] = mobileNo!!
        params["Merchant"] = ApiConstants.MerchantId
        params["Mode"] = "APP"
        params["MerAuthTxnId"] = ""
        params["AadharNumber"] = str_aadhar!!
        params["Latitude"] = mLatitude
        params["Longitude"] = mLongitude
//        params["pipe"] = aepsPipe
        params["bankPipe"] = aepsPipe
        if(captureType.equals(Face)){
            params["is_iris"] = "face_rd"
        }
        pidDataXML=pidDataXML.replace("\n", "")
        params["PId"] = pidDataXML

//        binding.faceEdt.setText(pidDataXML)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_AEPS_N).create(ApiInterface::class.java)
        val call = apiService.getAepsHeaderMap(URL, params, MyPreferences.getUserData(context),
            MyPreferences.getAepsToken(context))
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerTxn(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerTxn(response: ResponseBody) {
        try {
            if(TYPE.equals(CashWith)){
                val commonResponseModel = Gson().fromJson(response.string(), AepsResponse::class.java)
                if(commonResponseModel!=null ) {
                    if(commonResponseModel.statusCode.equals("00")){
                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                        openReceipt(commonResponseModel);
                    }else{
                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "No response found for given details", Toast.LENGTH_SHORT).show();
                }
            }else {
                val commonResponseModel = Gson().fromJson(response.string(), AepsRegistrationActivity.AepsMiniResponse::class.java)
                if(commonResponseModel!=null ) {
                    if(commonResponseModel.statusCode.equals("00")){
                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                        if(commonResponseModel.msDetails!=null && commonResponseModel.msDetails!!.size>0) {
                            openMiniStatement(commonResponseModel);
                        }else {
                            Toast.makeText(context, "No transaction is showing for this account.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, commonResponseModel.statusMessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "No response found for given details", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Oops! Something went wrong in response.\n"+e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMiniStatement(responseModel: AepsRegistrationActivity.AepsMiniResponse) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.mini_stmt_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        dialog.findViewById<View>(R.id.back_tv).setOnClickListener { dialog.dismiss() }
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = MiniStatementAdapter(context, responseModel.msDetails)

        /*if(responseModel.msDetails!=null && responseModel.msDetails.size()>0){
            Toast.makeText(context, responseModel.msDetails.size()+"\n"+responseModel.msDetails.get(0).amount, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "No mini statement found for given data.", Toast.LENGTH_SHORT).show();
        }*/
        dialog.show()
    }

    private fun openReceipt(responseModel: AepsResponse) {
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

    // validation for submit
    private fun validation(): Boolean {
        str_aadhar = binding.txtAadharno.text.toString().trim { it <= ' ' }
        if (adharType == AepsConstants.ADHAR_UID && TextUtils.isEmpty(str_aadhar)) {
            binding.txtAadharno.error = "Please enter Aadhar Number"
            //            et_aadhar.requestFocus();
            return false
        } else if (adharType == AepsConstants.ADHAR_UID && str_aadhar!!.length != 12) {
            binding.txtAadharno.error = "Please enter 12 digit Aadhar Number"
            //            et_aadhar.requestFocus();
            return false
        } else if (adharType == AepsConstants.VIRTUAL_ID && TextUtils.isEmpty(str_aadhar)) {
            binding.txtAadharno.error = "Please enter Virtual Id"
            //            et_aadhar.requestFocus();
            return false
        } else if (adharType == AepsConstants.VIRTUAL_ID && str_aadhar!!.length != 16) {
            binding.txtAadharno.error = "Please enter 16 digit Virtual Id"
            //            et_aadhar.requestFocus();
            return false
        }

        /*if (pidDataXML == null || pidDataXML.length() == 0) {
            Toast.makeText(this, "Please capture fingerprint before submit", Toast.LENGTH_LONG).show();
            return false;
        }*/
        return true
    }

    private fun readXMLData(pidDataXML: String) {
        try {
            val `is`: InputStream = ByteArrayInputStream(pidDataXML.toByteArray(charset("UTF-8")))

            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(`is`)

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
                    showTransactionAlert()
                    //                    showMessageDialogue("Data captured", "Fingerprint data status");
                } else {
                    val s_message = element2.getElementsByTagName("Resp")
                        .item(0).attributes.getNamedItem("errInfo").nodeValue
                    if(captureType.equals(Finger)) {
                        showMessageDialogue(s_message, "Fingerprint data status")
                    }else{
                        showMessageDialogue(s_message, "Face capture data status")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Jobs", "Exception parse xml :$e")
            showMessageDialogue("Error:-" + e.message, "EXCEPTION")
            //            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            .setTitle("Please confirm your transaction")
            .setMessage("Do you want to do transaction with given details!")
            .setPositiveButton("CONFIRM") { dialog, which ->
                dialog.cancel()
                //                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show();
//                        checkPermissions();
                sendMobileTransaction()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, i -> dialog.cancel() }
            .show()
    }

    private fun updateLocation() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = UpdateLocationRequest()
        request.lat = mLatitude
        request.longitude = mLongitude
        request.merchantcode = loginModel.Data.DoneCardUser
        request.mobile = loginModel.Data.Mobile
//                request.mobile="9012836576";
        GetAepsCredential.updateLocation(context, request)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled

            if (isLocationEnabled()) {
                // getting last
                // location from
                // FusedLocationClient
                // object

                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        mLatitude = location.latitude.toString() + ""
                        mLongitude = location.longitude.toString() + ""
                        //                            Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions()
            //            requestPermissions2();
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        // Initializing LocationRequest
        // object with appropriate methods

        val mLocationRequest = LocationRequest()
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(5)
        mLocationRequest.setFastestInterval(0)
        mLocationRequest.setNumUpdates(1)

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude.toString() + ""
            mLongitude = mLastLocation.longitude.toString() + ""
            //            Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun checkAepsCredential() {
        val request = GetAepsCredential.CheckCredentialRequest()
        val loginModel = LoginModel()
        request.AgentCode = MyPreferences.getLoginData(loginModel, context).Data.DoneCardUser
        MyCustomDialog.showCustomDialog(context, "Please wait...")
        NetworkCall().callAepsServiceNew(
            request, URLs.GenerateToken, context
        ) { response: ResponseBody?, responseCode: Int ->
            isGetAgain=false
            if (response != null) {
                try {
                    val commonResponseModel =
                        Gson().fromJson(
                            response.string(),
                            GetAepsCredential.CheckResponseClass::class.java
                        )
                    if (commonResponseModel != null && commonResponseModel.statusCode.equals(
                            "00",
                            ignoreCase = true
                        )
                    ) {
                        GetAepsCredential.saveData(commonResponseModel, context)
                        MyCustomDialog.hideCustomDialog()
                        if(captureType.equals(Finger)){
                            captureData()
                        }else{
                            captureFaceData()
                        }
                        isGetAgain = true
                    } else {
                        Toast.makeText(
                            context,
                            commonResponseModel!!.statusMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: java.lang.Exception) {
                    MyCustomDialog.hideCustomDialog()
                }
            } else {
                MyCustomDialog.hideCustomDialog()
            }
        }
    }

    // If everything is alright then
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }
    override fun onResume() {
        super.onResume()
    }


    private val miniResponse = """{
    "statusCode": "00",
    "statusMessage": "Success",
    "msDetails": [
        {
            "date": "06/07/2021",
            "txnType": "Dr",
            "amount": "       350.00",
            "narration": " UPI/11873111709"
        },
        {
            "date": "05/07/2021",
            "txnType": "Cr",
            "amount": "       290.00",
            "narration": " UPI/11868230357"
        },
        {
            "date": "05/07/2021",
            "txnType": "Dr",
            "amount": "       200.00",
            "narration": " UPI/11857992248"
        },
        {
            "date": "05/07/2021",
            "txnType": "Dr",
            "amount": "       200.00",
            "narration": " UPI/11850495756"
        },
        {
            "date": "05/07/2021",
            "txnType": "Dr",
            "amount": "      6000.00",
            "narration": " MMT/IMPS/118515"
        },
        {
            "date": "05/07/2021",
            "txnType": "Cr",
            "amount": "      3500.00",
            "narration": " UPI/11851892617"
        },
        {
            "date": "02/07/2021",
            "txnType": "Dr",
            "amount": "        50.00",
            "narration": " AEP/Cash Wdl/02"
        },
        {
            "date": "02/07/2021",
            "txnType": "Dr",
            "amount": "      7423.00",
            "narration": " ACH/TPCAPFRST I"
        },
        {
            "date": "01/07/2021",
            "txnType": "Cr",
            "amount": "     10000.00",
            "narration": " MMT/IMPS/118222"
        },
        {
            "date": "30/06/2021",
            "txnType": "Cr",
            "amount": "        42.00",
            "narration": " 135301507812:In"
        }
    ]
}"""
}