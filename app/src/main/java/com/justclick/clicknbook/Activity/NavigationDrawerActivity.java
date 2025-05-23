package com.justclick.clicknbook.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.BuildConfig;
import com.justclick.clicknbook.Fragment.changetpin.ChangeTpinFragment;
import com.justclick.clicknbook.Fragment.jctmoney.dmt2.Dmt2GetSenderFragment;
import com.justclick.clicknbook.Fragment.jctmoney.dmt2.Dmt2SenderDetailFragment;
import com.justclick.clicknbook.Fragment.profilemenus.BankDetailsFragment;
import com.justclick.clicknbook.Fragment.profilemenus.CompanyContactFragment;
import com.justclick.clicknbook.Fragment.profilemenus.ContactDetailsFragment;
import com.justclick.clicknbook.Fragment.profilemenus.PolicyFragment;
import com.justclick.clicknbook.Fragment.profilemenus.raisequery.RaiseQueryDashboardFragment;
import com.justclick.clicknbook.Fragment.profilemenus.SupportQueryFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentDepositRequestFragmentNewww;
import com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist.AirBookingListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt.AccountStatementListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AdminCreditReportFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AdminDepositReportFragment;
import com.justclick.clicknbook.Fragment.AgentChattingListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentCreditListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentCreditRequestFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentDepositListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirSalesReportFragment;
import com.justclick.clicknbook.Fragment.billpay.BillPayMainPagerFragment;
import com.justclick.clicknbook.Fragment.bus.BusSearchFragment;
import com.justclick.clicknbook.Fragment.cashfreeQR.CashFreeQRCodeFragment;
import com.justclick.clicknbook.Fragment.cashfreeQR.QRCodeFragment;
import com.justclick.clicknbook.Fragment.cashout.GetSenderFragment;
import com.justclick.clicknbook.Fragment.cashout.SenderDetailFragment;
import com.justclick.clicknbook.Fragment.cashoutnew.PayoutBeneFragment;
import com.justclick.clicknbook.Fragment.changepassword.ChangePasswordFragment;
import com.justclick.clicknbook.Fragment.creditcard.CreditCardFragment;
import com.justclick.clicknbook.Fragment.fasttag.FasttagFragment;
import com.justclick.clicknbook.Fragment.flights.fragments.FlightSearch;
import com.justclick.clicknbook.Fragment.flights.fragments.PlacesSearch;
import com.justclick.clicknbook.Fragment.flights.responseModel.FlightCityModel;
import com.justclick.clicknbook.Fragment.hotel.HotelSearchFragment;
import com.justclick.clicknbook.Fragment.jctmoney.CashoutTransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.JctMoneyGetSenderFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentSearchFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirCancellationListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirRefundReportFragment;
import com.justclick.clicknbook.Fragment.profilemenus.BalanceCheckFragment;
import com.justclick.clicknbook.Fragment.HomeFragment;
import com.justclick.clicknbook.Fragment.jctmoney.RapipaySenderDetailFragment;
import com.justclick.clicknbook.Fragment.jctmoney.RapipayTransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.TransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.UtilityTransactionListFragment;
import com.justclick.clicknbook.Fragment.lic.LicFragment;
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragmentNew;
import com.justclick.clicknbook.Fragment.profilemenus.TermsFragment;
import com.justclick.clicknbook.Fragment.recharge.RechargeMainPagerFragment;
import com.justclick.clicknbook.Fragment.recharge.RechargeListFragment;
import com.justclick.clicknbook.Fragment.TrainBookingCheckFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.TrainFailedListFragment;
import com.justclick.clicknbook.Fragment.salesReport.AgentVerificationFragment;
import com.justclick.clicknbook.Fragment.salesReport.NetSalesReportFragmentNew;
import com.justclick.clicknbook.Fragment.salesReport.SalesAccountListFragment;
import com.justclick.clicknbook.Fragment.train.TrainBookingListNewFragment;
import com.justclick.clicknbook.Fragment.train.TrainDashboardFragment;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.credopay.CredoPayActivityJava;
import com.justclick.clicknbook.jctPayment.Dashboard_New_Activity;
import com.justclick.clicknbook.jctPayment.Dashboard_Old_Activity;
import com.justclick.clicknbook.jctPayment.Models.UserInfo;
import com.justclick.clicknbook.jctPayment.Utilities.SessionManager;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.model.AgentDetails;
import com.justclick.clicknbook.model.CommonResponseModel;
import com.justclick.clicknbook.model.DepositRequestResponseModel;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.model.RblCommonResponse;
import com.justclick.clicknbook.myinterface.FragmentBackPressListener;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.paysprintMatm.MainMatmFragment;
import com.justclick.clicknbook.rapipayMatm.MatmTransactionListFragment;
import com.justclick.clicknbook.requestmodels.AgentCreditDetailModel;
import com.justclick.clicknbook.requestmodels.CommonRequestModel;
import com.justclick.clicknbook.requestmodels.LoginRequestModel;
import com.justclick.clicknbook.utils.CodeEnum;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MenuCodes;
import com.justclick.clicknbook.utils.MyCustomDialog;
import com.justclick.clicknbook.utils.MyPreferences;
import com.justclick.clicknbook.utils.UserType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class NavigationDrawerActivity extends AppCompatActivity
        implements View.OnClickListener,
        ToolBarTitleChangeListener, FragmentBackPressListener, ToolBarHideFromFragmentListener,
        PlacesSearch.OnFlightCitySearchListener{

    private static final String SHARED_PREF_NAME = "jctsharedpref";
    private static final String KEY_AUTH_TOKEN = "authkey";
    private static final String TAG = "SignInActivity";
    private static final int APP_SESSION = 1, RBL_MAPPING = 2, CHECK_BALANCE = 3, CHECK_SESSION_AEPS=4;
    private static final int RC_SIGN_IN = 9001;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private FirebaseAuth mFirebaseAuth;
    private Animation animUp, animDown;
    private Context context;
    private TextView toolbar_title_tv, tv_agency_name, tv_email_id, agentCodeTv, mobileTv;
    private ImageView menu_filter;
    private Dialog filterDialog, appUpdateDialog, transAlertDialog;
    private Fragment mobileFragmentNew, agentSearchFragment,
            agentChattingListFragment,
            agentCreditRequestFragment, agentDepositRequestFragment,
            trainPnrCheckFragment, homeFragment,
            busSearchFragment,
            hotelSearchFragment, jctMoneyGetSenderFragment;
    private DrawerLayout drawer_layout;
//    private LinearLayout menu_items_lin_container;
    private AlertDialog dialog2;
    private LoginModel loginModel;
    private ListView agencyListOfFragment;
    private Toolbar toolbar;
    private ArrayList<LoginModel.DataList> list=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
//        https://fabric.io/just-click-travels/android/apps/com.justclick.clicknbook/issues/59c24d0dbe077a4dcceea509?time=last-seven-days
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Code for manually handling crash in the activity
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Toast.makeText(this, "App restarted after crash", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(this, "Activity OnCreate", Toast.LENGTH_LONG).show();
        context=this;
        loginModel=new LoginModel();

        toolbar_title_tv=  findViewById(R.id.toolbar_title_tv);
        menu_filter=  findViewById(R.id.menu_filter);
        tv_agency_name=  findViewById(R.id.tv_agency_name);
        tv_email_id=  findViewById(R.id.tv_email_id);
        mobileTv=  findViewById(R.id.mobileTv);
        agentCodeTv=  findViewById(R.id.agentCodeTv);
//        menu_items_lin_container =  findViewById(R.id.menu_items_lin_container);

        menu_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog = new Dialog(context);
                filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                filterDialog.setContentView(R.layout.filter_menu);
                filterDialog.show();
            }
        });
        try {
            loginModel= MyPreferences.getLoginData(loginModel,context);
            tv_agency_name.setText(loginModel.Data.AgencyName.toUpperCase());
            tv_email_id.setText(loginModel.Data.Email);
            agentCodeTv.setText("( "+loginModel.Data.DoneCardUser.toUpperCase()+" )");
            mobileTv.setText(loginModel.Data.Mobile);
        }catch (NullPointerException e){

        }

//        Animations
        animUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.translate_up);
        animDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.translate_down);

//method to show credit and deposit request
        showOrHideCreditOrDepositRequest();

        if(loginModel.Data.UserType.equals(UserType.SalesPerson)){
            findViewById(R.id.balance_lin).setVisibility(View.GONE);
        }

        initializeFragments();
//        initializeGoogleSignIn();
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);

        drawer_layout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Common.hideSoftKeyboard((NavigationDrawerActivity)context);
            }
        });
        toggle.syncState();
        navigationDrawerInitialize();

        /*if (getIntent().getBooleanExtra("SessionValidate", false)) {
//            Toast.makeText(this, "Validating session", Toast.LENGTH_SHORT).show();
            validateSession();
        }else {
//            Toast.makeText(this, "No validation", Toast.LENGTH_SHORT).show();
        }*/
        /*if(!MyPreferences.isUserLogin(context) || !MyPreferences.isUserValidated(context)) {

        }*/

    }

    /*public class SessionRequest{
        public String  UserCode,LoginSessionId;
    }
    private void validateSession() {
        SessionRequest sessionRequest=new SessionRequest();
        sessionRequest.UserCode=MyPreferences.getLoginData(loginModel,context).Data.UserId;
        sessionRequest.LoginSessionId=MyPreferences.getLoginData(loginModel,context).LoginSessionId;

        new NetworkCall().callService(NetworkCall.getLoginRequestInterface().
                        loginRequest(ApiConstants.Validatesession, sessionRequest), context,true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandlerSession(response);
                    }else {
                        hideCustomDialog();
//                        Toast./makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandlerSession(ResponseBody response) {
        try {
            DepositRequestResponseModel responseModel = new Gson().fromJson(response.string(), DepositRequestResponseModel.class);
            hideCustomDialog();
            if(responseModel!=null){
                if(responseModel.DepositRequestResult.StatusCode.equals("0")) {
//                    Toast.makeText(context, responseModel.DepositRequestResult.Data.Message, Toast.LENGTH_SHORT).show();
                    MyPreferences.setUserValidated(context);
//                    Common.showResponsePopUp(context, responseModel.DepositRequestResult.Data.Message);
                }else {
//                    Toast.makeText(context, responseModel.DepositRequestResult.Status, Toast.LENGTH_LONG).show();
                    MyPreferences.logoutUser(context);
                    Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(context, R.string.appSession, Toast.LENGTH_SHORT).show();
                }
            }else {
//                Toast.makeText(context,R.string.response_failure_message, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){

        }
    }*/

    private void showOrHideCreditOrDepositRequest() {
        if((loginModel.Data.UserType.equals(UserType.Agent)||loginModel.Data.UserType.equals(UserType.Distributor))
                && !(loginModel.Data.ValidationCode.equals("D"))){
            findViewById(R.id.credit_request_lin).setVisibility(View.VISIBLE);
            findViewById(R.id.deposit_request_lin).setVisibility(View.VISIBLE);
        }else if(loginModel.Data.ValidationCode.equals("D")){
            findViewById(R.id.credit_request_lin).setVisibility(View.GONE);
            findViewById(R.id.deposit_request_lin).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.credit_request_lin).setVisibility(View.GONE);
            findViewById(R.id.deposit_request_lin).setVisibility(View.GONE);
        }
        if(loginModel.Data.ModuleAccess.contains(MenuCodes.DMT+"-1")){
            findViewById(R.id.credit_request_lin).setVisibility(View.GONE);
        }

//        remove these lines
//        findViewById(R.id.credit_request_lin).setVisibility(View.GONE);
//        findViewById(R.id.deposit_request_lin).setVisibility(View.GONE);
    }

    private void initializeGoogleSignIn() {
        // Initialize FirebaseAuth
        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("Google Sign-In")
                .requestEmail()
                .build();
    }

    private void initializeFragments() {
        mobileFragmentNew=new RechargeMainPagerFragment();
        agentSearchFragment =new AgentSearchFragment();
        agentCreditRequestFragment =new AgentCreditRequestFragment();
//        agentDepositRequestFragment =new AgentDepositRequestFragmentNew();
        agentDepositRequestFragment =new AgentDepositRequestFragmentNewww();
        agentChattingListFragment =new AgentChattingListFragment();
        trainPnrCheckFragment =new TrainBookingCheckFragment();
        homeFragment =new HomeFragment();
        jctMoneyGetSenderFragment =new JctMoneyGetSenderFragment();
        hotelSearchFragment =new HotelSearchFragment();
        busSearchFragment = new BusSearchFragment();
        toolbar_title_tv.setText(getResources().getString(R.string.nav_home));
        replaceFragmentWithBackStack(homeFragment);
    }

    private void navigationDrawerInitialize() {
        findViewById(R.id.balance_lin).setOnClickListener(this);
        findViewById(R.id.home_lin).setOnClickListener(this);
        findViewById(R.id.bank_details_lin).setOnClickListener(this);
        findViewById(R.id.contact_details_lin).setOnClickListener(this);
        findViewById(R.id.terms_lin).setOnClickListener(this);
        findViewById(R.id.policy_lin).setOnClickListener(this);
        findViewById(R.id.company_contact_lin).setOnClickListener(this);
        findViewById(R.id.query_lin).setOnClickListener(this);
        findViewById(R.id.raise_query_lin).setOnClickListener(this);
        findViewById(R.id.logout_lin).setOnClickListener(this);
        findViewById(R.id.changePassLin).setOnClickListener(this);
        findViewById(R.id.changeTpinLin).setOnClickListener(this);
        findViewById(R.id.deposit_request_lin).setOnClickListener(this);
        findViewById(R.id.credit_request_lin).setOnClickListener(this);
        ((TextView)findViewById(R.id.appVersionTv)).setText("App Version-" + BuildConfig.VERSION_NAME);

        try {
            list = getHomeScreenProductMenus();
        } catch (Exception e) {
            e.printStackTrace();
            list=new ArrayList<>();
        }

        for(int i=0; i<list.size(); i++){
            View view=getLayoutInflater().inflate(R.layout.drawer_mainitem_layout, null);
            ((TextView)view.findViewById(R.id.tv_dashboard)).setText(list.get(i).Mainmenu);
            final LinearLayout subItemContainer= (LinearLayout) view.findViewById(R.id.subItemContainer);
            final ImageView iv_downarrow_dashboard= (ImageView) view.findViewById(R.id.iv_downarrow_dashboard);
            view.findViewById(R.id.main_title_lin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuDownArrowClick(subItemContainer, iv_downarrow_dashboard);
                }
            });

            for(int j=0; j<list.get(i).subMenu.size(); j++){
                View subItem=getLayoutInflater().inflate(R.layout.drawer_subitem_layout, null);
                ((ImageView)subItem.findViewById(R.id.iv_sub_menu)).setImageResource(getImage(list.get(i).subMenu.get(j).SubMenuCode));
                ((TextView)subItem.findViewById(R.id.tv_sub_menu)).setText(list.get(i).subMenu.get(j).SubMenu);

                final int finalI = i;
                final int finalJ = j;
                subItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openThisFragment(list.get(finalI).subMenu.get(finalJ).SubMenuCode,
                                list.get(finalI).subMenu.get(finalJ).SubMenu);
                    }
                });

                subItemContainer.addView(subItem);
            }

//            menu_items_lin_container.addView(view);
        }

    }

    private void openThisFragment(String subMenuCode, String fragmentName) {
//        Toast.makeText(context, subMenuCode.toString(), Toast.LENGTH_SHORT).show();
        Bundle bundle=new Bundle();
        bundle.putString("FragmentName", fragmentName);
        switch (subMenuCode) {
            case MenuCodes.UpdateCredit:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(agentSearchFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.DepositUpdateRequest:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AdminDepositReportFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CreditUpdateRequest:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AdminCreditReportFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.DepositList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AgentDepositListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CreditList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AgentCreditListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AgentCreditRequestFragment:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(agentCreditRequestFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AgentDepositRequestFragment:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(agentDepositRequestFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.MobileFragment:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(mobileFragmentNew);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.FlightSearch:
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(FlightSearchFragment.newInstance());
                AirWebviewActivity.airSession(context);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.BusSearch:
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(busSearchFragment);
                AirWebviewActivity.airSession(context);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.HotelSearch:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(hotelSearchFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.DMT:
//                checkMapping();
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(jctMoneyGetSenderFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.DMT2:
//                checkMapping();
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new Dmt2GetSenderFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TrainBookingcheck:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(trainPnrCheckFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TrainFailedList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainFailedListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AirSalesReport:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AirSalesReportFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AirRefundReport:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AirRefundReportFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AirCancellationReport:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AirCancellationListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.BusBookingList:
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new BusTransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.JCTMoneyList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new RapipayTransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TopUpDetails:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new RechargeListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AEPS:
                startActivity(new Intent(context, Dashboard_New_Activity.class));
               /* if(Common.checkInternetConnection(context)){
//                    userLogin(true);
                    checkAepsCredential();
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }*/
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AEPS_OLD:
//                startActivity(new Intent(context, Dashboard_New_Activity.class));
                if(Common.checkInternetConnection(context)){
                    userLogin(true);
                }else {
                    Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.MATM:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new MainMatmFragment());
//                startActivity(new Intent(context, CredoPayActivityJava.class));
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CREDOPAY:
                startActivity(new Intent(context, CredoPayActivityJava.class));
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.FAST_TAG:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new FasttagFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CREDIT:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CreditCardFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TRAIN:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainDashboardFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TRAIN_TENT:
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainDashboardFragment());
                Toast.makeText(context, "Book irctc tent in mahakumbh", Toast.LENGTH_SHORT).show();
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CASH_OUT:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new GetSenderFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.PayoutList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CashoutTransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.UtilityList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new UtilityTransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CreditCardList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AirBookingList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AirBookingListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AccountStatement:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AccountStatementListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.LIC:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new LicFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.BILL_PAY:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new BillPayMainPagerFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.PAYTM:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new PaytmWalletFragmentNew());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.CASHFREE_QR:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CashFreeQRCodeFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.DYNAMIC_QR:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new QRCodeFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.NetSalesReport:
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new NetSalesReportFragment());
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new NetSalesReportFragmentNew());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.SalesAccountStatement:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new SalesAccountListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.TrainBookingList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainBookingListNewFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.AepsList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new com.justclick.clicknbook.jctPayment.Fragments.TransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.MatmList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new MatmTransactionListFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case MenuCodes.ApproveAgent:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(AgentVerificationFragment.newInstance());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

        }
    }

    private int getImage(String subMenuCode) {
        switch (subMenuCode){
            case MenuCodes.UpdateCredit:
                return R.drawable.ic_update_credit_black;
            case MenuCodes.CreditList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.DepositList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.DepositUpdateRequest:
                return R.drawable.ic_icon_cash_black;
            case MenuCodes.CreditUpdateRequest:
                return R.drawable.credit_icon;
            case MenuCodes.MobileFragment:
                return R.drawable.ic_icon_mobile_payment_black;
            case MenuCodes.FlightSearch:
                return R.drawable.air_icon;
            case MenuCodes.BusSearch:
                return R.drawable.ic_bus_icon_black;
            case MenuCodes.AEPS:
                return R.drawable.ic_icon_rupee;
            case MenuCodes.AEPS_OLD:
                return R.drawable.ic_icon_rupee;
            case MenuCodes.MATM:
                return R.drawable.ic_icon_mobile_payment_black;
            case MenuCodes.CREDOPAY:
                return R.drawable.ic_icon_mobile_payment_black;
            case MenuCodes.FAST_TAG:
                return R.drawable.ic_icon_mobile_payment_black;
            case MenuCodes.CREDIT:
                return R.drawable.ic_credit_card;
            case MenuCodes.TRAIN:
                return R.drawable.train_booking_check_icon;
            case MenuCodes.TRAIN_TENT:
                return R.drawable.train_booking_check_icon;
            case MenuCodes.HotelSearch:
                return R.drawable.hotel_menu_icon;
            case MenuCodes.AgentCreditRequestFragment:
                return R.drawable.request_money;
            case MenuCodes.AgentDepositRequestFragment:
                return R.drawable.ic_icon_cash_black;
            case MenuCodes.TrainBookingcheck:
                return R.drawable.train_booking_check_icon;
            case MenuCodes.DMT:
            case MenuCodes.DMT2:
                return R.drawable.money_transfer_menu_icon;
            case MenuCodes.CASH_OUT:
                return R.drawable.money_transfer_menu_icon;
            case MenuCodes.LIC:
                return R.drawable.lic_vector;
            case MenuCodes.PAYTM:
                return R.drawable.account_balance_wallet;
            case MenuCodes.CASHFREE_QR:
                return R.drawable.ic_qr_code;
            case MenuCodes.DYNAMIC_QR:
                return R.drawable.ic_qr_code;
            case MenuCodes.BILL_PAY:
                return R.drawable.ic_icon_mobile_payment_black;
            case MenuCodes.AirSalesReport:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.AirRefundReport:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.AirCancellationReport:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.BusBookingList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.JCTMoneyList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.PayoutList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.UtilityList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.CreditCardList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.AirBookingList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.AccountStatement:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.TrainBookingList:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.TopUpDetails:
                return R.drawable.ic_icon_list_view;
            case MenuCodes.NetSalesReport:
                return R.drawable.net_sales_report_big;
            case MenuCodes.SalesAccountStatement:
                return R.drawable.sales_account_statement;
            case MenuCodes.ApproveAgent:
                return R.drawable.agent_verification;

            default:
                return R.drawable.mobile_icon;
        }
    }

    @Override
    public void onClick(View v) {
        Common.hideSoftKeyboard(NavigationDrawerActivity.this);
        switch (v.getId()){

            case R.id.balance_lin:
                if(Common.checkInternetConnection(context)) {
                    checkBalance();
                    drawer_layout.closeDrawer(GravityCompat.START);
                }else {
                    Toast.makeText(context,R.string.no_internet_message,Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.home_lin:
                replaceFragmentWithBackStack(homeFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;

            case R.id.deposit_request_lin:
                replaceFragmentWithBackStack(agentDepositRequestFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.credit_request_lin:
                replaceFragmentWithBackStack(agentCreditRequestFragment);
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.bank_details_lin:
                replaceFragmentWithBackStack(new BankDetailsFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.contact_details_lin:
                replaceFragmentWithBackStack(new ContactDetailsFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.terms_lin:
                replaceFragmentWithBackStack(new TermsFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.policy_lin:
                replaceFragmentWithBackStack(new PolicyFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.company_contact_lin:
                replaceFragmentWithBackStack(new CompanyContactFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.query_lin:
                replaceFragmentWithBackStack(new SupportQueryFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.raise_query_lin:
                replaceFragmentWithBackStack(new RaiseQueryDashboardFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.changePassLin:
                replaceFragmentWithBackStack(new ChangePasswordFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.changeTpinLin:
                replaceFragmentWithBackStack(new ChangeTpinFragment());
                drawer_layout.closeDrawer(GravityCompat.START);
                break;
            case R.id.logout_lin:
                MyPreferences.logoutUserRemember(context);
                startActivity(new Intent(context, MyLoginActivityNew.class));
                finish();
                break;

//            case R.id.chatting_lin:
//                showProgressDialog();
//                signIn();
//                break;

        }
    }

    private void checkBalance() {
        AgentCreditDetailModel creditDetailModel=new AgentCreditDetailModel();
        creditDetailModel.DoneCardUser =loginModel.Data.DoneCardUser;
        creditDetailModel.DeviceId =Common.getDeviceId(context);
        creditDetailModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        creditDetailModel.AgentDoneCardUser = loginModel.Data.DoneCardUser;

        showCustomDialog();
        new NetworkCall().callMobileService(creditDetailModel , ApiConstants.CreditDetails, context, new NetworkCall.RetrofitResponseListener() {
            @Override
            public void onRetrofitResponse(ResponseBody response, int responseCode) {
                if(response!=null){
                    responseHandler(response, CHECK_BALANCE);
                }else {
                    hideCustomDialog();
                    Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showCustomDialog(){
        MyCustomDialog.showCustomDialog(context,getResources().getString(R.string.please_wait));
    }
    private void hideCustomDialog(){
        MyCustomDialog.hideCustomDialog();
    }

    private void menuDownArrowClick(LinearLayout subItemContainer, ImageView iv_downarrow_dashboard) {
        if(subItemContainer.getVisibility()==View.VISIBLE){
            subItemContainer.startAnimation(animUp);
            subItemContainer.setVisibility(View.GONE);
            iv_downarrow_dashboard.setRotation(0);
        }else {
            subItemContainer.startAnimation(animDown);
            subItemContainer.setVisibility(View.VISIBLE);
            iv_downarrow_dashboard.setRotation(180);
        }
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
                .replace(R.id.container,fragment,fragment.getTag()).commit();
    }

    public void replaceFragmentWithBackStack(Fragment fragment){
        FragmentManager fm= getSupportFragmentManager();
        if(!fragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
            .replace(R.id.container, fragment, fragment.getTag()).addToBackStack(null).commit();
        }
    }

    public void replaceFragmentWithTag(Fragment fragment, String tag){
        FragmentManager fm= getSupportFragmentManager();
        if(!fragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
                    .replace(R.id.container, fragment, tag).addToBackStack(null).commit();
        }
    }
    public void replaceFragmentWithTagNoBackStack(Fragment fragment, String tag){
        FragmentManager fm= getSupportFragmentManager();
        if(!fragment.isVisible()) {
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_enter_from_right, R.anim.fragment_exit_to_left, R.anim.fragment_enter_from_left, R.anim.fragment_exit_to_right)
                    .replace(R.id.container, fragment, tag).commit();
        }
    }

    @Override
    public void onToolBarTitleChange(String title) {
        toolbar_title_tv.setText(title);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                hideProgressDialog();
//                replaceFragmentWithBackStack(agentChattingListFragment);
                Toast.makeText(context, "Google Sign-In failed",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google Sign-In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            replaceFragmentWithBackStack(agentChattingListFragment);
                            drawer_layout.closeDrawer(GravityCompat.START);
                            drawer_layout.closeDrawer(GravityCompat.START);
                            //hideCustomDialog();
                        }
                    }
                });
    }

    public ArrayList<LoginModel.DataList> getHomeScreenProductMenus() throws NullPointerException {
        ArrayList<LoginModel.DataList> list=new ArrayList<>();
        boolean isUtilityBill=false;
        boolean isPayout=false;
        boolean isDmt2=false;
        boolean isQR=false;

        if(loginModel.ProductList!=null && loginModel.ProductList.size()>0){
            LoginModel.DataList dataList=loginModel.new DataList();
            dataList.Mainmenu=MenuCodes.ProductMainTitle;
            dataList.MainmenuCode=MenuCodes.ProductMainCode;

            ArrayList<LoginModel.DataList.subMenu> subMenuArrayList=new ArrayList<>();
            String modules[]=loginModel.Data.ModuleAccess.split(",");
            //  loginModel.Data.ModuleAccess.contains(MenuCodes.DMT+"-1"
                /*for(int i=0; i<loginModel.ProductList.size(); i++){
                    if(loginModel.ProductList.get(i).Active!=null && ((loginModel.ProductList.get(i).Active.equals("1") &&
                                    loginModel.ProductList.get(i).ProductCode.equalsIgnoreCase(MenuCodes.DMT)||
                            loginModel.ProductList.get(i).ProductCode.equalsIgnoreCase(MenuCodes.BusSearch) ||
                            loginModel.ProductList.get(i).ProductCode.equalsIgnoreCase(MenuCodes.AEPS) ))){*/
            for(int i=0; i<modules.length;i++){
                if(modules[i].contains(MenuCodes.DMT+"-1")|| modules[i].contains(MenuCodes.AEPS+"-1")||
                        modules[i].contains(MenuCodes.BusSearch+"-1")   ||
                        modules[i].contains(MenuCodes.MobileFragment+"-1" ) ||
                        modules[i].contains(MenuCodes.MATM+"-1") ||
//                        modules[i].contains(MenuCodes.PAYTM+"-1") ||
                        modules[i].contains(MenuCodes.TRAIN+"-1") ||
                        modules[i].contains(MenuCodes.CASHFREE_QR+"-1")||
                        modules[i].contains(MenuCodes.FlightSearch+"-1")||
                        modules[i].contains(MenuCodes.BILL_PAY+"-1") ){
                    LoginModel.DataList.subMenu subMenu=dataList.new subMenu();
//                        subMenu.SubMenu=loginModel.ProductList.get(i).Product;      //previous
//                        subMenu.SubMenuCode=loginModel.ProductList.get(i).ProductCode;
                    subMenu.SubMenu=modules[i].substring(0,modules[i].indexOf("-"));
                    subMenu.SubMenuCode=modules[i].substring(0,modules[i].indexOf("-"));
                    if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.DMT)){
                        subMenu.SubMenu="DMT";
                        isDmt2=true;
                        findViewById(R.id.credit_request_lin).setVisibility(View.GONE);
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.MobileFragment)){
                        subMenu.SubMenu="Recharge";
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.AEPS)){
                        subMenu.SubMenu="AEPS";
                        isPayout=true;
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.PAYTM)){
                        subMenu.SubMenu="Paytm Wallet";
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.TRAIN)){
                        subMenu.SubMenu="IRCTC";
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.CASHFREE_QR)){
                        subMenu.SubMenu="QR Code";
                        isQR=true;
                    }else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.BILL_PAY)){
                        subMenu.SubMenu="Utility Bill";
                        isUtilityBill=true;
                    }

//                    else if(subMenu.SubMenuCode.equalsIgnoreCase(MenuCodes.AEPS)){    //hardcoded
//                        LoginModel.DataList.subMenu subMenuHotel=dataList.new subMenu();
//                        subMenuHotel.SubMenu=MenuCodes.AEPS_OLD;
//                        subMenuHotel.SubMenuCode=MenuCodes.AEPS_OLD;
//                        subMenuArrayList.add(subMenuHotel);
//                    }
                    if(!((loginModel.Data.UserType.equalsIgnoreCase(UserType.Distributor))||
                            (loginModel.Data.UserType.equalsIgnoreCase(UserType.SalesPerson)) ||
                            (loginModel.Data.UserType.equalsIgnoreCase(UserType.AdminStaff)))) {
                        subMenuArrayList.add(subMenu);
                        if(isDmt2){
                            LoginModel.DataList.subMenu dmt2 = dataList.new subMenu();
                            dmt2.SubMenu = MenuCodes.DMT2;
                            dmt2.SubMenuCode = MenuCodes.DMT2;
                            subMenuArrayList.add(dmt2);
                            isDmt2=false;
                        }
                    }
                }
            }



//            hardcoded

//            LoginModel.DataList.subMenu tent=dataList.new subMenu();
//            tent.SubMenu=MenuCodes.TRAIN_TENT;
//            tent.SubMenuCode=MenuCodes.TRAIN_TENT;
//            subMenuArrayList.add(tent);

            if(isUtilityBill && !(loginModel.Data.UserType.equals(UserType.Distributor) || loginModel.Data.UserType.equals(UserType.AdminStaff))){
                LoginModel.DataList.subMenu fasttag=dataList.new subMenu();
                fasttag.SubMenu=MenuCodes.FAST_TAG;
                fasttag.SubMenuCode=MenuCodes.FAST_TAG;
                subMenuArrayList.add(fasttag);

                LoginModel.DataList.subMenu subLic=dataList.new subMenu();
                subLic.SubMenu=MenuCodes.LIC;
                subLic.SubMenuCode=MenuCodes.LIC;
                subMenuArrayList.add(subLic);

                //Credit card hide
                /*LoginModel.DataList.subMenu subPaytm=dataList.new subMenu();
                subPaytm.SubMenu=MenuCodes.CREDIT;
                subPaytm.SubMenuCode=MenuCodes.CREDIT;
                subMenuArrayList.add(subPaytm);*/
            }
            if(isPayout) {
                LoginModel.DataList.subMenu subMenuCashOut = dataList.new subMenu();
                subMenuCashOut.SubMenu = MenuCodes.CASH_OUT;
                subMenuCashOut.SubMenuCode = MenuCodes.CASH_OUT;
                subMenuArrayList.add(subMenuCashOut);
            }

            if(loginModel.Data.UserType.equals(UserType.Distributor)){
                LoginModel.DataList.subMenu qr=loginModel.new DataList().new subMenu();
                qr.SubMenu=MenuCodes.CASHFREE_QR;
                qr.SubMenuCode=MenuCodes.CASHFREE_QR;
                subMenuArrayList.add(qr);
            }


            /*if(subMenuArrayList.size()>3){
                Collections.swap(subMenuArrayList, 1,3);
            }*/

            dataList.subMenu=subMenuArrayList;
            if(subMenuArrayList.size()>0){
                list.add(dataList);
            }
        }

        for(int i=0; i<loginModel.DataList.size(); i++){
            LoginModel.DataList dataList=loginModel.new DataList();
            ArrayList<LoginModel.DataList.subMenu> subMenuArrayList=new ArrayList<>();
            for(int j=0; j<loginModel.DataList.get(i).subMenu.size(); j++){
                if(MenuCodes.ImplementedMenuCodes.contains(loginModel.DataList.get(i).subMenu.get(j).SubMenuCode)){
                    if(loginModel.DataList.get(i).subMenu.get(j).SubMenuCode.equals(MenuCodes.JCTMoneyList)){
                        loginModel.DataList.get(i).subMenu.get(j).SubMenu="DMT List";

                        LoginModel.DataList.subMenu payoutList=loginModel.new DataList().new subMenu();
                        payoutList.SubMenu=MenuCodes.PayoutList;
                        payoutList.SubMenuCode=MenuCodes.PayoutList;
                        subMenuArrayList.add(payoutList);

                        LoginModel.DataList.subMenu utilityList=loginModel.new DataList().new subMenu();
                        utilityList.SubMenu=MenuCodes.UtilityList;
                        utilityList.SubMenuCode=MenuCodes.UtilityList;
                        subMenuArrayList.add(utilityList);

                        LoginModel.DataList.subMenu ccList=loginModel.new DataList().new subMenu();
                        ccList.SubMenu=MenuCodes.CreditCardList;
                        ccList.SubMenuCode=MenuCodes.CreditCardList;
                        subMenuArrayList.add(ccList);

                        if(loginModel.Data.UserType.equals(UserType.Distributor) || loginModel.Data.UserType.equals(UserType.AdminStaff)
                                || loginModel.Data.UserType.equals(UserType.Admin)){
                            LoginModel.DataList.subMenu irctcList=loginModel.new DataList().new subMenu();
                            irctcList.SubMenu=MenuCodes.TrainBookingListDisplay;
                            irctcList.SubMenuCode=MenuCodes.TrainBookingList;
                            subMenuArrayList.add(irctcList);
                        }
                        if(loginModel.Data.UserType.equals(UserType.Distributor) || loginModel.Data.UserType.equals(UserType.Agent)
                                || loginModel.Data.UserType.equals(UserType.AdminStaff) || loginModel.Data.UserType.equals(UserType.Admin)){
                            LoginModel.DataList.subMenu aepsList=loginModel.new DataList().new subMenu();
                            aepsList.SubMenu=MenuCodes.AepsListDisplay;
                            aepsList.SubMenuCode=MenuCodes.AepsList;
                            subMenuArrayList.add(aepsList);

                            LoginModel.DataList.subMenu matmList=loginModel.new DataList().new subMenu();
                            matmList.SubMenu=MenuCodes.MatmListDisplay;
                            matmList.SubMenuCode=MenuCodes.MatmList;
                            subMenuArrayList.add(matmList);
                        }
                    }else if(loginModel.DataList.get(i).subMenu.get(j).SubMenuCode.equals(MenuCodes.AccountStatement)){
                        loginModel.DataList.get(i).subMenu.get(j).SubMenu="Account\nStatement";
                    }
                    subMenuArrayList.add(loginModel.DataList.get(i).subMenu.get(j));
                }
            }

            if(subMenuArrayList.size()>0){
                dataList.Mainmenu=loginModel.DataList.get(i).Mainmenu;
                dataList.MainmenuCode=loginModel.DataList.get(i).MainmenuCode;
                dataList.subMenu=subMenuArrayList;
                list.add(dataList);
            }
        }
        //list.addAll(loginModel.DataList);
        return list;
    }

    private void showProgressDialog() {
        try{
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            final View dialogView = LayoutInflater.from(context).
                    inflate(R.layout.progressbar, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            dialog2 = dialogBuilder.create();
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog2.show();
        }catch (Exception e){

        }

    }


    private void hideProgressDialog() {
        if (dialog2 != null && dialog2.isShowing()) {
            dialog2.dismiss();
        }
    }

    public void checkMapping() {
        AgentCreditDetailModel creditDetailModel=new AgentCreditDetailModel();
        creditDetailModel.DoneCardUser =loginModel.Data.DoneCardUser;
        if(Common.checkInternetConnection(context)){
            new NetworkCall().callRblLongTimeService(creditDetailModel , ApiConstants.Mappping, context, new NetworkCall.RetrofitResponseListener() {
                @Override
                public void onRetrofitResponse(ResponseBody response, int responseCode) {
                    if(response!=null){
                        responseHandler(response, RBL_MAPPING);
                    }else {
                        hideCustomDialog();
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }, true);
        }else {
            Toast.makeText(context, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void appSession() {
        CommonRequestModel requestModel=new CommonRequestModel();
        requestModel.DeviceId=Common.getDeviceId(context);
        requestModel.LoginSessionId= EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        requestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        requestModel.VersionCode=Common.getVersionCode(context);

        new NetworkCall().callRblLongTimeService(requestModel, ApiConstants.AppSession, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            responseHandler(response, APP_SESSION);
                        }
                    }
                },false);
    }

    private void responseHandler(ResponseBody response, int TYPE) {
        try {
            switch (TYPE){
                case APP_SESSION:
                    RblCommonResponse rblCommonResponse = new Gson().fromJson(response.string(), RblCommonResponse.class);
                    if(rblCommonResponse!=null && rblCommonResponse.status == 0) {
                        MyPreferences.logoutUser(context);
                        Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(context, R.string.appSession, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case RBL_MAPPING:
                    RblCommonResponse rblCommonResponse2 = new Gson().fromJson(response.string(), RblCommonResponse.class);
                    if(rblCommonResponse2!=null){
                        if(rblCommonResponse2.status == 1) {
                            startActivity(new Intent(context, MoneyTransferActivity.class));
                        }else {
                            Toast.makeText(context, rblCommonResponse2.description, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case CHECK_BALANCE:
                    AgentDetails agentDetails = new Gson().fromJson(response.string(), AgentDetails.class);
                    hideCustomDialog();
                    if(agentDetails!=null){
                        if(agentDetails.StatusCode.equalsIgnoreCase("0")) {
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("AgentDetail", agentDetails);
                            bundle.putSerializable("AgentDoneCard", loginModel.Data.DoneCardUser);
                            BalanceCheckFragment balanceCheckFragment=new BalanceCheckFragment();
                            balanceCheckFragment.setArguments(bundle);
                            ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(balanceCheckFragment);
                        }else {
                            Toast.makeText(context, agentDetails.Status, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
            if(TYPE!=APP_SESSION) {
                Toast.makeText(context, R.string.exception_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CheckResponseClass{
        public String statusCode, statusMessage;
        public ArrayList<credentialData> credentialData;
        public class credentialData{
            public String sessionKey, sessionRefNo, token, userData;
        }
    }

    private void loginAeps(CheckResponseClass responseModel) {
        // authentication token
        MyPreferences.saveAepsToken(responseModel.credentialData.get(0).token,context);
        MyPreferences.saveUserData(responseModel.credentialData.get(0).userData,context);
        MyPreferences.saveSessionKey(responseModel.credentialData.get(0).sessionKey,context);
        MyPreferences.saveSessionRefNo(responseModel.credentialData.get(0).sessionRefNo,context);

        startActivity(new Intent(context, Dashboard_New_Activity.class));
    }

    public void userLogin(boolean isDialog) {
        //first getting the values
        final String username = loginModel.Data.Email;
        final String password = MyPreferences.getLoginPassword(context);
        final String deviceId = Common.getDeviceId(context);

//        final String username = "ranjana@justclicknpay.in";
//        final String password = "123456";

        if(isDialog){
            showCustomDialog();}
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.AGENT_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            hideCustomDialog();
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            if (status.equals("true")) {
                                loginAeps(obj);
//                                alertBox(obj);
                            }else if(status.equals("3")){
                                alertBox(obj);
                            }else{
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error.toString();  for trace error help
                        error.printStackTrace();
                        hideCustomDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.aeps_response_failure), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("deviceId", deviceId);
                params.put("loginMode", "Mob");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
//        https://stackoverflow.com/questions/45984295/android-pre-lollipop-devices-giving-error-ssl-handshake-aborted-ssl-0x618d9c18/46025698
    }

    private void loginAeps(JSONObject obj) throws Exception {
        String str_token = obj.getString("token");
        decoded(str_token);
        // authentication token
        MyPreferences.saveToken(str_token,context);
//        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_AUTH_TOKEN, str_token);
//        editor.apply();
        startActivity(new Intent(context, Dashboard_Old_Activity.class));
    }

    private void alertBox(final JSONObject obj) {
        String message="You have a session active on another device. " +
                "Do you want to close your active session ?";

        appUpdateDialog = new AlertDialog.Builder(this)
                .setTitle("Session Alert")
                .setMessage(message)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkIfTransactionIsRunning(obj);
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appUpdateDialog.dismiss();
                            }
                        }).create();
        appUpdateDialog.setCancelable(false);
        appUpdateDialog.show();
//        Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
    }

    public class JctUserIdRequestClass{
        public String jctUserId;
    }
    public class JctUserIdResponseClass{
        public String status, message;
    }
    private void checkIfTransactionIsRunning(final JSONObject obj) {
        JctUserIdRequestClass requestModel=new JctUserIdRequestClass();
        requestModel.jctUserId=loginModel.Data.DoneCardUser;
        showCustomDialog();
        new NetworkCall().callAepsService(requestModel,URLs.checktxncompleted, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(appUpdateDialog!=null && appUpdateDialog.isShowing()){
                            appUpdateDialog.dismiss();
                        }
                        if(response!=null){
                            try {  //{"status":"1","message":"Transaction is completed."}
                                JctUserIdResponseClass commonResponseModel = new Gson().fromJson(response.string(), JctUserIdResponseClass.class);
                                if(commonResponseModel!=null && commonResponseModel.status.equalsIgnoreCase("0")) {
                                    hideCustomDialog();
                                    transactionRunningAlert(obj);
                                }else {
                                    logoutSession(obj);
//                                    Toast.makeText(context, commonResponseModel.message, Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                hideCustomDialog();
                                Toast.makeText(context, "Session error", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, "Session error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void transactionRunningAlert(final JSONObject obj) {
        String message="Your Transaction is in process do you still want to close session ?";

        transAlertDialog = new AlertDialog.Builder(this)
                .setTitle("Session Alert")
                .setMessage(message)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logoutSession(obj);
                                transAlertDialog.dismiss();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transAlertDialog.dismiss();
                            }
                        }).create();
        transAlertDialog.setCancelable(false);
        transAlertDialog.show();
    }

    private void logoutSession(final JSONObject obj) {
        CommonRequestModel commonRequestModel=new CommonRequestModel();
        commonRequestModel.DeviceId=Common.getDeviceId(context);
        commonRequestModel.DoneCardUser=loginModel.Data.DoneCardUser;
        commonRequestModel.LoginSessionId=EncryptionDecryptionClass.EncryptSessionId(
                EncryptionDecryptionClass.Decryption(loginModel.LoginSessionId, context), context);
        showCustomDialog();
        new NetworkCall().callMobileService(commonRequestModel,URLs.AEPSSessionclose, context,
                new NetworkCall.RetrofitResponseListener() {
                    @Override
                    public void onRetrofitResponse(ResponseBody response, int responseCode) {
                        if(response!=null){
                            try {
                                CommonResponseModel commonResponseModel = new Gson().fromJson(response.string(), CommonResponseModel.class);
                                if(commonResponseModel!=null && commonResponseModel.StatusCode.equalsIgnoreCase("1")) {
//                                   loginAeps(obj);
                                    userLogin(false);
                                }else {
                                    hideCustomDialog();
                                    Toast.makeText(context, commonResponseModel.Status, Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                hideCustomDialog();
                                Toast.makeText(context, "Session error", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            hideCustomDialog();
                            Toast.makeText(context, "Session error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            String strHeader = getJson(split[0]);
            JSONObject jsonHeader = new JSONObject(strHeader);
            String strBody = getJson(split[1]);
            JSONObject jsonBody = new JSONObject(strBody);

            UserInfo user = new UserInfo(jsonBody.getString("id"), jsonBody.getString("type"), jsonBody.getString("name")
                    , jsonBody.getString("iat"), jsonBody.getString("exp"), jsonBody.getString("iss"));
            //storing the user in shared preferences
            SessionManager.getInstance(context).userLogin(user);

            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    @Override
    public void onCitySearch(FlightCityModel.response data, int myStr) {
        FlightSearch articleFrag = (FlightSearch)
                getSupportFragmentManager().findFragmentByTag(FlightSearch.FlightSearchTag);

        if (articleFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            articleFrag.onCitySearch(data, myStr);
        }
    }


    private Boolean exit = false;
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((getSupportFragmentManager().findFragmentById(R.id.container)
                    instanceof HomeFragment)) {
                if (exit) {
                    finish(); // finish activity
                } else {
                    Toast.makeText(this, "Press Back again to Exit.",
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3 * 1000);

                }
            } else if (((getSupportFragmentManager().findFragmentById(R.id.container)
                    instanceof AdminDepositReportFragment ||
                    getSupportFragmentManager().findFragmentById(R.id.container)
                            instanceof AdminCreditReportFragment) &&
                    (agencyListOfFragment.getVisibility() == View.VISIBLE))) {
                agencyListOfFragment.setVisibility(View.GONE);
            } else {
                getSupportFragmentManager().popBackStack();
            }

        }
    }

    public void customBackPress() {
        /*if(getSupportFragmentManager().findFragmentById(R.id.container)
                instanceof FlightBookingConfirmationFragment ||
                getSupportFragmentManager().findFragmentById(R.id.container)
                        instanceof FlightDetails ||
                getSupportFragmentManager().findFragmentById(R.id.container)
                        instanceof TravellersDetails||
                getSupportFragmentManager().findFragmentById(R.id.container)
                        instanceof FareRulesFragment){
            while(!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof FlightSearch)){
                getSupportFragmentManager().popBackStackImmediate();
            }
        }else {
            super.onBackPressed();
        }*/
    }

    @Override
    public void onFragmentBackPress(ListView listView) {
        agencyListOfFragment=listView;
    }

    @Override
    public void onJctDetailBackPress(CodeEnum classType) {
        try{
            if(classType.equals(CodeEnum.Rapipay)){
                ((RapipaySenderDetailFragment)getSupportFragmentManager().findFragmentByTag(FragmentTags.jctMoneySenderDetailFragment)).
                        getSenderDetail(0);
            }else if(classType.equals(CodeEnum.PayoutNew)){
                ((PayoutBeneFragment)getSupportFragmentManager().findFragmentByTag(FragmentTags.payoutSenderDetailFragment)).
                        getBankDetails(false);
            }else if(classType.equals(CodeEnum.DMT2)){
                ((Dmt2SenderDetailFragment)getSupportFragmentManager().findFragmentByTag(FragmentTags.dmt2SenderDetailFragment)).
                        getSenderDetail(0);
            }else {
                ((SenderDetailFragment)getSupportFragmentManager().findFragmentByTag(FragmentTags.jctMoneySenderDetailFragment)).
                        getSenderDetail(0);
            }

        }catch (NullPointerException e){

        }
    }

    @Override
    public void onBusBackPress(boolean isHome) {
        if(isHome){
            while(!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof HomeFragment)){
                getFragmentManager().popBackStack();
            }
        }else {
//            while(!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof BusListFragment)){
//                getFragmentManager().popBackStack();
//            }
        }
    }

    @Override
    public void onToolBarHideFromFragment(Boolean isHide) {
        if(isHide) {
            toolbar.setVisibility(View.GONE);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else {
            toolbar.setVisibility(View.VISIBLE);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(Common.checkInternetConnection(context))
//            appSession();
        if (MyPreferences.getAppCurrentTime(context) > System.currentTimeMillis()) {
//                    logged in
        } else {
//                    logged out
            MyPreferences.logoutUserRemember(context);
            startActivity(new Intent(context, MyLoginActivityNew.class));
            finish();
        }
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//        Toast.makeText(context, "Activity OnResume", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(context, "Activity OnStart", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyPreferences.setAppCurrentTime(context);
//        Toast.makeText(context, "Activity OnPause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(context, "Activity OnStop", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyCustomDialog.hideCustomDialog();
//        Toast.makeText(context, "Activity OnDestroy", Toast.LENGTH_LONG).show();
    }
}