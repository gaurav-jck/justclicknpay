package com.justclick.clicknbook.Fragment.cashoutnew;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.Fragment.jctmoney.RapipayAddBeneFragment;
import com.justclick.clicknbook.Fragment.jctmoney.request.AddBeneRequest;
import com.justclick.clicknbook.Fragment.jctmoney.request.CommonParams;
import com.justclick.clicknbook.Fragment.jctmoney.response.BankResponse;
import com.justclick.clicknbook.Fragment.jctmoney.response.CommonRapiResponse;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.FileUtils;
import com.justclick.clicknbook.utils.FileUtilsUpload;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;


public class AddAccountFragment extends Fragment implements View.OnClickListener {
    private final int AddRecipient=1,VerifyAccount=3;
    private final String VERIFIED="1", NOT_VERIFIED="0";
    private String isVerified=NOT_VERIFIED;
    private Context context;
    private Activity activity;
    private FragmentBackPressListener backPress;
    private TextView submit_tv, verifyAccountTv, fileNameTv;
    private EditText user_mobile_edt, ifscEdt,
            name_edt,account_no_edt,confirmAccountEdt;
    private TextInputLayout Address;
    private String bankName, bankId;
    private DataBaseHelper dataBaseHelper;
    private LoginModel loginModel;
    private BankResponse ifscByCodeResponse;
    private ArrayList<BankResponse> bankArray;
    private CommonParams commonParams, commonParamNew;
    AutoCompleteTextView atv_bank;
    Uri imageUri;
    private String fileName;
    private File imageFile;
    private final long kbSize=1024*1024;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataBaseHelper=new DataBaseHelper(context);
        bankArray=new ArrayList<>();
        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);

        if(getArguments()!=null){
            commonParams= (CommonParams) getArguments().getSerializable("commonParams");
            commonParamNew= (CommonParams) getArguments().getSerializable("commonParamNew");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context=context;
            backPress= (FragmentBackPressListener) context;
        }catch (ClassCastException e){
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payout_add_account, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        submit_tv =  view.findViewById(R.id.submit_tv);
        verifyAccountTv = view.findViewById(R.id.verifyAccountTv);
        user_mobile_edt = view.findViewById(R.id.user_mobile_edt);
        ifscEdt =  view.findViewById(R.id.ifscEdt);
        Address =  view.findViewById(R.id.Address);
        Address.setHint("Bank IFSC");
        name_edt =  view.findViewById(R.id.name_edt);
        account_no_edt =  view.findViewById(R.id.account_no_edt);
        confirmAccountEdt =  view.findViewById(R.id.confirmAccountEdt);
        fileNameTv =  view.findViewById(R.id.fileNameTv);

        atv_bank =  view.findViewById(R.id.atv_bank);
        submit_tv.setOnClickListener(this);
        verifyAccountTv.setOnClickListener(this);
        view.findViewById(R.id.uploadTv).setOnClickListener(this);
        view.findViewById(R.id.back_arrow).setOnClickListener(this);

//        user_mobile_edt.setText(Mobile);
//        user_mobile_edt.setEnabled(false);

        setText();
        user_mobile_edt.setText(loginModel.Data.Mobile);

        if(dataBaseHelper.getJctBankNamesWithIFSC()!=null && dataBaseHelper.getJctBankNamesWithIFSC().size()>0) {
            String[] arr=new String[dataBaseHelper.getJctBankNamesWithIFSC().size()];
            for (int p=0; p<dataBaseHelper.getJctBankNamesWithIFSC().size(); p++){
                arr[p]=dataBaseHelper.getJctBankNamesWithIFSC().get(p).Name;
            }
            atv_bank.setAdapter(getSpinnerAdapter(arr));
        }else {
            getBankNames();
        }

        atv_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < bankArray.size(); i++) {
                    if (bankArray.get(i).getBANK_NAME().equals(selection)) {
                        pos = i;
                        break;
                    }
                }
//                if(pos>0){
                try{
                    ifscByCodeResponse=bankArray.get(pos);
                    bankName=atv_bank.getText().toString();
//                    ifscEdt.setText(ifscByCodeResponse.getMASTER_IFSC_CODE());

                }catch (Exception e){
//                        IFSCCodeEdt.setText("");
//                        ifscByCodeResponse=null;
                }
                /*}else {
                    bankName="";
                    ifscEdt.setText("");
                    ifscByCodeResponse=null;
                    verifyAccountTv.setVisibility(View.GONE);
                }*/
            }
        });


        atv_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) atv_bank.showDropDown();
            }
        });

        atv_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atv_bank.showDropDown();
            }
        });

    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);

        return adapter;
    }

    private void getBankNames() {
        if(!MyCustomDialog.isDialogShowing()){
            showCustomDialog();}
        ApiInterface apiService = APIClient.getClientRapipay().create(ApiInterface.class);
        Call<ArrayList<BankResponse>> call = apiService.getService(ApiConstants.BASE_URL_RAPIPAY+"api/payments/"+ApiConstants.GetBankName);
        call.enqueue(new Callback<ArrayList<BankResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BankResponse>>call, Response<ArrayList<BankResponse>> response) {

                try{
                    if(response.body()!=null && response.body().size()>0){
                        hideCustomDialog();
//                        JctIfscByCodeResponse relationResponse=new JctIfscByCodeResponse();
//                        relationResponse.Key="";
//                        relationResponse.Name="select-bank";
//                        relationResponse.Digit="A";
                        String[] arr=new String[response.body().size()];
                        bankArray.addAll(response.body());
//                        dataBaseHelper.insertJctBankNamesWithIFSC(bankArray);
                        for (int p=0;p<response.body().size();p++){
                            arr[p]=response.body().get(p).getBANK_NAME();
                        }

                        atv_bank.setAdapter(getSpinnerAdapter(arr));
                    }
                    else
                    {
                        hideCustomDialog();
                        Toast.makeText(context,R.string.response_failure_message,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    hideCustomDialog();
                    Toast.makeText(context,R.string.exception_message,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BankResponse>> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setText() {
        Typeface face1 = Common.TitleTypeFace(context);
        Typeface face2 = Common.EditTextTypeFace(context);
        Typeface face3 = Common.TextViewTypeFace(context);
        user_mobile_edt.setTypeface(face2);
        ifscEdt.setTypeface(face2);
        account_no_edt.setTypeface(face2);
        confirmAccountEdt.setTypeface(face2);

        name_edt.setTypeface(face2);
        submit_tv.setTypeface(face3);
        verifyAccountTv.setTypeface(face3);

    }

    private boolean validate() {
        if(atv_bank.getText().toString().trim().length()==0 ){
            Toast.makeText(context,R.string.empty_and_invalid_bank,Toast.LENGTH_SHORT).show();
            return false;
        }else if(account_no_edt.getText().toString().trim().length()<6 ){
            Toast.makeText(context,R.string.empty_and_invalid_account_number,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!confirmAccountEdt.getText().toString().trim().equals(account_no_edt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_account_confirmation,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isIFSCValid(ifscEdt.getText().toString())){
            Toast.makeText(context,R.string.empty_and_invalid_ifsc_code,Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Common.isNameValid(name_edt.getText().toString().trim())){
            Toast.makeText(context,R.string.empty_and_invalid_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if(user_mobile_edt.getText().toString().length()<10){
            Toast.makeText(context,R.string.empty_and_invalid_mobile,Toast.LENGTH_SHORT).show();
            return false;
        }else if(imageFile==null){
            Toast.makeText(context,"Please select file",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
                getParentFragmentManager().popBackStack();
                break;
            case R.id.uploadTv:
                askSelfPermission();
                break;
            case R.id.submit_tv:
                Common.preventFrequentClick(submit_tv);
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        addAccount(ApiConstants.addPayoutBene, AddRecipient, false);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.verifyAccountTv:
                Common.preventFrequentClick(verifyAccountTv);
                if(Common.checkInternetConnection(context)){
                    if(validate()) {
                        addOrValidateBeneficiary(ApiConstants.ValidateAccount, VerifyAccount, false);
                    }
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void askSelfPermission() {

        String readImagePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readImagePermission= Manifest.permission.READ_MEDIA_IMAGES;
        else
            readImagePermission=Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(
                requireContext(), readImagePermission) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            selectFile();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    readImagePermission);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    selectFile();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    private void selectFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        launchSomeActivity.launch(chooseFile);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
//                        imagePath=getPath(selectedImageUri);
                        try {
                            getImage(data, selectedImageUri);
//                            setImageBitmap(selectedImageUri, data);
//                            image.setImageURI(imageUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

    private void getImage(Intent data, Uri selectedImageUri) throws IOException{
        Uri uri2 = FileUtilsUpload.getFilePathFromUri(selectedImageUri, requireContext());
//                        File file = new File(getPath(context,uri));
//        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageUri=uri2;
            imageFile = new File(FileUtilsUpload.getPath(uri2, context));
            FileUtilsUpload.getSize(data, imageFile, context);
        }else {
            imageUri=selectedImageUri;
            String path = FileUtils.getPath(requireContext(), selectedImageUri);
            imageFile = new File(path);
        }
        int file_size = Integer.parseInt(String.valueOf(imageFile.length() / kbSize));
        if (file_size > 100) {
            Toast.makeText(requireContext(), "Please file less than 1 MB", Toast.LENGTH_SHORT).show();
        } else {
            fileNameTv.setText(imageFile.getName());
        }
        Log.d("fileUri: ", String.valueOf(selectedImageUri));
    }

    private void addAccount(String method, final int TYPE, boolean isVerify) {
        new NetworkCall().callService(NetworkCall.getPayoutNewApiInterface().addAccountPart(method,
                        createRequestBody(loginModel.Data.DoneCardUser),
                        createRequestBody(bankName),
                        createRequestBody(ifscByCodeResponse.getBankId()),
                        createRequestBody(ifscEdt.getText().toString()),
                        createRequestBody(account_no_edt.getText().toString()),
                        createRequestBody(user_mobile_edt.getText().toString()),
                        createRequestBody(name_edt.getText().toString()),
                        createRequestBody("App"),
                        createRequestBody(isVerified),
                        sendFile("Files", imageFile),
                        commonParamNew.getUserData(),
                        "Bearer "+commonParamNew.getToken()),
                requireContext(), true, (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response, TYPE);
                    }else {
                        Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private RequestBody createRequestBody(String userId) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),
                userId);
    }

    private MultipartBody.Part sendFile(String paramName, File imageFile){
        if(imageFile==null)
            return null;
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                imageFile
        );
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(requireContext().getContentResolver().getType(uri)),
                        imageFile
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(paramName, imageFile.getName(), requestFile);
        return body;
    }

    private void addOrValidateBeneficiary(String method, final int TYPE, boolean isVerify) {
        AddValidateAccountRequest requestModel=new AddValidateAccountRequest();
        requestModel.setAgentCode(loginModel.Data.DoneCardUser);
        requestModel.setSessionKey(commonParams.getSessionKey());
        requestModel.setSessionRefId(commonParams.getSessionRefNo());
        requestModel.setBankName(atv_bank.getText().toString());
        requestModel.setBankId(ifscByCodeResponse.getBankId());   // new change
        requestModel.setAccountHolderName(name_edt.getText().toString());
        requestModel.setAccountNumber(account_no_edt.getText().toString());
        requestModel.setConfirmAccountNumber(confirmAccountEdt.getText().toString());
        requestModel.setIfscCode(ifscEdt.getText().toString());
        requestModel.setMobile(user_mobile_edt.getText().toString());
        requestModel.setApiService(commonParams.getApiService());
        requestModel.setAddress(commonParams.getAddress());  // new change
        requestModel.setPinCode(commonParams.getPinCode());  // new change
        requestModel.setState(commonParams.getState());  // new change
        requestModel.setCity(commonParams.getCity());  // new change
        requestModel.setStatecode(commonParams.getStatecode());  // new change
        requestModel.setGst_state(commonParams.getStatecode());  // new change
        requestModel.setUserdata(commonParams.getUserData());  // new change
        requestModel.verified=isVerified;
        requestModel.isVerified=isVerified;
        new NetworkCall().callRapipayServiceHeader(requestModel, method, context,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerVerify(response, TYPE);
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                }, commonParams.getUserData(), commonParams.getToken());
    }

    private void responseHandlerVerify(ResponseBody response, int type) {
        try {
            RapipayAddBeneFragment.ValidateBeneResponse senderResponse = new Gson().fromJson(response.string(), RapipayAddBeneFragment.ValidateBeneResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")) {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_LONG).show();
                    name_edt.setText(senderResponse.beneficiaryName);
                    verifyAccountTv.setText("Account Verified");
                    verifyAccountTv.setEnabled(false);
                    verifyAccountTv.setAlpha(0.6f);
                    isVerified=VERIFIED;
                    addAccount(ApiConstants.addPayoutBene, AddRecipient, false);

                } else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    public class ValidateBeneResponse extends CommonRapiResponse{
        public String beneficiaryName;
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            CommonRapiResponse senderResponse = new Gson().fromJson(response.string(), CommonRapiResponse.class);
            if(senderResponse!=null){
                if(senderResponse.getStatusCode().equals("00")){
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                    backPress.onJctDetailBackPress(CodeEnum.PayoutNew);
                    getParentFragmentManager().popBackStack();
                }else {
                    Toast.makeText(context,senderResponse.getStatusMessage(),Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(isBackPress) {
//            backPress.onJctDetailBackPress();
//        }
    }
}