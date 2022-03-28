package com.justclick.clicknbook.Fragment.billpay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.RechargeRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;

import okhttp3.ResponseBody;

import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.DATACARD_TYPE;
import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.ELECTRICITY_TYPE;
import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.GAS_TYPE;
import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.LANDLINE_TYPE;
import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.POSTPAID_TYPE;
import static com.justclick.clicknbook.Fragment.billpay.BillPayFragment.WATER_TYPE;

public class BillConfirmationFragment extends Fragment {

    private final int GenerateToken=1, BillPay=2;
    private Context context;
    private RechargeRequestModel rechargeRequestModel;
    private TextView payBtn;
    private LoginModel loginModel;
    private BillPayFragment.FetchBillResponseModel.payfetchbilllist billDetail;
    private BillPayFragment.OperatorResponseModel.operatorlistDetails operatorDetail;
    private int rechargeType;
    private TextInputLayout idInput, amountInput,nameInput,dateInput;
    private EditText idEdt,amountEdt, nameEdt,dateEdt;

    public BillConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_billpay_confirmation, container, false);

        loginModel=new LoginModel();
        loginModel=MyPreferences.getLoginData(loginModel,context);

        initializeView(view);

        Bundle bundle=getArguments();
        try {
            if(bundle!=null){
                rechargeType=bundle.getInt("RechargeType");
                billDetail= (BillPayFragment.FetchBillResponseModel.payfetchbilllist) bundle.getSerializable("BillDetailResponse");
                operatorDetail= (BillPayFragment.OperatorResponseModel.operatorlistDetails) bundle.getSerializable("OperatorData");
                setValues();
            }
        }catch (Exception e){

        }

        return view;
    }

    private void setValues() {
        rechargeRequestModel=new RechargeRequestModel();
        rechargeRequestModel.DeviceId=Common.getDeviceId(context);
        rechargeRequestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        rechargeRequestModel.DoneCardUser =loginModel.Data.DoneCardUser;
        rechargeRequestModel.Password = MyPreferences.getLoginPassword(context);

        idInput.setHint(operatorDetail.displayname);
        idEdt.setText(billDetail.canumber);
        amountInput.setHint("Amount");
        amountEdt.setText(billDetail.billAmount+"");
        nameInput.setHint("Customer Name");
        nameEdt.setText(billDetail.customerName);
        dateInput.setHint("Due Date");
        dateEdt.setText(billDetail.dueDate);

        switch (rechargeType){
            case POSTPAID_TYPE:

                break;
            case WATER_TYPE:

                break;
            case DATACARD_TYPE:

                break;
            case ELECTRICITY_TYPE:

                break;
            case LANDLINE_TYPE:

                break;
            case GAS_TYPE:

                break;
        }
    }

    private void initializeView(View view) {
        idInput=view.findViewById(R.id.idInput);
        idEdt=view.findViewById(R.id.idEdt);
        nameInput=view.findViewById(R.id.nameInput);
        nameEdt=view.findViewById(R.id.nameEdt);
        amountInput=view.findViewById(R.id.amountInput);
        amountEdt=view.findViewById(R.id.amountEdt);
        dateInput=view.findViewById(R.id.dateInput);
        dateEdt=view.findViewById(R.id.dateEdt);
        payBtn=  view.findViewById(R.id.payBtn);

        view.findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.checkInternetConnection(context)) {
                    Common.preventFrequentClick(payBtn);
                    getCredential();
                    Toast.makeText(context,"Bill",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public class GenerateToken{
        public String AgentCode, Merchant=ApiConstants.MerchantId, Mode="App";
        /*{
            "AgentCode": "JC0A13387",
                "Merchant": "JUSTCLICKTRAVELS",
                "Mode": "App"
        }*/
    }
    public class CheckResponseClass{
        public String statusCode, statusMessage;
        public ArrayList<credentialData> credentialData;
        public class credentialData{
            public String token, userData;
        }
    }

    class PayBillRequest{
        public String Operatorid,Canumber,billdate,dueDate,cellNumber,userName,AgentCode,Email,Suppliercode,Category,fetchbillstring;
        public String Lattitude="27.2233",Longitude="78.26535",Mode="offline",acceptPayment="",acceptPartPay="",
                Merchant=ApiConstants.MerchantId,Type="App", PayBillType="";
        public float BillAmount,Billnetamount;
        /*{"Operatorid": "12",
    "BillAmount": 12.0,
    "Billnetamount": 10.0,
    "Canumber": "200185759",
    "Lattitude": "27.2232",
    "Longitude": "78.26535",
    "Mode": "offline",
    "billdate": "2021-06-16",
    "dueDate": "2021-06-16",
    "acceptPayment": "",
    "acceptPartPay": "",
    "cellNumber": "385884999",
    "userName": "DUMMY NAME",
    "Merchant": "JUSTCLICKTRAVELS",
    "Type": "Web",
    "AgentCode": "JC0A13387",
    "Email": "jitender.p@justclicknpay.in ",
    "Suppliercode": "Paysprint",
    "Category": "Electricity",
    "fetchbillstring": "{\r\n  \"amount\": 1,\r\n  \"name\": \"DUMMY NAME\",\r\n  \"duedate\": \"2021-06-16\",\n  \"ad2\": \"HDA99719971\",\r\n  \"ad3\": \"VDA96093013\"\r\n}"}*/
    }

    class PayBillResponse{
        public String statusCode,statusMessage;
        ArrayList<billDetails> billDetails;
        class billDetails{
            float amount;
            String transactionId,acknowledgementNo,operatotrId,status;
        }
    }

    private void getCredential() {
        GenerateToken request=new GenerateToken();
        request.AgentCode=loginModel.Data.DoneCardUser;
        new NetworkCall().callLicService(request, ApiConstants.GenerateToken, context, "", "", true, (response, responseCode) -> {
            MyCustomDialog.hideCustomDialog();
            if(response!=null){
                responseHandler(response, GenerateToken);
            }else {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void responseHandler(ResponseBody response, int type) {
        try {
            if(type==GenerateToken){
                CheckResponseClass responseModel=new Gson().fromJson(response.string(),CheckResponseClass.class);
                if(responseModel!=null && responseModel.statusCode.equals("00")){
//                    Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_SHORT).show();
                    payBill(responseModel.credentialData.get(0).userData, responseModel.credentialData.get(0).token);
                }else {
                    Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_SHORT).show();
                }
            }else {
                PayBillResponse responseModel=new Gson().fromJson(response.string()/*billResponse*/,PayBillResponse.class);
                if(responseModel!=null && responseModel.statusCode.equals("00")){
                    Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_LONG).show();
                    openReceipt(responseModel);
                }else {
                    Toast.makeText(context, responseModel.statusMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){

        }
    }

    private void openReceipt(PayBillResponse responseModel) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Design_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lic_receipt_dialog);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextView agentCodeTv=dialog.findViewById(R.id.agentCodeTv);
        TextView txnIdTv=dialog.findViewById(R.id.txnIdTv);
        TextView title=dialog.findViewById(R.id.title);
        TextView policyTv=dialog.findViewById(R.id.policyTv);
        TextView policyNoTv=dialog.findViewById(R.id.policyNoTv);
        TextView nameTv=dialog.findViewById(R.id.nameTv);
        TextView amountTv=dialog.findViewById(R.id.amountTv);
        TextView operatorIdTv=dialog.findViewById(R.id.operatorIdTv);
        TextView statusTv=dialog.findViewById(R.id.statusTv);
        title.setText(billDetail.category+" Bill Receipt");
        policyTv.setText("Acknowledge no");
        policyNoTv.setText(responseModel.billDetails.get(0).acknowledgementNo);
        txnIdTv.setText(responseModel.billDetails.get(0).transactionId);
        operatorIdTv.setText(responseModel.billDetails.get(0).operatotrId);
        statusTv.setText(responseModel.billDetails.get(0).status);
        amountTv.setText(responseModel.billDetails.get(0).amount+"");
        agentCodeTv.setText(loginModel.Data.DoneCardUser);
        nameTv.setText(billDetail.customerName);


        dialog.findViewById(R.id.back_tv).setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void payBill(String userData, String token) {
        PayBillRequest request=new PayBillRequest();
        request.AgentCode=loginModel.Data.DoneCardUser;
        request.Operatorid=billDetail.operatorid;
        request.Canumber=billDetail.canumber;
        request.billdate=billDetail.dueDate;
        request.dueDate=billDetail.dueDate;
        request.cellNumber=billDetail.billNumber;
        request.userName=billDetail.customerName;
        request.Email=loginModel.Data.Email;
        request.Suppliercode=operatorDetail.Suppliercode;
        request.Category=billDetail.category;
        request.fetchbillstring=billDetail.fetchbillstring;
        request.BillAmount= Float.parseFloat(amountEdt.getText().toString());
        request.Billnetamount= Float.parseFloat(amountEdt.getText().toString());
//        request.BillAmount= 10.0f;
//        request.Billnetamount=10.0f;
        showCustomDialog();
        new NetworkCall().callBillPayService(request, ApiConstants.PayBillpayments, context, userData, token, false, (response, responseCode) -> {
            hideCustomDialog();
//            responseHandler(response, BillPay);
            if(response!=null){
                responseHandler(response, BillPay);
            }else {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Paying your bill, please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    private String billResponse="{\n" +
            "    \"statusCode\": \"00\",\n" +
            "    \"statusMessage\": \"You Bill Payment for BSES Yamuna Power Limited of Amount 12 is successful.\",\n" +
            "    \"billDetails\": [\n" +
            "        {\n" +
            "            \"transactionId\": \"B260719HC4JC0A13387\",\n" +
            "            \"acknowledgementNo\": \"5013\",\n" +
            "            \"operatotrId\": \"MockLazYafrYZ7\",\n" +
            "            \"amount\": 12.0,\n" +
            "            \"status\": \"Success\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

}
