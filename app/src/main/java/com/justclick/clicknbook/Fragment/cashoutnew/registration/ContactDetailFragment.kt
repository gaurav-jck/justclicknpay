package com.justclick.clicknbook.Fragment.cashoutnew.registration

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
import com.justclick.clicknbook.R
import com.justclick.clicknbook.utils.Common

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

        return mView
    }

    private fun continueClicked() {
        if(validateDetails()){

            (mContext as RegistrationActivityNew).replaceFragmentWithBackStack(
                AgencyDetailFragment.newInstance(
                    registrationRequest
                )
            )
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