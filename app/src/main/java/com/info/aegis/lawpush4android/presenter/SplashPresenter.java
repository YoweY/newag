package com.info.aegis.lawpush4android.presenter;

import android.content.Context;

import com.info.aegis.lawpush4android.view.SplashActivityService;

/**
 * Created by SFS on 2017/3/9.
 * Description :
 */

public class SplashPresenter extends BasePresenter {
    public SplashPresenter(SplashActivityService splashActivityService) {
        super(splashActivityService.getContext());
        this.baseActivityService=splashActivityService;
        init();
    }
}
