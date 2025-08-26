package com.justclick.clicknbook.adapter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.HomeFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MenuCodes;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class MenuSubItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context context;
    private Fragment agentSearchFragment;
    private ArrayList<LoginModel.DataList.subMenu> arrayList;
    private OnRecyclerItemClickListener itemClickListener;
    private HomeFragment homeFragment;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view,  ArrayList<LoginModel.DataList.subMenu> list, int position);
    }

    public MenuSubItemsAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<LoginModel.DataList.subMenu> arrayList, HomeFragment homeFragment) {
        this.context = context;
        this.arrayList=arrayList;
        itemClickListener= fragment;
        this.homeFragment=homeFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_item_show_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            if (holder!=null && holder instanceof MyViewHolder) {
                final String subMenuCode = arrayList.get(position).SubMenuCode;
                MyViewHolder vh = (MyViewHolder) holder;

//                DisplayMetrics displaymetrics = new DisplayMetrics();
//                ((NavigationDrawerActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//                int screenWidth = displaymetrics.widthPixels;
//                int screenHeight = displaymetrics.heightPixels;
//
//                vh.linear_sub_menu.requestLayout();
//                vh.linear_sub_menu.setLayoutParams(new LinearLayout.LayoutParams(screenWidth/3, ViewGroup.LayoutParams.WRAP_CONTENT));

                Typeface face = Common.homeSubMenuItemTypeFace(context);
                vh.sub_item_title.setTypeface(face);

                vh.sub_item_title.setText(arrayList.get(position).SubMenu);

                ColorFilter appColor = new LightingColorFilter(context.getResources().getColor(R.color.app_blue_color),
                        context.getResources().getColor(R.color.app_blue_color) );

                if(subMenuCode.equalsIgnoreCase(MenuCodes.UpdateCredit)) {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.update_credit_home_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_update_credit);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CreditList)) {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.deposit_update_list_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.DepositList)) {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.deposit_update_list_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
//                   vh.imageView_sub_item.setImageResource(R.drawable.mobile3);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.DepositUpdateRequest)) {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.deposit_update_request_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.deposit_update_request_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CreditUpdateRequest)) {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.credit_update_request_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
//                    vh.imageView_sub_item.setImageResource(R.drawable.mobile5);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentCreditRequestFragment)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.credit_update_request_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentDepositRequestFragment)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.deposit_update_request_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.deposit_update_request_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TrainBookingcheck)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.train_home_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.train_home_vector);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.MobileFragment)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_icon_mobile_payment);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.FlightSearch)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.flight_blue );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_flight_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.BusSearch)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.flight_blue );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_bus_icon_blue);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.HotelSearch)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.hotel_home_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.hotel_home_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.DMT)
                        || subMenuCode.equalsIgnoreCase(MenuCodes.DMT2) || subMenuCode.equalsIgnoreCase(MenuCodes.DMT3)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_money_transfer);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CASH_OUT)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_money_transfer);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.LIC)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_life_insurance);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.PAYTM)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.account_balance_wallet_home);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CASHFREE_QR)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_qr_code_home);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.DYNAMIC_QR)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_qr_code_home);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.BILL_PAY)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_icon_mobile_payment);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AirSalesReport)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AirRefundReport)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AirCancellationReport)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AEPS)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AEPS_OLD)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.MATM)){
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CREDOPAY)){
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_cash_payout_border);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.FAST_TAG)){
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_icon_mobile_payment);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.CREDIT)){
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_credit_card_home);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TRAIN)){
                    vh.imageView_sub_item.setImageResource(R.drawable.train_home_vector);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TRAIN_TENT)){
                    vh.imageView_sub_item.setImageResource(R.drawable.tent_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TrainBookingList)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TopUpDetails)){
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.NetSalesReport)){
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_net_sales_report);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.SalesAccountStatement)){
                    vh.imageView_sub_item.setImageResource(R.drawable.sales_account_statement_big);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.ApproveAgent)){
                    vh.imageView_sub_item.setImageResource(R.drawable.agent_verification_big);
                }
                else {
//                    Drawable homeIcon = context.getResources().getDrawable( R.drawable.mobile_recharge_icon );
//                    homeIcon.setColorFilter(appColor);
//                    vh.imageView_sub_item.setImageDrawable(homeIcon);
                    vh.imageView_sub_item.setImageResource(R.drawable.ic_clipboards);
                }


                /*if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentSearchFragment)) {
                    vh.imageView_sub_item.setImageResource(R.drawable.update_credit);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentCreditListFragment)) {
                    vh.imageView_sub_item.setImageResource(R.drawable.credit_list_icon_new);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentDepositListFragment)) {
                    vh.imageView_sub_item.setImageResource(R.drawable.credit_list_icon_new);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AdminDepositReportFragment)) {
                    vh.imageView_sub_item.setImageResource(R.drawable.credit_list_icon_new);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AdminCreditReportFragment)) {
                    vh.imageView_sub_item.setImageResource(R.drawable.credit_list_icon_new);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentCreditRequestFragment)){
                    vh.imageView_sub_item.setImag eResource(R.drawable.agent_credit_request_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.AgentDepositRequestFragment)){
                    vh.imageView_sub_item.setImageResource(R.drawable.deposit_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.TrainBookingCheckFragment)){
                    vh.imageView_sub_item.setImageResource(R.drawable.train_booking_check_icon);
                }else if(subMenuCode.equalsIgnoreCase(MenuCodes.MobileFragment)){
                    vh.imageView_sub_item.setImageResource(R.drawable.mobile_icon);
                }*/

                vh.sub_item_title.setHorizontalScrollBarEnabled(true);
                vh.imageView_sub_item.setHorizontalScrollBarEnabled(true);
                vh.linear_sub_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        itemClickListener.onRecyclerItemClick(v,arrayList,position);
//                        Toast.makeText(context,"selected",Toast.LENGTH_SHORT).show();
                        homeFragment.sendMenuCode(subMenuCode);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sub_item_title;
        public ImageView imageView_sub_item;
        private LinearLayout linear_sub_menu;

        public MyViewHolder(View view) {
            super(view);
            sub_item_title = (TextView) view.findViewById(R.id.sub_item_title);
            imageView_sub_item = (ImageView) view.findViewById(R.id.imageView_sub_item);
            linear_sub_menu= (LinearLayout) view.findViewById(R.id.linear_sub_menu);
        }
    }


}