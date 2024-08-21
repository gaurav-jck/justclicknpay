package com.justclick.clicknbook.Fragment.cashout

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.cashout.adapter.PayoutTxnDetailAdapter
import com.justclick.clicknbook.Fragment.cashout.model.PayoutRequestModel
import com.justclick.clicknbook.Fragment.cashout.model.PayoutResponse
import com.justclick.clicknbook.Fragment.jctmoney.adapter.RapipayRecipientListAdapter
import com.justclick.clicknbook.Fragment.jctmoney.request.*
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse.benificiaryDetailData
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse.senderDetailInfo
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import com.justclick.clicknbook.utils.Words
import okhttp3.ResponseBody
import java.util.*

class SenderDetailFragment : Fragment(), View.OnClickListener {
  private val GetSenderDetails = 0
  private val DeleteRecipient = 1
  private val Transaction = 2
  private val VerifyAccount = 4
  private var titleChangeListener: ToolBarTitleChangeListener? = null
  private var senderNameTv: TextView? = null
  private var senderMobileTv: TextView? = null
  private var senderKycTv: TextView? = null
  private var addRecTv: TextView? = null
  private var limitTv: TextView? = null
  private var tv0: TextView? = null
  private var tv1: TextView? = null
  private var tv2: TextView? = null
  private var tv3: TextView? = null
  private var tv4: TextView? = null
  private var tv5: TextView? = null
  private var tv6: TextView? = null
  private var tv7: TextView? = null
  private var tv8: TextView? = null
  private var tv9: TextView? = null
  private var jctIdTv: TextView? = null
  private var otpEdt: TextView? = null
  private var amountEdt: EditText? = null
  private var clearTv: ImageView? = null
  private var cancel: ImageView? = null
  private var radioButton1: ImageView? = null
  private var radioButton2: ImageView? = null
  private var radioButton3: ImageView? = null
  private var radioButton4: ImageView? = null
  private var recipientRecycleView: RecyclerView? = null
  private var loginModel: LoginModel? = null
  private var senderDetailResponse: SenderDetailResponse? = null
  private var senderInfo: senderDetailInfo? = null
  private var beneData: benificiaryDetailData? = null
  private val adapter: RapipayRecipientListAdapter? = null
  private var amount = 0
  val IMPS = "IMPS"
  val NEFT = "NEFT"
  private var TType = IMPS
  private var Pin = ""
  private var paymentDialog: Dialog? = null
  private var pinDialog: Dialog? = null
  private var currentListItemPosition = 0
  private var isGetDetail = false
  private var RemainingLimt = 0f
  private var commonParams: CommonParams? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //        commonParams=new CommonParams();
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    isGetDetail = false
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      titleChangeListener = context as ToolBarTitleChangeListener
    } catch (e: ClassCastException) {
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    // Inflate the layout for this fragment
    val myView = inflater.inflate(R.layout.fragment_jct_money_sender_detail, container, false)
    if (requireArguments().getSerializable("senderResponse") != null) {
      senderDetailResponse = requireArguments().getSerializable("senderResponse") as SenderDetailResponse?
      commonParams = requireArguments().getSerializable("commonParams") as CommonParams?
      senderInfo = senderDetailResponse!!.senderDetailInfo[0]
    }
    try {
      initializeViews(myView)
    } catch (e: Exception) {
      Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
    }

    return myView

  }

  private fun initializeViews(view: View?) {
    senderNameTv = view!!.findViewById(R.id.senderNameTv)
    senderMobileTv = view.findViewById(R.id.senderMobileTv)
    senderKycTv = view.findViewById(R.id.senderKycTv)
    addRecTv = view.findViewById(R.id.addRecTv)
    limitTv = view.findViewById(R.id.limitTv)
    recipientRecycleView = view.findViewById(R.id.recipientRecycleView)
    loginModel = LoginModel()
    loginModel = MyPreferences.getLoginData(loginModel, context)
    val face = Common.TextViewTypeFace(context)
    view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
    senderNameTv!!.setText(senderInfo!!.name)
    senderMobileTv!!.setText(senderInfo!!.mobile)
    limitTv!!.setText("Rs. " + senderDetailResponse!!.remainingLimit + "")
    senderKycTv!!.setText(commonParams!!.kycStatus)
    recipientRecycleView!!.setAdapter(getAdapter(senderDetailResponse!!.benificiaryDetailData))
    recipientRecycleView!!.setLayoutManager(LinearLayoutManager(context))
    addRecTv!!.setOnClickListener(this)
    view.findViewById<View>(R.id.limitLin).visibility= View.GONE
//    view.findViewById<View>(R.id.limitLin).visibility= View.VISIBLE
    view.findViewById<View>(R.id.limitDetailLin).visibility= View.GONE
  }

  private fun getAdapter(benificiaryDetailData: ArrayList<benificiaryDetailData>): RapipayRecipientListAdapter {
    return RapipayRecipientListAdapter(context, RapipayRecipientListAdapter.OnRecyclerItemClickListener { view, list, position ->
      if (view.id == R.id.payNowTv) {
//                    Toast.makeText(context, "Pay Now", Toast.LENGTH_SHORT).show();
        beneData = list[position]
        if (beneData!!.accountNumber == null || beneData!!.accountNumber.length == 0) {
          Toast.makeText(context, """
     You can't do any transaction to this beneficiary.
     Please add new beneficiary.
     """.trimIndent(), Toast.LENGTH_SHORT).show()
        } else {
          validateCredential()
//                    openDialog(senderDetailResponse!!.remainingLimit)
        }
      } else if (view.id == R.id.validateTv) {
        beneData = list[position]
        if (beneData!!.accountNumber == null || beneData!!.accountNumber.length == 0) {
          Toast.makeText(context, "You can't validate this beneficiary.", Toast.LENGTH_SHORT).show()
        } else {
          verifyAccount(list[position])
          currentListItemPosition = position
        }
      } else {
        openDeleteConfirmationDialog("Confirm Delete Request", "Please confirm," +
                " you want to delete this beneficiary.", "Cancel", "Delete", list[position])
      }
    }, benificiaryDetailData, senderInfo!!.mobile)
  }

  private fun verifyAccount(beneData: benificiaryDetailData) {
    val requestModel = AddBeneRequest()
    requestModel.agentCode = loginModel!!.Data.DoneCardUser
    requestModel.sessionKey = commonParams!!.sessionKey
    requestModel.setMode("App")
    requestModel.sessionRefId = commonParams!!.sessionRefNo
    requestModel.bankName = beneData.bankName
    requestModel.accountHolderName = beneData.accountHolderName
    requestModel.accountNumber = beneData.accountNumber
    requestModel.ifscCode = beneData.ifsc
    requestModel.mobile = senderInfo!!.mobile
    requestModel.apiService = commonParams!!.apiService
    //  https://remittance.justclicknpay.com/api/payments/ValidateAccuont
//{"AccountHolderName":"atuulll","AccountNumber":"135301507733","AgentCode":"JC0A13387","BankName":"ICICI BANK LIMITED",
// "IfscCode":"ICIC0000001","MerchantId":"JUSTCLICKTRAVELS","Mobile":"8468862808","Mode":"App","SessionKey":"DBS210106145635S096280609627","SessionRefId":"V016532537"}
    NetworkCall().callRapipayServiceHeader(requestModel, ApiConstants.ValidateAccount, context,
      { response, responseCode ->
        if (response != null) {
          responseHandler(response, VerifyAccount)
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      }, commonParams!!.userData, commonParams!!.token)
  }

  private fun openDeleteConfirmationDialog(title: String, message: String,
                                           cancel: String, delete: String,
                                           listItem: benificiaryDetailData) {
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
      deleteBene(ApiConstants.DeleteBenificiary, listItem, DeleteRecipient)
      //                Toast.makeText(context, "Delete Ben", Toast.LENGTH_SHORT).show();
    }
    dialog.show()
  }

  private fun deleteBene(deleteBenificiary: String, listItem: benificiaryDetailData, deleteRecipient: Int) {
    val request = DeleteBeneRequest()
    request.beniId = listItem.beneid
    request.agentCode = loginModel!!.Data.DoneCardUser
    request.sessionKey = commonParams!!.sessionKey
    request.sessionRefId = commonParams!!.sessionRefNo
    request.apiService = commonParams!!.apiService
    request.setBankId(listItem.getBankid());
    request.setMobile(senderInfo!!.getMobile());
    //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
    NetworkCall().callRapipayServiceHeader(request, ApiConstants.DeleteBenificiary, context,
      { response, responseCode ->
        if (response != null) {
          responseHandler(response, deleteRecipient)
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      }, commonParams!!.userData, commonParams!!.token)
  }

  private fun openDialog(credentialResponse: CheckCredentialResponse.credentialData, remainingLimt: Float) {
    TType = IMPS
    RemainingLimt = remainingLimt
    paymentDialog = Dialog(requireContext())
    paymentDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    paymentDialog!!.setContentView(R.layout.jct_paynow_dialog)
    val window = paymentDialog!!.window
    window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT)
    amountEdt = paymentDialog!!.findViewById(R.id.amountEdt)
    var amountWordsTv:TextView = paymentDialog!!.findViewById(R.id.amountWordsTv)
    val otpEdt = paymentDialog!!.findViewById<EditText>(R.id.otpEdt)
    val cancelTv = paymentDialog!!.findViewById<TextView>(R.id.cancelTv)
    val payNowTv = paymentDialog!!.findViewById<TextView>(R.id.payNowTv)
    val limitTv = paymentDialog!!.findViewById<TextView>(R.id.limitTv)
    val limitDetailLin = paymentDialog!!.findViewById<LinearLayout>(R.id.limitDetailLin)
    limitDetailLin.visibility=View.GONE
    val transactionTypeIMPSTv = paymentDialog!!.findViewById<TextView>(R.id.transactionTypeIMPSTv)
    val transactionTypeNEFTTv = paymentDialog!!.findViewById<TextView>(R.id.transactionTypeNEFTTv)

    amountEdt!!.addTextChangedListener(object :TextWatcher{
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      }

      override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        try {
          val number = text.toString().toLong()
          val returnz = Words.convert(number)
          amountWordsTv.setText(returnz)
        } catch (e: NumberFormatException) {
          amountWordsTv.setText("")
        }
      }

      override fun afterTextChanged(p0: Editable?) {
      }

    })

    if (beneData!!.isIMPS != "Y") {
      TType = NEFT
      transactionTypeIMPSTv.visibility = View.GONE
      transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background)
      transactionTypeNEFTTv.setTextColor(resources.getColor(R.color.color_white))
    } else if (beneData!!.isNEFT != "Y") {
      transactionTypeNEFTTv.visibility = View.GONE
    }
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
    limitTv.text = "Remaining limit is: $remainingLimt"
    cancelTv.setOnClickListener { paymentDialog!!.cancel() }
    payNowTv.setOnClickListener {
      if (Common.isdecimalvalid(amountEdt!!.getText().toString().trim { it <= ' ' })) {
        amount = amountEdt!!.getText().toString().toInt()
        if (amount > credentialResponse.payoutlimit) {
          Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show()
        } else if (amount < 100) {
          amountEdt!!.setError(" ")
          Toast.makeText(context, "Please enter amount greater than 100", Toast.LENGTH_SHORT).show()
        } else {
          amountEdt!!.setEnabled(true)
          //                        paymentDialog.dismiss();
          openTxnConfirmationDialog("Confirm this transaction", """
     Please confirm, you want to make this transaction.
     Amount=$amount
     Account no=${beneData!!.accountNumber}
     Name=${beneData!!.accountHolderName}
     """.trimIndent(),
            "Cancel", "Submit",credentialResponse.token,credentialResponse.userData)
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
    dialog.findViewById<View>(R.id.submit_tv).setOnClickListener {
      dialog.dismiss()
      makeTransaction(token,userData)
    }
    dialog.show()
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
        responseHandlerCredential(response, 1) //https://remittance.justclicknpay.com/api/payments/CheckCredential
      } else {      //{"AgentCode":"JC0A13387","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun responseHandlerCredential(response: ResponseBody, i: Int) {
    try {
      val senderResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
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

  private fun openOtpDialog(remainingLimt: Float) {
    pinDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
    pinDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
    pinDialog!!.setContentView(R.layout.pin_otp)
    val window = pinDialog!!.window
    window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.MATCH_PARENT)
    jctIdTv = pinDialog!!.findViewById(R.id.jctIdTv)
    otpEdt = pinDialog!!.findViewById(R.id.otpEdt)
    tv0 = pinDialog!!.findViewById(R.id.tv0)
    tv1 = pinDialog!!.findViewById(R.id.tv1)
    tv2 = pinDialog!!.findViewById(R.id.tv2)
    tv3 = pinDialog!!.findViewById(R.id.tv3)
    tv4 = pinDialog!!.findViewById(R.id.tv4)
    tv5 = pinDialog!!.findViewById(R.id.tv5)
    tv6 = pinDialog!!.findViewById(R.id.tv6)
    tv7 = pinDialog!!.findViewById(R.id.tv7)
    tv8 = pinDialog!!.findViewById(R.id.tv8)
    tv9 = pinDialog!!.findViewById(R.id.tv9)
    clearTv = pinDialog!!.findViewById(R.id.clearTv)
    cancel = pinDialog!!.findViewById(R.id.cancel)
    radioButton1 = pinDialog!!.findViewById(R.id.radioButton1)
    radioButton2 = pinDialog!!.findViewById(R.id.radioButton2)
    radioButton3 = pinDialog!!.findViewById(R.id.radioButton3)
    radioButton4 = pinDialog!!.findViewById(R.id.radioButton4)
    jctIdTv!!.setText(loginModel!!.Data.DoneCardUser)
    tv0!!.setOnClickListener(this)
    tv1!!.setOnClickListener(this)
    tv2!!.setOnClickListener(this)
    tv3!!.setOnClickListener(this)
    tv4!!.setOnClickListener(this)
    tv5!!.setOnClickListener(this)
    tv6!!.setOnClickListener(this)
    tv7!!.setOnClickListener(this)
    tv8!!.setOnClickListener(this)
    tv9!!.setOnClickListener(this)
    clearTv!!.setOnClickListener(this)
    cancel!!.setOnClickListener(View.OnClickListener {
      Pin = ""
      pinDialog!!.cancel()
    })
    pinDialog!!.show()
  }

  fun getSenderDetail(type: Int) {
    val jctMoneySenderRequestModel = SenderDetailRequest()
    jctMoneySenderRequestModel.mobile = senderInfo!!.mobile
    jctMoneySenderRequestModel.agentCode = loginModel!!.Data.DoneCardUser
    jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
    jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
    jctMoneySenderRequestModel.setApiService(commonParams!!.apiService)
    //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
    NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
      { response, responseCode ->
        if (response != null) {
          responseHandler(response, GetSenderDetails)
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      }, commonParams!!.userData, commonParams!!.token)
  }

  private fun responseHandler(response: ResponseBody, TYPE: Int) {
    try {
      hideCustomDialog()
      if (TYPE == GetSenderDetails) {
        val senderResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
        if (senderResponse != null) {
          if (senderResponse.statusCode == "00") {
            commonParams!!.sessionKey = senderResponse.sessionKey
            commonParams!!.sessionRefNo = senderResponse.sessionRefId
            updateSenderDetails(senderResponse)
            //                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
          }
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      } else if (TYPE == DeleteRecipient) {
        val senderResponse = Gson().fromJson(response.string(), CommonRapiResponse::class.java)
        if (senderResponse != null) {
          if (senderResponse.statusCode == "00") {
            Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
            getSenderDetail(GetSenderDetails)
          } else {
            Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
          }
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      } else if (TYPE == Transaction) {
        val senderResponse = Gson().fromJson(response.string(), PayoutResponse::class.java)
        //                checkRadionButton(Pin);
        if (senderResponse != null) {
          if (senderResponse.statusCode == "00" || senderResponse.statusCode == "02") {
//                        Toast.makeText(context,senderResponse.Message,Toast.LENGTH_SHORT).show();
//                        pinDialog.dismiss();
            paymentDialog!!.dismiss()
            //                        commonParams.setSessionKey(senderResponse.getSessionKey());
//                        commonParams.setSessionRefNo(senderResponse.getSessionRefId());
            getSenderDetail(GetSenderDetails)
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
      } else if (TYPE == VerifyAccount) {
        val senderResponse = Gson().fromJson(response.string(), CommonRapiResponse::class.java)
        if (senderResponse != null) {
          if (senderResponse.statusCode == "00") {
            Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
            getSenderDetail(GetSenderDetails)
          } else {
            Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
          }
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      } else {
        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
      }
    } catch (e: Exception) {
      paymentDialog!!.dismiss()
      parentFragmentManager.popBackStack()
      Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
  }

  private fun showCustomDialog() {
    MyCustomDialog.showCustomDialog(context, "Please wait")
  }

  private fun hideCustomDialog() {
    MyCustomDialog.hideCustomDialog()
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

  private fun updateSenderDetails(senderResponse: SenderDetailResponse) {
    if (senderResponse.benificiaryDetailData.size > 0 && senderResponse.benificiaryDetailData.size >= currentListItemPosition) {
      senderResponse.benificiaryDetailData[currentListItemPosition].isVisible = true
    }
    limitTv!!.text = "Rs. " + senderResponse.remainingLimit
    senderDetailResponse!!.remainingLimit = senderResponse.remainingLimit
    senderDetailResponse!!.benificiaryDetailData.clear()
    senderDetailResponse!!.benificiaryDetailData.addAll(senderResponse.benificiaryDetailData)
    recipientRecycleView!!.adapter = getAdapter(senderResponse.benificiaryDetailData)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.back_arrow -> parentFragmentManager.popBackStack()
      R.id.addRecTv -> {
        Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
        Common.preventFrequentClick(addRecTv)
        val bundle = Bundle()
        bundle.putSerializable("Mobile", senderInfo!!.mobile)
        bundle.putSerializable("commonParams", commonParams)
        val fragment = AddBeneFragment()
        fragment.arguments = bundle
        (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(fragment)
      }
      R.id.clearTv -> if (Pin.length < 2) {
        Pin = ""
        checkRadionButton(Pin)
      } else {
        Pin = Pin.substring(0, Pin.length - 1)
        checkRadionButton(Pin)
      }
      R.id.tv0 -> {
        Pin = Pin + 0
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv1 -> {
        Pin = Pin + 1
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv2 -> {
        Pin = Pin + 2
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv3 -> {
        Pin = Pin + 3
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv4 -> {
        Pin = Pin + 4
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv5 -> {
        Pin = Pin + 5
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv6 -> {
        Pin = Pin + 6
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv7 -> {
        Pin = Pin + 7
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv8 -> {
        Pin = Pin + 8
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
      R.id.tv9 -> {
        Pin = Pin + 9
        checkRadionButton(Pin)
        if (Pin.length == 4) {
          makeTransaction("","")
        }
      }
    }
  }

  private fun checkRadionButton(Pin: String) {
    if (Pin.length == 0) {
      radioButton1!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
      radioButton2!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
      radioButton3!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
      radioButton4!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
    } else if (Pin.length == 1) {
      radioButton1!!.setImageDrawable(resources.getDrawable(R.drawable.radio_check_blue))
      radioButton2!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
    } else if (Pin.length == 2) {
      radioButton2!!.setImageDrawable(resources.getDrawable(R.drawable.radio_check_blue))
      radioButton3!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
    } else if (Pin.length == 3) {
      radioButton3!!.setImageDrawable(resources.getDrawable(R.drawable.radio_check_blue))
      radioButton4!!.setImageDrawable(resources.getDrawable(R.drawable.radio_uncheck_blue))
    } else if (Pin.length == 4) {
      radioButton4!!.setImageDrawable(resources.getDrawable(R.drawable.radio_check_blue))
    }
  }

  private fun makeTransaction(token:String, userData:String) {
    Common.hideSoftInputFromDialog(paymentDialog, context)
    val requestModel = PayoutRequestModel()
    requestModel.AgentCode = loginModel!!.Data.DoneCardUser
    requestModel.MobileNumber = senderInfo!!.mobile
    requestModel.Amount = amount
    requestModel.AccountNumber = beneData!!.accountNumber
    requestModel.Name = beneData!!.accountHolderName
    requestModel.BankName = beneData!!.bankName
    requestModel.IFSC = beneData!!.ifsc
    requestModel.BeniId = beneData!!.beneid
    requestModel.TransferType = TType
    requestModel.Email= loginModel!!.Data.Email
    requestModel.ApiService= commonParams!!.apiService

//        responseHandler(null, Transaction);
    NetworkCall().callPayoutTxnServiceHeader(requestModel, ApiConstants.CashfreeTrans, context,
      { response, responseCode ->
        if (response != null) {
          responseHandler(response, Transaction)
        } else {
          Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
      }, userData, token,true)
    Pin = ""
  }

  private fun validate(): Boolean {
    if (EditText(context).text.toString().length < 10) {
      Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
      return false
    }
    return true
  }

  override fun onResume() {
    super.onResume()
    //        if(isGetDetail==false){
//            isGetDetail=true;
//        }else {
//            getSenderDetail(ApiConstants.GetSenderDetails, null, 0);
//        }
  }
}