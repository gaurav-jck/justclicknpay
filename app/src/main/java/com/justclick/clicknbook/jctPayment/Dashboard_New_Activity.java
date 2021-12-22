package com.justclick.clicknbook.jctPayment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.tabs.TabLayout;
import com.justclick.clicknbook.Activity.MyLoginActivity;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Fragments.Services_Fragment;
import com.justclick.clicknbook.jctPayment.Fragments.TransactionListFragment;
import com.justclick.clicknbook.jctPayment.Fragments.Transactions_Fragment;
import com.justclick.clicknbook.jctPayment.Utilities.SessionManager;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.newaeps.Services_Fragment_New;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Dashboard_New_Activity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard__new_);

        context=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TransactionListFragment(), "Transactions");
        adapter.addFragment(new Services_Fragment_New(), "Services");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard__new_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, ChangePassword.class);
            startActivity(intent);
            return true;
        }*/
        if (id == R.id.action_download_excel){
            downloadLink();
        }
        if (id == R.id.action_logout){
            SessionManager.getInstance(context).logout();
            Intent intent = new Intent(getApplicationContext(), MyLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadLink() {
        DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = new DownloadManager.Request(
                Uri.parse(URLs.AEPS_EXCEL_DOWNLOAD_URL));
        mRqRequest.setDescription("Registry of AEPS transaction excel File");
        mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        mRqRequest.setShowRunningNotification(true);
//        mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//  mRqRequest.setDestinationUri(Uri.parse("give your local path"));
        long idDownLoad=mManager.enqueue(mRqRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
