package com.info.aegis.lawpush4android.presenter.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */

public class MViewPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<View> mViewList;
    private ViewPager mViewPager;

    public MViewPageAdapter(List<View> views,ViewPager mViewPager) {
        mViewList = views;
        this.mViewPager=mViewPager;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position), 0);
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        if (position == 0) {
//            mViewPager.setCurrentItem(1);
//        } else if (position == mViewList.size() - 1) {
//            mViewPager.setCurrentItem(position - 1);
//            Toast.makeText(getApplicationContext(), "页面即将跳转",
//                    Toast.LENGTH_SHORT).show();
//            //应该在这里跳转到MainActivity
//            // startActivity(intent);
//        }
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
