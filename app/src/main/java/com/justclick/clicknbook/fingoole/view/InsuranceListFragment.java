package com.justclick.clicknbook.fingoole.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.JctMoneyTransactionListAdapter;
import com.justclick.clicknbook.fingoole.model.TxnListRequestModel;
import com.justclick.clicknbook.fingoole.model.TxnListResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;

public class InsuranceListFragment extends Fragment {
    private Context context;
    private ArrayList<TxnListResponseModel> list;
    private RecyclerView insuranceListRv;
    private String fromDate="", toDate="";
    private SimpleDateFormat dateServerFormat;
    private int checkInDateDay, checkInDateMonth, checkInDateYear;
    private Calendar fromDateCalender, toDateCalender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insurance_list, container, false);
        insuranceListRv=view.findViewById(R.id.insuranceListRv);

        initializeDates();
        getInsuranceList();

        view.findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initializeDates() {
        //Date formats
        dateServerFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateCalender= Calendar.getInstance();
        fromDateCalender.add(Calendar.DAY_OF_MONTH,-1);
        fromDate= dateServerFormat.format(fromDateCalender.getTime());

        checkInDateDay = fromDateCalender.get(Calendar.DAY_OF_MONTH);
        checkInDateMonth = fromDateCalender.get(Calendar.MONTH);
        checkInDateYear = fromDateCalender.get(Calendar.YEAR);

        toDateCalender= Calendar.getInstance();
        toDate= dateServerFormat.format(toDateCalender.getTime());

    }

    private void getInsuranceList() {
        LoginModel loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        TxnListRequestModel txnListRequestModel=new TxnListRequestModel();
//        txnListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        txnListRequestModel.setFromDate(fromDate);
        txnListRequestModel.setToDate(toDate);
        Toast.makeText(context, fromDate+" to "+toDate, Toast.LENGTH_LONG).show();

        new NetworkCall().callInsuranceService(txnListRequestModel, ApiConstants.InsTransactionList, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, 1);    //http://uatmb.justclicknpay.com/API/Insurence/InsTransactionList
                        }else {      //{"FromDate":"2020-12-29","ToDate":"2020-12-30","DoneCardUser":"JC0A13387","MerchantID":"JUSTCLICKTRAVELS","Mode":"App"}   Response{protocol=http/1.1, code=500, message=Internal Server Error, url=http://uatmb.justclicknpay.com/API/Insurence/InsTransactionList}
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            TxnListResponseModel senderResponse = new Gson().fromJson(response.string(), TxnListResponseModel.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode()==0) {
                    Toast.makeText(context,senderResponse.getStausMessage(),Toast.LENGTH_SHORT).show();
                    if(senderResponse.getInsTransactionsList()!=null && senderResponse.getInsTransactionsList().size()>0){
                        setList(senderResponse.getInsTransactionsList());
                    }else {
                        Toast.makeText(context, "No booking.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context,"Status code="+senderResponse.getStatusCode()+
                            "\nMessage="+senderResponse.getStausMessage(),Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setList(ArrayList<TxnListResponseModel.InsTransactions> insTransactionsList) {
        InsuranceListAdapter listAdapter =new InsuranceListAdapter(context, new  InsuranceListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<TxnListResponseModel.InsTransactions> list,TxnListResponseModel data, int position) {

            }
        }, insTransactionsList);
        insuranceListRv.setLayoutManager(new LinearLayoutManager(context));
        insuranceListRv.setAdapter(listAdapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
