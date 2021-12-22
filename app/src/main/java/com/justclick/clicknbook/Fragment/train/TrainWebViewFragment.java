package com.justclick.clicknbook.Fragment.train;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.train.model.PnrResponse;
import com.justclick.clicknbook.Fragment.train.model.TrainBookingResponse;
import com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse;
import com.justclick.clicknbook.Fragment.train.model.TrainStationModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.JctMoneyTransactionResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import org.bouncycastle.util.encoders.Base64Encoder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import okhttp3.ResponseBody;

public class TrainWebViewFragment extends Fragment {

    String returnUrl, txnId, agent, userType, duration;
    Context context;
    private LoginModel loginModel;
    TrainPreBookResponse trainPreBookResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context=getActivity();
        super.onCreate(savedInstanceState);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_train_webview, container, false);
        WebView webView=view.findViewById(R.id.webview);

        MyWebViewClient client=new MyWebViewClient();
        webView.setWebViewClient(client);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        TrainBookingResponse.railBookDetail bookingResponse=null;
        if(getArguments()!=null){
            bookingResponse= (TrainBookingResponse.railBookDetail) getArguments().getSerializable("BookingData");
            trainPreBookResponse= (TrainPreBookResponse) getArguments().getSerializable("TrainPreBookResponse");
            String url = bookingResponse.trainUrl;
            returnUrl = bookingResponse.wsReturnUrl;
            txnId = bookingResponse.wsTxnId;
            agent = bookingResponse.agent;
            userType = bookingResponse.userType;
            duration = bookingResponse.duration;
//            String url = "https://www.agent.irctc.co.in/eticketing/wsapplogin";
            String postData = "wsloginId="+bookingResponse.wsloginId+"&wsTxnId="+bookingResponse.wsTxnId
                    +"&wsReturnUrl="+bookingResponse.wsReturnUrl;
            byte[] data = postData.getBytes(StandardCharsets.UTF_8);
//            String base64 = Base64.getEncoder().encodeToString(data);
            webView.postUrl(url, data);
//            responseHandler(null, 1);
        }


        return view;
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//https://justclicknpay.com/Rail/RailBookingConfirmation.aspx?Bookdetail=UjA2MTIxTVU5SEpDMEExMzM4N34yNTYwMzU0NDA0
//            Toast.makeText(context, "Url= "+url, Toast.LENGTH_SHORT).show();
//            https://justclicknpay.com/Rail/RailReturnPageNew.aspx?CancelButton=Y
            String responseUrl="https://justclicknpay.com/Rail/RailBookingConfirmation.aspx";
            if(url.contains("CancelButton=Y")){
                getParentFragmentManager().popBackStack();
            } else if(url.contains(responseUrl)){
//                Toast.makeText(context, "Booking Status called \n"+url, Toast.LENGTH_LONG).show();
                getBookingData();
            }else {
                view.loadUrl(url);
            }
            return true;
        }
    }

    private void getBookingData() {

        new NetworkCall().callTrainServiceFinalGet(ApiConstants.GetPnrDetails, txnId,"","",
                context, agent, userType,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response, int i) {
        try {
            if(response!=null){
                PnrResponse responseModel = new Gson().fromJson(response.string(), PnrResponse.class);
                if(responseModel!=null){
                    if(responseModel.statusCode.equals("00")){
//                        Toast.makeText(context, responseModel.statusMessage+"\nPnr="+responseModel.pnr, Toast.LENGTH_LONG).show();
                        Bundle bundle=new Bundle();
                        responseModel.transactionDetails.get(0).duration=duration;
                        bundle.putSerializable("trainResponse", responseModel);
                        bundle.putSerializable("TrainPreBookResponse", trainPreBookResponse);
                        TrainBookingResponseFragment fragment=new TrainBookingResponseFragment();
                        fragment.setArguments(bundle);
                        ((NavigationDrawerActivity)context).replaceFragment(fragment);
                    }else {
                        Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String responseString="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"Sucess\",\n" +
            "    \"reservationid\": \"R04121NO0YJC0A37520\",\n" +
            "    \"pnr\": \"6822773087\",\n" +
            "    \"transactionDetails\": [\n" +
            "        {\n" +
            "            \"pnrNo\": \"6822773087\",\n" +
            "            \"transactionID\": \"100003075562381\",\n" +
            "            \"from\": \"DGA\",\n" +
            "            \"boarding\": \" CPR\",\n" +
            "            \"resvUpto\": \"NK\",\n" +
            "            \"passengerMobileNo\": \"9970984362\",\n" +
            "            \"passengerAddress\": \"Nashik^Nashik^MAHARASHTRA^422003\",\n" +
            "            \"trainName\": \"JANSADHARAN EXP\",\n" +
            "            \"dateTimeOfBooking\": \"12/4/2021 2:17:05 PM\",\n" +
            "            \"dateOfJourney\": \"11/12/2021 10:55:00 PM\",\n" +
            "            \"dateOfBoarding\": \"11/12/2021 10:55:00 PM\",\n" +
            "            \"scheduledArrival\": \"13/12/2021 01:47:00 AM\",\n" +
            "            \"distance\": \"1536\",\n" +
            "            \"quota\": \"GN\",\n" +
            "            \"class\": \"2S\",\n" +
            "            \"to\": \"NK\",\n" +
            "            \"scheduledDeparture\": \"11/12/2021 10:55:00 PM\",\n" +
            "            \"adult\": \"6\",\n" +
            "            \"child\": \"0\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"passengerDetails\": [\n" +
            "        {\n" +
            "            \"sNo\": \"1\",\n" +
            "            \"name\": \"anita devi\",\n" +
            "            \"age\": \"39\",\n" +
            "            \"sex\": \"F\",\n" +
            "            \"bookingStatus\": \"WL-174\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sNo\": \"2\",\n" +
            "            \"name\": \"sanjay kumar\",\n" +
            "            \"age\": \"41\",\n" +
            "            \"sex\": \"M\",\n" +
            "            \"bookingStatus\": \"WL-175\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sNo\": \"3\",\n" +
            "            \"name\": \"payal kumari\",\n" +
            "            \"age\": \"15\",\n" +
            "            \"sex\": \"F\",\n" +
            "            \"bookingStatus\": \"WL-176\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sNo\": \"4\",\n" +
            "            \"name\": \"aadarsh kumar\",\n" +
            "            \"age\": \"12\",\n" +
            "            \"sex\": \"M\",\n" +
            "            \"bookingStatus\": \"WL-177\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sNo\": \"5\",\n" +
            "            \"name\": \"santosh kumar\",\n" +
            "            \"age\": \"33\",\n" +
            "            \"sex\": \"M\",\n" +
            "            \"bookingStatus\": \"WL-178\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"sNo\": \"6\",\n" +
            "            \"name\": \"sarvesh kumar\",\n" +
            "            \"age\": \"22\",\n" +
            "            \"sex\": \"M\",\n" +
            "            \"bookingStatus\": \"WL-179\",\n" +
            "            \"currentStatus\": \"WL\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"agentDetails\": [\n" +
            "        {\n" +
            "            \"principleAgent\": \"MATRIX CELLULAR INTERNATIONAL SERVICES LIMITED\",\n" +
            "            \"agentName\": \"JHIR ABBAS AHMAD KHAN\",\n" +
            "            \"address\": \"ROYAL HINGHNESS APP. TAKALI ROAD FT.NO 5 GANGHI NAGAR NASHIK\\tNASHIK\\tGANDHI NAGAR NASHIK\\tMAHARASHTRA\\t422006\",\n" +
            "            \"corporateName\": \"Jc\",\n" +
            "            \"contactNumber\": \"9890138532\",\n" +
            "            \"emailID\": \"dhanubhai7866@gmail.com\",\n" +
            "            \"ticketPrintingTime\": \"\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"fareDetails\": [\n" +
            "        {\n" +
            "            \"ticketFar\": \"2670.00\",\n" +
            "            \"cateringCharge\": \"0.00\",\n" +
            "            \"convenienceFee\": \"17.70\",\n" +
            "            \"travelInsurancePremium\": \"0.00\",\n" +
            "            \"travelAgentServiceCharge\": \"20.00\",\n" +
            "            \"pgCharges\": \"22.78\",\n" +
            "            \"totalFare\": \"2734.57\"\n" +
            "        }\n" +
            "    ]\n" +
            "}\n" +
            "\n";


}