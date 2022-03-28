package com.justclick.clicknbook.Fragment.recharge;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.myinterface.ToolBarTitleChangeListener;

/**
 * A simple {@link Fragment} subclass.

 */
public class RechargeMainPagerFragment extends Fragment implements TabLayout.OnTabSelectedListener{

//    private static final int NUM_PAGES = 6;
    private static final int NUM_PAGES = 2;
    private Context context;
    private ToolBarTitleChangeListener titleChangeListener;
    private ViewPager viewPager;
    //This is our tab layout
    private TabLayout tabLayout;
    private PagerAdapter mPagerAdapter;
    private Fragment mobileFragment;
    private String[] tabTitle = new  String[]{"Prepaid Mobile","Prepaid DTH"/*,"FastTag"*/};

    public RechargeMainPagerFragment()
    {
        // Required empty public constructor
    }

    public static RechargeMainPagerFragment newInstance(Context context) {

        Bundle args = new Bundle();
        RechargeMainPagerFragment fragment = new RechargeMainPagerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_mobile_new, container, false);
//        titleChangeListener.onToolBarTitleChange(getString(R.string.mobileFragmentTitle));
        titleChangeListener.onToolBarTitleChange(getString(R.string.rechargeFragmentTitle));
        initializeViews(view);

//        Initializing the tablayout
        tabLayout =  view.findViewById(R.id.tabLayout);
//
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Prepaid Mobile"));
        tabLayout.addTab(tabLayout.newTab().setText("Prepaid DTH"));
//        tabLayout.addTab(tabLayout.newTab().setText("FastTag"));
//        tabLayout.addTab(tabLayout.newTab().setText("Electricity"));
//        tabLayout.addTab(tabLayout.newTab().setText("Landline"));
//        tabLayout.addTab(tabLayout.newTab().setText("gas"));
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
            return RechargeFragment.newInstance(context,position+1);
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
