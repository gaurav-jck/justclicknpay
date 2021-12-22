package com.justclick.clicknbook.Fragment.jctmoney;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.utils.Common;

public class OtpFragment extends Fragment implements View.OnClickListener{

    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private OnFragmentInteractionListener mListener;
    private TextView otpDetailTv, resendTv, sendOtpTv;
    private EditText optEdt;
    private static OtpFragment fragment=null;

    public OtpFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OtpFragment newInstance(String param1, String param2) {
        if(fragment==null) {
            fragment = new OtpFragment();
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_otp, container, false);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
        optEdt=  view.findViewById(R.id.otpEdt);
        otpDetailTv=  view.findViewById(R.id.otpDetailTv);

        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        view.findViewById(R.id.sendOtpTv).setOnClickListener(this);
        view.findViewById(R.id.resendOtpTv).setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.sendOtpTv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                if(optEdt.getText().toString().trim().length()<4){
                    Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show();
                }else {
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(new JctMoneyGetSenderFragment());
                }
                break;
            case R.id.resendOtpTv:
                Toast.makeText(context, "Resend Otp", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
