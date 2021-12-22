package com.justclick.clicknbook.Fragment.billpay;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;

/**
 * A simple {@link Fragment} subclass.

 */
public class BillPayMainPagerFragment extends Fragment implements TabLayout.OnTabSelectedListener{

    private static final int NUM_PAGES = 6;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ViewPager viewPager;
    //This is our tab layout
    private TabLayout tabLayout;
    private PagerAdapter mPagerAdapter;
    private Fragment mobileFragment;
    private String[] tabTitle = new  String[]{BillPayFragment.categoryPostpaid,BillPayFragment.categoryWater,
            BillPayFragment.categoryDatacardPrepaid, BillPayFragment.categoryElectricity,
            BillPayFragment.categoryLandline,BillPayFragment.categoryGas};

    public BillPayMainPagerFragment()
    {
        // Required empty public constructor
    }

    public static BillPayMainPagerFragment newInstance(Context context) {

        Bundle args = new Bundle();
        BillPayMainPagerFragment fragment = new BillPayMainPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        try {
            titleChangeListener= (ToolBarTitleChangeListener) context;
        }catch (ClassCastException e){

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mobileFragment=new RechargeFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_main_billpay, container, false);
//        titleChangeListener.onToolBarTitleChange(getString(R.string.mobileFragmentTitle));
        titleChangeListener.onToolBarTitleChange(getString(R.string.rechargeFragmentTitle));
        initializeViews(view);

//        Initializing the tablayout
        tabLayout =  view.findViewById(R.id.tabLayout);
//
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryPostpaid));
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryWater));
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryDatacardPrepaid));
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryElectricity));
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryLandline));
        tabLayout.addTab(tabLayout.newTab().setText(BillPayFragment.categoryGas));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        //Adding onTabSelectedListener to swipe views
//        tabLayout.addOnTabSelectedListener(this);
//         Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(0);
        mPagerAdapter.notifyDataSetChanged();

        view.findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initializeViews(View view) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            return BillPayFragment.getInstance(context,position+1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
      //  getFragmentManager().popBackStack();
//        Toast.makeText(context,"Resume",Toast.LENGTH_SHORT).show();
    }

    }
