package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.Fragment.train.model.PnrStatusResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.R
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.retrofit.APIClient
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.retrofit.ApiInterface
import com.justclick.clicknbook.utils.MyCustomDialog
import com.justclick.clicknbook.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_train_pnr_status_detail.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrainPnrStatusDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrainPnrStatusDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var pnrResponse: PnrStatusResponse? = null
    private var param2: String? = null
    private var loginModel:LoginModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, context)
        arguments?.let {
            pnrResponse = it.getSerializable(ARG_PARAM1) as PnrStatusResponse
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_train_pnr_status_detail, container, false)

        if(pnrResponse!=null){
//            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
    try {
        setPnrStatusData(view,pnrResponse!!)
    }catch (e:Exception){}

        }

        view.back_arrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        /*view.cancelTv.setOnClickListener{
            cancelTicket(pnrResponse!!.pnrEnqueryresponse.pnrNumber)
        }*/

        return view
    }

    private fun setPnrStatusData(view: View, pnrResponse: PnrStatusResponse) {
        view.pnrTv.setText("PNR : "+pnrResponse.pnrEnqueryresponse.pnrNumber)
        view.trainNameTv.setText(pnrResponse.pnrEnqueryresponse.trainName+" ("+pnrResponse.pnrEnqueryresponse.trainNumber+")")
        view.fromStnTv.setText(pnrResponse.pnrEnqueryresponse.sourceStation)
        view.toStnTv.setText(pnrResponse.pnrEnqueryresponse.destinationStation)
        view.durationTv.setText(pnrResponse.pnrEnqueryresponse.journeyClass+" | "+pnrResponse.pnrEnqueryresponse.quota)
        view.boardingStn.setText("Boarding point - "+pnrResponse.pnrEnqueryresponse.boardingPoint+
                "\nDate Of Journey - "+pnrResponse.pnrEnqueryresponse.dateOfJourney)

        view.passengerContainerLin!!.removeAllViews()
        for(list in pnrResponse.pnrEnqueryresponse.passengerinfo.iterator()){
            val child: View = layoutInflater.inflate(R.layout.train_passanger_status, null)
            var sno: TextView =child.findViewById(R.id.snoTv)
            var ageTv: TextView =child.findViewById(R.id.ageTv)
            var bookingStatus: TextView =child.findViewById(R.id.bookingStatusTv)
            var currentStatus: TextView =child.findViewById(R.id.currentStatusTv)
            sno.text= list.passengerSerialNumber+"."
            ageTv.text= list.passengerAge
            bookingStatus.text= list.bookingStatus
            if(list.currentStatus.contains("CNF")){
                currentStatus.text= list.currentStatus+"\n"+list.bookingCoachId+"-"+list.bookingBerthNo
            }else{
                currentStatus.text= list.currentStatus
            }
            view.passengerContainerLin!!.addView(child)
        }
    }

    private fun cancelTicket(pnr: String) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelDetail?TransactionId="
                +pnr,
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

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: PnrStatusResponse, param2: String) =
                TrainPnrStatusDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}