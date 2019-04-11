package com.nicolappli.go4lunch.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.nicolappli.go4lunch.Controllers.Fragments.ListViewFragment;
import com.nicolappli.go4lunch.Controllers.Fragments.MapViewFragment;
import com.nicolappli.go4lunch.Controllers.Fragments.WorkmatesFragment;

public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(FragmentManager mgr){
        super(mgr);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return MapViewFragment.newInstance();
            case 1:
                return ListViewFragment.newInstance();
            case 2:
                return WorkmatesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return(3);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Map View";
            case 1:
                return "List View";
            case 2:
                return "Workmates";
            default:
                return null;
        }
    }
}
