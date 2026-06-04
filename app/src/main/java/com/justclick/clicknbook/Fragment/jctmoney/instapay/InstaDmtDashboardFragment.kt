package com.justclick.clicknbook.Fragment.jctmoney.instapay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.MerchantKycCheckResponse
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentInstaDmtDashboardBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import java.net.URL
import java.util.Scanner


class InstaDmtDashboardFragment : Fragment(), View.OnClickListener {
    private val NO_ACTION_REQUIRED = "NO-ACTION-REQUIRED"
    private val ACTION_REQUIRED = "ACTION-REQUIRED"
    private val Approved = "APPROVED"
    private val Pending = "PENDING"
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener?=null
    private var binding:FragmentInstaDmtDashboardBinding?=null
    val PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLatitude = "29.9319558"
    var mLongitude= "77.5334789"
    var transactionId:String?=null
    private var commonParams: CheckCredentialResponse.credentialData? = null

    companion object {
        fun newInstance() = InstaDmtDashboardFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable("credentialResponse", CheckCredentialResponse.credentialData::class.java)
            }else{
                commonParams = it.getSerializable("credentialResponse") as CheckCredentialResponse.credentialData?
            }
        }
        // Obtain the FirebaseAnalytics instance.
        //        location find
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // method to get the location
//        getLastLocation()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                 arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1);
        } else {
            // Permission is already granted, fetch location
            mFusedLocationClient!!.lastLocation
                .addOnSuccessListener(
                    requireActivity()
                ) { location ->
                    if (location != null) {
                        val latitude: Double = location.getLatitude()
                        val longitude: Double = location.getLongitude()
//                        Toast.makeText(context, "lat-"+latitude, Toast.LENGTH_SHORT)
//                            .show()
                        // Use the coordinates here
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentInstaDmtDashboardBinding.inflate(layoutInflater)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        binding!!.dmt1Lin.setOnClickListener(this)
        binding!!.dmt2Lin.setOnClickListener(this)
        binding!!.dmt3Lin.setOnClickListener(this)
        binding!!.backArrow.setOnClickListener(this)
        //            Common.showCommonAlertDialog(context, ip, "IP address");
        getIpAddress()
        getLocationPermission()
        return binding!!.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.dmt1Lin->{
                dmtClicked("bank1")
            }
            R.id.dmt2Lin->{
                dmtClicked("bank2")
            }
            R.id.dmt3Lin->{
                dmtClicked("bank3")
            }
            R.id.back_arrow->{
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun dmtClicked(bankType: String) {
        if (isKycChecked) {
            var fragment=InstaGetSenderFragment()
            var bundle=Bundle()
            bundle.putSerializable("credentialResponse", commonParams)
            bundle.putString("BankName", bankType)
            fragment.arguments=bundle
            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
        } else {
            checkMerchantKyc(bankType)
        }
    }

    private var isKycChecked = false
    private fun checkMerchantKyc(bankType: String) {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = CheckCredentialRequest()
        request.agentCode = loginModel.Data.DoneCardUser
        //        request.setAgentCode("jc0a13387");
//        request.setIPAddress(Common.getIpAddress());
        request.setIPAddress(ip)

        //        request.setIPAddress(CommonKotlin.Companion.fetchPublicIP());
        NetworkCall().callService(
            NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(
                ApiConstants.checkagentekycstatus,
                request, commonParams!!.getUserData(), "Bearer " + commonParams!!.getToken()
            ), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerMerchantKyc(response, bankType)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val checkKycString = """{
    "statusCode": "02",
    "statusMessage": "Agent kyc not done",
    "status": true
}"""

    private fun responseHandlerMerchantKyc(response: ResponseBody, bankType: String) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                MerchantKycCheckResponse::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    if (senderResponse.data.action == NO_ACTION_REQUIRED) {
                        isKycChecked = true
                        dmtClicked(bankType)
                    } else if (senderResponse.data.status == Pending) {
                        merchantKycAlert(senderResponse)
                    } else {
                        Common.showCommonAlertDialog(
                            requireContext(),
                            senderResponse.statusMessage,
                            "KYC status"
                        )
                    }
                    //                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Common.showCommonAlertDialog(
                        requireContext(),
                        senderResponse.statusMessage,
                        "Api Response"
                    )
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun merchantKycAlert(kycResponse: MerchantKycCheckResponse) {
        // Create an alert builder
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setTitle("Api response")
        builder.setMessage("Your KYC is pending, please do merchant KYC.")
        builder.setCancelable(false)

        // add a button
        builder.setPositiveButton("DO KYC") { dialog: DialogInterface, which: Int ->
            // send data from the AlertDialog to the Activity
            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                InstaMerchantKycFragment.newInstance(
                    commonParams!!,
                    kycResponse.data
                )
            )
            dialog.dismiss()
        }
        // add a button
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
            // send data from the AlertDialog to the Activity
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
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
//                                                    Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
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
//                        Toast.makeText(context,"Latitude: " + mLatitude+"\nLongitude: " + mLongitude, Toast.LENGTH_LONG ).show();
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
}