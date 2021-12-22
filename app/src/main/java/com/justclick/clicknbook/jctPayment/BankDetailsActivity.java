package com.justclick.clicknbook.jctPayment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.imageUpload.VolleyMultipartRequest;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankDetailsActivity extends AppCompatActivity {
    private final int VOLLEY_TIMEOUT=120000;
    private final float IMAGE_MAX_SIZE=1024*1024*4.2f, IMAGE_SERVER_SIZE=1024*1024*0.5f;
    private final int REQUEST_AMOUNT=1, MY_PERMISSIONS_REQUEST_STORAGE=2, REQUEST_IMAGE_CAPTURE = 1;
    private Context context;
    private AutoCompleteTextView atv_bank;
    private EditText txt_bene_name, txt_account_no, txt_confirm_account, txt_ifsc;
    ArrayAdapter arrayAdapter;
    ArrayList<String> bankArray = new ArrayList<>();
    HashMap<String, String> bank_iin = new HashMap<>();
    private ProgressDialog progressDialog;
    private ImageView imageFile;
    private ImageView check_image;
    private String imagepath;
    private ByteArrayOutputStream networkOutputStream=null;
    private boolean verified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);
        context=this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        putExtra("account",account)
//                .putExtra("name",name)
//                .putExtra("path", path)
//                .putExtra("ifsc", ifsc)
//                .putExtra("beneficiary_name",beneficiary_name)
//                .putExtra("hasBankDetail", true));


        progressDialog = new ProgressDialog(context);
        imageFile=  findViewById(R.id.imageFile);
        check_image=  findViewById(R.id.check_image);
        atv_bank = findViewById(R.id.atv_bank);
        txt_bene_name = findViewById(R.id.txt_bene_name);
        txt_account_no = findViewById(R.id.txt_account_no);
        txt_confirm_account = findViewById(R.id.txt_confirm_account);
        txt_ifsc = findViewById(R.id.txt_ifsc);

        if(getIntent().getBooleanExtra("hasBankDetail", false)){
            String account=getIntent().getStringExtra("account");
            String name=getIntent().getStringExtra("name");
            String path=getIntent().getStringExtra("path");
            String ifsc=getIntent().getStringExtra("ifsc");
            String beneficiary_name=getIntent().getStringExtra("beneficiary_name");
            showBankDetails(account, name, path, ifsc, beneficiary_name);
        }else {
            if(getIntent().hasExtra("verified")){
                verified=true;
            }else {
                verified=false;
            }
            addBankDetails();
        }

        atv_bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    atv_bank.showDropDown();
                    atv_bank.setError(null);
                }
            }
        });

        atv_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atv_bank.setError(null);
                atv_bank.showDropDown();
            }
        });

        atv_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Common.hideSoftKeyboard((BankDetailsActivity)context);
            }
        });

        findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    progressDialog.setMessage("Checking Bank Details....");
                    progressDialog.show();
                    BankDetailsModel request=new BankDetailsModel();
                    request.bankName=atv_bank.getText().toString().trim();
                    request.beneficiary_name=txt_bene_name.getText().toString().trim();
                    request.bank_account=txt_account_no.getText().toString().trim();
                    if(verified){
                        saveProfileAccount(URLs.updatebankdetail);
                    }else {
                        saveProfileAccount(URLs.addBankDetails);
                    }

                }
            }
        });

        findViewById(R.id.chooseFileLin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions((BankDetailsActivity)context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(Intent.createChooser(getPickImageIntent(context), "Select file to upload"), 1);
                }
            }
        });
    }
    /*public Intent getPickImageIntent(Context context) {
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }*/

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Bank_" + timeStamp + "_";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(resultCode == RESULT_OK && data != null){
            Uri selectedImageUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagepath = cursor.getString(columnIndex);
            cursor.close();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmOptions);

            if(imagepath!=null && (imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".jpg")||
                    imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".png"))) {
                imageFile.setImageBitmap(BitmapFactory.decodeFile(imagepath));
//                imageFile.setImageURI(selectedImageUri);
                networkOutputStream = new ByteArrayOutputStream();
            }else {
                Toast.makeText(context, "Please select image in JPEG or PNG format", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Error in selecting image! Unsupported image file.", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        if(resultCode == RESULT_OK && data != null){
            if(data.getExtras()!=null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                imagepath = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, "Deposit", null);
                if (imagepath != null && (imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".jpg") ||
                        imagepath.substring(imagepath.lastIndexOf(".")).equalsIgnoreCase(".png"))) {
                    if (new File(imagepath).length() > IMAGE_MAX_SIZE) {
                        Toast.makeText(context, "Image too large. Please select image less than 4 MB", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, "Image too large. Please select image less than 4 MB", Toast.LENGTH_LONG).show();
                    }else {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmOptions);
                        imageFile.setImageBitmap(bitmap);
                        networkOutputStream = new ByteArrayOutputStream();
                    }
                }else {
                    Toast.makeText(context, "Please select image in JPEG or PNG format", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //get bank list
    private void getBankList() {
        final String str_token = MyPreferences.getToken(context);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching The Bank List....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.BANK_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            JSONArray jsonArray = obj.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject myObject = jsonArray.getJSONObject(i);

                                bankArray.add(myObject.getString("bank_name"));
                                bank_iin.put(myObject.getString("bank_name"), myObject.getString("bank_iin")+"+"+myObject.getString("acquire_id"));
                            }
                            //spinner
                            arrayAdapter = new ArrayAdapter(context, R.layout.item_bank_list, bankArray);
                            atv_bank.setAdapter(arrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, getResources().getString(R.string.bankListErrorBal), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, getResources().getString(R.string.bankListErrorBal), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                //pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("Authorization", str_token);
                return pars;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveProfileAccount(String url) {
//   refer from:      https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
        final String str_token = MyPreferences.getToken(context);
//        final String bank = bank_iin.get(atv_bank.getText().toString().trim());
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                responseHandler(resultResponse, 1);
//                Toast.makeText(context, resultResponse, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                hideDialog();
                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "multipart/form-data");
                pars.put("Authorization", str_token);
                return pars;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("bankName", atv_bank.getText().toString().trim());
                params.put("bank_account", txt_account_no.getText().toString().trim());
                params.put("bank_ifsc", txt_ifsc.getText().toString().trim());
                params.put("beneficiary_name", txt_bene_name.getText().toString().trim());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
// file name could found file base or direct access from real path
// for now just get bitmap data from ImageView
//                Calendar
                params.put("image", new DataPart("_"+System.currentTimeMillis()+"CancelCheck.jpg", getFileDataFromDrawable(context, imageFile.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    private void hideDialog() {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        int compressUnit=100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
        byte[] imageBytes = baos.toByteArray();
        while(imageBytes.length>IMAGE_SERVER_SIZE){
            compressUnit=compressUnit-5;
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressUnit, baos);
            imageBytes = baos.toByteArray();
        }

        return imageBytes;
    }

    private void addBankDetails(){
        getBankList();
        check_image.setVisibility(View.GONE);
        findViewById(R.id.cancelCheckImageLabel).setVisibility(View.GONE);
        Picasso.with(context).invalidate(URLs.getCancelCheck+MyPreferences.getToken(context));
    }

    private void showBankDetails(String account, String name, String path, String ifsc, String beneficiary_name){
        check_image.setVisibility(View.VISIBLE);
        findViewById(R.id.cancelCheckImageLabel).setVisibility(View.VISIBLE);
        findViewById(R.id.confirm_account_no).setVisibility(View.GONE);
        findViewById(R.id.cancelCheckLabel).setVisibility(View.GONE);
        findViewById(R.id.chooseFileLin).setVisibility(View.GONE);
        findViewById(R.id.bt_submit).setVisibility(View.GONE);
        ((TextInputLayout)findViewById(R.id.bene_name)).setHint(getResources().getString(R.string.beneName));
        txt_bene_name.setText(beneficiary_name);
        txt_bene_name.setEnabled(false);
        ((TextInputLayout)findViewById(R.id.account_no)).setHint(getResources().getString(R.string.accountNo));
        txt_account_no.setText(account);
        txt_account_no.setEnabled(false);
        ((TextInputLayout)findViewById(R.id.ifsc_code)).setHint(getResources().getString(R.string.ifsc));
        txt_ifsc.setText(ifsc);
        txt_ifsc.setEnabled(false);
        atv_bank.setText(name);
        atv_bank.setEnabled(false);
        atv_bank.setFocusable(false);

        final Uri uri=Uri.parse(URLs.getCancelCheck+MyPreferences.getToken(context));
//        check_image.setImageURI(uri);
        Picasso.with(context).load(uri).fit().centerInside().placeholder(R.drawable.my_progress_one).into(check_image);
        //
        /*check_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            }
        });*/
    }

    private void responseHandler(String response, int TYPE) {
        hideDialog();
        if(response!=null){
            Toast.makeText(context, response.replace("{","").replace("}","").
                    replace("\"","").replace("[",""), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean validate() {
        try {
            /*if(atv_bank.getText().toString().trim().length()==0){
                Toast.makeText(context, R.string.empty_and_invalid_bank, Toast.LENGTH_LONG).show();
                return false;
            }else*/ if((!Common.isNameValid(txt_bene_name.getText().toString().trim()))) {
                Toast.makeText(context, R.string.invalid_bene_name, Toast.LENGTH_LONG).show();
                return false;
            }else if(txt_account_no.getText().toString().trim().length()<10){
                Toast.makeText(context, R.string.empty_and_invalid_account_number, Toast.LENGTH_LONG).show();
                return false;
            }else if(!txt_account_no.getText().toString().trim().equals(txt_confirm_account.getText().toString().trim())){
                Toast.makeText(context, R.string.empty_and_invalid_account_confirmation, Toast.LENGTH_LONG).show();
                return false;
            }else if((!Common.isIFSCValid(txt_ifsc.getText().toString().trim()))) {
                Toast.makeText(context, R.string.empty_and_invalid_ifsc_code, Toast.LENGTH_LONG).show();
                return false;
            }else if (networkOutputStream==null) {
                Toast.makeText(context, "please choose image file", Toast.LENGTH_LONG).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(context, R.string.exception_message, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private class BankDetailsModel{
        public String bankName, bank_account, bank_ifsc, beneficiary_name;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
