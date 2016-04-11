package com.example.yassine.sunlamp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.yassine.sunlamp.MyFavoriteFragment;
import com.example.yassine.sunlamp.MyListFragment;
import com.example.yassine.sunlamp.MySearchFragment;

/**
 * Created by YassIne on 26/08/2015.
 */
public class TabbedAdapter extends FragmentPagerAdapter {

    public TabbedAdapter (FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int selected){
        switch (selected){
            case 0:
                return new MyFavoriteFragment();
            case 1:
                return new MyListFragment();
        }
        return null;
    }

    public int getCount(){
        //numero dei tabs
        return 2;
    }
}
