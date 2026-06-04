package com.justclick.clicknbook.Fragment.jctmoney.instapay;

import  android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.GetSenderInstaRequest;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.MerchantKycCheckResponse;
import com.justclick.clicknbook.Fragment.jctmoney.request.CheckCredentialRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import okhttp3.ResponseBody;

public class InstaGetSenderFragment extends Fragment implements View.OnClickListener {
    private final String ARG_PARAM1 = "param1";
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    TextView get_tv;
    EditText number_edt;
    private LoginModel loginModel;
    private CheckCredentialResponse.credentialData credentialResponse;
    private boolean isCheckCredential =false;
    private String bankName;
    private View mView;
    private TextWatcher textWatcher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if(getArguments()!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                credentialResponse =getArguments().getSerializable("credentialResponse", CheckCredentialResponse.credentialData.class);
            }else{
                credentialResponse = (CheckCredentialResponse.credentialData) getArguments().getSerializable("credentialResponse");
            }
            bankName =getArguments().getString("BankName");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_jct_money_get_sender, container, false);
            toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
            getIpAddress();
//            Common.showCommonAlertDialog(context, ip, "IP address");
            initializeViews(mView);
//            tpinDialog();
        }
        return mView;
    }

    private void initializeViews(View view) {
        get_tv = view.findViewById(R.id.get_tv);
        number_edt =  view.findViewById(R.id.number_edt);
        get_tv.setOnClickListener(this);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        get_tv.setTypeface(face);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

        textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length()==10){
                    getClicked();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

//        number_edt.addTextChangedListener(textWatcher);
    }


    private void getSenderDetail() {
        if(validate())
        {
            GetSenderInstaRequest senderRequestModel=new GetSenderInstaRequest();
            senderRequestModel.Mobile=number_edt.getText().toString();
            senderRequestModel.AgentCode=loginModel.Data.DoneCardUser;
            senderRequestModel.Latitude="19.1641988";
            senderRequestModel.Longitude="72.8626135";
            senderRequestModel.BankName=bankName;
            senderRequestModel.IPAddress=ip;
            new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.SenderDetail,
                            senderRequestModel, credentialResponse.getUserData(), "Bearer "+credentialResponse.getToken()),
                    context,true,
                    (response, responseCode) -> {
                        if(response!=null){
                            responseHandler(response, 1);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("senderResponse", senderResponse);
                    credentialResponse.setSessionKey(senderResponse.getSessionKey());
                    bundle.putSerializable("commonParams", credentialResponse);
                    bundle.putString("bankName", bankName);
                    InstaSenderDetailFragment senderDetailFragment=new InstaSenderDetailFragment();
                    senderDetailFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithTag(senderDetailFragment, FragmentTags.InstaSenderDetailFragment);
                }else if(senderResponse.getStatusCode().equals("02")){
//                    add sender
                    remitterAlert(senderResponse);
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }else {
                    Common.showResponsePopUp(requireContext(), senderResponse.getStatusMessage());
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
    }

    private void remitterAlert(SenderDetailResponse senderResponse) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Api response");
        builder.setMessage("Remitter not found for this mobile number, please add remitter.");
        builder.setCancelable(false);

        // add a button
        builder.setPositiveButton("Add Remitter", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            Bundle bundle=new Bundle();
            bundle.putSerializable("senderResponse", senderResponse);
            bundle.putSerializable("commonParams", credentialResponse);
            bundle.putString("mobile", number_edt.getText().toString());
            bundle.putString("bankName", bankName);
            InstaRemitterAdharVerifyFragment senderDetailFragment=new InstaRemitterAdharVerifyFragment();
            senderDetailFragment.setArguments(bundle);
            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(senderDetailFragment);
            dialog.dismiss();
        });
        // add a button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            dialog.dismiss();
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;

            case R.id.get_tv:
                getClicked();
                break;
        }
    }

    private void getClicked() {
        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
        Common.preventFrequentClick(get_tv);
        if(Common.checkInternetConnection(context)) {
            getSenderDetail();
        }else {
            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean validate() {

        if (number_edt.getText().toString().length() < 10)
        {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    String ip;
    void getIpAddress(){
        requireActivity().runOnUiThread(() -> {
            try {
                URL url = new URL("https://api.ipify.org");
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0/Chrome"); // Set a User-Agent to avoid HTTP 403 Forbidden error
                InputStream inputStream = connection.getInputStream();
                Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                ip = s.next();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                ip = "103.139.75.200";
            }
            // Update UI elements here
        });
    }

    private void runThread() {

        new Thread() {
            int i=0;
            public void run() {
                while (i++ < 1000) {
                    try {
                        requireActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
//                                btn.setText("#" + i);
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    public void onResume() {
        super.onResume();
        number_edt.addTextChangedListener(textWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        number_edt.removeTextChangedListener(textWatcher);
    }
}

