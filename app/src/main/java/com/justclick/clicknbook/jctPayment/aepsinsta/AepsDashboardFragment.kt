package com.jck.myjckapp.ui.fragments.aeps

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
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
        binding!!.topView.backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        getIpAddress()
        if(!check2fa && !backPress){
            checkDailyAuthentication(false,AepsConstants.CW)
        }
        return binding!!.root
    }

    private fun checkDailyAuthentication(isNext: Boolean, type: String) {
        var request= CheckCredentialRequest()
        request.agentCode=MyPreferences.getLoginData(LoginModel(),context).Data.DoneCardUser
        request.setIPAddress(ip)
        NetworkCall().callService(
            NetworkCall.getAepsInterface().getAepsInstaHeader(
                ApiConstants.check2fastatus, request,
                commonParams!!.userData, "Bearer " + commonParams!!.token
            ),
            requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, isNext, type)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody, isNext: Boolean, type: String) {
        try {
            val commonResponse = Gson().fromJson(response.string(), PaytmWalletFragment.CommonResponse::class.java)
            if (commonResponse != null) {
                if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
//                    Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    if(commonResponse.statusMessage.equals("LOGGEDIN")){
                       check2fa=true
                        if(isNext){
                            openAepsTransaction(type)
                        }
                    }else{
                        backPress=true
                        (context as NavigationDrawerActivity).replaceFragmentWithBackStack(
                            AepsDailyLoginFragment.newInstance(commonParams!!)
                        )
                    }
                } else {
                    Common.showCommonAlertDialog(context,commonResponse.statusMessage,"Api Response")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

        }
    }

    private fun openAepsTransaction(type: String) {
        if(check2fa){
            backPress=true
            (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(AepsFragment.newInstance(commonParams!!,type))
        }else{
            checkDailyAuthentication(false,type)
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