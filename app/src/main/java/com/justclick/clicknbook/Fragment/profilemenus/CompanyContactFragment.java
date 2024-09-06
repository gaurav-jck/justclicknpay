package com.justclick.clicknbook.Fragment.profilemenus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompanyContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanyContactFragment extends Fragment {
    private Context context;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    private OnMapReadyCallback callback = googleMap -> {
        LatLng sydney = new LatLng(28.6935131, 77.1485739);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("JustClicknPay"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
    };

    public CompanyContactFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CompanyContactFragment newInstance(String param1, String param2) {
        CompanyContactFragment fragment = new CompanyContactFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_company_contact, container, false);
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