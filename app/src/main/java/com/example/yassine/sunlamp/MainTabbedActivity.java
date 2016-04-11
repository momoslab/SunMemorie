package com.example.yassine.sunlamp;

import java.util.ArrayList;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yassine.sunlamp.Adapter.TabbedAdapter;
import com.example.yassine.sunlamp.Model.ColorData;

public class MainTabbedActivity extends AppCompatActivity implements OnElementSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__tabbed_);

        //---------------------------------------------------------
        // ACTION BAR SETUP
        //---------------------------------------------------------
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);

        mSectionsPagerAdapter.addFragement(new MyListFragment(), "My List");
        mSectionsPagerAdapter.addFragement(new MyFavoriteFragment(), "My Favorite");

        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main__tabbed_, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                openSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        ArrayList<String> fragmentNameList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragement(Fragment frame, String name){
            fragmentList.add(frame);
            fragmentNameList.add(name);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
           return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
           return fragmentNameList.get(position);
        }

        public Drawable getPageIcon(int position){
            switch(position){
                case 0:
                    return getResources().getDrawable(R.drawable.ic_home_white_24dp);
                case 1:
                    return getResources().getDrawable(R.drawable.ic_favorite_white_24dp);
            }

            return null;
        }

        public Drawable getOnSelectedPageIcon(int position){
            switch(position){
                case 0:
                    return getResources().getDrawable(R.drawable.ic_home_yellow_24dp);
                case 1:
                    return getResources().getDrawable(R.drawable.ic_favorite_green_24dp);
            }

            return null;
        }

    }

    public void onElementSelected(ColorData elementSelected){
            Intent intent = new Intent(this, ViewSingleItemActivity.class);
            intent.putExtra("colorItemSelected", elementSelected);
            this.startActivity(intent);
    }

}
