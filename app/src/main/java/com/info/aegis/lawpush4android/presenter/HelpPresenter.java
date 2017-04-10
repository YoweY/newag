package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.HelpActivityService;

/**
 * Created by SFS on 2017/1/4.
 * Description : help 管理器
 */

public class HelpPresenter extends  BasePresenter {


    public HelpPresenter(HelpActivityService helpActivityService) {
        super(helpActivityService.getContext());
        this.baseActivityService=helpActivityService;

        init();
    }


}
