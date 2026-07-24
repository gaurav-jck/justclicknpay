package com.justclick.clicknbook.Fragment.hotel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.HotelImageAdapter;
import com.justclick.clicknbook.databinding.FragmentHotelMoreInfoBinding;
import com.justclick.clicknbook.model.HotelCityListModel;
import com.justclick.clicknbook.model.HotelMoreInfoResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.HotelCityRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.MySpannable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class HotelMoreInfoFragment extends Fragment implements View.OnClickListener{

    private ToolBarTitleChangeListener titleChangeListener;
    Context context;
    private LoginModel loginModel;
    private HotelMoreInfoResponseModel moreInfoResponseModel;
    Spinner[] spinnerSelection;
    TextView[] bookTv;
    private FragmentHotelMoreInfoBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        moreInfoResponseModel=new HotelMoreInfoResponseModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.fragment_hotel_more_info, container, false);
        binding=FragmentHotelMoreInfoBinding.inflate(getLayoutInflater());

        binding.topView.titleTv.setText("Hotel Info");
        binding.topView.backArrow.setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });

//        if(getArguments()!=null && getArguments().getSerializable("HotelInfo")!=null){
//            moreInfoResponseModel= (HotelMoreInfoResponseModel) getArguments().getSerializable("HotelInfo");
//        }

        initializeViews();
        setRoomDataDynamically();
        return binding.getRoot();
    }

    private void initializeViews() {
        spinnerSelection=new Spinner[4];
        bookTv=new TextView[4];
        makeTextViewResizable(binding.hotelDescriptionTv, 2, "View More", true);

        binding.locationTv.setOnClickListener(this);

        binding.imageRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        binding.imageRecycler.setAdapter(new HotelImageAdapter(context,new HotelImageAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<HotelMoreInfoResponseModel.HotelImages> list, int position) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(list.get(position).ImagePath))); /** replace with your own uri */
            }
        }, moreInfoResponseModel.HotelImages));

//        ratingBar.setRating(Float.parseFloat(moreInfoResponseModel.HotelStarRating));
        setAmenitiesDynamically();
        setFont();

    }
    private void setFont() {
        Typeface face = Common.EditTextTypeFace(context);
        Typeface face2 = Common.TextViewTypeFace(context);
        Typeface face1 = Common.OpenSansRegularTypeFace(context);

//        titles
        binding.summaryTv.setTypeface(face2);
        binding.locationTv.setTypeface(face2);
        binding.amenitiesTitleTv.setTypeface(face2);
        binding.aboutHotelTitleTv.setTypeface(face2);
        binding.galleryTv.setTypeface(face2);
        binding.chooseRoomTv.setTypeface(face2);

//        contents
        binding.hotelDetailsTv.setTypeface(face1);
        binding.cheapestPriceTv.setTypeface(face1);

//        labels
        binding.paxInfoTv.setTypeface(face1);

//        editTexts
//        binding.promoCodeEdt)).setTypeface(face);


    }


    private void setRoomDataDynamically() {
        for(int i = 0; i< 4; i++) {
            LinearLayout dynamicContent = binding.roomDetailsLin;
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.hotel_room_data, dynamicContent, false);

            spinnerSelection[i] =wizard.findViewById(R.id.spinnerSelection);
            bookTv[i] =wizard.findViewById(R.id.bookTv);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                    getStringArray(R.array.hotel_room_amenities_array));
            adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
            spinnerSelection[i].setAdapter(adapter);
            bookTv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HotelBookingInformationFragment fragment=new HotelBookingInformationFragment();
                    ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                    Toast.makeText(context,"book",Toast.LENGTH_SHORT).show();
                }
            });

            ((TextView)wizard.findViewById(R.id.roomTypeTv)).setTypeface(Common.TextViewTypeFace(context));
            ((TextView)wizard.findViewById(R.id.priceTv)).setTypeface(Common.OpenSansRegularTypeFace(context));
            ((TextView)wizard.findViewById(R.id.bookTv)).setTypeface(Common.TextViewTypeFace(context));
            ((TextView)wizard.findViewById(R.id.availabilityTv)).setTypeface(Common.OpenSansRegularTypeFace(context));

            dynamicContent.addView(wizard);
        }
    }

    private void setAmenitiesDynamically() {
       final String amenities[]={"wifi","Gym","Swimming Pool","Bar","Restaurant","Parking","Room service",
               "Gym","Swimming Pool","Bar","Restaurant","Parking","Room service",
               "Gym","Swimming Pool","Bar","Restaurant","Parking","Room service"};
        for(int i=0;i<amenities.length;i++) {

            TextView amenitiesTv=new TextView(context);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
            amenitiesTv.setLayoutParams(params);
            amenitiesTv.setPadding(10,5,10,5);
            amenitiesTv.setGravity(Gravity.CENTER);
            if(amenities[i].contains("wifi") )
            {
                amenitiesTv.setText("Wi-fi");
//                amenitiesImg.setImageDrawable(getResources().getDrawable(R.drawable.swimming_pool_vector));
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.wifi_vector),null,null);
            }else if(amenities[i].contains("Gym")){
                amenitiesTv.setText("Gym");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.gym_vector),null,null);
            }else if(amenities[i].contains("Swimming Pool")){
                amenitiesTv.setText("Swimming Pool");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.swimming_pool_vector),null,null);
            }else if(amenities[i].contains("Bar")){
                amenitiesTv.setText("Bar");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.bar_vector),null,null);
            }else if(amenities[i].contains("Restaurant")){
                amenitiesTv.setText("Restaurant");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.restaurant_vector),null,null);
            }else if(amenities[i].contains("Room service")){
                amenitiesTv.setText("Room service");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.room_service_vector),null,null);
            }else if(amenities[i].contains("Parking")){
                amenitiesTv.setText("Parking");
                amenitiesTv.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.parking_vector),null,null);
            }
            if(binding.amenitiesLin.getChildCount()<=5)
            {
                binding.amenitiesLin.addView(amenitiesTv);
            }else {
                final TextView more=new TextView(context);
                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                more.setLayoutParams(params1);
                more.setText("more");
                more.setTextColor(getResources().getColor(R.color.app_red_color));
                more.setPadding(20,5,10,10);
                more.setGravity(Gravity.CENTER);
                binding.amenitiesLin.addView(more);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.custom_dialog_view);
                        dialog.setCanceledOnTouchOutside(true);
                        final Window window= dialog.getWindow();
                        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.findViewById(R.id.imgVw_progress).setVisibility(View.GONE);
                        String amenity= "";
                        for (int i=0;i<amenities.length;i++)
                        {
                            amenity=amenity+"  - "+amenities[i]+"     \n";
                        }
                        ((TextView)dialog.findViewById(R.id.tv_progressmsg)).setText(amenity);
                        ((TextView)dialog.findViewById(R.id.tv_progressmsg)).setGravity(Gravity.LEFT);
                        dialog.show();
                    }
                });
                break;
            }
        }
    }
    private static void makeTextViewResizable(final TextView hotelDescriptionTv, final int i, final String s, final boolean b) {
        {

            if (hotelDescriptionTv.getTag() == null) {
                hotelDescriptionTv.setTag(hotelDescriptionTv.getText());
            }
            ViewTreeObserver vto = hotelDescriptionTv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {

                    ViewTreeObserver obs = hotelDescriptionTv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (i == 0) {
                        int lineEndIndex = hotelDescriptionTv.getLayout().getLineEnd(0);
                        String text = hotelDescriptionTv.getText().subSequence(0, lineEndIndex - s.length() + 1) + " " + s;
                        hotelDescriptionTv.setText(text);
                        hotelDescriptionTv.setMovementMethod(LinkMovementMethod.getInstance());
                        hotelDescriptionTv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(hotelDescriptionTv.getText().toString()), hotelDescriptionTv, i, s,
                                        b), TextView.BufferType.SPANNABLE);
                    } else if (i > 0 && hotelDescriptionTv.getLineCount() >= i) {
                        int lineEndIndex = hotelDescriptionTv.getLayout().getLineEnd(i - 1);
                        String text = hotelDescriptionTv.getText().subSequence(0, lineEndIndex - s.length() + 1) + " " + s;
                        hotelDescriptionTv.setText(text);
                        hotelDescriptionTv.setMovementMethod(LinkMovementMethod.getInstance());
                        hotelDescriptionTv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(hotelDescriptionTv.getText().toString()), hotelDescriptionTv, i, s,
                                        b), TextView.BufferType.SPANNABLE);
                    } else {
                        int lineEndIndex = hotelDescriptionTv.getLayout().getLineEnd(hotelDescriptionTv.getLayout().getLineCount() - 1);
                        String text = hotelDescriptionTv.getText().subSequence(0, lineEndIndex) + " " + s;
                        hotelDescriptionTv.setText(text);
                        hotelDescriptionTv.setMovementMethod(LinkMovementMethod.getInstance());
                        hotelDescriptionTv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(hotelDescriptionTv.getText().toString()), hotelDescriptionTv, lineEndIndex, s,
                                        b), TextView.BufferType.SPANNABLE);
                    }
                }
            });

        }}

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int i, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }



    public void hotelCity(HotelCityRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<HotelCityListModel> call = apiService.getHotelCityPost(ApiConstants.CityCountry, model);
        call.enqueue(new Callback<HotelCityListModel>() {
            @Override
            public void onResponse(Call<HotelCityListModel> call,Response<HotelCityListModel> response) {
                try {

                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(Call<HotelCityListModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.roomPlusTv:
                break;
            case R.id.locationTv:
                Bundle bundle=new Bundle();
                bundle.putSerializable("HotelDetail",moreInfoResponseModel);
                HotelMapFragment fragment=new HotelMapFragment();
                fragment.setArguments(bundle);
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(fragment);
                Toast.makeText(context,"location",Toast.LENGTH_SHORT).show();
                break;
        }


    }

}

