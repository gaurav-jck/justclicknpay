package com.justclick.clicknbook.Fragment.rblDmt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.MoneyTransferNextActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;


/**
 * Created by Lenovo on 03/28/2017.
 */

public class MoneyTransferTransactionInquiryFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
//    TextView get_detail_tv;
//    EditText transaction_number_edt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_money_transfer_transaction_inquiry, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
//        get_detail_tv = (TextView) view.findViewById(R.id.get_detail_tv);
//        transaction_number_edt = (EditText) view.findViewById(R.id.number_edt);
//        get_detail_tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.get_detail_tv:
//               if(validate()) {
//                   Intent i = new Intent(context, MoneyTransferNextActivity.class);
//                   startActivity(i);
//                   Toast.makeText(context, "registered", Toast.LENGTH_SHORT).show();
//
//               }

            //   break;
        }
    }

    private Boolean validate() {

//        if (transaction_number_edt.getText().toString().length() == 0) {
//            Toast.makeText(context, R.string.empty_and_invalid_transaction, Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }
}

