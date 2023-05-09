package com.justclick.clicknbook.Fragment.creditcard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class CreditCardTxnFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private String token, userData;
    private EditText name_edt, number_edt, card_number_edt, amount_edt, remarks_edt, network_edt, otp_edt;
    private CreditCardRequest request;

    public CreditCardTxnFragment() {
        // Required empty public constructor
    }

    public static CreditCardTxnFragment newInstance(String param1, String param2) {
        CreditCardTxnFragment fragment = new CreditCardTxnFragment();
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
        if (getArguments() != null) {
            request= (CreditCardRequest) getArguments().getSerializable("Request");
            token= getArguments().getString("token");
            userData= getArguments().getString("userData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_credit_card_txn, container, false);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        name_edt=view.findViewById(R.id.name_edt);
        number_edt=view.findViewById(R.id.number_edt);
        card_number_edt=view.findViewById(R.id.card_number_edt);
        amount_edt=view.findViewById(R.id.amount_edt);
        remarks_edt=view.findViewById(R.id.remarks_edt);
        network_edt=view.findViewById(R.id.network_edt);
        otp_edt=view.findViewById(R.id.otp_edt);

        try {
            setData();
        }catch (NullPointerException e){

        }

        view.findViewById(R.id.back_arrow).setOnClickListener(this);
        view.findViewById(R.id.submit_tv).setOnClickListener(this);

        return view;
    }

    private void setData() {
        name_edt.setText(request.name);
        number_edt.setText(request.mobile);
        card_number_edt.setText(request.card_number);
        amount_edt.setText(request.amount);
        network_edt.setText(request.network);
        remarks_edt.setText(request.remarks);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                if(validate()){
                    initiateTxn();
                }
                break;
        }
    }

    private void initiateTxn() {
        request.otp=otp_edt.getText().toString();

        String json = new Gson().toJson(request);

        new NetworkCall().callService(NetworkCall.getCreditCardApiInterface().creditCard(ApiConstants.ccinitiatetrans, request,
                        userData, "Bearer "+token), context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerTxn(response, 1);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerTxn(ResponseBody response, int i) {
        try {
            CreditCardTxnResponse senderResponse = new Gson().fromJson(response.string(), CreditCardTxnResponse.class);
            if(senderResponse!=null){
                if(senderResponse.statusCode.equals("00")) {
                    Toast.makeText(context,senderResponse.statusmessage,Toast.LENGTH_SHORT).show();
                    openReceipt(senderResponse);
                }else {
                    Toast.makeText(context,senderResponse.statusmessage,Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void openReceipt(CreditCardTxnResponse senderResponse) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.credit_receipt_dialog);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView title=dialog.findViewById(R.id.title);
        TextView agentCodeTv=dialog.findViewById(R.id.agentCodeTv);
        TextView cardNoTv=dialog.findViewById(R.id.cardNoTv);
        TextView nameTv=dialog.findViewById(R.id.nameTv);
        TextView networkTv=dialog.findViewById(R.id.networkTv);
        TextView amountTv=dialog.findViewById(R.id.amountTv);
        TextView txnIdTv=dialog.findViewById(R.id.txnIdTv);
        TextView refIdTv=dialog.findViewById(R.id.refIdTv);
        TextView remarkTv=dialog.findViewById(R.id.remarkTv);
        TextView dateTv=dialog.findViewById(R.id.dateTv);
        TextView statusTv=dialog.findViewById(R.id.statusTv);

        title.setText("Credit Card Bill Receipt");
        agentCodeTv.setText(request.agentcode);
        cardNoTv.setText(request.card_number);
        nameTv.setText(senderResponse.name);
        networkTv.setText(senderResponse.network);
        amountTv.setText(senderResponse.amount);
        txnIdTv.setText(senderResponse.txnid);
        refIdTv.setText(senderResponse.refid);
        remarkTv.setText(senderResponse.remarks);
        dateTv.setText(senderResponse.dateadded);
        statusTv.setText(senderResponse.status);


        dialog.findViewById(R.id.back_tv).setOnClickListener(view -> {
            dialog.dismiss();
            getParentFragmentManager().popBackStack();
        });

        dialog.show();
    }

    private boolean validate() {
        if(otp_edt.getText().toString().length()<6){
           Toast.makeText(context, R.string.empty_and_invalid_otp, Toast.LENGTH_SHORT).show();
           return false;
        }
        return true;
    }


    private String data="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusmessage\": \"Transaction Success\",\n" +
            "    \"txnid\": \"56160182\",\n" +
            "    \"refid\": \"CC26043H81IJC0A44006\",\n" +
            "    \"name\": \"Rama\",\n" +
            "    \"mobile\": \"9000789769\",\n" +
            "    \"card_number\": \"457704XXXXXX2237\",\n" +
            "    \"amount\": \"100.00\",\n" +
            "    \"network\": \"VISA\",\n" +
            "    \"utr\": \"AXISCN0241175596\",\n" +
            "    \"remarks\": \"Rk\",\n" +
            "    \"dateadded\": \"2023-04-26 23:32:33\",\n" +
            "    \"status\": \"Success\"\n" +
            "}";
}