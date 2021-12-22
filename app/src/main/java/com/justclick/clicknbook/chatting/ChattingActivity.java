/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justclick.clicknbook.chatting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        //   messageImageView = (ImageView) itemView.findViewById(R.Id.messageImageView);
//           messengerTextView = (TextView) itemView.findViewById(R.Id.messengerTextView);
       //     messengerImageView = (CircleImageView) itemView.findViewById(R.Id.messengerImageView);
        }
    }
    private LoginModel loginModel;
    private Context context;
    private static final String TAG = "ChattingActivity";
    public String MESSAGES_CHILD = "newmessages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 20000;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
   private ProgressDialog dialog;
   private AlertDialog dialog2;
    private EditText mMessageEditText;
    private TextView Agent_name;
    private ImageView mAddMessageImageView,agentImageView ;
    private LinearLayout.LayoutParams layoutParamsLeft, layoutParamsRight;

    // Firebase instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        context=this;
       // showProgressDialog();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        layoutParamsLeft=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsRight=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLeft.gravity=Gravity.LEFT;
        layoutParamsRight.gravity=Gravity.RIGHT;
//        layoutParams.gravity= Gravity.RIGHT;
        Agent_name= (TextView) findViewById(R.id.Agent_name);

        loginModel=new LoginModel();
        loginModel= MyPreferences.getLoginData(loginModel,context);
        ArrayList<String> arrayList=new ArrayList<>();
        if(getIntent().getStringExtra("JCT")!=null) {
            arrayList.add(getIntent().getStringExtra("JCT"));
            arrayList.add(loginModel.Data.DoneCardUser);
        }else {
            arrayList.add("JCT");
            arrayList.add(loginModel.Data.DoneCardUser);
        }
        if(getIntent().getStringExtra("AGENCY")!=null) {
            Agent_name.setText(getIntent().getStringExtra("AGENCY"));
        }

        Collections.sort(arrayList);
        MESSAGES_CHILD=arrayList.get(0)+"_"+arrayList.get(1);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        agentImageView= (ImageView) findViewById(R.id.agentImageView);



//        hideProgressDialog();
   //    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
// New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(context,"DATA Change",Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(context,"DATA error",Toast.LENGTH_LONG).show();
            }
        });
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
                MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              FriendlyMessage friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                Glide.with(viewHolder.messageImageView.getContext())
//                        .load(downloadUrl)
//                        .into(viewHolder.messageImageView);
//        hideProgressDialog();
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
//                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else {

                    String imageUrl = friendlyMessage.getImageUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
//                                            Glide.with(viewHolder.messageImageView.getContext())
//                                                    .load(downloadUrl)
//                                                    .into(viewHolder.messageImageView);
                                        } else {
                                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                            Log.w(TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
//                        Glide.with(viewHolder.messageImageView.getContext())
//                                .load(friendlyMessage.getImageUrl())
//                                .into(viewHolder.messageImageView);
                    }
//                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }

               // hideCustomDialog();
               // hideProgressDialog();
//                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if(!mPhotoUrl.equals(friendlyMessage.getPhotoUrl())){
                    Glide.with(ChattingActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(agentImageView);
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.receive_left_back);
                    viewHolder.messageTextView.setLayoutParams(layoutParamsLeft);
                }else {
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.send_right_back);
                    viewHolder.messageTextView.setLayoutParams(layoutParamsRight);
                }
              /*  if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChattingActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    Glide.with(ChattingActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);

                    if(!mPhotoUrl.equals(friendlyMessage.getPhotoUrl())){
                        Glide.with(ChattingActivity.this)
                                .load(friendlyMessage.getPhotoUrl())
                                .into(agentImageView);
                    }
                }*/

            }
        };

//       Toast.makeText(context,"hiiiii "+mFirebaseAdapter.getItemCount(),Toast.LENGTH_LONG).show();


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (friendlyMessageCount==0){
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);}
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {

                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl,
                        null /* no image */);
//                String node=
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");


            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
      //  showCustomDialog();
      //  showProgressDialog();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.Id.sign_out_menu:
//                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                mUsername = ANONYMOUS;
//                startActivity(new Intent(this, SignInActivity.class));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void showCustomDialog() {
        MyCustomDialog.showCustomDialog(context,"Please wait...");
    }

    private void hideCustomDialog() {
        MyCustomDialog.hideCustomDialog();
    }
    private void showProgressDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).
                inflate(R.layout.progressbar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        dialog2 = dialogBuilder.create();
        dialog2.show();
    }

    private void hideProgressDialog() {
        if (dialog2 != null && dialog2.isShowing()) {
            dialog2.dismiss();
        }
    }
}
