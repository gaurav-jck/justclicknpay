package com.justclick.clicknbook.Fragment.flight;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;

/**
 * Created by priyanshi on 11/22/2017.
 */

public class FlightConfirmFragment extends Fragment implements View.OnClickListener
{  Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView fromToTv,detailTv,dateTv,timeTv,nameTv,numIdTv,book_tv;
    private SimpleDraweeView flightImage;
    String names="",Email="",Number="",FromTime="",ToTime="",Date="",FromCity="",ToCity="", Image="",FlightName="";
    int AdultCount=1,ChildCount=0,InfantCount=0,FinalPrice=0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
//        if (getArguments().getString("Names") != null) {
//            names=getArguments().getString("Names");
//            Email=getArguments().getString("Email");
//            Number=getArguments().getString("Number");
//            FromTime=getArguments().getString("FromTime");
//            ToTime=getArguments().getString("ToTime");
//            Date=getArguments().getString("Date");
//            FromCity=getArguments().getString("FromCity");
//            ToCity=getArguments().getString("ToCity");
//            AdultCount=getArguments().getInt("AdultCount");
//            ChildCount=getArguments().getInt("ChildCount");
//            InfantCount=getArguments().getInt("InfantCount");
//            Image=getArguments().getString("FlightImage");
//            FlightName=getArguments().getString("FlightName");
//            FinalPrice=getArguments().getInt("FinalPrice");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_flight_confirm, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.flightConfirmation));
//        fromToTv= (TextView) view.findViewById(R.id.fromToTv);
//        detailTv= (TextView) view.findViewById(R.id.detailTv);
//        dateTv= (TextView) view.findViewById(R.id.dateTv);
//        timeTv= (TextView) view.findViewById(R.id.timeTv);
//        nameTv= (TextView) view.findViewById(R.id.nameTv);
//        numIdTv= (TextView) view.findViewById(R.id.numIdTv);
//        book_tv= (TextView) view.findViewById(R.id.book_tv);
//        flightImage= (SimpleDraweeView) view.findViewById(R.id.flightImage);
//        nameTv.setText(names);
//        fromToTv.setText(FromCity+" -> "+ToCity);
//        detailTv.setText(Date +", "+AdultCount+" Adult, "+ChildCount+" Child, "+InfantCount+" Infant");
//        dateTv.setText(FlightName);
//        timeTv.setText(FromTime+" - "+ToTime);
//        numIdTv.setText(Number+", "+Email);
//        flightImage.setImageURI(ApiConstants.FlightImageBaseUrl+Image);
//        book_tv.setText("Book"+ "( "+FinalPrice+" )");
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
