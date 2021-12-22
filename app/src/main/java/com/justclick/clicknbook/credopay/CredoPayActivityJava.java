package com.justclick.clicknbook.credopay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.justclick.clicknbook.R;

import in.credopay.payment.sdk.CredopayPaymentConstants;
import in.credopay.payment.sdk.PaymentActivity;

public class CredoPayActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credo_pay);

        findViewById(R.id.credoPayTv).setOnClickListener(view->{
            openCredoPayActivity();
        });
    }

    private void openCredoPayActivity() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("TRANSACTION_TYPE", CredopayPaymentConstants.MICROATM);
//        Purchase Transaction
        intent.putExtra("DEBUG_MODE",true);
        intent.putExtra("PRODUCTION",false);
//        intent.putExtra("DEBUG_MODE",false);
//        intent.putExtra("PRODUCTION",true);
        intent.putExtra("AMOUNT",100);
        intent.putExtra("LOGIN_ID","2000000491");
        intent.putExtra("LOGIN_PASSWORD","nj6nyuRz&v");

        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}