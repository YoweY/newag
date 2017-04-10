package com.info.aegis.lawpush4android.presenter;


import com.info.aegis.lawpush4android.view.GuideActivityService;

/**
 * Created by SFS on 2017/2/9.
 * Description :
 */

public class GuidePresenter extends  BasePresenter {

    public GuidePresenter(GuideActivityService guideActivityService) {
        super(guideActivityService.getContext());
        this.baseActivityService=guideActivityService;
        init();
    }
}
