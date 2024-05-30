package com.justclick.clicknbook.Fragment.registration

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordRequest
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordResponse
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import okhttp3.ResponseBody

private var registrationRequest: RegistrationRequest?=null

class ContactDetailFragment : Fragment() {

    private var mView: View? = null
    private var salutation_spinner: Spinner? = null
    private var mContext:Context?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_contact_detail, container, false)
        salutation_spinner=mView!!.findViewById(R.id.salutation_spinner)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.salutation_spinner_item,
            R.id.operator_tv,
            resources.getStringArray(R.array.salutation_array)
        )
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown)
        salutation_spinner!!.adapter = adapter

        mView!!.findViewById<TextView>(R.id.continue_tv).setOnClickListener {
            continueClicked()
        }
        mView!!.findViewById<TextView>(R.id.backTv).setOnClickListener {
            requireActivity().finish()
        }

        return mView
    }

    private fun continueClicked() {
        if(validateDetails()){
            checkUser()
        }
    }

    private fun checkUser() {

        val params: MutableMap<String, String> = HashMap()
        params["Emailid"] = mView!!.findViewById<TextView>(R.id.email_edt).text.toString()
        params["mobilenno"] = mView!!.findViewById<TextView>(R.id.mobile_edt).text.toString()
        params["MerchantId"] = ApiConstants.MerchantId

        NetworkCall().callServiceWithError(NetworkCall.getPayoutNewApiInterface().changePassword(ApiConstants.checkdataexists, params),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, responseCode)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody, responseCode: Int) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                PaytmWalletFragment.CommonResponse::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    (mContext as RegistrationActivityNew).replaceFragmentWithBackStack(
                        AgencyDetailFragment.newInstance(
                            registrationRequest
                        )
                    )
                } else if (senderResponse.statusMessage != null) {
                    Common.showResponsePopUp(context, "     "+senderResponse.statusMessage+"     ")
                } else {
                    Toast.makeText(
                        context,
                        "Error in checking user, please try after some time.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateDetails(): Boolean {
        var firstName=mView!!.findViewById<TextView>(R.id.first_name_edt).text.toString()
        var lastName=mView!!.findViewById<TextView>(R.id.user_last_name_edt).text.toString()
        var mobile=mView!!.findViewById<TextView>(R.id.mobile_edt).text.toString()
        var email=mView!!.findViewById<TextView>(R.id.email_edt).text.toString()
        if(!Common.isNameValid(firstName)){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_first_name), Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isNameValid(lastName)){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_last_name), Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isMobileValid(mobile)){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_mobile), Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isEmailValid(email)){
            Toast.makeText(requireContext(), getString(R.string.empty_and_invalid_email), Toast.LENGTH_SHORT).show()
            return false
        }else{
            registrationRequest = RegistrationRequest(Parcel.obtain())
            registrationRequest!!.salutation=salutation_spinner!!.selectedItem.toString()
            registrationRequest!!.firstname=firstName
            registrationRequest!!.lastname=lastName
            registrationRequest!!.mobile=mobile
            registrationRequest!!.email=email
            return true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContactDetailFragment()
    }
}