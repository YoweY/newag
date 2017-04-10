package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.StandardQuestionDialogActivity;
import com.info.aegis.lawpush4android.view.iml.TextActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class StanardQuestionActivityHandler extends  BaseHandler
{
    public StanardQuestionActivityHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        final StandardQuestionDialogActivity standardQuestionDialogActivity = (StandardQuestionDialogActivity) reference.get();
        switch (message.what){
            case 101:
                standardQuestionDialogActivity.endBack((String)message.obj);
                break;
            case 102:
                standardQuestionDialogActivity.checkItem((String)message.obj);
                break;
        }
    }
}
