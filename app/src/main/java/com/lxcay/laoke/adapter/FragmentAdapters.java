package com.lxcay.laoke.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lxcay.laoke.fragment.ContactsFragment;
import com.lxcay.laoke.fragment.MessageFragment;
import com.lxcay.laoke.fragment.SettingFragment;


/**
 * Created by lxcay on 2015/7/23.
 */

public class FragmentAdapters extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 3;

    public FragmentAdapters(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position) {
            case 0:
                f = new MessageFragment();
                break;
            case 1:
                f = new ContactsFragment();
                break;
            case 2:
                f = new SettingFragment();
                break;

            default:
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

}
