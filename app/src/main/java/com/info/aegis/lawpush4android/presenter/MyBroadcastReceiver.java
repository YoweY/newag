package com.info.aegis.lawpush4android.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.info.aegis.lawpush4android.view.iml.BaseActivity;
import com.info.aegis.lawpush4android.view.iml.GuideActivity;

/**
 * Created by SFS on 2017/2/12.
 * Description :
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private BaseActivity activity;

    public MyBroadcastReceiver(BaseActivity activity) {

        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBroadcastReceiver", "接收到消息");
        activity.startActivity(new Intent(activity, GuideActivity.class));
        activity.finish();
    }
}
