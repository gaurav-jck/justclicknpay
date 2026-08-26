package com.justclick.clicknbook.jctPayment.newaeps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.ActivityAepsKyc2Bank3Binding
import com.justclick.clicknbook.jctPayment.Models.GetAadharRequest
import com.justclick.clicknbook.jctPayment.Models.GetAadharResponse
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


class AepsKyc2Bank3Fragment(aepsPipe: String) : Fragment() {
    //https://developers.google.com/identity/sign-in/android/sign-in
    private val BAL_ENQ = 1
    private val CAPTURE_REQUEST_CODE = 123
    private val FINO_AEPS_CODE: Int = 12
    private var context: Context? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    var str_aadhar: String? = null
    var mobileNo: kotlin.String? = null
    var pidDataXML = ""
    var d_type = AepsConstants.MANTRA
    var adharType: kotlin.String? = AepsConstants.ADHAR_UID
    var TYPE = BAL_ENQ
    var aepsPipe = aepsPipe
    var wadh="18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw="
    private var isGetAgain = false
    val PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLatitude = "29.9319558"
    var mLongitude= "77.5334789"
    lateinit var binding:ActivityAepsKyc2Bank3Binding

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding= ActivityAepsKyc2Bank3Binding.inflate(inflater)
        binding.rbVirtualId.setVisibility(View.GONE)
        binding.txtAadharno.setText(MyPreferences.getAgentAdhar(context))
//        binding.txtMobileno.setText(MyPreferences.getAgentMobile(context))
        if (binding.txtAadharno.text.toString().length == 12) {
            showCapture()
        } else {
            hideCapture()
        }

        if(aepsPipe.equals("bank2")){
            wadh="18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw="
        }else{
            wadh="E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc="
        }

        binding.btnCapture.setOnClickListener { v: View? ->
            Common.preventFrequentClick(binding.btnCapture)
            str_aadhar = binding.txtAadharno.text.toString().trim { it <= ' ' }
            Common.hideSoftKeyboard(requireActivity())
            if (!isGetAgain) {
                checkAepsCredential()
            } else {
                captureData();
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

        getAdharNumber()

        binding.backArrow.setOnClickListener( {
            getParentFragmentManager().popBackStack();
        });
        return binding.root
    }

    private fun getAdharNumber() {
        val loginModel = LoginModel()
        val request = GetAadharRequest()
        request.AgentCode = MyPreferences.getLoginData(loginModel, context).Data.DoneCardUser

        val json = Gson().toJson(request)

        NetworkCall().callService(
            NetworkCall.getAepsInterface().aepsPostServiceN(URLs.getagentadhar, request),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response)
            } else {
//                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                GetAadharResponse::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    binding.txtAadharno.setText(senderResponse.adharno)
//                    binding.txtMobileno.setText(senderResponse.mobileno)
                }
            } else {
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        } catch (e: Exception) {
//            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
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
            "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" wadh=\"${wadh}\" env=\"P\" />" + "" + "<CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
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
//                        captureType=FINGER_CAPTURE
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
//                            captureType=FACE_CAPTURE
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
        binding.btnCapture.isEnabled = false
        binding.btnCapture.setTextColor(getResources().getColor(R.color.black_text_color))
        binding.btnCapture.setBackgroundResource(R.color.gray_color)
        binding.btnCapture.alpha = 0.4f
    }

    private fun showCapture() {
        binding.btnCapture.isEnabled = true
        binding.btnCapture.setTextColor(getResources().getColor(R.color.color_white))
        binding.btnCapture.setBackgroundResource(R.drawable.button_shep)
        binding.btnCapture.alpha = 1f
        binding.txtAadharno.error = null
    }

    private fun capture(packageName: String, pidOptXML: String, requestCode: Int) {
//        sessionCheckMethod(false);
        val selectedPackage = packageName
        val intent1 = Intent("in.gov.uidai.rdservice.fp.CAPTURE", null)
        //String pidOptXML = getPIDOptions(); //working
        intent1.putExtra("PID_OPTIONS", pidOptXML)
        intent1.setPackage(selectedPackage)
        startForResult.launch(intent1)
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
        val params: MutableMap<String, String> = HashMap()
        params["AgentCode"] =
            MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        params["Merchant"] = ApiConstants.MerchantId
        params["Mode"] = "APP"
        params["AadharNumber"] = str_aadhar!!
        params["pipe"] = aepsPipe
        params["bankPipe"] = aepsPipe
        params["Transactionid"] = str_aadhar!!
        params["Latitude"] = mLatitude
        params["Longitude"] = mLongitude
//        pidDataXML="<?xml version=\"1.0\"?>"+pidDataXML
        pidDataXML= "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>$pidDataXML"
        pidDataXML=pidDataXML.replace("\n", "")
        params["PId"] = pidDataXML

//        binding.pid.setText(pidDataXML)

        val apiService = APIClient.getClient(ApiConstants.BASE_URL_AEPS).create(ApiInterface::class.java)
        val call = apiService.getAepsHeaderMap(URLs.bank3ekyc, params, MyPreferences.getUserData(context),
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
            val senderResponse = Gson().fromJson(response.string(), AepsResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse != null && senderResponse.statusCode.equals("00", ignoreCase = true)) {
                    Common.showCommonAlertDialog(context, senderResponse.statusMessage, "Api Response")
//                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    MyPreferences.saveAepsAgentData(str_aadhar, mobileNo, context)
                    //                                    openReceipt(commonResponseModel);
                } else {
                    Toast.makeText(context, senderResponse!!.statusMessage, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Oops! Something went wrong in response.\n"+e.message, Toast.LENGTH_SHORT).show()
        }
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
                    //                    showMessageDialogue(s_message, "Fingerprint data status");
                    showMessageDialogue(s_message, "Face capture data status")
//                    showTransactionAlert()
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
                        captureData()
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

}