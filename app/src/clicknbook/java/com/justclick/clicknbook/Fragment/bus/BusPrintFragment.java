package com.justclick.clicknbook.Fragment.bus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusCancelResponse;
import com.justclick.clicknbook.Fragment.bus.busResponseModel.BusPrintResponse;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusCancelRequestModel;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.BusTransactionListResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.ResponseBody;

/**
 * Created by priyanshi on 11/22/2017.
 */

public class BusPrintFragment extends Fragment implements View.OnClickListener {
    Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ScrollView scrollView;
    private ImageView backArrow;
    public TextView sourceCityTv,destCityTv,dayDateTv,pnrNumberTv,operatorTv,busTypeTv,
            reportingTimeTv,departureTimeTv,boardingAddressTv,landmarkTv,addressIdTv,seatNo_tv,
            contactTv,baseFareTv,taxTv,grossFareTv,tdsTv,totalTvValue,transactionIdTv,transactionTv,
            transactionDateTv,paymentTv,executiveTv,executiveMobileTv,agencyTv,cancelTv,discountTv,conveyanceTv,
            cancel1Tv,cancel11Tv,cancel2Tv,cancel21Tv,cancel3Tv,cancel31Tv,cancel4Tv,cancel41Tv, cancel5Tv, cancel51Tv,partialNoteTv;
    private EditText remarkEdt;
    private RelativeLayout totalFareRel,cancellationRel;
    private ImageView fareArrowImg,cancellationArrowImg;
    private TextView[]nameTv,seatTv,pnrTv,ageTv;
    private AppCompatCheckBox[] checkImg;
    public BusPrintResponse commonResponse;
    private CheckBox checkedAllImg;
    private LinearLayout pdfLinear,dialogLinear,linearAdd,fareLayout,cancellationLayout;
    int noOfPerson=1;
    String person="",seatNo="";
    private LoginModel loginModel;
    private Boolean checkedAll=false;
    String seatNoArray[]=new String[noOfPerson];
    private Animation animUp, animDown;

    BusTransactionListResponseModel.BusListModel busListModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_bus_reservation, container, false);

        backArrow= view.findViewById(R.id.backArrow);
        sourceCityTv=  view.findViewById(R.id.sourceCityTv);
        destCityTv=  view.findViewById(R.id.destCityTv);
        dayDateTv=  view.findViewById(R.id.dayDateTv);
        pnrNumberTv=  view.findViewById(R.id.pnrNumberTv);
        operatorTv= view.findViewById(R.id.operatorTv);
        busTypeTv=  view.findViewById(R.id.busTypeTv);
//        reportingTimeTv=  view.findViewById(R.id.reportingTimeTv);
//        departureTimeTv= view.findViewById(R.id.departureTimeTv);
        boardingAddressTv=  view.findViewById(R.id.boardingAddressTv);
        landmarkTv=  view.findViewById(R.id.landmarkTv);
        addressIdTv=  view.findViewById(R.id.addressIdTv);
        fareArrowImg=  view.findViewById(R.id.fareArrowImg);
        cancellationArrowImg= view.findViewById(R.id.cancellationArrowImg);
//        nameTv=  view.findViewById(R.id.nameTv);
//        seatNo_tv=  view.findViewById(R.id.seatNo_tv);
//        contactTv=  view.findViewById(R.id.contactTv);
//        pnrTv=  view.findViewById(R.id.pnrTv);
        cancellationRel=  view.findViewById(R.id.cancellationRel);
        totalFareRel=  view.findViewById(R.id.totalFareRel);
        fareLayout=  view.findViewById(R.id.fareLayout);
        cancellationLayout=  view.findViewById(R.id.cancellationLayout);
        totalFareRel.setOnClickListener(this);
        cancellationRel.setOnClickListener(this);
        baseFareTv= view.findViewById(R.id.baseFareTv);
        taxTv=  view.findViewById(R.id.taxTv);
        grossFareTv=  view.findViewById(R.id.grossFareTv);
        tdsTv=  view.findViewById(R.id.tdsTv);
        checkedAllImg=  view.findViewById(R.id.checkedAllImg);
        totalTvValue=  view.findViewById(R.id.totalTvValue);
//        pdfLinear=  view.findViewById(R.id.pdfLinear);
//        dialogLinear=  view.findViewById(R.id.dialogLinear);
        transactionIdTv=  view.findViewById(R.id.transactionIdTv);
        transactionTv=  view.findViewById(R.id.transactionTv);
        transactionDateTv=  view.findViewById(R.id.transactionDateTv);
        paymentTv=  view.findViewById(R.id.paymentTv);
        executiveTv=  view.findViewById(R.id.executiveTv);
        executiveMobileTv= view.findViewById(R.id.executiveMobileTv);
        agencyTv=  view.findViewById(R.id.agencyTv);
        linearAdd=  view.findViewById(R.id.linearAdd);
        cancelTv=  view.findViewById(R.id.cancelTv);
        discountTv= view.findViewById(R.id.discountTv);
        conveyanceTv=  view.findViewById(R.id.conveyanceTv);
        partialNoteTv= view.findViewById(R.id.partialNoteTv);
        remarkEdt= view.findViewById(R.id.remarkEdt);
        cancelTv.setOnClickListener(this);
        if(loginModel.Data.UserType.equalsIgnoreCase("D")){
            cancelTv.setEnabled(false);
            view.findViewById(R.id.selectAllLin).setVisibility(View.GONE);
            view.findViewById(R.id.cancelLin).setVisibility(View.GONE);
        }
//        pdfLinear.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        cancel1Tv=  view.findViewById(R.id.cancel1Tv);
        cancel11Tv=  view.findViewById(R.id.cancel11Tv);
        cancel2Tv=  view.findViewById(R.id.cancel2Tv);
        cancel21Tv=  view.findViewById(R.id.cancel21Tv);
        cancel3Tv=  view.findViewById(R.id.cancel3Tv);
        cancel31Tv=  view.findViewById(R.id.cancel31Tv);
        cancel4Tv=  view.findViewById(R.id.cancel4Tv);
        cancel41Tv=  view.findViewById(R.id.cancel41Tv);
        cancel5Tv=  view.findViewById(R.id.cancel4Tv);
        cancel51Tv=  view.findViewById(R.id.cancel41Tv);

        animUp = AnimationUtils.loadAnimation(context,
                R.anim.translate_up);
        animDown = AnimationUtils.loadAnimation(context,
                R.anim.translate_down);


        checkedAllImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedAll=isChecked;
                for(int i=0;i<noOfPerson;i++) {
                    if (isChecked) {
                        checkImg[i].setChecked(true);
                    } else {
                        checkImg[i].setChecked(false);
                    }
                }
            }
        });


        if(getArguments().getSerializable("model")!=null && getArguments().getSerializable("TxnListData")!=null)
        {
            commonResponse = (BusPrintResponse) getArguments().getSerializable("model");
            busListModel = (BusTransactionListResponseModel.BusListModel) getArguments().getSerializable("TxnListData");
            noOfPerson=commonResponse.inventoryItems.size();

//            for(int i=0;i<noOfPerson-1;i++)
//            {
//                person=person+commonResponse.inventoryItems.get(i).passenger.get(0).name+"\n";
//                seatNo=seatNo+commonResponse.inventoryItems.get(i).seatName+"\n";
//            }
            sourceCityTv.setText(commonResponse.From);
            destCityTv.setText(commonResponse.To);
            dayDateTv.setText(commonResponse.BookingDate);
            pnrNumberTv.setText(commonResponse.PNR);
//            pnrTv.setText(commonResponse.PNR);
            busTypeTv.setText(commonResponse.
                    BusType);
            operatorTv.setText(commonResponse.BusOpertor);
//            nameTv.setText(person);
//            reportingTimeTv.setText(commonResponse.BTIme);
//            departureTimeTv.setText(commonResponse.BTIme);
            boardingAddressTv.setText(commonResponse.BPoint);
            landmarkTv.setText(commonResponse.LandMark);
            transactionIdTv.setText(commonResponse.ReservationId);
            transactionTv.setText(commonResponse.Status);
            transactionDateTv.setText(commonResponse.DOJ);
            executiveMobileTv.setText(commonResponse.LeadMobile);
            agencyTv.setText(commonResponse.AgencyName);
            executiveTv.setText(busListModel.BusOpertor);
            paymentTv.setText(commonResponse.TxnMedium);


            addressIdTv.setText(commonResponse.BPoint);
//            seatNo_tv.setText(seatNo);

//            contactTv.setText(commonResponse.LeadMobile+"");

            conveyanceTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.ServiceCharge))+"");
            discountTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.Discount))+"");
            baseFareTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.BaseFare))+"");
            taxTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.ServiceCharge))+"");
            grossFareTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.TotalFare))+"");
            tdsTv.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.TDS))+"");
            totalTvValue.setText(Common.roundOffDecimalValue(Float.valueOf(commonResponse.NetFare))+"");

            String cancelPolicy=commonResponse.CancellationPolicy; //0:12:100:0;12:24:50:0;24:-1:10:0
            String arr[]=cancelPolicy.split(";");
            try {
                cancel1Tv.setText(arr[0].split(":")[0]+" hour to "+arr[0].split(":")[1]+" hour ");
                cancel11Tv.setText(arr[0].split(":")[2]);
//                cancel2Tv.setText(arr[1].split(":")[0]+" hour to "+arr[1].split(":")[1]+" hour");
                cancel2Tv.setText(arr[1].split(":")[0]+" hour to "+(Integer.parseInt(arr[1].split(":")[1])<0?"Before ":arr[1].split(":")[1]));
                cancel21Tv.setText(arr[1].split(":")[2]);
                cancel3Tv.setText(arr[2].split(":")[0]+" hour to "+(Integer.parseInt(arr[2].split(":")[1])<0?"Before ":arr[2].split(":")[1]));
                cancel31Tv.setText(arr[2].split(":")[2]);
                cancel4Tv.setText(arr[3].split(":")[0]+" hour to "+(Integer.parseInt(arr[3].split(":")[1])<0?"Before ":arr[3].split(":")[1]));
                cancel41Tv.setText(arr[3].split(":")[2]);
                cancel5Tv.setText(arr[4].split(":")[0]+" hour to "+(Integer.parseInt(arr[4].split(":")[1])<0?"Before ":arr[4].split(":")[1]));
                cancel51Tv.setText(arr[4].split(":")[2]);
            }catch (Exception e){

            }

            setTicketDetailDynamically();
        }
        if(commonResponse.PartialFlag.equalsIgnoreCase("0")) {
            partialNoteTv.setVisibility(View.VISIBLE);
            partialNoteTv.setText("Note: Partial cancellation not allowed.");
            checkedAllImg.setChecked(true);
            checkedAllImg.setClickable(false);
            for(int i=0;i<noOfPerson;i++) {
                checkImg[i].setChecked(true);
                checkImg[i].setClickable(false);
            }
        }else {
            partialNoteTv.setVisibility(View.GONE);
        }

        return view;
    }

    public void setTicketDetailDynamically() {

        nameTv=new TextView[noOfPerson];
        seatTv=new TextView[noOfPerson];
        pnrTv=new TextView[noOfPerson];
        ageTv=new TextView[noOfPerson];
        checkImg=new AppCompatCheckBox[noOfPerson];

        for(int i = 0; i< noOfPerson; i++) {
            final View wizard = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.bus_ticket_pass_detail_info, linearAdd, false);

            TextView nameTv = wizard.findViewById(R.id.nameTv);
            TextView seatTv = wizard.findViewById(R.id.seatTv);
            TextView pnrTv = wizard.findViewById(R.id.pnrTv);
            TextView ageTv = wizard.findViewById(R.id.ageTv);

            nameTv.setText(commonResponse.inventoryItems.get(i).passenger.get(0).name);
            seatTv.setText(commonResponse.inventoryItems.get(i).seatName+"");
            pnrTv.setText(commonResponse.ReservationId);
            ageTv.setText(commonResponse.inventoryItems.get(i).passenger.get(0).age+"");
            checkImg[i]=wizard.findViewById(R.id.checkImg);
            checkImg[i].setButtonDrawable(R.drawable.checkbox_custom);
            checkImg[i].setPadding((int) getResources().getDimension(R.dimen.small_margin),0,0,0);

            if(isDistributor()){
                checkImg[i].setVisibility(View.INVISIBLE);
                wizard.findViewById(R.id.cancelTv).setVisibility(View.INVISIBLE);
            }


            final int finalI = i;
            checkImg[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        if(seatNo.length()==0){
                            seatNo=commonResponse.inventoryItems.get(finalI).seatName;
                        }else{
                            seatNo=seatNo+","+commonResponse.inventoryItems.get(finalI).seatName;
                        }
//                        Toast.makeText(context, seatNo, Toast.LENGTH_SHORT).show();
//                        busTypeDialog = busTypeDialog +buttonView.getText().toString();
                    }else {
                        seatNo.replace(","+commonResponse.inventoryItems.get(finalI).seatName, "");
                        seatNo.replace(commonResponse.inventoryItems.get(finalI).seatName, "");
//                        seatNo=seatNo.replace(commonResponse.inventoryItems.get(finalI).seatName.toString(),"");
//                        Toast.makeText(context, seatNo, Toast.LENGTH_SHORT).show();
//                        busTypeDialog = busTypeDialog.replace(buttonView.getText().toString(),"");
                    }
                }
            });

            linearAdd.addView(wizard);
        }
    }

    private boolean isDistributor() {
        return loginModel.Data.UserType.equalsIgnoreCase("D");
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
        switch (v.getId()) {
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.cancelTv:
                BusCancelRequestModel requestModel=new BusCancelRequestModel();
                requestModel.DoneCardUser = loginModel.Data.DoneCardUser;
                requestModel.DeviceId = Common.getDeviceId(context);
                requestModel.LoginSessionId =EncryptionDecryptionClass.EncryptSessionId(
                        EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                requestModel.RsID =busListModel.ReservationID;
                requestModel.seatsToCancel=seatNo.split(",");
//                requestModel.seatsToCancel=new String[]{seatNo};
                requestModel.tin = commonResponse.PNR;
                requestModel.Remark= remarkEdt.getText().toString();
                Common.preventFrequentClick(cancelTv);
                if(Common.checkInternetConnection(context)){
                    if(remarkEdt.getText().toString().trim().length()>0) {
                        cancel(requestModel);
                    }else {
                        Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.totalFareRel:
                menuDownArrowClick(fareLayout, fareArrowImg);
                break;
            case R.id.cancellationRel:
                menuDownArrowClick(cancellationLayout, cancellationArrowImg);
                break;

        }
    }
    private void menuDownArrowClick(LinearLayout fareLayout, ImageView fareArrowImg) {
        if(fareLayout.getVisibility()==View.VISIBLE){
            fareLayout.startAnimation(animUp);
            fareLayout.setVisibility(View.GONE);
            fareArrowImg.setRotation(0);
        }else {
            fareLayout.startAnimation(animDown);
            fareLayout.setVisibility(View.VISIBLE);
            fareArrowImg.setRotation(180);
        }
    }

    private void cancel(BusCancelRequestModel requestModel) {
        showCustomDialog();
        new NetworkCall().callBusService(requestModel, ApiConstants.CancelBooking, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void responseHandler(ResponseBody response) {

        try {
            hideCustomDialog();
            BusCancelResponse commonResponse = new Gson().fromJson(response.string(), BusCancelResponse.class);
            if (commonResponse != null) {
                if(commonResponse.Status==1){
                    Toast.makeText(context,commonResponse.Description,Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context,commonResponse.Description, Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void makePdf(View view) {
        //Assuming your rootView is called mRootView like so
        View mRootView=view;

//First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
//            Toast.
        }

//Create a directory for your PDF
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "/JCT");
//        File pdfDir = new File(Environment.getDataDirectory()+ "/JCT");
//        File pdfDir = new File(\context.getFilesDir()+ "/JCT");

      /*  if (Environment.getExternalStorageState() == null) {
            pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "/JCT");
        }else {
            pdfDir = new File(Environment.getDataDirectory() + "/JCT");
        }*/
        if (!pdfDir.exists()){
            pdfDir.mkdir();
        }

        Bitmap screen=createBitmapFromView(context, mRootView);

        //Now create the name of your PDF file that you will generate
        File pdfFile = new File(pdfDir,"jct.pdf");

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            screen.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document,byteArray);
            document.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

//        Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(pdfDir,"jck.pdf"));
        intent.setDataAndType(uri, "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
//        startActivityForResult(intent,Activity.RESULT_OK);
    }

    private void addImage(Document document, byte[] byteArray) {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
        }
        catch (BadElementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        image.scaleAbsolute(300f,300f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait");
    }
    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }


    private Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels,  displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
