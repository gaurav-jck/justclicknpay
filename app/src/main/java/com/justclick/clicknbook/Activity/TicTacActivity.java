package com.justclick.clicknbook.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.R;

public class TicTacActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    private boolean isFirstTurn=true, isSecondTurn=false;
    private LinearLayout toeLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac);
        tv1= (TextView) findViewById(R.id.tv1);
        tv2= (TextView) findViewById(R.id.tv2);
        tv3= (TextView) findViewById(R.id.tv3);
        tv4= (TextView) findViewById(R.id.tv4);
        tv5= (TextView) findViewById(R.id.tv5);
        tv6= (TextView) findViewById(R.id.tv6);
        tv7= (TextView) findViewById(R.id.tv7);
        tv8= (TextView) findViewById(R.id.tv8);
        tv9= (TextView) findViewById(R.id.tv9);
        toeLinear= (LinearLayout) findViewById(R.id.toeLinear);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        findViewById(R.id.restartTv).setOnClickListener(this);

        setHints();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv1:
                if(isFirstTurn){
                    tv1.setText("0");
                    isFirstTurn=false;
                }else {
                    tv1.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv1.setClickable(false);
                break;

            case R.id.tv2:
                if(isFirstTurn){
                    tv2.setText("0");
                    isFirstTurn=false;
                }else {
                    tv2.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv2.setClickable(false);
                break;

            case R.id.tv3:
                if(isFirstTurn){
                    tv3.setText("0");
                    isFirstTurn=false;
                }else {
                    tv3.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv3.setClickable(false);
                break;

            case R.id.tv4:
                if(isFirstTurn){
                    tv4.setText("0");
                    isFirstTurn=false;
                }else {
                    tv4.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv4.setClickable(false);
                break;

            case R.id.tv5:
                if(isFirstTurn){
                    tv5.setText("0");
                    isFirstTurn=false;
                }else {
                    tv5.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv5.setClickable(false);
                break;

            case R.id.tv6:
                if(isFirstTurn){
                    tv6.setText("0");
                    isFirstTurn=false;
                }else {
                    tv6.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv6.setClickable(false);
                break;

            case R.id.tv7:
                if(isFirstTurn){
                    tv7.setText("0");
                    isFirstTurn=false;
                }else {
                    tv7.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv7.setClickable(false);
                break;

            case R.id.tv8:
                if(isFirstTurn){
                    tv8.setText("0");
                    isFirstTurn=false;
                }else {
                    tv8.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv8.setClickable(false);
                break;

            case R.id.tv9:
                if(isFirstTurn){
                    tv9.setText("0");
                    isFirstTurn=false;
                }else {
                    tv9.setText("x");
                    isFirstTurn=true;
                }
                setHints();
                winnerStatus();
                tv9.setClickable(false);
                break;
            case R.id.restartTv:
                restartGame();
                break;

        }
    }

    private void winnerStatus() {
        if(tv1.getText().toString().length()!=0 && tv1.getText().toString().equals(tv2.getText().toString()) &&
                tv1.getText().toString().equals(tv3.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv1.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv1,tv2,tv3);
        }else if(tv1.getText().toString().length()!=0 && tv1.getText().toString().equals(tv4.getText().toString()) &&
                tv1.getText().toString().equals(tv7.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv1.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv1,tv4,tv7);
        }else if(tv1.getText().toString().length()!=0 && tv1.getText().toString().equals(tv5.getText().toString()) &&
                tv1.getText().toString().equals(tv9.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv1.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv1,tv5,tv9);
        }else if(tv3.getText().toString().length()!=0 && tv3.getText().toString().equals(tv6.getText().toString()) &&
                tv3.getText().toString().equals(tv9.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv3.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv3,tv6,tv9);
        }else if(tv3.getText().toString().length()!=0 && tv3.getText().toString().equals(tv5.getText().toString()) &&
                tv3.getText().toString().equals(tv7.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv3.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv3,tv5,tv7);
        }else if(tv2.getText().toString().length()!=0 && tv2.getText().toString().equals(tv5.getText().toString()) &&
                tv2.getText().toString().equals(tv8.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv2.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv2,tv5,tv8);
        }else if(tv4.getText().toString().length()!=0 && tv4.getText().toString().equals(tv5.getText().toString()) &&
                tv4.getText().toString().equals(tv6.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv4.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv4,tv5,tv6);
        }else if(tv7.getText().toString().length()!=0 && tv7.getText().toString().equals(tv8.getText().toString()) &&
                tv7.getText().toString().equals(tv9.getText().toString())){
            Toast.makeText(TicTacActivity.this,"Winner is "+ tv7.getText().toString(),Toast.LENGTH_LONG).show();
            setTextViewDisable();
            setColorBackground(tv7,tv8,tv9);
        }else if(tv1.getText().toString().length()!=0 && tv2.getText().toString().length()!=0 &&
                tv3.getText().toString().length()!=0 && tv4.getText().toString().length()!=0 &&
                tv5.getText().toString().length()!=0 && tv6.getText().toString().length()!=0 &&
                tv7.getText().toString().length()!=0 && tv8.getText().toString().length()!=0 &&
                tv9.getText().toString().length()!=0){
            Toast.makeText(TicTacActivity.this,"Match Draw !!",Toast.LENGTH_LONG).show();
            toeLinear.setEnabled(false);
        }/*else {
            Toast.makeText(TicTacActivity.this,"Match draw",Toast.LENGTH_LONG).show();
        }*/
    }

    private void setColorBackground(TextView tv1, TextView tv2, TextView tv3) {
        tv1.setBackgroundResource(R.color.app_red_color_light);
        tv2.setBackgroundResource(R.color.app_red_color_light);
        tv3.setBackgroundResource(R.color.app_red_color_light);
    }

    private void setTextViewDisable() {
        tv1.setClickable(false);
        tv2.setClickable(false);
        tv3.setClickable(false);
        tv4.setClickable(false);
        tv5.setClickable(false);
        tv6.setClickable(false);
        tv7.setClickable(false);
        tv8.setClickable(false);
        tv9.setClickable(false);
    }

    private void restartGame() {
        isFirstTurn=true;
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");
        tv5.setText("");
        tv6.setText("");
        tv7.setText("");
        tv8.setText("");
        tv9.setText("");

        tv1.setClickable(true);
        tv2.setClickable(true);
        tv3.setClickable(true);
        tv4.setClickable(true);
        tv5.setClickable(true);
        tv6.setClickable(true);
        tv7.setClickable(true);
        tv8.setClickable(true);
        tv9.setClickable(true);

        tv1.setBackgroundResource(R.color.filterBackground);
        tv2.setBackgroundResource(R.color.filterBackground);
        tv3.setBackgroundResource(R.color.filterBackground);
        tv4.setBackgroundResource(R.color.filterBackground);
        tv5.setBackgroundResource(R.color.filterBackground);
        tv6.setBackgroundResource(R.color.filterBackground);
        tv7.setBackgroundResource(R.color.filterBackground);
        tv8.setBackgroundResource(R.color.filterBackground);
        tv9.setBackgroundResource(R.color.filterBackground);

        setHints();
    }

    private void setHints() {
        if(isFirstTurn){
            tv1.setHint("");
            tv2.setHint("");
            tv3.setHint("");
            tv4.setHint("");
            tv5.setHint("");
            tv6.setHint("");
            tv7.setHint("");
            tv8.setHint("");
            tv9.setHint("");
        }else {
            tv1.setHint("");
            tv2.setHint("");
            tv3.setHint("");
            tv4.setHint("");
            tv5.setHint("");
            tv6.setHint("");
            tv7.setHint("");
            tv8.setHint("");
            tv9.setHint("");
        }
    }
}
