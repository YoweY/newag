package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.GuideActivity;

/**
 * Created by SFS on 2017/2/9.
 * Description :
 */

public class GuideHandler extends  BaseHandler {
    public GuideHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        GuideActivity guideActivity = (GuideActivity) reference.get();
        switch (message.what){

        }
    }
}
