package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.BaseActivityService;
import com.info.aegis.lawpush4android.view.LawDetailActivityService;

/**
 * Created by mcs on 2016/12/30.
 */

public class LawDetailPresenter extends BasePresenter {
    public LawDetailPresenter(LawDetailActivityService lawDetailActivityService) {
        super(lawDetailActivityService.getContext());
        this.baseActivityService = (BaseActivityService) lawDetailActivityService;
        init();
    }
}
