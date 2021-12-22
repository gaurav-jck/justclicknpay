package com.justclick.clicknbook.Fragment.jctmoney;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.AutocompleteAdapter;
import com.justclick.clicknbook.adapter.JctMoneyTransactionListAdapter;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.JctMoneyRefundResponse;
import com.justclick.clicknbook.model.JctMoneyTransactionListResponseModel;
import com.justclick.clicknbook.model.JctMoneyTransactionStatusResponse;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblPrintResponse;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentNameRequestModel;
import com.justclick.clicknbook.requestmodels.JctMoneyRefundRequestModel;
import com.justclick.clicknbook.requestmodels.JctMoneyTransactionListRequestModel;
import com.justclick.clicknbook.requestmodels.RblPrintRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Lenovo on 03/25/2017.
 */

public class JctMoneyTransactionListFragment extends Fragment implements
        View.OnClickListener{
    private final int START_DATE=1, END_DATE=2, REFUND_OTP_REQUEST =1, REFUND=2;
    private final int PRINT=1, CALL_AGENT=2;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private Context context;
    private RecyclerView recyclerView;
    private String startDateToSend, endDateToSend;
    private ArrayList<JctMoneyTransactionListResponseModel.Data> arrayList;
    private JctMoneyTransactionListAdapter listAdapter;
    private LinearLayoutManager layoutManager;
    private TextView startDateTv, noRecordTv;
    private Dialog filterDialog, refundDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private int pageStart=1, pageEnd=10, totalPageCount=0;
    private JctMoneyTransactionListRequestModel transactionListRequestModel;
    private LoginModel loginModel;
    private EditText otp_edt;
    private JctMoneyRefundRequestModel refundRequestModel;
    private String txnId="", txnStatus ="",agentName="", agentDoneCard="", CustMobile="";
    private int txnStatusPosition=0;
    private AutocompleteAdapter autocompleteAdapter;
    private AgentNameModel agentNameModel;
    private ListView list_agent;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        arrayList =new ArrayList<>();
        transactionListRequestModel=new JctMoneyTransactionListRequestModel();
        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);
        refundRequestModel=new JctMoneyRefundRequestModel();
        agentNameModel=new AgentNameModel();
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
        View view= inflater.inflate(R.layout.fragment_jctmoney_transaction_list, container, false);
        recyclerView =  view.findViewById(R.id.recyclerView);
        startDateTv = view.findViewById(R.id.startDateTv);
        noRecordTv = view.findViewById(R.id.noRecordTv);
        noRecordTv.setVisibility(View.GONE);

//        titleChangeListener.onToolBarTitleChange(getString(R.string.refund));
//        fragmentBackPressListener.onFragmentBackPress(agencyList);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);


        view.findViewById(R.id.lin_dateFilter).setOnClickListener(this);
        view.findViewById(R.id.linFilter).setOnClickListener(this);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);
//        view.findViewById(R.id.lin_sortList).setOnClickListener(this);

        //initialize date values
        setDates();

//        addValueToModel();

        listAdapter =new JctMoneyTransactionListAdapter(context, new  JctMoneyTransactionListAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<JctMoneyTransactionListResponseModel.Data> list,JctMoneyTransactionListResponseModel.Data data, int position) {
                switch (view.getId()){
                    case R.id.refund_tv:
                        try {
                            refundRequestModel= new JctMoneyRefundRequestModel();
                            refundRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            refundRequestModel.DeviceId=Common.getDeviceId(context);
                            refundRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            refundRequestModel.MobileNo=data.CustMobile;
                            refundRequestModel.OTP= "";
//                            refundRequestModel.RefID= data.Data.get(position).TID;
                            refundRequestModel.RefID= data.Data.get(position).ReferenceID;
                            sendRefundOtp(data, position, view.findViewById(R.id.refund_tv));
//                            openRefundDialog(list.get(position));
                        }catch (Exception e){

                        }
                        break;

                    case R.id.status:
                        try {
                            JctMoneyRefundRequestModel refundRequestModel= new JctMoneyRefundRequestModel();
                            refundRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            refundRequestModel.DeviceId=Common.getDeviceId(context);
                            refundRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            refundRequestModel.MobileNo=data.CustMobile;
                            refundRequestModel.TxnID= data.Data.get(position).TID;
                            refundRequestModel.RefID= data.Data.get(position).ReferenceID;
                            if(!(loginModel.Data.UserType.equalsIgnoreCase("D"))) {
                                checkStatus(refundRequestModel, data, position);
                            }
//                            openRefundDialog(list.get(position));
                        }catch (Exception e){

                        }
                        break;

                    case R.id.print_tv:
                        try {
                            openPrintDialog(list.get(position));
                          /*  RblPrintRequestModel requestModel= new RblPrintRequestModel();
                            requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                            requestModel.DeviceId=Common.getDeviceId(context);
                            requestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                            requestModel.Sid=list.get(position).ReservationId;

                            if(Common.checkInternetConnection(context)) {
                                openPrintDialog(list.get(position));
                            }else {
                                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                            }
*/
                        }catch (Exception e){
                            Toast.makeText(context,"Enable to print data",Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        }, arrayList);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listAdapter);

        if(arrayList !=null && arrayList.size()==0) {
            pageStart=1;
            pageEnd=10;
            transactionListRequestModel.EndPage = pageEnd+"";
            transactionListRequestModel.StartPage =pageStart+"";
            transactionListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
            transactionListRequestModel.DeviceId=Common.getDeviceId(context);
            transactionListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                    EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
            transactionListRequestModel.FromDate=startDateToSend+"";
            transactionListRequestModel.ToDate=endDateToSend+"";
            transactionListRequestModel.ReservationID="";

            if(Common.checkInternetConnection(context)) {
                callAgent(transactionListRequestModel, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
            }
        }else {
            listAdapter.notifyDataSetChanged();
        }

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

        return view;
    }

    private void checkStatus(JctMoneyRefundRequestModel refundRequestModel, final JctMoneyTransactionListResponseModel.Data data, final int position) {
        new NetworkCall().callJctMoneyService(refundRequestModel, ApiConstants.TransactionStatus, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            try {
                                JctMoneyTransactionStatusResponse response1=new Gson().fromJson(response.string(), JctMoneyTransactionStatusResponse.class);
                                if(response1!=null && response1.statusCode==1 && response1.RefID!=null && response1.RefID.size()>0){
                                    Toast.makeText(context, response1.RefID.get(0).txstatus_desc, Toast.LENGTH_SHORT).show();
                                    arrayList.get(arrayList.indexOf(data)).Data.get(position).txstatus_desc=response1.RefID.get(0).txstatus_desc;
                                    listAdapter.notifyItemChanged(arrayList.indexOf(data));
                                }else {
                                    Toast.makeText(context, response1!=null?response1.message:R.string.response_failure_message+"", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void print(RblPrintRequestModel printRequestModel) {
        new NetworkCall().callJctMoneyService(printRequestModel, ApiConstants.PrintTicket, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response,PRINT);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openPrintDialog(final JctMoneyTransactionListResponseModel.Data responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jct_money_print_dialog);
        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final TextView beneNameTv= dialog.findViewById(R.id.beneNameTv);
        final TextView mobileTv= dialog.findViewById(R.id.mobileTv);
        final TextView bankNameTv= dialog.findViewById(R.id.bankNameTv);
        final TextView accountNoTv= dialog.findViewById(R.id.accountNoTv);
        final TextView txnTypeTv= dialog.findViewById(R.id.txnTypeTv);
        final TextView rrnNoTv= dialog.findViewById(R.id.rrnNoTv);
        final TextView remitAmountTv= dialog.findViewById(R.id.remitAmountTv);
        final TextView txnDateTv= dialog.findViewById(R.id.txnDateTv);
        final TextView agencyNameTv= dialog.findViewById(R.id.agencyNameTv);

        beneNameTv.setText(responseModel.Name);
        mobileTv.setText(responseModel.CustMobile);
        bankNameTv.setText(responseModel.BankName);
        accountNoTv.setText(responseModel.AccountNo);
        txnTypeTv.setText(responseModel.TxnType);
        for(int i=0; i<responseModel.Data.size(); i++){
            if(i==responseModel.Data.size()-1){
                rrnNoTv.setText(rrnNoTv.getText()+responseModel.Data.get(i).BankRefNo);}
            else {
                rrnNoTv.setText(rrnNoTv.getText()+responseModel.Data.get(i).BankRefNo+", ");}
        }
        remitAmountTv.setText(responseModel.TxnAMt);
        txnDateTv.setText(responseModel.TxnDate);
        agencyNameTv.setText(responseModel.AgencyName);

        dialog.findViewById(R.id.print_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void makePdf(View view, String reservationId) throws Exception{
        //Assuming your rootView is called mRootView like so
        View mRootView=view;

//First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
//            Toast.
        }

//Create a directory for your PDF
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "/JCT_DMT");

        if (!pdfDir.exists()){
            pdfDir.mkdir();
        }

        Bitmap screen=createBitmapFromView(context, mRootView);

        //Now create the name of your PDF file that you will generate
        File pdfFile = new File(pdfDir,reservationId+".pdf");

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
        Uri uri = Uri.fromFile(new File(pdfDir,reservationId+".pdf"));
        intent.setDataAndType(uri, "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
//        startActivityForResult(intent,Activity.RESULT_OK);
    }

    private Bitmap createBitmapFromView(Context context, View view) throws Exception {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

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


    private void sendRefundOtp(JctMoneyTransactionListResponseModel.Data refundListModel, int position, View refundView) {
//        RefundOTP
        refund(refundListModel,REFUND_OTP_REQUEST, ApiConstants.RRefundOTP, refundView);
    }

    private void openRefundDialog(final JctMoneyTransactionListResponseModel.Data rblRefundListResponseModel, final View refundView) {
        if(refundDialog==null) {
            refundDialog = new Dialog(context);
            refundDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            refundDialog.setContentView(R.layout.jct_money_refund_dialog);
            final Window window = refundDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        otp_edt=  refundDialog.findViewById(R.id.otp_edt);
        final TextView continue_tv= refundDialog.findViewById(R.id.continue_tv);
        final TextView resend_tv= refundDialog.findViewById(R.id.resend_tv);
        final TextView cancel_tv= refundDialog.findViewById(R.id.cancel_tv);

        continue_tv.setTypeface(Common.TitleTypeFace(context));
        resend_tv.setTypeface(Common.TitleTypeFace(context));
        cancel_tv.setTypeface(Common.TitleTypeFace(context));

        continue_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(otp_edt.getText().toString().trim().length()>=4){
                        refundRequestModel.OTP= otp_edt.getText().toString().trim();
                        refund(rblRefundListResponseModel,  REFUND, ApiConstants.GetRefund, refundView);
                        refundDialog.dismiss();
                    }else {
//                    makePdf(dialog.findViewById(R.id.dialogLin));
                        Toast.makeText(context, R.string.empty_and_invalid_otp,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(context, R.string.exception_message,Toast.LENGTH_SHORT).show();
                }
            }
        });

        resend_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refund(rblRefundListResponseModel,  REFUND_OTP_REQUEST, ApiConstants.RRefundOTP, refundView);
//                dialog.dismiss();
            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundDialog.dismiss();
            }
        });

        if(!refundDialog.isShowing()){
            refundDialog.show();
        }
    }

    private void refund(final JctMoneyTransactionListResponseModel.Data refundListModel,
                        final int type, String method, final View refundView) {
        new NetworkCall().callJctMoneyService(refundRequestModel, method, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            try {
                                JctMoneyRefundResponse commonResponse = new Gson().fromJson(response.string(), JctMoneyRefundResponse.class);
                                if(commonResponse!=null){
                                    if(commonResponse.statusCode==1 && type== REFUND_OTP_REQUEST){
                                        openRefundDialog(refundListModel, refundView);
                                        Toast.makeText(context,commonResponse.message, Toast.LENGTH_LONG).show();
                                    }else if(commonResponse.statusCode==1 && type==REFUND){
                                        Toast.makeText(context,commonResponse.message, Toast.LENGTH_LONG).show();
                                        refundView.setVisibility(View.GONE);
                                        arrayList.clear();
                                        pageStart=1;
                                        pageEnd=10;
                                        transactionListRequestModel.EndPage =pageEnd+"";
                                        transactionListRequestModel.StartPage =pageStart+"";
                                        transactionListRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
                                        transactionListRequestModel.DeviceId=Common.getDeviceId(context);
                                        transactionListRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                                        transactionListRequestModel.FromDate=startDateToSend+"";
                                        transactionListRequestModel.ToDate=endDateToSend+"";
                                        transactionListRequestModel.ReservationID="";
                                        callAgent(transactionListRequestModel, SHOW_PROGRESS);
                                    }else{
                                        Toast.makeText(context,commonResponse.message, Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void responseHandler(ResponseBody response, int TYPE) {
        switch (TYPE){
            case CALL_AGENT:
                try {
                    JctMoneyTransactionListResponseModel commonResponse = new Gson().fromJson(response.string(), JctMoneyTransactionListResponseModel.class);
                    if(commonResponse!=null){
                        if(commonResponse.StatusCode.equalsIgnoreCase("0")){
                            noRecordTv.setVisibility(View.GONE);
                            arrayList.addAll(commonResponse.Data);
                            totalPageCount = Integer.parseInt(commonResponse.Data.get(0).TCount);
                            listAdapter.notifyDataSetChanged();
                            if(commonResponse.Data!=null &&
                                    commonResponse.Data.size()==0){
                                noRecordTv.setVisibility(View.VISIBLE);
                            }
                        }else if(commonResponse.StatusCode.equalsIgnoreCase("2")){
                            arrayList.clear();
                            listAdapter.notifyDataSetChanged();
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }else {
                            noRecordTv.setVisibility(View.VISIBLE);
                            arrayList.clear();
                            listAdapter.notifyDataSetChanged();
                            Toast.makeText(context,commonResponse.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        noRecordTv.setVisibility(View.VISIBLE);
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    noRecordTv.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
                break;

            case PRINT:
                try {
                    RblPrintResponse commonResponse = new Gson().fromJson(response.string(), RblPrintResponse.class);
                    if (commonResponse != null) {
                        if(commonResponse.StatusCode.equalsIgnoreCase("0")){
//                            openPrintDialog(commonResponse);
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
        start_date_value_tv= filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv= filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv= filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv= filterDialog.findViewById(R.id.end_day_value_tv);
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

    public AgentNameModel call_agent(AgentNameRequestModel model) {
//        agent.DATA.clear()
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

        Call<AgentNameModel> call = apiService.agentNamePost(ApiConstants.GetAgentName, model);
        call.enqueue(new Callback<AgentNameModel>() {
            @Override
            public void onResponse(Call<AgentNameModel> call, Response<AgentNameModel> response) {
                try {
                    agentNameModel = response.body();
                    if(agentNameModel.StatusCode.equalsIgnoreCase("0")) {
                        autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
                        list_agent.setAdapter(autocompleteAdapter);
                        list_agent.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<AgentNameModel> call, Throwable t) {
                int a=0;
//                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        });
        return agentNameModel;
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
            int totalItemCount = listAdapter.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//            if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount <= totalPageCount
                    && dy>0) {

                if(!(pageEnd>totalItemCount)) {
                    pageStart = pageStart + 10;
                    pageEnd = pageEnd + 10;
                    transactionListRequestModel.EndPage =pageEnd+"";
                    transactionListRequestModel.StartPage =pageStart+"";
                    transactionListRequestModel.FromDate=startDateToSend+"";
                    transactionListRequestModel.ToDate=endDateToSend+"";
                    transactionListRequestModel.ReservationID=txnId;
                    transactionListRequestModel.Name=agentName;
                    transactionListRequestModel.AgentID=agentDoneCard;
                    transactionListRequestModel.TxnID=txnId;
                    transactionListRequestModel.txnstatus=txnStatus;
                    transactionListRequestModel.CustMobile=CustMobile;
                    callAgent( transactionListRequestModel, NO_PROGRESS);
                }
            }
        }
    };

    public void callAgent(JctMoneyTransactionListRequestModel transactionListRequestModel, boolean progress) {
        if(progress && !MyCustomDialog.isDialogShowing()){
            showCustomDialog();
        }
        new NetworkCall().callJctMoneyTxnListService(transactionListRequestModel, ApiConstants.JctMoneyTxnList, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, CALL_AGENT);
                        }else {
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                        hideCustomDialog();
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
            case R.id.submit_tv:

                long diff = endDateCalendar.getTime().getTime() - startDateCalendar.getTime().getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if(days<0){
                    Toast.makeText(context, "you have selected wrong dates", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(days>=15){
                    Toast.makeText(context, "please select 15 days data", Toast.LENGTH_SHORT).show();
                    break;
                }

                filterDialog.dismiss();
                arrayList.clear();
                listAdapter.notifyDataSetChanged();
                //set date to fragment

                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );
                pageStart=1;
                pageEnd=10;
                transactionListRequestModel.EndPage =pageEnd+"";
                transactionListRequestModel.StartPage =pageStart+"";
                transactionListRequestModel.FromDate=startDateToSend+"";
                transactionListRequestModel.ToDate=endDateToSend+"";
                transactionListRequestModel.ReservationID="";

                if(Common.checkInternetConnection(context)) {
                    callAgent(transactionListRequestModel, SHOW_PROGRESS);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.startDateLinear:
                try {
                    openStartDatePicker();
                }catch (Exception e){

                }
                break;

            case R.id.endDateLinear:
                try {
                    openEndDatePicker();
                }catch (Exception e){

                }
                break;

            case R.id.lin_dateFilter:
                openFilterDialog();
                break;
            case R.id.linFilter:
                openListFilterDialog();
                break;
            case R.id.back_arrow:
                getFragmentManager().popBackStack();
                break;



        }
    }

    private void openListFilterDialog() {
        txnId="";
        txnStatus ="";
        agentDoneCard="";
        agentName="";
        CustMobile="";
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.jct_txn_list_filter);

        final Spinner statusSpinner= dialog.findViewById(R.id.statusSpinner);
        final EditText txnEdt= dialog.findViewById(R.id.txnEdt);
        final EditText mobileEdt= dialog.findViewById(R.id.mobileEdt);
        final EditText agent_search_edt =  dialog.findViewById(R.id.agent_search_edt);
        list_agent =  dialog.findViewById(R.id.list_agent);
//        final RelativeLayout agent_search_rel = (RelativeLayout) dialog.findViewById(R.id.agent_search_rel);
//        final EditText agentEdt= dialog.findViewById(R.id.agentEdt);

        if(loginModel.Data.UserType.equalsIgnoreCase("A")){
            agent_search_edt.setVisibility(View.GONE);
            dialog.findViewById(R.id.agentLabelTv).setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.agent_details_spinner_item_dropdown, R.id.operator_tv, getResources().
                getStringArray(R.array.jct_list_array));
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setSelection(txnStatusPosition);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    txnStatus="";
                    txnStatusPosition=0;
                }else {
                    txnStatus = statusSpinner.getSelectedItem().toString();
                    txnStatusPosition=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.findViewById(R.id.cancelTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.applyTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                txnId=txnEdt.getText().toString().trim();
                CustMobile=mobileEdt.getText().toString().trim();

                applyFilter();
            }
        });

        if(agentNameModel!=null && agentNameModel.Data!=null && agentNameModel.Data.size()>0){
            autocompleteAdapter = new AutocompleteAdapter(context, agentNameModel);
            list_agent.setAdapter(autocompleteAdapter);
        }

        list_agent.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        agentName = autocompleteAdapter.getItem(position).AgencyName;
                        agentDoneCard=agentName.substring(agentName.indexOf("(")+1,agentName.indexOf(")"));
//                        Active = autocompleteAdapter.getItem(position).Active;
                        list_agent.setVisibility(View.GONE);
//                        agent_name_tv.setText(agentName);
//                        agent_search_rel.setVisibility(View.VISIBLE);
                        agent_search_edt.setText(agentName);
                        agent_search_edt.setSelection(agent_search_edt.getText().length());
                        Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                    }
                });

        agent_search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agent_search_edt.setText("");
                agentName="";
                agentDoneCard="";
//                Active="False";
                list_agent.setVisibility(View.VISIBLE);
            }
        });

        agent_search_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Common.checkInternetConnection(context)) {
                    if(s.length()>=2) {
                        String term = s.toString();

                        AgentNameRequestModel model=new AgentNameRequestModel();
                        model.AgencyName=term;
                        model.DeviceId=Common.getDeviceId(context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.Type=loginModel.Data.UserType;
                        model.RequiredType=loginModel.Data.UserType;

                        call_agent(model);
//                    Toast.makeText(context, s.toString() + " " + start + " " + before + " " + count, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final Window window= dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    private void applyFilter() {
        arrayList.clear();
        listAdapter.notifyDataSetChanged();
        //set date to fragment

        startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                dayFormat.format(endDateCalendar.getTime())+" "+
                dateFormat.format(endDateCalendar.getTime())
        );
        pageStart=1;
        pageEnd=10;
        transactionListRequestModel.EndPage =pageEnd+"";
        transactionListRequestModel.StartPage =pageStart+"";
        transactionListRequestModel.FromDate=startDateToSend+"";
        transactionListRequestModel.ToDate=endDateToSend+"";
        transactionListRequestModel.ReservationID=txnId;
        transactionListRequestModel.Name=agentName;
        transactionListRequestModel.AgentID=agentDoneCard;
        transactionListRequestModel.TxnID=txnId;
        transactionListRequestModel.txnstatus=txnStatus;
        transactionListRequestModel.CustMobile=CustMobile;

        if(Common.checkInternetConnection(context)) {
            callAgent(transactionListRequestModel, SHOW_PROGRESS);
        }else {
            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
        }
    }

    private ArrayAdapter<String> setSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
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
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, -15);
//        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
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

    private void hideSoftInputFromDialog(Dialog dialog) {
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getCurrentFocus().getWindowToken(), 0);}
        catch (NullPointerException e){}
    }

    @Override
    public void onDetach() {
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
        super.onDetach();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    otp_edt.setText(message);
                }
            }catch (Exception e)
            {

            }
        }
    };

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,context.getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

}
