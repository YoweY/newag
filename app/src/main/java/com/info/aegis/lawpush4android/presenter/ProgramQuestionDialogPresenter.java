package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.ProgramQuestionActivityService;

/**
 * Created by SFS on 2017/3/13.
 * Description :
 */

public class ProgramQuestionDialogPresenter extends BasePresenter {
    public ProgramQuestionDialogPresenter(ProgramQuestionActivityService programQuestionActivityService) {
        super(programQuestionActivityService.getContext());
        this.baseActivityService = programQuestionActivityService;
        init();
    }
}
