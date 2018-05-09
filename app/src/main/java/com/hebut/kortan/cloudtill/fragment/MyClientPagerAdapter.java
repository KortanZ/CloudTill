package com.hebut.kortan.cloudtill.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyClientPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;
    public MyClientPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new RealTimeData();
        } else if (position == 1) {
            return new FarmManage();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
