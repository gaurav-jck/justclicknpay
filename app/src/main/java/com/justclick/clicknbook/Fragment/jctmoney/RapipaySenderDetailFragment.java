package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.adapter.RapipayRecipientListAdapter;
import com.justclick.clicknbook.Fragment.jctmoney.adapter.RapipayTxnDetailAdapter;
import com.justclick.clicknbook.Fragment.jctmoney.request.AddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams;
import com.justclick.clicknbook.Fragment.jctmoney.request.DeleteBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.SenderDetailRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.TransactionRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.TransactionResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;


public class RapipaySenderDetailFragment extends Fragment implements View.OnClickListener {
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
    private SenderDetailResponse senderDetailResponse;
    private SenderDetailResponse.senderDetailInfo senderInfo;
    private SenderDetailResponse.benificiaryDetailData beneData;
    private RapipayRecipientListAdapter adapter;
    private int amount;
    final String IMPS="IMPS", NEFT="NEFT";
    private String TType=IMPS,Pin="";
    private Dialog paymentDialog, pinDialog;
    private int currentListItemPosition=0;
    private boolean isGetDetail=false;
    private float RemainingLimt;
    private CommonParams commonParams;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
//        commonParams=new CommonParams();
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
                senderDetailResponse = (SenderDetailResponse) getArguments().getSerializable("senderResponse");
                commonParams = (CommonParams) getArguments().getSerializable("commonParams");
                senderInfo = senderDetailResponse.getSenderDetailInfo().get(0);
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

        senderNameTv.setText(senderInfo.getName());
        senderMobileTv.setText(senderInfo.getMobile());

        limitTv.setText("Rs. "+senderDetailResponse.getRemainingLimit()+"");

        senderKycTv.setText(commonParams.getKycStatus());

        recipientRecycleView.setAdapter(getAdapter(senderDetailResponse.getBenificiaryDetailData()));
        recipientRecycleView.setLayoutManager(new LinearLayoutManager(context));

        addRecTv.setOnClickListener(this);

        final TextView type1= view.findViewById(R.id.type1);
        final TextView bank1= view.findViewById(R.id.bank1);
        final TextView type2= view.findViewById(R.id.type2);
        final TextView bank2= view.findViewById(R.id.bank2);
        final TextView type3= view.findViewById(R.id.type3);
        final TextView bank3= view.findViewById(R.id.bank3);

        type1.setText(senderDetailResponse.getType1());
        type2.setText(senderDetailResponse.getType2());
        type3.setText(senderDetailResponse.getType3());
        bank1.setText(senderDetailResponse.getBank1());
        bank2.setText(senderDetailResponse.getBank2());
        bank3.setText(senderDetailResponse.getBank3());

    }

    private RapipayRecipientListAdapter getAdapter(ArrayList<SenderDetailResponse.benificiaryDetailData> benificiaryDetailData) {
        return new RapipayRecipientListAdapter(context, new RapipayRecipientListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<SenderDetailResponse.benificiaryDetailData> list, int position) {
                if(view.getId()==R.id.payNowTv){
//                    Toast.makeText(context, "Pay Now", Toast.LENGTH_SHORT).show();
                    beneData=list.get(position);
                    if(beneData.getAccountNumber()==null || beneData.getAccountNumber().length()==0){
                        Toast.makeText(context, "You can't do any transaction to this beneficiary.\n" +
                                "Please add new beneficiary.", Toast.LENGTH_SHORT).show();
                    }else {
                        openDialog(senderDetailResponse);
                    }
                }else if(view.getId()==R.id.validateTv){
                    beneData=list.get(position);
                    if(beneData.getAccountNumber()==null || beneData.getAccountNumber().length()==0){
                        Toast.makeText(context, "You can't validate this beneficiary.", Toast.LENGTH_SHORT).show();
                    }else {
                        verifyAccount(list.get(position));
                        currentListItemPosition=position;
                    }
                }else {
                    openDeleteConfirmationDialog("Confirm Delete Request","Please confirm," +
                            " you want to delete this beneficiary.","Cancel","Delete", list.get(position));
                }
            }
        },benificiaryDetailData,senderInfo.getMobile());
    }

    private void verifyAccount(SenderDetailResponse.benificiaryDetailData beneData) {
        AddBeneRequest requestModel=new AddBeneRequest();
        requestModel.setAgentCode(loginModel.Data.DoneCardUser);
        requestModel.setSessionKey(commonParams.getSessionKey());
        requestModel.setMode("App");
        requestModel.setSessionRefId(commonParams.getSessionRefNo());
        requestModel.setBankName(beneData.getBankName());
        requestModel.setAccountHolderName(beneData.getAccountHolderName());
        requestModel.setAccountNumber(beneData.getAccountNumber());
        requestModel.setIfscCode(beneData.getIfsc());
        requestModel.setMobile(senderInfo.getMobile());
        //  https://remittance.justclicknpay.com/api/payments/ValidateAccuont
//{"AccountHolderName":"atuulll","AccountNumber":"135301507733","AgentCode":"JC0A13387","BankName":"ICICI BANK LIMITED",
// "IfscCode":"ICIC0000001","MerchantId":"JUSTCLICKTRAVELS","Mobile":"8468862808","Mode":"App","SessionKey":"DBS210106145635S096280609627","SessionRefId":"V016532537"}
        new NetworkCall().callRapipayServiceHeader(requestModel, ApiConstants.ValidateAccount, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, VerifyAccount);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private void openDeleteConfirmationDialog(String title, String message,
                                              String cancel, String delete,
                                              final SenderDetailResponse.benificiaryDetailData listItem) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog_layout);

        ((TextView) dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView) dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView) dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView) dialog.findViewById(R.id.submit_tv)).setText(delete);
        dialog.findViewById(R.id.remark_edt).setVisibility(View.GONE);

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
                deleteBene(ApiConstants.DeleteBenificiary, listItem, DeleteRecipient);
//                Toast.makeText(context, "Delete Ben", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void deleteBene(String deleteBenificiary, SenderDetailResponse.benificiaryDetailData listItem, final int deleteRecipient) {
        DeleteBeneRequest request =new DeleteBeneRequest();
        request.setBeniId(listItem.getBeneid());
        request.setAgentCode(loginModel.Data.DoneCardUser);
        request.setSessionKey(commonParams.getSessionKey());
        request.setSessionRefId(commonParams.getSessionRefNo());
//{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
        new NetworkCall().callRapipayServiceHeader(request, ApiConstants.DeleteBenificiary, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, deleteRecipient);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }


    private void openDialog(final SenderDetailResponse senderDetailResponse) {
        TType=IMPS;
        RemainingLimt=senderDetailResponse.getRemainingLimit();
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
        final TextView type1= paymentDialog.findViewById(R.id.type1);
        final TextView bank1= paymentDialog.findViewById(R.id.bank1);
        final TextView type2= paymentDialog.findViewById(R.id.type2);
        final TextView bank2= paymentDialog.findViewById(R.id.bank2);
        final TextView type3= paymentDialog.findViewById(R.id.type3);
        final TextView bank3= paymentDialog.findViewById(R.id.bank3);
        final TextView transactionTypeIMPSTv=  paymentDialog.findViewById(R.id.transactionTypeIMPSTv);
        final TextView transactionTypeNEFTTv=  paymentDialog.findViewById(R.id.transactionTypeNEFTTv);

        type1.setText(senderDetailResponse.getType1());
        type2.setText(senderDetailResponse.getType2());
        type3.setText(senderDetailResponse.getType3());
        bank1.setText(senderDetailResponse.getBank1());
        bank2.setText(senderDetailResponse.getBank2());
        bank3.setText(senderDetailResponse.getBank3());

//        Toast.makeText(context, list.available_channel, Toast.LENGTH_SHORT).show();
        if(!beneData.getIsIMPS().equals("Y")){
            TType=NEFT;
            transactionTypeIMPSTv.setVisibility(View.GONE);
            transactionTypeNEFTTv.setBackgroundResource(R.drawable.blue_rect_button_background);
            transactionTypeNEFTTv.setTextColor(getResources().getColor(R.color.color_white));
        }else if(!beneData.getIsNEFT().equals("Y")){
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

        limitTv.setText("Remaining limit is: "+senderDetailResponse.getRemainingLimit()+"");

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
                    amount= Integer.parseInt(amountEdt.getText().toString());
                    if(amount>RemainingLimt  ){
                        Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show();
                    }else if( amount<100){
                        amountEdt.setError(" " );
                        Toast.makeText(context, "Please enter amount greater than 100", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        amountEdt.setEnabled(true);
//                        paymentDialog.dismiss();
                        openTxnConfirmationDialog("Confirm this transaction","Please confirm," +
                                        " you want to make this transaction.\n"+"Amount="+amount+
                                        "\nAccount no="+beneData.getAccountNumber()+"\nName="+beneData.getAccountHolderName(),
                                "Cancel","Submit");
                    }
                }else {
                    Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
                }


            }
        });

        paymentDialog.show();
    }

    private void openTxnConfirmationDialog(String title, String message,
                                           String cancel, String submit) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_dialog_layout);

        ((TextView) dialog.findViewById(R.id.title_tv)).setText(title);
        ((TextView) dialog.findViewById(R.id.confirm_message_tv)).setText(message);
        ((TextView) dialog.findViewById(R.id.cancel_tv)).setText(cancel);
        ((TextView) dialog.findViewById(R.id.submit_tv)).setText(submit);
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
                makeTransaction();
            }
        });

        dialog.show();
    }


    private void openOtpDialog(float remainingLimt) {
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

    public void getSenderDetail(final int type) {
        SenderDetailRequest jctMoneySenderRequestModel=new SenderDetailRequest();
        jctMoneySenderRequestModel.setMobile(senderInfo.getMobile());
        jctMoneySenderRequestModel.setAgentCode(loginModel.Data.DoneCardUser);
        jctMoneySenderRequestModel.setSessionKey(commonParams.getSessionKey());
        jctMoneySenderRequestModel.setSessionRefId(commonParams.getSessionRefNo());
//{"AgentCode":"JC0A13387","Mobile":"8468862808","SessionKey":"DBS210101215032S856120185611","SessionRefId":"V015563577","MerchantId":"JUSTCLICKTRAVELS","Mode":"App"}
        new NetworkCall().callRapipayServiceHeader(jctMoneySenderRequestModel, ApiConstants.SenderDetail, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, GetSenderDetails);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            hideCustomDialog();
            if(TYPE==GetSenderDetails){
                SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        commonParams.setSessionKey(senderResponse.getSessionKey());
                        commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                        updateSenderDetails(senderResponse);
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==DeleteRecipient){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        getSenderDetail(GetSenderDetails);
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==Transaction){
                TransactionResponse senderResponse = new Gson().fromJson(response.string(), TransactionResponse.class);
//                checkRadionButton(Pin);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
//                        Toast.makeText(context,senderResponse.Message,Toast.LENGTH_SHORT).show();
//                        pinDialog.dismiss();
                        paymentDialog.dismiss();
//                        commonParams.setSessionKey(senderResponse.getSessionKey());
//                        commonParams.setSessionRefNo(senderResponse.getSessionRefId());
                        getSenderDetail(GetSenderDetails);
                        try {
                            openReceipt(senderResponse);
                        }catch (Exception e){
                            Toast.makeText(context,"Enable to show receipt",Toast.LENGTH_SHORT).show();
                        }
                    }else {
//                        pinDialog.dismiss();
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==VerifyAccount){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        getSenderDetail(GetSenderDetails);
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            paymentDialog.dismiss();
            getParentFragmentManager().popBackStack();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait");
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    private void openReceipt(final TransactionResponse senderResponse) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jct_money_receipt_dialog_rapipay);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final TextView beneTv= dialog.findViewById(R.id.beneTv);
        final TextView bankNameTv=  dialog.findViewById(R.id.bankNameTv);
        final TextView accountNoTv= dialog.findViewById(R.id.accountNoTv);
        final TextView ifscTv= dialog.findViewById(R.id.ifscTv);
        final TextView txnAmountTv=  dialog.findViewById(R.id.txnAmountTv);
        final TextView txnTypeTv= dialog.findViewById(R.id.txnTypeTv);

        final RecyclerView txnRecycleView=dialog.findViewById(R.id.txnRecycleView);

        final TextView errorTv= dialog.findViewById(R.id.errorTv);
        final LinearLayout contentLin= dialog.findViewById(R.id.contentLin);

        beneTv.setText(senderResponse.getBankDetails().get(0).getBeniName());
        bankNameTv.setText(senderResponse.getBankDetails().get(0).getBank());
        accountNoTv.setText(senderResponse.getBankDetails().get(0).getAccountNumber());
        txnAmountTv.setText(senderResponse.getBankDetails().get(0).getAmount()+"");
        txnTypeTv.setText(senderResponse.getBankDetails().get(0).getTxnType());
        ifscTv.setText(senderResponse.getBankDetails().get(0).getIfscCode());

        RapipayTxnDetailAdapter adapter=new RapipayTxnDetailAdapter(context, new RapipayTxnDetailAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<TransactionResponse.transactionDetails> list, int position) {
            }
        },senderResponse.getTransactionDetails());
        txnRecycleView.setAdapter(adapter);
        txnRecycleView.setLayoutManager(new LinearLayoutManager(context));

        dialog.findViewById(R.id.print_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void updateSenderDetails(final SenderDetailResponse senderResponse) {
        if(senderResponse.getBenificiaryDetailData().size()>0 && senderResponse.getBenificiaryDetailData().size()>=currentListItemPosition){
            senderResponse.getBenificiaryDetailData().get(currentListItemPosition).isVisible=true;
        }
        limitTv.setText("Rs. "+senderResponse.getRemainingLimit());
        senderDetailResponse.setRemainingLimit(senderResponse.getRemainingLimit());
        senderDetailResponse.getBenificiaryDetailData().clear();
        senderDetailResponse.getBenificiaryDetailData().addAll(senderResponse.getBenificiaryDetailData());
        recipientRecycleView.setAdapter(getAdapter(senderResponse.getBenificiaryDetailData()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;

            case R.id.addRecTv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(addRecTv);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Mobile", senderInfo.getMobile());
                bundle.putSerializable("commonParams", commonParams);
                RapipayAddBeneFragment fragment=new RapipayAddBeneFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
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
                    makeTransaction();
                }
                break;
            case R.id.tv1:
                Pin=Pin+1;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv2:
                Pin=Pin+2;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv3:
                Pin=Pin+3;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv4:
                Pin=Pin+4;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv5:
                Pin=Pin+5;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv6:
                Pin=Pin+6;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv7:
                Pin=Pin+7;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv8:
                Pin=Pin+8;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
                }
                break;
            case R.id.tv9:
                Pin=Pin+9;
                checkRadionButton(Pin);
                if(Pin.length()==4) {
                    makeTransaction();
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

    private void makeTransaction() {
        Common.hideSoftInputFromDialog(pinDialog,context);
        TransactionRequest requestModel=new TransactionRequest();
        requestModel.setAgentCode(loginModel.Data.DoneCardUser);
        requestModel.setSessionKey(commonParams.getSessionKey());
        requestModel.setSessionRefId(commonParams.getSessionRefNo());
        requestModel.setMobileNumber(senderInfo.getMobile());
        requestModel.setAmount(amount);
        requestModel.setAccountNumber(beneData.getAccountNumber());
        requestModel.setName(beneData.getAccountHolderName());
        requestModel.setBankName(beneData.getBankName());
        requestModel.setIFSC(beneData.getIfsc());
        requestModel.setBeniId(beneData.getBeneid());
        requestModel.setTransferType(TType);

//        responseHandler(null, Transaction);
        new NetworkCall().callRapipayServiceHeader(requestModel, ApiConstants.TransactionRapi, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, Transaction);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
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

