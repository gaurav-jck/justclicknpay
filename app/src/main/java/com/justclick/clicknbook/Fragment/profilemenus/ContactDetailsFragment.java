package com.justclick.clicknbook.Fragment.profilemenus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactDetailsFragment extends Fragment {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;

    public ContactDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContactDetailsFragment newInstance(String param1, String param2) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_contact_details, container, false);
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);

        view.findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}