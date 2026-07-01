package com.jck.myjckapp.ui.fragments.aeps

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillFetchResponse
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayBillerDetailsRequest
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayBillerDetailsResponse
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayFetchBillRequest
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayOperatorList
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayOperatorListRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaFetchBillBinding
import com.justclick.clicknbook.jctPayment.aepsinsta.AepsInstaResponse
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.Constants.BillPay
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import java.net.URL
import java.util.Scanner

class InstaBillerDetailFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private var binding: FragmentInstaFetchBillBinding?=null
    private var billType=BillPay.Electricity
    private lateinit var loginResponse:LoginModel
    private var billerName:String?=null
    private var billerId:String?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var mView:View?=null

    companion object {
        fun newInstance(param1: CheckCredentialResponse.credentialData, type:String) =
            InstaBillerDetailFragment().apply {
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
            billType= requireArguments().getString(ARG_PARAM2).toString()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(mView==null) {
            binding= FragmentInstaFetchBillBinding.inflate(layoutInflater)
            mView = binding!!.root

            binding!!.topView.titleTv.text = "$billType Biller Detail"

            binding!!.fetchbillTv.setOnClickListener {
                fetchBillDetails()
            }

            getIpAddress()
            checkLocationPermission()
            binding!!.fetchbillTv.isEnabled = false
            binding!!.fetchbillTv.alpha = 0.5f

            binding!!.topView.backArrow.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            binding!!.cancelTv.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            getOperatorList()
            paramVisibilityGone()

            binding!!.atvOperator.setOnClickListener(
                {
                    binding!!.atvOperator.showDropDown()
                }
            )
            binding!!.atvOperator.setOnFocusChangeListener(
                { view, isFocus ->
                    if(isFocus){
                        binding!!.atvOperator.showDropDown()
                    }
                }
            )
            binding!!.atvOperator.setOnItemClickListener { adapterView, view, pos, l ->
                val selection = adapterView.getItemAtPosition(pos) as String
                var pos = -1

                for (i in operatorArray!!.indices) {
                    if (operatorArray!![i].billername.equals(selection)) {
                        pos = i
                        break
                    }
                }
                try {
                    billerId = operatorArray!![pos].billerid
                    billerName = operatorArray!![pos].billername
                    Toast.makeText(context, billerName, Toast.LENGTH_SHORT).show()
                    getBillerDetails()
                } catch (e: java.lang.Exception) {
                    billerId = null
                }
            }
        }
        return mView
    }

    private fun fetchBillDetails() {
        if(billerId==null){
            Toast.makeText(requireContext(), "Please select operator", Toast.LENGTH_SHORT).show()
        }else if(isParam1 && binding!!.param1Edt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter "+billerDetail!!.parameters[0].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam1 && !Common.isRegexValid(binding!!.param1Edt.text.toString(),billerDetail!!.parameters[0].regex)){
            Toast.makeText(requireContext(), "Please enter valid "+billerDetail!!.parameters[0].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam2  && mandate2 && binding!!.param2Edt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter "+billerDetail!!.parameters[1].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam2 && !Common.isRegexValid(binding!!.param2Edt.text.toString(),billerDetail!!.parameters[1].regex)){
            Toast.makeText(requireContext(), "Please enter valid "+billerDetail!!.parameters[1].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam3 && binding!!.param3Edt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter "+billerDetail!!.parameters[2].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam3 && !Common.isRegexValid(binding!!.param3Edt.text.toString(),billerDetail!!.parameters[2].regex)){
            Toast.makeText(requireContext(), "Please enter valid "+billerDetail!!.parameters[2].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam4 && binding!!.param4Edt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Please enter "+billerDetail!!.parameters[3].desc, Toast.LENGTH_SHORT).show()
        }else if(isParam4 && !Common.isRegexValid(binding!!.param4Edt.text.toString(),billerDetail!!.parameters[3].regex)){
            Toast.makeText(requireContext(), "Please enter valid "+billerDetail!!.parameters[3].desc, Toast.LENGTH_SHORT).show()
        }else if(mLatitude==null){
            Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        }else{
            Common.hideSoftKeyboard(requireActivity())
            fetchBillApiCall()
        }
    }

    private fun fetchBillApiCall() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = BillpayFetchBillRequest()
        request.AgentCode = loginModel.Data.DoneCardUser
        request.BillerId = billerId
        request.Latitude = mLatitude
        request.Longitude = mLongitude
        request.IPAddress = ip
        request.Param1 = binding!!.param1Edt.text.toString()
        request.Param2 = binding!!.param2Edt.text.toString()
        request.Param3 = binding!!.param3Edt.text.toString()
        request.Param4 = binding!!.param4Edt.text.toString()

        if(isFetchBillRequirment){
            NetworkCall().callService(
                NetworkCall.getInstaBillpayInterface().getInstaBillpayHeader(
                    ApiConstants.FetchBill,request,commonParams!!.userData,commonParams!!.token),
                context, true
            ) { response: ResponseBody?, responseCode: Int ->
                if (response != null) {
                    responseHandlerBillFetch(response, request)
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else{
            request.OperatorName=billerName
            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                InstaBillPayDirectFragment.newInstance(commonParams!!, request, billType))
        }
    }

    private fun responseHandlerBillFetch(response: ResponseBody, request: BillpayFetchBillRequest) {
        try {
            val response = Gson().fromJson(response.string(), BillFetchResponse::class.java)
            if (response != null) {
                if(response.StatusCode.equals("00")){
                    if(response.data!=null){
                        Toast.makeText(context, "Bill Fetched", Toast.LENGTH_SHORT).show()
                        request.OperatorName=billerName
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                            InstaBillPayFragment.newInstance(commonParams!!, response.data, request, billType))
                    }else{
                        Toast.makeText(context, "Bill fetched with error", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Common.showCommonAlertDialog(requireContext(), response.StatusMessage, "Api Response")
                }
            } else {
                Toast.makeText(context, "bill not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun getBillerDetails() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = BillpayBillerDetailsRequest()
        request.AgentCode = loginModel.Data.DoneCardUser
        request.BillerId = billerId
        request.Latitude = mLatitude
        request.Longitude = mLongitude
        request.IPAddress = ip
        NetworkCall().callService(
            NetworkCall.getInstaBillpayInterface().getInstaBillpayHeader(
                ApiConstants.BillerDetails,request,commonParams!!.userData,commonParams!!.token),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerBillerDetails(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    var billerDetail:BillpayBillerDetailsResponse.data?=null
    private fun responseHandlerBillerDetails(response: ResponseBody) {
        try {
            val response = Gson().fromJson(response.string(), BillpayBillerDetailsResponse::class.java)
            if (response != null && response.StatusCode.equals("00")) {
                if(response.data!=null){
                    Toast.makeText(context, "Biller details Fetched", Toast.LENGTH_SHORT).show()
                    billerDetail=response.data
                    setInputValues()
                }else{
                    Toast.makeText(context, "Biller details found with error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "biller details not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private var mandate1=false
    private var mandate2=false
    private var mandate3=false
    private var mandate4=false
    private var isParam1=false
    private var isParam2=false
    private var isParam3=false
    private var isParam4=false
    private var isFetchBillRequirment=true
    private fun setInputValues() {
        binding!!.fetchbillTv.isEnabled=true
        binding!!.fetchbillTv.alpha=1f

        paramVisibilityGone()
        if(billerDetail!!.fetchRequirement.equals("MANDATORY") || billerDetail!!.supportValidation.equals("MANDATORY")){
            isFetchBillRequirment=true
        }else{
            isFetchBillRequirment=false
        }

        if(billerDetail!!.parameters.size==1){
            binding!!.param1.visibility=View.VISIBLE
            param1Value(billerDetail!!.parameters[0])
        }else if(billerDetail!!.parameters.size==2){
            binding!!.param1.visibility=View.VISIBLE
            binding!!.param2.visibility=View.VISIBLE
            param1Value(billerDetail!!.parameters[0])
            param2Value(billerDetail!!.parameters[1])
        }else if(billerDetail!!.parameters.size==3){
            binding!!.param1.visibility=View.VISIBLE
            binding!!.param2.visibility=View.VISIBLE
            binding!!.param3.visibility=View.VISIBLE
            param1Value(billerDetail!!.parameters[0])
            param2Value(billerDetail!!.parameters[1])
            param3Value(billerDetail!!.parameters[2])
        }else if(billerDetail!!.parameters.size==4){
            binding!!.param1.visibility=View.VISIBLE
            binding!!.param2.visibility=View.VISIBLE
            binding!!.param3.visibility=View.VISIBLE
            binding!!.param4.visibility=View.VISIBLE
            param1Value(billerDetail!!.parameters[0])
            param2Value(billerDetail!!.parameters[1])
            param3Value(billerDetail!!.parameters[2])
            param4Value(billerDetail!!.parameters[3])
        }
    }

    private fun paramVisibilityGone() {
        binding!!.param1.visibility=View.GONE
        binding!!.param2.visibility=View.GONE
        binding!!.param3.visibility=View.GONE
        binding!!.param4.visibility=View.GONE
    }

    private fun param1Value(parameters: BillpayBillerDetailsResponse.parameters?) {
        isParam1=true
        binding!!.param1.hint = "Please enter "+ parameters!!.desc
        binding!!.param1Edt.filters = (binding!!.param1Edt.filters ?: emptyArray()) +
                InputFilter.LengthFilter(parameters.maxLength.toInt())
        if(parameters.mandatory==1){
            mandate1=true
        }
        if(parameters.inputType.equals("NUMERIC")){
            binding!!.param1Edt.inputType = InputType.TYPE_CLASS_NUMBER
        }else{
            binding!!.param1Edt.inputType = InputType.TYPE_CLASS_TEXT
        }
        binding!!.param2Edt.setText("")
        binding!!.param3Edt.setText("")
        binding!!.param4Edt.setText("")
    }
    private fun param2Value(parameters: BillpayBillerDetailsResponse.parameters?) {
        isParam2=true
        binding!!.param2.hint = "Please enter "+ parameters!!.desc
        binding!!.param2Edt.filters = (binding!!.param2Edt.filters ?: emptyArray()) +
                InputFilter.LengthFilter(parameters.maxLength.toInt())
        if(parameters.mandatory==1){
            mandate2=true
        }
        if(parameters.inputType.equals("NUMERIC")){
            binding!!.param2Edt.inputType = InputType.TYPE_CLASS_NUMBER
        }else{
            binding!!.param2Edt.inputType = InputType.TYPE_CLASS_TEXT
        }
        binding!!.param3Edt.setText("")
        binding!!.param4Edt.setText("")
    }
    private fun param3Value(parameters: BillpayBillerDetailsResponse.parameters?) {
        isParam3=true
        binding!!.param3.hint = "Please enter "+ parameters!!.desc
        binding!!.param3Edt.filters = (binding!!.param3Edt.filters ?: emptyArray()) +
                InputFilter.LengthFilter(parameters.maxLength.toInt())
        if(parameters.mandatory==1){
            mandate3=true
        }
        if(parameters.inputType.equals("NUMERIC")){
            binding!!.param3Edt.inputType = InputType.TYPE_CLASS_NUMBER
        }else{
            binding!!.param3Edt.inputType = InputType.TYPE_CLASS_TEXT
        }
        binding!!.param4Edt.setText("")
    }
    private fun param4Value(parameters: BillpayBillerDetailsResponse.parameters?) {
        isParam4=true
        binding!!.param4.hint = "Please enter "+ parameters!!.desc
        binding!!.param4Edt.filters = (binding!!.param4Edt.filters ?: emptyArray()) +
                InputFilter.LengthFilter(parameters.maxLength.toInt())
        if(parameters.mandatory==1){
            mandate4=true
        }
        if(parameters.inputType.equals("NUMERIC")){
            binding!!.param4Edt.inputType = InputType.TYPE_CLASS_NUMBER
        }else{
            binding!!.param4Edt.inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    private fun getOperatorList() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = BillpayOperatorListRequest()
        request.AgentCode = loginModel.Data.DoneCardUser
        request.Category = billType
        NetworkCall().callService(
            NetworkCall.getInstaBillpayInterface().getInstaBillpayHeader(
                ApiConstants.Operatorlist,request,"",""),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerOperatorList(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private var operatorArray=ArrayList<BillpayOperatorList.data>()
    private fun responseHandlerOperatorList(response: ResponseBody) {
        try {
            val response = Gson().fromJson(response.string(), BillpayOperatorList::class.java)
            if (response != null && response.statusCode.equals("00")) {
                Toast.makeText(context, "operator List Fetched", Toast.LENGTH_SHORT).show()
                operatorArray!!.addAll(response.data)
                setAdapter()
            } else {
                Toast.makeText(context, "opertaor list not fetched", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun setAdapter() {
        val arr = arrayOfNulls<String>(operatorArray.size)
//        arr[0]="[ Select-operator ]"
        for (p in operatorArray.indices) {
            arr[p] = operatorArray[p].billername
        }
        binding!!.atvOperator.setAdapter(getSpinnerAdapter(arr))
        binding!!.atvOperator.showDropDown()
    }


    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter<String?>(requireContext(), R.layout.mobile_operator_spinner_item,
            R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
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
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Latitude"] = mLatitude!!
        params["Lattitude"] = mLatitude!!
        params["Longitude"] = mLongitude!!
        params["Mode"] = "App"
        params["Merchant"] = ApiConstants.MerchantId
        params["IPAddress"] = ip!!
        params["Ip"] = ip!!
        params["CustomerName"] = ""
        params["DeviceId"] = Common.getDeviceId(requireContext())

        NetworkCall().callService(
            NetworkCall.getAepsInterface().getInstaAepsHeaderMap(
                "", params,
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
                    openReceipt(commonResponse)
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
    }

    var responseData="{\n" +
            "  \"StatusCode\": \"00\",\n" +
            "  \"StatusMessage\": \"Bill Details Fetch Successfully\",\n" +
            "  \"data\": {\n" +
            "    \"enquiryReferenceId\": \"TSB3ea4f35a3c474cf0b7070d4861591307\",\n" +
            "    \"CustomerName\": \"BBPS\",\n" +
            "    \"BillNumber\": \"12345678\",\n" +
            "    \"BillPeriod\": \"JUN\",\n" +
            "    \"BillDate\": \"08/06/2026\",\n" +
            "    \"BillDueDate\": \"30/06/2026\",\n" +
            "    \"BillAmount\": \"0.01\",\n" +
            "    \"CustomerParamsDetails\": [],\n" +
            "    \"BillDetails\": []\n" +
            "  }\n" +
            "}\n"


}