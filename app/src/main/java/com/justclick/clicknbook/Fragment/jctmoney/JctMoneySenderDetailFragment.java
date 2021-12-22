package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.JctMoneyRecipientListAdapter;
import com.justclick.clicknbook.model.JctMoneySenderDetailResponseModel;
import com.justclick.clicknbook.model.JctMoneyTransactionResponseModel;
import com.justclick.clicknbook.model.JctVerifyAccountResponse;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.JctMoneySenderRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;


import java.util.ArrayList;

import okhttp3.ResponseBody;


/**
 * Created by Lenovo on 03/28/2017.
 */

public class JctMoneySenderDetailFragment extends Fragment implements View.OnClickListener {
    private final int GetSenderDetails=0,DeleteRecipient=1,Transaction=2, VerifyAccount=4;
    private View view;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView senderNameTv, senderMobileTv, senderKycTv, addRecTv, limitTv,
            tv0,tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,
            jctIdTv,otpEdt;
    private EditText amountEdt;
    private ImageView clearTv,cancel,radioButton1,radioButton2,radioButton3,radioButton4;
    private RecyclerView recipientRecycleView;
    private LoginModel loginModel;
    private JctMoneySenderDetailResponseModel jctMoneySenderDetailResponseModel;
    private JctMoneySenderDetailResponseModel.BeneList List;
    private JctMoneyRecipientListAdapter adapter;
    private String amount="0";
    final String IMPS="IMPS", NEFT="NEFT";
    private String TType=IMPS,Pin="";
    private Dialog paymentDialog, pinDialog;
    private int currentListItemPosition=0;
    private boolean isGetDetail=false;
    private float RemainingLimt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        isGetDetail=false;
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
        if(view==null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_jct_money_sender_detail, container, false);
            if (getArguments().getSerializable("senderResponse") != null) {
                jctMoneySenderDetailResponseModel = (JctMoneySenderDetailResponseModel) getArguments().getSerializable("senderResponse");
            }
            try {
                initializeViews(view);
            } catch (Exception e) {
                Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    private void initializeViews(View view) {
        senderNameTv=  view.findViewById(R.id.senderNameTv);
        senderMobileTv= view.findViewById(R.id.senderMobileTv);
        senderKycTv=  view.findViewById(R.id.senderKycTv);
        addRecTv=  view.findViewById(R.id.addRecTv);
        limitTv=  view.findViewById(R.id.limitTv);
        recipientRecycleView=  view.findViewById(R.id.recipientRecycleView);
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

        senderNameTv.setText(jctMoneySenderDetailResponseModel.name);
        senderMobileTv.setText(jctMoneySenderDetailResponseModel.mobile+"");

        limitTv.setText("Rs. "+jctMoneySenderDetailResponseModel.remainingLimt+"");

        if(jctMoneySenderDetailResponseModel.state.equalsIgnoreCase("4")){
            senderKycTv.setText(getResources().getString(R.string.kyc_active));
        }else {
            senderKycTv.setText(getResources().getString(R.string.kyc_inactive));
        }

        adapter=new JctMoneyRecipientListAdapter(context, new JctMoneyRecipientListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<JctMoneySenderDetailResponseModel.BeneList> list, int position) {
                if(view.getId()==R.id.payNowTv){
//                    Toast.makeText(context, "Pay Now", Toast.LENGTH_SHORT).show();
                    openDialog(list.get(position), jctMoneySenderDetailResponseModel.remainingLimt);
                }else if(view.getId()==R.id.accVerifyTv){
//                    Toast.makeText(context, "Verify account", Toast.LENGTH_SHORT).show();
                    verifyAccount(list.get(position));
                    currentListItemPosition=position;
                }else {
                    openDeleteConfirmationDialog("Confirm Delete Request","Please confirm," +
                            " you want to delete this beneficiary.","Cancel","Delete", list.get(position));

                 /*   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    Dialog dialog2 = builder.create();
                    dialog2.setTitle("my dialog");
                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = dialog2.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                    wmlp.x = 100;   //x position
                    wmlp.y = 100;   //y position
                    dialog2.show();*/
                }
            }
        },jctMoneySenderDetailResponseModel.BeneList);
        recipientRecycleView.setAdapter(adapter);
        recipientRecycleView.setLayoutManager(new LinearLayoutManager(context));

        addRecTv.setOnClickListener(this);

    }

    private void verifyAccount(JctMoneySenderDetailResponseModel.BeneList beneList) {
        JctMoneySenderRequestModel jctMoneySenderRequestModel=new JctMoneySenderRequestModel();
        jctMoneySenderRequestModel.MobileNo =jctMoneySenderDetailResponseModel.mobile+"";
        jctMoneySenderRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        jctMoneySenderRequestModel.DeviceId=Common.getDeviceId(context);
        jctMoneySenderRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        jctMoneySenderRequestModel.AccNo=beneList.account;
        jctMoneySenderRequestModel.ifsc=beneList.ifsc;
        new NetworkCall().callJctMoneyService(jctMoneySenderRequestModel, ApiConstants.VerifyAccount, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, VerifyAccount);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openDeleteConfirmationDialog(String title, String message,
                                              String cancel, String delete,final JctMoneySenderDetailResponseModel.BeneList listItem) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog_layout);

        ((TextView) dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView) dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView) dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView) dialog.findViewById(R.id.submit_tv)).setText(delete);
        dialog.findViewById(R.id.remark_edt).setVisibility(View.GONE); ;

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getSenderDetail(ApiConstants.DeleteRecipient, listItem, DeleteRecipient);
//                Toast.makeText(context, "Delete Ben", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


    private void openDialog(final JctMoneySenderDetailResponseModel.BeneList list, final float remainingLimt) {
        TType=IMPS;
        RemainingLimt=remainingLimt;
        List=list;
        paymentDialog = new Dialog(context);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.jct_paynow_dialog);
        final Window window= paymentDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        amountEdt= paymentDialog.findViewById(R.id.amountEdt);
        final EditText otpEdt=  paymentDialog.findViewById(R.id.otpEdt);
        final TextView cancelTv= paymentDialog.findViewById(R.id.cancelTv);
        final TextView payNowTv=  paymentDialog.findViewById(R.id.payNowTv);
        final TextView limitTv= paymentDialog.findViewById(R.id.limitTv);
        final TextView transactionTypeIMPSTv=  paymentDialog.findViewById(R.id.transactionTypeIMPSTv);
        final TextView transactionTypeNEFTTv=  paymentDialog.findViewById(R.id.transactionTypeNEFTTv);

//        Toast.makeText(context, list.available_channel, Toast.LENGTH_SHORT).show();
        if(list.available_channel!=null && list.available_channel.equalsIgnoreCase("1")){
            TType=NEFT;
            transactionTypeIMPSTv.setVisibility(View.GONE);
            transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background);
            transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
        }else if(list.available_channel!=null && list.available_channel.equalsIgnoreCase("2")){
            transactionTypeNEFTTv.setVisibility(View.GONE);
        }

        transactionTypeIMPSTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TType=IMPS;
                transactionTypeIMPSTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.color_white));

                transactionTypeNEFTTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
            }
        });
        transactionTypeNEFTTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TType=NEFT;
                transactionTypeIMPSTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                transactionTypeIMPSTv.setTextColor(getResources().getColor(R.color.dark_blue_color));

                transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background);
                transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
            }
        });

        limitTv.setText("Remaining limit is: "+remainingLimt+"");

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentDialog.cancel();
            }
        });

        payNowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isdecimalvalid(amountEdt.getText().toString().trim())){
                    amount=amountEdt.getText().toString().trim();
                    if(Float.parseFloat(amount)>RemainingLimt  ){
                        Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show();
                    }else if( Float.parseFloat(amount)<100){
                        amountEdt.setError(" " );
                        Toast.makeText(context, "Please enter amount greater than 100", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        amountEdt.setEnabled(true);
//                        paymentDialog.dismiss();
                        openOtpDialog(list,remainingLimt);
                    }
                }else {
                    Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
                }


            }
        });
//        payNowTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Common.hideSoftInputFromDialog(paymentDialog,context);
//                if(Common.isdecimalvalid(amountEdt.getText().toString().trim())){
//                    amount=amountEdt.getText().toString().trim();
//                    if(Float.parseFloat(amount)>remainingLimt  ){
//                        Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show();
//                    }else if( Float.parseFloat(amount)<100){
//                        amountEdt.setError(" " );
//                        Toast.makeText(context, "Please enter amount grater than 100", Toast.LENGTH_SHORT).show();
//                    }else if( otpEdt.getText().toString().length()==0){
//                        otpEdt.setVisibility(View.VISIBLE);
//                        amountEdt.setEnabled(true);
//                        Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        otpEdt.setVisibility(View.VISIBLE);
//                        amountEdt.setEnabled(true);
//                        if( otpEdt.getText().toString().length()==0) {
//
//                            Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show();
//                        }else {
//                        getSenderDetail(ApiConstants.Transaction, list, Transaction);
//                    }}
//                }else {
//                    Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        paymentDialog.show();
    }


    private void openOtpDialog(JctMoneySenderDetailResponseModel.BeneList list, float remainingLimt) {
        pinDialog = new Dialog(context, R.style.Theme_Design_Light);
        pinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pinDialog.setContentView(R.layout.pin_otp);
        final Window window= pinDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        jctIdTv=  pinDialog.findViewById(R.id.jctIdTv);
        otpEdt= pinDialog.findViewById(R.id.otpEdt);
        tv0=  pinDialog.findViewById(R.id.tv0);
        tv1=  pinDialog.findViewById(R.id.tv1);
        tv2=  pinDialog.findViewById(R.id.tv2);
        tv3=  pinDialog.findViewById(R.id.tv3);
        tv4=  pinDialog.findViewById(R.id.tv4);
        tv5=  pinDialog.findViewById(R.id.tv5);
        tv6=  pinDialog.findViewById(R.id.tv6);
        tv7=  pinDialog.findViewById(R.id.tv7);
        tv8=  pinDialog.findViewById(R.id.tv8);
        tv9=  pinDialog.findViewById(R.id.tv9);
        clearTv=  pinDialog.findViewById(R.id.clearTv);
        cancel=  pinDialog.findViewById(R.id.cancel);
        radioButton1=  pinDialog.findViewById(R.id.radioButton1);
        radioButton2=  pinDialog.findViewById(R.id.radioButton2);
        radioButton3=  pinDialog.findViewById(R.id.radioButton3);
        radioButton4=  pinDialog.findViewById(R.id.radioButton4);

        jctIdTv.setText(loginModel.Data.DoneCardUser);
        tv0.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);

        clearTv.setOnClickListener(this);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pin="";
                pinDialog.cancel();
            }
        });
        pinDialog.show();
    }

    public void getSenderDetail(String methodName, JctMoneySenderDetailResponseModel.BeneList item, final int type) {
        JctMoneySenderRequestModel jctMoneySenderRequestModel=new JctMoneySenderRequestModel();
        jctMoneySenderRequestModel.MobileNo =jctMoneySenderDetailResponseModel.mobile+"";
        jctMoneySenderRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        jctMoneySenderRequestModel.DeviceId=Common.getDeviceId(context);
        jctMoneySenderRequestModel.benreq=true;
        if(item!=null){
            jctMoneySenderRequestModel.recipient_id=item.recipient_id;
            jctMoneySenderRequestModel.customer_name=item.recipient_name;
        }
        jctMoneySenderRequestModel.TxnType=TType;
        jctMoneySenderRequestModel.customer_id=jctMoneySenderDetailResponseModel.customer_id;
        jctMoneySenderRequestModel.Amount= Float.parseFloat(amount);
//        jctMoneySenderRequestModel.Pin= EncryptionDecryptionClass.EncryptSessionId(Pin, context);
//        jctMoneySenderRequestModel.Pin= "";
        try {
            jctMoneySenderRequestModel.TPin=EncryptionDecryptionClass.computeHash(Pin);
        } catch (Exception e) {
            jctMoneySenderRequestModel.TPin="";
            e.printStackTrace();
        }
        jctMoneySenderRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);

        new NetworkCall().callJctMoneyService(jctMoneySenderRequestModel, methodName, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, type);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            hideCustomDialog();
            if(TYPE==GetSenderDetails){
                JctMoneySenderDetailResponseModel senderResponse = new Gson().fromJson(response.string(), JctMoneySenderDetailResponseModel.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode ==1) {
                        updateSenderDetails(senderResponse);
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==DeleteRecipient){
                JctMoneySenderDetailResponseModel senderResponse = new Gson().fromJson(response.string(), JctMoneySenderDetailResponseModel.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode ==1) {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                        getSenderDetail(ApiConstants.GetSenderDetails, null, GetSenderDetails);
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==Transaction){
                JctMoneyTransactionResponseModel senderResponse = new Gson().fromJson(response.string(), JctMoneyTransactionResponseModel.class);
                checkRadionButton(Pin);
                if(senderResponse!=null){
                    if(senderResponse.StatusCode ==1 || senderResponse.StatusCode ==2) {
//                        Toast.makeText(context,senderResponse.Message,Toast.LENGTH_SHORT).show();
                        pinDialog.dismiss();
                        paymentDialog.dismiss();
                        getSenderDetail(ApiConstants.GetSenderDetails, null, GetSenderDetails);
                        try {
                            openReceipt(senderResponse);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("RESPONSE");
                            DatabaseReference myRef2 = database.getReference("TYPE");
                            myRef2.setValue("Hello");
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
//                                    boolean value =  (boolean)dataSnapshot.getValue();
//                                    Log.d(TAG, "Value is: " + value);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
//                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                            myRef.setValue(true);

                        }catch (Exception e){
                            Toast.makeText(context,"Enable to show receipt",Toast.LENGTH_SHORT).show();
                        }
                    }else if(senderResponse.StatusCode ==10){
                        Toast.makeText(context,senderResponse.Message,Toast.LENGTH_SHORT).show();
                    }else {
                        pinDialog.dismiss();
                        Toast.makeText(context,senderResponse.Message,Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VerifyAccount){
                JctVerifyAccountResponse senderResponse = new Gson().fromJson(response.string(), JctVerifyAccountResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode ==1) {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                        getSenderDetail(ApiConstants.GetSenderDetails, null, GetSenderDetails);
                    } else {
                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){

        }
    }
    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait");
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    private void openReceipt(final JctMoneyTransactionResponseModel senderResponse) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jct_money_receipt_dialog);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final TextView mobileTv= dialog.findViewById(R.id.mobileTv);
        final TextView bankNameTv=  dialog.findViewById(R.id.bankNameTv);
        final TextView accountNoTv= dialog.findViewById(R.id.accountNoTv);
        final TextView benIdTv=  dialog.findViewById(R.id.benIdTv);
        final TextView jctTxnIdTv= dialog.findViewById(R.id.jctTxnIdTv);
        final TextView bankRefNoTv=  dialog.findViewById(R.id.bankRefNoTv);
        final TextView remitAmountTv=  dialog.findViewById(R.id.remitAmountTv);
        final TextView txnTypeTv= dialog.findViewById(R.id.txnTypeTv);
        final TextView txnIdTv=  dialog.findViewById(R.id.txnIdTv);
        final TextView txnDateTv= dialog.findViewById(R.id.txnDateTv);

        final TextView errorTv= dialog.findViewById(R.id.errorTv);
        final LinearLayout contentLin= dialog.findViewById(R.id.contentLin);

        final TextView tab1= dialog.findViewById(R.id.tab1);
        final TextView tab2= dialog.findViewById(R.id.tab2);
        final TextView tab3= dialog.findViewById(R.id.tab3);
        final TextView tab4= dialog.findViewById(R.id.tab4);
        final TextView tab5= dialog.findViewById(R.id.tab5);

        tab1.setBackgroundResource(R.drawable.blue_round_button_background);
        tab2.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab3.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab4.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab5.setBackgroundResource(R.drawable.blue_round_corner_button_background);

        if (senderResponse.Response != null && senderResponse.Response.size() > 0) {
            if(senderResponse.Response.size()==1){
                tab1.setVisibility(View.GONE);
                tab2.setVisibility(View.GONE);
                tab3.setVisibility(View.GONE);
                tab4.setVisibility(View.GONE);
                tab5.setVisibility(View.GONE);
            }else if(senderResponse.Response.size()==2){
                tab1.setVisibility(View.VISIBLE);
                tab2.setVisibility(View.VISIBLE);
                tab3.setVisibility(View.GONE);
                tab4.setVisibility(View.GONE);
                tab5.setVisibility(View.GONE);
                float totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
                }
                tab1.setText("1st - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(1).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(1).data.get(i).amount;
                }
                tab2.setText("2nd - "+totalAmt);
            }else if(senderResponse.Response.size()==3){
                tab1.setVisibility(View.VISIBLE);
                tab2.setVisibility(View.VISIBLE);
                tab3.setVisibility(View.VISIBLE);
                tab4.setVisibility(View.GONE);
                tab5.setVisibility(View.GONE);
                float totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
                }
                tab1.setText("1st - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(1).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(1).data.get(i).amount;
                }
                tab2.setText("2nd - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(2).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(2).data.get(i).amount;
                }
                tab3.setText("3rd - "+totalAmt);
            }else if(senderResponse.Response.size()==4){
                tab1.setVisibility(View.VISIBLE);
                tab2.setVisibility(View.VISIBLE);
                tab3.setVisibility(View.VISIBLE);
                tab4.setVisibility(View.VISIBLE);
                tab5.setVisibility(View.INVISIBLE);
                float totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
                }
                tab1.setText("1st - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(1).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(1).data.get(i).amount;
                }
                tab2.setText("2nd - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(2).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(2).data.get(i).amount;
                }
                tab3.setText("3rd - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(3).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(3).data.get(i).amount;
                }
                tab4.setText("4th - "+totalAmt);
            }else {
                tab1.setVisibility(View.VISIBLE);
                tab2.setVisibility(View.VISIBLE);
                tab3.setVisibility(View.VISIBLE);
                tab4.setVisibility(View.VISIBLE);
                tab5.setVisibility(View.VISIBLE);
                float totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
                }
                tab1.setText("1st - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(1).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(1).data.get(i).amount;
                }
                tab2.setText("2nd - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(2).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(2).data.get(i).amount;
                }
                tab3.setText("3rd - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(3).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(3).data.get(i).amount;
                }
                tab4.setText("4th - "+totalAmt);
                totalAmt=0;
                for(int i=0;i<senderResponse.Response.get(4).data.size(); i++){
                    totalAmt=totalAmt+senderResponse.Response.get(4).data.get(i).amount;
                }
                tab5.setText("5th - "+totalAmt);
            }
        }


        mobileTv.setText(senderResponse.Response.get(0).data.get(0).customer_id);
        bankNameTv.setText(senderResponse.Response.get(0).data.get(0).bank);
        accountNoTv.setText(senderResponse.Response.get(0).data.get(0).account);
        benIdTv.setText(senderResponse.Response.get(0).data.get(0).recipient_id);
        jctTxnIdTv.setText(senderResponse.Response.get(0).data.get(0).JCTRefID);
        bankRefNoTv.setText(senderResponse.Response.get(0).data.get(0).bank_ref_num);
        txnTypeTv.setText(senderResponse.Response.get(0).data.get(0).TxnType);
        txnIdTv.setText(senderResponse.Response.get(0).data.get(0).tid);
        float totalAmt=0;
        for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
            totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
        }
        remitAmountTv.setText(totalAmt+"");
        txnDateTv.setText(senderResponse.Response.get(0).data.get(0).timestamp);

        dialog.findViewById(R.id.print_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(senderResponse.Response.get(0).data.size()==0){
                    contentLin.setVisibility(View.GONE);
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Transaction not completed.\nMessage: "+senderResponse.Response.get(0).message);
                }else {
                    contentLin.setVisibility(View.VISIBLE);
                    errorTv.setVisibility(View.GONE);
                    mobileTv.setText(senderResponse.Response.get(0).data.get(0).customer_id);
                    bankNameTv.setText(senderResponse.Response.get(0).data.get(0).bank);
                    accountNoTv.setText(senderResponse.Response.get(0).data.get(0).account);
                    benIdTv.setText(senderResponse.Response.get(0).data.get(0).recipient_id);
                    jctTxnIdTv.setText(senderResponse.Response.get(0).data.get(0).JCTRefID);
                    bankRefNoTv.setText(senderResponse.Response.get(0).data.get(0).bank_ref_num);
                    txnTypeTv.setText(senderResponse.Response.get(0).data.get(0).TxnType);
                    txnIdTv.setText(senderResponse.Response.get(0).data.get(0).tid);
                    float totalAmt=0;
                    for(int i=0;i<senderResponse.Response.get(0).data.size(); i++){
                        totalAmt=totalAmt+senderResponse.Response.get(0).data.get(i).amount;
                    }
                    remitAmountTv.setText(totalAmt+"");
                    txnDateTv.setText(Common.getJCTMoneyTime(senderResponse.Response.get(0).data.get(0).timestamp));
                }
                tabClick(tab1, tab2, tab3, tab4, tab5);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(senderResponse.Response.get(1).data.size()==0){
                    contentLin.setVisibility(View.GONE);
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Transaction not completed.\nMessage: "+senderResponse.Response.get(1).message);
                }else {
                    contentLin.setVisibility(View.VISIBLE);
                    errorTv.setVisibility(View.GONE);
                    mobileTv.setText(senderResponse.Response.get(1).data.get(0).customer_id);
                    bankNameTv.setText(senderResponse.Response.get(1).data.get(0).bank);
                    accountNoTv.setText(senderResponse.Response.get(1).data.get(0).account);
                    benIdTv.setText(senderResponse.Response.get(1).data.get(0).recipient_id);
                    jctTxnIdTv.setText(senderResponse.Response.get(1).data.get(0).JCTRefID);
                    bankRefNoTv.setText(senderResponse.Response.get(1).data.get(0).bank_ref_num);
                    txnTypeTv.setText(senderResponse.Response.get(1).data.get(0).TxnType);
                    txnIdTv.setText(senderResponse.Response.get(1).data.get(0).tid);
                    float totalAmt=0;
                    for(int i=0;i<senderResponse.Response.get(1).data.size(); i++){
                        totalAmt=totalAmt+senderResponse.Response.get(1).data.get(i).amount;
                    }
                    remitAmountTv.setText(totalAmt+"");
                    txnDateTv.setText(Common.getJCTMoneyTime(senderResponse.Response.get(1).data.get(0).timestamp));
                }
                tabClick(tab2, tab1, tab3, tab4, tab5);
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(senderResponse.Response.get(2).data.size()==0){
                    contentLin.setVisibility(View.GONE);
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Transaction not completed.\nMessage: "+senderResponse.Response.get(2).message);
                }else {
                    contentLin.setVisibility(View.VISIBLE);
                    errorTv.setVisibility(View.GONE);
                    mobileTv.setText(senderResponse.Response.get(2).data.get(0).customer_id);
                    bankNameTv.setText(senderResponse.Response.get(2).data.get(0).bank);
                    accountNoTv.setText(senderResponse.Response.get(2).data.get(0).account);
                    benIdTv.setText(senderResponse.Response.get(2).data.get(0).recipient_id);
                    jctTxnIdTv.setText(senderResponse.Response.get(2).data.get(0).JCTRefID);
                    bankRefNoTv.setText(senderResponse.Response.get(2).data.get(0).bank_ref_num);
                    txnTypeTv.setText(senderResponse.Response.get(2).data.get(0).TxnType);
                    txnIdTv.setText(senderResponse.Response.get(2).data.get(0).tid);
                    float totalAmt=0;
                    for(int i=0;i<senderResponse.Response.get(2).data.size(); i++){
                        totalAmt=totalAmt+senderResponse.Response.get(2).data.get(i).amount;
                    }
                    remitAmountTv.setText(totalAmt+"");
                    txnDateTv.setText(Common.getJCTMoneyTime(senderResponse.Response.get(2).data.get(0).timestamp));
                }
                tabClick(tab3, tab2, tab1, tab4, tab5);
            }
        });
        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(senderResponse.Response.get(3).data.size()==0){
                    contentLin.setVisibility(View.GONE);
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Transaction not completed.\nMessage: "+senderResponse.Response.get(0).message);
                }else {
                    contentLin.setVisibility(View.VISIBLE);
                    errorTv.setVisibility(View.GONE);
                    mobileTv.setText(senderResponse.Response.get(3).data.get(0).customer_id);
                    bankNameTv.setText(senderResponse.Response.get(3).data.get(0).bank);
                    accountNoTv.setText(senderResponse.Response.get(3).data.get(0).account);
                    benIdTv.setText(senderResponse.Response.get(3).data.get(0).recipient_id);
                    jctTxnIdTv.setText(senderResponse.Response.get(3).data.get(0).JCTRefID);
                    bankRefNoTv.setText(senderResponse.Response.get(3).data.get(0).bank_ref_num);
                    txnTypeTv.setText(senderResponse.Response.get(3).data.get(0).TxnType);
                    txnIdTv.setText(senderResponse.Response.get(3).data.get(0).tid);
                    float totalAmt=0;
                    for(int i=0;i<senderResponse.Response.get(3).data.size(); i++){
                        totalAmt=totalAmt+senderResponse.Response.get(3).data.get(i).amount;
                    }
                    remitAmountTv.setText(totalAmt+"");
                    txnDateTv.setText(Common.getJCTMoneyTime(senderResponse.Response.get(3).data.get(0).timestamp));
                }
                tabClick(tab4, tab2, tab3, tab1, tab5);
            }
        });
        tab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(senderResponse.Response.get(4).data.size()==0){
                    contentLin.setVisibility(View.GONE);
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Transaction not completed.\nMessage: "+senderResponse.Response.get(4).message);
                }else {
                    contentLin.setVisibility(View.VISIBLE);
                    errorTv.setVisibility(View.GONE);
                    mobileTv.setText(senderResponse.Response.get(4).data.get(0).customer_id);
                    bankNameTv.setText(senderResponse.Response.get(4).data.get(0).bank);
                    accountNoTv.setText(senderResponse.Response.get(4).data.get(0).account);
                    benIdTv.setText(senderResponse.Response.get(4).data.get(0).recipient_id);
                    jctTxnIdTv.setText(senderResponse.Response.get(4).data.get(0).JCTRefID);
                    bankRefNoTv.setText(senderResponse.Response.get(4).data.get(0).bank_ref_num);
                    txnTypeTv.setText(senderResponse.Response.get(4).data.get(0).TxnType);
                    txnIdTv.setText(senderResponse.Response.get(4).data.get(0).tid);
                    float totalAmt=0;
                    for(int i=0;i<senderResponse.Response.get(4).data.size(); i++){
                        totalAmt=totalAmt+senderResponse.Response.get(4).data.get(i).amount;
                    }
                    remitAmountTv.setText(totalAmt+"");
                    txnDateTv.setText(Common.getJCTMoneyTime(senderResponse.Response.get(4).data.get(0).timestamp));
                }
                tabClick(tab5, tab2, tab3, tab4, tab1);
            }
        });

        dialog.show();

    }

    private void tabClick(TextView tab1, TextView tab2, TextView tab3, TextView tab4, TextView tab5) {
        tab1.setBackgroundResource(R.drawable.blue_round_button_background);
        tab2.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab3.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab4.setBackgroundResource(R.drawable.blue_round_corner_button_background);
        tab5.setBackgroundResource(R.drawable.blue_round_corner_button_background);

        tab1.setTextColor(getResources().getColor(R.color.color_white));
        tab2.setTextColor(getResources().getColor(R.color.printDialogLableColor));
        tab3.setTextColor(getResources().getColor(R.color.printDialogLableColor));
        tab4.setTextColor(getResources().getColor(R.color.printDialogLableColor));
        tab5.setTextColor(getResources().getColor(R.color.printDialogLableColor));
    }

    private void updateSenderDetails(JctMoneySenderDetailResponseModel senderResponse) {
        if(senderResponse.BeneList.size()>0 && senderResponse.BeneList.size()>=currentListItemPosition){
            senderResponse.BeneList.get(currentListItemPosition).isVisible=true;
        }
        limitTv.setText("Rs. "+senderResponse.remainingLimt+"");
        jctMoneySenderDetailResponseModel.remainingLimt=senderResponse.remainingLimt;
        jctMoneySenderDetailResponseModel.Limit=senderResponse.Limit;
        jctMoneySenderDetailResponseModel.BeneList.clear();
        jctMoneySenderDetailResponseModel.BeneList.addAll(senderResponse.BeneList);
        recipientRecycleView.setAdapter(new JctMoneyRecipientListAdapter(context, new JctMoneyRecipientListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<JctMoneySenderDetailResponseModel.BeneList> list, int position) {
                if(view.getId()==R.id.payNowTv){
//                    Toast.makeText(context, "Pay Now", Toast.LENGTH_SHORT).show();
                    openDialog(list.get(position), jctMoneySenderDetailResponseModel.remainingLimt);
                }else if(view.getId()==R.id.accVerifyTv){
//                    Toast.makeText(context, "Verify account", Toast.LENGTH_SHORT).show();
                    verifyAccount(list.get(position));
                    currentListItemPosition=position;
                }else {
                    openDeleteConfirmationDialog("Confirm Delete Request","Please confirm," +
                            " you want to delete this beneficiary.","Cancel","Delete", list.get(position));
                }
            }
        },senderResponse.BeneList));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                getFragmentManager().popBackStack();
                break;

            case R.id.addRecTv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(addRecTv);
                JctMoneyAddRecipientFragment jctMoneyAddRecipientFragment=new JctMoneyAddRecipientFragment();
                Bundle bundle=new Bundle();
//                bundle.putSerializable("Fragment", fragment);
                bundle.putString("Mobile", jctMoneySenderDetailResponseModel.mobile+"");
                jctMoneyAddRecipientFragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(jctMoneyAddRecipientFragment);
                break;
            case R.id.clearTv:
                if(Pin.length()<2){
                    Pin="";
                    checkRadionButton(Pin);
                }else {
                    Pin=Pin.substring(0,Pin.length()-1);
                    checkRadionButton(Pin);
                }
                break;
            case R.id.tv0:
                Pin=Pin+0;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv1:
                Pin=Pin+1;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv2:
                Pin=Pin+2;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv3:
                Pin=Pin+3;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv4:
                Pin=Pin+4;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv5:
                Pin=Pin+5;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv6:
                Pin=Pin+6;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv7:
                Pin=Pin+7;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv8:
                Pin=Pin+8;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
            case R.id.tv9:
                Pin=Pin+9;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    checkOtp();
                }
                break;
        }
    }

    private void checkRadionButton(String Pin) {
        if(Pin.length()==0) {
            radioButton1.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==1) {
            radioButton1.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==2){
            radioButton2.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));
        }else if(Pin.length()==3){
            radioButton3.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_uncheck_blue));

        }else if(Pin.length()==4){
            radioButton4.setImageDrawable(getResources().getDrawable(R.drawable.radio_check_blue));
        }

    }

    private void checkOtp() {
        Common.hideSoftInputFromDialog(pinDialog,context);
        getSenderDetail(ApiConstants.Transaction, List, Transaction);
        Pin="";
    }

    private Boolean validate() {

        if (new EditText(context).getText().toString().length() < 10)
        {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(isGetDetail==false){
//            isGetDetail=true;
//        }else {
//            getSenderDetail(ApiConstants.GetSenderDetails, null, 0);
//        }
    }
}

