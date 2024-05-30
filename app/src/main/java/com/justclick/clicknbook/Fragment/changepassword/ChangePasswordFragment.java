package com.justclick.clicknbook.Fragment.changepassword;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.MyLoginActivityNew;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private EditText oldPassEdt, newPassEdt, confirmPassEdt;
    private LoginModel loginModel;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginModel= MyPreferences.getLoginData(new LoginModel(), context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_change_password, container, false);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        oldPassEdt=view.findViewById(R.id.oldPassEdt);
        newPassEdt=view.findViewById(R.id.newPassEdt);
        confirmPassEdt=view.findViewById(R.id.confirmPassEdt);

        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        view.findViewById(R.id.submit_tv).setOnClickListener(this);

        if(context instanceof ChangePasswordActivity){
            view.findViewById(R.id.rel_top).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                if(validate()){
                    submitQuery();
                }
                break;
        }
    }

    private void submitQuery() {
        ChangePasswordRequest request=new ChangePasswordRequest();
        request.oldpassword= oldPassEdt.getText().toString();
        request.newpassword= newPassEdt.getText().toString();
        request.confirmed= confirmPassEdt.getText().toString();
        request.BookUserID= loginModel.Data.UserId;

        String json = new Gson().toJson(request);

        Map<String,String> params = new HashMap<>();
        params.put("oldpassword", request.oldpassword);
        params.put("newpassword", request.newpassword);
        params.put("confirmed", request.confirmed);
        params.put("BookUserID", request.BookUserID);
        params.put("MerchantId", request.MerchantId);

        new NetworkCall().callServiceWithError(NetworkCall.getPayoutNewApiInterface().changePassword(ApiConstants.otpChangePassword, params),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, responseCode);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response, int responseCode) {
        try {
            ChangePasswordResponse senderResponse = new Gson().fromJson(response.string(),
                    ChangePasswordResponse.class);
            if(senderResponse!=null && (responseCode==401 || responseCode==400)){
                Common.showResponsePopUp(context, senderResponse.errors.newpassword[1]);
                return;
            }
            if(senderResponse!=null){
                if(senderResponse.status.equals("00")) {
//                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
                    otpDialog(senderResponse.description);
                }else if(senderResponse.status!=null){
//                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
                    Common.showResponsePopUp(context, senderResponse.description);
                }else {
                    Toast.makeText(context,"Error in change password, please try after some time.",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void otpDialog(String description){
        Dialog otpDialog = new Dialog(context);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.change_pass_otp_layout);
        otpDialog.setCancelable(false);
        final EditText otpEdt1= otpDialog.findViewById(R.id.otpEdt1);
        final TextView otpErrorTv= otpDialog.findViewById(R.id.otpErrorTv);
        final Button submit= (Button) otpDialog.findViewById(R.id.submit_btn);
        ImageButton dialogCloseButton = (ImageButton) otpDialog.findViewById(R.id.close_btn);

        otpErrorTv.setText(description);
        otpEdt1.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submit);
                Common.hideSoftKeyboard(requireActivity());

                String otp=otpEdt1.getText().toString().trim();

                if(Common.checkInternetConnection(context)){
                    if(otp.length()>5) {
//                        otpErrorTv.setVisibility(View.INVISIBLE);
                        changePassword(otp, otpDialog);
                    }else {
                        Toast.makeText(context,R.string.empty_and_invalid_otp,Toast.LENGTH_LONG).show();
//                        otpErrorTv.setVisibility(View.VISIBLE);
                    }
//                    forgetDialog.dismiss();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                otpDialog.dismiss();
            }
        });
        otpDialog.show();
    }

    private void changePassword(String otp, Dialog otpDialog) {
        ChangePasswordRequest request=new ChangePasswordRequest();
        request.oldpassword= oldPassEdt.getText().toString();
        request.newpassword= newPassEdt.getText().toString();
        request.confirmed= confirmPassEdt.getText().toString();
        request.BookUserID= loginModel.Data.UserId;
        request.OTP= otp;

        String json = new Gson().toJson(request);

        Map<String,String> params = new HashMap<>();
        params.put("oldpassword", request.oldpassword);
        params.put("newpassword", request.newpassword);
        params.put("confirmed", request.confirmed);
        params.put("BookUserID", request.BookUserID);
        params.put("MerchantId", request.MerchantId);
        params.put("OTP", request.OTP);

        new NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().changePassword(ApiConstants.ChangePassword, params),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        otpDialog.dismiss();
                        responseHandlerPassword(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerPassword(ResponseBody response, int i) {
        try {
            ChangePasswordResponse senderResponse = new Gson().fromJson(response.string(),
                    ChangePasswordResponse.class);
            if(senderResponse!=null){
                if(senderResponse.status.equals("00")) {
                    showResponsePopUp(senderResponse.description);
                }else if(senderResponse.status!=null){
                    Toast.makeText(context,senderResponse.description,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"Error in change password, please try after some time.",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showResponsePopUp(String message) {
        Dialog responseDialog = new Dialog(requireContext());
        responseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        responseDialog.setContentView(R.layout.mobile_alert_dialog_layout);
        responseDialog.setCancelable(false);
        TextView textView=responseDialog.findViewById(R.id.detail_tv);
        textView.setText(message);
        responseDialog.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseDialog.dismiss();
                MyPreferences.logoutUser(context);
                context.startActivity(new Intent(context, MyLoginActivityNew.class));
                requireActivity().finish();
            }
        });
        responseDialog.show();
    }


    private void resetData() {
        oldPassEdt.setText("");
        newPassEdt.setText("");
    }

    private boolean validate() {
        if(oldPassEdt.getText().toString().trim().isEmpty()){
           Toast.makeText(context, "Please enter old password", Toast.LENGTH_SHORT).show();
           return false;
        }else if(newPassEdt.getText().toString().trim().isEmpty()){
            Toast.makeText(context, "Please enter new password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(oldPassEdt.getText().toString().trim().equals(newPassEdt.getText().toString().trim())){
            Toast.makeText(context, "You can not use same password as used earlier", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(confirmPassEdt.getText().toString().trim().isEmpty()){
            Toast.makeText(context, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!confirmPassEdt.getText().toString().trim().equals(newPassEdt.getText().toString().trim())){
            Toast.makeText(context, "New password you entered does not matched with confirm password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}