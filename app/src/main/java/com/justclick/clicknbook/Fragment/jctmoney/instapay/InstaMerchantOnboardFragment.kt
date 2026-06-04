package com.justclick.clicknbook.Fragment.jctmoney.instapay

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.jck.myjckapp.ui.fragments.aeps.AepsDashboardFragment
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.billpay.BillPayFragment
import com.justclick.clicknbook.Fragment.billpayinsta.InstaBillpayDashboardFragment
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.DmtKycResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaMerchantOnboardBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Scanner

class InstaMerchantOnboardFragment : Fragment() {
    private val DMT_INSTA = 1
    private val BILLPAY_INSTA: Int = 2
    private val AEPS_INSTA: Int = 3
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var binding: FragmentInstaMerchantOnboardBinding?=null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null
    private var gender:String="M"
    private val genderArray = arrayOf("Male", "Female", "Transgender")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var productType=DMT_INSTA

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener= context as ToolBarHideFromFragmentListener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
                productType = it.getInt(ARG_PARAM2)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
                productType = it.getInt(ARG_PARAM2)
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentInstaMerchantOnboardBinding.inflate(layoutInflater)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        getIpAddress()
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)
        initializeDates()
        checkLocationPermission()
        binding!!.spinnerGender.adapter= Common.getSpinnerAdapter(genderArray, requireContext())
        binding!!.spinnerGender.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                when (i) {
                    0 -> gender = "M"
                    1 -> gender = "F"
                    2 -> gender = "T"
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })

        binding!!.btnCapture.setOnClickListener {
            if(validateFields()){
                sendMobileTransaction()
            }
        }
        binding!!.dobEdt.setOnClickListener {
            openDatePicker()
        }

        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding!!.addressEdt.setText(commonParams!!.address)
        binding!!.cityEdt.setText(commonParams!!.city)
        binding!!.pinEdt.setText(commonParams!!.pinCode)
        return binding!!.root
    }

    private fun validateFields(): Boolean {
        if(binding!!.aadharEdt.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Please enter aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.aadharEdt.text.toString().trim().length<12) {
            Toast.makeText(context, "Please enter valid aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding!!.numberEdt.text.toString().length < 10) {
            Toast.makeText(
                context,
                getString(R.string.empty_and_invalid_mobile),
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }else if (!Common.isEmailValid(binding!!.emailEdt.text.toString().trim())) {
            Toast.makeText(context, "Email is empty or invalid", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isNameValid(binding!!.nameEdt.text.toString().trim())) {
            Toast.makeText(context, "Pan name is empty or invalid", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Common.isPancardValid(binding!!.panEdt.text.toString().trim())) {
            Toast.makeText(context, "Please enter valid pan number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.dobEdt.text.toString().isEmpty()) {
            Toast.makeText(context, "please select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.addressEdt.text.isEmpty()) {
            Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.cityEdt.text.isEmpty()) {
            Toast.makeText(context, "Please enter city name", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding!!.pinEdt.text.toString().length < 6) {
            Toast.makeText(
                context,
                getString(R.string.empty_and_invalid_pincode),
                Toast.LENGTH_SHORT
            ).show();
            return false;
        }else if(mLatitude==null){
            Toast.makeText(requireContext(), "Please enable device location", Toast.LENGTH_SHORT).show()
            checkLocationPermission()
        }
        return true
    }

    private fun sendMobileTransaction() {
        var loginModel= MyPreferences.getLoginData(LoginModel(), context);
//        Toast.makeText(requireContext(), "Transaction", Toast.LENGTH_SHORT).show()
        val params: MutableMap<String, String> = HashMap()
        params["Aadhar"] = binding!!.aadharEdt.text.toString()
        params["Email"] = binding!!.emailEdt.text.toString()
        params["Mobileno"] = binding!!.numberEdt.text.toString()
        params["Gender"] = gender
        params["Name"] = binding!!.nameEdt.text.toString()
        params["PAN"] = binding!!.panEdt.text.toString()
        params["Address"] = binding!!.addressEdt.text.toString()
        params["City"] = binding!!.cityEdt.text.toString()
        params["PinCode"] = binding!!.pinEdt.text.toString()
        params["AgentCode"] = loginModel.Data.DoneCardUser
        params["Mode"] = "App"
        params["MerchantId"] = ApiConstants.MerchantId
        params["DOB"] = binding!!.dobEdt.text.toString()
        params["Lattitude"] = mLatitude!!
        params["Longitude"] = mLongitude!!
//        params["IPAddress"] = "101.212.323.434"
//        params["IPAddress"] = Common.getIpAddress()
        params["IPAddress"] = ip!!

        NetworkCall().callService(
            NetworkCall.getDmtInstaApiInterface().getInstaHeaderMap(
                ApiConstants.agentonboarding, params,
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
                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    val bundle = Bundle()
                    bundle.putSerializable("credentialResponse", commonResponse)
                    if(productType==DMT_INSTA){
                        val instaFragment = InstaDmtDashboardFragment()
                        instaFragment.arguments = bundle
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(instaFragment)
                    }else if(productType==BILLPAY_INSTA){
                        val billPayFragment = InstaBillpayDashboardFragment()
                        billPayFragment.arguments = bundle
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(billPayFragment)
                    }else{
                        val fragment = AepsDashboardFragment()
                        fragment.arguments = bundle
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                    }
                } else {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
//                    (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
//                        InstaMerchantKycFragment.newInstance(commonParams!!));
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var dateServerFormat: SimpleDateFormat? = null
    private var checkInDateDay :Int= 0
    private var checkInDateMonth: Int = 0
    private var checkInDateYear: Int = 0
    private var dobDateCalendar: Calendar? = null
    private var currentDate: Calendar? = null
    private fun initializeDates() {
        //Date formats
        dateServerFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        currentDate = Calendar.getInstance()
        dobDateCalendar = Calendar.getInstance()

        checkInDateDay = currentDate!!.get(Calendar.DAY_OF_MONTH)
        checkInDateMonth = currentDate!!.get(Calendar.MONTH)
        checkInDateYear = currentDate!!.get(Calendar.YEAR)
    }
    private fun openDatePicker() {
        //Date formats

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                dobDateCalendar!!.set(year, monthOfYear, dayOfMonth)
                binding!!.dobEdt.setText(dateServerFormat!!.format(dobDateCalendar!!.getTime()))
            }, checkInDateYear, checkInDateMonth, checkInDateDay
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = currentDate!!.timeInMillis
        datePickerDialog.show()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: CheckCredentialResponse.credentialData, productType:Int) =
            InstaMerchantOnboardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param1)
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
            "    \"statusMessage\": \"Request made from invalid ip address - 103.139.75.200\"\n" +
            "}"
}