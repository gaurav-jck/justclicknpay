package com.justclick.clicknbook.Fragment.bus;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.HomeFragment;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusBookingResponseModel;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.Fragment.bus.busmodel.SeatMapBean;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusBookingRequestModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

/**
 * Created by priyanshi on 11/22/2017.
 */

public class BusConfirmFragment extends Fragment implements View.OnClickListener {
    Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private FragmentBackPressListener backPress;
    private ScrollView scrollView;
    private BusSearchBean busSearchBean;
    private BusBookingResponseModel busBookingResponseModel;
    private BusBookingRequestModel busBookingRequestModel;
    private ArrayList<SeatMapBean> selectedArray;
    private TextView bookingRefTv,busTypeTv, pnrTv,boardingPointTv,busNameTv,
            droppingPointTv,departureTv,arrivalTv,contactTv,childFareTv,
            grandTotalTv, adultFareTv,book_tv,dateTv;
    private TextView[] nameTv,detailTv;
    private ImageView backArrow;
    private LinearLayout linearAdd;
    private float totalpayable;
    private int boardingPointPosition,droppingPointPosition;
    int NoOfPassenger=1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        if(getArguments()!=null && getArguments().getSerializable("busSearchBean")!=null){
            selectedArray = (ArrayList<SeatMapBean>) getArguments().getSerializable("selectedArray");
            busSearchBean = (BusSearchBean) getArguments().getSerializable("busSearchBean");
            busBookingResponseModel = (BusBookingResponseModel) getArguments().getSerializable("busModel");
            busBookingRequestModel = (BusBookingRequestModel) getArguments().getSerializable("busBookingRequestModel");
            boardingPointPosition =  getArguments().getInt("boardingPointPosition");
            droppingPointPosition =  getArguments().getInt("droppingPointPosition");
            totalpayable =  getArguments().getFloat("totalpayable");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_bus_confirm, container, false);

        backArrow=  view.findViewById(R.id.backArrow);
        bookingRefTv=  view.findViewById(R.id.bookingRefTv);
        busTypeTv=  view.findViewById(R.id.busTypeTv);
        busNameTv= view.findViewById(R.id.busNameTv);
        pnrTv=  view.findViewById(R.id.pnrTv);
        boardingPointTv=  view.findViewById(R.id.boardingPointTv);
        droppingPointTv=  view.findViewById(R.id.droppingPointTv);
        departureTv=  view.findViewById(R.id.departureTv);
        arrivalTv=  view.findViewById(R.id.arrivalTv);
        contactTv=  view.findViewById(R.id.contactTv);
        childFareTv=  view.findViewById(R.id.childFareTv);
        grandTotalTv=  view.findViewById(R.id.grandTotalTv);
        adultFareTv=  view.findViewById(R.id.adultFareTv);
        book_tv= view.findViewById(R.id.book_tv);
        dateTv=  view.findViewById(R.id.dateTv);
        linearAdd=  view.findViewById(R.id.linearAdd);
        book_tv.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        bookingRefTv.setText(busBookingResponseModel.ReservationID);
        pnrTv.setText(busBookingResponseModel.PNR);
        busTypeTv.setText(busSearchBean.bustype);
        busNameTv.setText(busSearchBean.travelName);

//        dateTv.setText(Common.formatBusTime(busSearchBean.doj.substring(0,10)));
        dateTv.setText(busSearchBean.doj.substring(0,10));
        boardingPointTv.setText(busSearchBean.boardingTimes.get(boardingPointPosition).address);
        droppingPointTv.setText(busSearchBean.droppingTimes.get(droppingPointPosition).address);
        departureTv.setText(MyPreferences.getBusFromCity(context).substring(0,MyPreferences.getBusFromCity(context).indexOf("(")));
        arrivalTv.setText(MyPreferences.getBusToCity(context).substring(0,MyPreferences.getBusToCity(context).indexOf("(")));
        contactTv.setText(busBookingRequestModel.BLReq.inventoryItems.get(0).passenger.get(0).mobile+"");

        childFareTv.setText(busSearchBean.fareDetails.get(0).childFare+"");
        grandTotalTv.setText(Common.roundOffDecimalValue(totalpayable)+"");
        setDynamicPaxInfo();
        adultFareTv.setText(busSearchBean.fareDetails.get(0).baseFare+"");

        scrollView= (ScrollView) view.findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

            }
        });
        return view;
    }

    private void setDynamicPaxInfo() {
        NoOfPassenger=busBookingRequestModel.BLReq.inventoryItems.size();
        nameTv=new TextView[NoOfPassenger];
        detailTv=new TextView[NoOfPassenger];

        for(int i = 0; i< NoOfPassenger; i++) {
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.bus_pax_info, linearAdd, false);

            nameTv[i] = wizard.findViewById(R.id.nameTv);
            detailTv[i] = wizard.findViewById(R.id.detailTv);
            nameTv[i].setText(busBookingRequestModel.BLReq.inventoryItems.get(i).passenger.get(0).name+"");
            detailTv[i].setText("Seat No. "+busBookingRequestModel.BLReq.inventoryItems.get(i).seatName);

            linearAdd.addView(wizard);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            backPress= (FragmentBackPressListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backArrow:
//                ((NavigationDrawerActivity)context).replaceFragment(new BusSearchFragment());
//                while(!(((NavigationDrawerActivity)context).getSupportFragmentManager().findFragmentById(R.id.container) instanceof BusListFragment)){
//                    getFragmentManager().popBackStack();
//                }
//                backPress.onBusBackPress(false);
                getFragmentManager().popBackStack();
                break;
            case R.id.book_tv:
//                while(!(getFragmentManager().findFragmentById(R.id.container) instanceof BusSearchFragment)){
//                    getFragmentManager().popBackStack();
//                }
//                backPress.onBusBackPress(true);
                ((NavigationDrawerActivity)context).replaceFragment(new HomeFragment());
                break;
        }

    }
}
