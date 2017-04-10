package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.GuideActivity;
import com.info.aegis.lawpush4android.view.iml.TextActivity;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class GuideSceneActivityHandler extends  BaseHandler
{
    public GuideSceneActivityHandler(Activity activity) {
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
