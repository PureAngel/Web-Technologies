package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Search_Fav_pagerAdapter extends FragmentPagerAdapter {
    int tab_number;
    private List<Fragment> mFragments = new ArrayList<>();

    public Search_Fav_pagerAdapter(FragmentManager fm, int tab_number) {
        super(fm);
        this.tab_number = tab_number;
        if (mFragments.size() < 2) {
            mFragments.add(new Search());
            mFragments.add(new Favorites());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
