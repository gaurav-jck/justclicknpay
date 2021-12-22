package com.justclick.clicknbook.Fragment.train

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.*
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse
import com.justclick.clicknbook.Fragment.jctmoney.response.PinCityResponse.PostOffice
import com.justclick.clicknbook.R
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import kotlinx.android.synthetic.main.fragment_train_book.*
import kotlinx.android.synthetic.main.fragment_train_book.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse
import com.justclick.clicknbook.Fragment.train.model.*
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.train_passanger_show.*
import kotlinx.android.synthetic.main.train_passanger_show.view.*
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class TrainBookFragment : Fragment(), View.OnClickListener {
    private final val ADULT:Int=1
    private final val INFANT:Int=2
    private final val NoPreference="No Preference"
    private final val LowerBerth="Lower Berth"
    private final val UpperBerth="Upper Berth"
    private final val MiddleBerth="Middle Berth"
    private final val SideUpper="Side Upper"
    private final val SideLower="Side Lower"
    private final val WindowSeat="Window Seat"
    private final val Veg="Veg"
    private final val NonVeg="Non Veg"
    private final val DoNotSelect="No Food"
    var loginModel:LoginModel?=null
    var passengerContainerLin:LinearLayout?=null
    var infantContainerLin:LinearLayout?=null
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
    private var pinCityResponseArrayList: ArrayList<PostOffice>? = null
    var trainResponse:TrainSearchDataModel?=null
    var fareRuleResponse:FareRuleResponse?=null
    var trainBookingRequest:TrainBookingRequest?=null
    var passengerArray:ArrayList<TrainBookingRequest.adultRequest>?=null
    var position:Int=0
    var fragView:View?=null

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
            fragView=view
            addInfantTv=view.findViewById(R.id.addInfantTv)
            addPassTv=view.findViewById(R.id.addPassTv)
            view.back_arrow.setOnClickListener(this)
            view.addPassTv.setOnClickListener(this)
            view.addInfantTv.setOnClickListener(this)
            view.removeInfantTv.setOnClickListener(this)
            view.bookTv.setOnClickListener(this)
            removePassTv=view.findViewById(R.id.removePassTv)
            removePassTv!!.setOnClickListener(this)
            view.gstLabelRel.setOnClickListener(this)
            view.fareLabelRel.setOnClickListener(this)
            passengerContainerLin=view.findViewById(R.id.passengerContainerLin)
            infantContainerLin=view.findViewById(R.id.infantContainerLin)

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

            view.baseFareEdt.setText(fareRuleResponse!!.baseFare)
            view.totalFareEdt.setText(fareRuleResponse!!.totalFare)
            view.concessionEdt.setText("0")
            view.pgChargeEdt.setText("0")
            view.serviceChargeEdt.setText("0")

            view.pin_edt.addTextChangedListener(object : TextWatcher {
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
            })

            view.pin_edt2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Toast.makeText(context, "count="+count+"  text="+s.toString(),Toast.LENGTH_SHORT).show();
                    if (s.length == 6 && before!=6) {
                        getGstCity(s.toString())
//                        getCityState2(s.toString())
                    } else {
                        clearCityState2()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            view.cityAtv.onItemClickListener = OnItemClickListener { parent, view, position, id ->
//                post = view.cityAtv.text.toString()
//                city = pinCityResponseArrayList!![position].district
//                state = pinCityResponseArrayList!![position].state
//                address = pinCityResponseArrayList!![position].district
//                state_edt.setText(state)
//                cityEdt.setText(city)
            }
            view.cityAtv.setOnClickListener(this)

            setFonts()

            callBoardingStation()

            view.spinnerBoardingStn.onItemSelectedListener = object : OnItemSelectedListener {
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
            R.id.fareLabelRel->
                showHideFare()
            R.id.cityAtv-> {
                Common.hideSoftKeyboard(context as Activity?)
                cityAtv.showDropDown()
            }
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
        if(fareView.visibility== VISIBLE){
            fareView.visibility= GONE
        }else{
            fareView.visibility= VISIBLE
        }
    }

    private fun showHideGst() {
        if(gstView.visibility== VISIBLE){
            gstView.visibility= GONE
        }else{
            gstView.visibility= VISIBLE
        }
    }

    class BoardingStnResponse{
        var boardingStationList :ArrayList<BoardingStationList>? =null
        class BoardingStationList{
            var stnNameCode:String?=null
        }
        /*{"boardingStationList":[{"stnNameCode":"ANAND VIHAR TRM - ANVT"},{"stnNameCode":"KANPUR CENTRAL - CNB"}]}*/
    }

    private fun callBoardingStation() {
        val apiService = APIClient.getClient("https://rail.justclicknpay.com/").create(ApiInterface::class.java)
        val call = apiService.getBoardingStn("https://rail.justclicknpay.com/apiV1/RailEngine/BoardingStation?Trainno="
                +fareRuleResponse!!.trainNo+"&Date="+journeyDate()+"&fromStation="+
                trainResponse!!.trainBtwnStnsList!!.get(position).fromStnCode+
                "&toStation="+trainResponse!!.trainBtwnStnsList!!.get(position).toStnCode+"&className="+fareRuleResponse!!.enqClass,
        loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<BoardingStnResponse?> {
            override fun onResponse(call: Call<BoardingStnResponse?>, response: Response<BoardingStnResponse?>) {
                try {
                    if (response != null && response.body() != null && response.body()!!.boardingStationList!=null) {
                        if(response.body()!!.boardingStationList!!.size>0){
                            boardingStnLabelRel.visibility = VISIBLE

                            var arr: Array<String?> =arrayOfNulls<String>(response.body()!!.boardingStationList!!.size)
                            for(pos in response.body()!!.boardingStationList!!.indices){
                                arr[pos]=response.body()!!.boardingStationList!!.get(pos).stnNameCode
                            }

                            spinnerBoardingStn.adapter=getSpinnerAdapter(arr)

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
        val apiService = APIClient.getClient("https://rail.justclicknpay.com/").create(ApiInterface::class.java)
        val call = apiService.getServiceCityPinResponse("https://rail.justclicknpay.com/apiV1/RailEngine/PostOffice?pincode="+pin)
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
                            Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getCityPin(pin: String, cityName: String?) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClient("https://rail.justclicknpay.com/").create(ApiInterface::class.java)
        val call = apiService.getServiceCityPin("https://rail.justclicknpay.com/apiV1/RailEngine/Postofficecity?pincode="+
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
                            cityAtv.setAdapter(getSpinnerAdapter(pinCity!!.postofficeList!!))
                            Common.hideSoftKeyboard(context as Activity?)
                            cityAtv.showDropDown()
                            city=pinCity!!.city
                            state=pinCity!!.state
                            state_edt.setText(state)
                            cityEdt.setText(city)
                        }else{
                            Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    hideCustomDialog()
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clearCityState() {
        city = ""
        state = ""
        pinCityResponseArrayList!!.clear()
        cityAtv.setAdapter(getSpinnerAdapter(arrayOfNulls<String>(0)))
        cityAtv.clearListSelection()
        cityAtv.setText("")
        state_edt.setText("")
    }

    private fun getGstCity(pin: String) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog()
        }
        val apiService = APIClient.getClient("https://rail.justclicknpay.com/").create(ApiInterface::class.java)
        val call = apiService.getServiceCityPinResponse("https://rail.justclicknpay.com/apiV1/RailEngine/PostOffice?pincode="+pin)
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
                        cityEdt2.setText(pinCity.cityList!!.get(0))
                        state_edt2.setText(pinCity!!.state)
                    } else {
                        hideCustomDialog()
                        clearCityState2()
                        Toast.makeText(context, "No data found for this pin code", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
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
                            cityEdt2.setText(response.body()!![0].postOffice.get(0).district)
                            state_edt2.setText(response.body()!![0].postOffice.get(0).state)
                        } else {
                            Toast.makeText(context, response.body()!![0].message, Toast.LENGTH_LONG).show()
                            clearCityState2()
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

    private fun clearCityState2() {
        city2 = ""
        state2 = ""
        cityEdt2.setText("")
        state_edt2.setText("")
    }

    private fun validate() {
        var isPass=false
        for(data in passengerArray!!.iterator()){
            if(data.type==ADULT){
                isPass=true
                break
            }
        }
        var mobile=mobileEdt.text.toString()
        var email=emailEdt.text.toString()
        if(!isPass){
            Toast.makeText(context, "Please add passenger", Toast.LENGTH_SHORT).show()
        }else if(!isGstValid()){
            Toast.makeText(context, "Please add all GST details", Toast.LENGTH_SHORT).show()
        }else if(!Common.isMobileValid(mobile)){
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show()
        } else if(!Common.isEmailValid(email)){
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show()
        }else if(addressEdt.text.toString().isEmpty()){
            Toast.makeText(context, R.string.empty_address, Toast.LENGTH_SHORT).show()
        }else if(pin_edt.text.toString().length<6){
            Toast.makeText(context, R.string.empty_and_invalid_pincode, Toast.LENGTH_SHORT).show()
        }else if(cityAtv.text.toString().isEmpty()){
            Toast.makeText(context, R.string.empty_post, Toast.LENGTH_SHORT).show()
        }else{
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
        NetworkCall().callRailService(NetworkCall.getApiInterface().bookTrain(trainBookingRequest,ApiConstants.PreBook,
                agentCode, userTypr, ApiConstants.MerchantId, "App", token, userData), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1, token, userData) //https://rail.justclicknpay.com/apiV1/RailEngine/CheckCredential
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeBookingRequest(): TrainBookingRequest {
//        var trainBookingRequest:TrainBookingRequest= TrainBookingRequest();
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
        journey.reservationChoice="99"
        journey.ticketType="F"
        var checkbox=insuranceRadioGroup.findViewById<RadioButton>(R.id.travelRadioYes)
        if(checkbox.isChecked){
            journey.travelInsuranceOpted="True"
        }else{
            journey.travelInsuranceOpted="False"
        }

        journeyList.add(journey)

        var destination:TrainBookingRequest.destinationDetail=trainBookingRequest!!.destinationDetail()
        destination.address=addressEdt.text.toString()
        destination.pinCode=pin_edt.text.toString()
        destination.stateName=state_edt.text.toString().uppercase()
        destination.city=cityEdt.text.toString()
        destination.postOffice=cityAtv.text.toString()

        destinationList.add(destination)

        var gstDetail:TrainBookingRequest.gstDetails=trainBookingRequest!!.gstDetails()
        gstDetail.gst=gstEdt.text.toString()
        gstDetail.gstName=gstNameEdt.text.toString()
        gstDetail.flat=gstFlatEdt.text.toString()
        gstDetail.city=cityEdt2.text.toString()
        gstDetail.pinCode=pin_edt2.text.toString()
        gstDetail.stateCity=state_edt2.text.toString()
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
        additionalDetail.email=emailEdt.text.toString()
        additionalDetail.mobile=mobileEdt.text.toString()
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
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeTrainBookingRequest(response: TrainPreBookResponse, token:String, userData:String) {
        if(response.finalBookingFareAndJourneyDetail!=null){

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
        NetworkCall().callRailService(NetworkCall.getApiInterface().getTrainCredentials(ApiConstants.CheckCredential,
                agentCode, userTypr, ApiConstants.MerchantId, "App"), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandlerCredential(response, 1) //https://rail.justclicknpay.com/apiV1/RailEngine/CheckCredential
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandlerCredential(response: ResponseBody, TYPE: Int) {
        try {
            val senderResponse = Gson().fromJson(response.string(), CheckCredentialResponse::class.java)
            if (senderResponse != null) {
                if (senderResponse.statusCode == "00") {
                    Toast.makeText(requireContext(),senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    bookTrain(senderResponse.credentialData[0].token, senderResponse.credentialData[0].userData)
                } else {
                    Toast.makeText(context, senderResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPass(type:Int, position:Int){
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(layoutInflater.inflate(R.layout.train_passanger_view, null))
        var radioGroup:RadioGroup?=dialog.findViewById(R.id.genderRadioGroup)
        var nameEdt: TextView? =dialog.findViewById(R.id.nameEdt)
        var ageEdt: TextView? =dialog.findViewById(R.id.ageEdt)
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
        for(i in fareRuleResponse!!.bkgCfg!!.applicableBerthTypes.indices){
            list.add(getBerthName(fareRuleResponse!!.bkgCfg!!.applicableBerthTypes[i]))
        }

        var isAdd:Boolean
        if(position<passengerArray!!.size){
            nameEdt!!.text=passengerArray!!.get(position).passengerName
            ageEdt!!.text=passengerArray!!.get(position).passengerAge
            isAdd=false
        }else{
            isAdd=true
        }

        dialog.addPassTv.setOnClickListener {
            if(!Common.isNameValid(nameEdt!!.text.toString())){
                nameEdt!!.setError("Please enter valid name")
            }else if(ageEdt!!.text.toString().isEmpty() || Integer.parseInt(ageEdt!!.text.toString())<5){
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
                    if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
                        passenger.passengerFoodChoice=getFoodChoice(foodChoice!!.selectedItem.toString())
                    }
                    passenger.type=type
                    passengerArray!!.add(passenger)
                    addPassenger(passenger.passengerName, passenger.passengerAge, passenger.passengerGender)

                }else{
                    passengerArray!!.get(position).passengerName=nameEdt!!.text.toString()
                    passengerArray!!.get(position).passengerAge=ageEdt!!.text.toString()
                    passengerArray!!.get(position).passengerGender=getGender(genderRadio!!.text.toString())
//                    passenger.passengerBerthChoice=birthPref.selectedItem.toString()
                    passengerArray!!.get(position).passengerBerthChoice=getBerthChoice(birthPref.selectedItem.toString())
                    if(fareRuleResponse!!.bkgCfg.foodChoiceEnabled.equals("true")){
                        passengerArray!!.get(position).passengerFoodChoice=getFoodChoice(foodChoice!!.selectedItem.toString())
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
            return Veg
        }else if(value.equals("N")){
            return NonVeg
        }else if(value.equals("D")){
            return DoNotSelect
        }else{
            return value!!
        }
    }

    private fun getFoodChoice(value: String): String? {
        var food=""
        when (value) {
            Veg -> food="V"
            NonVeg -> food="N"
            DoNotSelect -> food="D"
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

        dialog.addPassTv.setOnClickListener {
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
                    addPassenger(passenger.passengerName, passenger.passengerAge, passenger.passengerGender)

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

    private fun addPassenger(name:String, age:String, gender:String){
        val child: View = layoutInflater.inflate(R.layout.train_passanger_show, null)
        var count:TextView=child.findViewById(R.id.passengerCountTv)
        var nameTv:TextView=child.findViewById(R.id.nameTv)
        nameTv.text= name
        var ageTv:TextView=child.findViewById(R.id.ageTv)
        var delete:TextView=child.findViewById(R.id.delete)
        var genderTv:TextView=child.findViewById(R.id.genderTv)
        ageTv.text= age
        genderTv.text= gender
        count.text=(passengerContainerLin!!.childCount+1).toString()

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
            ageTv.text= list.passengerAge
            count.text=(passengerContainerLin!!.childCount+1).toString()
            child.genderTv.text=list.passengerGender

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
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
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
