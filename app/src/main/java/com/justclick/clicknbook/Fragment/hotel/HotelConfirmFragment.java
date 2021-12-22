package com.justclick.clicknbook.Fragment.hotel;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;

/**
 * Created by priyanshi on 11/22/2017.
 */

public class HotelConfirmFragment extends Fragment implements View.OnClickListener {
    Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ScrollView scrollView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_hotel_confirm, container, false);
        scrollView= (ScrollView) view.findViewById(R.id.scrollView);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
//                if(scrollView.getY()==scrollView.getHeight()){
//                    Toast.makeText(context, "Bottom1", Toast.LENGTH_SHORT).show();
//                }
//                if((scrollView.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()))==0){
//                    Toast.makeText(context, "Bottom2", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onClick(View v) {

    }
}
