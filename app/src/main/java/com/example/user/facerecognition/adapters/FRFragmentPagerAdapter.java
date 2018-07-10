package com.example.user.facerecognition.adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by User on 2018/7/3.
 */

public class FRFragmentPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> data;

    public FRFragmentPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
