package com.justclick.clicknbook.Fragment.lic

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.android.synthetic.main.fragment_lic.view.back_arrow
import kotlinx.android.synthetic.main.lic_receipt_dialog.back_tv
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    var dobTv:EditText?=null
    var amountEdt:EditText?=null
    var dateEdt:EditText?=null
    var nameEdt:EditText?=null
    var nameField: TextInputLayout?=null
    var amountField: TextInputLayout?=null
    var dateField: TextInputLayout?=null
    var submitBtn: Button?=null
    private var dateServerFormat: SimpleDateFormat? = null
    private var dob: String? = null
    private var startDateDay = 0
    private var startDateMonth = 0
    private var startDateYear = 0
    private var currentDate: Calendar? = null
    private var dobDateCalendar: Calendar? = null

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
        dobTv=view.findViewById(R.id.dobTv)
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

        initializeDates()
        dobTv!!.setOnClickListener {
            openDatePicker()
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

    private fun initializeDates() {
        //Date formats
        dateServerFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        //default start Date
        dobDateCalendar = Calendar.getInstance()
        currentDate = Calendar.getInstance()
        startDateDay = currentDate!!.get(Calendar.DAY_OF_MONTH)
        startDateMonth = currentDate!!.get(Calendar.MONTH)
        startDateYear = currentDate!!.get(Calendar.YEAR)

        dob = dateServerFormat!!.format(currentDate!!.getTime())
    }

    private fun openDatePicker() {
        //Date formats
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { view, year, monthOfYear, dayOfMonth ->
                dobDateCalendar!![year, monthOfYear] = dayOfMonth
                dobTv!!.setText(dateServerFormat!!.format(dobDateCalendar!!.time))
            }, startDateYear, startDateMonth, startDateDay
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = currentDate!!.timeInMillis
        datePickerDialog.show()
    }

    private fun checkValidation() {
        var policy=policyEdt!!.text.toString().trim()
        var email=emailEdt!!.text.toString().trim()
        var dob=dobTv!!.text.toString().trim()

        if(policy.isEmpty()){
            Toast.makeText(context, "Please enter policy number", Toast.LENGTH_SHORT).show()
            return
        }else if(!Common.isEmailValid(email)){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
            return
        }else if(dob.isEmpty()){
            Toast.makeText(context, "Please select DOB", Toast.LENGTH_SHORT).show()
            return
        }else{
            var loginModel = LoginModel()
            loginModel = MyPreferences.getLoginData(loginModel, context)
            val request = LicRequest()
            request.AgentCode = loginModel.Data.DoneCardUser
            request.PolicyNumber = policy
            request.Email = email
            request.Dob = dob
            request.Ad2 = dob
            getCredentials(request)
        }
    }

    class LicRequest{
        val Mode = "offline"
        val Merchant= ApiConstants.MerchantId
        var AgentCode: String? = null
        var PolicyNumber: String? = null
        var Email: String? = null
        var Dob: String? = null
        var userData: String? = null
        var token: String? = null

        var Lattitude: String? = "28.351839"
        var Longitude: String? = "79.409561"
        var BillNumber: String? = null
        var Ad1: String? = null
        var Ad2: String? = null
        var Ad3: String? = null
        var Amount: Float? = null
        var bill_Fetch: BillResponse.Bill_Fetch?=null

    }

    class BillResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
        var licBill: List<LicBill>? = null
        var billDetail: List<BillDetail>? = null

        /*{"status":true,"response_code":0,"operatorid":"0","ackno":61013641,
        "message":"You Bill Payment for Life Insurance Corporation of Amount 100 is Processed successful.",
        "opening":"360351.35","closing":"360251.73"}*/

        inner class LicBill {
            var billNumber: String? = null
            var billDate: String? = null
            val dueDate: String? = null
            var billAmount: Float? = null
            var acceptPartPay: String? = null
            var customerName: String? = null
            var ad2: String? = null
            var ad3: String? = null
            var bill_Fetch:Bill_Fetch?=null
        }

        inner class Bill_Fetch{
            var billNumber:String?=null
            var billAmount:String?=null
            var billnetamount:String?=null
            var billdate:String?=null
            var acceptPayment:String?=null
            var acceptPartPay:String?=null
            var cellNumber:String?=null
            var dueFrom:String?=null
            var dueTo:String?=null
            var validationId:String?=null
            var billId:String?=null
//            newly added
            var netamount:String?=null
            var customerName:String?=null
            var duedatefromto:String?=null
            var status:Int?=null
        }

        inner class BillDetail{
            var transactionId:String?=null
            var acknowledgementNo:String?=null
            var operatotrId:String?=null
            var amount:String?=null
            var status:String?=null
        }

        /*{"statusCode":"00","statusMessage":"Bill fetch successfully.",
        "licBill":[{"amount":"1324.10","customerName":"Isubu","dueDate":"28/03/2023",
        "bill_Fetch":{},"ad2":"HGAYV12E000693551346B0","ad3":"HGAYV12E000693551346"}]}*/
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
                hideCustomDialog()
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
        amountEdt!!.setText(commonResponse.licBill!!.get(0).bill_Fetch!!.netamount)
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
        licPayBillRequest!!.Amount=commonResponse.licBill!!.get(0).bill_Fetch!!.netamount!!.toFloat()
//        licPayBillRequest!!.Amount=1f
        licPayBillRequest!!.Ad1=emailEdt!!.text.toString().trim()
        licPayBillRequest!!.Ad2=commonResponse.licBill!!.get(0).ad2
        licPayBillRequest!!.Ad3=commonResponse.licBill!!.get(0).ad3
//        commonResponse.licBill!!.get(0).bill_Fetch!!.billAmount="1"
//        commonResponse.licBill!!.get(0).bill_Fetch!!.billnetamount="1"
        licPayBillRequest!!.bill_Fetch=commonResponse.licBill!!.get(0).bill_Fetch

    }


    private fun payBill(){
        var req=Gson().toJson(licPayBillRequest)
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

        try{
            agentCodeTv.text=licPayBillRequest!!.AgentCode
            policyNoTv.text=licPayBillRequest!!.PolicyNumber
            nameTv.text=nameEdt!!.text.toString()
            amountTv.text=commonResponse.billDetail!!.get(0).amount
            txnIdTv.text=commonResponse.billDetail!!.get(0).transactionId
            operatorIdTv.text=commonResponse.billDetail!!.get(0).operatotrId
            statusTv.text=commonResponse.billDetail!!.get(0).status
        }catch (e:NullPointerException){

        }

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
