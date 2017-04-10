package com.info.aegis.lawpush4android.view.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.GuideActivity;

/**
 * Created by SFS on 2017/2/26.
 */
public class CycleViewPager extends ViewPager {
    private Context context;

    public boolean isScroll=true;

    public CycleViewPager(Context context) {
        this(context,null);
    }

    public CycleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        innerOnPageChangeListener onPageChangeListener=new innerOnPageChangeListener(listener);
        super.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        innerPagerAdapter innerPagerAdapter = new innerPagerAdapter(adapter);
        super.setAdapter(innerPagerAdapter);
         addOnPageChangeListener(null);
        //设置默认显示页面
        setCurrentItem(1);
        //开始轮播
        startScroll();

    }

    /**
     * 内部监听
     */
    class innerOnPageChangeListener implements OnPageChangeListener {

        private int position;
        private OnPageChangeListener listener;

        public innerOnPageChangeListener(OnPageChangeListener listener) {

            this.listener = listener;
        }

        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (listener != null) {
                listener.onPageScrolled(position,positionOffset,positionOffsetPixels);
            }
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {

            this.position = position;

            if (listener != null) {
                listener.onPageSelected(position);
            }



        }

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         */
        @Override
        public void onPageScrollStateChanged(int state) {

            //当前页面状态为空闲
            if (state== ViewPager.SCROLL_STATE_IDLE) {
                if (position == 0) {
                    setCurrentItem(getAdapter().getCount() - 2, false);
                } else if (position == getAdapter().getCount() - 1) {
                    setCurrentItem(1, false);
                }
            }

            if (listener != null) {
                listener.onPageScrollStateChanged(state);
            }

        }
    }

    /**
     * 设置触摸滑动的监听
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                stopScroll();
            break;
            case MotionEvent.ACTION_MOVE:
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startScroll();

            break;
        }
        return super.onTouchEvent(ev);
    }

    public  Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            if (currentItem/2==0){
                handler.sendEmptyMessageDelayed(1, 13000);
            }
            else{
                handler.sendEmptyMessageDelayed(1, 18000);
            }
   //       handler.sendEmptyMessageDelayed(1, 5000);

        }
    };

    /**
     * 开始无限轮播
     */
    public void startScroll() {
//        if (getCurrentItem()/2==0){
//            handler.sendEmptyMessageDelayed(1, 18000);
//        }
//        else{
//            handler.sendEmptyMessageDelayed(1, 18000);
//        }
       handler.sendEmptyMessageDelayed(1, 18000);
        isScroll=true;
    }

    /**
     * 停止无限轮播
     */
    public void stopScroll() {
        handler.removeMessages(1);
        isScroll=false;
    }


    /**
     * 修正PagerAdapter 适配器
     */
    class innerPagerAdapter extends PagerAdapter {

        private PagerAdapter adapter;

        public innerPagerAdapter(PagerAdapter adapter) {

            this.adapter = adapter;
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return adapter.getCount() + 2;
        }

        /**
         * Determines whether a page View is associated with a specific key object
         * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view   Page View to check for association with <code>object</code>
         * @param object Object to check for association with <code>view</code>
         * @return true if <code>view</code> is associated with the key object <code>object</code>
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                position = adapter.getCount() - 1;
            } else if (position == getCount() - 1) {
                position = 0;
            } else {
                position -= 1;
            }
            return adapter.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            adapter.destroyItem(container, position, object);
        }
    }

    //设置 页面改变监听




}
