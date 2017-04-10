package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.view.iml.GuideActivity;
import com.info.aegis.lawpush4android.view.iml.SplashActivity;

/**
 * Created by SFS on 2017/3/9.
 * Description :
 */

public class SplashHandler extends BaseHandler {
    public SplashHandler(Activity activity) {
        super(activity);
    }


    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        SplashActivity splashActivity = (SplashActivity) reference.get();
        switch (message.what){
            case 106:
                splashActivity.initFailed();
                break;
            case 107:
                splashActivity.initSuccess();
                break;
            case 101:
                splashActivity.rePreCause((String)message.obj);
                break;
            case 102:
                splashActivity.checkItem((String)message.obj);
        }
    }
}
