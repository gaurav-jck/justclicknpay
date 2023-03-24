package com.justclick.clicknbook.Fragment.cashout

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.RapipayAddBeneFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.AddBeneRequest
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.database.DataBaseHelper
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.FragmentBackPressListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddBeneFragment : Fragment(), View.OnClickListener {
    private val AddRecipient = 1
    private val VerifyAccount = 3
    private val activity: Activity? = null
    private var backPress: FragmentBackPressListener? = null
    private var submit_tv: TextView? = null
    private var verifyAccountTv: TextView? = null
    private var user_mobile_edt: EditText? = null
    private var ifscEdt: EditText? = null
    private var name_edt: EditText? = null
    private var account_no_edt: EditText? = null
    private var confirmAccountEdt: EditText? = null
    private var Address: TextInputLayout? = null
    private var bankName: String? = null
    private var Mobile: String? = null
    private var dataBaseHelper: DataBaseHelper? = null
    private var loginModel: LoginModel? = null
    private var ifscByCodeResponse: BankResponse? = null
    private var bankArray: ArrayList<BankResponse>? = null
    private var commonParams: CommonParams? = null
    var atv_bank: AutoCompleteTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBaseHelper = DataBaseHelper(context)
        bankArray = ArrayList()
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        if (arguments != null && requireArguments().getString("Mobile") != null) {
//            user_mobile_edt.setText(getArguments().getString("SenderNumber"));
            Mobile = requireArguments().getString("Mobile")
            commonParams = requireArguments().getSerializable("commonParams") as CommonParams?
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            backPress = context as FragmentBackPressListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_jct_money_add_recipient, container, false)
        initializeViews(view)
        return view
    }

    private fun initializeViews(view: View) {
        submit_tv = view.findViewById(R.id.submit_tv)
        verifyAccountTv = view.findViewById(R.id.verifyAccountTv)
        user_mobile_edt = view.findViewById(R.id.user_mobile_edt)
        ifscEdt = view.findViewById(R.id.ifscEdt)
        Address = view.findViewById(R.id.Address)
        Address!!.setHint("Bank IFSC")
        name_edt = view.findViewById(R.id.name_edt)
        account_no_edt = view.findViewById(R.id.account_no_edt)
        confirmAccountEdt = view.findViewById(R.id.confirmAccountEdt)
        atv_bank = view.findViewById(R.id.atv_bank)
        submit_tv!!.setOnClickListener(this)
        verifyAccountTv!!.setOnClickListener(this)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        user_mobile_edt!!.setText(Mobile)
        user_mobile_edt!!.setEnabled(false)
        setText()
        if (dataBaseHelper!!.jctBankNamesWithIFSC != null && dataBaseHelper!!.jctBankNamesWithIFSC.size > 0) {
            val arr = arrayOfNulls<String>(dataBaseHelper!!.jctBankNamesWithIFSC.size)
            for (p in dataBaseHelper!!.jctBankNamesWithIFSC.indices) {
                arr[p] = dataBaseHelper!!.jctBankNamesWithIFSC[p].Name
            }
            atv_bank!!.setAdapter(getSpinnerAdapter(arr))
        } else {
            bankNames
        }
        atv_bank!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val selection = parent.getItemAtPosition(position) as String
            var pos = -1
            for (i in bankArray!!.indices) {
                if (bankArray!![i].banK_NAME == selection) {
                    pos = i
                    break
                }
            }
            //                if(pos>0){
            try {
                ifscByCodeResponse = bankArray!![pos]
                bankName = atv_bank!!.getText().toString()
                ifscEdt!!.setText(ifscByCodeResponse!!.masteR_IFSC_CODE)
            } catch (e: Exception) {
//                        IFSCCodeEdt.setText("");
//                        ifscByCodeResponse=null;
            }
            /*}else {
                    bankName="";
                    ifscEdt.setText("");
                    ifscByCodeResponse=null;
                    verifyAccountTv.setVisibility(View.GONE);
                }*/
        })
        atv_bank!!.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus -> if (hasFocus) atv_bank!!.showDropDown() })
        atv_bank!!.setOnClickListener(View.OnClickListener { atv_bank!!.showDropDown() })
    }

    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter(requireContext(),
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    //                        JctIfscByCodeResponse relationResponse=new JctIfscByCodeResponse();
//                        relationResponse.Key="";
//                        relationResponse.Name="select-bank";
//                        relationResponse.Digit="A";
    private val bankNames:
            //                        dataBaseHelper.insertJctBankNamesWithIFSC(bankArray);
            Unit
        private get() {
            if (!MyCustomDialog.isDialogShowing()) {
                showCustomDialog()
            }
            val apiService = APIClient.getClientRapipay().create(ApiInterface::class.java)
            val call = apiService.getService(ApiConstants.BASE_URL_RAPIPAY + "api/payments/" + ApiConstants.GetBankName)
            call.enqueue(object : Callback<ArrayList<BankResponse>?> {
                override fun onResponse(call: Call<ArrayList<BankResponse>?>, response: Response<ArrayList<BankResponse>?>) {
                    try {
                        if (response.body() != null && response.body()!!.size > 0) {
                            hideCustomDialog()
                            //                        JctIfscByCodeResponse relationResponse=new JctIfscByCodeResponse();
//                        relationResponse.Key="";
//                        relationResponse.Name="select-bank";
//                        relationResponse.Digit="A";
                            val arr = arrayOfNulls<String>(response.body()!!.size)
                            bankArray!!.addAll(response.body()!!)
                            //                        dataBaseHelper.insertJctBankNamesWithIFSC(bankArray);
                            for (p in response.body()!!.indices) {
                                arr[p] = response.body()!![p].banK_NAME
                            }
                            atv_bank!!.setAdapter(getSpinnerAdapter(arr))
                        } else {
                            hideCustomDialog()
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<BankResponse>?>, t: Throwable) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                }
            })
        }

    private fun setText() {
        val face1 = Common.TitleTypeFace(context)
        val face2 = Common.EditTextTypeFace(context)
        val face3 = Common.TextViewTypeFace(context)
        user_mobile_edt!!.typeface = face2
        ifscEdt!!.typeface = face2
        account_no_edt!!.typeface = face2
        confirmAccountEdt!!.typeface = face2
        name_edt!!.typeface = face2
        submit_tv!!.typeface = face3
        verifyAccountTv!!.typeface = face3
    }

    private fun validate(): Boolean {
        if (atv_bank!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_bank, Toast.LENGTH_SHORT).show()
            return false
        } else if (account_no_edt!!.text.toString().trim { it <= ' ' }.length < 6) {
            Toast.makeText(context, R.string.empty_and_invalid_account_number, Toast.LENGTH_SHORT).show()
            return false
        } else if (confirmAccountEdt!!.text.toString().trim { it <= ' ' } != account_no_edt!!.text.toString().trim { it <= ' ' }) {
            Toast.makeText(context, R.string.empty_and_invalid_account_confirmation, Toast.LENGTH_SHORT).show()
            return false
        } else if (!Common.isIFSCValid(ifscEdt!!.text.toString())) {
            Toast.makeText(context, R.string.empty_and_invalid_ifsc_code, Toast.LENGTH_SHORT).show()
            return false
        } else if (!Common.isNameValid(name_edt!!.text.toString().trim { it <= ' ' })) {
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show()
            return false
        } else if (user_mobile_edt!!.text.toString().length < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_arrow -> parentFragmentManager.popBackStack()
            R.id.submit_tv -> {
                Common.preventFrequentClick(submit_tv)
                if (Common.checkInternetConnection(context)) {
                    if(validate()) {
                        addOrValidateBeneficiary(ApiConstants.AddBenificiary, AddRecipient)
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.verifyAccountTv -> {
                Common.preventFrequentClick(verifyAccountTv)
                if (Common.checkInternetConnection(context)) {
                    if(validate()) {
                        addOrValidateBeneficiary(ApiConstants.ValidateAccount, VerifyAccount)
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addOrValidateBeneficiary(method: String, TYPE: Int) {
        val requestModel = AddBeneRequest()
        requestModel.agentCode = loginModel!!.Data.DoneCardUser
        requestModel.sessionKey = commonParams!!.sessionKey
        requestModel.sessionRefId = commonParams!!.sessionRefNo
        requestModel.bankName = atv_bank!!.text.toString()
        requestModel.setBankId(ifscByCodeResponse!!.getBankId());   // new change
        requestModel.accountHolderName = name_edt!!.text.toString()
        requestModel.accountNumber = account_no_edt!!.text.toString()
        requestModel.confirmAccountNumber = confirmAccountEdt!!.text.toString()
        requestModel.ifscCode = ifscEdt!!.text.toString()
        requestModel.mobile = user_mobile_edt!!.text.toString()
        requestModel.apiService = commonParams!!.apiService
        //  https://remittance.justclicknpay.com/api/payments/AddBenificiary
//{"Mobile": "8468862808","SessionRefId": "V015838345","AccountNumber": "135301507755","ConfirmAccountNumber": "135301507755","IfscCode": "ICIC0000001",
//    "AccountHolderName": "apptest","BankName": "ICICI BANK LIMITED","Mode": "WEB","AgentCode": "JC0A13387","MerchantId": "JUSTCLICKTRAVELS","SessionKey": "DBS210103115407S725580372557"}
        NetworkCall().callRapipayServiceHeader(requestModel, method, context,
            { response, responseCode ->
                if (response != null) {
                    responseHandler(response, TYPE)
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                }
            }, commonParams!!.userData, commonParams!!.token)
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
        try {
            if (TYPE == AddRecipient) {
                val senderResponse:CommonRapiResponse= Gson().fromJson(response.string(), CommonRapiResponse::class.java)
                if (senderResponse != null) {
                    if (senderResponse.statusCode == "00") {
                        Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                        backPress!!.onJctDetailBackPress(CodeEnum.Payout)
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                }
            } else if (TYPE == VerifyAccount) {
                val senderResponse = Gson().fromJson(response.string(), RapipayAddBeneFragment.ValidateBeneResponse::class.java)
                if (senderResponse != null) {
                    if (senderResponse.statusCode == "00") {
                        Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                        name_edt!!.setText(senderResponse.beneficiaryName)
                        verifyAccountTv!!.text = "Account Verified"
                        verifyAccountTv!!.isEnabled = false
                        verifyAccountTv!!.alpha = 0.6f
                        addOrValidateBeneficiary(ApiConstants.AddBenificiary,AddRecipient)
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
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        //        if(isBackPress) {
//            backPress.onJctDetailBackPress();
//        }
    }
}