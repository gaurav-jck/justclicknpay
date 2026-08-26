package com.justclick.clicknbook.Fragment.jctmoney.instapay

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.KycData
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.DmtKycResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaMerchantKycBinding
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
import java.io.StringWriter
import java.net.URL
import java.util.Scanner
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.random.Random


class InstaMerchantKycFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val FINGER_CAPTURE = "Finger"
    private val FACE_CAPTURE = "Face"
    private final val CAPTURE_REQUEST_CODE = 123
    private val MANTRA_L1 = "MANTRA_L1"
    private val STARTEK_L1 = "STARTEK_L1"
    private val MORPHO_L1 = "MORPHO_L1"
    private val MANTRA_L1_PACKAGE = "com.mantra.mfs110.rdservice"
    private val STARTEK_PACKAGE_L1 = "com.acpl.registersdk_l1"
    private val MORPHO_PACKAGE_L1 = "com.idemia.l1rdservice"
    private val deviceArray = arrayOf("Mantra", "Morpho", "Startek (Access)")
    var d_type = MANTRA_L1
    var pidDataXML = "";
    private var captureType=FINGER_CAPTURE
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var kycData: KycData? = null
    private var binding: FragmentInstaMerchantKycBinding?=null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
                kycData = it.getSerializable(ARG_PARAM2, KycData::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
                kycData = it.getSerializable(ARG_PARAM2) as KycData?
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentInstaMerchantKycBinding.inflate(layoutInflater)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        getIpAddress()
        binding!!.btnCapture.setOnClickListener {
            if(validateFields()){
                captureData()
            }

        }
        binding!!.btnFaceCapture.setOnClickListener {
            if(validateFields()){
                captureFaceData()
            }

        }

        binding!!.spinnerDeviceType.adapter= Common.getSpinnerAdapter(deviceArray, requireContext())
        binding!!.spinnerDeviceType.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                when (i) {
                    0 -> d_type = MANTRA_L1
                    1 -> d_type = MORPHO_L1
                    2 -> d_type = STARTEK_L1
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        checkLocationPermission()
        return binding!!.root
    }

    private fun validateFields(): Boolean {
        if(binding!!.aadharEdt.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Please enter aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.aadharEdt.text.toString().trim().length<12) {
            Toast.makeText(context, "Please enter valid aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (mLatitude==null) {
            Toast.makeText(context, "Please enable device location", Toast.LENGTH_SHORT).show();
            getLastLocation()
            return false;
        }
        return true
    }

    fun captureData() {
        try {
            if (d_type == STARTEK_L1) {
                val pidOptXML =getPIDOptionsPay()
                capture(STARTEK_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == MANTRA_L1) {
                val pidOptXML =getPIDOptionsPay()
                capture(MANTRA_L1_PACKAGE, pidOptXML, CAPTURE_REQUEST_CODE)
            }else if (d_type == MORPHO_L1) {
                val pidOptXML =getPIDOptionsPay()
                capture(MORPHO_PACKAGE_L1, pidOptXML, CAPTURE_REQUEST_CODE)
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
        return kycData!!.pidOptionWadh
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

    private fun getPIDOptionsPay(): String {
        return "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" wadh=\"${kycData!!.pidOptionWadh}\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
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
            .setTitle("Please confirm your KYC")
            .setMessage("Do you want to do KYC with given details!")
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
        var loginModel= MyPreferences.getLoginData(LoginModel(), context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["Aadhar"] = binding!!.aadharEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["latitude"] =mLatitude!!
        params["longitude"] = mLongitude!!
        params["captureType"] = captureType
        params["Mode"] = "App"
        params["MerchantId"] = ApiConstants.MerchantId
        params["referencekey"] = kycData!!.referenceKey
        params["IPAddress"] = ip!!

        pidDataXML=pidDataXML.replace("\n", "")
        params["pid"]=pidDataXML

        NetworkCall().callService(
            NetworkCall.getDmtInstaApiInterface().getInstaHeaderMap(
                ApiConstants.agentekyc, params,
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
            val commonResponse = Gson().fromJson(response.string(), DmtKycResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                } else {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: CheckCredentialResponse.credentialData, param2: KycData) =
            InstaMerchantKycFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                }
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