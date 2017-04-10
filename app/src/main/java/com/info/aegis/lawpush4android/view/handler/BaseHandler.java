package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;
import com.info.aegis.lawpush4android.view.iml.HelpActivity;

import java.lang.ref.WeakReference;

/**
 * Created by mcs on 2016/12/29.
 */

public class BaseHandler extends Handler {
    static String TAG = "handler";

    WeakReference<Activity> reference;

    public BaseHandler(Activity activity) {
        super();
        reference = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Constant.BACK:
                back();
                break;
            case Constant.HOME:
                home();
                break;
            case  Constant.HELP:
                help();
                break;
            case  Constant.DETAIL:
                detail();
                break;
            case  Constant.NEW:
                newPager();
                break;
//            case  Constant.FLOW:
//                flow();
//                break;
        }
    }

    private void flow() {
        Log.d(TAG, "flow: ");
        Activity activity = reference.get();
        Intent intent = new Intent(activity, DataDetailActivity.class);
        intent.putExtra("item",6);
        activity.startActivity(intent);
    }

    private void newPager() {
        Log.d(TAG, "new: ");
        Activity activity = reference.get();
        Intent intent = new Intent(activity, ChatActivity.class);
        activity.startActivity(intent);
    }


    private void back() {
        Log.d(TAG, "back: ");
        Activity activity = reference.get();
        activity.finish();
    }

    private void home() {
        Log.d(TAG, "home: ");
        Activity activity = reference.get();
        Intent intent = new Intent(activity, ChatActivity.class);
        activity.startActivity(intent);
    }

    private void help() {
        Log.d(TAG, "help: ");
        Activity activity = reference.get();
        Intent intent = new Intent(activity, HelpActivity.class);
        activity.startActivity(intent);
    }
    private void detail() {
        Log.d(TAG, "detail: ");
        Activity activity = reference.get();
        Intent intent = new Intent(activity, DataDetailActivity.class);
        activity.startActivity(intent);
    }
}
