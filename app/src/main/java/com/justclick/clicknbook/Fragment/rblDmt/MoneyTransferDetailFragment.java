package com.justclick.clicknbook.Fragment.rblDmt;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;


/**
 * Created by Lenovo on 03/28/2017.
 */

public class MoneyTransferDetailFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    TextView get_tv;
    EditText number_edt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            titleChangeListener = (ToolBarTitleChangeListener) context;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.receiver_detail_layout, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

    }

    @Override
    public void onClick(View v) {

    }

    private Boolean validate() {

        if (number_edt.getText().toString().length() < 10) {
            Toast.makeText(context, R.string.empty_and_invalid_mobile, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

