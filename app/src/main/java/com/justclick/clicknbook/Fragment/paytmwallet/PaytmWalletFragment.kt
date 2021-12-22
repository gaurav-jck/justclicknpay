package com.justclick.clicknbook.Fragment.paytmwallet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_lic.view.*
import kotlinx.android.synthetic.main.lic_receipt_dialog.*
import okhttp3.ResponseBody

/**
 * A simple [Fragment] subclass.
 */
class PaytmWalletFragment : Fragment() {

    private val GenerateToken=1
    private val SEND_OTP=2
    private val VerifyOtp=3
    private val TopUp=4
    var isOtp=false
    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    var mobileEdt:EditText?=null
    var emailEdt:EditText?=null
    var amountEdt:EditText?=null
    var nameEdt:EditText?=null
    var otpEdt:EditText?=null
    var otpField: TextInputLayout?=null
    var resendTv: TextView?=null
    var submitBtn: Button?=null
    var token: String?=null
    var userData: String?=null

    var paytmRequest:PaytmRequest?= PaytmRequest()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_paytm_wallet, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        mobileEdt=view.findViewById(R.id.mobileEdt)
        emailEdt=view.findViewById(R.id.emailEdt)
        nameEdt=view.findViewById(R.id.nameEdt)
        amountEdt=view.findViewById(R.id.amountEdt)
        otpEdt=view.findViewById(R.id.otpEdt)
        otpField=view.findViewById(R.id.otp)
        resendTv=view.findViewById(R.id.resendTv)
        submitBtn=view.findViewById(R.id.submitBtn)
        view.back_arrow.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        emailEdt!!.setText(MyPreferences.getLoginData(LoginModel(),context).Data.Email)

        submitBtn!!.setOnClickListener {
            if (checkValidation()) {
                if(!isOtp){
                    getCredentials()
                }else if(otpEdt!!.text.toString().trim().length<4){
                    Toast.makeText(context,"Please enter valid Otp", Toast.LENGTH_SHORT).show()
                }else{
                    paytmRequest!!.Otp=otpEdt!!.text.toString().trim()
                    verifyOtp()
                }
            }
        }

        return view
    }

    private fun getCredentials() {
        paytmRequest!!.AgentCode=MyPreferences.getLoginData(LoginModel(),context).Data.DoneCardUser
        showCustomDialog("Getting credentials...")
        NetworkCall().callLicService(paytmRequest, ApiConstants.GenerateToken, context, "","",false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, GenerateToken, paytmRequest!!) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {      //{"AgentCode":"JC0A13387","Merchant":"JUSTCLICKTRAVELS","Mode":"App"}
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp() {
        if(MyCustomDialog.isDialogShowing()){
            MyCustomDialog.setDialogMessage("Sending OTP, please wait...")
        }else{
            showCustomDialog("Sending OTP, please wait...")
        }
        NetworkCall().callPaytmService(paytmRequest, ApiConstants.SendOtp, context, userData,token,false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                hideCustomDialog()
                responseHandler(response, SEND_OTP, paytmRequest!!) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyOtp() {
        showCustomDialog("Verifying OTP, please wait...")
        NetworkCall().callPaytmService(paytmRequest, ApiConstants.VerifyOtp, context, userData,token,false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, VerifyOtp, paytmRequest!!) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun topUp(commonResponse: VerifyOtpResponse) {
        paytmRequest!!.ReqMappingId=commonResponse.reqMappingId
        paytmRequest!!.TxnToken=commonResponse.txnToken
        if(MyCustomDialog.isDialogShowing()){
            MyCustomDialog.setDialogMessage("Paytm TopUp requested, please wait...")
        }else{
            showCustomDialog("Paytm TopUp requested, please wait...")
        }
        NetworkCall().callPaytmService(paytmRequest, ApiConstants.TopUp, context, userData,token,false
        ) { response: ResponseBody?, responseCode: Int ->

            if (response != null) {
                hideCustomDialog()
                responseHandler(response, TopUp, paytmRequest!!) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                /*val commonResponse = Gson().fromJson(*//*response.string()*//*bill, PaytmTopUpResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                        showBillReceipt(commonResponse)
                    } else {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }*/
            }
        }
    }

    private fun checkValidation(): Boolean {
        var mobile=mobileEdt!!.text.toString().trim()
        var name=nameEdt!!.text.toString().trim()
        var amount=amountEdt!!.text.toString().trim()
        var email=emailEdt!!.text.toString().trim()

        if(!Common.isMobileValid(mobile)){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        }else if(!Common.isNameValid(name)){
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show()
            return false
        }/*else if(!Common.isEmailValid(email)){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
            return false
        }*/else if(!Common.isdecimalvalid(amount)){
            Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show()
            return false
        }else if(amount.toInt()<100 || amount.toInt()>5000){
            Toast.makeText(context, "Amount should be in between 100 to 5000", Toast.LENGTH_SHORT).show()
            return false
        }
        paytmRequest!!.Mobile=mobile
        paytmRequest!!.Name=name
        paytmRequest!!.Email=email
        paytmRequest!!.Amount=amount.toInt()
        return true
    }

    class PaytmRequest{
        val Mode = "App"
        val Merchant= ApiConstants.MerchantId
        val ServiceType= "PAYTM"
        var AgentCode: String? = null
        var Mobile: String? = null
        var Email: String? = null
        var Name: String? = null
        var userData: String? = null
        var token: String? = null
        var Amount: Int? = null

        var Otp: String? = null
        var ReqMappingId: String? = null
        var TxnToken: String? = null

    }

    internal class CommonResponse{
        var statusCode: String? = null
        var statusMessage: String? = null
    }

    internal class VerifyOtpResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
        var reqMappingId: String? = null
        var txnToken: String? = null

    }

    internal class PaytmTopUpResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
        var txnDetail:ArrayList<TxnDetail>?=null

        internal class TxnDetail{
            var txnStatus:String?=null
            var mobile:String?=null
            var agentCode:String?=null
            var transactionId:String?=null
            var apiReferenceId:String?=null
            var serviceType:String?=null
            var amount:String?=null
        }

    }

    private fun responseHandler(response: ResponseBody, type: Int, request: PaytmRequest) {
        try{
            if(type==GenerateToken){
                val commonResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
//                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                        token=commonResponse.credentialData.get(0).token
                        userData=commonResponse.credentialData.get(0).userData
                        sendOtp()
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }else{
                    hideCustomDialog()
                }
            }else if(type==SEND_OTP){
                val commonResponse = Gson().fromJson(response.string(), CommonResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                        showOtpField();
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }else{
                    hideCustomDialog()
                }
            }else if(type==VerifyOtp){
                val commonResponse = Gson().fromJson(response.string(), VerifyOtpResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                        topUp(commonResponse)
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }else{
                    hideCustomDialog()
                }
            }else{
                Toast.makeText(context, "Response",Toast.LENGTH_SHORT).show()
                val commonResponse = Gson().fromJson(response.string()/*bill*/, PaytmTopUpResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                        showBillReceipt(commonResponse)
                    } else {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }catch (e: Exception){

        }
    }

    private fun showOtpField() {
        otpField!!.visibility=View.VISIBLE
        resendTv!!.visibility=View.VISIBLE
        isOtp=true
        resendTv!!.setOnClickListener {
            Common.preventFrequentClick(resendTv)
            sendOtp()
        }
    }

    private fun showBillReceipt(commonResponse: PaytmTopUpResponse) {
        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.lic_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        val title = dialog.findViewById<TextView>(R.id.title)
        val agentCodeTv = dialog.findViewById<TextView>(R.id.agentCodeTv)
        val policyTv = dialog.findViewById<TextView>(R.id.policyTv)
        val policyNoTv = dialog.findViewById<TextView>(R.id.policyNoTv)
        val nameTv = dialog.findViewById<TextView>(R.id.nameTv)
        val amountTv = dialog.findViewById<TextView>(R.id.amountTv)
        val txnIdTv = dialog.findViewById<TextView>(R.id.txnIdTv)
        val refId = dialog.findViewById<TextView>(R.id.refId)
        val operatorIdTv = dialog.findViewById<TextView>(R.id.operatorIdTv)
        val statusTv = dialog.findViewById<TextView>(R.id.statusTv)

        title.text="Paytm TopUp Receipt"
        policyTv.text="Mobile number"
        refId.text="Reference Id"
        agentCodeTv.text=paytmRequest!!.AgentCode
        policyNoTv.text=paytmRequest!!.Mobile
        nameTv.text=nameEdt!!.text.toString()
        amountTv.text=commonResponse.txnDetail!!.get(0).amount
        txnIdTv.text=commonResponse.txnDetail!!.get(0).transactionId
        operatorIdTv.text=commonResponse.txnDetail!!.get(0).apiReferenceId
        statusTv.text=commonResponse.txnDetail!!.get(0).txnStatus

        dialog.back_tv.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    fun hideCustomDialog(){
        MyCustomDialog.hideCustomDialog()
    }

    fun showCustomDialog(msg:String){
        MyCustomDialog.showCustomDialog(context, msg)
    }

    var bill=" {\"statusCode\":\"00\",\"statusMessage\":\"Transaction Successful\"," +
            "\"txnDetail\":[{\"txnStatus\":\"SUCCESS\",\"mobile\":\"9779401874\"," +
            "\"agentCode\":\"JC0A7253\",\"transactionId\":\"W10081XOW9JC0A7253\"," +
            "\"apiReferenceId\":\"1687728\",\"serviceType\":\"PAYTM\",\"amount\":100}]}"

}
