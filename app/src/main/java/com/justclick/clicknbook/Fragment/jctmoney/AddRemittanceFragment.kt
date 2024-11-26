package com.justclick.clicknbook.Fragment.jctmoney

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.request.AddSenderRequest
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.FragmentTags
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentDmtAddRemitterBinding
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import okhttp3.ResponseBody

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class AddRemittanceFragment : Fragment() {
    private var commonParams: CommonParams? = null
    private var stateResponse: String? = null
    private var kycId: String? = null
    private var binding: FragmentDmtAddRemitterBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                senderDetailResponse = it.getSerializable(ARG_PARAM1, SenderDetailResponse::class.java)
                commonParams = it.getSerializable(ARG_PARAM1, CommonParams::class.java)
            }else{
//                senderDetailResponse = it.getSerializable(ARG_PARAM1) as SenderDetailResponse?
                commonParams = it.getSerializable(ARG_PARAM1) as CommonParams?
            }
            stateResponse = it.getString(ARG_PARAM2)
            kycId = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentDmtAddRemitterBinding.inflate(layoutInflater)
//        return inflater.inflate(R.layout.fragment_dmt_kyc, container, false)

        binding!!.submitTv.setOnClickListener {
            if(binding!!.otpEdt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "PLease enter OTP", Toast.LENGTH_SHORT).show()
            }else if(binding!!.otpEdt.text.toString().length<4){
                Toast.makeText(requireContext(), "PLease enter valid OTP", Toast.LENGTH_SHORT).show()
            }else{
                addSender()
            }
        }

        binding!!.mobileEdt.setText(commonParams!!.mobile.toString())
        binding!!.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding!!.root
    }

    // show message
    private fun showTransactionAlert() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("Please confirm your Transaction")
            .setMessage("Do you want to do Transaction with given details!")
            .setPositiveButton("CONFIRM") { dialog, which ->
                dialog.cancel()
                //                        Toast.makeText(context, "Confirmed", Toast.LENGTH_SHORT).show();
//                        checkPermissions();
                addSender()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, i -> dialog.cancel() }
            .show()
    }

    private fun addSender() {
        val jctMoneySenderRequestModel = AddSenderRequest()
        jctMoneySenderRequestModel.Name = ""
        jctMoneySenderRequestModel.LastName = ""
        jctMoneySenderRequestModel.Mobile = commonParams!!.mobile
        jctMoneySenderRequestModel.AgentCode =commonParams!!.agentCode
        jctMoneySenderRequestModel.Mode ="App"
        jctMoneySenderRequestModel.MerchantId =ApiConstants.MerchantId
        jctMoneySenderRequestModel.gst_state = ""
        jctMoneySenderRequestModel.Pin = ""
        jctMoneySenderRequestModel.Address = ""
        jctMoneySenderRequestModel.State = ""
        jctMoneySenderRequestModel.Dob = ""
        jctMoneySenderRequestModel.Gender = ""
        jctMoneySenderRequestModel.RequestFor = ""
        jctMoneySenderRequestModel.SessionKey = commonParams!!.sessionKey
        jctMoneySenderRequestModel.SessionRefId = commonParams!!.sessionRefNo
        jctMoneySenderRequestModel.ApiService = commonParams!!.apiService
        jctMoneySenderRequestModel.stateResp = stateResponse
        jctMoneySenderRequestModel.ekyc_id = kycId
        jctMoneySenderRequestModel.otp = binding!!.otpEdt.text.toString()
        jctMoneySenderRequestModel.lat = "12.9166931"
        NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.AddSender, context,
            { response, responseCode ->
                if (response != null) {
                    responseHandler(response)
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                }
            }, commonParams!!.userData, commonParams!!.token)
    }

    private fun responseHandler(response: ResponseBody) {
        try {
            val commonResponse = Gson().fromJson(response.string(), CommonRapiResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
//                            openPrintDialog(commonResponse);
                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    getSenderDetail()
                } else {
                    Common.showResponsePopUp(requireContext(), commonResponse.statusMessage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSenderDetail() {
        val jctMoneySenderRequestModel = SenderDetailRequest()
        jctMoneySenderRequestModel.mobile = commonParams!!.mobile
        jctMoneySenderRequestModel.agentCode = commonParams!!.agentCode
        jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
        jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
        jctMoneySenderRequestModel.setApiService(commonParams!!.apiService) //new change
        jctMoneySenderRequestModel.setIsBank2(commonParams!!.isBank2) //new change
        jctMoneySenderRequestModel.setIsBank3(commonParams!!.isBank3) //new change
        NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
            { response, responseCode ->
                if (response != null) {
                    responseHandlerDetails(response)
                } else {
                    Toast.makeText(
                        context,
                        R.string.response_failure_message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, commonParams!!.userData, commonParams!!.token
        )
    }

    private fun responseHandlerDetails(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode.equals("00", ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    bundle.putSerializable("commonParams", commonParams)
                    val senderDetailFragment = RapipaySenderDetailFragment()
                    senderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity).replaceFragmentWithTagNoBackStack(
                        senderDetailFragment,
                        FragmentTags.jctMoneySenderDetailFragment
                    )
                }
                else {
                    showMessageDialogue(senderResponse.statusMessage, "Response Message")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     private fun showMessageDialogue(messageTxt: String, argTitle: String) {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(argTitle)
            .setMessage(messageTxt)
            .setPositiveButton(
                "OK"
            ) {
                    dialog,
                    which -> dialog.cancel()
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(commonParams: CommonParams, stateResponse: String, kycId:String) =
            AddRemittanceFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, commonParams)
                    putString(ARG_PARAM2, stateResponse)
                    putString(ARG_PARAM3, kycId)
                }
            }
    }

    var responseString="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Remitter account details fetched.!\",\n" +
            "    \"otpRefId\": null,\n" +
            "    \"fundTransferId\": null\n" +
            "}"
}