package com.stellar.xueyingbai.travelandentertainmentsearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
//    int tab_number;
//
//    public PagerAdapter(FragmentManager fm, int tab_number) {
//        super(fm);
//        this.tab_number = tab_number;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                Info info = new Info();
//                return info;
//            case 1:
//                Photos photos = new Photos();
//                return photos;
//            case 2:
//                Map map = new Map();
//                return map;
//            case 3:
//                Reviews reviews = new Reviews();
//                return reviews;
//            default:
//                return null;
//        }
//
//    }
//
//    @Override
//    public int getCount() {
//        return tab_number;
//    }
    int tab_number;
    private List<Fragment> mFragments = new ArrayList<>();

    public PagerAdapter(FragmentManager fm, int tab_number) {
        super(fm);
        this.tab_number = tab_number;
        if (mFragments.size() < 4) {
            mFragments.add(new Info());
            mFragments.add(new Photos());
            mFragments.add(new Map());
            mFragments.add(new Reviews());
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
