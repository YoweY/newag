package com.info.aegis.lawpush4android.presenter;

import com.info.aegis.lawpush4android.view.ChatActivityService;
import com.info.aegis.lawpush4android.view.TextActivityActivityService;

/**
 * Created by Administrator on 2016/12/13.
 */

public class TextActivityPresenter extends BasePresenter {
    public TextActivityPresenter(TextActivityActivityService textActivityActivityService) {
        super(textActivityActivityService.getContext());
        this.baseActivityService = textActivityActivityService;
        init();
    }
}
