package com.info.aegis.lawpush4android.view.iml;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.presenter.LawDetailPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.LawDetailActivityService;
import com.info.aegis.lawpush4android.view.detail.CaseFragment;
import com.info.aegis.lawpush4android.view.detail.GuidesFragment;
import com.info.aegis.lawpush4android.view.detail.LawInfoFragment;
import com.info.aegis.lawpush4android.view.detail.PointsFragment;
import com.info.aegis.lawpush4android.view.handler.LawDetailHandler;

import aegis.aegisxunfei.MyLawPushApp;


/**
 * Created by 申富生 on 2016/12/20.
 * Description :  详情页
 */

public class LawDetailActivity extends BaseActivity implements LawDetailActivityService, View.OnTouchListener {

    private Context context;
    /**
     * 标题
     */
    private String titleName;
    /**
     * 请求id
     */
    private String id = "";
    /**
     * 请求类型
     */
    private int type = -1;

    private TextView titleText;

    private LawDetailPresenter lawDetailPresenter;

    private String pqContent = "";

    private View rootView;

private String speakContent="";


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_detail);

        AppManager.getAppManager().addActivity(LawDetailActivity.this);

        titleName = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        type = getIntent().getIntExtra("type", -1);
        pqContent = getIntent().getStringExtra("pq_content");

        context = this;
        handler = new LawDetailHandler(LawDetailActivity.this);
        lawDetailPresenter = new LawDetailPresenter(this);

        titleText = (TextView) findViewById(R.id.id_title_text);

        findViewById(R.id.id_back_relay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rootView = findViewById(R.id.id_rootView);
        rootView.setOnTouchListener(this);

        Fragment fragment = null;
        switch (type) {
            case 1:
                //1.cases  案例
                titleText.setText("案例");
                fragment = new CaseFragment();
                break;
            case 2:
                //2. laws   法律知识
                titleText.setText("法律知识");
                fragment = new LawInfoFragment();
                break;
            case 3:
                //3.points  司法观点
                titleText.setText("司法观点");
                fragment = new PointsFragment();
                break;
            case 4:
                //4.guides   指导案例
                titleText.setText("指导案例");
                fragment = new GuidesFragment();
                break;
            case 5:
                //4.guides   指导案例
                titleText.setText("参考答案");
                fragment = new LawInfoFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("name", titleName);
        bundle.putString("id", id);
        bundle.putString("pq_content", pqContent);
        fragment.setArguments(bundle);
        selectFragment(fragment);
        Log.e("lawPush","LawDetailActivity-----onCreate");
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
        LawPushApp.setCurrntActivity("LawDetailActivity");
        //开启语音唤醒监听
        switch (type) {
            case 1:
                //1.cases  案例
                speakContent="您好,您正在查看的是与该问题相关的案例,";

                break;
            case 2:
                //2. laws   法律知识
                speakContent="您好,以下内容是与该问题相关的法条信息,";
                break;
            case 3:
                //3.points  司法观点
                speakContent="您好,您正在查看的是与该问题相关的司法观点,";
                break;
            case 4:
                //4.guides   指导案例
                speakContent="您好,您正在查看的是与该问题相关的指导案例,";
                break;
            case 5:
                speakContent="您好,您正在查看的是与该问题相关的参考答案,";
                break;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSpeak(speakContent);
            }
        }).start();
        Log.e("lawPush","LawDetailActivity-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        AppManager.getAppManager().removeActivityStack(LawDetailActivity.this);
        Log.e("lawPush","LawDetailActivity-----onPause");
        finish();
    }

    private void selectFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.id_container_lay, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
        }
    }

    @Override
    public Context getContext() {
        return context == null ? LawDetailActivity.this : context;
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
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                stopCountDownTime();
                break;
        }

        return false;
    }



}
