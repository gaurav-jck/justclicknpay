package com.justclick.clicknbook.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.justclick.clicknbook.Activity.AirWebviewActivity;
import com.justclick.clicknbook.Activity.NavigationDrawerActivity;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentDepositRequestFragmentNewww;
import com.justclick.clicknbook.Fragment.accountsAndReports.airbookinglist.AirBookingListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt.AccountStatementListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AdminCreditReportFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AdminDepositReportFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentCreditListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentCreditRequestFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentDepositListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AgentSearchFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirCancellationListFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirRefundReportFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.AirSalesReportFragment;
import com.justclick.clicknbook.Fragment.accountsAndReports.TrainFailedListFragment;
import com.justclick.clicknbook.Fragment.billpay.BillPayMainPagerFragment;
import com.justclick.clicknbook.Fragment.bus.BusTransactionListFragment;
import com.justclick.clicknbook.Fragment.cashfreeQR.CashFreeQRCodeFragment;
import com.justclick.clicknbook.Fragment.cashfreeQR.QRCodeFragment;
import com.justclick.clicknbook.Fragment.cashout.GetSenderFragment;
import com.justclick.clicknbook.Fragment.cashoutnew.PayoutBeneFragment;
import com.justclick.clicknbook.Fragment.creditcard.CreditCardFragment;
import com.justclick.clicknbook.Fragment.fasttag.FasttagFragment;
import com.justclick.clicknbook.Fragment.hotel.HotelSearchFragment;
import com.justclick.clicknbook.Fragment.jctmoney.CashoutTransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.JctMoneyGetSenderFragment;
import com.justclick.clicknbook.Fragment.jctmoney.RapipayTransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.TransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.UtilityTransactionListFragment;
import com.justclick.clicknbook.Fragment.jctmoney.dmt2.Dmt2GetSenderFragment;
import com.justclick.clicknbook.Fragment.lic.LicFragment;
import com.justclick.clicknbook.Fragment.paytmwallet.PaytmWalletFragmentNew;
import com.justclick.clicknbook.Fragment.recharge.RechargeListFragment;
import com.justclick.clicknbook.Fragment.recharge.RechargeMainPagerFragment;
import com.justclick.clicknbook.Fragment.salesReport.AgentVerificationFragment;
import com.justclick.clicknbook.Fragment.salesReport.NetSalesReportFragment;
import com.justclick.clicknbook.Fragment.salesReport.NetSalesReportFragmentNew;
import com.justclick.clicknbook.Fragment.salesReport.SalesAccountListFragment;
import com.justclick.clicknbook.Fragment.train.TrainBookingListFragment;
import com.justclick.clicknbook.Fragment.train.TrainBookingListNewFragment;
import com.justclick.clicknbook.Fragment.train.TrainDashboardFragment;
import com.justclick.clicknbook.Fragment.train.TrainSearchFragment;
import com.justclick.clicknbook.FragmentTags;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.adapter.MenuItemsAdapter;
import com.justclick.clicknbook.credopay.CredoPayActivityJava;
import com.justclick.clicknbook.databinding.FragmentHomeBinding;
import com.justclick.clicknbook.graphhome.GraphFragment;
import com.justclick.clicknbook.jctPayment.Dashboard_New_Activity;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;
import com.justclick.clicknbook.paysprintMatm.MainMatmFragment;
import com.justclick.clicknbook.rapipayMatm.MatmTransactionListFragment;
import com.justclick.clicknbook.utils.Constants;
import com.justclick.clicknbook.utils.MenuCodes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
//    private RecyclerView recyclerView;
    private ToolBarTitleChangeListener titleChangeListener;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    private MenuItemsAdapter menuItemsAdapter;
    private Context context;
    private LinearLayoutManager layoutManager;
//    private ViewFlipper viewFlipper;
//    private ImageView image1, image2, image3, image4, image5;
    private FragmentHomeBinding binding;
    private View mView;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        Toast.make;Text(context, "Fragment OnAttach", Toast.LENGTH_LONG).show();
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
            toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
//        Toast.makeText(context, "Fragment OnCreate", Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView==null){
            mView= inflater.inflate(R.layout.fragment_home, container, false);
            binding=FragmentHomeBinding.bind(mView);
            //        viewFlipper=  view.findViewById(R.id.viewFlipper);
//        image1=  view.findViewById(R.id.image1);
//        image2=  view.findViewById(R.id.image2);
//        image3=  view.findViewById(R.id.image3);
//        image4=  view.findViewById(R.id.image4);
//        image5=  view.findViewById(R.id.image5);

//        Picasso.with(context).load("https://aclient.justclicknpay.com/jct/Image/appslider1.jpg").placeholder(R.drawable.hotel_demo_image).into(binding.image1);
//        Picasso.with(context).load("https://aclient.justclicknpay.com/jct/Image/appslider2.jpg").placeholder(R.drawable.hotel_demo_image).into(binding.image2);
//        Picasso.with(context).load("https://aclient.justclicknpay.com/jct/Image/appslider3.jpg").placeholder(R.drawable.hotel_demo_image).into(binding.image3);
//        Picasso.with(context).load("https://aclient.justclicknpay.com/jct/Image/appslider4.jpg").placeholder(R.drawable.hotel_demo_image).into(binding.image4);
//        Picasso.with(context).load("https://aclient.justclicknpay.com/jct/Image/appslider5.jpg").placeholder(R.drawable.hotel_demo_image).into(binding.image5);

//        binding.viewFlipper.setAutoStart(true);
//        binding.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
//        binding.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out));
//        binding.viewFlipper.setFlipInterval(5000);
//        binding.viewFlipper.startFlipping();


//        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        Toast.makeText(context, "Fragment OnCreateView", Toast.LENGTH_LONG).show();

            ArrayList<LoginModel.DataList> list= null;
            try {
                list = ((NavigationDrawerActivity)context).getHomeScreenProductMenus();
            } catch (Exception e) {
                e.printStackTrace();
                list=new ArrayList<>();
            }

            menuItemsAdapter=new MenuItemsAdapter(context, new MenuItemsAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<LoginModel.DataList> list, int position) {

                }

            },list,this);
            layoutManager=new LinearLayoutManager(context);
            binding.recyclerView.setLayoutManager(layoutManager);
            binding.recyclerView.setAdapter(menuItemsAdapter);

            binding.graphLin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NavigationDrawerActivity)context).
                            replaceFragmentWithBackStack(new GraphFragment());
                }
            });
//        String t=FirebaseInstanceId.getInstance().getToken();
        }
        return mView;
    }

    public void sendMenuCode(String subMenuCode) {
        switch (subMenuCode) {
            case MenuCodes.UpdateCredit:      //1
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AgentSearchFragment());
                break;
            case MenuCodes.DepositUpdateRequest://2
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AdminDepositReportFragment());
                break;
            case MenuCodes.CreditUpdateRequest://3
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AdminCreditReportFragment());
                break;
            case MenuCodes.DepositList://4
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AgentDepositListFragment());
                break;
            case MenuCodes.CreditList://5
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AgentCreditListFragment());
                break;
            case MenuCodes.AgentCreditRequestFragment://6
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AgentCreditRequestFragment());
                break;
            case MenuCodes.AgentDepositRequestFragment://7
//                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(new AgentDepositRequestFragmentNeww());
                ((NavigationDrawerActivity)context).replaceFragmentWithBackStack(new AgentDepositRequestFragmentNewww());
                break;
            case MenuCodes.MobileFragment://8
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new RechargeMainPagerFragment());
                break;
            case MenuCodes.DMT://9
//                ((NavigationDrawerActivity)context).checkMapping();
//                startActivity(new Intent(context, MoneyTransferActivity.class));
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new JctMoneyGetSenderFragment());
                break;
            case MenuCodes.DMT2://9
//                ((NavigationDrawerActivity)context).checkMapping();
//                startActivity(new Intent(context, MoneyTransferActivity.class));
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new Dmt2GetSenderFragment());
                break;
            case MenuCodes.TrainBookingcheck://10
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new TrainBookingCheckFragment());
                break;
            case MenuCodes.TrainFailedList://11
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new TrainFailedListFragment());
                break;
            case MenuCodes.FlightSearch://12
//                ((NavigationDrawerActivity) context).replaceFragmentWithTag(FlightSearch.newInstance(), FlightSearch.FlightSearchTag);
                AirWebviewActivity.airSession(context);
//                startActivity(new Intent(context, AirWebviewActivity.class));
                break;
            case MenuCodes.BusSearch://13
//                ((NavigationDrawerActivity)context).
//                        replaceFragmentWithBackStack(new BusSearchFragment());
                AirWebviewActivity.airSession(context);
                break;
            case MenuCodes.HotelSearch://14
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new HotelSearchFragment());
                break;
            case MenuCodes.AirSalesReport://15
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AirSalesReportFragment());
                break;
            case MenuCodes.AirRefundReport://16
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AirRefundReportFragment());
                break;
            case MenuCodes.AirCancellationReport://17
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AirCancellationListFragment());
                break;
            case MenuCodes.AEPS://18
                startActivity(new Intent(context, Dashboard_New_Activity.class));
//                ((NavigationDrawerActivity)context).userLogin(true);
//                ((NavigationDrawerActivity)context).checkAepsCredential();
                break;
            case MenuCodes.AEPS_OLD://18
//                startActivity(new Intent(context, Dashboard_New_Activity.class));
                ((NavigationDrawerActivity)context).userLogin(true);
//                ((NavigationDrawerActivity)context).checkAepsCredential();
                break;
            case MenuCodes.BusBookingList://19
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new BusTransactionListFragment());
                break;
            case MenuCodes.JCTMoneyList://20
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new RapipayTransactionListFragment());
                break;
            case MenuCodes.PayoutList://20
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new CashoutTransactionListFragment());
                break;
            case MenuCodes.UtilityList://20
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new UtilityTransactionListFragment());
                break;
            case MenuCodes.CreditCardList://20
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new TransactionListFragment());
                break;
            case MenuCodes.AirBookingList:
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new AirBookingListFragment());
                break;
            case MenuCodes.TopUpDetails://21
                ((NavigationDrawerActivity)context).
                        replaceFragmentWithBackStack(new RechargeListFragment());
                break;
            case MenuCodes.NetSalesReport://22
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new NetSalesReportFragment());
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new NetSalesReportFragmentNew());
                break;
            case MenuCodes.SalesAccountStatement://23
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new SalesAccountListFragment());
                break;
            case MenuCodes.AccountStatement://23
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new AccountStatementListFragment());
                break;
            case MenuCodes.ApproveAgent://24
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(AgentVerificationFragment.newInstance());
                break;
            case MenuCodes.MATM://24
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new MainMatmFragment());
//                startActivity(new Intent(context, CredoPayActivityJava.class));
                break;
            case MenuCodes.CREDOPAY://24
                startActivity(new Intent(context, CredoPayActivityJava.class));
                break;
            case MenuCodes.FAST_TAG://24
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new FasttagFragment());
                break;
            case MenuCodes.TRAIN://25
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainDashboardFragment());  // old
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainSearchFragment());    //new change
                break;
            case MenuCodes.TRAIN_TENT://25
                openTrainTent();
                break;
            case MenuCodes.CASH_OUT://26
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new GetSenderFragment());
//                ((NavigationDrawerActivity) context).replaceFragmentWithTag(new PayoutBeneFragment(), FragmentTags.payoutSenderDetailFragment);
                break;
            case MenuCodes.LIC://27
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new LicFragment());
                break;
            case MenuCodes.BILL_PAY://28
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new BillPayMainPagerFragment());
                break;
            case MenuCodes.PAYTM://29
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new PaytmWalletFragmentNew());
                break;
            case MenuCodes.CASHFREE_QR://30
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CashFreeQRCodeFragment());
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new QRCodeFragment());
                break;
            case MenuCodes.DYNAMIC_QR://31
//                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CashFreeQRCodeFragment());
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new QRCodeFragment());
                break;
            case MenuCodes.TrainBookingList://32
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new TrainBookingListNewFragment());
                break;
            case MenuCodes.AepsList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new com.justclick.clicknbook.jctPayment.Fragments.TransactionListFragment());
                break;
            case MenuCodes.MatmList:
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new MatmTransactionListFragment());
                break;
            case MenuCodes.CREDIT://33
                ((NavigationDrawerActivity) context).replaceFragmentWithBackStack(new CreditCardFragment());
                break;
            default:
                Toast.makeText(context,"Not working", Toast.LENGTH_SHORT).show();
//                ((NavigationDrawerActivity)context).
//                        replaceFragmentWithBackStack(new HomeFragment());
        }
    }

    private void openTrainTent() {
//        Toast.makeText(context, "Open url", Toast.LENGTH_SHORT).show();
        String url = Constants.trainTentUrl;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleChangeListener.onToolBarTitleChange(getString(R.string.nav_home));
        toolBarHideFromFragmentListener.onToolBarHideFromFragment(false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(context, "Fragment OnResume", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Toast.makeText(context, "Fragment OnDestroyView", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(context, "Fragment OnStart", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText(context, "Fragment OnPause", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(context, "Fragment OnStop", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(context, "Fragment OnDestroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        Toast.makeText(context, "Fragment OnDetach", Toast.LENGTH_LONG).show();
    }
}