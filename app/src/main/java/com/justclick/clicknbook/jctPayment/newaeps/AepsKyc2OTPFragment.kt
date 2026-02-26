package com.justclick.clicknbook.jctPayment.newaeps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.ActivityAepsKycOtpBinding
import com.justclick.clicknbook.jctPayment.Models.AepsKyc2GetOtpRequest
import com.justclick.clicknbook.jctPayment.Models.AepsKyc2GetOtpResponse
import com.justclick.clicknbook.jctPayment.Utilities.URLs
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody

class AepsKyc2OTPFragment(aepsPipe: String) : Fragment(), View.OnClickListener {
    private var binding:ActivityAepsKycOtpBinding?=null
    val PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLatitude = "29.9319558"
    var mLongitude= "77.5334789"
    var transactionId:String?=null
    var otpreqid:String?=null
    var aepsPipe = aepsPipe
    var wadh="18f4CEiXeXcfGXvgWA/blxD+w2pw7hfQPY45JMytkPw="
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        //        location find
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // method to get the location
        getLastLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= ActivityAepsKycOtpBinding.inflate(layoutInflater)
        binding!!.getOtp.setOnClickListener(this)
        binding!!.verifyOtp.setOnClickListener(this)
        binding!!.backArrow.setOnClickListener(this)
        hideVerifyOtpView()
        return binding!!.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.getOtp->{
                getOtp()
            }
            R.id.verifyOtp->{
                val otp=binding!!.otpEdt.text.toString()
                if(otp.isEmpty()){
                    Toast.makeText(requireContext(),"Please enter OTP", Toast.LENGTH_SHORT).show()
                }else if(otp.length<6){
                    Toast.makeText(requireContext(),"Please enter valid OTP", Toast.LENGTH_SHORT).show()
                }else{
                    verifyOtp()
                }
            }
            R.id.backArrow->{
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun getOtp() {
        var requestModel= AepsKyc2GetOtpRequest()
        requestModel.AgentCode=MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        requestModel.Latitude=mLatitude
        requestModel.Longitude=mLongitude
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_AEPS_N).create(ApiInterface::class.java)
        val call = apiService.getAepsWithHeaderNew(URLs.Bank3SendOTP, requestModel, MyPreferences.getUserData(context),
            MyPreferences.getAepsToken(context))
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
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
            val commonResponse = Gson().fromJson(response.string(), AepsKyc2GetOtpResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
//                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "Api Response")
                    Toast.makeText(requireContext(), commonResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    transactionId=commonResponse.transactionid
                    otpreqid=commonResponse.otpreqid
                    showVerifyOtpView()
                } else {
                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "Api Response")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun verifyOtp() {
        var requestModel= AepsKyc2GetOtpRequest()
        requestModel.AgentCode=MyPreferences.getLoginData(LoginModel(), context).Data.DoneCardUser
        requestModel.Latitude=mLatitude
        requestModel.Longitude=mLongitude
        requestModel.Transactionid=transactionId
        requestModel.otpreqid=otpreqid
        requestModel.otp=binding!!.otpEdt.text.toString()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_AEPS_N).create(ApiInterface::class.java)
        val call = apiService.getAepsWithHeaderNew(URLs.bank3verifyOTP, requestModel, MyPreferences.getUserData(context),
            MyPreferences.getAepsToken(context))
        NetworkCall().callService(call,context,true
        ) { response, responseCode ->
            if (response != null) {
                responseHandlerVerify(response)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerVerify(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), AepsKyc2GetOtpResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                    Toast.makeText(requireContext(), commonResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    (context as NavigationDrawerActivity).replaceFragmentWithBackStack(AepsKyc2Bank3Fragment(aepsPipe))
                } else {
                    Common.showCommonAlertDialog(context, commonResponse.statusMessage, "Api Response")
//                    (context as NavigationDrawerActivity).replaceFragmentWithBackStack(AepsKyc2Bank3Fragment())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideVerifyOtpView(){
        binding!!.otpEdt.visibility=View.GONE
        binding!!.verifyOtp.visibility=View.GONE
        binding!!.getOtp.visibility=View.VISIBLE
    }
    fun showVerifyOtpView(){
        binding!!.otpEdt.visibility=View.VISIBLE
        binding!!.verifyOtp.visibility=View.VISIBLE
        binding!!.getOtp.visibility=View.GONE
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



}