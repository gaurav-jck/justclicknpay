package com.justclick.clicknbook.Fragment.bus;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.Fragment.bus.busmodel.SeatMapBean;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

public class BusSeatFragment extends Fragment implements View.OnClickListener{
    private View view;
    private final String ONE_WAY="ONE", ROUND_TRIP="ROUND";
    private ToolBarTitleChangeListener titleChangeListener;
    private Context context;
    private ArrayList<SeatMapBean> seatMapArrayList, arrayListOneLower, arrayListTwoLower;
    private ArrayList<BusSearchBean> boardingArrayList;
    private BusSearchBean busSearchBean;
    ArrayList<SeatMapBean> selectedArrayOneLower, selectedArrayTwoLower;
    private SeatMapBean seatMapBean;
    private RecyclerView recyclerView;
    int totalPageCount=0;
    private ImageView backArrow,driverImg;
    private TextView fromToTv,busNameTv,busDetailTv,lowerSeatLin,upperSeatLin,availabilityTv,
            selectedTv,occupiedTv,ladiesTv, oneWayTv, roundWayTv,seatNumberTv, totalPriceTv,continueTv;
    private LinearLayout SeatLin, busSeatLinTwoLower,busSeatLinTwoUpper;
    private RelativeLayout  busSeatLinOneLower, busSeatLinOneUpper;
    private int seatWidth, seatHeight, sleeperHeight;
    private String tripType=ONE_WAY;
    private ObjectAnimator objectAnimatorOneWay, objectAnimatorRound,objectAnimatorLower, objectAnimatorUpper;
    float price;
    int row=0,column=0;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        arrayListOneLower =new ArrayList<>();
        arrayListTwoLower =new ArrayList<>();
        seatMapBean=new SeatMapBean();
        seatWidth= (int) getDpToPx(40);
        seatHeight= (int) getDpToPx(40);
        sleeperHeight= (int) getDpToPx(80);

        if(getArguments().getSerializable("BusSeat")!=null) {
            arrayListOneLower = (ArrayList<SeatMapBean>) getArguments().getSerializable("BusSeat");
            arrayListTwoLower = (ArrayList<SeatMapBean>) getArguments().getSerializable("BusSeat");
            busSearchBean = (BusSearchBean) getArguments().getSerializable("BusSearchBean");
//            tripType = getArguments().getString("tripType");
        }
        if(getArguments().getSerializable("List")!=null) {
            boardingArrayList = (ArrayList<BusSearchBean>) getArguments().getSerializable("List");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
             view = inflater.inflate(R.layout.fragment_bus_seat, container, false);
            recyclerView =  view.findViewById(R.id.recyclerView);
            oneWayTv =  view.findViewById(R.id.oneWayTv);
            roundWayTv =  view.findViewById(R.id.roundWayTv);
            continueTv =  view.findViewById(R.id.continueTv);
            lowerSeatLin =  view.findViewById(R.id.lowerSeatLin);
            upperSeatLin =  view.findViewById(R.id.upperSeatLin);
            titleChangeListener.onToolBarTitleChange(getString(R.string.busSearchFragmentTitle));

            initializeAnimations();
            initializeViews(view);
            setFont();
        }
            return view;
    }

    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace2(context);
        fromToTv.setTypeface(face);
        busNameTv.setTypeface(face);
        busDetailTv.setTypeface(face);
        availabilityTv.setTypeface(face);
        selectedTv.setTypeface(face);
        occupiedTv.setTypeface(face);
        ladiesTv.setTypeface(face);
        continueTv.setTypeface(face2);
        seatNumberTv.setTypeface(face);
        totalPriceTv.setTypeface(face);
    }

    private void initializeViews(View view) {
        driverImg=  view.findViewById(R.id.driverImg);
        backArrow=  view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        fromToTv=  view.findViewById(R.id.fromToTv);
        busNameTv=  view.findViewById(R.id.busNameTv);
        busDetailTv=  view.findViewById(R.id.busDetailTv);
        SeatLin=  view.findViewById(R.id.SeatLin);

        busSeatLinOneLower =  view.findViewById(R.id.busSeatLinOneLower);
        busSeatLinTwoLower =  view.findViewById(R.id.busSeatLinTwoLower);
        busSeatLinOneUpper =  view.findViewById(R.id.busSeatLinOneUpper);
        busSeatLinTwoUpper =  view.findViewById(R.id.busSeatLinTwoUpper);
        availabilityTv=  view.findViewById(R.id.availabilityTv);
        selectedTv=  view.findViewById(R.id.selectedTv);
        occupiedTv=  view.findViewById(R.id.occupiedTv);
        ladiesTv=  view.findViewById(R.id.ladiesTv);
        seatNumberTv=  view.findViewById(R.id.seatNumberTv);
        totalPriceTv=  view.findViewById(R.id.totalPriceTv);
        oneWayTv.setOnClickListener(this);
        upperSeatLin.setOnClickListener(this);
        lowerSeatLin.setOnClickListener(this);
        roundWayTv.setOnClickListener(this);
        view.findViewById(R.id.continueTv).setOnClickListener(this);
        busNameTv.setText(busSearchBean.travelName);
        busDetailTv.setText(busSearchBean.bustype);
        //get no of rows and column

        for(int i = 0; i< arrayListOneLower.size(); i++)
        {
            if(arrayListOneLower.get(i).row!=null && arrayListOneLower.get(i).row.length()>0 &&
                    Integer.parseInt(arrayListOneLower.get(i).row)>row)
            {
                row=Integer.parseInt(arrayListOneLower.get(i).row);
            }
            if(arrayListOneLower.get(i).column!=null && arrayListOneLower.get(i).column.length()>0 &&
                    Integer.parseInt(arrayListOneLower.get(i).column)>column)
            {
                column=Integer.parseInt(arrayListOneLower.get(i).column);
            }
        }

        setSeatMapOneLower();
        boolean isUpper=setSeatMapOneUpper(row,column);
        if(!isUpper){
            SeatLin.setVisibility(View.GONE);
        }
        if(tripType.equalsIgnoreCase(ONE_WAY)){
            view.findViewById(R.id.roundLin).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.roundLin).setVisibility(View.VISIBLE);
            oneWayClicked();
            setSeatMapTwoLower();
        }
    }

    private void setSeatMapOneLower() {
        selectedArrayOneLower =new ArrayList<>();
        for(int i=row;i>=0;i--) {
            LinearLayout verticalLayout = new LinearLayout(context);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            verticalLayout.setLayoutParams(layoutParams);
            for (int j = 0; j <= column; j++) {
                int position = -1;
                for (int k = 0; k < arrayListOneLower.size(); k++) {
                    if (arrayListOneLower.get(k).zIndex.equalsIgnoreCase("0") &&
                            arrayListOneLower.get(k).row.equalsIgnoreCase(String.valueOf(i)) &&
                            arrayListOneLower.get(k).column.equalsIgnoreCase(String.valueOf(j))) {
                        position = k;
                        break;
                    }
                }
                if (position == -1) {
                    setEmptySeat(verticalLayout,i);
                } else {
                    if (arrayListOneLower.get(position).length.equalsIgnoreCase("2") &&
                            arrayListOneLower.get(position).zIndex.equalsIgnoreCase("0")) {
                        setSleeperSeatOneLower(verticalLayout, position,i);
                        j++;
                    }
                    else if (arrayListOneLower.get(position).length.equalsIgnoreCase("2") &&
                            arrayListOneLower.get(position).zIndex.equalsIgnoreCase("1")) {
                        setSleeperSeatOneLower(verticalLayout, position,i);
                        j++;
                    } else if (arrayListOneLower.get(position).length.equalsIgnoreCase("1") &&
                            arrayListOneLower.get(position).width.equalsIgnoreCase("2")) {
                        setSleeperWidthSeatOneLower(verticalLayout, position,i);
                    }
                    else if (arrayListOneLower.get(position).length.equalsIgnoreCase("1")) {
                        setSingleSeatOneLower(verticalLayout, position,i);
                    }

                }
            }
            busSeatLinOneLower.addView(verticalLayout);
        }
    }

    private boolean setSeatMapOneUpper(int row, int column) {
//        selectedArrayOneLower =new ArrayList<>();
        boolean isSeat=false;
        for(int i=row;i>=0;i--) {
            LinearLayout verticalLayout = new LinearLayout(context);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            verticalLayout.setLayoutParams(layoutParams);
            for (int j = 0; j <= column; j++) {
                int position = -1;
                for (int k = 0; k < arrayListOneLower.size(); k++) {
                    if (arrayListOneLower.get(k).zIndex.equalsIgnoreCase("1") &&
                            arrayListOneLower.get(k).row.equalsIgnoreCase(String.valueOf(i)) &&
                            arrayListOneLower.get(k).column.equalsIgnoreCase(String.valueOf(j))) {
                        position = k;
                        break;
                    }
                }
                if (position == -1) {
                    setEmptySeat(verticalLayout,i);
                } else {
                    if (arrayListOneLower.get(position).length.equalsIgnoreCase("2") &&
                            arrayListOneLower.get(position).zIndex.equalsIgnoreCase("1")) {
                        setSleeperSeatOneLower(verticalLayout, position,i);
                        isSeat=true;
                        j++;
                    }else if (arrayListOneLower.get(position).length.equalsIgnoreCase("1") &&
                            arrayListOneLower.get(position).width.equalsIgnoreCase("2")) {
                        setSleeperWidthSeatOneLower(verticalLayout, position,i);
                    }
                    else if (arrayListOneLower.get(position).length.equalsIgnoreCase("1")&&
                            arrayListOneLower.get(position).zIndex.equalsIgnoreCase("1")) {
                        setSingleSeatOneLower(verticalLayout, position,i);
                        isSeat=true;
                    }
                }
            }
            busSeatLinOneUpper.addView(verticalLayout);
        }
        return isSeat;
    }

    private void setSeatMapTwoLower() {
        selectedArrayTwoLower =new ArrayList<>();

        for(int i=row;i>=0;i--) {
            LinearLayout verticalLayout = new LinearLayout(context);
            verticalLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            verticalLayout.setLayoutParams(layoutParams);
            for (int j = 0; j <= column; j++) {
                int position = -1;
                for (int k = 0; k < arrayListTwoLower.size(); k++) {
                    if (arrayListTwoLower.get(k).zIndex.equalsIgnoreCase("0") &&
                            arrayListTwoLower.get(k).row.equalsIgnoreCase(String.valueOf(i)) &&
                            arrayListTwoLower.get(k).column.equalsIgnoreCase(String.valueOf(j))) {
                        position = k;
                        break;
                    }
                }
                if (position == -1) {
                    setEmptySeat(verticalLayout,i);
                } else {
                    if (arrayListTwoLower.get(position).length.equalsIgnoreCase("2") &&
                            arrayListTwoLower.get(position).zIndex.equalsIgnoreCase("0")) {
                        setSleeperSeatTwoLower(verticalLayout, position,i);
                        j++;
                    } else if (arrayListTwoLower.get(position).length.equalsIgnoreCase("1")) {
                        setSingleSeatTwoLower(verticalLayout, position,i);
                    }
                }
            }
            busSeatLinTwoLower.addView(verticalLayout);
        }
    }

    private void setSingleSeatOneLower(LinearLayout verticalLayout,int position, int i) {
        RelativeLayout layout1 = new RelativeLayout(context);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins(((row-i)*seatWidth)+3,0,0,0);
        final ImageView busSeat = new ImageView(context);
        final TextView busSeatNo = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seatWidth, seatHeight);
        RelativeLayout.LayoutParams paramsTv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, seatHeight);
        layout1.setLayoutParams(paramsLayout);
        busSeat.setLayoutParams(params);
        busSeatNo.setLayoutParams(paramsTv);
//        busSeat.setRotation(90);
        paramsTv.addRule(RelativeLayout.CENTER_IN_PARENT, busSeat.getId());
//        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
        if(arrayListOneLower.get(position).available.equalsIgnoreCase("false")){
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_seat));
            busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
        }
        else {
            if(arrayListOneLower.get(position).ladiesSeat.equalsIgnoreCase("true")){
//                setLadiesSeat
                busSeat.setImageDrawable(getResources().getDrawable(R.drawable.red_seat));
                busSeatNo.setTextColor(getResources().getColor(R.color.color_white));
            }else {
                busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
                busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
            }
        }
        busSeatNo.setText(arrayListOneLower.get(position).name);
        busSeatNo.setGravity(Gravity.CENTER);
        busSeatNo.setTextSize(10);
        final int finalPosition = position;
        busSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayOneLower.contains(arrayListOneLower.get(finalPosition))) {
                    selectedArrayOneLower.remove(arrayListOneLower.get(finalPosition));
                    if(arrayListOneLower.get(finalPosition).ladiesSeat.equalsIgnoreCase("true")){
//                setLadiesSeat
                        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.red_seat));
                        busSeatNo.setTextColor(getResources().getColor(R.color.color_white));
                    }else {
                        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
                        busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
                    }
                } else {
                    if (selectedArrayOneLower.size() == 6 && arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Max Seat can be 6", Toast.LENGTH_LONG).show();
                    } else {
                        if(arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")){
                            selectedArrayOneLower.add(arrayListOneLower.get(finalPosition));
                            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_seat));
                            busSeatNo.setTextColor(getResources().getColor(R.color.color_white));
                        }else {
                            Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                setSeatNumberAndPrice(ONE_WAY);
            }
        });

        layout1.addView(busSeat);
        layout1.addView(busSeatNo);
        verticalLayout.addView(layout1);
    }

    private void setSingleSeatTwoLower(LinearLayout verticalLayout,int position,int row) {
        RelativeLayout layout1 = new RelativeLayout(context);
        RelativeLayout.LayoutParams paramsLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins((row*seatWidth)+3,0,0,0);

        final ImageView busSeat = new ImageView(context);
        TextView busSeatNo = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seatWidth, seatHeight);
        RelativeLayout.LayoutParams paramsTv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, seatHeight);
        layout1.setLayoutParams(paramsLayout);
        busSeat.setLayoutParams(params);
        busSeatNo.setLayoutParams(paramsTv);
//        busSeat.setRotation(90);
        paramsTv.addRule(RelativeLayout.CENTER_IN_PARENT, busSeat.getId());
//        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
        if(arrayListTwoLower.get(position).available.equalsIgnoreCase("false")){
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_seat));
        }
        else {
            if(arrayListTwoLower.get(position).ladiesSeat.equalsIgnoreCase("true")){
//                setLadiesSeat
                busSeat.setImageDrawable(getResources().getDrawable(R.drawable.red_seat));
            }else {
                busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
            }
        }
        busSeatNo.setText(arrayListTwoLower.get(position).name);
        busSeatNo.setGravity(Gravity.CENTER);
        busSeatNo.setTextSize(10);
        final int finalPosition = position;
        busSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayTwoLower.contains(arrayListTwoLower.get(finalPosition))) {
                    selectedArrayTwoLower.remove(arrayListTwoLower.get(finalPosition));
                    if(arrayListTwoLower.get(finalPosition).ladiesSeat.equalsIgnoreCase("true")){
//                setLadiesSeat
                        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.red_seat));
                    }else {
                        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_seat));
                    }
                } else {
                    if (selectedArrayTwoLower.size() == 6 && arrayListTwoLower.get(finalPosition).available.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Max Seat can be 6", Toast.LENGTH_LONG).show();
                    } else {
                        if(arrayListTwoLower.get(finalPosition).available.equalsIgnoreCase("true")){
                            selectedArrayTwoLower.add(arrayListTwoLower.get(finalPosition));
                            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_seat));
                        }else {
                            Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        layout1.addView(busSeat);
        layout1.addView(busSeatNo);
        verticalLayout.addView(layout1);
    }

    private void setSleeperSeatOneLower(LinearLayout verticalLayout, int position,int i) {
        RelativeLayout layout1 = new RelativeLayout(context);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins(((row-i)*seatWidth)+3,0,0,0);

        final ImageView busSeat = new ImageView(context);
        final TextView busSeatNo = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seatWidth, sleeperHeight);
        RelativeLayout.LayoutParams paramsTv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, sleeperHeight);
        layout1.setLayoutParams(paramsLayout);
        busSeat.setLayoutParams(params);
        busSeatNo.setLayoutParams(paramsTv);
        paramsTv.addRule(RelativeLayout.CENTER_IN_PARENT, busSeat.getId());

        if(arrayListOneLower.get(position).available.equalsIgnoreCase("false")){
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper));
            busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
        }
        else {
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper));
            busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
        }
//        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper));
        busSeatNo.setText(arrayListOneLower.get(position).name);
        busSeatNo.setGravity(Gravity.CENTER);
        busSeatNo.setTextSize(10);
        final int finalPosition = position;
        busSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayOneLower.contains(arrayListOneLower.get(finalPosition))) {
                    selectedArrayOneLower.remove(arrayListOneLower.get(finalPosition));
                    busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper));
                    busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
                } else {
                    if (selectedArrayOneLower.size() == 6 && arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Max Seat can be 6", Toast.LENGTH_LONG).show();
                    } else {
                        if(arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")){
                            selectedArrayOneLower.add(arrayListOneLower.get(finalPosition));
                            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_sleeper));
                            busSeatNo.setTextColor(getResources().getColor(R.color.color_white));
                        }else {
                            Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                setSeatNumberAndPrice(ONE_WAY);
            }
        });

        layout1.addView(busSeat);
        layout1.addView(busSeatNo);
        verticalLayout.addView(layout1);
    }

    private void setSleeperWidthSeatOneLower(LinearLayout verticalLayout, int position,int i) {

        RelativeLayout layout1 = new RelativeLayout(context);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins(((row-i-1)*seatWidth)+3,0,0,0);
        final ImageView busSeat = new ImageView(context);
        final TextView busSeatNo = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sleeperHeight, seatHeight);
        RelativeLayout.LayoutParams paramsTv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, seatHeight);
        layout1.setLayoutParams(paramsLayout);
//        params.setMargins(-seatWidth,2,2,2);
        busSeat.setLayoutParams(params);
//        busSeat.setPadding(-seatWidth,1,1,1);
        busSeatNo.setLayoutParams(paramsTv);
        paramsTv.addRule(RelativeLayout.CENTER_IN_PARENT, busSeat.getId());

        if(arrayListOneLower.get(position).available.equalsIgnoreCase("false")){
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper_width
            ));
            busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
        }
        else {
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper_wide));
            busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
        }
//        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper));
        busSeatNo.setText(arrayListOneLower.get(position).name);
        busSeatNo.setGravity(Gravity.CENTER);
        busSeatNo.setTextSize(10);
        final int finalPosition = position;
        busSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayOneLower.contains(arrayListOneLower.get(finalPosition))) {
                    selectedArrayOneLower.remove(arrayListOneLower.get(finalPosition));
                    busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper_wide));
                    busSeatNo.setTextColor(getResources().getColor(R.color.color_black));
                } else {
                    if (selectedArrayOneLower.size() == 6 && arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Max Seat can be 6", Toast.LENGTH_LONG).show();
                    } else {
                        if(arrayListOneLower.get(finalPosition).available.equalsIgnoreCase("true")){
                            selectedArrayOneLower.add(arrayListOneLower.get(finalPosition));
                            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_sleeper_wide));
                            busSeatNo.setTextColor(getResources().getColor(R.color.color_white));
                        }else {
                            Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                setSeatNumberAndPrice(ONE_WAY);
            }
        });

        layout1.addView(busSeat);
        layout1.addView(busSeatNo);
        verticalLayout.addView(layout1);
    }

    private void setSleeperSeatTwoLower(LinearLayout verticalLayout, int position,int row) {
        RelativeLayout layout1 = new RelativeLayout(context);
        RelativeLayout.LayoutParams paramsLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins((row*seatWidth)+3,0,0,0);
        final ImageView busSeat = new ImageView(context);
        TextView busSeatNo = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seatWidth, sleeperHeight);
        RelativeLayout.LayoutParams paramsTv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 80);
        layout1.setLayoutParams(paramsLayout);
        busSeat.setLayoutParams(params);
        busSeatNo.setLayoutParams(paramsTv);
        paramsTv.addRule(RelativeLayout.CENTER_IN_PARENT, busSeat.getId());

        if(arrayListTwoLower.get(position).available.equalsIgnoreCase("false")){
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper));
        }
        else {
            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper));
        }
//        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.gray_sleeper));
        busSeatNo.setText(arrayListTwoLower.get(position).name);
        busSeatNo.setGravity(Gravity.CENTER);
        busSeatNo.setTextSize(10);
        final int finalPosition = position;
        busSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArrayTwoLower.contains(arrayListTwoLower.get(finalPosition))) {
                    selectedArrayTwoLower.remove(arrayListTwoLower.get(finalPosition));
                    busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blank_sleeper));
                } else {
                    if (selectedArrayTwoLower.size() == 6 && arrayListTwoLower.get(finalPosition).available.equalsIgnoreCase("true")) {
                        Toast.makeText(context, "Max Seat can be 6", Toast.LENGTH_LONG).show();
                    } else {
                        if(arrayListTwoLower.get(finalPosition).available.equalsIgnoreCase("true")){
                            selectedArrayTwoLower.add(arrayListTwoLower.get(finalPosition));
                            busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_sleeper));
                        }else {
                            Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        layout1.addView(busSeat);
        layout1.addView(busSeatNo);
        verticalLayout.addView(layout1);
    }

    private void setEmptySeat(LinearLayout layout,int i) {
        ImageView busSeat = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(seatWidth, seatHeight);
        params.setMargins(((row-i)*seatWidth)+3,0,0,0);
        busSeat.setLayoutParams(params);
//        busSeat.setRotation(90);
        busSeat.setImageDrawable(getResources().getDrawable(R.drawable.blue_seat));
        busSeat.setVisibility(View.INVISIBLE);
        layout.addView(busSeat);
    }

    private void setSeatNumberAndPrice(String tripType) {
        String seatNo="Seat no:";
        price=0;
        if(selectedArrayOneLower==null || selectedArrayOneLower.size()==0){
            seatNumberTv.setText(seatNo);
            totalPriceTv.setText("INR 0");
        }else {
            for(int i=0; i<selectedArrayOneLower.size(); i++){
                seatNo=seatNo+", "+selectedArrayOneLower.get(i).name;
                price=price+Float.parseFloat(selectedArrayOneLower.get(i).GrossFare);
            }
            seatNumberTv.setText(seatNo.replaceFirst(",",""));
            totalPriceTv.setText("INR "+price);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.oneWayTv:
                oneWayClicked();
                break;
            case R.id.roundWayTv:
                roundTripClicked();
                break;
            case R.id.lowerSeatLin:
                lowerClicked();
                break;
            case R.id.upperSeatLin:
                upperClicked();
                break;
            case R.id.continueTv:
                Common.preventFrequentClick(continueTv);
                if(selectedArrayOneLower.size()>0){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("BoardingList", boardingArrayList);
                    bundle.putSerializable("SelectedPassengerArray", selectedArrayOneLower);
                    bundle.putSerializable("tripType", tripType);
                    bundle.putSerializable("BusSearchBean", busSearchBean);
                    bundle.putString("TotalPrice", String.valueOf(price));
                    Fragment boardingFragment=new BusBoardingListFragment();
                    boardingFragment.setArguments(bundle);
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(boardingFragment);
                }else {
                    Toast.makeText(context, "Please select atleast one seat", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initializeAnimations() {
        objectAnimatorOneWay= ObjectAnimator.ofObject(oneWayTv, "backgroundColor", new ArgbEvaluator(),
                         /*start*/0xFF4FCCF5, /*end*/0xFF2ba9ca).setDuration(500);
        objectAnimatorRound=ObjectAnimator.ofObject(roundWayTv, "backgroundColor", new ArgbEvaluator(),
                         /*start*/0xFF4FCCF5, /*end*/0xFF2ba9ca).setDuration(500);
        objectAnimatorLower=ObjectAnimator.ofObject(lowerSeatLin, "backgroundColor", new ArgbEvaluator(),
                         /*start*/0xFF4FCCF5, /*end*/0xFF2ba9ca).setDuration(500);
        objectAnimatorUpper=ObjectAnimator.ofObject(upperSeatLin, "backgroundColor", new ArgbEvaluator(),
                         /*start*/0xFF4FCCF5, /*end*/0xFF2ba9ca).setDuration(500);

    }

    private void oneWayClicked() {

        busSeatLinTwoLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus_gone));
        busSeatLinTwoLower.setVisibility(View.GONE);
        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus));
        busSeatLinOneLower.setVisibility(View.VISIBLE);
        Common.preventFrequentClick(oneWayTv);
        objectAnimatorOneWay.start();
        objectAnimatorRound.end();
        roundWayTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
        oneWayTv.setTextColor(getResources().getColor(R.color.color_white));
        roundWayTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);

    }

    private void roundTripClicked() {
//        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.alpha_bus));
        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus_gone));
        busSeatLinTwoLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus));
        busSeatLinTwoLower.setVisibility(View.VISIBLE);
        busSeatLinOneLower.setVisibility(View.GONE);
        Common.preventFrequentClick(roundWayTv);
        objectAnimatorOneWay.end();
        objectAnimatorRound.start();

        roundWayTv.setTextColor(getResources().getColor(R.color.color_white));
        oneWayTv.setTextColor(getResources().getColor(R.color.dark_blue_color));
        oneWayTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
    }

    private void lowerClicked() {
        driverImg.setVisibility(View.VISIBLE);
        busSeatLinOneUpper.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus_gone));
        busSeatLinOneUpper.setVisibility(View.GONE);
        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus));
        busSeatLinOneLower.setVisibility(View.VISIBLE);
        Common.preventFrequentClick(oneWayTv);
        objectAnimatorLower.start();
        objectAnimatorUpper.end();
        upperSeatLin.setTextColor(getResources().getColor(R.color.dark_blue_color));
        lowerSeatLin.setTextColor(getResources().getColor(R.color.color_white));
        upperSeatLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);

    }

    private void upperClicked() {
//        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
//                R.anim.alpha_bus));
        driverImg.setVisibility(View.INVISIBLE);
        busSeatLinOneLower.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus_gone));
        busSeatLinOneUpper.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_bus));
        busSeatLinOneUpper.setVisibility(View.VISIBLE);
        busSeatLinOneLower.setVisibility(View.GONE);
        Common.preventFrequentClick(roundWayTv);
        objectAnimatorLower.end();
        objectAnimatorUpper.start();

        upperSeatLin.setTextColor(getResources().getColor(R.color.color_white));
        lowerSeatLin.setTextColor(getResources().getColor(R.color.dark_blue_color));
        lowerSeatLin.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
    }

    float getDpToPx(float Dp){
        return Dp * context.getResources().getDisplayMetrics().density;
    }
}