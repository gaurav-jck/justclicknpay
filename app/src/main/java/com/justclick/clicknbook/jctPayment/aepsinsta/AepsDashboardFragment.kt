package com.jck.myjckapp.ui.fragments.aeps

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.instapay.InstaMerchantKycFragment
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.MerchantKycCheckResponse
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentAepsDashboardBinding
import com.justclick.clicknbook.jctPayment.newaeps.AepsConstants
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import java.net.URL
import java.util.Scanner

class AepsDashboardFragment : Fragment(), View.OnClickListener {
    private val ARG_PARAM1 = "param1"
    private val NO_ACTION_REQUIRED = "NO-ACTION-REQUIRED"
    private val Pending = "PENDING"
    private var binding: FragmentAepsDashboardBinding?=null
    private var commonParams: CheckCredentialResponse.credentialData? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    private var check2fa=false;
    private var backPress=false;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                commonParams = it.getSerializable(ARG_PARAM1, CheckCredentialResponse.credentialData::class.java)
            }else{
                commonParams = it.getSerializable(ARG_PARAM1) as CheckCredentialResponse.credentialData?
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAepsDashboardBinding.inflate(layoutInflater)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        binding!!.topView.titleTv.text = "AEPS Dashboard"
        binding!!.cashWithdrawLin.setOnClickListener(this)
        binding!!.balanceEnqLin.setOnClickListener(this)
        binding!!.miniStmtLin.setOnClickListener(this)
        binding!!.adharPayLin.setOnClickListener(this)
        binding!!.authenticationLin.setOnClickListener(this)
        binding!!.topView.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        getIpAddress()
        return binding!!.root
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cashWithdrawLin->{
                openAepsTransaction(AepsConstants.CW)
            }
            R.id.balanceEnqLin->{
                openAepsTransaction(AepsConstants.BE)
            }
            R.id.miniStmtLin->{
                openAepsTransaction(AepsConstants.MS)
            }
            R.id.adharPayLin->{
                openAepsTransaction(AepsConstants.AP)
            }
            R.id.authenticationLin->{
                openAepsTransaction(AepsConstants.D2FA)
            }

        }
    }

    private fun openAepsTransaction(type: String) {
        if(isKycChecked){
            if(type.equals(AepsConstants.D2FA)){
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                    AepsDailyLoginFragment.newInstance(commonParams!!, "")
                )
            }else{
                (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(AepsFragment.newInstance(commonParams!!,type))
            }
        }else{
            checkMerchantKyc(type)
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
                        openAepsTransaction(bankType)
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

    companion object {
        @JvmStatic
        fun newInstance(param1: CheckCredentialResponse.credentialData) =
            AepsDashboardFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}