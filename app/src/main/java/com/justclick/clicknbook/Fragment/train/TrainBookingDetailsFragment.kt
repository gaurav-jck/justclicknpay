package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.Common
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_booking_details.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.*
import kotlinx.android.synthetic.main.train_passanger_seats_show.view.statusTv
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class TrainBookingDetailsFragment : Fragment() {

    var trainResponse: PnrResponse?=null
    var loginModel: LoginModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_train_booking_details, container, false)

        if(arguments!=null) {
            trainResponse = requireArguments().getSerializable("trainResponse") as PnrResponse
            var fromStationTv: TextView =view.findViewById(R.id.fromStationTv)
            var toStationTv: TextView =view.findViewById(R.id.toStationTv)
            var dateTv: TextView =view.findViewById(R.id.dateTv)
            fromStationTv.text=trainResponse!!.transactionDetails.get(0).boarding
            toStationTv.text=trainResponse!!.transactionDetails.get(0).resvUpto
            dateTv.text=trainResponse!!.transactionDetails.get(0).dateOfJourney

            setData(view)
        }

        view.fareLabelRel.setOnClickListener {
            showHideFare(view)
        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.okTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.changeBoarding.setOnClickListener{

        }
        view.cancelTicket.setOnClickListener{
            cancelTicket(trainResponse!!.reservationid)
        }

        return view
    }

    private fun showHideFare(view: View) {
        if(view.fareView.visibility== AdapterView.VISIBLE){
            view.fareView.visibility= AdapterView.GONE
        }else{
            view.fareView.visibility= AdapterView.VISIBLE
        }
    }

    private fun setData(view: View) {

        view.statusTv.text="ResId : "+trainResponse!!.reservationid
        view.pnrTv.text="Pnr : "+trainResponse!!.pnr

        var journeyDetail=trainResponse!!.transactionDetails.get(0);
        view.trainNameTv.text = journeyDetail.trainName+" ("+journeyDetail.trainnumber+")"
        try{
            view.startTimeTv.text = journeyDetail.scheduledDeparture.substring(0, 10)+"\n"+
                    journeyDetail.scheduledDeparture.substring(11, journeyDetail.scheduledDeparture.length)
            view.endTimeTv.text = journeyDetail.scheduledArrival.substring(0, 10)+"\n"+
                    journeyDetail.scheduledArrival.substring(11, journeyDetail.scheduledArrival.length)
        }catch (e:Exception){

        }
        view.fromStnTv.text = journeyDetail.from
        view.toStnTv.text = journeyDetail.to
        view.durationTv.text = journeyDetail.duration
        view.classTv.text = "Adult = "+journeyDetail.adult+" | "+"Child = "+journeyDetail.child +
                " | "+"Class = "+journeyDetail.journeyClass + " | "+ "Quota = "+journeyDetail.quota
        view.boardingStn.text="Boarding point - "+journeyDetail.boarding

        var seats=""
        view.seatsTv.text = seats

        if(seats.contains("AVAIL")||
                seats.contains("AVBL")){
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.green))
        }else{
            view.seatsTv.setTextColor(requireContext().resources.getColor(R.color.blue))
        }

        view.passengerContainerLin!!.removeAllViews()
        for(list in trainResponse!!.passengerDetails.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_seats_show, null)
            var count:TextView=child.findViewById(R.id.passengerCountTv)
            var nameTv:TextView=child.findViewById(R.id.nameTv)
            nameTv.text= list.name
            var ageTv:TextView=child.findViewById(R.id.ageTv)
            var seatNoTv:TextView=child.findViewById(R.id.seatNoTv)
            var statusTv:TextView=child.findViewById(R.id.statusTv)
            ageTv.text= list.age
            child.genderTv.text= list.sex
            count.text=(view.passengerContainerLin!!.childCount+1).toString()

            seatNoTv.text="Seat no."/*+list.sNo*/
            statusTv.text=list.bookingStatus

            view.passengerContainerLin!!.addView(child)
        }

        var fareDetails=trainResponse!!.fareDetails.get(0)
        view.baseFareTv.setText(fareDetails.ticketFar.toString())
        view.cateringTv.setText(fareDetails.cateringCharge.toString())
        view.conFeeTv.setText(fareDetails.convenienceFee.toString())
        view.pgChargeTv.setText(fareDetails.pgCharges.toString())
        view.insuranceTv.setText(fareDetails.travelInsurancePremium.toString())
        view.serviceChargeTv.setText(fareDetails.travelAgentServiceCharge.toString())
        view.totalFareTv.setText(fareDetails.totalFare.toString())

    }

    private fun journeyDate(departDate: String?): String? {
        if(departDate!!.contains("/")){
            return Common.getServerDateFormat().format(SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(departDate))
        }else{
            return Common.getServerDateFormat().format(SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(departDate))
        }
    }

    private fun changeBoardingStn(list: TrainBookingListResponseModel.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getBoardingStnForChange("https://rail.justclicknpay.com/apiV1/RailEngine/BoardingStation?Trainno="
                +list!!.trainNumber+"&Date="+journeyDate(list.departDate)+"&fromStation="+
                list.sourceCode+ "&toStation="+list.destinationCode+"&className="+list.journeyClass,
            loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        responseString=responseString.replace("boardingStationList\":{", "boardingStationList\":[{")
                        responseString=responseString.replace("},\"mealChoiceenable", "}],\"mealChoiceenable")
                        val boardingStnResponse = Gson().fromJson(responseString, TrainBookFragment.BoardingStnResponse::class.java)
                        if(boardingStnResponse.boardingStationList!=null && boardingStnResponse.boardingStationList!!.size>0){

                            var arr: Array<String?> =arrayOfNulls<String>(boardingStnResponse.boardingStationList!!.size)
                            for(pos in boardingStnResponse.boardingStationList!!.indices){
                                arr[pos]=boardingStnResponse.boardingStationList!!.get(pos).stnNameCode
                            }

                            val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
                            (context as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)

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

    private fun cancelTicket(reservationId:String) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(
            ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelDetail?TransactionId="+reservationId,
            loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        val response = Gson().fromJson(responseString, TrainCancelTicketDetailResponse::class.java)
                        if(response.statusCode.equals("00")){
//                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putSerializable("cancelResponse", response)
                            val fragment = TrainCancelDetailsFragment()
                            fragment.arguments = bundle
                            (context as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                        }else{
                            Toast.makeText(context,response.statusMessage, Toast.LENGTH_LONG).show()
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

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }
}