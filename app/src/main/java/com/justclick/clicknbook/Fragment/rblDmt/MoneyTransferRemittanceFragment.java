package com.justclick.clicknbook.Fragment.rblDmt;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.justclick.clicknbook.R;

public class MoneyTransferRemittanceFragment extends Fragment {

    private Context context;
    private OnFragmentInteractionListener mListener;
    private TextView label_account_tv;
    private EditText account_edt;

    public MoneyTransferRemittanceFragment() {
        // Required empty public constructor
    }

    public static MoneyTransferRemittanceFragment newInstance(String param1, String param2) {
        MoneyTransferRemittanceFragment fragment = new MoneyTransferRemittanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_money_transfer_remittance, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
