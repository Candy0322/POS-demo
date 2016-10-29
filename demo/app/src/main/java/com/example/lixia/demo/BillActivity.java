package com.example.lixia.demo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BillActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        mTabLayout = (TabLayout) findViewById(R.id.bill_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.bill_viewpager);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(BillFragment.newInstance(0));
        fragmentList.add(BillFragment.newInstance(1));
        FragmentPagerAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragmentList.size());
        mTabLayout.setupWithViewPager(mViewPager);
    }


    class FragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> mFragmentList;

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList != null ? mFragmentList.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position <= 0 ? "银行卡账单" : "扫码账单";
        }
    }
}
