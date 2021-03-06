package com.justclick.clicknbook.Fragment.lic

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
import kotlinx.android.synthetic.main.lic_receipt_dialog.view.*
import okhttp3.ResponseBody

/**
 * A simple [Fragment] subclass.
 */
class LicFragment : Fragment() {

    private val INITIATE=1
    private val BILL=2
    private val PAY_BILL=3
    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    var policyEdt:EditText?=null
    var emailEdt:EditText?=null
    var amountEdt:EditText?=null
    var dateEdt:EditText?=null
    var nameEdt:EditText?=null
    var nameField: TextInputLayout?=null
    var amountField: TextInputLayout?=null
    var dateField: TextInputLayout?=null
    var submitBtn: Button?=null

    var licPayBillRequest:LicRequest?= LicRequest()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        toolBarHideFromFragmentListener=context as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_lic, container, false)
        toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)

        policyEdt=view.findViewById(R.id.policyEdt)
        emailEdt=view.findViewById(R.id.emailEdt)
        nameEdt=view.findViewById(R.id.nameEdt)
        amountEdt=view.findViewById(R.id.amountEdt)
        dateEdt=view.findViewById(R.id.dateEdt)
        nameField=view.findViewById(R.id.name)
        amountField=view.findViewById(R.id.amount)
        dateField=view.findViewById(R.id.date)
        submitBtn=view.findViewById(R.id.submitBtn)
        view.back_arrow.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        submitBtn!!.setOnClickListener{
            if(policyEdt!!.isEnabled){
                checkValidation()
            }else{
                payBill();
            }
        }

        return view
    }

    private fun checkValidation() {
        var policy=policyEdt!!.text.toString().trim()
        var email=emailEdt!!.text.toString().trim()

        if(policy.isEmpty()){
            Toast.makeText(context, "Please enter policy number", Toast.LENGTH_SHORT).show()
            return
        }else if(!Common.isEmailValid(email)){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
            return
        }else{
            var loginModel = LoginModel()
            loginModel = MyPreferences.getLoginData(loginModel, context)
            val request = LicRequest()
            request.AgentCode = loginModel.Data.DoneCardUser
            request.PolicyNumber = policy
            request.Email = email
            getCredentials(request)
        }
    }

    class LicRequest{
        val Mode = "App"
        val Merchant= ApiConstants.MerchantId
        var AgentCode: String? = null
        var PolicyNumber: String? = null
        var Email: String? = null
        var userData: String? = null
        var token: String? = null

        var Lattitude: String? = "28.351839"
        var Longitude: String? = "79.409561"
        var BillNumber: String? = null
        var Ad2: String? = null
        var Ad3: String? = null
        var Amount: Float? = null

    }

    internal class BillResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
        var licBill: List<LicBill>? = null
        var billDetail: List<BillDetail>? = null

        inner class LicBill {
            val billNumber: String? = null
            var billDate: String? = null
            val dueDate: String? = null
            var billAmount: Float? = null
            var acceptPartPay: String? = null
            val customerName: String? = null
            val ad2: String? = null
            val ad3: String? = null
        }

        inner class BillDetail{
            var transactionId:String?=null
            var acknowledgementNo:String?=null
            var operatotrId:String?=null
            var amount:String?=null
            var status:String?=null
        }
    }


    private fun getCredentials(request: LicRequest) {
        showCustomDialog("Getting credentials...")
        NetworkCall().callLicService(request, ApiConstants.GenerateToken, context, "","",false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, INITIATE, request) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {      //{"AgentCode":"JC0A13387","Merchant":"JUSTCLICKTRAVELS","Mode":"App"}
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPolicyBill(request: LicRequest, userData:String, token:String) {
        request.userData=userData
        request.token=token
        if(MyCustomDialog.isDialogShowing()){
            MyCustomDialog.setDialogMessage("Fetching LIC bill details...")
        }else{
            showCustomDialog("Fetching LIC bill details...")
        }
        NetworkCall().callLicService(request, ApiConstants.FetchLicBill, context,userData,token, false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, BILL, request) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {      //{"AgentCode":"JC0A13387","Merchant":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody, type: Int, request: LicRequest) {
        try{
            if(type==INITIATE){
                val commonResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        fetchPolicyBill(request,commonResponse.credentialData.get(0).userData, commonResponse.credentialData.get(0).token)
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }else{
                    hideCustomDialog()
                }
            }else if(type==BILL){
                hideCustomDialog()
                val commonResponse = Gson().fromJson(response.string(), BillResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        showBillDetails(commonResponse, request)
                    } else {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                hideCustomDialog()
                val commonResponse = Gson().fromJson(response.string()/*bill*/, BillResponse::class.java)
                if (commonResponse != null) {
                    if (commonResponse.statusCode.equals("00", ignoreCase = true)) {
                        showBillReceipt(commonResponse)
                    } else {
                        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }catch (e: Exception){

        }
    }

    private fun showBillDetails(commonResponse: BillResponse, request: LicRequest) {
//        Toast.makeText(context, commonResponse.licBill!!.get(0).customerName,Toast.LENGTH_LONG).show()
        policyEdt!!.isEnabled=false
        emailEdt!!.isEnabled=false
        amountEdt!!.setText(commonResponse.licBill!!.get(0).billAmount.toString())
        nameEdt!!.setText(commonResponse.licBill!!.get(0).customerName.toString())
        dateEdt!!.setText(commonResponse.licBill!!.get(0).dueDate.toString())
        submitBtn!!.setText("Pay bill")

        nameField!!.visibility=View.VISIBLE
        amountField!!.visibility=View.VISIBLE
        dateField!!.visibility=View.VISIBLE

//      "BillNumber":null,"token":"","userData":""}
        licPayBillRequest!!.PolicyNumber=request.PolicyNumber
        licPayBillRequest!!.Email=request.Email
        licPayBillRequest!!.AgentCode=request.AgentCode
        licPayBillRequest!!.userData=request.userData
        licPayBillRequest!!.token=request.token
        licPayBillRequest!!.Amount=commonResponse.licBill!!.get(0).billAmount
        licPayBillRequest!!.Ad2=commonResponse.licBill!!.get(0).ad2
        licPayBillRequest!!.Ad3=commonResponse.licBill!!.get(0).ad3

    }

    private fun payBill(){
        NetworkCall().callLicService(licPayBillRequest, ApiConstants.PayLicBill, context,
                licPayBillRequest!!.userData, licPayBillRequest!!.token, true
        ) { response: ResponseBody?, responseCode: Int ->
            hideCustomDialog()
            if (response != null) {
                responseHandler(response, PAY_BILL, LicRequest()) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {      //{"AgentCode":"JC0A13387","Merchant":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
//                responseHandler(response, PAY_BILL, LicRequest())
            }
        }
//        Toast.makeText(context, "Pay bill", Toast.LENGTH_LONG).show()
    }

    private fun showBillReceipt(commonResponse: BillResponse) {
        Toast.makeText(context, commonResponse.statusMessage, Toast.LENGTH_LONG).show()
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.lic_receipt_dialog)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        val agentCodeTv = dialog.findViewById<TextView>(R.id.agentCodeTv)
        val policyNoTv = dialog.findViewById<TextView>(R.id.policyNoTv)
        val nameTv = dialog.findViewById<TextView>(R.id.nameTv)
        val amountTv = dialog.findViewById<TextView>(R.id.amountTv)
        val txnIdTv = dialog.findViewById<TextView>(R.id.txnIdTv)
        val operatorIdTv = dialog.findViewById<TextView>(R.id.operatorIdTv)
        val statusTv = dialog.findViewById<TextView>(R.id.statusTv)

        agentCodeTv.text=licPayBillRequest!!.AgentCode
        policyNoTv.text=licPayBillRequest!!.PolicyNumber
        nameTv.text=nameEdt!!.text.toString()
        amountTv.text=commonResponse.billDetail!!.get(0).amount
        txnIdTv.text=commonResponse.billDetail!!.get(0).transactionId
        operatorIdTv.text=commonResponse.billDetail!!.get(0).operatotrId
        statusTv.text=commonResponse.billDetail!!.get(0).status

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

    var data="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Bill fetch successfully.\",\n" +
            "    \"licBill\": [\n" +
            "        {\n" +
            "            \"billNumber\": \"839805709\",\n" +
            "            \"billDate\": null,\n" +
            "            \"dueDate\": \"28/07/2021 - 28/07/2021\",\n" +
            "            \"billAmount\": 2558,\n" +
            "            \"acceptPartPay\": null,\n" +
            "            \"customerName\": \"Naru Mondal\",\n" +
            "            \"ad2\": \"HDC234164\",\n" +
            "            \"ad3\": \"HDC400590\"\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    var bill="{\"statusCode\":\"00\",\"statusMessage\":\"Transaction is pending.Pls to status check after 2 hours.\",\"billDetail\":[{\"transactionId\":\"L24071ZXJPJC0A16599\",\"acknowledgementNo\":\"1519062\",\"operatotrId\":\"\",\"amount\":2558,\"status\":\"Success\"}]}"

    var req=" RsId  :  https://recharge.justclicknpay.com/Utility/BillPayment/FetchLicBill : ReqTime-   |  AgencyID :  JC0A16599  |  2021-07-24 14:51:55  |  FetchLicBill\n" +
            "Request  :\n" +
            "{\"Merchant\":\"JUSTCLICKTRAVELS\",\"AgentCode\":\"JC0A16599\",\"Mode\":\"Web\",\"PolicyNumber\":\"839805709\",\"Email\":\"joydeep64@gmail.com\",\"token\":\"\",\"userData\":\"\"} Req~Rep {\"statusCode\":\"00\",\"statusMessage\":\"Bill fetch successfully.\",\"licBill\":[{\"billNumber\":\"839805709\",\"billDate\":null,\"dueDate\":\"28/07/2021 - 28/07/2021\",\"billAmount\":2558.30,\"acceptPartPay\":null,\"customerName\":\"Naru Mondal\",\"ad2\":\"HDC874803\",\"ad3\":\"HDC347869\"}]}\n" +
            "Response :\n" +
            "\n" +
            "--------------------------------------------------------------------------------------------------------\n" +
            " RsId  :  https://recharge.justclicknpay.com/Utility/BillPayment/PayLicBill : ReqTime-   |  AgencyID :  JC0A16599  |  2021-07-24 14:52:02  |  PayLicBill\n" +
            "Request  :\n" +
            "{\"Merchant\":\"JUSTCLICKTRAVELS\",\"AgentCode\":\"JC0A16599\",\"Mode\":\"Web\",\"PolicyNumber\":\"839805709\",\"Email\":\"joydeep64@gmail.com\",\"Lattitude\":\"28.351839\",\"Longitude\":\"79.409561\",\"BillNumber\":null,\"Ad2\":\"HDC874803\",\"Ad3\":\"HDC347869\",\"Amount\":2558,\"token\":\"\",\"userData\":\"\"} Req~Rep {\"statusCode\":\"00\",\"statusMessage\":\"Transaction is pending.Pls to status check after 2 hours.\",\"billDetail\":[{\"transactionId\":\"L24071ZXJPJC0A16599\",\"acknowledgementNo\":\"1519062\",\"operatotrId\":\"\",\"amount\":2558,\"status\":\"Success\"}]}"
}
