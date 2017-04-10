package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.ShowGuideDialogActivityService;

/**
 * Created by SFS on 2017/3/21.
 * Description :
 */

public class ShowGuideDialogPresenter extends  BasePresenter {
    public ShowGuideDialogPresenter(ShowGuideDialogActivityService showGuideDialogActivityService) {
        super(showGuideDialogActivityService.getContext());
        this.baseActivityService=showGuideDialogActivityService;
        init();
    }
}
