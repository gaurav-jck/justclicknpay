package com.jck.myjckapp.ui.fragments.aeps

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.billpay.BillConfirmationFragment
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillFetchResponse
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayBillerDetailsResponse
import com.justclick.clicknbook.Fragment.billpayinsta.models.BillpayFetchBillRequest
import com.justclick.clicknbook.Fragment.billpayinsta.models.InstaPaybillRequest
import com.justclick.clicknbook.Fragment.billpayinsta.models.PayBillInstaResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaPayBillBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.Constants.BillPay
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import java.net.URL
import java.util.Scanner

class InstaBillPayFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_PARAM3 = "param3"
    private val ARG_PARAM4 = "param4"
    private var binding: FragmentInstaPayBillBinding?=null
    private var billType=BillPay.Electricity
    private lateinit var loginResponse:LoginModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var billDetails: BillFetchResponse.data? = null
    private var billerRequest: BillpayFetchBillRequest? = null

    companion object {
        fun newInstance(
            param1: CheckCredentialResponse.credentialData,
            billDetails: BillFetchResponse.data,
            request: BillpayFetchBillRequest,
            type: String
        ) =
            InstaBillPayFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, billDetails)
                    putSerializable(ARG_PARAM3, request)
                    putString(ARG_PARAM4, type)
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginResponse=MyPreferences.getLoginData(LoginModel(),context)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
                billDetails = it.getSerializable(ARG_PARAM2, BillFetchResponse.data::class.java)
                billerRequest = it.getSerializable(ARG_PARAM3, BillpayFetchBillRequest::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
                billDetails = it.getSerializable(ARG_PARAM2) as BillFetchResponse.data?
                billerRequest = it.getSerializable(ARG_PARAM3) as BillpayFetchBillRequest?
            }
            billType= requireArguments().getString(ARG_PARAM4).toString()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentInstaPayBillBinding.inflate(layoutInflater)

        binding!!.topView.titleTv.text= "$billType Bill Pay"
        binding!!.personDetailTv.text= "$billType Bill Detail"

        binding!!.nameEdt.setText(billDetails!!.CustomerName)
        binding!!.billNoEdt.setText(billDetails!!.BillNumber)
        binding!!.dueDateEdt.setText(billDetails!!.BillDueDate)
        binding!!.amountEdt.setText(billDetails!!.BillAmount)

        binding!!.fetchbillTv.setOnClickListener {
            fetchBillDetails()
        }

        getIpAddress()
        checkLocationPermission()

        binding!!.topView.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding!!.cancelTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding!!.root
    }

    private fun fetchBillDetails() {
        var amount=binding!!.amountEdt.text.toString()
        if(amount.isEmpty()){
            Toast.makeText(requireContext(), "Enable to pay this bill", Toast.LENGTH_SHORT).show()
        }else if(!Common.isdecimalvalid(amount)){
            Toast.makeText(requireContext(), "Amount is not valid for this bill", Toast.LENGTH_SHORT).show()
        }else if(amount.toFloat()<3 || amount.toFloat()>1500000){
            Toast.makeText(requireContext(), "Amount should be between 3 to 1500000", Toast.LENGTH_SHORT).show()
        }else if(mLatitude==null){
            Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        }else{
            Common.hideSoftKeyboard(requireActivity())
            showTransactionAlert()
        }
    }

    var loginModel = LoginModel()
    private fun payBill() {
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = InstaPaybillRequest()
        request.AgentCode = loginModel.Data.DoneCardUser
        request.BillerId = billerRequest!!.BillerId
        request.Latitude = mLatitude
        request.Longitude = mLongitude
        request.IPAddress = ip
        request.Param1 = billerRequest!!.Param1
        request.Param2 = billerRequest!!.Param2
        request.Param3 = billerRequest!!.Param3
        request.Param4 = billerRequest!!.Param4
        request.OperatorName = billerRequest!!.OperatorName
        request.Category = billType
        request.EnquiryReferenceId = billDetails!!.enquiryReferenceId
        request.BillDate = billDetails!!.BillDate
        request.DueDate = billDetails!!.BillDueDate
        request.CustomerName = billDetails!!.CustomerName
        request.Amount = billDetails!!.BillAmount
        NetworkCall().callService(
            NetworkCall.getInstaBillpayInterface().getInstaBillpayHeader(
                ApiConstants.PayBill,request,commonParams!!.userData,commonParams!!.token),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerBillPay(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerBillPay(response: ResponseBody) {
        try {
            val response = Gson().fromJson(response.string(), PayBillInstaResponse::class.java)
            if (response != null) {
                if(response.StatusCode.equals("00")){
                    if(response.billDetails!=null){
                        Toast.makeText(context, "Bill Pay success", Toast.LENGTH_SHORT).show()
//                        Common.showSuccessDialog(requireContext(),response.StatusMessage)
                        openReceipt(response)
                    }else{
                        Toast.makeText(context, "Bill pay with error", Toast.LENGTH_SHORT).show()
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
                payBill()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, i -> dialog.cancel() }
            .show()
    }

    private fun openReceipt(responseModel: PayBillInstaResponse) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.lic_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val agentCodeTv = dialog.findViewById<TextView>(R.id.agentCodeTv)
        val txnIdTv = dialog.findViewById<TextView>(R.id.txnIdTv)
        val title = dialog.findViewById<TextView>(R.id.title)
        val policyTv = dialog.findViewById<TextView>(R.id.policyTv)
        val policyNoTv = dialog.findViewById<TextView>(R.id.policyNoTv)
        val nameTv = dialog.findViewById<TextView>(R.id.nameTv)
        val amountTv = dialog.findViewById<TextView>(R.id.amountTv)
        val operatorIdTv = dialog.findViewById<TextView>(R.id.operatorIdTv)
        val statusTv = dialog.findViewById<TextView>(R.id.statusTv)
        title.setText("$billType Bill Receipt")
        policyTv.text = "Acknowledge no"
        policyNoTv.text = responseModel.billDetails[0].acknowledgementNo
        txnIdTv.text = responseModel.billDetails[0].transactionId
        operatorIdTv.text = responseModel.billDetails[0].operatotrId
        statusTv.text = responseModel.billDetails[0].status
        amountTv.text = responseModel.billDetails[0].amount.toString() + ""
        agentCodeTv.setText(loginModel.Data.DoneCardUser)
        nameTv.setText(billDetails!!.CustomerName)


        dialog.findViewById<View>(R.id.back_tv).setOnClickListener { view: View? ->
            dialog.dismiss()
        }

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

    var billResponse="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"You Bill Payment for BSES Yamuna Power Limited of Amount 12 is successful.\",\n" +
            "    \"billDetails\": [\n" +
            "        {\n" +
            "            \"transactionId\": \"B260719HC4JC0A13387\",\n" +
            "            \"acknowledgementNo\": \"5013\",\n" +
            "            \"operatotrId\": \"MockLazYafrYZ7\",\n" +
            "            \"amount\": 12.0,\n" +
            "            \"status\": \"Success\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";


}