package com.techbytecare.kk.livewallpaper.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.techbytecare.kk.livewallpaper.Fragment.CategoryFragment;
import com.techbytecare.kk.livewallpaper.Fragment.DailyPopularFragment;
import com.techbytecare.kk.livewallpaper.Fragment.RecentFragment;

/**
 * Created by kundan on 2/5/2018.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private Context context;

    public MyFragmentAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return CategoryFragment.getInstance();
        }
        else if (position == 1)   {
            return RecentFragment.getInstance(context);
        }
        else if (position == 2)   {
            return DailyPopularFragment.getInstance();
        }
        else     {
            return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)   {
            case 0 :
                return "Category";

            case 1 :
                return "Recent";

            case 2 :
                return "Trending";
        }
        return "";
    }
}
