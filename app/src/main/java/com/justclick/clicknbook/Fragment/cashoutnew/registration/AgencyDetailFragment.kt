package com.justclick.clicknbook.Fragment.cashoutnew.registration

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import okhttp3.ResponseBody


private const val ARG_PARAM1 = "param1"

class AgencyDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var registrationRequest: RegistrationRequest?=null
    private var mView: View? = null
    private var mContext:Context?=null
    private var userTypeSpinner: Spinner? = null
    private var countrySpinner: Spinner? = null
    private var stateSpinner: Spinner? = null
    private var citySpinner: Spinner? = null
//    private val userTypeArray = arrayOf("Agent","Distributor")
    private val userTypeArray = arrayOf("Agent")
    private val countryArray = arrayOf("India")
    private var stateList:Array<String?> = emptyArray()
    private var cityList:Array<String?> = emptyArray()
    private var stateCityList:ArrayList<StateCityResponse.CityInfo>?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registrationRequest =it.getParcelable(ARG_PARAM1, RegistrationRequest::class.java)
            }else{
                registrationRequest =it.getParcelable(ARG_PARAM1)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_agency_detail, container, false)

        mView!!.findViewById<TextView>(R.id.continue_tv).setOnClickListener {
            continueClicked()
        }

        userTypeSpinner=mView!!.findViewById(R.id.userTypeSpinner)
        val adapterUser = ArrayAdapter(
            requireContext(),
            R.layout.registration_spinner_item,
            R.id.operator_tv,
            userTypeArray)
        adapterUser.setDropDownViewResource(R.layout.registration_spinner_item_dropdown)
        userTypeSpinner!!.adapter = adapterUser

        countrySpinner=mView!!.findViewById(R.id.country_spinner)
        val adapterCountry = ArrayAdapter(
            requireContext(),
            R.layout.registration_spinner_item,
            R.id.operator_tv,
            countryArray)
        adapterCountry.setDropDownViewResource(R.layout.registration_spinner_item_dropdown)
        countrySpinner!!.adapter = adapterCountry

        stateSpinner=mView!!.findViewById(R.id.state_spinner)

        stateSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
//                Toast.makeText(requireContext(), selectedItem, Toast.LENGTH_SHORT).show()
                setCityAdapter(selectedItem, position)
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        citySpinner=mView!!.findViewById(R.id.city_spinner)

        getStateCity()

        return mView
    }

    private fun setCityAdapter(selectedItem: String, position: Int) {
        var cityArray:ArrayList<String> = ArrayList()
        for(stateCity in stateCityList!!.iterator()){
            if(stateCity.State.equals(selectedItem)){
                cityArray.add(stateCity.City)
            }
        }
        cityList= arrayOfNulls(cityArray.size)
        for(i in cityArray.indices){
            cityList[i]=cityArray[i]
        }
        setCityAdapter()
    }

    private fun getStateCity() {
        NetworkCall().callService(
            NetworkCall.getPayoutNewApiInterface().getStateCity(ApiConstants.getcityandstate), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerStateCity(response)  // https://uatapitravel.justclicknpay.com/V2/Cashfree/getcityandstate
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerStateCity(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(response.string(), StateCityResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.CityData!=null && senderResponse.CityData.CityInfo!=null &&
                    senderResponse.CityData.CityInfo.size>0) {
                    stateCityList=senderResponse.CityData.CityInfo
//                    Toast.makeText(context, senderResponse.CityData.CityInfo.size.toString(), Toast.LENGTH_LONG).show()
                    val distinctNames = senderResponse.CityData.CityInfo.flatMap { listOf(it.State) }
                        .distinct()
                    stateList=arrayOfNulls(distinctNames.size)
                    for(i in distinctNames.indices)
                        stateList[i]=distinctNames[i]
                    stateList.sort()
                    setStateAdapter()
                    stateSpinner!!.setSelection(0)
                } else {
                    Toast.makeText(context, "No data", Toast.LENGTH_LONG).show()
                }
            } else {
//                hideCustomDialog()
                Toast.makeText(context, "Error in registration", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
//            hideCustomDialog()
            Toast.makeText(context, "Exception occurs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setStateAdapter() {
        val adapterState = ArrayAdapter(
            requireContext(),
            R.layout.registration_spinner_item,
            R.id.operator_tv,
            stateList)
        adapterState.setDropDownViewResource(R.layout.registration_spinner_item_dropdown)
        stateSpinner!!.adapter = adapterState
    }
    private fun setCityAdapter() {
        val adapterCity = ArrayAdapter(
            requireContext(),
            R.layout.registration_spinner_item,
            R.id.operator_tv,
            cityList)
        adapterCity.setDropDownViewResource(R.layout.registration_spinner_item_dropdown)
        citySpinner!!.adapter = adapterCity
    }

    private fun continueClicked() {
        if(validateDetails()){
            (mContext as RegistrationActivityNew).replaceFragmentWithBackStack(
                OtherDetailFragment.newInstance(
                    registrationRequest
                )
            )
        }
    }

    private fun validateDetails(): Boolean {
        var userType=userTypeSpinner!!.selectedItem.toString()
        var companyName=mView!!.findViewById<TextView>(R.id.company_name_edt).text.toString()
        var country=countrySpinner!!.selectedItem.toString()
        var state:String?=null
        var city:String?=null
        if(stateSpinner!!.selectedItem!=null){
            state=stateSpinner!!.selectedItem.toString()
            city=citySpinner!!.selectedItem.toString()
        }
        var address=mView!!.findViewById<TextView>(R.id.address_edt).text.toString()
        var pincode=mView!!.findViewById<TextView>(R.id.pincode_edt).text.toString()
        var gst=mView!!.findViewById<TextView>(R.id.gst_edt).text.toString()
        var userPin=mView!!.findViewById<TextView>(R.id.user_pin_edt).text.toString()
        if(!Common.isNameValid(companyName)){
            Toast.makeText(requireContext(), "Company name is empty or invalid", Toast.LENGTH_SHORT).show()
            return false
        }else if(state==null || state.isEmpty()){
            Toast.makeText(requireContext(), "Please select State", Toast.LENGTH_SHORT).show()
            return false
        }else if(address.length<4){
            Toast.makeText(requireContext(), "Address is empty or invalid", Toast.LENGTH_SHORT).show()
            return false
        }else if(pincode.length<6){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_pincode), Toast.LENGTH_SHORT).show()
            return false
        }/*else if(!Common.isGSTValid(gst)){
            Toast.makeText(requireContext(), "GST number is empty or invalid", Toast.LENGTH_SHORT).show()
            return false
        }*/else if(userPin.length<4){
            Toast.makeText(requireContext(), "Userpin is empty or invalid", Toast.LENGTH_SHORT).show()
            return false
        }else{
            registrationRequest!!.usertype=userType
            registrationRequest!!.companyname=companyName
            registrationRequest!!.country=country
            registrationRequest!!.state=state
            registrationRequest!!.city=city
            registrationRequest!!.address=address
            registrationRequest!!.pincode=pincode
            registrationRequest!!.gstnumber=gst
            registrationRequest!!.userpin=userPin
            return true
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: RegistrationRequest?) =
            AgencyDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}