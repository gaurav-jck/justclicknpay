package com.justclick.clicknbook.Fragment.fasttag

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.justclick.clicknbook.ApiConstants
import com.justclick.clicknbook.Fragment.billpay.BillConfirmationFragment
import com.justclick.clicknbook.Fragment.billpay.BillPayFragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.network.NetworkCall
import com.justclick.clicknbook.utils.MyCustomDialog
import okhttp3.ResponseBody

class FasttagRepository {
    val credentialLiveData:MutableLiveData<BillConfirmationFragment.CheckResponseClass>?=MutableLiveData<BillConfirmationFragment.CheckResponseClass>()
    val operatorLiveData:MutableLiveData<BillPayFragment.OperatorResponseModel>?=MutableLiveData<BillPayFragment.OperatorResponseModel>()
    val billLiveData:MutableLiveData<BillPayFragment.FetchBillResponseModel>?=MutableLiveData<BillPayFragment.FetchBillResponseModel>()

    fun checkCredentialsLiveData(): MutableLiveData<BillConfirmationFragment.CheckResponseClass>? {
        return credentialLiveData
    }

    fun getFasttagOperatorLiveData(): MutableLiveData<BillPayFragment.OperatorResponseModel>? {
        return operatorLiveData
    }

    fun fetchBillLiveData(): MutableLiveData<BillPayFragment.FetchBillResponseModel>? {
        return billLiveData
    }

    fun getOperatorList(context: Context?) {
        NetworkCall().callService(NetworkCall.getFastTagApiInterface().fastTagOperator, context,true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                var responseString=response.string()
                val responseData = Gson().fromJson(responseString, BillPayFragment.OperatorResponseModel::class.java)
                operatorLiveData!!.postValue(responseData)
            } else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getBillDetails(context: Context?, billRequest: FasttagFragment.FetchBillRequest) {
        /*NetworkCall().callBillPayService( billRequest, ApiConstants.Fetchbilldetails, context, "", "", true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                MyCustomDialog.hideCustomDialog();
                var responseString=response.string()
                val responseData = Gson().fromJson(responseString, BillPayFragment.FetchBillResponseModel::class.java)
                billLiveData!!.postValue(responseData)
            } else {
                MyCustomDialog.hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }*/
        NetworkCall().callService( NetworkCall.getFastTagApiInterface().getFastTagBill(billRequest), context, true
        ) { response: ResponseBody?, responseCode: Int ->
            if (response != null) {
                MyCustomDialog.hideCustomDialog();
                var responseString=response.string()
                val responseData = Gson().fromJson(responseString, BillPayFragment.FetchBillResponseModel::class.java)
                billLiveData!!.postValue(responseData)
            } else {
                MyCustomDialog.hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}