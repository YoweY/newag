package com.info.aegis.lawpush4android.view.iml;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.presenter.DialogPresenter;
import com.info.aegis.lawpush4android.presenter.TextActivityPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.TextActivityActivityService;
import com.info.aegis.lawpush4android.view.handler.TextActivityHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/2/22.
 * Description :
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class TextActivity extends BaseActivity implements View.OnClickListener, TextActivityActivityService {

    private ImageView mGifImg;
    private TextView mTitle;
    private MyCountDown time;

//    Integer[] mImg = {R.drawable.jqr, R.drawable.jqr3};
//    private Random random;
//    private int m;

    private ImageView mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg, voiceBgImageView, mVoiceBgMicImageView;
    private AnimationDrawable mVoiceAnimationDrawable;
    private RelativeLayout mVoiceRelay;

    private TextView mRead;
    private TextView mContent;
    private ScrollView mScrollView;
    String content;
    String motionValue = "";
    private TextActivityPresenter textActivityPresenter;
    private View rootView;
    private String speakContent = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        AppManager.getAppManager().addActivity(TextActivity.this);
        textActivityPresenter = new TextActivityPresenter(TextActivity.this);
        handler = new TextActivityHandler(TextActivity.this);
        initView();
        initEvent();

        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        mContent.setText("\u3000\u3000" + content);
        speakContent = content;
        if (intent.getStringExtra("motion") != null && !intent.getStringExtra("motion").equals("")) {
            motionValue = intent.getStringExtra("motion");
        }
        Log.e("lawPush", " TextActivity-----onCreate");
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
        if (LawPushApp.isNeedContinueListen())
            startAnimation();
        else
            stopAnimation();

        LawPushApp.setCurrntActivity("TextActivity");
        if (!motionValue.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        robotPerformAction(new JSONObject(motionValue));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        byte[] bytes = content.getBytes();
        int length = bytes.length;
        int i = length / Integer.valueOf("70");
        final String[] split = content.split("，");
        time = new MyCountDown(length * 100, 1000);
        time.start();

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
        Log.e("lawPush", " TextActivity-----onReusme");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (time != null)
            time.cancel();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();

        if (!LawPushApp.getDoubleAnswerDoctext().equals("")) {
            Intent intent = new Intent(this, DialogActivity.class);
            intent.putExtra("title", LawPushApp.getDoubleAnswerDoctext());
            intent.putExtra("isOne", true);
            intent.putExtra("type", "choices");

            ArrayList<String> stringList = new ArrayList<>();
            for (int i = 0; i < LawPushApp.getDoubleAnswerChoice().length; i++) {
                stringList.add(LawPushApp.getDoubleAnswerChoice()[i].getContent());
            }
            intent.putStringArrayListExtra("tagArray", stringList);
            startActivity(intent);
        }
        AppManager.getAppManager().removeActivityStack(TextActivity.this);
        Log.e("lawPush", " TextActivity-----onPause");
        finish();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        mGifImg = (ImageView) findViewById(R.id.chat_dialog_img);
        mTitle = (TextView) findViewById(R.id.chat_dialog_title);
        mRead = (TextView) findViewById(R.id.chat_dialog_read);
        mContent = (TextView) findViewById(R.id.chat_dialog_content);
        mScrollView = (ScrollView) findViewById(R.id.chat_dialog_ScrollView);
        //持续录音
        mVoiceAnimationOutImg = (ImageView) findViewById(R.id.animationIV);
        mVoiceAnimationInImg = (ImageView) findViewById(R.id.animationIV1);
        mVoiceAnimationMicImg = (ImageView) findViewById(R.id.animationIV2);
        mVoiceAnimationDrawable = (AnimationDrawable) mVoiceAnimationMicImg.getDrawable();
        voiceBgImageView = (ImageView) findViewById(R.id.voice);
        mVoiceBgMicImageView = (ImageView) findViewById(R.id.voiceBg);
        mVoiceRelay = (RelativeLayout) findViewById(R.id.mVoiceRelativeLayout);

        mScrollView.setScrollBarFadeDuration(1000);

//        random = new Random();
//        m = random.nextInt(mImg.length);

        rootView = findViewById(R.id.id_rootView);


    }


    void initEvent() {

        mRead.setOnClickListener(this);
        mVoiceRelay.setOnClickListener(this);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pauseSpeak();
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    case MotionEvent.ACTION_UP:
                        resumeSpeak();
                        Log.d("TextActivity", "我的手指抬起了");
                        break;

                }
                return false;
            }
        });

        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopSpeak();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_dialog_read:
                finish();
                break;
            case R.id.mVoiceRelativeLayout:
//                stopAnimation();
                startFrontVoiceRecognizeListening();
                break;

        }
    }

    /**
     * 添加动画
     */
    public void startAnimation() {
        try {
            Animation roateAnimation, roateAnimation2, mAlphaAnimation;
            //  旋转动画
            roateAnimation = AnimationUtils.loadAnimation(this, R.anim.roate);
            //匀速动画
            LinearInterpolator lin = new LinearInterpolator();
            roateAnimation.setInterpolator(lin);
            mVoiceAnimationOutImg.startAnimation(roateAnimation);      //  旋转动画

            //  小圆旋转动画
            roateAnimation2 = AnimationUtils.loadAnimation(this, R.anim.roate1);

            roateAnimation.setInterpolator(lin);
            mVoiceAnimationInImg.startAnimation(roateAnimation2);      //  旋转动画

            //添加透明动画
            mAlphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha);
            //保持动画结束的状态
            mAlphaAnimation.setFillAfter(true);
            voiceBgImageView.startAnimation(mAlphaAnimation);

            mVoiceBgMicImageView.setImageResource(R.mipmap.maikef_1);
            mVoiceAnimationMicImg.setVisibility(View.VISIBLE);
            mVoiceAnimationDrawable.start();
        } catch (Exception e) {
            MyLog.e("LawPush", e.getMessage());
        }
    }

    public void stopAnimation() {
        mVoiceAnimationOutImg.clearAnimation();
        mVoiceAnimationInImg.clearAnimation();
        voiceBgImageView.clearAnimation();
        mVoiceAnimationMicImg.clearAnimation();
        mVoiceAnimationMicImg.setVisibility(View.GONE);
        mVoiceBgMicImageView.setImageResource(R.mipmap.button_mic);
    }


    @Override
    public void setType(int type) {
    }

    private Context myContext;

    @Override
    public Context getContext() {
        if (myContext == null)
            myContext = TextActivity.this;
        return myContext;
    }

    /**
     * 定时器
     */
    protected class MyCountDown extends CountDownTimer {

        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            mRead.setText("阅读完毕(" + millisUntilFinished / 1000 + "s)");

        }

        @Override
        public void onFinish() {
            finish();
        }
    }


    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        endBack(content);
    }

    //select接口识别错误，重新调取cause接口
    public void endBack(String content) {
        Intent intent = new Intent();
        intent.putExtra("value", content);
        setResult(11, intent);
        finish();
    }
}
