package com.justclick.clicknbook.Fragment.cashout

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.RapipaySenderDetailFragment
import com.justclick.clicknbook.Fragment.jctmoney.request.AddSenderRequest
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.AddSenderResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse.PostOffice
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse
import com.justclick.clicknbook.FragmentTags
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddSenderFragment : Fragment(), View.OnClickListener {
    private val ADD_SENDER = "05"
    private val UPDATE_SENDER = "06"
    private val CREATE_SENDER = 1
    private val VALIDATE_OTP = 2
    private val RESEND_OTP = 3
    private val SENDER_DETAIL = 4
    private var titleChangeListener: ToolBarTitleChangeListener? = null
    private var get_tv: TextView? = null
    private var resendTv: TextView? = null
    private var titleTv: TextView? = null
    private var otpDetailTv: TextView? = null
    private var number_edt: EditText? = null
    private var name_edt: EditText? = null
    private var otpEdt: EditText? = null
    private var dob_edt: EditText? = null
    private var pin_edt: EditText? = null
    private var state_edt: EditText? = null
    private var genderAtv: AutoCompleteTextView? = null
    private var cityAtv: AutoCompleteTextView? = null
    private var otpLin: LinearLayout? = null
    private var loginModel: LoginModel? = null
    private var isVerify = false
    private var senderDetailResponse: SenderDetailResponse? = null
    private var addSenderResponse: AddSenderResponse? = null
    private var pinCityResponseArrayList: ArrayList<PostOffice>? = null
    private var commonParams: CommonParams? = null
    private var gender: String? = ""
    private var city = ""
    private var state = ""
    private var address = ""
    private var dateServerFormat: SimpleDateFormat? = null
    private var checkInDateDay = 0
    private var checkInDateMonth = 0
    private var checkInDateYear = 0
    private var dobDateCalendar: Calendar? = null
    private var currentDate: Calendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        pinCityResponseArrayList = ArrayList()
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
        val view = inflater.inflate(R.layout.fragment_jct_money_create_sender_rapipay, container, false)
        initializeViews(view)
        initializeDates()
        return view
    }

    private fun initializeViews(view: View) {
        if (arguments!!.getSerializable("senderResponse") != null) {
            senderDetailResponse = arguments!!.getSerializable("senderResponse") as SenderDetailResponse?
            commonParams = arguments!!.getSerializable("commonParams") as CommonParams?
        }
        val senderNumber = arguments!!.getString("SenderNumber", "")
        val face = Common.TextViewTypeFace(context)
        titleTv = view.findViewById(R.id.titleTv)
        get_tv = view.findViewById(R.id.get_tv)
        resendTv = view.findViewById(R.id.resendTv)
        otpDetailTv = view.findViewById(R.id.otpDetailTv)
        otpLin = view.findViewById(R.id.otpLin)
        otpEdt = view.findViewById(R.id.otpEdt)
        number_edt = view.findViewById(R.id.number_edt)
        name_edt = view.findViewById(R.id.name_edt)
        genderAtv = view.findViewById(R.id.genderAtv)
        cityAtv = view.findViewById(R.id.cityAtv)
        dob_edt = view.findViewById(R.id.dob_edt)
        pin_edt = view.findViewById(R.id.pin_edt)
        state_edt = view.findViewById(R.id.state_edt)
        number_edt!!.setText(senderNumber)
        view.findViewById<View>(R.id.back_arrow).setOnClickListener(this)
        dob_edt!!.setOnClickListener(this)
        get_tv!!.setOnClickListener(this)
        resendTv!!.setOnClickListener(this)
        cityAtv!!.setOnClickListener(this)
        get_tv!!.setTypeface(face)
        val genderArray = arrayOf<String?>("MALE", "FEMALE")
        genderAtv!!.setOnClickListener(this)
        genderAtv!!.setAdapter<ArrayAdapter<*>>(ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, genderArray))
        genderAtv!!.setSelection(0)
        genderAtv!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id -> gender = genderArray[position] })
        cityAtv!!.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            city = pinCityResponseArrayList!![position].name
            state = pinCityResponseArrayList!![position].state
            address = pinCityResponseArrayList!![position].district
            state_edt!!.setText(state)
        })
        if (senderDetailResponse != null && senderDetailResponse!!.statusCode == ADD_SENDER) {
            addSenderRequired()
        } else {
            updateRequired()
        }
        pin_edt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Toast.makeText(context, "count="+count+"  text="+s.toString(),Toast.LENGTH_SHORT).show();
                if (s.length == 6) {
                    getCityState(s.toString())
                } else {
                    clearCityState()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getCityState(pin: String) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClientRapipay().create(ApiInterface::class.java)
        val call = apiService.getServicePin("https://api.postalpincode.in/pincode/$pin")
        call.enqueue(object : Callback<ArrayList<PinCityResponse>?> {
            override fun onResponse(call: Call<ArrayList<PinCityResponse>?>, response: Response<ArrayList<PinCityResponse>?>) {
                try {
                    if (response != null && response.body() != null && response.body()!!.size > 0) {
                        hideCustomDialog()
                        if (response.body()!![0].postOffice != null) {
                            pinCityResponseArrayList!!.addAll(response.body()!![0].postOffice)
                            val arr = arrayOfNulls<String>(pinCityResponseArrayList!!.size)
                            for (p in pinCityResponseArrayList!!.indices) {
                                arr[p] = pinCityResponseArrayList!![p].name
                            }
                            cityAtv!!.setAdapter(getSpinnerAdapter(arr))
                            Common.hideSoftKeyboard(context as Activity?)
                            cityAtv!!.showDropDown()
                        } else {
                            Toast.makeText(context, response.body()!![0].message, Toast.LENGTH_LONG).show()
                            clearCityState()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<PinCityResponse>?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearCityState() {
        city = ""
        state = ""
        pinCityResponseArrayList!!.clear()
        cityAtv!!.setAdapter(getSpinnerAdapter(arrayOfNulls(0)))
        cityAtv!!.clearListSelection()
        cityAtv!!.setText("")
        state_edt!!.setText("")
    }

    private fun verifySender() {
        number_edt!!.isEnabled = false
        otpLin!!.visibility = View.VISIBLE
        otpDetailTv!!.visibility = View.VISIBLE
        titleTv!!.text = "Verify Sender"
        isVerify = true
    }

    private fun addSenderRequired() {
        number_edt!!.isEnabled = false
        otpLin!!.visibility = View.GONE
        otpDetailTv!!.visibility = View.GONE
        titleTv!!.text = "Add Sender"
    }

    private fun updateRequired() {
        if (senderDetailResponse!!.senderDetailInfo != null && senderDetailResponse!!.senderDetailInfo.size > 0) {
            number_edt!!.isEnabled = false
            otpLin!!.visibility = View.GONE
            otpDetailTv!!.visibility = View.GONE
            name_edt!!.setText(senderDetailResponse!!.senderDetailInfo[0].name)
            dob_edt!!.setText(senderDetailResponse!!.senderDetailInfo[0].dob)
            titleTv!!.text = "Update Sender"
        }
    }

    private fun addSender(methodName: String, otp: String, responseType: Int) {
        val jctMoneySenderRequestModel = AddSenderRequest()
        jctMoneySenderRequestModel.name = name_edt!!.text.toString().trim { it <= ' ' }
        jctMoneySenderRequestModel.mobile = number_edt!!.text.toString()
        jctMoneySenderRequestModel.agentCode = loginModel!!.Data.DoneCardUser
        jctMoneySenderRequestModel.pin = pin_edt!!.text.toString()
        jctMoneySenderRequestModel.address = city
        jctMoneySenderRequestModel.state = state
        jctMoneySenderRequestModel.dob = dob_edt!!.text.toString()
        jctMoneySenderRequestModel.gender = gender
        jctMoneySenderRequestModel.requestFor = senderDetailResponse!!.requestFor
        jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
        jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
        NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, methodName, context,
                { response, responseCode ->
                    if (response != null) {
                        responseHandler(response, responseType)
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                    }
                }, commonParams!!.userData, commonParams!!.token)
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int) {
//        status code==09  update success(no otp) fundtransferid    code==10 (otp) otprefid fundtransferid inko request me wapis dena h
        try {
            if (TYPE == CREATE_SENDER) {
                addSenderResponse = Gson().fromJson(response.string(), AddSenderResponse::class.java)
                if (addSenderResponse != null) {
                    if (addSenderResponse!!.statusCode == "00") {
                        senderDetail
                        Toast.makeText(context, addSenderResponse!!.statusMessage, Toast.LENGTH_SHORT).show()
                    } else if (addSenderResponse!!.statusCode == "09") {
                        senderDetail
                        Toast.makeText(context, addSenderResponse!!.statusMessage, Toast.LENGTH_SHORT).show()
                    } else if (addSenderResponse!!.statusCode == "10") {
                        verifySender()
                        Toast.makeText(context, addSenderResponse!!.statusMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, addSenderResponse!!.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                }
            } else if (TYPE == VALIDATE_OTP) {
                val senderResponse = Gson().fromJson(response.string(), CommonRapiResponse::class.java)
                if (senderResponse != null) {
                    if (senderResponse.statusCode == "00") {
                        senderDetail
                        Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_arrow -> {
                isVerify = false
                parentFragmentManager.popBackStack()
            }
            R.id.get_tv -> {
                Common.hideSoftKeyboard(context as NavigationDrawerActivity?)
                Common.preventFrequentClick(get_tv)
                if (Common.checkInternetConnection(context)) {
                    if (isVerify) {
                        if (otpEdt!!.text.toString().length >= 4) {
                            validateSender(ApiConstants.VerifySender, otpEdt!!.text.toString(), VALIDATE_OTP)
                        } else {
                            Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (validate()) {
                            addSender(ApiConstants.AddSender, "", CREATE_SENDER)
                        }
                    }
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.resendTv -> {
                //                Common.hideSoftKeyboard((MoneyTransferActivity)context);
                Common.preventFrequentClick(get_tv)
                if (validate()) {
                    if (Common.checkInternetConnection(context)) {
                        if (validate()) {
                            addSender(ApiConstants.AddSender, "", CREATE_SENDER)
                        }
                    } else {
                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.genderAtv -> {
                Common.hideSoftKeyboard(context as Activity?)
                genderAtv!!.showDropDown()
            }
            R.id.dob_edt -> {
                Common.hideSoftKeyboard(context as Activity?)
                openDatePicker()
            }
            R.id.cityAtv -> {
                Common.hideSoftKeyboard(context as Activity?)
                cityAtv!!.showDropDown()
            }
        }
    }

    private fun validateSender(verifySender: String, otp: String, responseType: Int) {
        val jctMoneySenderRequestModel = AddSenderRequest()
        jctMoneySenderRequestModel.mobile = number_edt!!.text.toString()
        jctMoneySenderRequestModel.agentCode = loginModel!!.Data.DoneCardUser
        jctMoneySenderRequestModel.requestFor = senderDetailResponse!!.requestFor
        jctMoneySenderRequestModel.otp = otp
        jctMoneySenderRequestModel.otpRefId = addSenderResponse!!.otpRefId
        jctMoneySenderRequestModel.fundTransferId = addSenderResponse!!.fundTransferId
        jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
        jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
        NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, verifySender, context,
                { response, responseCode ->
                    if (response != null) {
                        responseHandler(response, responseType)
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                    }
                }, commonParams!!.userData, commonParams!!.token)
    }

    private fun validate(): Boolean {
        if (!Common.isNameValid(name_edt!!.text.toString().trim { it <= ' ' })) {
            Toast.makeText(context, R.string.empty_and_invalid_name, Toast.LENGTH_SHORT).show()
            return false
        } else if (number_edt!!.text.toString().length < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
            return false
        } else if (gender!!.length == 0) {
            Toast.makeText(context, "please select gender", Toast.LENGTH_SHORT).show()
            return false
        } else if (dob_edt!!.text.toString().length == 0) {
            Toast.makeText(context, "please select DOB", Toast.LENGTH_SHORT).show()
            return false
        } else if (pin_edt!!.text.toString().length < 6) {
            Toast.makeText(context, R.string.empty_and_invalid_pincode, Toast.LENGTH_SHORT).show()
            return false
        } else if (city.length == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_city, Toast.LENGTH_SHORT).show()
            return false
        } else if (state.length == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_state, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
    private val senderDetail: Unit
        private get() {
            val jctMoneySenderRequestModel = SenderDetailRequest()
            jctMoneySenderRequestModel.mobile = number_edt!!.text.toString()
            jctMoneySenderRequestModel.agentCode = loginModel!!.Data.DoneCardUser
            jctMoneySenderRequestModel.sessionKey = commonParams!!.sessionKey
            jctMoneySenderRequestModel.sessionRefId = commonParams!!.sessionRefNo
            //{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
            NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
                    { response, responseCode ->
                        if (response != null) {
                            responseHandlerSenderDetail(response, SENDER_DETAIL)
                        } else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
                        }
                    }, commonParams!!.userData, commonParams!!.token)
        }

    private fun responseHandlerSenderDetail(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), SenderDetailResponse::class.java)
            if (senderResponse != null) {
                parentFragmentManager.popBackStack()
                if (senderResponse.statusCode == "00") {
                    val bundle = Bundle()
                    bundle.putSerializable("senderResponse", senderResponse)
                    commonParams!!.sessionKey = senderResponse.sessionKey
                    commonParams!!.sessionRefNo = senderResponse.sessionRefId
                    bundle.putSerializable("commonParams", commonParams)
                    val senderDetailFragment = SenderDetailFragment()
                    senderDetailFragment.arguments = bundle
                    (context as NavigationDrawerActivity?)!!.replaceFragmentWithTag(senderDetailFragment, FragmentTags.jctMoneySenderDetailFragment)
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

    private fun initializeDates() {
        //Date formats
        dateServerFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        currentDate = Calendar.getInstance()
        dobDateCalendar = Calendar.getInstance()
        checkInDateDay = currentDate!!.get(Calendar.DAY_OF_MONTH)
        checkInDateMonth = currentDate!!.get(Calendar.MONTH)
        checkInDateYear = currentDate!!.get(Calendar.YEAR)
    }

    private fun openDatePicker() {
        //Date formats
        val datePickerDialog = DatePickerDialog(context!!,
                R.style.DatePickerTheme,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dobDateCalendar!![year, monthOfYear] = dayOfMonth
                    dob_edt!!.setText(dateServerFormat!!.format(dobDateCalendar!!.time))
                }, checkInDateYear, checkInDateMonth, checkInDateDay)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        datePickerDialog.datePicker.maxDate = currentDate!!.timeInMillis
        datePickerDialog.show()
    }

    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter(context!!,
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }
}