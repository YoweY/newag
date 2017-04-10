package com.info.aegis.lawpush4android.presenter;

import android.content.Context;

import com.info.aegis.lawpush4android.view.ChooseActivityService;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class ChoosePresenter extends BasePresenter {
   public ChoosePresenter(ChooseActivityService chooseActivityService) {
        super(chooseActivityService.getContext());
       this.baseActivityService=chooseActivityService;
        init();
    }

    @Override
    public void getAnalyse(String content, String options, Integer type) {
        super.getAnalyse(content, options, type);
    }
}
