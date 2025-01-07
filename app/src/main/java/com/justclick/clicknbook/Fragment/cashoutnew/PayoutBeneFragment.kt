package com.justclick.clicknbook.Fragment.cashoutnew

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.cashout.adapter.PayoutTxnDetailAdapter
import com.justclick.clicknbook.Fragment.cashout.model.PayoutRequestModel
import com.justclick.clicknbook.Fragment.cashout.model.PayoutResponse
import com.justclick.clicknbook.Fragment.jctmoney.RapipayAddBeneFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyPreferences
import com.justclick.clicknbook.utils.Words
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File

class PayoutBeneFragment : Fragment(), View.OnClickListener {
    private val All=-1;
    private var titleChangeListener: ToolBarTitleChangeListener? = null
    private var toolBarHideFromFragmentListener: ToolBarHideFromFragmentListener? = null
    private var loginModel: LoginModel? = null
    private var commonParams: CommonParams? = null
    private var commonParamNew: CommonParams? = null
    private var isCheckCredential = false
    private var isApprove=All
    private var beneData: BankListResponse.data? = null
    private var senderDetailResponse: SenderDetailResponse? = null
//    private var senderInfo: SenderDetailResponse.senderDetailInfo? = null
    private var limitTv:TextView?=null
    private var remainingLimit:Float?=0.0f
    private var recipientRecycleView:RecyclerView?=null
    private var mView:View?=null
    val IMPS = "IMPS"
    val NEFT = "NEFT"
    private var TType = IMPS
    private var Pin: String? = ""
    private var paymentDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        commonParams = CommonParams()
        commonParamNew = CommonParams()
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
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_payout_bene, container, false)
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                senderDetailResponse = requireArguments().getSerializable("senderResponse", SenderDetailResponse::class.java)
                commonParams = requireArguments().getSerializable("commonParams", CommonParams::class.java)
            }else{
                senderDetailResponse = requireArguments().getSerializable("senderResponse") as SenderDetailResponse?
                commonParams = requireArguments().getSerializable("commonParams") as CommonParams?
            }
            checkCredential()
            initializeViews(mView!!)
        }
        return mView
    }

    private fun checkCredential() {
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = CheckCredentialRequest()
        request.agentCode = loginModel!!.Data.DoneCardUser

        NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().getPayoutNew(ApiConstants.Authentication, request,
            "", ""), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerCredential(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerCredential(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    commonParamNew!!.token = senderResponse.credentialData[0].token
                    commonParamNew!!.userData = senderResponse.credentialData[0].userData
                    getBankDetails(true)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
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

    public fun getBankDetails(isLimit: Boolean) {
        if (isLimit){checkLimit()}
        val bankDetailsRequest = BankDetailsRequest()
        bankDetailsRequest.isApprove = All;
        bankDetailsRequest.AgentCode = loginModel!!.Data.DoneCardUser
        bankDetailsRequest.start = 1
        bankDetailsRequest.length = 50

        NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().getPayoutNew(ApiConstants.getagentbankdetails, bankDetailsRequest,
            commonParamNew!!.userData, "Bearer "+commonParamNew!!.token), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeViews(view: View) {
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val face = Common.TextViewTypeFace(context)
        recipientRecycleView=view.findViewById(R.id.recipientRecycleView)
        limitTv=view.findViewById(R.id.limitTv)
        view.findViewById<TextView>(R.id.senderNameTv).setText("")
        view.findViewById<TextView>(R.id.senderMobileTv).setText(commonParams!!.mobile)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        view.findViewById<View>(R.id.addBankTv).setOnClickListener(this)
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), BankListResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode.equals("00") && senderResponse.data!=null) {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    recipientRecycleView!!.setAdapter(getAdapter(senderResponse.data))
                    recipientRecycleView!!.setLayoutManager(LinearLayoutManager(context))
                } else if (senderResponse.statusCode.equals("01") && senderResponse.data!=null) {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    recipientRecycleView!!.setAdapter(getAdapter(senderResponse.data))
                    recipientRecycleView!!.setLayoutManager(LinearLayoutManager(context))
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAdapter(data: ArrayList<BankListResponse.data>?): RecyclerView.Adapter<*>? {
        return BankDetailsListAdapter(context,
            { view, list, position ->
                if (view.id == R.id.payNowTv) {
                    //                    Toast.makeText(context, "Pay Now", Toast.LENGTH_SHORT).show();
                    beneData=list[position]
                    if (beneData!!.accountNo == null || beneData!!.accountNo.length == 0) {
                         Toast.makeText(
                             context, """
      You can't do any transaction to this beneficiary.
      Please add new beneficiary.
      """.trimIndent(), Toast.LENGTH_LONG
                         ).show()
                    } else {
                        validateCredential()
                    }
                } else if (view.id == R.id.benVerifyTv) {
                    beneData = list[position]
                    if (beneData!!.accountNo == null || beneData!!.accountNo.length == 0) {
                        Toast.makeText(
                            context,
                            "You can't validate this beneficiary.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        validateAccount(ApiConstants.ValidateAccount)
                    }
                } else {
                    openDeleteConfirmationDialog(
                        "Confirm Delete Request",
                        "Please confirm," +
                                " you want to delete this beneficiary.",
                        "Cancel",
                        "Delete",
                        list[position]
                    )
                }
            }, data)
    }

    private fun validateAccount(method: String) {
        val requestModel = AddValidateAccountRequest()
        requestModel.agentCode = loginModel!!.Data.DoneCardUser
        requestModel.sessionKey = commonParams!!.sessionKey
        requestModel.sessionRefId = commonParams!!.sessionRefNo
        requestModel.bankName = beneData!!.bankName
        requestModel.setBankId(beneData!!.bankid) // new change
        requestModel.accountHolderName = beneData!!.accountHolderName
        requestModel.accountNumber = beneData!!.accountNo
        requestModel.confirmAccountNumber = beneData!!.accountNo
        requestModel.ifscCode = beneData!!.ifscCode
        requestModel.mobile = beneData!!.mobileNo
        requestModel.apiService = commonParams!!.apiService
        requestModel.setAddress(commonParams!!.address) // new change
        requestModel.setPinCode(commonParams!!.pinCode) // new change
        requestModel.setState(commonParams!!.state) // new change
        requestModel.setCity(commonParams!!.city) // new change
        requestModel.setStatecode(commonParams!!.statecode) // new change
        requestModel.setGst_state(commonParams!!.statecode) // new change
        requestModel.setUserdata(commonParams!!.userData) // new change
        requestModel.verified = "1"
        requestModel.isVerified = "1"
        NetworkCall().callRapipayServiceHeader(
            requestModel, method, context,
            { response: ResponseBody?, responseCode: Int ->
                if (response != null) {
                    responseHandlerVerify(response)
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                        .show()
                }
            }, commonParams!!.userData, commonParams!!.token
        )
    }

    private fun responseHandlerVerify(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                RapipayAddBeneFragment.ValidateBeneResponse::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    addAccount(senderResponse.beneficiaryName)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAccount(name: String) {
        NetworkCall().callService(
            NetworkCall.getPayoutNewApiInterface().addAccountPartNoFile(
                ApiConstants.addPayoutBene,
                createRequestBody(loginModel!!.Data.DoneCardUser),
                createRequestBody(beneData!!.id.toString()),
                createRequestBody(beneData!!.bankName),
                createRequestBody(beneData!!.bankid),
                createRequestBody(beneData!!.ifscCode),
                createRequestBody(beneData!!.accountNo),
                createRequestBody(beneData!!.mobileNo),
                createRequestBody(name),
                createRequestBody("App"),
                createRequestBody("1"),
                commonParamNew!!.userData,
                "Bearer " + commonParamNew!!.token
            ),
            requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerAdd(response)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.response_failure_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createRequestBody(userId: String): RequestBody? {
        return RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            userId
        )
    }

    private fun sendFile(paramName: String, imageFile: File?): MultipartBody.Part? {
        if (imageFile == null) return null

        val requestFile: RequestBody = RequestBody.create(
            "image/jpg".toMediaType(),
            imageFile
        )

        // MultipartBody.Part is used to send also the actual file name
        return createFormData(paramName, imageFile.name, requestFile)
    }

    private fun responseHandlerAdd(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(
                response.string(),
                RapipayAddBeneFragment.ValidateBeneResponse::class.java
            )
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    getBankDetails(false)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateCredential() {
        var loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        val request = CheckCredentialRequest()
        request.agentCode = loginModel.Data.DoneCardUser
        request.setApiService(commonParams!!.apiService)
        NetworkCall().callPayoutService(request, ApiConstants.ValidateCredential, context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerCredentialPay(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerCredentialPay(response: ResponseBody, i: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), ValidateCredentialResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    if(MyCustomDialog.isDialogShowing()){
//                    MyCustomDialog.setDialogMessage("Please wait transaction running...")}
                    openDialog(senderResponse.credentialData[0], senderResponse.credentialData[0].payoutlimit)
//                    makeTransaction(senderResponse.credentialData[0].token, senderResponse.credentialData[0].userData)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
//                    hideCustomDialog()
                }
            } else {
//                hideCustomDialog()
                Toast.makeText(context, "Validation failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
//            hideCustomDialog()
            Toast.makeText(context, "Exception occurs", Toast.LENGTH_SHORT).show()
        }
    }

    var RemainingLimt:Float?= 0.0f
    private var amount = 0
    private fun openDialog(credentialResponse: ValidateCredentialResponse.credentialData, remainingLimt: Float) {
        TType = IMPS
        RemainingLimt = remainingLimit
        paymentDialog = Dialog(requireContext())
        paymentDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentDialog!!.setContentView(R.layout.jct_paynow_dialog)
        val window = paymentDialog!!.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        val amountEdt = paymentDialog!!.findViewById<EditText>(R.id.amountEdt)
        var amountWordsTv:TextView = paymentDialog!!.findViewById(R.id.amountWordsTv)
        val otpTv = paymentDialog!!.findViewById<TextView>(R.id.otpTv)
        val cancelTv = paymentDialog!!.findViewById<TextView>(R.id.cancelTv)
        val payNowTv = paymentDialog!!.findViewById<TextView>(R.id.payNowTv)
        val limitTv = paymentDialog!!.findViewById<TextView>(R.id.limitTv)
        val limitDetailLin = paymentDialog!!.findViewById<LinearLayout>(R.id.limitDetailLin)
        limitDetailLin.visibility=View.GONE
        val transactionTypeIMPSTv = paymentDialog!!.findViewById<TextView>(R.id.transactionTypeIMPSTv)
        val transactionTypeNEFTTv = paymentDialog!!.findViewById<TextView>(R.id.transactionTypeNEFTTv)

        otpTv.visibility=View.GONE
        payNowTv.visibility=View.VISIBLE

        amountEdt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
//                    val number = text.toString().toLong()
//                    val returnz = Words.convert(number)
                    val returnz = Words.convertToIndianCurrency(text.toString());
                    amountWordsTv.setText(returnz)
                } catch (e: NumberFormatException) {
                    amountWordsTv.setText("")
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        transactionTypeIMPSTv.setOnClickListener {
            TType = IMPS
            transactionTypeIMPSTv.setBackgroundResource(R.drawable.blue_rect_button_background)
            transactionTypeIMPSTv.setTextColor(resources.getColor(R.color.color_white))
            transactionTypeNEFTTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner)
            transactionTypeNEFTTv.setTextColor(resources.getColor(R.color.dark_blue_color))
        }
        transactionTypeNEFTTv.setOnClickListener {
            TType = NEFT
            transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner)
            transactionTypeIMPSTv.setTextColor(resources.getColor(R.color.dark_blue_color))
            transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background)
            transactionTypeNEFTTv.setTextColor(resources.getColor(R.color.color_white))
        }
        limitTv.text = "Remaining limit is: $remainingLimit"
        cancelTv.setOnClickListener { paymentDialog!!.cancel() }
        payNowTv.setOnClickListener {
            if (Common.isdecimalvalid(amountEdt!!.getText().toString().trim { it <= ' ' })) {
                amount = amountEdt!!.getText().toString().toInt()
                if (amount > remainingLimit!!) {
                    Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show()
                } else if (amount < 100) {
                    amountEdt!!.setError(" ")
                    Toast.makeText(context, "Please enter amount greater than 100", Toast.LENGTH_SHORT).show()
                } else {
                    amountEdt!!.setEnabled(true)
                    //                        paymentDialog.dismiss();
                    openTxnConfirmationDialogNew(amount,beneData!!.accountNo,beneData!!.accountHolderName,
                        credentialResponse.token,credentialResponse.userData)
                }
            } else {
                Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show()
            }
        }
        paymentDialog!!.show()
    }

    private fun openTxnConfirmationDialog(title: String, message: String,
                                          cancel: String, submit: String, token: String, userData: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.confirmation_dialog_layout)
        (dialog.findViewById<View>(R.id.title_tv) as TextView).text = title
        (dialog.findViewById<View>(R.id.confirm_message_tv) as TextView).text = message
        (dialog.findViewById<View>(R.id.cancel_tv) as TextView).text = cancel
        (dialog.findViewById<View>(R.id.submit_tv) as TextView).text = submit
        dialog.findViewById<View>(R.id.remark_edt).visibility = View.GONE
        dialog.findViewById<View>(R.id.cancel_tv).setOnClickListener { dialog.dismiss() }
        val submitTv=dialog.findViewById<View>(R.id.submit_tv)
        submitTv.setOnClickListener {
            dialog.dismiss()
            Common.preventFrequentClick(submitTv)
            makeTransaction(token,userData)
        }
        dialog.show()
    }

    private fun openTxnConfirmationDialogNew(amount: Int, account: String, name: String,
                                             token: String, userData: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dmt_txn_confirmation_dialog)
        (dialog.findViewById<View>(R.id.amountTv) as TextView).text = amount.toString()
        (dialog.findViewById<View>(R.id.accountTv) as TextView).text = account
        (dialog.findViewById<View>(R.id.nameTv) as TextView).text = name
        dialog.findViewById<View>(R.id.cancel_tv).setOnClickListener { dialog.dismiss() }
        val submitTv=dialog.findViewById<View>(R.id.submit_tv)
        submitTv.setOnClickListener {
            dialog.dismiss()
            Common.preventFrequentClick(submitTv)
            makeTransaction(token,userData)
        }
        dialog.show()
    }

    private fun makeTransaction(token:String, userData:String) {
        Common.hideSoftInputFromDialog(paymentDialog, context)
        val requestModel = PayoutRequestModel()
        requestModel.AgentCode = loginModel!!.Data.DoneCardUser
        requestModel.MobileNumber = beneData!!.mobileNo
        requestModel.Amount = amount
//        requestModel.Amount = 0
        requestModel.AccountNumber = beneData!!.accountNo
        requestModel.Name = beneData!!.agencyName
        requestModel.BankName = beneData!!.bankName
        requestModel.IFSC = beneData!!.ifscCode
        requestModel.BeniId = beneData!!.id.toString()
        requestModel.TransferType = TType
        requestModel.Email= loginModel!!.Data.Email
        requestModel.ApiService= commonParams!!.apiService
        var json=Gson().toJson(requestModel)

        NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().getPayoutTxnWithHeader(ApiConstants.TransactionPayout, requestModel,
            userData, "Bearer "+token), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerTransaction(response)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
        Pin = ""
    }

    private fun responseHandlerTransaction(response: ResponseBody) {
        try {
            val senderResponse = Gson().fromJson(response.string(), PayoutResponse::class.java)
            //                checkRadionButton(Pin);
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00" || senderResponse.statusCode == "02") {
                        Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
//                        pinDialog.dismiss();
                    paymentDialog!!.dismiss()
                    //                        commonParams.setSessionKey(senderResponse.getSessionKey());
//                        commonParams.setSessionRefNo(senderResponse.getSessionRefId());
//                    getBankDetails()
                    try {
                        openReceipt(senderResponse)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Enable to show receipt", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                        pinDialog.dismiss();
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){

        }
    }

    private fun openReceipt(senderResponse: PayoutResponse) {
        val dialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.jct_money_receipt_dialog_rapipay)
        val window = dialog.window
        window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        val beneTv = dialog.findViewById<TextView>(R.id.beneTv)
        val bankNameTv = dialog.findViewById<TextView>(R.id.bankNameTv)
        val accountNoTv = dialog.findViewById<TextView>(R.id.accountNoTv)
        val ifscTv = dialog.findViewById<TextView>(R.id.ifscTv)
        val txnAmountTv = dialog.findViewById<TextView>(R.id.txnAmountTv)
        val txnTypeTv = dialog.findViewById<TextView>(R.id.txnTypeTv)
        val txnRecycleView: RecyclerView = dialog.findViewById(R.id.txnRecycleView)
        val txnMsgTv: TextView = dialog.findViewById(R.id.txnMsgTv)
        val errorTv = dialog.findViewById<TextView>(R.id.errorTv)
        val contentLin = dialog.findViewById<LinearLayout>(R.id.contentLin)
        beneTv.text = senderResponse.bankDetails?.get(0)?.beniName
        bankNameTv.text = senderResponse.bankDetails?.get(0)?.bank
        accountNoTv.text = senderResponse.bankDetails?.get(0)?.accountNumber
        txnAmountTv.text = senderResponse.bankDetails?.get(0)?.amount.toString() + ""
        txnTypeTv.text = senderResponse.bankDetails?.get(0)?.txnType
        ifscTv.text = senderResponse.bankDetails?.get(0)?.ifscCode
//        txnMsgTv.text = senderResponse.statusMessage
        val adapter = PayoutTxnDetailAdapter(context, PayoutTxnDetailAdapter.OnRecyclerItemClickListener { view, list, position -> }, senderResponse.transactionDetails)
        txnRecycleView.adapter = adapter
        txnRecycleView.layoutManager = LinearLayoutManager(context)
        dialog.findViewById<View>(R.id.print_tv).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun openDeleteConfirmationDialog(
        title: String, message: String,
        cancel: String, delete: String,
        listItem: BankListResponse.data
    ) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.confirmation_dialog_layout)
        (dialog.findViewById<View>(R.id.title_tv) as TextView).text = title
        (dialog.findViewById<View>(R.id.confirm_message_tv) as TextView).text = message
        (dialog.findViewById<View>(R.id.cancel_tv) as TextView).text = cancel
        (dialog.findViewById<View>(R.id.submit_tv) as TextView).text = delete
        dialog.findViewById<View>(R.id.remark_edt).visibility = View.GONE
        dialog.findViewById<View>(R.id.cancel_tv).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.submit_tv).setOnClickListener {
            dialog.dismiss()
            deleteBene(ApiConstants.DeleteBenificiary, listItem)
        }
        dialog.show()
    }

    private fun deleteBene(
        deleteBenificiary: String,
        listItem: BankListResponse.data
    ) {
        val deleteRequest = DeleteAccountRequest()
        deleteRequest.beneid = listItem.id
        deleteRequest.AgentCode = loginModel!!.Data.DoneCardUser

        NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().getPayoutNew(ApiConstants.deletebenepayout, deleteRequest,
            commonParamNew!!.userData, "Bearer "+commonParamNew!!.token), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerDelete(response, 1)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerDelete(response: ResponseBody, i: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CommonRapiResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode.equals("00") ) {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                    getBankDetails(false)
                }else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class PayoutLimit{
        public var AgentCode:String?=null
        public var MerchantId=ApiConstants.MerchantId
        public var userid:String?=null
        public var userdata:String?=null
        public var token:String?=null
    }
    class PayoutLimitResponse{
        var statusCode:String?=null
        var statusMessage:String?=null
        var agencycode:String?=null
        var availableLimit:Float?=null
        var statementBalance:Float?=null
    }

    private fun checkLimit() {
        val request = PayoutLimit()
        request.AgentCode=loginModel!!.Data.DoneCardUser
        request.userid=loginModel!!.Data.UserId
        request.userdata=commonParamNew!!.userData
        request.token=commonParamNew!!.token

        NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().getPayoutTxnWithHeader(ApiConstants.payoutlimit, request,
            commonParamNew!!.userData, "Bearer "+commonParamNew!!.token), requireContext(), false
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerLimit(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
            } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerLimit(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), PayoutLimitResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.agencycode,Toast.LENGTH_SHORT).show()
                    remainingLimit= senderResponse.availableLimit
                    limitTv!!.text = remainingLimit.toString()
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_arrow -> parentFragmentManager.popBackStack()
            R.id.addBankTv -> {
                val bundle = Bundle()
                bundle.putSerializable("commonParams", commonParams)
                bundle.putSerializable("commonParamNew", commonParamNew)
                val fragment = AddAccountFragment()
                fragment.arguments = bundle
                (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
            }
            R.id.get_tv -> {
                Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
//                Common.preventFrequentClick(getTv)
                if (Common.checkInternetConnection(context)) {
                    if (isCheckCredential) {
                        checkCredential()
                    } else {
                        getBankDetails(true)
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validate(): Boolean {
        /*if (number_edt!!.text.toString().length < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true
    }
}