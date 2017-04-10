package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;


/**
 * Created by SFS on 2017/3/21.
 * Description :
 */

public class ShowGuideDialogActivityHandler extends  BaseHandler {
    public ShowGuideDialogActivityHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
//        ShowGuideDialogActivity showGuideDialogActivity = (ShowGuideDialogActivity) reference.get();
//        switch (message.what) {
//            case 106:
//                showGuideDialogActivity.initFailed();
//                break;
//            case 107:
//                showGuideDialogActivity.initSuccess();
//                break;
//            case 101:
//                showGuideDialogActivity.rePreCause((String) message.obj);
//                break;
//            case 102:
//                showGuideDialogActivity.checkItem((String) message.obj);
//        }
    }
}
