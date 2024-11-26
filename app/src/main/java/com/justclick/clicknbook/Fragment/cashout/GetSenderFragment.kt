package com.justclick.clicknbook.Fragment.cashout

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.cashoutnew.PayoutBeneFragment
import com.justclick.clicknbook.Fragment.jctmoney.JctMoneyCreateSenderFragment
import com.justclick.clicknbook.Fragment.jctmoney.JctMoneySenderDetailFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.FragmentTags
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.JctMoneySenderDetailResponseModel
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody

class GetSenderFragment : Fragment(), View.OnClickListener {
    private var titleChangeListener: ToolBarTitleChangeListener? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    var getTv: TextView? = null
    var number_edt: EditText? = null
    private var loginModel: LoginModel? = null
    private var commonParams: CommonParams? = null
    private val kycStatus: String? = null
    private var isCheckCredential = false
    var textWatcher:TextWatcher?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonParams = CommonParams()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            titleChangeListener = context as ToolBarTitleChangeListener
            toolBarHideFromFragmentListener = context as ToolBarHideFromFragmentListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_jct_money_get_sender, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
        if (commonParams!!.token == null && commonParams!!.userData == null) {
            checkCredential()
        }
        initializeViews(view)
        return view
    }

    private fun checkCredential() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = CheckCredentialRequest()
        request.agentCode = loginModel.Data.DoneCardUser
        NetworkCall().callRapipayService(request, ApiConstants.CheckCredential, context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerCredential(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                isCheckCredential = true
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerCredential(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    commonParams!!.sessionKey = senderResponse.credentialData[0].sessionKey
                    commonParams!!.sessionRefNo = senderResponse.credentialData[0].sessionRefNo
                    commonParams!!.token = senderResponse.credentialData[0].token
                    commonParams!!.userData = senderResponse.credentialData[0].userData
                    commonParams!!.kycStatus = senderResponse.credentialData[0].kycStatus
                    commonParams!!.apiService = senderResponse.apiServices

                    commonParams!!.setSessionKey(senderResponse.getCredentialData().get(0).getSessionKey());
                    commonParams!!.setSessionRefNo(senderResponse.getCredentialData().get(0).getSessionRefNo());
                    commonParams!!.setToken(senderResponse.getCredentialData().get(0).getToken());
                    commonParams!!.setUserData(senderResponse.getCredentialData().get(0).getUserData());
                    commonParams!!.setKycStatus(senderResponse.getCredentialData().get(0).getKycStatus());
                    commonParams!!.setApiService(senderResponse.apiServices);
                    commonParams!!.setAddress(senderResponse.getCredentialData().get(0).address);
                    commonParams!!.setPinCode(senderResponse.getCredentialData().get(0).pinCode);
                    commonParams!!.setState(senderResponse.getCredentialData().get(0).state);
                    commonParams!!.setCity(senderResponse.getCredentialData().get(0).city);
                    commonParams!!.setStatecode(senderResponse.getCredentialData().get(0).statecode);
                    commonParams!!.isBank2= senderResponse.getCredentialData().get(0).isBank2;
                    commonParams!!.isBank3= senderResponse.getCredentialData().get(0).isBank3;
                    if (isCheckCredential) {
                        isCheckCredential = false
                        senderDetail
                    }
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                }
            } else {
                isCheckCredential = true
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            isCheckCredential = true
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeViews(view: View) {
        getTv = view.findViewById(R.id.get_tv)
        number_edt = view.findViewById(R.id.number_edt)
        getTv!!.setOnClickListener(this)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val face = Common.TextViewTypeFace(context)
        getTv!!.setTypeface(face)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.length == 10) {
                    getClicked()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        }
    }

    private fun getClicked() {
        Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
        Common.preventFrequentClick(getTv)
        if (Common.checkInternetConnection(context)) {
            if (isCheckCredential) {
                checkCredential()
            } else {
                senderDetail
            }
        } else {
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
        }
    }

    //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
    private val senderDetail: Unit
        private get() {
            if (validate()) {
                val jctMoneySenderRequestModel = SenderDetailRequest()
                jctMoneySenderRequestModel.mobile = number_edt!!.text.toString()
                jctMoneySenderRequestModel.agentCode = loginModel!!.Data.DoneCardUser
                jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
                jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
                jctMoneySenderRequestModel.setApiService(commonParams!!.apiService)
                //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
                        { response, responseCode ->
                            if (response != null) {
                                responseHandler(response, 1)
                            } else {
                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                            }
                        }, commonParams!!.userData, commonParams!!.token)
            }
        }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00" || senderResponse.statusCode == "02" || senderResponse.statusCode == "03") {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    commonParams!!.mobile=number_edt!!.text.toString()
                    bundle.putSerializable("commonParams", commonParams)
                    val payoutBeneFragment = PayoutBeneFragment()
                    payoutBeneFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithTag(payoutBeneFragment, FragmentTags.payoutSenderDetailFragment)
                } else if (senderResponse.statusCode == "05") {
//                    add sender
//                    getsender ka response requestfor me dena h
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    bundle.putString("SenderNumber", number_edt!!.text.toString())
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    bundle.putSerializable("commonParams", commonParams)
                    val senderDetailFragment = AddSenderFragment()
                    senderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(senderDetailFragment)
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                } else if (senderResponse.statusCode == "06") {
//                    update sender
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    bundle.putString("SenderNumber", number_edt!!.text.toString())
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    bundle.putSerializable("commonParams", commonParams)
                    val senderDetailFragment = AddSenderFragment()
                    senderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(senderDetailFragment)
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }

    private fun responseHandlerOld(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), JctMoneySenderDetailResponseModel::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == 1 && (senderResponse.state.equals("2", ignoreCase = true) ||
                                senderResponse.state.equals("4", ignoreCase = true))) {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    val jctMoneySenderDetailFragment = JctMoneySenderDetailFragment()
                    jctMoneySenderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithTag(jctMoneySenderDetailFragment, FragmentTags.jctMoneySenderDetailFragment)
                    //                    Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                } else if (senderResponse.statusCode == 1 && senderResponse.state.equals("1", ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    bundle.putString("SenderNumber", number_edt!!.text.toString())
                    val jctMoneyCreateSenderFragment = JctMoneyCreateSenderFragment()
                    jctMoneyCreateSenderFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(jctMoneyCreateSenderFragment)
                    Toast.makeText(context, senderResponse.message, Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle()
                    bundle.putString("SenderNumber", number_edt!!.text.toString())
                    val jctMoneyCreateSenderFragment = JctMoneyCreateSenderFragment()
                    jctMoneyCreateSenderFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(jctMoneyCreateSenderFragment)
                    Toast.makeText(context, senderResponse.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_arrow -> parentFragmentManager.popBackStack()
            R.id.get_tv -> {
                Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
                Common.preventFrequentClick(getTv)
                if (Common.checkInternetConnection(context)) {
                    if (isCheckCredential) {
                        checkCredential()
                    } else {
                        senderDetail
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (number_edt!!.text.toString().length < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        number_edt!!.addTextChangedListener(textWatcher)
    }

    override fun onPause() {
        super.onPause()
        number_edt!!.removeTextChangedListener(textWatcher)
    }
}