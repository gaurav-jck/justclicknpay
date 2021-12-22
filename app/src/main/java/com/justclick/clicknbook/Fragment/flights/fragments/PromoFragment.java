package com.justclick.clicknbook.Fragment.flights.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.flights.MyCustomDialog;
import com.justclick.clicknbook.Fragment.flights.responseModel.ApplyPromoCodeResponse;
import com.justclick.clicknbook.Fragment.flights.responseModel.PromoCodeResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.CurrencyCode;
import com.justclick.clicknbook.utils.DecodeLoginToken;
import com.justclick.clicknbook.utils.InternetConnectionOffDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import okhttp3.ResponseBody;

public class PromoFragment extends Fragment {
    private final int APPLY=0, REMOVE=1, PROMO_ERROR=2;
    private OnPromoListener promoListener;
    private Context context;
    //    promo views
    private EditText promoCodeEdt;
    private LinearLayout promoLin, promoContainerLin;
    private RelativeLayout promoListSelectionRel;
    private TextView promoApplyEdtTv,promoRemoveFromListTv,
            promoAppliedNameTv, promoAppliedDescriptionTv;

    private String SessionId, srvcId, srvcCatId;
    private String amount;

    public interface OnPromoListener {
        void onFragmentInteraction(int type, ApplyPromoCodeResponse senderResponse);
    }

    public PromoFragment(){

    }

    public PromoFragment(OnPromoListener promoListener, String amount, String sessionId, String srvcId, String srvcCatId) {
        // Required empty public constructor
        this.promoListener=promoListener;
        this.amount=amount;
        this.SessionId=sessionId;
        this.srvcId=srvcId;
        this.srvcCatId=srvcCatId;
    }

    // TODO: Rename and change types and number of parameters
    public static PromoFragment newInstance(String param1, String param2) {
        PromoFragment fragment = new PromoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view= inflater.inflate(R.layout.fragment_promo, container, false);
        //        promo code
        promoLin=view.findViewById(R.id.promoLin);
        promoContainerLin=view.findViewById(R.id.promoContainerLin);
        promoListSelectionRel=view.findViewById(R.id.promoListSelectionRel);
        promoCodeEdt=view.findViewById(R.id.promoCodeEdt);
        promoAppliedNameTv =view.findViewById(R.id.promoAppliedNameTv);
        promoAppliedDescriptionTv =view.findViewById(R.id.promoAppliedDescriptionTv);
        promoRemoveFromListTv =view.findViewById(R.id.promoRemoveFromListTv);
        promoApplyEdtTv =view.findViewById(R.id.promoApplyEdtTv);
        promoRemoveFromListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePromoCodeFromList();
            }
        });
        promoApplyEdtTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.checkInternetConnection(context)){
                    if(promoCodeEdt.getText().toString().trim().length()==0){
                        Toast.makeText(context, "Please enter any promo/coupon.", Toast.LENGTH_SHORT).show();
                    }else {
                        applyPromoCode(promoCodeEdt.getText().toString().trim(), amount, "");
                    }
                }
                else {
                    showInternetCustomDialog();
                }
            }
        });
        defaultPromoView();
        return view;
    }

    private void defaultPromoView() {
        if(MyPreferences.isUserLogin(context)){
            showPromoView();
        }else {
            hidePromoView();
        }
    }

    private void hidePromoView() {
        promoLin.setVisibility(View.GONE);
    }

    private void showPromoView() {
        promoListSelectionRel.setVisibility(View.GONE);
        getPromoCode();
    }

    private void removePromoCodeFromList() {
        showCustomDialog();
        new NetworkCall().deleteServiceWithHeader(ApiConstants.RemovePromoCode+SessionId+"/"+srvcId,"", context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        hideCustomDialog();
                        if(responseCode==200){
                            promoListSelectionRel.setVisibility(View.GONE);

//                            promoCodeRelative.setVisibility(View.GONE);
//                            tv_total_price.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+" "
//                                    +totalFare+"");
//                            totalFareTv.setText(CurrencyCode.getCurrencySymbol(flightResponse.fare.currency, context)+" "
//                                    +totalFare+"");
//                            discountedTotalFare=totalFare;
                            for(int i=0; i<promoContainerLin.getChildCount(); i++){
                                ((RadioButton)promoContainerLin.getChildAt(i).findViewById(R.id.radioButtonPromo)).setChecked(false);
                            }
                            promoListener.onFragmentInteraction(REMOVE, null);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getPromoCode() {
//        showCustomDialog();
        new NetworkCall().checkHotelTransactions(ApiConstants.GetPromoList+"srvcId="+srvcId+"&srvcCatId="+srvcCatId, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
//                            hideCustomDialog();
                            try {
                                getPromoCodeResponseHandler(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getPromoCodeResponseHandler(ResponseBody response) {
        try {
            PromoCodeResponse senderResponse = new Gson().fromJson(response.string(), PromoCodeResponse.class);
            if(senderResponse!=null){
                if(senderResponse.status ==200) {
                    promoLin.setVisibility(View.VISIBLE);
                    showPromoList(senderResponse);
                } else {
//                    showNoResultCustomDialog();
//                    Toast.makeText(context,senderResponse.error,Toast.LENGTH_SHORT).show();
                }
            }else {
//                showNoResultCustomDialog();
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
//            showOopsCustomDialog();
//            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPromoList(final PromoCodeResponse promoResponse) {
//        LinearLayout promoLinear= cancelDialog.findViewById(R.id.promoLinear);
        for (int i=0;i<promoResponse.response.promoCodeDetails.size();i++){
            View promoItem = getLayoutInflater().inflate(R.layout.promo_dialog_item, null);
            final RadioButton radioButtonPromo = promoItem.findViewById(R.id.radioButtonPromo);
            TextView promoNameTv = promoItem.findViewById(R.id.promoNameTv);
            TextView promoDetailTv = promoItem.findViewById(R.id.promoDetailTv);
            promoNameTv.setText(promoResponse.response.promoCodeDetails.get(i).promoCodeName);
            promoDetailTv.setText(promoResponse.response.promoCodeDetails.get(i).description);
            final int finalI = i;
            radioButtonPromo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        for(int j=0; j<promoContainerLin.getChildCount(); j++){
                            if(finalI!=j){
                                ((RadioButton)promoContainerLin.getChildAt(j).findViewById(R.id.radioButtonPromo)).setChecked(false);
                            }
                        }
                        applyPromoCode(promoResponse.response.promoCodeDetails.get(finalI).promoCodeName+"", amount+"",
                                promoResponse.response.promoCodeDetails.get(finalI).description);
                    }
                }
            });
            promoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioButtonPromo.setChecked(true);
                }
            });
            promoContainerLin.addView(promoItem);
        }
    }

    private void applyPromoCode(String promoCode, String amount, final String description) {
        showCustomDialog();
        MyCustomDialog.setDialogMessage("Please wait...");
//        String url=ApiConstants.ApplyPromoCode+promocode+"&tripType=1&sessionId=" +SessionId+
//                "&billAmount="+totalFare+"&srvcId=1&srvcCatId="+(flightSearchRequestModel.isDomestic?1:2)+"&channel=m";
        String url= ApiConstants.ApplyPromoCode+ promoCode+
                "&tripType=1"+
                "&billAmount="+amount+
                "&email="+ DecodeLoginToken.getLoginEmail(context)+
                "&channel=M" +
                "&srvcId=" +srvcId+
                "&srvcCatId="+srvcCatId+
                "&sessionId="+SessionId +
                "&serviceTypeId="+srvcId;
        new NetworkCall().checkHotelTransactions(url, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            hideCustomDialog();
                            try {
                                applyPromoCodeResponseHandler(response, description);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void applyPromoCodeResponseHandler(ResponseBody response, String description) {
        try {
            ApplyPromoCodeResponse senderResponse = new Gson().fromJson(response.string(), ApplyPromoCodeResponse.class);
            if(senderResponse!=null){
                if(senderResponse.status ==200 ) {

                    promoListSelectionRel.setVisibility(View.VISIBLE);
                    promoAppliedNameTv.setText(senderResponse.response.promoCode);
                    promoAppliedDescriptionTv.setText("Congratulations! your promocode has been successfully applied, you will be getting "+
                            CurrencyCode.getCurrencySymbol("INR", context)+senderResponse.response.redeemAmount+" discount!");
                    promoListener.onFragmentInteraction(APPLY, senderResponse);
                } else {
//                    showNoResultCustomDialog();
//                    promoCodeRelative.setVisibility(View.GONE);
                    for(int i=0; i<promoContainerLin.getChildCount(); i++){
                        ((RadioButton)promoContainerLin.getChildAt(i).findViewById(R.id.radioButtonPromo)).setChecked(false);
                    }
//                    Toast.makeText(context,senderResponse.message,Toast.LENGTH_SHORT).show();
                    promoListSelectionRel.setVisibility(View.VISIBLE);
                    promoAppliedNameTv.setText("");
                    promoAppliedDescriptionTv.setText(senderResponse.message);
                    promoListener.onFragmentInteraction(PROMO_ERROR, senderResponse);
                }
            }else {
//                showNoResultCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
//            showOopsCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }
    private void showInternetCustomDialog() {
        InternetConnectionOffDialog.getInstance().showCustomDialog(context);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        promoListener = null;
    }
}
