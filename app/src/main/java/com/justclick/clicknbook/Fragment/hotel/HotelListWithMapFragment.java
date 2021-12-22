/**
 * Copyright 2015-present Amberfog
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

package com.justclick.clicknbook.Fragment.hotel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.HotelListMapHeaderAdapter;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.utils.LockableRecyclerView;
import com.justclick.clicknbook.utils.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class HotelListWithMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_LOCATION = "arg.location";
    private Context context;
    // private LockableListView mListView;
    private LockableRecyclerView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private CameraUpdate cameraUpdate;
    // ListView stuff
    //private View mTransparentHeaderView;
    //private View mSpaceView;
    private View mTransparentView;
    private View mWhiteSpaceView;

    private HotelListMapHeaderAdapter hotelAvailabilityAdapter;

    private LatLng mLocation;
    private Marker mLocationMarker;

    private SupportMapFragment mMapFragment;

    private GoogleMap mMap;
    private boolean mIsNeedLocationUpdate = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private View rootView;

    public HotelListWithMapFragment() {
    }

    public static HotelListWithMapFragment newInstance(LatLng location) {
        HotelListWithMapFragment f = new HotelListWithMapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mListView = (LockableRecyclerView) rootView.findViewById(android.R.id.list);
            mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

            mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
            mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

            int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
            mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
            mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

            mSlidingUpPanelLayout.setPanelSlideListener(this);

            // transparent view at the top of ListView
            mTransparentView = rootView.findViewById(R.id.transparentView);
            mWhiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);

            // init header view for ListView
            // mTransparentHeaderView = inflater.inflate(R.layout.transparent_header_view, mListView, false);
            // mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

            collapseMap();

            mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mSlidingUpPanelLayout.onPanelDragged(0);
                }
            });

            ArrayList<HotelAvailabilityResponseModel.Hotels> testData = new ArrayList<>();
            for(int i=0; i<10; i++) {
                HotelAvailabilityResponseModel.Hotels object = new HotelAvailabilityResponseModel().new Hotels();
                testData.add(object);
            }
            // show white bg if there are not too many items
            // mWhiteSpaceView.setVisibility(View.VISIBLE);

            // ListView approach
        /*mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSlidingUpPanelLayout.collapsePane();
            }
        });*/
//        mHotelListMapHeaderAdapter = new HotelListMapHeaderAdapter(context, this,testData);

            hotelAvailabilityAdapter=new HotelListMapHeaderAdapter(context, new HotelListMapHeaderAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<HotelAvailabilityResponseModel.Hotels> list, int position) {
                    mSlidingUpPanelLayout.collapsePane();
                    switch (view.getId()){
                        case R.id.space:
                            mSlidingUpPanelLayout.collapsePane();
//                        HotelRoomInfoRequestModel model=new HotelRoomInfoRequestModel();
//                        model.DataFileName=list.get(position).DataFileName;
//                        model.HotelCode=list.get(position).HotelCode;
//                        model.ResultIndex=list.get(position).RoomIndex;
//                        model.Supplier="TBO";
//                        hotelMoreInfo(model);
                            break;

                        case R.id.bookHotelTv:
//                        HotelRoomInfoRequestModel model2=new HotelRoomInfoRequestModel();
//                        model2.DataFileName=list.get(position).DataFileName;
//                        model2.HotelCode=list.get(position).HotelCode;
//                        model2.ResultIndex=list.get(position).RoomIndex;
//                        model2.Supplier="TBO";
//                        hotelRoomData(model2);
                            break;
                        case R.id.itemLin:
                            HotelMoreInfoFragment hotelMoreInfoFragment=new HotelMoreInfoFragment();
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(
                                    hotelMoreInfoFragment);
                            break;
                    }
                }
            },testData);
            mListView.setItemAnimator(null);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mListView.setLayoutManager(layoutManager);
            mListView.setAdapter(hotelAvailabilityAdapter);

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLocation = getArguments().getParcelable(ARG_LOCATION);
        if (mLocation == null) {
            mLocation = getLastKnownLocation(false);
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);


        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
//                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                LatLng update = getLastKnownLocation();
                if (update != null) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 11.0f)));
                }
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mIsNeedLocationUpdate = false;
                        moveToLocation(latLng, false);
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
//        collapseMap();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private LatLng getLastKnownLocation() {
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }

    private void moveMarker(LatLng latLng) {


        if (mLocationMarker != null) {
            mLocationMarker.remove();
        }
        mLocationMarker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                .position(latLng).anchor(0.5f, 0.5f));
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {
        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && moveCamera) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        });
    }

    private void collapseMap() {
        if (hotelAvailabilityAdapter != null) {
            hotelAvailabilityAdapter.showSpace();
        }
        mTransparentView.setVisibility(View.GONE);
        if (mMap != null && mLocation != null && cameraUpdate!=null) {
            mMap.animateCamera(cameraUpdate);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (hotelAvailabilityAdapter != null) {
            hotelAvailabilityAdapter.hideSpace();
        }
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null && cameraUpdate!=null) {
            mMap.animateCamera(cameraUpdate);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);

//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);
        List<Marker> markers = new ArrayList<Marker>();
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(-34.1, 144)).title("The leela Palace")));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(-34.12, 140)).title("Hotel Name")));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(-34.04, 141)).title("Taj Hotel")));
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(-34.07, 143)).title("Swarg Hotel")));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        cameraUpdate=CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.map_custom_info_window, null, false);
        }

        @Override
        public View getInfoContents(Marker marker) {

            final TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.hotelNameTv));
            tvTitle.setText(marker.getTitle());
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(context, "Window clicked...", Toast.LENGTH_SHORT).show();
    }
}
