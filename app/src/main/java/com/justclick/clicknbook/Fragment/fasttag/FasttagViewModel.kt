package com.justclick.clicknbook.Fragment.fasttag

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.justclick.clicknbook.Fragment.billpay.BillConfirmationFragment
import com.justclick.clicknbook.Fragment.billpay.BillPayFragment

class FasttagViewModel : ViewModel() {

    private var fasttagRepository:FasttagRepository?=null
    private var fasttagOperatorListLiveData:MutableLiveData<BillPayFragment.OperatorResponseModel>?=null
    private var fetchBillLiveData:MutableLiveData<BillPayFragment.FetchBillResponseModel>?=null
    private var fasttagCredentialLiveData:MutableLiveData<BillConfirmationFragment.CheckResponseClass>?=null

    init {
        fasttagRepository=FasttagRepository()
        fasttagCredentialLiveData=fasttagRepository!!.checkCredentialsLiveData()
        fasttagOperatorListLiveData=fasttagRepository!!.getFasttagOperatorLiveData()
        fetchBillLiveData=fasttagRepository!!.fetchBillLiveData()
    }

    fun getOperatorList(context: Context?) {
        fasttagRepository!!.getOperatorList(context)
    }

    fun getOperatorLiveData():MutableLiveData<BillPayFragment.OperatorResponseModel>?{
        return fasttagOperatorListLiveData
    }

    fun fetchBillDetails(context: Context?, billRequest:FasttagFragment.FetchBillRequest) {
        fasttagRepository!!.getBillDetails(context, billRequest)
    }

    fun fetchBillLiveData():MutableLiveData<BillPayFragment.FetchBillResponseModel>?{
        return fetchBillLiveData
    }
}