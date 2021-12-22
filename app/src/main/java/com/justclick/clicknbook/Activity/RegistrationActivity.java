package com.justclick.clicknbook.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.database.DataBaseHelper;
import com.justclick.clicknbook.model.CityNameResponseModel;
import com.justclick.clicknbook.model.RegistrationModel;
import com.justclick.clicknbook.model.StateNameResponseModel;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.CityNameRequest;
import com.justclick.clicknbook.requestmodels.RegistrationRequestModel;
import com.justclick.clicknbook.retrofit.APIClient;
import com.justclick.clicknbook.retrofit.ApiInterface;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyCustomDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private ProgressDialog dialog;
    private TextView submit_tv;
    private WebView terms_web_view;
    private CheckBox remember_me_checkbox;
    private EditText company_name_edt, user_name_edt, user_last_name_edt, user_email_edt,
            user_mobile_edt, pan_card_edt, pan_card_name_edt, address_edt, pincode_edt, remarksEdt, addressProofEdt;
    private Spinner country_spinner, state_spinner, city_spinner, salutation_spinner, userTypeSpinner, addressProofSpinner;
    private ImageView imageFile, addressProofFile;
    private LinearLayout chooseAddressProofLin;
    private String countryName = "INDIA", stateName = "", cityName = "", address = "", userType, addressProof, addressImageString, panNoImageString;
    private RelativeLayout term_relative;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = this;
        dataBaseHelper = new DataBaseHelper(context);
        company_name_edt = (EditText) findViewById(R.id.company_name_edt);
        user_name_edt = (EditText) findViewById(R.id.user_name_edt);
        user_last_name_edt = (EditText) findViewById(R.id.user_last_name_edt);
        user_email_edt = (EditText) findViewById(R.id.user_email_edt);
        user_mobile_edt = (EditText) findViewById(R.id.user_mobile_edt);
        pan_card_edt = (EditText) findViewById(R.id.pan_card_edt);
        pan_card_name_edt = (EditText) findViewById(R.id.pan_card_name_edt);
        address_edt = (EditText) findViewById(R.id.address_edt);
        pincode_edt = (EditText) findViewById(R.id.pincode_edt);
        remarksEdt = (EditText) findViewById(R.id.remarksEdt);
        addressProofEdt = (EditText) findViewById(R.id.addressProofEdt);
        country_spinner = (Spinner) findViewById(R.id.country_spinner);
        state_spinner = (Spinner) findViewById(R.id.state_spinner);
        city_spinner = (Spinner) findViewById(R.id.city_spinner);
        salutation_spinner = (Spinner) findViewById(R.id.salutation_spinner);
        userTypeSpinner = (Spinner) findViewById(R.id.userTypeSpinner);
        addressProofSpinner = (Spinner) findViewById(R.id.addressProofSpinner);
        chooseAddressProofLin = (LinearLayout) findViewById(R.id.chooseAddressProofLin);
        imageFile = (ImageView) findViewById(R.id.imageFile);
        addressProofFile = (ImageView) findViewById(R.id.addressProofFile);
        submit_tv = (TextView) findViewById(R.id.submit_tv);
        submit_tv.setOnClickListener(this);
        chooseAddressProofLin.setOnClickListener(this);
        remember_me_checkbox = (CheckBox) findViewById(R.id.remember_me_checkbox);
        term_relative = (RelativeLayout) findViewById(R.id.term_relative);
        findViewById(R.id.lin_container).setOnClickListener(this);
        findViewById(R.id.terms_condition_tv).setOnClickListener(this);
        findViewById(R.id.terms_ok).setOnClickListener(this);
        findViewById(R.id.chooseFileLin).setOnClickListener(this);
        terms_web_view = (WebView) findViewById(R.id.terms_web_view);
//        terms_web_view.getSettings().setLoadsImagesAutomatically(true);
        terms_web_view.getSettings().setJavaScriptEnabled(true);
//        terms_web_view.setWebViewClient(new WebViewClient());
        terms_web_view.setWebViewClient(new MyBrowser());

        terms_web_view.loadUrl(ApiConstants.TermsAndConditionUrl);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.salutation_spinner_item, R.id.operator_tv, getResources().
                getStringArray(R.array.salutation_array));
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        salutation_spinner.setAdapter(adapter);

        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<>(context,
                R.layout.spinner_item, R.id.name_tv, getResources().
                getStringArray(R.array.user_type_array));
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        userTypeSpinner.setAdapter(userTypeAdapter);

        ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, R.id.name_tv,
                getResources().getStringArray(R.array.address_array));
        adapter.setDropDownViewResource(R.layout.salutation_spinner_item_dropdown);
        addressProofSpinner.setAdapter(addressAdapter);

        country_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.country_array)));
        state_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.state_array)));
        city_spinner.setAdapter(getSpinnerAdapter(getResources().
                getStringArray(R.array.city_array)));

        //get state names from database if exist
        if (dataBaseHelper.getAllStateNames("").size() == 0) {
            getStateCity(ApiConstants.STATELIST, "INDIA");
        } else {
            String[] arr = new String[dataBaseHelper.getAllStateNames("").size()];
            for (int p = 0; p < dataBaseHelper.getAllStateNames("").size(); p++) {
                arr[p] = dataBaseHelper.getAllStateNames("").get(p);
            }
            state_spinner.setAdapter(getSpinnerAdapter(arr));
        }

        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    userType = "";
                } else {
                    userType = userTypeSpinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addressProofSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    addressProof = "";
                } else {
                    addressProof = addressProofSpinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 || !state_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    stateName = state_spinner.getSelectedItem().toString();
                    getCity(ApiConstants.CITYLIST, stateName);
                } else {
//                    hideCustomDialog();
                    stateName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 || !city_spinner.getSelectedItem().
                        toString().toLowerCase().contains("select")) {
                    cityName = city_spinner.getSelectedItem().toString();
                } else {
                    cityName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getStateCity(final String methodName, String value) {
        showCustomDialog();
        String valueToSend = Common.getDecodedString(value);
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<StateNameResponseModel> call = apiService.getState(methodName, valueToSend);
        call.enqueue(new Callback<StateNameResponseModel>() {
            @Override
            public void onResponse(Call<StateNameResponseModel> call, Response<StateNameResponseModel> response) {

                try {

                    if (response.body().Data != null && response.body().Status.equalsIgnoreCase("Success")) {
                        hideCustomDialog();
                        String[] arr = new String[response.body().Data.size() + 1];
                        arr[0] = "select-state";
                        dataBaseHelper.insertStateNames("select-state", null);
                        for (int p = 0; p < response.body().Data.size(); p++) {
                            arr[p + 1] = response.body().Data.get(p).StateName;
                            dataBaseHelper.insertStateNames(response.body().Data.get(p).StateName, null);
                        }
                        state_spinner.setAdapter(getSpinnerAdapter(arr));
                    } else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    hideCustomDialog();
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StateNameResponseModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCity(final String methodName, String stateName) {
        if (!MyCustomDialog.isDialogShowing()) {
            showCustomDialog();
        }
        CityNameRequest cityModel = new CityNameRequest();
        cityModel.StateName = stateName;
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<CityNameResponseModel> call = apiService.getCity(methodName, cityModel);
        call.enqueue(new Callback<CityNameResponseModel>() {
            @Override
            public void onResponse(Call<CityNameResponseModel> call, Response<CityNameResponseModel> response) {
                try {
                    hideCustomDialog();
                    if (response.body().Data != null && response.body().StatusCode.equalsIgnoreCase("0")) {
                        String[] arr = new String[response.body().Data.size()];
                        for (int p = 0; p < arr.length; p++) {
                            arr[p] = response.body().Data.get(p).CityName;
                        }
                        city_spinner.setAdapter(getSpinnerAdapter(arr));
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CityNameResponseModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void call_registration(RegistrationRequestModel value) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<RegistrationModel> call = apiService.registrationPost(ApiConstants.REGISTRATION, value);
        call.enqueue(new Callback<RegistrationModel>() {
            @Override
            public void onResponse(Call<RegistrationModel> call, Response<RegistrationModel> response) {
                try {
                    hideCustomDialog();
                    if (response != null) {
                        if (response.body().StatusCode.equalsIgnoreCase("0")) {
                            Toast.makeText(context, response.body().Data.Message, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(context, response.body().Status, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationModel> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFile(ByteArrayOutputStream value, String fileName) {
        showCustomDialog();
        ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.uploadFile(ApiConstants.UploadFile, fileName, value);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    hideCustomDialog();
                    Toast.makeText(context, "OnResponse", Toast.LENGTH_LONG).show();
                    if (response != null && response.body()!=null) {
                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFile1(ByteArrayOutputStream value, String fileName) {
        new UploadFileTask(value,fileName).execute();
    }

    private class UploadFileTask extends AsyncTask<Object, Void, String> {
        ByteArrayOutputStream request;
        String fileName;

        public UploadFileTask(ByteArrayOutputStream transactionRequest, String fileName) {
            this.request=transactionRequest;
            this.fileName=fileName;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPreExecute() {
            showCustomDialog();
        }

        @Override
        protected String doInBackground(Object... objects) {
            return NetworkCall.uploadFileToServer(ApiConstants.UploadFile,fileName,request);
        }

        protected void onPostExecute(String result) {
            try{
                hideCustomDialog();
                if(result!=null) {
                }
            }catch (Exception e){
                hideCustomDialog();
                Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayAdapter<String> getSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item, R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }

    private void registration() {
        if (validatee(company_name_edt.getText().toString().trim(),
                user_name_edt.getText().toString().trim(),
                user_email_edt.getText().toString().trim(),
                user_mobile_edt.getText().toString().trim(),
                pan_card_edt.getText().toString().trim(),
                pan_card_name_edt.getText().toString().trim(),
                address_edt.getText().toString().trim())) {
            RegistrationRequestModel model = new RegistrationRequestModel();
            model.CompanyName = company_name_edt.getText().toString().trim();
            model.Salulation = company_name_edt.getText().toString().trim();
            model.FirstName = user_name_edt.getText().toString().trim();
            model.LastName = user_last_name_edt.getText().toString().trim();
            model.Mail = user_email_edt.getText().toString().trim();
            model.Mobile = user_mobile_edt.getText().toString().trim();
            model.Country = country_spinner.getSelectedItem().toString();
            model.State = stateName;
            model.City = cityName;
            model.Address = address_edt.getText().toString().trim();
            model.PostalCode = pincode_edt.getText().toString().trim();
            model.PanNumber = pan_card_edt.getText().toString().trim();
            model.PancardName = pan_card_name_edt.getText().toString().trim();
            model.Remark = remarksEdt.getText().toString().trim();
            model.AddressProfID = addressProofEdt.getText().toString().trim();
            model.Usertype = userType;
            model.AddressProfType = addressProof;
            model.AddressProfPic = addressImageString.replace("\n", "");
            model.PanNoPic = panNoImageString.replace("\n", "");

            call_registration(model);
        }
    }

    private boolean validatee(String compName, String name, String email,
                              String mobile, String panCard, String panCardName, String address) {
        if (!Common.isNameValid(compName)) {
            Toast.makeText(context, R.string.empty_and_invalid_company, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Common.isNameValid(name)) {
            Toast.makeText(context, R.string.empty_and_invalid_first_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Common.isNameValid(user_last_name_edt.getText().toString().trim())) {
            Toast.makeText(context, R.string.empty_and_invalid_last_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Common.isEmailValid(email)) {
            Toast.makeText(context, R.string.empty_and_invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mobile) || mobile.length() < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        } else if (panCard.length() < 10 || !Common.isPancardValid(panCard)) {
            Toast.makeText(context, R.string.empty_and_invalid_pancard, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Common.isNameValid(panCardName)) {
            Toast.makeText(context, R.string.empty_and_invalid_pancardName, Toast.LENGTH_SHORT).show();
            return false;
        } else if (stateName.length() == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_state, Toast.LENGTH_SHORT).show();
            return false;
        } else if (cityName.length() == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_city, Toast.LENGTH_SHORT).show();
            return false;
        } else if (address_edt.getText().toString().trim().length() == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_address, Toast.LENGTH_SHORT).show();
            return false;
        } else if (pincode_edt.getText().toString().trim().length() < 6) {
            Toast.makeText(context, R.string.empty_and_invalid_pincode, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!remember_me_checkbox.isChecked()) {
            Toast.makeText(context, R.string.check_box, Toast.LENGTH_SHORT).show();
            return false;
        } else if (userType.length() == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_user_type, Toast.LENGTH_SHORT).show();
            return false;
        } else if (addressProof.length() == 0) {
            Toast.makeText(context, R.string.empty_and_invalid_address_proof, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context, getResources().getString(R.string.please_wait));
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_tv:
                if (Common.checkInternetConnection(context)) {
                    registration();
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.lin_container:
                Common.hideSoftKeyboard(RegistrationActivity.this);
                break;
            case R.id.terms_condition_tv:
                if (Common.checkInternetConnection(context)) {
                    term_relative.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.terms_ok:
                term_relative.setVisibility(View.INVISIBLE);
                break;

            case R.id.chooseFileLin:
                chooseFile(1);
                break;

            case R.id.chooseAddressProofLin:
                chooseFile(2);
                break;

        }
    }

//    private void chooseFile() {
//        Toast.makeText(context, "Choose file", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent();
//
//        intent.setType("*/*");
//
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(intent, "Select file to upload"), 1);
//
//    }

    private void chooseFile(int type) {
//        Toast.makeText(context, "Choose file", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ;

//        intent.setType("*/*");
//
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select file to upload"), type);

    }

    private String encodeFileToBase64Binary(File file) {
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);

            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);

//            encodedfile = Base64.encodeBase64(bytes).toString();
            encodedfile = Base64.encodeToString(bytes, 1);
            Toast.makeText(context, encodedfile, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch <span id="IL_AD2" class="IL_AD">block</span>
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }

    private Bitmap FixBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//            imageFile.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);

//                imageView.setImageBitmap(bitmap);

            int compressUnit=100;
            boolean largeSize=false;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
            byte[] imageBytes = baos.toByteArray();
            while(imageBytes.length>50000){
                compressUnit=compressUnit-5;
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
                imageBytes = baos.toByteArray();
                if(imageBytes.length>50000 && compressUnit==0){
                    Toast.makeText(context, "Image too large.", Toast.LENGTH_SHORT).show();
                    largeSize=true;
                    break;
                }
            }
//            File file=
            if(!largeSize) {
                if (requestCode == 1) {
                addressImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    ByteArrayOutputStream networkOutputStream = new ByteArrayOutputStream();
                    String fileName = new File(selectedImage.getPath()).getName() + picturePath.substring(picturePath.lastIndexOf("."));
                    try {
                        ByteArrayInputStream bs = new ByteArrayInputStream(imageBytes);
//                    FileInputStream fileInputStream = new FileInputStream(picturePath);
                        int nRead;
                        byte[] dataByte = new byte[16384];

                        try {
                            while ((nRead = bs.read(dataByte)) != -1) {
                                networkOutputStream.write(dataByte, 0, nRead);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                    fileInputStream.close();
                        bs.close();
                    } catch (Exception e) {

                    }
                    imageBytes = Base64.decode(addressImageString, Base64.DEFAULT);
                    Bitmap addressDecodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageFile.setImageBitmap(addressDecodedImage);
                    uploadFile1(networkOutputStream, fileName);
                } else {
//                panNoImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//                imageBytes = Base64.decode(panNoImageString, Base64.DEFAULT);
//                Bitmap panNoDecodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                addressProofFile.setImageBitmap(panNoDecodedImage);
                }
            }
        }

    }

    private void setImage1(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            imageFile.setImageBitmap(null);

            //Uri return from external activity
            Uri orgUri = data.getData();
//            text1.setText("Returned Uri: " + orgUri.toString() + "\n");

            //path converted from Uri
            String convertedPath = getRealPathFromURI(orgUri);
            Bitmap bm = BitmapFactory.decodeFile(convertedPath);
            imageFile.setImageBitmap(bm);
//            text2.setText("Real Path: " + convertedPath + "\n");

            //Uri convert back again from path
            Uri uriFromPath = Uri.fromFile(new File(convertedPath));
//            text3.setText("Back Uri: " + uriFromPath.toString() + "\n");
        }
    }

    private String getRealPathFromURI(Uri orgUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                orgUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onBackPressed() {
        if (term_relative.getVisibility() == View.VISIBLE) {
            term_relative.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}