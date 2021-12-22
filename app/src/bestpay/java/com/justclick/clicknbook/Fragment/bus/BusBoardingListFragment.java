package com.justclick.clicknbook.Fragment.bus;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.bus.busmodel.BusSearchBean;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.BusBoardingPointAdapter;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;


public class BusBoardingListFragment extends Fragment implements View.OnClickListener{
    private View view;
    private final String ONE_WAY="ONE";
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private RecyclerView boardingList, droppingList, boardingListReturn, droppingListReturn;
    private Context context;
    private ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> boardingArrayList, boardingArrayListReturn;
    private ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> deBoardingArrayList, deBoardingArrayListReturn;
    private BusSearchBean busSearchBean;
    private ArrayList<SeatMapBean> arrayListSeatMap;
    private TextView fromToTv,goingTv,returnTv, continueTv;
    private ObjectAnimator objectAnimatorOneWay, objectAnimatorRound;
    private int arrayPositionBoarding=0, arrayPositionDropping=0, arrayPositionBoardingReturn=0, arrayPositionDroppingReturn=0;
    private String tripType,TotalPrice;
    int selectedBoardingPoint,selectedDroppingPoint;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        boardingArrayList=new ArrayList<>();
        deBoardingArrayList=new ArrayList<>();
        boardingArrayListReturn=new ArrayList<>();
        deBoardingArrayListReturn=new ArrayList<>();

        if(getArguments().getSerializable("BoardingList")!=null) {
            busSearchBean = (BusSearchBean) getArguments().getSerializable("BusSearchBean");
            arrayListSeatMap = (ArrayList<SeatMapBean>) getArguments().getSerializable("arrayListSeatMap");
            tripType = getArguments().getString("tripType");
            TotalPrice= getArguments().getString("TotalPrice");
        }
        boardingArrayList.addAll(busSearchBean.boardingTimes);
        deBoardingArrayList.addAll(busSearchBean.droppingTimes);
        boardingArrayListReturn.addAll(busSearchBean.boardingTimes);
        deBoardingArrayListReturn.addAll(busSearchBean.droppingTimes);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,

                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.bus_boarding_point_dialog, container, false);
            titleChangeListener.onToolBarTitleChange(getString(R.string.busSearchFragmentTitle));
            fromToTv = view.findViewById(R.id.fromToTv);
            goingTv =  view.findViewById(R.id.goingTv);
            returnTv =  view.findViewById(R.id.returnTv);
            continueTv =  view.findViewById(R.id.continueTv);

            boardingList =  view.findViewById(R.id.boardingList);
            droppingList =  view.findViewById(R.id.droppingList);
            boardingListReturn =  view.findViewById(R.id.boardingListReturn);
            droppingListReturn =  view.findViewById(R.id.deBoardingListReturn);

            if (tripType.equalsIgnoreCase(ONE_WAY)) {
                returnTv.setVisibility(View.GONE);
            } else {
                goingTv.setOnClickListener(this);
            }

            boardingList.setLayoutManager(new LinearLayoutManager(context));
            BusBoardingPointAdapter boardingArrayListAdapter = new BusBoardingPointAdapter(context, new BusBoardingPointAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> list, int position) {
                    selectedBoardingPoint = position;
//                Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show();
//                boardingArrayList.get(arrayPositionBoarding).isClick=false;
//                boardingArrayList.get(position).isClick=true;
//                arrayPositionBoarding=position;
                }
            }, boardingArrayList);
            boardingList.setAdapter(boardingArrayListAdapter);

            droppingList.setLayoutManager(new LinearLayoutManager(context));
            BusBoardingPointAdapter deBoardingArrayListAdapter = new BusBoardingPointAdapter(context, new BusBoardingPointAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> list, int position) {
//                Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show();
                    selectedDroppingPoint = position;
                    deBoardingArrayList.get(arrayPositionDropping).isClick = false;
                    deBoardingArrayList.get(position).isClick = true;
                    arrayPositionDropping = position;
                }
            }, deBoardingArrayList);
            droppingList.setAdapter(deBoardingArrayListAdapter);

            boardingListReturn.setLayoutManager(new LinearLayoutManager(context));
            BusBoardingPointAdapter boardingArrayListReturnAdapter = new BusBoardingPointAdapter(context, new BusBoardingPointAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> list, int position) {
//                Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show();
                    boardingArrayListReturn.get(arrayPositionBoardingReturn).isClick = false;
                    boardingArrayListReturn.get(position).isClick = true;
                    arrayPositionBoardingReturn = position;
                }
            }, boardingArrayListReturn);
            boardingListReturn.setAdapter(boardingArrayListReturnAdapter);

            droppingListReturn.setLayoutManager(new LinearLayoutManager(context));
            BusBoardingPointAdapter deBoardingArrayListReturnAdapter = new BusBoardingPointAdapter(context, new BusBoardingPointAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<BusSearchBean.BusBoardingDroppingTimeBean> list, int position) {
//                Toast.makeText(context, "Item Clicked", Toast.LENGTH_SHORT).show();
                    deBoardingArrayListReturn.get(arrayPositionDroppingReturn).isClick = false;
                    deBoardingArrayListReturn.get(position).isClick = true;
                    arrayPositionDroppingReturn = position;
                }
            }, deBoardingArrayListReturn);
            droppingListReturn.setAdapter(deBoardingArrayListReturnAdapter);


            view.findViewById(R.id.backArrow).setOnClickListener(this);
            view.findViewById(R.id.continueTv).setOnClickListener(this);
            returnTv.setOnClickListener(this);
            goingTv.setText(MyPreferences.getBusFromCity(context).substring(0,MyPreferences.getBusFromCity(context).indexOf("("))
                    + " - " + MyPreferences.getBusToCity(context).substring(0,MyPreferences.getBusToCity(context).indexOf("(")));
//        set fonts
            setFont();
            initializeAnimations();
        }
        return view;

    }

    private void initializeAnimations() {
        objectAnimatorOneWay= ObjectAnimator.ofObject(goingTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);
        objectAnimatorRound=ObjectAnimator.ofObject(returnTv, "backgroundColor", new ArgbEvaluator(),
                         /*Red*/0xFF8ca9ff, /*Blue*/0xFF285ab4).setDuration(500);

    }

    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.FlightCalenderTypeFace3(context);
        Typeface face1 = Common.FlightCalenderTypeFace3(context);
        fromToTv.setTypeface(face);
        goingTv.setTypeface(face);
        returnTv.setTypeface(face);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.continueTv:
                Common.preventFrequentClick(continueTv);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BusSeat", arrayListSeatMap);
                bundle.putSerializable("SelectedPassengerArray",getArguments().getSerializable("SelectedPassengerArray"));
                bundle.putInt("boardingPointPosition", selectedBoardingPoint);
                bundle.putInt("droppingPointPosition", selectedDroppingPoint);
                bundle.putString("TotalPrice", TotalPrice);
                bundle.putSerializable("busSearchBean",busSearchBean);
                Fragment fragment=new BusDetailFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                break;

            case R.id.goingTv:
                boardingList.setVisibility(View.VISIBLE);
                droppingList.setVisibility(View.VISIBLE);
                boardingListReturn.setVisibility(View.GONE);
                droppingListReturn.setVisibility(View.GONE);
                boardingList.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.alpha_bus));
                droppingList.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.alpha_bus));
                Common.preventFrequentClick(goingTv);
                objectAnimatorOneWay.start();
                objectAnimatorRound.end();
                returnTv.setTextColor(getResources().getColor(R.color.app_blue_color));
                goingTv.setTextColor(getResources().getColor(R.color.color_white));
                returnTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                break;

            case R.id.returnTv:
                boardingList.setVisibility(View.GONE);
                droppingList.setVisibility(View.GONE);
                boardingListReturn.setVisibility(View.VISIBLE);
                droppingListReturn.setVisibility(View.VISIBLE);
                boardingListReturn.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.alpha_bus));
                droppingListReturn.startAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.alpha_bus));
                Common.preventFrequentClick(returnTv);
                objectAnimatorOneWay.end();
                objectAnimatorRound.start();
                returnTv.setTextColor(getResources().getColor(R.color.color_white));
                goingTv.setTextColor(getResources().getColor(R.color.app_blue_color));
                goingTv.setBackgroundResource(R.drawable.money_receiver_border_rect_corner);
                break;
        }

    }

}

