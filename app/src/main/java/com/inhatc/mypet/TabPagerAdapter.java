package com.inhatc.mypet;

/**
 * Created by user on 2016-12-26.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                pet tabFragment1 = new pet();
                return tabFragment1;
            case 1:
                diary tabFragment2 = new diary();
                return tabFragment2;
            case 2:
                board tabFragment3 = new board();
                return tabFragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}