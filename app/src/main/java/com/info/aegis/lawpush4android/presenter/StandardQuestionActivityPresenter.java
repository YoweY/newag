package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.StandardQuestionActivityService;
import com.info.aegis.lawpush4android.view.TextActivityActivityService;

/**
 * Created by Administrator on 2016/12/13.
 */

public class StandardQuestionActivityPresenter extends BasePresenter {
    public StandardQuestionActivityPresenter(StandardQuestionActivityService standardQuestionActivityService) {
        super(standardQuestionActivityService.getContext());
        this.baseActivityService = standardQuestionActivityService;
        init();
    }
}
