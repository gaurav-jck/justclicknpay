package com.justclick.clicknbook.Fragment.train

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.view.children
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.TrainCancelResponse
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_cancel_details.*
import kotlinx.android.synthetic.main.fragment_train_cancel_details.passengerContainerLin
import kotlinx.android.synthetic.main.fragment_train_cancel_details.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.statusTv
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TrainCancelDetailsFragment : Fragment() {

    var trainResponse: TrainCancelTicketDetailResponse?=null
    var loginModel:LoginModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_cancel_details, container, false)

        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("cancelResponse") as TrainCancelTicketDetailResponse
            setData(view)
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.okTv.setOnClickListener {
            checkSelected()
        }

        view.refundTv.setOnClickListener {
            getCancelIdForRefund()
        }

        return view
    }

    private fun setData(view: View) {

        view.statusTv.text="ResId : "+trainResponse!!.trainCancelDetail.get(0).transactionId
        view.pnrTv.text="Pnr : "+trainResponse!!.trainCancelDetail.get(0).pnr

        var journeyDetail=trainResponse!!.trainCancelDetail.get(0);
        view.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainNo+")"
        try{
            view.startTimeTv.text = journeyDetail.fromStation
            view.endTimeTv.text = journeyDetail.toStation
        }catch (e:Exception){

        }
        view.fromStnTv.text = journeyDetail.fromStationCode
        view.toStnTv.text = journeyDetail.toStationCode
        view.durationTv.text = ""
        view.classTv.text = "Passenger = "+trainResponse!!.paxCancelDetail.size+" | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+journeyDetail.boardingStation+" ("+journeyDetail.boardingStationCode+")"

        var seats=""
        view.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        view.passengerContainerLin!!.removeAllViews()
        var isRefund=false
        var isCancel=false
        for(list in trainResponse!!.paxCancelDetail.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_cancel_view, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var seatNoTv:TextView=child.findViewById(R.id.seatNoTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            var remarkLin:LinearLayout=child.findViewById(R.id.remarkLin)
            ageTv.text= list.age.toString()
            child.genderTv.text= list.gender
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            seatNoTv.text="Booking Status:"+list.bookingStatus
            statusTv.text="Current Status:"+list.currentStatus

            if(list.currentStatus.equals("CAN")){
                remarkLin.visibility=View.GONE
            }

            if(!isRefund && list.currentStatus.equals("CAN")){
                isRefund=true
            }
            if(!isCancel && (list.currentStatus.equals("CNF") || list.currentStatus.equals("RAC") || list.currentStatus.equals("WL")
                        || list.currentStatus.equals("TQWL"))){
                isCancel=true
            }

            view.passengerContainerLin!!.addView(child)
        }

        if(isRefund){
            view.refundTv.visibility=View.VISIBLE
        }else{
            view.refundTv.visibility=View.GONE
        }
        if(isCancel){
            view.okTv.visibility=View.VISIBLE
        }else{
            view.okTv.visibility=View.GONE
        }

    }

    private fun getCancelIdForRefund() {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelIdRefund(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelId?Pnr="
                +trainResponse!!.trainCancelDetail.get(0).pnr,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        val response = Gson().fromJson(responseString, TrainCancelResponse::class.java)
                        if(response.statusCode.equals("00")){
//                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
                            otpDialog(response.cancelIdDetail.get(0).cancellationId)
//                            otpAuthentication(response.cancelIdDetail.get(0).cancellationId)
                        }else{
                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
//                            otpDialog("cancelId")
                        }
                    } else {
                        hideCustomDialog()
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: java.lang.Exception) {
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

    class OtpResponse{
        var response:String?=null
        var resend_OTP_Left:String?=null
        var statusCode:String?=null
        var statusMessage:String?=null
    }

    private fun otpAuthentication(cancelId: String) {
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getRefundOtp(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/OtpAuthentication?Pnr="
                +trainResponse!!.trainCancelDetail.get(0).pnr+"&CancellationId="+cancelId,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        showCustomDialog()
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response?.body() != null) {
                        val responseData = Gson().fromJson(response.body()!!.string(), OtpResponse::class.java)
                        if(responseData.statusCode.equals("00")){
                            Toast.makeText(context, responseData.response, Toast.LENGTH_LONG).show()
                            otpDialog(cancelId)
                        }else{
                            Toast.makeText(context, responseData.response, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: java.lang.Exception) {
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

    class RefundRequest{
        var pnr:String?=null
        var cancellationId:String?=null
        var otp:String?=null
    }

    private fun otpDialog(cancelId: String) {
        var otpDialog = Dialog(requireContext())
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        otpDialog.setContentView(R.layout.train_otp_layout)
        otpDialog.setCancelable(false)
        val otp_edt = otpDialog.findViewById<EditText>(R.id.otp_edt)
        val submit = otpDialog.findViewById<TextView>(R.id.submit_btn)
        val resend_btn = otpDialog.findViewById<TextView>(R.id.resend_btn)
        val radioGroup = otpDialog.findViewById<RadioGroup>(R.id.radioGroup)
        val dialogCloseButton = otpDialog.findViewById<ImageButton>(R.id.close_btn)

        val radioButton=RadioButton(requireContext())
        radioButton.text=cancelId
        radioButton.isChecked=true
        radioGroup.addView(radioButton)
        submit.setOnClickListener {
            if(otp_edt.text.toString().length<4){
                Toast.makeText(requireContext(), "Please enter valid otp", Toast.LENGTH_SHORT).show()
            }else{
                refundRequest(cancelId, otp_edt.text.toString())
                Common.hideSoftInputFromDialog(otpDialog,requireContext())
                otpDialog.dismiss()
            }
        }
        resend_btn.setOnClickListener {
            otpAuthentication(cancelId)
        }
        dialogCloseButton.setOnClickListener {
            otpDialog.dismiss()
        }
        otpDialog.show()
    }

    private fun refundRequest(cancelId: String, otp: String) {
        var request= RefundRequest()
        request.cancellationId=cancelId
        request.pnr=trainResponse!!.trainCancelDetail.get(0).pnr
        request.otp=otp
        NetworkCall().callService(NetworkCall.getTrainApiInterface().trainRefundVerifyOtp(ApiConstants.VerifyOtpRefund, request,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App"),
                context,true)
        { response: ResponseBody?, responseCode: Int ->

            if (response != null) {
                responseHandlerRefund(response, request) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    class TrainRefundResponse {
        var statusCode: String? = null
        var statusMessage: String? = null
    }

    private fun responseHandlerRefund(response: ResponseBody, request: RefundRequest) {
        try{
            val refundResponse = Gson().fromJson(response.string(), TrainRefundResponse::class.java)
            if(refundResponse!=null){
                if(refundResponse.statusCode.equals("00")){
                    Toast.makeText(context, refundResponse.statusMessage, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, refundResponse.statusMessage, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    class CancelRequest{
        var transactionId:String?=null
        var paxData:ArrayList<PaxData>?=null

        class PaxData {
            var paxCancelId:String?=null
            var paxCancelRemarks:String?=null
        }
    }

    private fun checkSelected(): Boolean {
        var cancelRequest=CancelRequest()
        var list:ArrayList<CancelRequest.PaxData>?=ArrayList()
        var count=0
        for(pass in passengerContainerLin.children){
            var checkBox=pass.findViewById<CheckBox>(R.id.checkbox)
            var remark=pass.findViewById<EditText>(R.id.remarkEdt)
            var pax:CancelRequest.PaxData=CancelRequest.PaxData()
            if(checkBox.isChecked){
                if(remark.text.toString().isEmpty()){
                    Toast.makeText(requireContext(), "Please enter all selected remarks", Toast.LENGTH_SHORT).show()
                    return false
                }
                pax.paxCancelId=trainResponse!!.paxCancelDetail.get(count).paxPnr
                pax.paxCancelRemarks=remark.text.toString()
                list!!.add(pax)
            }
            count++
        }

        cancelRequest.transactionId=trainResponse!!.trainCancelDetail.get(0).transactionId
        cancelRequest.paxData=list

        if(list!!.size>0){
            cancelTicketConfirmation(cancelRequest)
            return true
        }else{
            Toast.makeText(requireContext(), "Please select passenger to cancel", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun cancelTicketConfirmation(cancelRequest: CancelRequest) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Confirm Cancellation")
        //set message for alert dialog
        builder.setMessage("Are you sure to cancel selected tickets?")
//        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // Create the AlertDialog
        var alertDialog: AlertDialog?=null
        //performing positive action
        builder.setNegativeButton("Cancel"){dialogInterface, which ->
            alertDialog!!.dismiss()
        }
        builder.setPositiveButton("Submit"){dialogInterface, which ->
            cancelSelected(cancelRequest)
        }
        alertDialog= builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun cancelSelected(cancelRequest: CancelRequest) {
        NetworkCall().callService(NetworkCall.getTrainApiInterface().trainCancelTicket(ApiConstants.CancelTicket, cancelRequest,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App"),
                context,true)
        { response: ResponseBody?, responseCode: Int ->

            if (response != null) {
                responseHandler(response, cancelRequest) //https://recharge.justclicknpay.com/Utility/BillPayment/GenerateToken
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody, cancelRequest: CancelRequest) {
        try{
            val cancelResponse = Gson().fromJson(response.string(), TrainCancelResponse::class.java)
            if(cancelResponse!=null){
                if(cancelResponse.statusCode.equals("00")){
                    Toast.makeText(context, cancelResponse.statusMessage, Toast.LENGTH_SHORT).show()
                    otpDialog(cancelResponse.paxCancel.get(0).cancellationId)
                }else{
                    Toast.makeText(context, cancelResponse.statusMessage, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, R.string.error_in_response, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }
}