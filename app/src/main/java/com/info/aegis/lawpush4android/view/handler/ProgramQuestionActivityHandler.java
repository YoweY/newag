package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.ProgramQuestionDialogActivity;
import com.info.aegis.lawpush4android.view.iml.StandardQuestionDialogActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class ProgramQuestionActivityHandler extends  BaseHandler
{
    public ProgramQuestionActivityHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        ProgramQuestionDialogActivity programQuestionDialogActivity = (ProgramQuestionDialogActivity) reference.get();
        switch (message.what){
            case 101:
                programQuestionDialogActivity.endBack((String)message.obj);
                break;
            case 102:
                programQuestionDialogActivity.checkItem((String)message.obj);
                break;
        }
    }
}
