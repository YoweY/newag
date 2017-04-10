package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.DialogActivity;
import com.info.aegis.lawpush4android.view.iml.TextActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class TextActivityHandler extends  BaseHandler
{
    public TextActivityHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        final TextActivity textActivity = (TextActivity) reference.get();
        switch (message.what){
            case 101:
                textActivity.endBack((String)message.obj);
                break;
        }
    }
}
