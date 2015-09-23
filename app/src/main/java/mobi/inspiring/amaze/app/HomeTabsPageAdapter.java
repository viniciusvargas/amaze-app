package mobi.inspiring.amaze.app;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeTabsPageAdapter extends FragmentPagerAdapter{

    public HomeTabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
        public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new NearbyBeaconsFragment();
            case 1:
                return new BookmarkedBeaconsFragment();
            default:
                return new NearbyBeaconsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }




}
