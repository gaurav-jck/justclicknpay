package com.jck.myjckapp.ui.fragments.aeps

import android.Manifest
import android.app.Activity
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
import android.widget.AdapterView
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
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentAepsDailyLoginBinding
import com.justclick.clicknbook.jctPayment.newaeps.AepsConstants
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.random.Random


class AepsDailyLoginFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private val FINGER_CAPTURE = "Finger"
    private val FACE_CAPTURE = "Face"
    private final val CAPTURE_REQUEST_CODE = 123
    var d_type = AepsConstants.MANTRA
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var binding: FragmentAepsDailyLoginBinding?=null
    private var txnType:Int?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null

    companion object {
        fun newInstance(param1: CheckCredentialResponse.credentialData) =
            AepsDailyLoginFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAepsDailyLoginBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        binding!!.topView.titleTv.text = "AEPS Daily Login"
        binding!!.btnCapture.setOnClickListener {
            if(binding!!.aadharEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter your Aadhar number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.aadharEdt.text.toString().length<12){
                Toast.makeText(requireContext(), "Please enter valid Aadhar number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.mobileEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter your Mobile number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.mobileEdt.text.toString().length<10){
                Toast.makeText(requireContext(), "Please enter valid Mobile number", Toast.LENGTH_SHORT).show()
            }else if(mLatitude==null){
                Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
                checkLocationPermission()
            }else{
                captureData()
            }
        }
        getIpAddress()
        checkLocationPermission()

        binding!!.btnCaptureFace.setOnClickListener {
            if(binding!!.aadharEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter your Aadhar number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.aadharEdt.text.toString().length<12){
                Toast.makeText(requireContext(), "Please enter valid Aadhar number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.mobileEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter your Mobile number", Toast.LENGTH_SHORT).show()
            }else if(binding!!.mobileEdt.text.toString().length<10){
                Toast.makeText(requireContext(), "Please enter valid Mobile number", Toast.LENGTH_SHORT).show()
            }else if(mLatitude==null){
                Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
                checkLocationPermission()
            }else{
                captureFaceData()
            }
        }
        binding!!.rdLinear.faceTv.setOnClickListener {
            Common.openDownloadLink(requireContext(),AepsConstants.FACE_RD_PACKAGE)
        }
        binding!!.rdLinear.mantraTv.setOnClickListener {
            Common.openDownloadLink(requireContext(),AepsConstants.MANTRA_PACKAGE_L1)
        }
        binding!!.rdLinear.morphoTv.setOnClickListener {
            Common.openDownloadLink(requireContext(),AepsConstants.MORPHO_PACKAGE_L1)
        }
        binding!!.rdLinear.startekTv.setOnClickListener {
            Common.openDownloadLink(requireContext(),AepsConstants.STARTEK_PACKAGE_L1)
        }

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

        return binding!!.root
    }

    fun captureData() {
        try {
             if (d_type == AepsConstants.MANTRA) {
                val pidOptXML=getPidOptions()
                capture(AepsConstants.MANTRA_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.MORPHO) {
                val pidOptXML=getPidOptions()
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.STARTEK) {
                val pidOptXML=getPidOptions()
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
                        readXMLData(pidDataXML, FINGER_CAPTURE)
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
                            readXMLData(pidDataXML, FACE_CAPTURE)
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

    fun captureFaceData() {
        try {
            val intent = Intent("in.gov.uidai.rdservice.face.CAPTURE")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

//            Common.showCommonAlertDialog(requireContext(), createPidOptionForKUA(getRandomNumber(), "P"),"Face XML")
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
//        return "sgydIC09zzy6f8Lb3xaAqzKquKe9lFcNR9uTvYxFp+A="
        return ""
    }
    val LANGUAGE=""
    fun createPidOptionForKUA(txnId: String, buildType:String): String {
        return createPidOptionsKUA(txnId, "auth", getWADH2(), buildType)
    }
    private fun createPidOptionsKUA(txnId: String, purpose: String, wadh:String, buildType:String): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PidOptions ver=\"1.0\" env=\"${buildType}\">\n" +
                "   <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" iType=\"0\" pCount=\"0\" pType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"\" otp=\"\" wadh=\"${wadh}\" posh=\"\" />\n" +
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
        try{
            sendMobileTransaction()
        }catch(e:Exception){
            Common.showCommonAlertDialog(context, e.message+"\n"+e.toString()+"\n"+e.printStackTrace(), "Exception data")
        }
    }

    private fun sendMobileTransaction() {
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        var loginModel= MyPreferences.getLoginData(LoginModel(), context)
        val params: MutableMap<String, String> = HashMap()
        params["AadharNumber"] = binding!!.aadharEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Mobile"] = binding!!.mobileEdt.text.toString()
        params["Lattitude"] = mLatitude!!
        params["Longitude"] = mLongitude!!
        params["CaptureType"] = captureType
        params["Mode"] = "App"
        params["Merchant"] = ApiConstants.MerchantId
        params["IPAddress"] = ip!!

        pidDataXML=pidDataXML.replace("\n", "")
        params["Pid"]=pidDataXML

        NetworkCall().callService(
            NetworkCall.getAepsInterface().getInstaAepsHeaderMap(
                ApiConstants.Authenticity, params,
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
            val commonResponse = Gson().fromJson(response.string(), PaytmWalletFragment.CommonResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Common.showCommonAlertDialog(context,commonResponse.statusMessage,"Api Response")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error occurred", Toast.LENGTH_LONG).show()
        }
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
        }catch (e:Exception){

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

    private var callLoc=1
    override fun onResume() {
        super.onResume()
        callMethod()
    }

    private fun callMethod() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // Call your function here
                if(mLatitude==null){
                    callLoc++
                    getLastLocation()
//                    binding!!.btnCaptureFace.text = "Capture Face Data $callLoc"
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
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Kyc completed and OTP has been sent to remitter mobile number.\",\n" +
            "    \"data\": {\n" +
            "        \"fname\": \"NA\",\n" +
            "        \"lname\": \"NA\",\n" +
            "        \"mobile\": \"7452016171\",\n" +
            "        \"ekyc_id\": \"718266\",\n" +
            "        \"stateresp\": \"468343571\"\n" +
            "    }\n" +
            "}"
}