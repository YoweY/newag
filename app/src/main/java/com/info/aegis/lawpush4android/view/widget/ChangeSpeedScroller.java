package com.info.aegis.lawpush4android.view.widget;

import android.content.Context;
import android.view.animation.Interpolator;

import android.widget.Scroller;

/**
 * Created by SFS on 2017/3/13.
 * Description :  轮播图的滑动速度页面
 */

public class ChangeSpeedScroller extends Scroller {
    private int mDuration=250;

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public ChangeSpeedScroller(Context context){
        super(context);
    }
    public ChangeSpeedScroller(Context context, Interpolator interpolator){
        super(context,interpolator);
    }
    public ChangeSpeedScroller(Context context, Interpolator interpolator,boolean flywheel){
        super(context,interpolator,flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy);
    }

}
