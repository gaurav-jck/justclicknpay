package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.BusTransactionListAdapter;
import com.justclick.clicknbook.model.BusTransactionListResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblPrintResponse;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.Fragment.bus.requestModel.BusPrintRequestModel;
import com.justclick.clicknbook.requestmodels.TransactionListRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class TrainFailedListFragment extends Fragment implements
        View.OnClickListener{
    private final int START_DATE=1, END_DATE=2, REFUND_OTP=1, REFUND=2;
    private final int PRINT=1, CALL_AGENT=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<BusTransactionListResponseModel.BusListModel> refundArrayList;
    private BusTransactionListAdapter rblRefundListAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv;
    private ImageView backArrow;
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=20, totalPageCount=0;
    private TransactionListRequestModel transactionListRequestModel;
    private LoginModel loginModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener=(ToolBarHideFromFragmentListener)context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        refundArrayList =new ArrayList<>();
        transactionListRequestModel=new TransactionListRequestModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        initializeDates();

    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();
        dateFormat = Common.getToFromDateFormat();
        dayFormat = Common.getShortDayFormat();

        //default start Date
        startDateCalendar=Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        //default end Date
        endDateCalendar = Calendar.getInstance();
        endDateDay=endDateCalendar.get(Calendar.DAY_OF_MONTH);
        endDateMonth=endDateCalendar.get(Calendar.MONTH);
        endDateYear=endDateCalendar.get(Calendar.YEAR);

        startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
        endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());

    }



    private void setDates() {
        //set default date
        startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                dayFormat.format(endDateCalendar.getTime())+" "+
                dateFormat.format(endDateCalendar.getTime())
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_train_failed_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startDateTv = (TextView) view.findViewById(R.id.startDateTv);
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
//        titleChangeListener.onToolBarTitleChange(getString(R.string.refund));
//        fragmentBackPressListener.onFragmentBackPress(agencyList);


        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);
        setDates();
//        view.findViewById(R.id.lin_sortList).setOnClickListener(this);

        //initialize date values
//        setDates();

//        addValueToModel();

        rblRefundListAdapter =new BusTransactionListAdapter(context, new  BusTransactionListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<BusTransactionListResponseModel.BusListModel> list, int position) {
                switch (view.getId()){
                    case R.id.print_tv:
                        try {
                            BusPrintRequestModel requestModel= new BusPrintRequestModel();
                            requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            requestModel.DeviceId=Common.getDeviceId(context);
                            requestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            requestModel.ReservationID="B26127LK7D000001";
                            requestModel.TicketNo="";

                            if(Common.checkInternetConnection(context)) {
                                print(requestModel);
                            }else {
                                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                        }

                        break;
                }
            }
        }, refundArrayList);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rblRefundListAdapter);

        if(refundArrayList!=null && refundArrayList.size()==0) {
            pageStart=1;
            pageEnd=20;
            transactionListRequestModel.EndPage = pageEnd+"";
            transactionListRequestModel.StartPage =pageStart+"";
            transactionListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            transactionListRequestModel.DeviceId=Common.getDeviceId(context);
            transactionListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            transactionListRequestModel.FromDate=startDateToSend+"";
            transactionListRequestModel.ToDate=endDateToSend+"";
            transactionListRequestModel.AccountNo="";
            transactionListRequestModel.ReservationID="";
            transactionListRequestModel.MobileNo="";
            transactionListRequestModel.TxnTpe="";

            if(Common.checkInternetConnection(context)) {
                callAgent(transactionListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            rblRefundListAdapter.notifyDataSetChanged();
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        return view;
    }

    public void print(BusPrintRequestModel printRequestModel) {
        new NetworkCall().callRblLongTimeService(printRequestModel, ApiConstants.TicketInfo, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response,PRINT);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                },true);
    }

    private void openPrintDialog(final RblPrintResponse rblprintResponseModel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.print_dialog);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final TextView reservationID_tv= (TextView) dialog.findViewById(R.id.reservationID_tv);
        final TextView acount_tv= (TextView) dialog.findViewById(R.id.acount_tv);
        final TextView ifsc_Code_tv= (TextView) dialog.findViewById(R.id.ifsc_Code_tv);
        final TextView beneficiaryName_tv= (TextView) dialog.findViewById(R.id.beneficiaryName_tv);
        final TextView senderName_tv= (TextView) dialog.findViewById(R.id.senderName_tv);
        final TextView senderMobileNo_tv= (TextView) dialog.findViewById(R.id.senderMobileNo_tv);
        final TextView dateTimeTransaction_tv= (TextView) dialog.findViewById(R.id.dateTimeTransaction_tv);
        final TextView transactionType_tv= (TextView) dialog.findViewById(R.id.transactionType_tv);
        final TextView txnAmt_tv= (TextView) dialog.findViewById(R.id.txnAmt_tv);
        final TextView transactionID_tv= (TextView) dialog.findViewById(R.id.transactionID_tv);
        final TextView bankrefno_tv= (TextView) dialog.findViewById(R.id.bankrefno_tv);
        final TextView bCAgentName_tv= (TextView) dialog.findViewById(R.id.bCAgentName_tv);

        reservationID_tv.setText(rblprintResponseModel.ReservationId);
        acount_tv.setText(rblprintResponseModel.AccountNo);
        ifsc_Code_tv.setText(rblprintResponseModel.IFSCCode);
        beneficiaryName_tv.setText(rblprintResponseModel.BenName);
        senderName_tv.setText(rblprintResponseModel.SenderName);
        senderMobileNo_tv.setText(rblprintResponseModel.SenderMobile);
        dateTimeTransaction_tv.setText(rblprintResponseModel.TranDateTime);
        transactionType_tv.setText(rblprintResponseModel.TranType);
        reservationID_tv.setText(rblprintResponseModel.ReservationId);
        reservationID_tv.setText(rblprintResponseModel.ReservationId);
        txnAmt_tv.setText(rblprintResponseModel.TxnAmt);
        transactionID_tv.setText(rblprintResponseModel.RBLTranId);
        bankrefno_tv.setText(rblprintResponseModel.BankrefNo);
        bCAgentName_tv.setText(rblprintResponseModel.BCAgentName);
        dialog.show();

        dialog.findViewById(R.id.print_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    makePdf(dialog.findViewById(R.id.dialogLinear));
                }catch (Exception e){
                    Toast.makeText(context, "Unable to create pdf file", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

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

    private void responseHandler(ResponseBody response, int TYPE) {
        switch (TYPE){
            case CALL_AGENT:
                try {
                    BusTransactionListResponseModel commonResponse = new Gson().fromJson(response.string(), BusTransactionListResponseModel.class);
                    if(commonResponse!=null){
                        if(commonResponse.StatusCode.equalsIgnoreCase("0")){
                            refundArrayList.addAll(commonResponse.Data);
                            totalPageCount = Integer.parseInt(commonResponse.Data.get(0).TCount);
                            rblRefundListAdapter.notifyDataSetChanged();
                        }else if(commonResponse.StatusCode.equalsIgnoreCase("2")){
                            refundArrayList.clear();
                            rblRefundListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }else {
                            refundArrayList.clear();
                            rblRefundListAdapter.notifyDataSetChanged();
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PRINT:
                try {
                    RblPrintResponse commonResponse = new Gson().fromJson(response.string(), RblPrintResponse.class);
                    if (commonResponse != null) {
                        if(commonResponse.StatusCode.equalsIgnoreCase("0")){
                            openPrintDialog(commonResponse);
                        }else {
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
    }

    private void openFilterDialog() {
        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.filter_dialog_layout);
        start_date_value_tv= (TextView) filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv= (TextView) filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv= (TextView) filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv= (TextView) filterDialog.findViewById(R.id.end_day_value_tv);
        filterDialog.findViewById(R.id.cancel_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.submit_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.startDateLinear).setOnClickListener(this);
        filterDialog.findViewById(R.id.endDateLinear).setOnClickListener(this);
        start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
        start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));
        end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
        end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));
        filterDialog.show();
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount = layoutManager.getItemCount();
//            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = rblRefundListAdapter.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//            if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount <= totalPageCount
                    && dy>0) {

                if(!(pageEnd>totalItemCount)) {
                    pageStart = pageStart + 20;
                    pageEnd = pageEnd + 20;
                    transactionListRequestModel.EndPage =pageEnd+"";
                    transactionListRequestModel.StartPage =pageStart+"";
                    transactionListRequestModel.FromDate=startDateToSend+"";
                    transactionListRequestModel.ToDate=endDateToSend+"";
                    transactionListRequestModel.AccountNo="";
                    transactionListRequestModel.ReservationID="";
                    transactionListRequestModel.TxnTpe="";
                    callAgent( transactionListRequestModel, NO_PROGRESS);
                }


            }
        }
    };

    public void callAgent(TransactionListRequestModel transactionListRequestModel, boolean progress) {
        boolean isProgress=false;
        if(progress && !MyCustomDialog.isDialogShowing()){
            isProgress=true;
            MyCustomDialog.showCustomDialog(context, getResources().getString(R.string.please_wait));
        }
        new NetworkCall().callBusService(transactionListRequestModel, ApiConstants.TxnList, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            MyCustomDialog.hideCustomDialog();
                            responseHandler(response, CALL_AGENT);
                        }else {
                            MyCustomDialog.hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.cancel_tv:
                if(filterDialog!=null)
                    filterDialog.dismiss();
                break;
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.submit_tv:
                filterDialog.dismiss();
                refundArrayList.clear();
                rblRefundListAdapter.notifyDataSetChanged();
                //set date to fragment

                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );
                pageStart=1;
                pageEnd=20;
                transactionListRequestModel.EndPage =pageEnd+"";
                transactionListRequestModel.StartPage =pageStart+"";
                transactionListRequestModel.FromDate=startDateToSend+"";
                transactionListRequestModel.ToDate=endDateToSend+"";
                transactionListRequestModel.AccountNo="";
                transactionListRequestModel.ReservationID="";
                transactionListRequestModel.TxnTpe="";

                if(Common.checkInternetConnection(context)) {
                    callAgent(transactionListRequestModel, SHOW_PROGRESS);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.startDateLinear:
                openStartDatePicker();
                break;
            case R.id.endDateLinear:
                openEndDatePicker();
                break;

            case R.id.lin_dateFilter:
                openFilterDialog();
                break;


        }
    }

    private void openStartDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateCalendar.set(year, monthOfYear, dayOfMonth);
                        if (startDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            startDateDay=dayOfMonth;
                            startDateMonth=monthOfYear;
                            startDateYear=year;
                            start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
                            start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));
                            startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
                        }
                    }
                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    private void openEndDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDateCalendar = Calendar.getInstance();
                        endDateCalendar.set(year, monthOfYear, dayOfMonth);

                        if (endDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            endDateDay=dayOfMonth;
                            endDateMonth=monthOfYear;
                            endDateYear=year;

                            end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
                            end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));

                            endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
                        }
                    }
                },endDateYear, endDateMonth, endDateDay);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDetach() {
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
