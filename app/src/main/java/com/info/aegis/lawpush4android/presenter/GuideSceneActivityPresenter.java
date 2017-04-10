package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.GuideSceneActivityService;
import com.info.aegis.lawpush4android.view.TextActivityActivityService;

/**
 * Created by Administrator on 2016/12/13.
 */

public class GuideSceneActivityPresenter extends BasePresenter {
    public GuideSceneActivityPresenter(GuideSceneActivityService guideSceneActivityService) {
        super(guideSceneActivityService.getContext());
        this.baseActivityService = guideSceneActivityService;
        init();
    }
}
