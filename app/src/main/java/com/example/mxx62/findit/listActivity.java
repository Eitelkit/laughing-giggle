package com.example.mxx62.findit;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.Arrays;

public class listActivity extends AppCompatActivity implements FavoriteList.OnFragmentInteractionListener, BlackList.OnFragmentInteractionListener {

    private String [] tabname = new String[]{"Favorite", "Dislike"};
    private final Fragment[] mFragments = new Fragment[]{
            new FavoriteList(),
            new BlackList()

    };
    private ViewPager listVp;
    private listVpAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listAdapter = new listVpAdapter(getSupportFragmentManager());
        listVp = (ViewPager)findViewById(R.id.listViewPager);
        listVp.setAdapter(listAdapter);
        listVp.setCurrentItem(0);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayoutlist);
        tabLayout.setupWithViewPager(listVp);




    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class listVpAdapter extends FragmentPagerAdapter {


        public listVpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return tabname.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabname[position];
        }
    }
}
