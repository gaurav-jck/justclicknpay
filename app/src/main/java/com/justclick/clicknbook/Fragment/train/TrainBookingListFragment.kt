package com.justclick.clicknbook.Fragment.train

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.justclick.clicknbook.Activity.NavigationDrawerActivity
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.train.adapter.TrainBookingListAdapter
import com.justclick.clicknbook.Fragment.train.model.PnrResponse
import com.justclick.clicknbook.Fragment.train.model.TrainBookingListResponseModel
import com.justclick.clicknbook.Fragment.train.model.TrainCancelTicketDetailResponse
import com.justclick.clicknbook.Fragment.train.model.TrainSearchDataModel
import com.justclick.clicknbook.R
import com.justclick.clicknbook.databinding.FragmentTrainBookingListBinding
import com.justclick.clicknbook.model.LoginModel
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener
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
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TrainBookingListFragment : Fragment(), View.OnClickListener {

    var toolBarHideFromFragmentListener:ToolBarHideFromFragmentListener?=null
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    var adapter: TrainBookingListAdapter?=null
    var loginModel: LoginModel?=null
    var arrayList:ArrayList<TrainBookingListResponseModel.reservationlist>?=null
    var fragView:View?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginModel = LoginModel()
        loginModel = MyPreferences.getLoginData(loginModel, requireContext())
        toolBarHideFromFragmentListener=requireContext() as ToolBarHideFromFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(fragView==null){
            val view = inflater.inflate(R.layout.fragment_train_booking_list, container, false)
            var binding=FragmentTrainBookingListBinding.bind(view)
            fragView=view
            toolBarHideFromFragmentListener!!.onToolBarHideFromFragment(true)
            arrayList= ArrayList()

            var layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(requireContext())
                else -> GridLayoutManager(requireContext(), columnCount)
            }

            adapter = TrainBookingListAdapter(arrayList, object : OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(item: ArrayList<TrainBookingListResponseModel.reservationlist>?, id: Int, listPosition: Int) {
                    if(id==R.id.changeBoarding){
                        changeBoardingStn(item!!.get(listPosition))
                    }else if(id==R.id.cancelTicket){
                        cancelTicket(item!!.get(listPosition))
                    }else{
                        getBookingData(item!!.get(listPosition).reservationID)
                    }
                }
            })

            binding.recycleView.layoutManager=layoutManager
            binding.recycleView.adapter=adapter

            binding.backArrow.setOnClickListener(this)

            callBookingList()
        }

        return fragView
    }

    private fun changeBoardingStn(list: TrainBookingListResponseModel.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getBoardingStnForChange(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/BoardingStation?Trainno="
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
                        if(boardingStnResponse!=null){
                            if(boardingStnResponse.boardingStationList!=null && boardingStnResponse.boardingStationList!!.size>0){

                                var arr: Array<String?> =arrayOfNulls<String>(boardingStnResponse.boardingStationList!!.size)
                                for(pos in boardingStnResponse.boardingStationList!!.indices){
                                    arr[pos]=boardingStnResponse.boardingStationList!!.get(pos).stnNameCode
                                }

//                                val pnrDetailFragment= TrainChangeBoardingStnFragment.newInstance(arr!!,list)
//                                (requireContext() as NavigationDrawerActivity?)!!.replaceFragmentWithBackStack(pnrDetailFragment)

                            }else{
                                Toast.makeText(requireContext(), boardingStnResponse.errorMessage, Toast.LENGTH_LONG).show()
                            }
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

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cancelTicket(list: TrainBookingListResponseModel.reservationlist) {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        val call = apiService.getCancelTicketDetail(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetCancelDetail?TransactionId="
                +list!!.reservationID,
                loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null ) {
                        var responseString=response!!.body()!!.string()
                        val response = Gson().fromJson(responseString, TrainCancelTicketDetailResponse::class.java)
                        if(response.statusCode.equals("00")){
//                            Toast.makeText(requireContext(),response.statusMessage, Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putSerializable("cancelResponse", response)
                            val fragment = TrainCancelDetailsFragment()
                            fragment.arguments = bundle
                            (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                        }else{
                            Toast.makeText(requireContext(),response.statusMessage, Toast.LENGTH_LONG).show()
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

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getStnCode(station: String?): String? {
        try {
            return station!!.substring(station.indexOf("(")+1, station.indexOf(")"))
        }catch (e: Exception){}
        return ""
    }

    private fun journeyDate(departDate: String?): String? {
        if(departDate!!.contains("/")){
            return Common.getServerDateFormat().format(SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(departDate))
        }else{
            return Common.getServerDateFormat().format(SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(departDate))
        }
    }

    private fun callBookingList() {
        showCustomDialog()
        val apiService = APIClient.getClient(ApiConstants.BASE_URL_TRAIN).create(ApiInterface::class.java)
        var userId=""
        if(loginModel!!.Data.UserType.equals("A")){
            userId=loginModel!!.Data.DoneCardUser
        }else{
            userId=loginModel!!.Data.DoneCardUser
//            userId=loginModel!!.Data.UserId
        }
        val call = apiService.getTrainBookingList(ApiConstants.BASE_URL_TRAIN+"apiV1/RailEngine/GetTopDetails",
                userId, loginModel!!.Data.UserType, ApiConstants.MerchantId, "App")
//                "JC0A30527", "A", ApiConstants.MerchantId, "App")   //JC0O188   "JC0A30527"
        call.enqueue(object : Callback<TrainBookingListResponseModel?> {
            override fun onResponse(call: Call<TrainBookingListResponseModel?>, response: Response<TrainBookingListResponseModel?>) {
                try {
                    hideCustomDialog()
                    if (response != null && response.body() != null) {
                        if(response.body()!!.reservationlist!=null && response.body()!!.reservationlist!!.size>0){
                            arrayList!!.addAll(response.body()!!.reservationlist)
                            adapter!!.notifyDataSetChanged()
//                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(requireContext(), response.body()!!.statusMessage, Toast.LENGTH_SHORT).show()
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

            override fun onFailure(call: Call<TrainBookingListResponseModel?>, t: Throwable) {
                hideCustomDialog()
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getBookingData(reservationID: String) {
        NetworkCall().callTrainServiceFinalGet(ApiConstants.GetPnrDetails, reservationID, "", "",
                requireContext(), loginModel!!.Data.DoneCardUser, loginModel!!.Data.UserType, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                responseHandler(response, 1)
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun responseHandler(response: ResponseBody?, i: Int) {
        try {
            if (response != null) {
                val responseModel: PnrResponse = Gson().fromJson<PnrResponse>(response.string(), PnrResponse::class.java)
                if (responseModel != null) {
                    if (responseModel.statusCode == "00") {
                        val bundle = Bundle()
                        bundle.putSerializable("trainResponse", responseModel)
                        val fragment = TrainBookingDetailsFragment()
                        fragment.arguments = bundle
                        (requireContext() as NavigationDrawerActivity).replaceFragmentWithBackStack(fragment)
                    } else {
                        Toast.makeText(requireContext(), responseModel.statusMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ArrayList<TrainBookingListResponseModel.reservationlist>?, id: Int, listPosition: Int)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_arrow->
                parentFragmentManager.popBackStack()
        }
    }

    private fun showCustomDialog() {
        MyCustomDialog.showCustomDialog(requireContext(), resources.getString(R.string.please_wait))
    }

    private fun hideCustomDialog() {
        MyCustomDialog.hideCustomDialog()
    }

}
