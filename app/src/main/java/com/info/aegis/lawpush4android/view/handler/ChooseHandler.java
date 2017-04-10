package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.ChooseActivity;
import com.info.aegis.lawpush4android.view.iml.DialogActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class ChooseHandler extends  BaseHandler {
    public ChooseHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        final ChooseActivity chooseActivity = (ChooseActivity) reference.get();
        switch (message.what){
            case 101:
                chooseActivity.endBack((String)message.obj);
                break;
            case 102:
                chooseActivity.checkItem((String)message.obj);
                break;
        }
    }
}
