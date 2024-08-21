package com.justclick.clicknbook.Fragment.accountsAndReports;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.DepositRequestResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.requestmodels.AgentDepositRequestRequestModel;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.ImageCompressor;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

//https://developer.android.com/training/data-storage/shared/photopicker

public class AgentDepositRequestFragmentNeww extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final int REQUEST_AMOUNT=1, MY_PERMISSIONS_REQUEST_STORAGE=2, REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 1;
    private final float IMAGE_MAX_SIZE_8MB=1024 * 1024 * 8.0f;
    private final float IMAGE_MAX_SIZE_1MB=1024 * 1024 * 1.1f;
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
    private ImageView fileImageView;
    private File receiptFile;
    private Uri imageUri;
    private ByteArrayOutputStream networkOutputStream=null;
    private boolean isStorage, isCamera;

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
        fileImageView =  view.findViewById(R.id.imageFile);
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
            }/*else if (networkOutputStream==null && spinner_submit_type.getSelectedItem().toString().equals("Cash")) {
                Toast.makeText(context, "please choose receipt image file", Toast.LENGTH_LONG).show();
                return false;
            }*/else if (imagepath==null/* && spinner_submit_type.getSelectedItem().toString().equals("Cash")*/) {
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
            case R.id.agency_name_tv:
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
                break;

            case R.id.chooseFileLin:
//                askSelfPermission();
                askSelfPermission2();
                break;

        }
    }


    String readImagePermission;

    ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.size()>0) {
                    boolean allGranted=true;
                    for (String key: result.keySet()) {
                        allGranted=allGranted && result.get(key);
                    }
                    if(allGranted){
                        selectFile();
                    }
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    private ActivityResultLauncher<String> storagePermissionLauncher2 =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    selectFile();
                } else {
//                    requestPermission();
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });


    private void askSelfPermission2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readImagePermission= READ_MEDIA_IMAGES;
        else
            readImagePermission= READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                        ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED) {
            // Partial access on Android 14 (API level 34) or higher
            selectFile();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                (ContextCompat.checkSelfPermission(context, READ_MEDIA_IMAGES) != PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, READ_MEDIA_VISUAL_USER_SELECTED) != PERMISSION_GRANTED)){
            requestPermissionLauncher.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED});
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(), readImagePermission) == PERMISSION_GRANTED) {
            selectFile();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(requireContext(), readImagePermission) != PERMISSION_GRANTED) {
            storagePermissionLauncher2.launch(READ_MEDIA_IMAGES);
        } else if (ContextCompat.checkSelfPermission(requireContext(), readImagePermission) == PERMISSION_GRANTED) {
            selectFile();
        }else {
            storagePermissionLauncher2.launch(READ_EXTERNAL_STORAGE);
        }

    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                    imageUri=uri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        fileImageView.setImageBitmap(bitmap);
                        imagepath=uri.getPath();
                        getImagePath();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void getImagePath() {
        imagepath = getPath(imageUri);
        String imageType="";
        if(imagepath!=null){
            imageType=imagepath.substring(imagepath.lastIndexOf("."));
        }
        if(imagepath!=null && (imageType.equalsIgnoreCase(".jpg")||
                imageType.equalsIgnoreCase(".png")) ||
                imageType.equalsIgnoreCase(".jpeg")) {
            if (new File(imagepath).length() > IMAGE_MAX_SIZE_8MB) {
                if (new File(imagepath).length() > IMAGE_MAX_SIZE_1MB) {
//                Toast.makeText(context, "Image too large. Please select image less than 3 MB", Toast.LENGTH_LONG).show();
                    try {
                        File compressedImageFile = ImageCompressor.getCompressed(requireContext(),imagepath);
                        receiptFile = new File(requireContext().getExternalCacheDir(), compressedImageFile.getName());
//                    Toast.makeText(requireContext(), compressedImageFile.length()/(1024 * 1024)+"", Toast.LENGTH_SHORT).show();
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(receiptFile.getCanonicalPath(), bmOptions);
                        Display display= getActivity().getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;
                        float scaleHt = (float)width / bitmap.getWidth();
                        Log.e("Scaled percent ", " $scaleHt");
                        Bitmap scaled =
                                Bitmap.createScaledBitmap(bitmap, width, (int) (bitmap.getWidth() * scaleHt), true);
                        fileImageView.setImageBitmap(scaled);
                        networkOutputStream = new ByteArrayOutputStream();
                    } catch (IOException e) {
                        Toast.makeText(context, "Image too large. Please select image less than 3 MB", Toast.LENGTH_LONG).show();
                        throw new RuntimeException(e);
                    }
                }else {
//                imagepath = getPath(imageUri);
                    receiptFile=new File(imagepath);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmOptions);
                    fileImageView.setImageBitmap(bitmap);
                    networkOutputStream = new ByteArrayOutputStream();
                }
            }else {
                Toast.makeText(context, "Image too large. Please select image less than 8 MB", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(context, "Please select image in JPEG or PNG format", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {
//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.setType("image/*");
//        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//        launchSomeActivity.launch(chooseFile);
//        launchSomeActivity.launch(getPickImageIntent(context));

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

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

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private final String TAG = "CameraXApp";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private void openCamera(){
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }
    }

    private ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),permissions ->
    {
            // Handle Permission granted/rejected
            boolean permissionGranted = true;
        for (String key: permissions.keySet()) {
            permissionGranted=permissionGranted && permissions.get(key);
        }
        if (!permissionGranted) {
            Toast.makeText(requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show();
        } else {
            startCamera();
        }
    });

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            activityResultLauncher.launch(new String[]{CAMERA});
        }else {
            activityResultLauncher.launch(new String[]{CAMERA});
        }
    }

    private void startCamera() {

    }

    private boolean allPermissionsGranted() {
        return false;
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
        addAccount(model);
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

    private void addAccount(AgentDepositRequestRequestModel model) {
        new NetworkCall().callService(NetworkCall.getDepositRequestInterface().depositRequest(ApiConstants.AgentDepositRequest,
                        createRequestBody(new Gson().toJson(model)),
                        sendFile("File", receiptFile)),
                requireContext(), true, (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerNew(response);
                    }else {
                        Toast.makeText(requireContext(), R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerNew(ResponseBody response) {
        try {
            DepositRequestResponseModel responseModel = new Gson().fromJson(response.string(), DepositRequestResponseModel.class);
            hideCustomDialog();
            if(responseModel!=null){
                if(responseModel.DepositRequestResult.StatusCode.equals("0")) {
                    Toast.makeText(context, responseModel.DepositRequestResult.Data.Message, Toast.LENGTH_SHORT).show();
                    amount_edt.setText("");
                    receipt_no_edt.setText("");
                    networkOutputStream=null;
                    fileImageView.setImageResource(R.drawable.choose_file);
                    Common.showResponsePopUp(context, responseModel.DepositRequestResult.Data.Message);
                }else {
                    Toast.makeText(context, responseModel.DepositRequestResult.Status, Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
        }
    }

    private RequestBody createRequestBody(String userId) {
        return RequestBody.create(MediaType.parse("multipart/form-data"),
                userId);
//        return RequestBody.create(MediaType.parse("text/plain"),
//                userId);
    }

    private MultipartBody.Part sendFile2(String paramName, File imageFile){
        if(imageFile==null)
            return null;

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(requireContext().getContentResolver().getType(imageUri)),
                        imageFile
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(paramName, imageFile.getName(), requestFile);
        return body;
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