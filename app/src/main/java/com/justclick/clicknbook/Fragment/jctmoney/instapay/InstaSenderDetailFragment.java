package com.justclick.clicknbook.Fragment.jctmoney.instapay;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.AddBeneOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.GetSenderInstaRequest;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaDeleteBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.instapay.model.InstaTransactionRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.AddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.TransactionRequest;
import com.justclick.clicknbook.Fragment.jctmoney.response.CheckCredentialResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.SenderDetailResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.TransactionOtpResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.TransactionResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.ValidateAccountResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.databinding.FragmentInstaSenderDetailBinding;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.Words;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import okhttp3.ResponseBody;


public class InstaSenderDetailFragment extends Fragment implements View.OnClickListener {
    private final int GetSenderDetails=0,DeleteRecipient=1,Transaction=2, VerifyAccount=4, AddBene=5, TxnOtp=6,
            DeleteRecipientOtpVerify=7;
//    private View view;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentInstaSenderDetailBinding binding;
    private LoginModel loginModel;
    private SenderDetailResponse senderDetailResponse;
    private SenderDetailResponse.senderDetailInfo senderInfo;
    private SenderDetailResponse.benificiaryDetailData beneData;
    private RapipayRecipientListAdapter adapter;
    private int amount;
    final String IMPS="IMPS", NEFT="NEFT";
    private String TType=IMPS,Pin="", bankName;
    private Dialog paymentDialog, pinDialog;
    private int currentListItemPosition=0;
    private boolean isGetDetail=false;
    private float RemainingLimt;
    private CheckCredentialResponse.credentialData commonParams;
    private String transactionId, stateresp, jckRefId, OTP;

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
        if(binding==null) {
            // Inflate the layout for this fragment
            binding = FragmentInstaSenderDetailBinding.inflate(getLayoutInflater());
            if (getArguments().getSerializable("senderResponse") != null) {
                senderDetailResponse = (SenderDetailResponse) getArguments().getSerializable("senderResponse");
                commonParams = (CheckCredentialResponse.credentialData) getArguments().getSerializable("commonParams");
                bankName = getArguments().getString("bankName");
                senderInfo = senderDetailResponse.getSenderDetailInfo().get(0);
            }
            try {
                initializeViews();
            } catch (Exception e) {
                Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
            }
            RadioGroup r=new RadioGroup(context);
            r.setOnCheckedChangeListener((radioGroup, i) -> {

            });
            getIpAddress();
        }
        return binding.getRoot();
    }

    private void initializeViews() {
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        Typeface face = Common.TextViewTypeFace(context);
        binding.backArrow.setOnClickListener(this);

        binding.senderNameTv.setText(senderInfo.getName());
        binding.senderMobileTv.setText(senderInfo.getMobile());

        binding.limitTv.setText("Rs. "+senderDetailResponse.getRemainingLimit()+"");

        if(senderDetailResponse.getBenificiaryDetailData()!=null){
            binding.recipientRecycleView.setAdapter(getAdapter(senderDetailResponse.getBenificiaryDetailData()));
            binding.recipientRecycleView.setLayoutManager(new LinearLayoutManager(context));
        }else {
            binding.recipientRecycleView.setAdapter(getAdapter( new ArrayList<SenderDetailResponse.benificiaryDetailData>()));
            binding.recipientRecycleView.setLayoutManager(new LinearLayoutManager(context));
        }

        if(bankName.equalsIgnoreCase("bank1")){
            binding.dmtTypeTv.setText("DMT-1");
        }else if(bankName.equalsIgnoreCase("bank2")){
            binding.dmtTypeTv.setText("DMT-2");
        }else {
            binding.dmtTypeTv.setText("DMT-3");
        }

        binding.addRecTv.setOnClickListener(this);

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
                        verifyAccount(list.get(position), ApiConstants.ValidateAccount, VerifyAccount);
                        currentListItemPosition=position;
                    }
                }else {
                    openDeleteConfirmationDialog("Confirm Delete Request","Please confirm," +
                            " you want to delete this beneficiary.","Cancel","Delete", list.get(position));
                }
            }
        },benificiaryDetailData,senderInfo.getMobile());
    }

    private void verifyAccount(SenderDetailResponse.benificiaryDetailData beneData, String method, int type) {
        AddBeneRequest requestModel=new AddBeneRequest();
        requestModel.setAgentCode(loginModel.Data.DoneCardUser);
        requestModel.setSessionKey(commonParams.getSessionKey());
        requestModel.setMode("App");
        requestModel.setSessionRefId(commonParams.getSessionRefNo());
        requestModel.setBankName(beneData.getBankName());
        requestModel.setBankId(beneData.getBankid());
        requestModel.setAccountHolderName(beneData.getAccountHolderName());
        requestModel.setAccountNumber(beneData.getAccountNumber());
        requestModel.setIfscCode(beneData.getIfsc());
        requestModel.setMobile(senderInfo.getMobile());
        requestModel.setApiService("");  // new change
        requestModel.setAddress(commonParams.address);  // new change
        requestModel.setPinCode(commonParams.pinCode);  // new change
        requestModel.setState(commonParams.state);  // new change
        requestModel.setCity(commonParams.city);  // new change
        requestModel.setStatecode(commonParams.statecode);  // new change
        requestModel.setGst_state(commonParams.statecode);  // new change
        requestModel.setBene_id(beneData.getBeneid());  // new change
        requestModel.verified="1";  // new change


        new NetworkCall().callRapipayServiceHeader(requestModel, method, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandlerVerify(response, type, beneData);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private void responseHandlerVerify(ResponseBody response, int type, SenderDetailResponse.benificiaryDetailData beneData) {
        if(type==VerifyAccount){
            try {
                ValidateAccountResponse senderResponse = new Gson().fromJson(response.string(), ValidateAccountResponse.class);
                Toast.makeText(context,response.string(),Toast.LENGTH_LONG).show();
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        beneData.setAccountHolderName(senderResponse.beneficiaryName);
                        verifyAccount(beneData, ApiConstants.AddBenificiary, AddBene);
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                Toast.makeText(context,response.string(),Toast.LENGTH_LONG).show();
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
            }catch (Exception e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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
                deleteBene(listItem, DeleteRecipient);
//                Toast.makeText(context, "Delete Ben", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private String beneId;
    private void deleteBene(SenderDetailResponse.benificiaryDetailData listItem, final int deleteRecipient) {
        beneId=listItem.getBeneid();
        InstaDeleteBeneRequest request =new InstaDeleteBeneRequest();
        request.BeneId=beneId;
        request.AgentCode=loginModel.Data.DoneCardUser;
        request.mobile=senderInfo.getMobile();
        request.IPAddress=ip;

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.DeleteBenificiary, request,
                        commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, deleteRecipient);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void openDialog(final SenderDetailResponse senderDetailResponse) {
        TType=IMPS;
        RemainingLimt=senderDetailResponse.getRemainingLimit();
        paymentDialog = new Dialog(context);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.insta_paynow_dialog);
        final Window window= paymentDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        EditText amountEdt= paymentDialog.findViewById(R.id.amountEdt);
        TextView amountWordsTv= paymentDialog.findViewById(R.id.amountWordsTv);
        final EditText otpEdt=  paymentDialog.findViewById(R.id.otpEdt);
        final TextView cancelTv= paymentDialog.findViewById(R.id.cancelTv);
        final TextView otpTv=  paymentDialog.findViewById(R.id.otpTv);
        final TextView payNowTv=  paymentDialog.findViewById(R.id.payNowTv);
        final TextView limitTv= paymentDialog.findViewById(R.id.limitTv);
        final TextView transactionTypeIMPSTv=  paymentDialog.findViewById(R.id.transactionTypeIMPSTv);
        final TextView transactionTypeNEFTTv=  paymentDialog.findViewById(R.id.transactionTypeNEFTTv);

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

        amountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    final long number = Long.parseLong(charSequence.toString());
//                    String returnz = Words.convert(number);
                    String returnz = Words.convertToIndianCurrency(charSequence.toString());
                    amountWordsTv.setText(returnz);
                } catch ( NumberFormatException e) {
                    amountWordsTv.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        limitTv.setText("Remaining limit is: "+senderDetailResponse.getRemainingLimit()+"");

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentDialog.cancel();
            }
        });

        otpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isdecimalvalid(amountEdt.getText().toString().trim())){
                    amount= Integer.parseInt(amountEdt.getText().toString());
                    if(amount>RemainingLimt  ){
                        Toast.makeText(context, "Please enter amount less than your limit", Toast.LENGTH_SHORT).show();
                    }else if( amount<100 || amount>5000){
                        amountEdt.setError(" " );
                        Toast.makeText(context, "Please enter amount between 100 to 5000", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        amountEdt.setEnabled(true);
//                        paymentDialog.dismiss();
                        getTransactionOtp();
//                        openTxnConfirmationDialogNew(amount,beneData.getAccountNumber(),beneData.getAccountHolderName());
                    }
                }else {
                    Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
                }


            }
        });
        payNowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isdecimalvalid(amountEdt.getText().toString().trim())){
                    OTP=otpEdt.getText().toString();
                    if(OTP.isEmpty()){
                        Toast.makeText(context, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    }else if( OTP.length()<4){
                        Toast.makeText(context, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        paymentDialog.dismiss();
                        openTxnConfirmationDialogNew(amount,beneData.getAccountNumber(),beneData.getAccountHolderName());
                    }
                }else {
                    Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_SHORT).show();
                }


            }
        });

        paymentDialog.show();
    }

    private void openTxnConfirmationDialogNew(int amount,
                                           String account, String name) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dmt_txn_confirmation_dialog);

        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ((TextView) dialog.findViewById(R.id.amountTv)).setText(amount+"");
        ((TextView) dialog.findViewById(R.id.accountTv)).setText(account);
        ((TextView) dialog.findViewById(R.id.nameTv)).setText(name);
        TextView submitTv=dialog.findViewById(R.id.submit_tv);

        dialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.preventFrequentClick(submitTv);
                dialog.dismiss();
                makeTransaction();
            }
        });

        dialog.show();
    }

    public void getSenderDetail(final int type) {
        GetSenderInstaRequest senderRequestModel=new GetSenderInstaRequest();
        senderRequestModel.Mobile=senderInfo.getMobile();
        senderRequestModel.AgentCode=loginModel.Data.DoneCardUser;
        senderRequestModel.Latitude="19.1641988";
        senderRequestModel.Longitude="72.8626135";
        senderRequestModel.BankName="Bank1";
        senderRequestModel.IPAddress=ip;
        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.SenderDetail,
                        senderRequestModel, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, GetSenderDetails);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            hideCustomDialog();
            if(TYPE==GetSenderDetails){
                SenderDetailResponse senderResponse = new Gson().fromJson(response.string(), SenderDetailResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        commonParams.setSessionKey(senderResponse.getSessionKey());
                        updateSenderDetails(senderResponse);
//                        Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==DeleteRecipient){
                AddBeneOtpResponse senderResponse = new Gson().fromJson(response.string(), AddBeneOtpResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.statusCode.equals("00")) {
                        stateresp=senderResponse.stateResp;
                        deleteOtpDialog(senderResponse.statusMessage);
                    } else {
                        Toast.makeText(context,senderResponse.statusMessage,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==DeleteRecipientOtpVerify){
                CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                        getSenderDetail(GetSenderDetails);
                        deleteOtpDialog.dismiss();
                    } else {
                        Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }else if(TYPE==TxnOtp){
                TransactionOtpResponse senderResponse = new Gson().fromJson(response.string(), TransactionOtpResponse.class);
                if(senderResponse!=null){
                    if(senderResponse.getStatusCode().equals("00")) {
                        Toast.makeText(context,senderResponse.data.txnMessage,Toast.LENGTH_LONG).show();
                        EditText otpEdt=paymentDialog.findViewById(R.id.otpEdt);
                        TextView payNowTv=paymentDialog.findViewById(R.id.payNowTv);
                        TextView otpTv=paymentDialog.findViewById(R.id.otpTv);
                        otpEdt.setVisibility(View.VISIBLE);
                        otpTv.setVisibility(View.GONE);
                        payNowTv.setVisibility(View.VISIBLE);
                        transactionId=senderResponse.data.transactionId;
                        stateresp=senderResponse.data.stateresp;
                        jckRefId=senderResponse.data.jckRefId;
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

    private void deleteOtpDialog(String message){
        Dialog otpDialog = new Dialog(context);
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setContentView(R.layout.change_pass_otp_layout);
        otpDialog.setCancelable(false);
        final EditText otpEdt1= otpDialog.findViewById(R.id.otpEdt1);
        final TextView otpErrorTv= otpDialog.findViewById(R.id.otpErrorTv);
        final Button submit= (Button) otpDialog.findViewById(R.id.submit_btn);
        ImageButton dialogCloseButton = (ImageButton) otpDialog.findViewById(R.id.close_btn);

        otpErrorTv.setText(message);
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
                    if(otp.length()<6) {
                        Toast.makeText(context,R.string.empty_and_invalid_otp,Toast.LENGTH_LONG).show();
//                        otpErrorTv.setVisibility(View.INVISIBLE);
                    }else {
                        deleteBeneOtpVerify(otp, otpDialog, DeleteRecipient);
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
    Dialog deleteOtpDialog;
    private void deleteBeneOtpVerify(String otp, Dialog otpDialog, final int deleteRecipient) {
        deleteOtpDialog=otpDialog;
        InstaDeleteBeneRequest request =new InstaDeleteBeneRequest();
        request.BeneId=beneId;
        request.AgentCode=loginModel.Data.DoneCardUser;
        request.mobile=senderInfo.getMobile();
        request.IPAddress=ip;
        request.StateResp=stateresp;
        request.OTP=otp;

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.DeleteBenificiaryVerifyOTP,
                        request,
                        commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, DeleteRecipientOtpVerify);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

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
        binding.limitTv.setText("Rs. "+senderResponse.getRemainingLimit());
        senderDetailResponse.setRemainingLimit(senderResponse.getRemainingLimit());
        senderDetailResponse.getBenificiaryDetailData().clear();
        senderDetailResponse.getBenificiaryDetailData().addAll(senderResponse.getBenificiaryDetailData());
        binding.recipientRecycleView.setAdapter(getAdapter(senderResponse.getBenificiaryDetailData()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;

            case R.id.addRecTv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                Common.preventFrequentClick(binding.addRecTv);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Mobile", senderInfo.getMobile());
                bundle.putSerializable("commonParams", commonParams);
                InstaAddBeneFragment fragment=new InstaAddBeneFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                break;
        }
    }

    InstaTransactionRequest transactionRequest;
    private void getTransactionOtp() {
        Common.hideSoftInputFromDialog(pinDialog,context);
        transactionRequest=new InstaTransactionRequest();
        transactionRequest.AgentCode=loginModel.Data.DoneCardUser;
        transactionRequest.MobileNumber=senderInfo.getMobile();
        transactionRequest.Amount=amount;
        transactionRequest.AccountNumber=beneData.getAccountNumber();
        transactionRequest.Name=beneData.getAccountHolderName();
        transactionRequest.Bankid="0";
        transactionRequest.BankName=beneData.getBankName();
        transactionRequest.IFSC=beneData.getIfsc();
        transactionRequest.BeneId=beneData.getBeneid();
        transactionRequest.TransferType=TType;
        transactionRequest.Latitude="28.12322";
        transactionRequest.Longitude="77.12322";
        transactionRequest.stateResp=senderInfo.getStateResp();
        transactionRequest.IPAddress=ip;

        String json = new Gson().toJson(transactionRequest);

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.transactionotp,
                        transactionRequest, commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, TxnOtp);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

        Pin="";
    }

    private void makeTransaction() {
        Common.hideSoftInputFromDialog(pinDialog,context);

        transactionRequest.OTP=OTP;
        transactionRequest.Transactionid=transactionId;
        transactionRequest.stateResp=stateresp;
        transactionRequest.JCKTransactionid=jckRefId;

        String json = new Gson().toJson(transactionRequest);

        new NetworkCall().callService(NetworkCall.getDmtInstaApiInterface().getDmtInstaHeader(ApiConstants.TransactionRapi, transactionRequest,
                        commonParams.getUserData(), "Bearer "+commonParams.getToken()),
                context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, Transaction);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });

        Pin="";
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

