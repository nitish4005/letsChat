package com.example.android.letschat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Prasad on 29-Dec-17.
 */

class CategoryAdapter extends FragmentPagerAdapter{
    private String tabTitles[] = new String[] { "Chat", "Status","Contacts" };
    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new Chat();
        } else if (position == 1){
            return new Status();
        } else if (position ==2){
            return new Contacts();
        } else {
            return null;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
