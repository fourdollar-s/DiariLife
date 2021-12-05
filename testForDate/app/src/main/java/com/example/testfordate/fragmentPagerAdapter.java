package com.example.testfordate;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class fragmentPagerAdapter extends FragmentPagerAdapter {

    //    private final int PAGER_COUNT = 4;
//    private MyFragment1 myFragment1 = null;
//    private MyFragment2 myFragment2 = null;
//    private MyFragment3 myFragment3 = null;
//    private MyFragment4 myFragment4 = null;
    private List<Fragment> mList = new ArrayList<>();


    public fragmentPagerAdapter(FragmentManager fm, List<Fragment> mList) {
        super(fm);
//        myFragment1 = new MyFragment1();
//        myFragment2 = new MyFragment2();
//        myFragment3 = new MyFragment3();
//        myFragment4 = new MyFragment4();
        this.mList = mList;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {

        return mList.get(position);
    }

}

