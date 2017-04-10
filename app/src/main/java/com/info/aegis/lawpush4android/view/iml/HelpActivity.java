package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.presenter.HelpPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.MyToast;
import com.info.aegis.lawpush4android.view.HelpActivityService;
import com.info.aegis.lawpush4android.view.handler.HelpHandler;

import aegis.aegisxunfei.MyLawPushApp;


/**
 * Created by SFS on 2017/1/3.
 * Description : 帮助中心
 */

public class HelpActivity extends BaseActivity implements HelpActivityService, View.OnTouchListener {

    private Context context;
    private RelativeLayout mBack;
    private HelpPresenter helpPresenter;
    private int type;
    private ScrollView mScrollView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_help);

        AppManager.getAppManager().addActivity(HelpActivity.this);

        handler = new HelpHandler(this);
        helpPresenter = new HelpPresenter(HelpActivity.this);
        initView();
        onListener();
        Log.e("lawPush","HelpActivity-----onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        initVoice();
        initSpeakParama();
        //开启语音唤醒监听
        if (MyLawPushApp.isNeedContinueListen())
            startBackVoiceRecognizeListening();
        else
            startVoiceWakeUp();
        LawPushApp.setCurrntActivity("HelpActivity");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSpeak("您好,欢迎来到帮助中心!");
            }
        }).start();
        Log.e("lawPush","HelpActivity-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        AppManager.getAppManager().removeActivityStack(HelpActivity.this);
        Log.e("lawPush","HelpActivity-----onPause");
        finish();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBack = (RelativeLayout) findViewById(R.id.id_back_relay);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        mScrollView.setOnTouchListener(this);
    }

    /**
     * 初始化监听
     */
    private void onListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public Context getContext() {
        return context == null ? HelpActivity.this : context;
    }

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        endBack(content);
    }

    public void endBack(String content){
        Intent intent = new Intent();
        intent.putExtra("value",content);
        setResult(11, intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                stopCountDownTime();
                stopSpeak();
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                startCountDownTimer();
                break;
        }
        return false;
    }


}
