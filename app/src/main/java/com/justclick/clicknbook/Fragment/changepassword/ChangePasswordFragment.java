package com.justclick.clicknbook.Fragment.changepassword;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.SupportQueryRequest;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private EditText oldPassEdt, newPassEdt, confirmPassEdt;
    private LoginModel loginModel;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
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

        new NetworkCall().callService(NetworkCall.getAccountStmtApiInterface().getSupportPost(ApiConstants.otpChangePassword, request),
                context,true,
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
            PaytmWalletFragment.CommonResponse senderResponse = new Gson().fromJson(response.string(),
                    PaytmWalletFragment.CommonResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    resetData();
                }else if(senderResponse.getStatusMessage()!=null){
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
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
        else if(confirmPassEdt.getText().toString().trim().isEmpty()){
            Toast.makeText(context, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(confirmPassEdt.getText().toString().trim().equals(newPassEdt.getText().toString().trim())){
            Toast.makeText(context, "New password you entered does not matched with confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}