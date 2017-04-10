package com.info.aegis.lawpush4android.presenter;


import com.info.aegis.lawpush4android.view.GuideActivityService;
import com.info.aegis.lawpush4android.view.SubGuideActivityService;

/**
 * Created by SFS on 2017/2/9.
 * Description :
 */

public class SubGuidePresenter extends  BasePresenter {

    public SubGuidePresenter(SubGuideActivityService subGuideActivityService) {
        super(subGuideActivityService.getContext());
        this.baseActivityService=subGuideActivityService;
        init();
    }
}
