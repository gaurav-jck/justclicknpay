package com.justclick.clicknbook.Fragment.bus;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusCancelResponse;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusPrintResponse;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusCancelRequestModel;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusPrintRequestModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.BusTransactionListResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;

/**
 * Created by priyanshi on 11/22/2017.
 */

public class BusReservationFragment extends Fragment implements View.OnClickListener {
    Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ScrollView scrollView;
    private LinearLayout printLayout,pdfLinear;
    private ImageView backArrow;
    private TextView pnrNumberTv,agencyTv,passengerTv,sourceTv,destinationTv,
            boardingTv,journeyTv,operatorTv,transactionIdTv,transactionTv,transactionDateTv,
            paymentTv,executiveTv,nameTv,executiveMobileTv,pnrTv,cancelTv;
    private BusTransactionListResponseModel.BusListModel busTransactionListResponseModel;
    private LoginModel loginModel;
    private ArrayList<BusTransactionListResponseModel.BusListModel> refundArrayList;
    private int totalPageCount=0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_bus_reservation, container, false);
        pdfLinear= view.findViewById(R.id.pdfLinear);
        printLayout=  view.findViewById(R.id.printLayout);
        pnrNumberTv=  view.findViewById(R.id.pnrNumberTv);
        agencyTv=  view.findViewById(R.id.agencyTv);
        passengerTv=  view.findViewById(R.id.passengerTv);
        sourceTv=  view.findViewById(R.id.sourceTv);
        destinationTv=  view.findViewById(R.id.destinationTv);
        boardingTv=  view.findViewById(R.id.boardingTv);
        journeyTv=  view.findViewById(R.id.journeyTv);
        operatorTv=  view.findViewById(R.id.operatorTv);
        transactionIdTv=  view.findViewById(R.id.transactionIdTv);
        transactionTv=  view.findViewById(R.id.transactionTv);
        transactionDateTv=  view.findViewById(R.id.transactionDateTv);
        paymentTv=  view.findViewById(R.id.paymentTv);
        executiveTv=  view.findViewById(R.id.executiveTv);
        nameTv=  view.findViewById(R.id.nameTv);
        executiveMobileTv=  view.findViewById(R.id.executiveMobileTv);
        pnrTv=  view.findViewById(R.id.pnrTv);
        cancelTv=  view.findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(this);
        printLayout.setOnClickListener(this);
        backArrow=  view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        pdfLinear.setOnClickListener(this);

        if(getArguments().getSerializable("model")!=null) {
            busTransactionListResponseModel = (BusTransactionListResponseModel.BusListModel) getArguments().getSerializable("model");
            pnrNumberTv.setText(busTransactionListResponseModel.PNR);
            pnrTv.setText(busTransactionListResponseModel.PNR);
            agencyTv.setText(busTransactionListResponseModel.BusOpertor);

            operatorTv.setText(busTransactionListResponseModel.BusOpertor);
            transactionIdTv.setText(busTransactionListResponseModel.ReservationID);
            transactionTv.setText(busTransactionListResponseModel.Status);
            transactionDateTv.setText(busTransactionListResponseModel.JourrnyDate);
            paymentTv.setText("");
            executiveTv.setText("");
            nameTv.setText("");
            executiveMobileTv.setText(busTransactionListResponseModel.LeadMobile);
            boardingTv.setText(busTransactionListResponseModel.JourrnyDate);
            journeyTv.setText(busTransactionListResponseModel.JourrnyDate);
            sourceTv.setText(busTransactionListResponseModel.FromStation);
            destinationTv.setText(busTransactionListResponseModel.ToStation);
        }

//        scrollView= (ScrollView) view.findViewById(R.id.scrollView);
//
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
////                if(scrollView.getY()==scrollView.getHeight()){
////                    Toast.makeText(context, "Bottom1", Toast.LENGTH_SHORT).show();
////                }
////                if((scrollView.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()))==0){
////                    Toast.makeText(context, "Bottom2", Toast.LENGTH_SHORT).show();
////                }
//            }
//        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
           case  R.id.printLayout:
               try {
                   BusPrintRequestModel requestModel = new BusPrintRequestModel();
                   requestModel.DoneCardUser = loginModel.Data.DoneCardUser;
                   requestModel.DeviceId = Common.getDeviceId(context);
                   requestModel.LoginSessionId = EncryptionDecryptionClass.EncryptSessionId(
                           EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                   requestModel.ReservationID = busTransactionListResponseModel.ReservationID;
                   requestModel.TicketNo = "";

                   if (Common.checkInternetConnection(context)) {
                       print(requestModel);
                   } else {
                       Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                   }
               }catch (Exception e){
               }

                   break;
            case R.id.cancelTv:
                BusCancelRequestModel requestModel=new BusCancelRequestModel();
                requestModel.DoneCardUser = loginModel.Data.DoneCardUser;
                requestModel.DeviceId = Common.getDeviceId(context);
                requestModel.LoginSessionId =EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                requestModel.RsID =busTransactionListResponseModel.ReservationID;
                requestModel.seatsToCancel=new String[]{busTransactionListResponseModel.PNR};
                requestModel.tin = "";
                cancel(requestModel);
                break;
//            case R.id.pdfLinear:
//                try {
//                    BusPrintRequestModel requestModel1 = new BusPrintRequestModel();
//                    requestModel1.DoneCardUser = loginModel.Data.DoneCardUser;
//                    requestModel1.DeviceId = Common.getDeviceId(context);
//                    requestModel1.LoginSessionId = EncryptionDecryptionClass.Encryption(
//                            EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
//                    requestModel1.ReservationID = busTransactionListResponseModel.ReservationID;
//                    requestModel1.TicketNo = "";
//
//                    if (Common.checkInternetConnection(context)) {
//                        print(requestModel1);
//                    } else {
//                        Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
//                    }
//                }catch (Exception e){
//                }
//                break;
    }
}
    private void cancel(BusCancelRequestModel requestModel) {
        showCustomDialog();
        new NetworkCall().callBusService(requestModel, ApiConstants.CancelBooking, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showCustomDialog() {
            MyCustomDialog.showCustomDialog(context,"Please wait");
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    public void print(BusPrintRequestModel printRequestModel) {
        showCustomDialog();
        new NetworkCall().callBusService(printRequestModel, ApiConstants.TicketInfo, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerPrint(response);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response) {

                try {
                    hideCustomDialog();
                    BusCancelResponse commonResponse = new Gson().fromJson(response.string(), BusCancelResponse.class);
                    if (commonResponse != null) {
                        if(commonResponse.Status==1){
                            Toast.makeText(context,commonResponse.Description,Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(context,commonResponse.Description, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

    }

    private void responseHandlerPrint(ResponseBody response) {
        try {
            hideCustomDialog();
            BusPrintResponse commonResponse = new Gson().fromJson(response.string(), BusPrintResponse.class);
            if (commonResponse != null) {

                Bundle bundle=new Bundle();
                bundle.putSerializable("model",commonResponse);
                BusPrintFragment fragment=new BusPrintFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
//                if(commonResponse.Status.equalsIgnoreCase("0")){
//                    Toast.makeText(context,commonResponse.Status,Toast.LENGTH_SHORT).show();
//
//                }else {

//                    Toast.makeText(context,commonResponse.BPoint, Toast.LENGTH_LONG).show();
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
