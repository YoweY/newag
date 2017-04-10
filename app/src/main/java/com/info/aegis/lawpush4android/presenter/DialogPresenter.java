package com.info.aegis.lawpush4android.presenter;

import android.content.Context;

import com.info.aegis.lawpush4android.view.DialogActivityService;
import com.info.aegis.lawpush4android.view.iml.DialogActivity;

public class DialogPresenter extends BasePresenter {
    public DialogPresenter(DialogActivityService dialogActivityService) {
        super(dialogActivityService.getContext());
        this.baseActivityService = dialogActivityService;
        init();
    }

}
