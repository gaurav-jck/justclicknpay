package com.justclick.clicknbook.Fragment.accountsAndReports;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.imageUpload.VolleyMultipartRequest;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.model.DepositRequestResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.requestmodels.AgentDepositRequestRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lenovo on 03/28/2017.
 */

public class AgentDepositRequestFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final int REQUEST_AMOUNT=1, MY_PERMISSIONS_REQUEST_STORAGE=2, REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 1;
    private final int VOLLEY_TIMEOUT=120000;
    private final float IMAGE_MAX_SIZE=1024 * 1024 * 3.5f;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private TextView agency_name_tv, date_tv;
    private EditText agent_name_edt, amount_edt, mobile_edt, receipt_no_edt, remark_edt;
    private Spinner spinner_submit_type, spinner_bank_name;
    private RelativeLayout date_lin;
    private Button submit;
    private LoginModel loginModel;
    private String transactionDate, type, response="";
    private SimpleDateFormat dateToServerFormat;
    private int startDateDay, startDateMonth, startDateYear;
    private Calendar startDateCalendar;
    private TextInputLayout view_agent_name_edt;
    private String imagepath=null, encodedImage;
    private ImageView imageFile;
    private ByteArrayOutputStream networkOutputStream=null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context=getActivity();
        loginModel= MyPreferences.getLoginData(new LoginModel(),context);

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agent_deposit_request, container, false);
        titleChangeListener.onToolBarTitleChange(getString(R.string.agentDepositRequestFragmentTitle));
        initializeViews(view);
        initializeDates();
        return view;
    }

    private void initializeViews(View view) {
        agency_name_tv =  view.findViewById(R.id.agency_name_tv);
        date_tv =  view.findViewById(R.id.address_tv);
        agent_name_edt=  view.findViewById(R.id.agent_name_edt);
        amount_edt=  view.findViewById(R.id.amount_edt);
        mobile_edt=  view.findViewById(R.id.mobile_edt);
        remark_edt=  view.findViewById(R.id.remark_edt);
        receipt_no_edt=  view.findViewById(R.id.receipt_no_edt);
        view_agent_name_edt=  view.findViewById(R.id.view_agent_name_edt);

        spinner_submit_type=  view.findViewById(R.id.spinner_submit_type);
        spinner_bank_name=  view.findViewById(R.id.spinner_bank_name);
        date_lin=  view.findViewById(R.id.date_lin);
        submit=  view.findViewById(R.id.bt_submit);
        imageFile=  view.findViewById(R.id.imageFile);
        submit.setOnClickListener(this);
        date_lin.setOnClickListener(this);

        mobile_edt.setText(loginModel.Data.Mobile);
        agent_name_edt.setText(loginModel.Data.Name);

        view.findViewById(R.id.card_view).setOnClickListener(this);
        view.findViewById(R.id.chooseFileLin).setOnClickListener(this);

        agency_name_tv.setOnClickListener(this);

        spinner_submit_type.setOnItemSelectedListener(this);

        spinner_submit_type.setAdapter(setSpinnerAdapter(new String[]{"-Select-","Cash","Cheque","OnlineTransfer"}));
        spinner_bank_name.setAdapter(setSpinnerAdapter(loginModel.Data.BankNames.split("#")));

        try {
            agency_name_tv.setText(loginModel.Data.AgencyName.toUpperCase()+
                    "\n("+loginModel.Data.DoneCardUser+")");
        }catch (NullPointerException e){

        }
    }

    private void initializeDates() {
        //Date formats
        dateToServerFormat = Common.getServerDateFormat();

        //default start Date
        startDateCalendar= Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        transactionDate=dateToServerFormat.format(startDateCalendar.getTime());

        //set default date
        date_tv.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));

    }

    private boolean validate() {
        try {
            if(spinner_submit_type.getSelectedItem().toString().contains("Select")){
                Toast.makeText(context, R.string.select_submit_type, Toast.LENGTH_LONG).show();
                return false;
            }else if((!Common.isdecimalvalid(amount_edt.getText().toString().trim()))||
                    Float.parseFloat(amount_edt.getText().toString().trim())==0) {
                Toast.makeText(context, R.string.empty_and_invalid_amount, Toast.LENGTH_LONG).show();
                return false;
            }else if(Float.parseFloat(amount_edt.getText().toString().trim())>1000000) {
                Toast.makeText(context, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
                return false;
            }else if(mobile_edt.getText().toString().trim().length()<10){
                Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_LONG).show();
                return false;
            }else if(spinner_bank_name.getSelectedItem().toString().contains("Select")){
                Toast.makeText(context, R.string.select_bank, Toast.LENGTH_LONG).show();
                return false;
            }else if(receipt_no_edt.getText().toString().trim().length()==0){
                Toast.makeText(context, R.string.empty_and_invalid_receipt, Toast.LENGTH_LONG).show();
                return false;
            }else if(!Common.isNameValid(agent_name_edt.getText().toString().trim())&&
                    spinner_submit_type.getSelectedItem().equals("Cash")){
                Toast.makeText(context, R.string.empty_and_invalid_agent_name, Toast.LENGTH_LONG).show();
                return false;
            }else if (remark_edt.getText().toString().length()==0) {
                Toast.makeText(context, R.string.empty_remark, Toast.LENGTH_LONG).show();
                return false;
            }else if (networkOutputStream==null) {
                Toast.makeText(context, "please choose receipt image file", Toast.LENGTH_LONG).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
        }
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_submit_type:
                typeSelector(position, spinner_submit_type.getSelectedItem().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void typeSelector(int position, String selectedValue) {
        type=selectedValue;
        if(selectedValue.equalsIgnoreCase("Cash")){
            agent_name_edt.setVisibility(View.VISIBLE);
            view_agent_name_edt.setVisibility(View.VISIBLE);
        }else {
            agent_name_edt.setVisibility(View.INVISIBLE);
            view_agent_name_edt.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_submit:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                if(Common.checkInternetConnection(context)) {
                    if(validate()){
                        AgentDepositRequestRequestModel model=new AgentDepositRequestRequestModel();
                        model.TotalAmount=amount_edt.getText().toString().trim();
                        model.DeviceId=Common.getDeviceId(context);
                        model.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
                        model.DoneCardUser=loginModel.Data.DoneCardUser;
                        model.EmpName=agent_name_edt.getText().toString().trim();
                        model.BankName=spinner_bank_name.getSelectedItem().toString();
                        model.TypeOfAmount=spinner_submit_type.getSelectedItem().toString();
                        model.MobileNumber=mobile_edt.getText().toString();
                        model.ReceiptNo =receipt_no_edt.getText().toString();
                        model.TransactionDate=transactionDate;

                        if(remark_edt.getText().toString().trim()!=null){
                            model.Remarks=remark_edt.getText().toString().trim();}
                        else {model.Remarks="";  }

                        if(Common.checkInternetConnection(context)) {
                            requestAmount(model);
                        }else {
                            Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.date_lin:
                try {
                    openDatePicker();
                }catch (Exception e){

                }
                break;

            case R.id.card_view:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;

            case R.id.agency_name_tv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;

            case R.id.chooseFileLin:
                ActivityCompat.requestPermissions((NavigationDrawerActivity)context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    chooseFile(1);
                }
                break;

        }
    }

    private void chooseFile(int type) {
//        Toast.makeText(context, "Choose file", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("*/*");
//
//        intent.setAction(Intent.ACTION_GET_CONTENT);

//        dispatchTakePictureIntent1();
//        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(Intent.createChooser(takePhotoIntent, "Select file to upload"), REQUEST_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(getPickImageIntent(context), "Select file to upload"), type);

//        reference
//        https://www.androidpit.com/forum/626144/android-image-uploading-to-server-from-gallery

    }
    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File f = new File(android.os.Environment.getExternalStorageDirectory(), "deposit.jpg");
//        Uri mImageCaptureUri = Uri.fromFile(f);
        try {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Uri apkURI = null;
//        try {
//            apkURI = FileProvider.getUriForFile(
//                    context,
//                    context.getApplicationContext()
//                            .getPackageName() + ".provider", createImageFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
//        takePhotoIntent.setData(apkURI);
//        takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Select file to upload");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE:
//                Toast.makeText(context, "Permission Result case call", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(context, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
//                    Toast.makeText(context, "PERMISSION_NOT_GRANTED", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            default:
//            Toast.makeText(context, "Permission Result default", Toast.LENGTH_SHORT).show();
          /*  case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }*/

                // other 'case' lines to check for other
                // permissions this app might request.
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Deposit_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagepath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent1() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(context,
//                        "com.example.android.fileprovider",
//                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile.getAbsolutePath());
                AgentDepositRequestFragment.this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent2() {

        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(context, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
//                activity.setCapturedImageURI(fileUri);
//                activity.setCurrentPhotoPath(fileUri.getPath());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        activity.getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageFile.getWidth();
        int targetH = imageFile.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageFile.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            if(data.getExtras()!=null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                imagepath = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "Deposit", null);
                if (imagepath != null && (imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".jpg") ||
                        imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".png"))) {
                    if (new File(imagepath).length() > IMAGE_MAX_SIZE) {
                        Toast.makeText(context, "Image too large. Please select image less than 3 MB", Toast.LENGTH_LONG).show();
                    }
                    try {
                        imageFile.setImageBitmap(imageBitmap);
                        networkOutputStream = new ByteArrayOutputStream();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "Please select image in JPEG or PNG format", Toast.LENGTH_SHORT).show();
                }
            }else {
                Uri selectedImageUri = data.getData();
                imagepath = getPath(selectedImageUri);
                if(imagepath!=null && (imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".jpg")||
                        imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".png"))) {
                    if (new File(imagepath).length() > IMAGE_MAX_SIZE) {
                        Toast.makeText(context, "Image too large. Please select image less than 3 MB", Toast.LENGTH_LONG).show();
                    }else {
                        imagepath = getPath(selectedImageUri);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmOptions);
                        imageFile.setImageBitmap(bitmap);
                        networkOutputStream = new ByteArrayOutputStream();
                    }
                }else {
                    Toast.makeText(context, "Please select image in JPEG or PNG format", Toast.LENGTH_SHORT).show();
                }
            }
           /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap=BitmapFactory.decodeFile(imagepath, bmOptions);
                imageFile.setImageBitmap(bitmap);*/

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
//                byte[] imageBytes = baos.toByteArray();
//                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

               /* int compressUnit=100;
                boolean largeSize=false;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
                byte[] imageBytes = baos.toByteArray();
                while(imageBytes.length>(1024*1024*4)){
                    compressUnit=compressUnit-5;
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
                    imageBytes = baos.toByteArray();
                    if(imageBytes.length>(1024*1024*4) && compressUnit==0){
                        Toast.makeText(context, "Image too large.", Toast.LENGTH_SHORT).show();
                        largeSize=true;
                        break;
                    }
                }
//            File file=
                if(!largeSize){
//                    imageFile.setImageBitmap(bitmap);
                    networkOutputStream = new ByteArrayOutputStream();
                }*/
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void decodeImage(final String path) {
        int targetW = imageFile.getWidth();
        int targetH = imageFile.getHeight();

        final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imageFile.setImageBitmap(bitmap);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void openDatePicker() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDateCalendar.set(year, monthOfYear, dayOfMonth);
                        transactionDate=dateToServerFormat.format(startDateCalendar.getTime());
                        startDateDay=dayOfMonth;
                        startDateMonth=monthOfYear;
                        startDateYear=year;
                        date_tv.setText(Common.getShowInTVDateFormat().format(startDateCalendar.getTime()));
                    }

                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();


    }

    private void requestAmount(AgentDepositRequestRequestModel model) {
//        showCustomDialog();

        new UploadFileTask(model).execute();
//        new NetworkCall().getFormDataFromServer(ApiConstants.MobilePage, ApiConstants.AgentDepositRequest, model);
       /* new NetworkCall().callFormMobileService(model, ApiConstants.AgentDepositRequest, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, REQUEST_AMOUNT);
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }

    private void responseHandler(String response, int TYPE) {
        try {
            switch (TYPE){
                case REQUEST_AMOUNT:
                    DepositRequestResponseModel responseModel = new Gson().fromJson(response, DepositRequestResponseModel.class);
                    hideCustomDialog();
                    if(responseModel!=null){
                        if(responseModel.DepositRequestResult.StatusCode.equals("0")) {
                            Toast.makeText(context, responseModel.DepositRequestResult.Data.Message, Toast.LENGTH_SHORT).show();
                            amount_edt.setText("");
                            receipt_no_edt.setText("");
                            networkOutputStream=null;
                            imageFile.setImageResource(R.drawable.choose_file);
                            Common.showResponsePopUp(context, responseModel.DepositRequestResult.Data.Message);
                        }else {
                            Toast.makeText(context, responseModel.DepositRequestResult.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            hideCustomDialog();
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }


    private ArrayAdapter<String> setSpinnerAdapter(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                R.layout.mobile_operator_spinner_item,R.id.operator_tv, data);
        adapter.setDropDownViewResource(R.layout.mobile_operator_spinner_item_dropdown);
        return adapter;
    }

    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }


    private class UploadFileTask extends AsyncTask<Object, Void, String> {
        AgentDepositRequestRequestModel request;
        String fileName;

        public UploadFileTask(AgentDepositRequestRequestModel model) {
            this.request=model;
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
            String url = ApiConstants.BASE_URL + ApiConstants.MobilePage + ApiConstants.AgentDepositRequest;
//            return uploadFile(url,imagepath, request);
//            return uploadMultipartCamera(url,imagepath, request);
//            return imageUpload(url,imagepath, request);
            saveProfileAccount(url,imagepath, request);
            return "";

//            return new NetworkCall().getFormDataFromServer(context, ApiConstants.MobilePage, ApiConstants.AgentDepositRequest,
//                    request, networkOutputStream, fileToSend, fileUri, imagepath);
        }

        protected void onPostExecute(String result) {
            /* try{
             *//*hideCustomDialog();
                if(result!=null && result.length()>0) {
                    DepositRequestResponseModel responseModel = new Gson().fromJson(result, DepositRequestResponseModel.class);
//                    hideCustomDialog();
                    if(responseModel!=null){
                        if(responseModel.DepositRequestResult.StatusCode.equals("0")) {
                            Toast.makeText(context, responseModel.DepositRequestResult.Data.Message, Toast.LENGTH_SHORT).show();
                            amount_edt.setText("");
                            receipt_no_edt.setText("");
                            Common.showResponsePopUp(context, responseModel.DepositRequestResult.Data.Message);
                        }else {
                            Toast.makeText(context, responseModel.DepositRequestResult.Status, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
                }*//*
            }catch (Exception e){
                hideCustomDialog();
                Toast.makeText(context,R.string.exception_message,Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private void saveProfileAccount(String postUrl, final String filePath, final AgentDepositRequestRequestModel request) {
//   refer from:      https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
        String url = postUrl;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                responseHandler(resultResponse, REQUEST_AMOUNT);
//                Toast.makeText(context, resultResponse, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                hideCustomDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                params.put("RequestData", new Gson().toJson(request));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
// file name could found file base or direct access from real path
// for now just get bitmap data from ImageView
//                Calendar
                params.put("Image", new DataPart("_"+System.currentTimeMillis()+"DepositReceipt.jpg", getFileDataFromDrawable(context, imageFile.getDrawable()), "image/jpeg"));
//                params.put("cover", new DataPart("file_cover.jpg", getFileDataFromDrawable(context, imageFile.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }


    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideCustomDialog();
    }
}