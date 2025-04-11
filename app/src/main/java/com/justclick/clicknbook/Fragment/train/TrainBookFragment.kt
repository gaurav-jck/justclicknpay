package com.justclick.clicknbook.Fragment.train

import android.R.attr.text
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.GONE
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.AdapterView.VISIBLE
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordRequest
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse.PostOffice
import com.justclick.clicknbook.Fragment.train.adapter.GSTListAdapter
import com.justclick.clicknbook.Fragment.train.model.CustomerDetailResponse
import com.justclick.clicknbook.Fragment.train.model.FareRuleResponse
import com.justclick.clicknbook.Fragment.train.model.GSTDetailRequest
import com.justclick.clicknbook.Fragment.train.model.GSTDetailResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingRequest
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainBookBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.CodeEnum
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import com.justclick.clicknbook.utils.MySpannable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 */
class TrainBookFragment : Fragment(), View.OnClickListener {
    private val ADULT:Int=1
    private val INFANT:Int=2
    private val NoChoice="99"
    private val OnlyConfirm="4"
    private val SameCoach="1"
    private val OneLower="2"
    private val TwoLower="3"
    private val ConfirmSame="5"
    private val NoPreference="No Preference"
    private val LowerBerth="Lower Berth"
    private val UpperBerth="Upper Berth"
    private val MiddleBerth="Middle Berth"
    private val SideUpper="Side Upper"
    private val SideLower="Side Lower"
    private val WindowSeat="Window Seat"
    var loginModel:LoginModel?=null
    var passengerContainerLin:LinearLayout?=null
    var preferenceRadioGroup:RadioGroup?=null
    var reservationChoice:String=NoChoice
    var autoUpgradeFlag=true
    var doj:String?=null
    private val gender = ""
    private  var post:String? = ""
    private  var city:String? = ""
    private  var state:String? = ""
    private  var address:String? = ""
    private  var city2:String? = ""
    private  var state2:String? = ""
    private  var boardingStationName:String? = null
    private  var boardingStationCode:String? = null
    var removePassTv:TextView?=null
    var addInfantTv:TextView?=null
    var addPassTv:TextView?=null
//    var cusMobileEdt:EditText?=null
    private var pinCityResponseArrayList: ArrayList<PostOffice>? = null
    var trainResponse:TrainSearchDataModel?=null
    var fareRuleResponse:FareRuleResponse?=null
    var trainBookingRequest:TrainBookingRequest?=null
    var passengerArray:ArrayList<TrainBookingRequest.adultRequest>?=null
    var position:Int=0
    var fragView:View?=null
    var binding:FragmentTrainBookBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinCityResponseArrayList = ArrayList()
        trainBookingRequest= TrainBookingRequest();
        passengerArray= ArrayList();
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(fragView==null){
            val view= inflater.inflate(R.layout.fragment_train_book, container, false)
            binding=FragmentTrainBookBinding.bind(view)
            fragView=view
            addInfantTv=view.findViewById(R.id.addInfantTv)
            addPassTv=view.findViewById(R.id.addPassTv)
//            cusMobileEdt=view.findViewById(R.id.cusMobileEdt)
            binding!!.backArrow.setOnClickListener(this)
            binding!!.addPassTv.setOnClickListener(this)
            binding!!.addInfantTv.setOnClickListener(this)
            binding!!.bookTv.setOnClickListener(this)
            removePassTv=view.findViewById(R.id.removePassTv)
            removePassTv!!.setOnClickListener(this)
            binding!!.gstLabelRel.setOnClickListener(this)
            binding!!.otherPrefRel.setOnClickListener(this)
            binding!!.fareLabelRel.setOnClickListener(this)
            binding!!.getDetails.setOnClickListener(this)
            binding!!.addGstDetails.setOnClickListener(this)
            binding!!.getGstDetails.setOnClickListener(this)
            passengerContainerLin=view.findViewById(R.id.passengerContainerLin)
            preferenceRadioGroup=view.findViewById(R.id.preferenceRadioGroup)

            if(arguments!=null) {
                trainResponse = requireArguments().getSerializable("trainResponse") as TrainSearchDataModel
                fareRuleResponse = requireArguments().getSerializable("fareRuleResponse") as FareRuleResponse
                doj = requireArguments().getString("DOJ")
                position=requireArguments().getInt("Position")
                var fromStationTv:TextView=view.findViewById(R.id.fromStationTv)
                var toStationTv:TextView=view.findViewById(R.id.toStationTv)
                var dateTv:TextView=view.findViewById(R.id.dateTv)
                fromStationTv.text=trainResponse!!.fromStnName
                toStationTv.text=trainResponse!!.toStnName
                dateTv.text=fareRuleResponse!!.availablityDate
            }

            binding!!.baseFareEdt.setText(fareRuleResponse!!.baseFare)
            binding!!.totalFareEdt.setText(fareRuleResponse!!.totalFare)
            binding!!.concessionEdt.setText("0")
            binding!!.pgChargeEdt.setText("0")
            binding!!.serviceChargeEdt.setText("0")

            makeTextViewResizable(binding!!.mobileNoteTv, 2, "View More", true)

            /*view.pin_edt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Toast.makeText(context, "count="+count+"  text="+s.toString(),Toast.LENGTH_SHORT).show();
                    if (s.length == 6 && before!=6) {
                        getCity(s.toString())
                    } else {
                        clearCityState()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })*/

            binding!!.pinEdt2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Toast.makeText(context, "count="+count+"  text="+s.toString(),Toast.LENGTH_SHORT).show();
                    if (s.length == 6 && before!=6 && start!=0) {
                        getGstCity(s.toString())
//                        getCityState2(s.toString())
                    } else if(start!=0) {
                        clearCityState2()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            setFonts()

            callBoardingStation()

            preferenceRadioGroup!!.setOnCheckedChangeListener { radioGroup: RadioGroup?, checkedId: Int ->
                when (checkedId) {
                    R.id.noChoice -> {
                        reservationChoice=NoChoice
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    R.id.onlyConfirm -> {
                        reservationChoice=OnlyConfirm
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    R.id.sameCoach -> {
                        reservationChoice=SameCoach
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    R.id.oneLower -> {
                        reservationChoice=OneLower
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    R.id.twoLower -> {
                        reservationChoice=TwoLower
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    R.id.confirmSame -> {
                        reservationChoice=ConfirmSame
//                        var radioButton:RadioButton = radioGroup!!.findViewById(checkedId)
//                        Toast.makeText(context, reservationChoice+", value="+radioButton.text.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding!!.spinnerBoardingStn.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                ) {
                    changeBoardingStn(parent.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }

            return view
        }else{
            return fragView
        }
    }

    private fun getCustomerDetails() {
        val request = ChangePasswordRequest()
        request.oldpassword = binding!!.mobileEdt.getText().toString()
        request.BookUserID = loginModel!!.Data.UserId
        val json = Gson().toJson(request)
        NetworkCall().callService(NetworkCall.getTrainApiInterface()
                .getCustomerDetails(ApiConstants.getPassenger, loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType,
                    ApiConstants.MerchantId, "App", binding!!.mobileEdt!!.text.toString()),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, responseCode)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody, responseCode: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CustomerDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
                    customerListDialog(senderResponse.passengerList)
                } else if (senderResponse.statusMessage != null) {
                    Common.showResponsePopUp(context, senderResponse.statusMessage)
                } else {
                    Toast.makeText(
                        context, "Unable to get customer details", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    var filterDialog:Dialog?=null
    var listName=""
    var noPassenger=0
    private fun customerListDialog(list: ArrayList<CustomerDetailResponse.passengerList>) {
        listName = ""
        filterDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        filterDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        filterDialog!!.setContentView(R.layout.customer_details_pnr_layout)

        var classLinear: LinearLayout = filterDialog!!.findViewById(R.id.classLinear)

        var classFilterItem = ""
        noPassenger=0

        for (i in 0 until list!!.size) {
//            classFilterItem+=arrayList!!.get(i).avlClasses!![c]
            var checkBox = CheckBox(requireContext())
            checkBox.text = list!!.get(i).name+"  [ "+list!!.get(i).age+"-"+list!!.get(i).sex+" ]"
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    noPassenger++
                    if(passengerArray!!.size+noPassenger>6){
                        Toast.makeText(requireContext(),"Number of passenger should not be greater than 6", Toast.LENGTH_SHORT).show()
                        checkBox.isChecked=false
                        noPassenger--
                    }
                } else {
                    noPassenger--
                }
            }
            classLinear.addView(checkBox)
        }
//        className += arrayList!!.get(i).avlClasses!![c]

        filterDialog!!.findViewById<TextView>(R.id.cancelTv).setOnClickListener {
            for(i in 0 until classLinear.childCount){
                var checkBox:CheckBox= classLinear.getChildAt(i) as CheckBox
                if(checkBox.isChecked && passengerArray!!.size<6){
                    addCustomerPassenger(list.get(i).name, list.get(i).age, list.get(i).sex)
//                    Toast.makeText(requireContext(), checkBox.text.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            filterDialog!!.cancel()
        }
        /*filterDialog!!.findViewById<TextView>(R.id.applyTv).setOnClickListener {
            filterDialog!!.cancel()
        }*/

        filterDialog!!.show()
    }

    private fun addCustomerPassenger(name: String, age: String, sex: String) {
        var passenger:TrainBookingRequest.adultRequest=TrainBookingRequest().adultRequest()
        passenger.passengerName=name
        passenger.passengerAge=age
        passenger.passengerGender=sex
        var food=""
        if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
            if(fareRuleResponse!!.bkgCfg!!.foodDetails!=null && fareRuleResponse!!.bkgCfg!!.foodDetails.isNotEmpty()){
                passenger.passengerFoodChoice=fareRuleResponse!!.bkgCfg!!.foodDetails[0]
                food=passenger.passengerFoodChoice
            }
            else{
                passenger.passengerFoodChoice=""
            }
        }else{
            passenger.passengerFoodChoice=""
        }
        passenger.childBerthFlag=null
        passenger.type=ADULT
        passengerArray!!.add(passenger)
        addPassenger(passenger.passengerName, passenger.passengerAge, passenger.passengerGender, food)
    }

    private fun changeBoardingStn(stationCode: String) {
        var stnName:String=stationCode.split("-")[0].trim()
        var stnCode:String=stationCode.split("-")[1].trim()
//        Toast.makeText(context, "Stn Name="+stnName+"\nstnCode="+stnCode, Toast.LENGTH_SHORT).show()
        boardingStationName=stnName
        boardingStationCode=stnCode
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
            R.id.addPassTv->{
//                addPassenger()
                if(getCount(ADULT)==6){
                    Toast.makeText(context, "You can add maximum 6 passenger", Toast.LENGTH_SHORT).show()
                }else{
                    addPass(ADULT, passengerArray!!.size)
                }
            }
            R.id.addInfantTv->
                if(getCount(INFANT)==2){
                    Toast.makeText(context, "You can add maximum 2 infant", Toast.LENGTH_SHORT).show()
                }else{
                    addInf(INFANT, passengerArray!!.size)
                }
            R.id.bookTv->
                validate()
            R.id.gstLabelRel->
                showHideGst()
            R.id.otherPrefRel->
                showHidePref()
            R.id.fareLabelRel->
                showHideFare()
             R.id.getDetails->{
                 if(binding!!.mobileEdt!!.text.toString().length<10){
                     Toast.makeText(requireContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                 }else{
                     getCustomerDetails()
                 }
             }
            R.id.getGstDetails->{
                getGSTDetails()
             }
            R.id.addGstDetails->{
                addGstDialog()
            }
            R.id.cityAtv-> {
                Common.hideSoftKeyboard(context as Activity?)
//                cityAtv.showDropDown()
            }
        }
    }

    private fun getGSTDetails() {
        val request = GSTDetailRequest()
        request.NameonGST = "Dummy"
        request.gstNumber = GSTDetailRequest.dummyGstNumber
        request.action = GSTDetailRequest.SELECT
        request.Pincode="110001"
        request.City="City"
        request.Flat="Flatt"
        request.State="State"
        val json = Gson().toJson(request)
        NetworkCall().callService(NetworkCall.getTrainApiInterface()
            .getGSTDetails(ApiConstants.addagentgstdetails, request, loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType,
                ApiConstants.MerchantId, "App"),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerGST(response, responseCode)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerGST(response: ResponseBody, responseCode: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), GSTDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    if(senderResponse.gstDetailsList!=null && senderResponse.gstDetailsList.size>0){
                        gstListDialog(senderResponse.gstDetailsList)
                    }else{
                        Toast.makeText(context,"No GST detail found for this ID, please add details.",Toast.LENGTH_LONG).show();
                    }
                } else if (senderResponse.statusMessage != null) {
                    Common.showResponsePopUp(context, senderResponse.statusMessage)
                } else {
                    Toast.makeText(
                        context, "Unable to get GST details", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    interface OnGSTListListener {
        // TODO: Update argument type and name
        fun onGstListInteraction(item: ArrayList<GSTDetailResponse.gstDetailsList>?, id: Int, listPosition: Int, type: CodeEnum)
    }
    var gstListDialog:Dialog?=null
    private fun gstListDialog(gstDetailsList: java.util.ArrayList<GSTDetailResponse.gstDetailsList>?) {
        gstListDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        gstListDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        gstListDialog!!.setContentView(R.layout.gst_list_dialog)

        var back_arrow=gstListDialog!!.findViewById<ImageView>(R.id.back_arrow)
        var gstRecycler=gstListDialog!!.findViewById<RecyclerView>(R.id.gstRecycler)

        gstRecycler.layoutManager= LinearLayoutManager(context)
        gstRecycler.adapter= GSTListAdapter(gstDetailsList,object : OnGSTListListener {
            override fun onGstListInteraction(item: ArrayList<GSTDetailResponse.gstDetailsList>?, id: Int, listPosition: Int, type: CodeEnum) {
                if(type==CodeEnum.GSTDelete){
                    deleteGstDetail(item!!.get(listPosition))
                }
                else if(type==CodeEnum.GSTDetail){
                    Toast.makeText(context, item!!.get(listPosition).gstin, Toast.LENGTH_SHORT).show()
                    setGstData(item!!.get(listPosition))
                    gstListDialog!!.dismiss()
                }
            }
        })

        back_arrow.setOnClickListener {
            gstListDialog!!.dismiss()
        }

        gstListDialog!!.show()
    }

    private fun setGstData(gstDetail: GSTDetailResponse.gstDetailsList) {
        binding!!.gstEdt.setText(gstDetail.gstin)
        binding!!.gstNameEdt.setText(gstDetail.nameOnGST)
        binding!!.gstFlatEdt.setText(gstDetail.flat)
        binding!!.cityEdt2.setText(gstDetail.city)
        binding!!.pinEdt2.setText(gstDetail.pincode)
        binding!!.stateEdt2.setText(gstDetail.state)
    }

    private fun deleteGstDetail(gstDetail: GSTDetailResponse.gstDetailsList) {
        val request = GSTDetailRequest()
        request.id = gstDetail.sid
        request.NameonGST = gstDetail.nameOnGST
        request.gstNumber = gstDetail.gstin
        request.action = GSTDetailRequest.DELETE
        request.Pincode=gstDetail.pincode
        request.City=gstDetail.city
        request.Flat=gstDetail.flat
        request.State=gstDetail.state
        val json = Gson().toJson(request)
        NetworkCall().callService(NetworkCall.getTrainApiInterface()
            .getGSTDetails(ApiConstants.addagentgstdetails, request, loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType,
                ApiConstants.MerchantId, "App"),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerDelete(response, responseCode)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerDelete(response: ResponseBody, responseCode: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), GSTDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    gstListDialog!!.dismiss()
                    getGSTDetails()
                } else if (senderResponse.statusMessage != null) {
                    Common.showResponsePopUp(context, senderResponse.statusMessage)
                } else {
                    Toast.makeText(
                        context, "Unable to delete GST details", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    var addGstDialog:Dialog?=null
    private fun addGstDialog() {
        addGstDialog = Dialog(requireContext(), R.style.Theme_Design_Light)
        addGstDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addGstDialog!!.setContentView(R.layout.add_gst_dialog)

        var back_arrow=addGstDialog!!.findViewById<ImageView>(R.id.back_arrow)
        var submitGstDetails=addGstDialog!!.findViewById<TextView>(R.id.submitGstDetails)
        var gstEdt=addGstDialog!!.findViewById<EditText>(R.id.gstEdt)
        var gstNameEdt=addGstDialog!!.findViewById<EditText>(R.id.gstNameEdt)
        var gstFlatEdt=addGstDialog!!.findViewById<EditText>(R.id.gstFlatEdt)
        var pin_edt2=addGstDialog!!.findViewById<EditText>(R.id.pin_edt2)
        var state_edt2=addGstDialog!!.findViewById<EditText>(R.id.state_edt2)
        var cityEdt2=addGstDialog!!.findViewById<EditText>(R.id.cityEdt2)


        back_arrow.setOnClickListener {
            addGstDialog!!.dismiss()
        }
        submitGstDetails.setOnClickListener{
            var gstNumber=gstEdt.text.toString()
            var gstName=gstNameEdt.text.toString()
            var gstFlat=gstFlatEdt.text.toString()
            var gstPin=pin_edt2.text.toString()
            var state=state_edt2.text.toString()
            var city=cityEdt2.text.toString()
            if(gstNumber.isEmpty() || gstNumber.length<15){
                Toast.makeText(requireContext(), "GST number is empty or invalid", Toast.LENGTH_SHORT).show()
            }else if(gstName.isEmpty() || !Common.isNameValid(gstName)){
                Toast.makeText(requireContext(), "Name is empty or invalid", Toast.LENGTH_SHORT).show()
            }else if(gstFlat.isEmpty() || gstFlat.length<5){
                Toast.makeText(requireContext(), "Flat number is empty or invalid", Toast.LENGTH_SHORT).show()
            }else if(gstPin.isEmpty() || gstPin.length<6){
                Toast.makeText(requireContext(), "Pincode number is empty or invalid", Toast.LENGTH_SHORT).show()
            }else if(state.isEmpty() || gstPin.length<3){
                Toast.makeText(requireContext(), "State is empty or invalid", Toast.LENGTH_SHORT).show()
            }else if(city.isEmpty() || city.length<3){
                Toast.makeText(requireContext(), "City is empty or invalid", Toast.LENGTH_SHORT).show()
            }else{
                var request:GSTDetailRequest= GSTDetailRequest()
                request.gstNumber=gstNumber
                request.NameonGST=gstName
                request.Flat=gstFlat
                request.Pincode=gstPin
                request.State=state
                request.City=city
                request.action=GSTDetailRequest.INSET
                addGstDetails(request)
            }
        }

        addGstDialog!!.show()
    }

    private fun addGstDetails(request: GSTDetailRequest) {
        val json = Gson().toJson(request)
        NetworkCall().callService(NetworkCall.getTrainApiInterface()
            .getGSTDetails(ApiConstants.addagentgstdetails, request, loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType,
                ApiConstants.MerchantId, "App"),
            context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerGSTAdd(response, responseCode)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun responseHandlerGSTAdd(response: ResponseBody, responseCode: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), GSTDetailResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show()
                    addGstDialog?.dismiss()
                } else if (senderResponse.statusMessage != null) {
                    Common.showResponsePopUp(context, senderResponse.statusMessage)
                } else {
                    Toast.makeText(
                        context, "Unable to add GST details", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCount(type:Int): Int {
        var adCount=0
        var infCount=0
        for(data in passengerArray!!.iterator()){
            if(data.type==ADULT){
                adCount++
            }else{
                infCount++
            }
        }
        if(type==ADULT)
            return adCount
        else
            return infCount
    }

    private fun showHideFare() {
        if(binding!!.fareView.visibility== VISIBLE){
            binding!!.fareView.visibility= GONE
        }else{
            binding!!.fareView.visibility= VISIBLE
        }
    }

    private fun showHideGst() {
        if(binding!!.gstView.visibility== VISIBLE){
            binding!!.gstView.visibility= GONE
        }else{
            binding!!.gstView.visibility= VISIBLE
        }
    }
    private fun showHidePref() {
        if(preferenceRadioGroup!!.visibility== VISIBLE){
            preferenceRadioGroup!!.visibility= GONE
        }else{
            preferenceRadioGroup!!.visibility= VISIBLE
        }
    }

    class BoardingStnResponse{
        var errorMessage:String?=null
        var boardingStationList :ArrayList<BoardingStationList>? =null
        class BoardingStationList{
            var stnNameCode:String?=null
        }
        /*{"boardingStationList":[{"stnNameCode":"ANAND VIHAR TRM - ANVT"},{"stnNameCode":"KANPUR CENTRAL - CNB"}]}*/
        /*{"errorMessage":"This action not allowed as the Date given is Outside Advance Reservation Period- (80012)","mealChoiceenable":"false","serverId":"DM04AP19MS3","timeStamp":"2022-01-28T11:22:43.981"}*/
    }

    private fun callBoardingStation() {
        val url=ApiConstants.BASE_URL_TRAIN;
        val apiService = APIClient.getClient(url).create(ApiInterface::class.java)
        val call = apiService.getBoardingStn(url+"apiV1/RailEngine/BoardingStation?Trainno="
                +fareRuleResponse!!.trainNo+"&Date="+journeyDate()+"&fromStation="+
                trainResponse!!.trainBtwnStnsList!!.get(position).fromStnCode+
                "&toStation="+trainResponse!!.trainBtwnStnsList!!.get(position).toStnCode+"&className="+fareRuleResponse!!.enqClass,
        loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<BoardingStnResponse?> {
            override fun onResponse(call: Call<BoardingStnResponse?>, response: Response<BoardingStnResponse?>) {
                try {
                    if (response != null && response.body() != null && response.body()!!.boardingStationList!=null) {
                        if(response.body()!!.boardingStationList!!.size>0){
                            binding!!.boardingStnLabelRel.visibility = VISIBLE

                            var arr: Array<String?> =arrayOfNulls<String>(response.body()!!.boardingStationList!!.size)
                            for(pos in response.body()!!.boardingStationList!!.indices){
                                arr[pos]=response.body()!!.boardingStationList!!.get(pos).stnNameCode
                            }

                            binding!!.spinnerBoardingStn.adapter=getSpinnerAdapter(arr)

                        }
                    } else {
                        hideCustomDialog()
//                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
//                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BoardingStnResponse?>, t: Throwable) {
                hideCustomDialog()
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    class CityPinResponse{
        public var cityList:Array<String?>?=null;
        public var city:String?=null;
        public var state:String?=null;
        public var postofficeList :Array<String?>? =null
        /*{"cityList":"Saharanpur","serverId":"DM04AP18MS3","state":"UTTAR PRADESH","timeStamp":"2021-12-03T19:01:39.884"}*/
    }

    private fun getCity(pin: String) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getServiceCityPinResponse(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/PostOffice?pincode="+pin)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if(response!=null && response.body()!=null){
                        var responseString= response.body()!!.string()
                        responseString=responseString.replace("cityList\":\"", "cityList\":[\"")
                        responseString=responseString.replace("\",\"serverId", "\"],\"serverId")

                        val pinCity = Gson().fromJson(responseString, CityPinResponse::class.java)
                        if(pinCity!=null && pinCity!!.cityList!=null){
                            getCityPin(pin,pinCity!!.cityList!!.get(0))
                        }else{
                            hideCustomDialog()
                            Toast.makeText(requireContext(), "No data found for this pin code", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        hideCustomDialog()
                        Toast.makeText(requireContext(), "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getCityPin(pin: String, cityName: String?) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getServiceCityPin(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/Postofficecity?pincode="+
                pin+"&pincity="+cityName)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if(response!=null && response.body()!=null){
                        var responseString= response.body()!!.string()
                        responseString=responseString.replace("postofficeList\":\"", "postofficeList\":[\"")
                        responseString=responseString.replace("\",\"serverId", "\"],\"serverId")

                        val pinCity = Gson().fromJson(responseString, CityPinResponse::class.java)
                        if(pinCity!=null && pinCity!!.postofficeList!=null && pinCity!!.postofficeList!!.size>0){
                            /*cityAtv.setAdapter(getSpinnerAdapter(pinCity!!.postofficeList!!))
                            Common.hideSoftKeyboard(context as Activity?)
                            cityAtv.showDropDown()
                            city=pinCity!!.city
                            state=pinCity!!.state
                            state_edt.setText(state)
                            cityEdt.setText(city)*/
                        }else{
                            Toast.makeText(requireContext(), "No data found for this pin code", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(requireContext(), "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearCityState() {
        city = ""
        state = ""
        pinCityResponseArrayList!!.clear()
        /*cityAtv.setAdapter(getSpinnerAdapter(arrayOfNulls<String>(0)))
        cityAtv.clearListSelection()
        cityAtv.setText("")
        state_edt.setText("")*/
    }

    private fun getGstCity(pin: String) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getServiceCityPinResponse(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/PostOffice?pincode="+pin)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if(response!=null && response.body()!=null){
                        hideCustomDialog()
                        Common.hideSoftKeyboard(context as Activity?)
                        var responseString= response.body()!!.string()
                        responseString=responseString.replace("cityList\":\"", "cityList\":[\"")
                        responseString=responseString.replace("\",\"serverId", "\"],\"serverId")

                        val pinCity = Gson().fromJson(responseString, CityPinResponse::class.java)
                        binding!!.cityEdt2.setText(pinCity.cityList!!.get(0))
                        binding!!.stateEdt2.setText(pinCity!!.state)
                    } else {
                        hideCustomDialog()
                        clearCityState2()
                        Toast.makeText(requireContext(), "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }
                  /*  if (response != null && response.body() != null && response.body()!!.cityList!=null) {
                        hideCustomDialog()
                        Common.hideSoftKeyboard(context as Activity?)
                        cityEdt2.setText(response.body()!!.cityList)
                        state_edt2.setText(response.body()!!.state)
                    } else {
                        hideCustomDialog()
                        clearCityState2()
                        Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }*/
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getCityState2(pin: String) {
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
                            Common.hideSoftKeyboard(context as Activity?)
                            binding!!.cityEdt2.setText(response.body()!![0].postOffice.get(0).district)
                            binding!!.stateEdt2.setText(response.body()!![0].postOffice.get(0).state)
                        } else {
                            Toast.makeText(requireContext(), response.body()!![0].message, Toast.LENGTH_LONG).show()
                            clearCityState2()
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<PinCityResponse>?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearCityState2() {
        city2 = ""
        state2 = ""
        binding!!.cityEdt2.setText("")
        binding!!.stateEdt2.setText("")
    }

    private fun validate() {
        var isPass=false
        for(data in passengerArray!!.iterator()){
            if(data.type==ADULT){
                isPass=true
                break
            }
        }
        var mobile=binding!!.mobileEdt.text.toString()
        var email=binding!!.emailEdt.text.toString()
        if(!isPass){
            Toast.makeText(requireContext(), "Please add passenger", Toast.LENGTH_SHORT).show()
        }else if(!isGstValid()){
            Toast.makeText(requireContext(), "Please add all GST details", Toast.LENGTH_SHORT).show()
        }else if(!Common.isMobileValid(mobile)){
            Toast.makeText(requireContext(), R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
        } else if(!Common.isEmailValid(email)){
            Toast.makeText(requireContext(), R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
        }/*else if(addressEdt.text.toString().isEmpty()){
            Toast.makeText(requireContext(), R.string.empty_address, Toast.LENGTH_SHORT).show()
        }else if(pin_edt.text.toString().length<6){
            Toast.makeText(requireContext(), R.string.empty_and_invalid_pincode, Toast.LENGTH_SHORT).show()
        }else if(cityAtv.text.toString().isEmpty()){
            Toast.makeText(requireContext(), R.string.empty_post, Toast.LENGTH_SHORT).show()
        }*/else{
            checkCredential()
        }
    }

    private fun isGstValid(): Boolean {
        return true
    }

    private fun bookTrain(token: String, userData: String) {

        trainBookingRequest= makeBookingRequest()
        val requestModel = Gson().fromJson(requestString.toString(), TrainBookingRequest::class.java)
        var agentCode = loginModel!!.Data.DoneCardUser
        var userTypr = loginModel!!.Data.UserType

        val json = Gson().toJson(trainBookingRequest)
        NetworkCall().callRailService(NetworkCall.getTrainApiInterface().bookTrain(trainBookingRequest,ApiConstants.PreBook,
                agentCode, userTypr, ApiConstants.MerchantId, "App", token, userData), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1, token, userData) //https://rail.justclicknpay.com/apiV1/RailEngine/CheckCredential
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeBookingRequest(): TrainBookingRequest {
//        var trainBookingRequest= TrainBookingRequest();
        var journeyList:ArrayList<TrainBookingRequest.journeyDetails> = ArrayList()
        var destinationList:ArrayList<TrainBookingRequest.destinationDetail> = ArrayList()
        var passengerAddDetailList:ArrayList<TrainBookingRequest.passengerAdditionalDetail> = ArrayList()
        var gstList:ArrayList<TrainBookingRequest.gstDetails> = ArrayList()
        var fareList:ArrayList<TrainBookingRequest.fareDetail> = ArrayList()
        var adultList:ArrayList<TrainBookingRequest.adultRequest> = ArrayList()
        var childList:ArrayList<TrainBookingRequest.childRequest> = ArrayList()

        var adCnt=0
        for(data in passengerArray!!.iterator()){
            if(data.type==ADULT){
                adCnt++
                data.passengerSerialNumber=adCnt.toString()
                adultList.add(data)
            }else{
                var child:TrainBookingRequest.childRequest=trainBookingRequest!!.childRequest()
                child.passengerAge=data.passengerAge
                child.passengerName=data.passengerName
                child.passengerGender=data.passengerGender
                childList.add(child)
            }
        }

        var journey:TrainBookingRequest.journeyDetails=trainBookingRequest!!.journeyDetails()
        journey.arrivalTime=trainResponse!!.trainBtwnStnsList!!.get(position).arrivalTime
        journey.departTime=trainResponse!!.trainBtwnStnsList!!.get(position).departureTime
        journey.fromStation=trainResponse!!.fromStnName
        journey.toStation=trainResponse!!.toStnName
        journey.fromStationCode=trainResponse!!.trainBtwnStnsList!!.get(position).fromStnCode
        journey.toStationCode=trainResponse!!.trainBtwnStnsList!!.get(position).toStnCode
        journey.trainName=trainResponse!!.trainBtwnStnsList!!.get(position).trainName
        journey.trainNo=trainResponse!!.trainBtwnStnsList!!.get(position).trainNumber
        if(boardingStationCode!=null){
            journey.boardingStation=boardingStationName
            journey.boardingStationCode=boardingStationCode
        }else{
            journey.boardingStation=trainResponse!!.fromStnName
            journey.boardingStationCode=trainResponse!!.trainBtwnStnsList!!.get(position).fromStnCode
        }

        journey.reservationUpTo=trainResponse!!.toStnName
        journey.reservationUpToCode=trainResponse!!.trainBtwnStnsList!!.get(position).toStnCode
        journey.journeyClass=fareRuleResponse!!.enqClass
        journey.JourneyClass=fareRuleResponse!!.enqClass
        journey.duration=trainResponse!!.trainBtwnStnsList!!.get(position).duration
        journey.quota=fareRuleResponse!!.quota
        journey.bookingFlag="Y"
        trainBookingRequest!!.dateToSet=fareRuleResponse!!.availablityDate
        journey.journeyDate=journeyDate()
        journey.moreThanOneDay="True"
        journey.enquiryType="3"
        journey.reservationChoice=reservationChoice
        journey.ticketType="F"
        var checkbox=binding!!.insuranceRadioGroup.findViewById<RadioButton>(R.id.travelRadioYes)
        if(checkbox.isChecked){
            journey.travelInsuranceOpted="True"
        }else{
            journey.travelInsuranceOpted="False"
        }
        var upgrade=binding!!.otherPrefRadioGroup.findViewById<RadioButton>(R.id.autoUpgradeRadio)
        if(upgrade.isChecked){
            journey.autoUpgradationSelected="True"
        }else{
            journey.autoUpgradationSelected="False"
        }

        journeyList.add(journey)

        var destination:TrainBookingRequest.destinationDetail=trainBookingRequest!!.destinationDetail()
        /*destination.address=addressEdt.text.toString()
        destination.pinCode=pin_edt.text.toString()
        destination.stateName=state_edt.text.toString().uppercase()
        destination.city=cityEdt.text.toString()
        destination.postOffice=cityAtv.text.toString()*/
        destination.address="Saharanpur"
        destination.pinCode="247001"
        destination.stateName="UTTAR PRADESH"
        destination.city="Saharanpur"
        destination.postOffice="Hakikat Nagar S.O"

        destinationList.add(destination)

        var gstDetail:TrainBookingRequest.gstDetails=trainBookingRequest!!.gstDetails()
        gstDetail.gst=binding!!.gstEdt.text.toString()
        gstDetail.gstName=binding!!.gstNameEdt.text.toString()
        gstDetail.flat=binding!!.gstFlatEdt.text.toString()
        gstDetail.city=binding!!.cityEdt2.text.toString()
        gstDetail.pinCode=binding!!.pinEdt2.text.toString()
        gstDetail.stateCity=binding!!.stateEdt2.text.toString()
        gstList.add(gstDetail)

        var fareDetail:TrainBookingRequest.fareDetail=trainBookingRequest!!.fareDetail()
        fareDetail.baseFare= fareRuleResponse!!.baseFare.toFloat()
        fareDetail.agentServiceCharge=0f
        fareDetail.concession=0f
        fareDetail.pgCharge=0f
        fareDetail.serviceCharge=fareRuleResponse!!.serviceTax.toFloat()
        fareDetail.totalFare=fareRuleResponse!!.totalFare.toFloat()
        fareList.add(fareDetail)

        var additionalDetail:TrainBookingRequest.passengerAdditionalDetail=trainBookingRequest!!.passengerAdditionalDetail()
        additionalDetail.adultCount=getCount(ADULT)
        additionalDetail.childCount=getCount(INFANT)
        additionalDetail.coach=""
        additionalDetail.email=binding!!.emailEdt.text.toString()
//        val data: ByteArray = additionalDetail.email.toByteArray(StandardCharsets.UTF_8)
//        val base64email: String = Base64.encodeToString(data, Base64.DEFAULT)
//        additionalDetail.email=base64email
        additionalDetail.mobile=binding!!.mobileEdt.text.toString()
        additionalDetail.totalPaxCount=additionalDetail.adultCount+additionalDetail.childCount
        additionalDetail.preference="LB"
        additionalDetail.remarks="JustClick"
        passengerAddDetailList.add(additionalDetail)

        trainBookingRequest!!.journeyDetails=journeyList
        trainBookingRequest!!.adultRequest=adultList
        trainBookingRequest!!.childRequest=childList
        trainBookingRequest!!.destinationDetail=destinationList
        trainBookingRequest!!.gstDetails=gstList
        trainBookingRequest!!.fareDetail=fareList
        trainBookingRequest!!.passengerAdditionalDetail=passengerAddDetailList


        return trainBookingRequest!!
    }

    private fun journeyDate(): String? {
        return Common.getServerDateFormat().format(SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(fareRuleResponse!!.availablityDate))
    }

    private fun responseHandler(response: ResponseBody, TYPE: Int, token:String, userData:String) {
        try {
            val senderResponse = Gson().fromJson(response.string(), TrainPreBookResponse::class.java)
            if (senderResponse != null) {
                if(senderResponse.statusCode!=null && senderResponse.statusCode.equals("00")){
                    makeTrainBookingRequest(senderResponse, token, userData)
                }else{
                    Toast.makeText(requireContext(), senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeTrainBookingRequest(response: TrainPreBookResponse, token:String, userData:String) {
        if(response.finalBookingFareAndJourneyDetail!=null){

            if(response.paymentDetails.get(0).availableAmount<response.paymentDetails.get(0).payableAmount){
                Common.showResponsePopUp(context,"Your available amount is not enough for this booking.")
                return
            }

            trainBookingRequest!!.journeyDetails.get(0).fromStation=response.bookingDetails.journeyDetails.get(0).fromStation
            trainBookingRequest!!.journeyDetails.get(0).toStation=response.bookingDetails.journeyDetails.get(0).toStation
            trainBookingRequest!!.journeyDetails.get(0).boardingStation=response.bookingDetails.journeyDetails.get(0).boardingStation
            trainBookingRequest!!.journeyDetails.get(0).reservationUpTo=response.bookingDetails.journeyDetails.get(0).reservationUpTo

            trainBookingRequest!!.fareDetail.get(0).baseFare= response.confirmFareDetail.get(0).ticketFare.toFloat()
            trainBookingRequest!!.fareDetail.get(0).agentServiceCharge= response.confirmFareDetail.get(0).agentServiceCharge.toFloat()
            trainBookingRequest!!.fareDetail.get(0).concession= response.confirmFareDetail.get(0).concession.toFloat()
            trainBookingRequest!!.fareDetail.get(0).pgCharge= response.confirmFareDetail.get(0).pgCharge.toFloat()
            trainBookingRequest!!.fareDetail.get(0).serviceCharge= response.confirmFareDetail.get(0).serviceCharge.toFloat()
            trainBookingRequest!!.fareDetail.get(0).totalFare= response.confirmFareDetail.get(0).totalFare.toFloat()

            var finalFareList:ArrayList<TrainBookingRequest.finalBookingFareAndJourneyDetails> = ArrayList()
            var finalFare:TrainBookingRequest.finalBookingFareAndJourneyDetails=trainBookingRequest!!.finalBookingFareAndJourneyDetails()
            finalFare.totalFare=response.finalBookingFareAndJourneyDetail.get(0).totalFare
            finalFare.cateringCharge=response.finalBookingFareAndJourneyDetail.get(0).cateringCharge
            finalFare.travelInsuranceCharge=response.finalBookingFareAndJourneyDetail.get(0).travelInsuranceCharge
            finalFare.travelInsuranceServiceTax=response.finalBookingFareAndJourneyDetail.get(0).travelInsuranceServiceTax
            finalFare.wpServiceCharge=response.finalBookingFareAndJourneyDetail.get(0).wpServiceCharge
            finalFare.wpServiceTax=response.finalBookingFareAndJourneyDetail.get(0).wpServiceTax
            finalFare.journeyClass=fareRuleResponse!!.enqClass
            finalFareList.add(finalFare)
            trainBookingRequest!!.finalBookingFareAndJourneyDetails=finalFareList

            trainBookingRequest!!.TransactionId=response.bookingDetails.transactionId;
            trainBookingRequest!!.availableSeats=response.availableblityDetail!!.get(0).avaialbleSeats
            trainBookingRequest!!.token=token;
            trainBookingRequest!!.userDate=userData;
            trainBookingRequest!!.agentCode=loginModel!!.Data.DoneCardUser;
            trainBookingRequest!!.userType=loginModel!!.Data.UserType;

            var trainBookFragment=TrainBookingConfirmationFragment()
            val bundle = Bundle()
            bundle.putSerializable("trainRequest", trainBookingRequest)
            bundle.putSerializable("trainPreBookResponse", response)
            bundle.putString("DOJ", doj)
            trainBookFragment.arguments=bundle
            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(trainBookFragment)

        }else{
            return
        }
    }

    private fun checkCredential() {
        var agentCode = loginModel!!.Data.DoneCardUser
        var userTypr = loginModel!!.Data.UserType
        NetworkCall().callRailService(NetworkCall.getTrainApiInterface().getTrainCredentials(ApiConstants.CheckCredential,
                agentCode, userTypr, ApiConstants.MerchantId, "App"), requireContext(), true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerCredential(response, 1) //https://rail.justclicknpay.com/apiV1/RailEngine/CheckCredential
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerCredential(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
//                    Toast.makeText(requireContext(),senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    bookTrain(senderResponse.credentialData[0].token, senderResponse.credentialData[0].userData)
                } else {
                    Toast.makeText(requireContext(), senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPass(type:Int, position:Int){
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(layoutInflater.inflate(R.layout.train_passanger_view, null))
        var radioGroup:RadioGroup?=dialog.findViewById(R.id.genderRadioGroup)
        var nameEdt: EditText? =dialog.findViewById(R.id.nameEdt)
        var ageEdt: EditText? =dialog.findViewById(R.id.ageEdt)
        var berthCheck: CheckBox? =dialog.findViewById(R.id.berthCheck)
        var textView: TextView? =dialog.findViewById(R.id.passengerCountTv)
        var nationality:Spinner?= dialog.findViewById(R.id.nationality)
        var birthPref:Spinner?= dialog.findViewById(R.id.birthPref)
        var foodChoice:Spinner?= dialog.findViewById(R.id.foodChoice)
        var foodLin:LinearLayout?= dialog.findViewById(R.id.foodLin)
        textView!!.text="Passenger "+(position+1)
        var nation = ArrayList<String>()
        nation.add("Indian")
        var list = ArrayList<String>()
        list.add(NoPreference)
        if(fareRuleResponse!!.bkgCfg!!.applicableBerthTypes!=null){
            for(i in fareRuleResponse!!.bkgCfg!!.applicableBerthTypes.indices){
                list.add(getBerthName(fareRuleResponse!!.bkgCfg!!.applicableBerthTypes[i]))
            }
        }/*else{
            list.add(WindowSeat)
        }*/

        var adapter=ArrayAdapter<String>(requireContext(), R.layout.spinner_item, R.id.name_tv, list)
        birthPref!!.adapter=adapter

        var adapterNation=ArrayAdapter<String>(requireContext(), R.layout.spinner_item, R.id.name_tv, nation)
        nationality!!.adapter=adapterNation

        var foodList = ArrayList<String>()
        if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
            foodLin!!.visibility=View.VISIBLE
            for(i in fareRuleResponse!!.bkgCfg!!.foodDetails.indices){
//                foodList.add(fareRuleResponse!!.bkgCfg!!.foodDetails[i])
                foodList.add(getFoodName(fareRuleResponse!!.bkgCfg!!.foodDetails[i]))
            }
            val aa = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, R.id.name_tv, foodList)
            foodChoice!!.adapter=aa
        }else{
            foodLin!!.visibility=View.GONE
        }

        var isAdd:Boolean
        if(position<passengerArray!!.size){
            nameEdt!!.setText(passengerArray!!.get(position).passengerName)
            ageEdt!!.setText(passengerArray!!.get(position).passengerAge)
            if(Integer.parseInt(passengerArray!!.get(position).passengerAge)<12){
                berthCheck!!.isEnabled=true;
            }
            berthCheck!!.isChecked = passengerArray!!.get(position).childBerthFlag!=null &&
                    passengerArray!!.get(position).childBerthFlag.equals("True")
            isAdd=false
        }else{
            isAdd=true
        }

        ageEdt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty()) {
                    var a: Int = Integer.parseInt(s.toString())
                    if(a in 5..11){
                        berthCheck!!.isEnabled=true
                        berthCheck!!.isChecked=true
                    }else if(a>120){
                        ageEdt!!.error = "Age must be smaller than 120"
                    }else{
                        berthCheck!!.isEnabled=false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        dialog.findViewById<TextView>(R.id.addPassTv)!!.setOnClickListener {
            if(!Common.isNameValid(nameEdt!!.text.toString())){
                nameEdt!!.setError("Please enter valid name")
            }else if(ageEdt!!.text.toString().isEmpty() || Integer.parseInt(ageEdt!!.text.toString())<5 || Integer.parseInt(ageEdt!!.text.toString())>120){
                ageEdt.setError("Please enter valid age")
            }else{
                var genderRadio:RadioButton?=dialog.findViewById(radioGroup!!.checkedRadioButtonId)
                if(isAdd){
                    var passenger:TrainBookingRequest.adultRequest=TrainBookingRequest().adultRequest()
                    passenger.passengerName=nameEdt!!.text.toString()
                    passenger.passengerAge=ageEdt!!.text.toString()
                    passenger.passengerGender=getGender(genderRadio!!.text.toString())
//                    passenger.passengerBerthChoice=birthPref.selectedItem.toString()
                    passenger.passengerBerthChoice=getBerthChoice(birthPref.selectedItem.toString())
                    var food=""
                    if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
                        passenger.passengerFoodChoice=getFoodChoice(foodChoice!!.selectedItem.toString())
                        food=foodChoice.selectedItem.toString()
                    }else{
                        passenger.passengerFoodChoice=""
                    }
                    if(berthCheck!!.isEnabled){
                        if(berthCheck.isChecked){
                            passenger.childBerthFlag="True"
                        }else{
                            passenger.childBerthFlag="False"
                        }
                    }else{
                        passenger.childBerthFlag=null
                    }
                    passenger.type=type
                    passengerArray!!.add(passenger)
                    addPassenger(passenger.passengerName, passenger.passengerAge, passenger.passengerGender, food)

                }else{
                    passengerArray!!.get(position).passengerName=nameEdt!!.text.toString()
                    passengerArray!!.get(position).passengerAge=ageEdt!!.text.toString()
                    passengerArray!!.get(position).passengerGender=getGender(genderRadio!!.text.toString())
//                    passenger.passengerBerthChoice=birthPref.selectedItem.toString()
                    passengerArray!!.get(position).passengerBerthChoice=getBerthChoice(birthPref.selectedItem.toString())
                    if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
                        passengerArray!!.get(position).passengerFoodChoice=getFoodChoice(foodChoice!!.selectedItem.toString())
                    }
                    if(berthCheck!!.isEnabled){
                        if(berthCheck.isChecked){
                            passengerArray!!.get(position).childBerthFlag="True"
                        }else{
                            passengerArray!!.get(position).childBerthFlag="False"
                        }
                    }else{
                        passengerArray!!.get(position).childBerthFlag=null
                    }
                    passengerArray!!.get(position).type=type
                    refreshPassengerList()
                }

                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun getBerthName(value: String?): String {
        if(value.equals("LB")){
            return LowerBerth
        }else if(value.equals("UB")){
            return UpperBerth
        }else if(value.equals("MB")){
            return MiddleBerth
        }else if(value.equals("SU")){
            return SideUpper
        }else if(value.equals("SL")){
            return SideLower
        }else if(value.equals("WS")){
            return WindowSeat
        }else{
            return ""
        }
    }

    private fun getBerthChoice(berthValue: String): String? {
        var berth=""
        when (berthValue) {
            NoPreference -> berth=""
            LowerBerth -> berth="LB"
            UpperBerth -> berth="UB"
            MiddleBerth -> berth="MB"
            SideLower -> berth="SL"
            SideUpper -> berth="SU"
            WindowSeat -> berth="WS"
            else -> { // Note the block
                berth=""
            }
        }
        return berth
    }

    private fun getFoodName(value: String?): String {
        if(value.equals("V")){
            return FoodChoice.Veg
        }else if(value.equals("N")){
            return FoodChoice.NonVeg
        }else if(value.equals("D")){
            return FoodChoice.DoNotSelect
        }else if(value.equals("E")){
            return FoodChoice.Snacks
        }else if(value.equals("J")){
            return FoodChoice.JainMeal
        }else if(value.equals("F")){
            return FoodChoice.VegDiabetic
        }else if(value.equals("G")){
            return FoodChoice.NonVegDiabetic
        }else if(value.equals("T")){
            return FoodChoice.TeaCoffee
        }else{
            return value!!
        }
    }

    private fun getFoodChoice(value: String): String? {
        var food=""
        when (value) {
            FoodChoice.Veg -> food="V"
            FoodChoice.NonVeg -> food="N"
            FoodChoice.DoNotSelect -> food="D"
            FoodChoice.Snacks -> food="E"
            FoodChoice.JainMeal -> food="J"
            FoodChoice.VegDiabetic -> food="F"
            FoodChoice.NonVegDiabetic -> food="G"
            FoodChoice.TeaCoffee -> food="T"
            else -> { // Note the block
                food=value
            }
        }
        return food
    }

    private fun getGender(gender: String): String? {
        if(gender.equals("Male")){
            return "M"
        }else if(gender.equals("Female")){
            return "F"
        }else{
            return "T"
        }
    }

    private fun addInf(type:Int, position:Int){
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(layoutInflater.inflate(R.layout.train_infant_view, null))
        var radioGroup:RadioGroup?=dialog.findViewById(R.id.genderRadioGroup)
        var nameEdt: TextView? =dialog.findViewById(R.id.nameEdt)
        var textView:TextView?=dialog.findViewById(R.id.passengerCountTv)
        var infantAge:Spinner?=dialog.findViewById(R.id.infantAge)

        textView!!.text="Passenger "+(position+1)
        var list = ArrayList<String>()
        list.add("Below 1 year")
        list.add("1")
        list.add("2")
        list.add("3")
        list.add("4")
        val aa = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,list)
        var adapter=ArrayAdapter<String>(requireContext(), R.layout.spinner_item, R.id.name_tv, list)
        infantAge!!.adapter=adapter

        var isAdd:Boolean
        if(position<passengerArray!!.size){
            nameEdt!!.text=passengerArray!!.get(position).passengerName
//            ageEdt!!.text=passengerArray!!.get(position).passengerAge
            isAdd=false
        }else{
            isAdd=true
        }

        dialog.findViewById<TextView>(R.id.addPassTv)!!.setOnClickListener {
            if(!Common.isNameValid(nameEdt!!.text.toString())){
                nameEdt!!.setError("Please enter valid name")
            }else{
                var genderRadio:RadioButton?=dialog.findViewById(radioGroup!!.checkedRadioButtonId)
                if(isAdd){
                    var passenger:TrainBookingRequest.adultRequest=TrainBookingRequest().adultRequest()
                    passenger.passengerName=nameEdt!!.text.toString()
                    if(infantAge.selectedItemPosition==0){
                        passenger.passengerAge="1"
                    }else{
                        passenger.passengerAge=infantAge.selectedItem.toString()
                    }
                    passenger.passengerGender=getGender(genderRadio!!.text.toString())
                    passenger.type=type
                    passengerArray!!.add(passenger)
                    addPassenger(passenger.passengerName, passenger.passengerAge, passenger.passengerGender, "")

                }else{
                    passengerArray!!.get(position).passengerName=nameEdt!!.text.toString()
                    if(infantAge.selectedItemPosition==0){
                        passengerArray!!.get(position).passengerAge="1"
                    }else{
                        passengerArray!!.get(position).passengerAge=infantAge.selectedItem.toString()
                    }
                    passengerArray!!.get(position).passengerGender=getGender(genderRadio!!.text.toString())
                    passengerArray!!.get(position).type=type
                    refreshPassengerList()
                }

                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addPassenger(name:String, age:String, gender:String, food:String){
        val child: View = layoutInflater.inflate(R.layout.train_passanger_show, null)
        var count:TextView=child.findViewById(R.id.passengerCountTv)
        var nameTv:TextView=child.findViewById(R.id.nameTv)
        nameTv.text= name
        var ageTv:TextView=child.findViewById(R.id.ageTv)
        var delete:TextView=child.findViewById(R.id.delete)
        var genderTv:TextView=child.findViewById(R.id.genderTv)
        var foodTv:TextView=child.findViewById(R.id.foodTv)
        ageTv.text= age
        genderTv.text= gender
        count.text=(passengerContainerLin!!.childCount+1).toString()
        if(food.isEmpty()){
            foodTv.text=""
        }else{
            foodTv.text= "Food- $food"
        }
        child.setOnClickListener{
            if(passengerArray!!.get(passengerContainerLin!!.indexOfChild(child)).type==ADULT){
                addPass(ADULT, passengerContainerLin!!.indexOfChild(child))
            }else{
                addInf(INFANT, passengerContainerLin!!.indexOfChild(child))
            }
        }
        delete.setOnClickListener{
//            passengerContainerLin!!.removeView(child)
            passengerArray!!.removeAt(passengerContainerLin!!.indexOfChild(child))
            refreshPassengerList()
        }
        passengerContainerLin!!.addView(child)
    }

    private fun refreshPassengerList() {
        passengerContainerLin!!.removeAllViews()
        for(list in passengerArray!!.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.passengerName
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var delete:TextView=child.findViewById(R.id.delete)
            var genderTv:TextView=child.findViewById(R.id.genderTv)
            ageTv.text= list.passengerAge
            count.text=(passengerContainerLin!!.childCount+1).toString()
            genderTv.text=list.passengerGender

            child.setOnClickListener{
                if(passengerArray!!.get(passengerContainerLin!!.indexOfChild(child)).type==ADULT){
                    addPass(ADULT, passengerContainerLin!!.indexOfChild(child))
                }else{
                    addInf(INFANT, passengerContainerLin!!.indexOfChild(child))
                }
            }
            delete.setOnClickListener{
//                passengerContainerLin!!.removeView(child)
                passengerArray!!.removeAt(passengerContainerLin!!.indexOfChild(child))
                refreshPassengerList()
            }
            passengerContainerLin!!.addView(child)
        }
    }

    private fun setFonts() {
        addInfantTv!!.setTypeface(Common.OpenSansRegularTypeFace(requireContext()))
        addPassTv!!.setTypeface(Common.OpenSansRegularTypeFace(requireContext()))
    }

    private fun getSpinnerAdapter(data: Array<String?>): ArrayAdapter<String?> {
        val adapter = ArrayAdapter(requireContext(),
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data)
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown)
        return adapter
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(requireContext(), resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

    private fun makeTextViewResizable(hotelDescriptionTv: TextView, i: Int, s: String, b: Boolean) {
        run {
            if (hotelDescriptionTv.tag == null) {
                hotelDescriptionTv.tag = hotelDescriptionTv.text
            }
            val vto = hotelDescriptionTv.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val obs = hotelDescriptionTv.viewTreeObserver
                    obs.removeGlobalOnLayoutListener(this)
                    if (i == 0) {
                        val lineEndIndex = hotelDescriptionTv.layout.getLineEnd(0)
                        val text =
                            hotelDescriptionTv.text.subSequence(0, lineEndIndex - s.length + 1)
                                .toString() + " " + s
                        hotelDescriptionTv.text = text
                        hotelDescriptionTv.movementMethod = LinkMovementMethod.getInstance()
                        hotelDescriptionTv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(hotelDescriptionTv.text.toString()),
                                hotelDescriptionTv,
                                i,
                                s,
                                b
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else if (i > 0 && hotelDescriptionTv.lineCount >= i) {
                        val lineEndIndex = hotelDescriptionTv.layout.getLineEnd(i - 1)
                        val text =
                            hotelDescriptionTv.text.subSequence(0, lineEndIndex - s.length + 1)
                                .toString() + " " + s
                        hotelDescriptionTv.text = text
                        hotelDescriptionTv.movementMethod = LinkMovementMethod.getInstance()
                        hotelDescriptionTv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(hotelDescriptionTv.text.toString()),
                                hotelDescriptionTv,
                                i,
                                s,
                                b
                            ), TextView.BufferType.SPANNABLE
                        )
                    } else {
                        val lineEndIndex = hotelDescriptionTv.layout
                            .getLineEnd(hotelDescriptionTv.layout.lineCount - 1)
                        val text =
                            hotelDescriptionTv.text.subSequence(0, lineEndIndex)
                                .toString() + " " + s
                        hotelDescriptionTv.text = text
                        hotelDescriptionTv.movementMethod = LinkMovementMethod.getInstance()
                        hotelDescriptionTv.setText(
                            addClickablePartTextViewResizable(
                                Html.fromHtml(hotelDescriptionTv.text.toString()),
                                hotelDescriptionTv,
                                lineEndIndex,
                                s,
                                b
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                }
            })
        }
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        i: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    if (viewMore) {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, -1, "See Less", false)
                    } else {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, 3, ".. See More", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }


    var requestString="{\n" +
            "  \"journeyDetails\": [\n" +
            "    {\n" +
            "      \"trainName\": \"SHRAMJIVI EXP SP\",\n" +
            "      \"trainNo\": \"02392\",\n" +
            "      \"fromStation\": \"NEW DELHI\",\n" +
            "      \"toStation\": \"PATNA JN\",\n" +
            "      \"fromStationCode\": \"NDLS\",\n" +
            "      \"toStationCode\": \"PNBE\",\n" +
            "      \"boardingStation\": \"NEW DELHI\",\n" +
            "      \"boardingStationCode\": \"NDLS\",\n" +
            "      \"reservationUpTo\": \"PATNA JN\",\n" +
            "      \"reservationUpToCode\": \"PNBE\",\n" +
            "      \"class\": \"2S\",\n" +
            "      \"quota\": \"GN\",\n" +
            "      \"bookingFlag\": \"Y\",\n" +
            "      \"departTime\": \"13:10\",\n" +
            "      \"arrivalTime\": \"07:05\",\n" +
            "      \"journeyDate\": \"20211230\",\n" +
            "      \"moreThanOneDay\": \"True\",\n" +
            "      \"enquiryType\": \"3\",\n" +
            "      \"reservationChoice\": \"99\",\n" +
            "      \"ticketType\": \"E\",\n" +
            "      \"reservationMode\": \"MOBILE_ANDROID\",\n" +
            "      \"travelInsuranceOpted\": \"True\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"destinationDetail\": [\n" +
            "    {\n" +
            "      \"address\": \"kurthaul\",\n" +
            "      \"pinCode\": \"804453\",\n" +
            "      \"stateName\": \"BIHAR\",\n" +
            "      \"city\": \"Patna\",\n" +
            "      \"postOffice\": \"Kurthaul B.O\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"passengerAdditionalDetail\": [\n" +
            "    {\n" +
            "      \"mobile\": \"8468862808\",\n" +
            "      \"coach\": \"string\",\n" +
            "      \"preference\": \"LB\",\n" +
            "      \"email\": \"iamrohitesh@gmail.com\",\n" +
            "      \"remarks\": \"test\",\n" +
            "      \"adultCount\": 1,\n" +
            "      \"childCount\": 0,\n" +
            "      \"totalPaxCount\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"gstDetails\": [\n" +
            "    {\n" +
            "      \"gst\": \"\",\n" +
            "      \"flat\": \"\",\n" +
            "      \"gstName\": \"\",\n" +
            "      \"pinCode\": \"\",\n" +
            "      \"stateCity\": \"\",\n" +
            "      \"city\": \"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"fareDetail\": [\n" +
            "    {\n" +
            "      \"baseFare\": 0,\n" +
            "      \"serviceCharge\": 0,\n" +
            "      \"agentServiceCharge\": 0,\n" +
            "      \"concession\": 0,\n" +
            "      \"pgCharge\": 0,\n" +
            "      \"totalFare\": 0\n" +
            "    }\n" +
            "  ],\n" +
            "  \"adultRequest\": [\n" +
            "    {\n" +
            "      \"passengerName\": \"TEST\",\n" +
            "      \"passengerAge\": \"29\",\n" +
            "      \"passengerGender\": \"M\",\n" +
            "      \"currentBerthChoice\": \"\",\n" +
            "      \"passengerCardNumber\": \"\",\n" +
            "      \"passengerBerthChoice\": \"\",\n" +
            "      \"passengerNationality\": \"IN\",\n" +
            "      \"passengerCardType\": \"\",\n" +
            "      \"passengerConcession\": \"\",\n" +
            "      \"forgoConcession\": \"\",\n" +
            "      \"passengerIcardFlag\": \"\",\n" +
            "      \"passengerBedrollChoice\": \"false\",\n" +
            "      \"passengerFoodChoice\": \"\",\n" +
            "      \"passengerSerialNumber\": \"1\",\n" +
            "      \"concessionOpted\": \"True\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"childRequest\": [\n" +
            "    {\n" +
            "      \"passengerName\": \"\",\n" +
            "      \"passengerAge\": \"\",\n" +
            "      \"passengerGender\": \"\"\n" +
            "    }\n" +
            "  ],\n" +
            "   \"finalBookingFareAndJourneyDetails\": [\n" +
            "    {\n" +
            "      \"totalFare\": 0,\n" +
            "      \"wpServiceTax\": 0,\n" +
            "      \"wpServiceCharge\": 0,\n" +
            "      \"travelInsuranceCharge\": 0,\n" +
            "      \"travelInsuranceServiceTax\": 0,\n" +
            "      \"cateringCharge\": 0,\n" +
            "      \"class\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"

}
