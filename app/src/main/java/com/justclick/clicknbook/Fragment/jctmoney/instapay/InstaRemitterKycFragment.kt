package com.justclick.clicknbook.Fragment.jctmoney.instapay

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.justclick.clicknbook.Activity.NavigationDrawerActivity

import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.FragmentTags
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaRemitterKycBinding
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

class InstaRemitterKycFragment : Fragment() {
    private val FINGER_CAPTURE = "FINGER"
    private val FACE_CAPTURE = "FACE"
    private final val CAPTURE_REQUEST_CODE = 123
    var d_type = AepsConstants.MANTRA;
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var remittanceResponse: SenderDetailResponse? = null
    private var binding: FragmentInstaRemitterKycBinding?=null
    private var senderMobile:String?=null
    private var bankName:String?=null
    private var pidWadh:String?=null
    private var stateresp:String?=null
    private var aadharNumber:String?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var commonParams: CheckCredentialResponse.credentialData? = null

    companion object {
        fun newInstance(mobile: String, stateresp: String, pidWadh: String?, aadharNumber: String): InstaRemitterKycFragment {
            var bundle=Bundle()
            bundle.putSerializable("commonParams", mobile)
            bundle.putString("mobile", mobile)
            bundle.putString("stateRes", stateresp)
            bundle.putString("pidWadh", pidWadh)
            bundle.putString("aadhar", aadharNumber)
            bundle.putString("bankName", aadharNumber)
            val fragment = InstaRemitterKycFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = requireArguments().getSerializable("commonParams", CheckCredentialResponse.credentialData::class.java)
            }else{
                commonParams = requireArguments().getSerializable("commonParams") as CheckCredentialResponse.credentialData?
            }
            senderMobile=requireArguments().getString("mobile")
            bankName=requireArguments().getString("bankName")
            stateresp=requireArguments().getString("stateRes")
            pidWadh=requireArguments().getString("pidWadh")
            aadharNumber=requireArguments().getString("aadhar")
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentInstaRemitterKycBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)

        binding!!.aadharEdt.setText(aadharNumber)
        binding!!.mobileEdt.setText(senderMobile)
        binding!!.btnCapture.setOnClickListener {
           if(mLatitude==null){
                Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
                checkLocationPermission()
            }else{
                captureData()
            }
        }
        getIpAddress()
        checkLocationPermission()
        binding!!.btnFaceCapture.setOnClickListener {
            if(mLatitude==null){
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

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding!!.root
    }

    fun captureData() {
        try {
            if (d_type == AepsConstants.MANTRA) {
                val pidOptXML: String = getPIDOptions()
                capture(AepsConstants.MANTRA_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            } else if (d_type == AepsConstants.MORPHO) {
                val pidOptXML: String = getPIDOptions()
                capture(AepsConstants.MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else{
                val pidOptXML: String = getPIDOptions()
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
//    https://developer.android.com/develop/background-work/services/aidl#kotlin

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

    private fun getPIDOptions(): String {
        return "<?xml version=\"1.0\"?><PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"${pidWadh}\" posh=\"UNKNOWN\" env=\"P\" /><CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts></PidOptions>"
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
        sendMobileTransaction()
//        responseHandler(null)
    }

    private fun sendMobileTransaction() {
        var loginModel= MyPreferences.getLoginData(LoginModel(), context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Mode"] = "App"
        params["MerchantId"] = ApiConstants.MerchantId
        params["Mobile"] = senderMobile!!
        params["StateResp"] = stateresp!!
        params["captureType"] = captureType
        params["BankName"] = bankName!!
        params["IPAddress"] = ip!!
        params["Latitude"] = mLatitude
        params["Longitude"] = mLongitude

        pidDataXML=pidDataXML.replace("\n", "")
        params["Pid"]=pidDataXML

        NetworkCall().callService(
            NetworkCall.getDmtInstaApiInterface().getInstaHeaderMap(
                ApiConstants.registerRemitterekyc, params,
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

    private fun responseHandler(response: ResponseBody?) {
        try {
            val stringResponse = response?.string()
            val commonResponse = Gson().fromJson(stringResponse, SenderDetailResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    showApiAlertDialog("You have successfully completed your KYC, please proceed for transaction", "KYC Response", commonResponse)
                } else {
                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "KYC Response")

                }
            }else{
                Toast.makeText(context, "Response failure", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "exception occurs\n"+e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showApiAlertDialog(
        messageTxt: String,
        argTitle: String,
        commonResponse: SenderDetailResponse
    ) {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(argTitle)
            .setMessage(messageTxt)
            .setPositiveButton(
                "OK"
            ) {
                    dialog, which -> dialog.cancel()
                val bundle = Bundle()
                bundle.putSerializable("senderResponse", commonResponse)
                bundle.putSerializable("commonParams", commonParams)
                bundle.putString("bankName", bankName)
                val senderDetailFragment = InstaSenderDetailFragment()
                senderDetailFragment.arguments = bundle
                (context as NavigationDrawerActivity).replaceFragmentWithTagNoBackStack(
                    senderDetailFragment,
                    FragmentTags.InstaSenderDetailFragment
                )
            }
            .show()
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

    private var mLatitude="27.112232"
    private var mLongitude="77.21211"
    private fun getLastLocation() {
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

    var responseData="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Success\",\n" +
            "    \"sessionRefId\": null,\n" +
            "    \"bank1\": 0,\n" +
            "    \"type1\": null,\n" +
            "    \"bank2\": 0,\n" +
            "    \"type2\": null,\n" +
            "    \"bank3\": 0,\n" +
            "    \"type3\": null,\n" +
            "    \"sessionKey\": null,\n" +
            "    \"requestFor\": null,\n" +
            "    \"remainingLimit\": 25000,\n" +
            "    \"senderDetailInfo\": [\n" +
            "        {\n" +
            "            \"dob\": \"N/A\",\n" +
            "            \"gender\": \"Male\",\n" +
            "            \"mobile\": \"9326891941\",\n" +
            "            \"name\": \"Jyoti - Dave\",\n" +
            "            \"pin\": \"\",\n" +
            "            \"city\": \"\",\n" +
            "            \"state\": \"N/A\",\n" +
            "            \"stateResp\": \"jgm8e+OwiIevYpGT7DwYnFN/eXXHPh5q+PQ7OlPXdcO1LwirNgRuo9/joo7506nG\",\n" +
            "            \"pidOptionWadh\": \"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\",\n" +
            "            \"ekyc_id\": \"\",\n" +
            "            \"limit\": \"25000.00\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"benificiaryDetailData\": null\n" +
            "}"
}