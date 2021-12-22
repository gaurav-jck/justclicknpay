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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.justclick.clicknbook.Activity.MapsActivity;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.HotelAvailabilityResponseModel;
import com.justclick.clicknbook.utils.LockableRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.justclick.clicknbook.R.id.map;

/**
 * Created by priyanshi on 12/5/2017.
 */

public class HotelMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_LOCATION = "arg.location";
    private Context context;
    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private Marker mLocationMarker;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private CameraUpdate cameraUpdate;
    private Location mLastLocation;
    private LatLng mLocation;
    private LockableRecyclerView mListView;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mMapFragment;
    private boolean mIsNeedLocationUpdate = true;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_maps, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

//        displayLocation();
        // Show location button click listener
       view.findViewById(R.id.btnShowLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }


       });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocation = getArguments().getParcelable(ARG_LOCATION);
        if (mLocation == null) {
            mLocation = getLastKnownLocation(false);
        }
//        mMapFragment = SupportMapFragment.newInstance();
//        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.map, mMapFragment, "map");
//        fragmentTransaction.commit();
//        mMapFragment.getMapAsync(this);
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mLastLocation = LocationServices.FusedLocationApi
//                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Toast.makeText(context, "Latitude="+latitude+"\nLongitude="+longitude,Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "Couldn't get the location." +
                    " Make sure location is enabled on the device",Toast.LENGTH_SHORT).show();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, context,PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(context,
//                        "This device is not supported.", Toast.LENGTH_LONG)
//                        .show();
////                finish();
//            }
            return false;
        }
        return true;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);

            // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//            LatLngBounds bounds = builder.build();
//            int padding = 50; // offset from edges of the map in pixels
//            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            googleMap.moveCamera(cameraUpdate);
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(context, "hotel",Toast.LENGTH_SHORT).show();

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
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        checkPlayServices();   }
}
