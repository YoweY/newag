package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;
import android.widget.Button;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.iml.DialogActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class DialogHandler extends  BaseHandler
{
    public DialogHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        final DialogActivity dialogActivity = (DialogActivity) reference.get();
        switch (message.what){
            case 101:
                dialogActivity.endBack((String)message.obj);
                break;
            case 102:
                dialogActivity.checkItem((String)message.obj);
                break;
        }
    }
}
