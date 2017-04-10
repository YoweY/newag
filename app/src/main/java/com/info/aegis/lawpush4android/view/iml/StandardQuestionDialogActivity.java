package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.presenter.StandardQuestionActivityPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.StandardQuestionActivityService;
import com.info.aegis.lawpush4android.view.handler.StanardQuestionActivityHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/3/7.
 * Description :  标准问题的全屏
 */

public class StandardQuestionDialogActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener ,StandardQuestionActivityService{

    private ImageView mTitleImg;
    private TextView mTitleText;
    private TextView mAlContent;
    private TextView mAlClose;


    private ImageView mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg, voiceBgImageView, mVoiceBgMicImageView;
    private AnimationDrawable mVoiceAnimationDrawable;

    private boolean isAnswer = false;
    private String mAnswer, mContent, mTitle, mPointTitle, mPointContent, mCaseInfoTitle, mCaseInfoContent, mGuidanceCaseTitle, mGuidanceCaseContent;
    private FrameLayout mFrameLayout;
    private View mAnswerView;
    private TextView mLawContent;
    private TextView mLawTitle;
    String motionValue = "";
    private View mScrollView;
    private RelativeLayout mVoiceRelay;
    private String speakContent = "";
    private TextView mAlAnswer;
    private TextView mAlLaw;
    private TextView mAlPoint;
    private TextView mAlCase;
    private View mLawView;
    private int type;
    private List<String> compareStringList = new ArrayList<>();
    StandardQuestionActivityPresenter standardQuestionActivityPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(StandardQuestionDialogActivity.this);
        setContentView(R.layout.activity_standandquestion);
        initData();
        initView();
        standardQuestionActivityPresenter = new StandardQuestionActivityPresenter(StandardQuestionDialogActivity.this);
        handler = new StanardQuestionActivityHandler(StandardQuestionDialogActivity.this);
        Log.e("lawPush", " StandardQuestionActivity-----onCreate");
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

        LawPushApp.setCurrntActivity("StandardQuestionDialogActivity");
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
        upData();
        switch (type) {
            case -1:
            case 0:
                break;
            case 1:
                selectPointBg();
                break;
            case 2:
                selectCaseBg();
                break;
        }
        Log.e("lawPush", " StandardQuestionActivity-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        AppManager.getAppManager().removeActivityStack(StandardQuestionDialogActivity.this);
        Log.e("lawPush", " StandardQuestionActivity-----onPause");
        finish();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        type = getIntent().getIntExtra("type", -1);
        //参考答案
        isAnswer = getIntent().getBooleanExtra("isAnswer", false);
        mAnswer = getIntent().getStringExtra("answer");
        //法律法规
        mContent = getIntent().getStringExtra("content");
        mTitle = getIntent().getStringExtra("title");
        //司法观点
        mPointTitle = getIntent().getStringExtra("pointTitle");
        mPointContent = getIntent().getStringExtra("pointContent");
        //相关案例
        mCaseInfoTitle = getIntent().getStringExtra("caseInfoTitle");
        mCaseInfoContent = getIntent().getStringExtra("caseInfoContent");
        //指导案例
        mGuidanceCaseTitle = getIntent().getStringExtra("guidanceCaseTitle");
        mGuidanceCaseContent = getIntent().getStringExtra("guidanceCaseContent");
        if (getIntent().getStringExtra("motion") != null && !getIntent().getStringExtra("motion").equals("")) {
            motionValue = getIntent().getStringExtra("motion");
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mVoiceRelay = (RelativeLayout) findViewById(R.id.mVoiceRelativeLayout);
        mTitleImg = (ImageView) findViewById(R.id.title_img);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mFrameLayout = (FrameLayout) findViewById(R.id.al_frame);
        //持续录音
        mVoiceAnimationOutImg = (ImageView) findViewById(R.id.animationIV);
        mVoiceAnimationInImg = (ImageView) findViewById(R.id.animationIV1);
        mVoiceAnimationMicImg = (ImageView) findViewById(R.id.animationIV2);
        mVoiceAnimationDrawable = (AnimationDrawable) mVoiceAnimationMicImg.getDrawable();
        voiceBgImageView = (ImageView) findViewById(R.id.voice);
        mVoiceBgMicImageView = (ImageView) findViewById(R.id.voiceBg);
        //底部按钮
        mAlClose = (TextView) findViewById(R.id.al_close);
        mAlAnswer = (TextView) findViewById(R.id.al_answer);
        mAlLaw = (TextView) findViewById(R.id.al_law);
        mAlPoint = (TextView) findViewById(R.id.al_point);
        mAlCase = (TextView) findViewById(R.id.al_case);
        mScrollView = findViewById(R.id.al_ScrollView);

        mScrollView.setOnTouchListener(this);
        mAlClose.setOnClickListener(this);
        mAlAnswer.setOnClickListener(this);
        mAlLaw.setOnClickListener(this);
        mAlPoint.setOnClickListener(this);
        mAlCase.setOnClickListener(this);
        mVoiceRelay.setOnClickListener(this);
        //  实体问题
        if (isAnswer) {
            mAlAnswer.setVisibility(View.VISIBLE);
        } else {
            mAlAnswer.setVisibility(View.GONE);
        }
    }

    /**
     * 更新数据
     */
    private void upData() {
        mFrameLayout.removeAllViews();
        compareStringList.clear();
        if (isAnswer) {
            selectAnswerBg();
            mAnswerView = View.inflate(this, R.layout.fram_answer, null);
            mAlContent = (TextView) mAnswerView.findViewById(R.id.al_content);
            if (mAnswer != null) {
                speakContent = "您好,您正在查看的是与该问题相关的答案," + mAnswer;
                mAlContent.setText("\u3000\u3000" + mAnswer);


            } else {
                speakContent = "您好,该问题暂无相关答案!!";
                mAlContent.setText("\u3000\u3000" + "该问题暂无相关答案!");
                mAlAnswer.setVisibility(View.GONE);
            }
            mFrameLayout.addView(mAnswerView);
            compareStringList.add("答案");
        } else {
            selectLawBg();
            mLawView = View.inflate(this, R.layout.fram_law, null);
            mLawContent = (TextView) mLawView.findViewById(R.id.al_content);
            mLawTitle = (TextView) mLawView.findViewById(R.id.al_title);
            if (mTitle == null || mContent == null) {
                speakContent = "您好,没有与该问题相匹配的法条信息!!";
                mLawTitle.setVisibility(View.INVISIBLE);
                mLawContent.setText("您好,没有与该问题相匹配的法条信息!!");
            } else {
                speakContent = "您好,您正在查看的是与该问题相关的法条," + mTitle + mContent;
                mLawTitle.setText("\u3000\u3000" + mTitle);
                mLawContent.setText("\u3000\u3000" + mContent);
            }
            mFrameLayout.addView(mLawView);
            compareStringList.add("法律");
        }
        speakContent(speakContent);
        compareStringList.add("观点");
        compareStringList.add("案例");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_close:
                finish();
                break;
            case R.id.mVoiceRelativeLayout:
//                stopAnimation();
                startFrontVoiceRecognizeListening();
                break;

            case R.id.al_answer:
                //选中
                isAnswer = true;
                upData();
                break;
            case R.id.al_law:
                isAnswer = false;
                upData();
                break;
            case R.id.al_point:
                //司法观点
                selectPointBg();
                break;
            case R.id.al_case:
                //指导案例
                selectCaseBg();
                break;
        }

    }

    /**
     * 案例的背景
     */
    private void selectCaseBg() {
        mFrameLayout.removeAllViews();
        mAlAnswer.setBackgroundResource(R.mipmap.normalimgbg);
        mAlLaw.setBackgroundResource(R.mipmap.normalimgbg);
        mAlPoint.setBackgroundResource(R.mipmap.normalimgbg);
        mAlCase.setBackgroundResource(R.mipmap.selectimgbg);
        //加载数据
        mTitleImg.setImageResource(R.mipmap.caseimg);
        mTitleText.setText("案例");
        mLawView = View.inflate(this, R.layout.fram_law, null);
        mLawContent = (TextView) mLawView.findViewById(R.id.al_content);
        mLawTitle = (TextView) mLawView.findViewById(R.id.al_title);
        if (mGuidanceCaseTitle == null || mGuidanceCaseContent == null) {
            if (mCaseInfoTitle == null || mCaseInfoContent == null) {
                speakContent = "您好,没有与该问题相匹配的案例!!";
                mLawTitle.setVisibility(View.INVISIBLE);
                mLawContent.setText("您好,没有与该问题相匹配的案例!!");
            } else {
                speakContent = "您好,您正在查看的是与该问题相关的案例," + mCaseInfoTitle + mCaseInfoContent;
                mLawTitle.setText("\u3000\u3000" + mCaseInfoTitle);
                mLawContent.setText("\u3000\u3000" + mCaseInfoContent);
            }
        } else {
            speakContent = "您好,您正在查看的是与该问题相关的指导案例," + mGuidanceCaseTitle + mGuidanceCaseContent;
            mLawTitle.setText("\u3000\u3000" + mGuidanceCaseTitle);
            mLawContent.setText("\u3000\u3000" + mGuidanceCaseContent);
        }
        mFrameLayout.addView(mLawView);
        speakContent(speakContent);
    }


    /**
     * 司法观点的背景
     */
    private void selectPointBg() {
        mFrameLayout.removeAllViews();
        mAlAnswer.setBackgroundResource(R.mipmap.normalimgbg);
        mAlLaw.setBackgroundResource(R.mipmap.normalimgbg);
        mAlPoint.setBackgroundResource(R.mipmap.selectimgbg);
        mAlCase.setBackgroundResource(R.mipmap.normalimgbg);
        mTitleImg.setImageResource(R.mipmap.pointimg);
        mTitleText.setText("司法观点");
        mLawView = View.inflate(this, R.layout.fram_law, null);
        mLawContent = (TextView) mLawView.findViewById(R.id.al_content);
        mLawTitle = (TextView) mLawView.findViewById(R.id.al_title);
        if (mPointTitle == null || mPointContent == null) {
            speakContent = "您好,没有与该问题相匹配的司法观点!!";
            mLawTitle.setVisibility(View.INVISIBLE);
            mLawContent.setText("您好,没有与该问题相匹配的司法观点!!");
        } else {
            speakContent = "您好,您正在查看的是与该问题相关的司法观点, 观点内容为," + mPointTitle + mPointContent;
            mLawTitle.setText("\u3000\u3000" + mPointTitle);
            mLawContent.setText("\u3000\u3000" + mPointContent);
        }
        mFrameLayout.addView(mLawView);
        speakContent(speakContent);
    }

    /**
     * 法律法规的背景
     */
    private void selectLawBg() {

        mAlAnswer.setBackgroundResource(R.mipmap.normalimgbg);
        mAlLaw.setBackgroundResource(R.mipmap.selectimgbg);
        mAlPoint.setBackgroundResource(R.mipmap.normalimgbg);
        mAlCase.setBackgroundResource(R.mipmap.normalimgbg);
        mTitleImg.setImageResource(R.mipmap.lawimg);
        mTitleText.setText("法律法规");
    }

    /**
     * 参考答案的背景
     */
    private void selectAnswerBg() {
        mAlAnswer.setBackgroundResource(R.mipmap.selectimgbg);
        mAlLaw.setBackgroundResource(R.mipmap.normalimgbg);
        mAlPoint.setBackgroundResource(R.mipmap.normalimgbg);
        mAlCase.setBackgroundResource(R.mipmap.normalimgbg);
        mTitleImg.setImageResource(R.mipmap.answerimg);
        mTitleText.setText("参考答案");
    }




    /**
     * 语音读取
     *
     * @param content
     */
    private void speakContent(final String content) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSpeak(content);
            }
        }).start();
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
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
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

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);

        //本地匹配
        boolean isHave = false;
        int index = 0;
        for (int i = 0; i < compareStringList.size(); i++) {
            if (content.contains(compareStringList.get(i))){
                isHave = true;
                index = i;
                break;
            }
        }
        if (isHave){
            switch (compareStringList.get(index)){
                case "答案":
                    isAnswer = true;
                    upData();
                    break;
                case "法律":
                    isAnswer = false;
                    upData();
                    break;
                case "观点":
                    selectPointBg();
                    break;
                case "案例":
                    selectCaseBg();
                    break;
            }
            return;
        }

        //服务端匹配
        boolean isRight = false;
        float value;
        for (int i = 0; i < compareStringList.size(); i++) {
            value = Constant.getSimilarityRatio(content,compareStringList.get(i));
            if (value > 0.4){
                isRight = true;
                break;
            }
        }
        if (isRight){
            String choices = "";
            standardQuestionActivityPresenter.getSelect(choices,content);
            return;
        }
        if (LawPushApp.getVoiceListeningType()==2){
            LawPushApp.setMultipleValue(0);
            endBack(content);
        }
    }

    @Override
    public void setType(int type) {}

    /**
     * 匹配选项
     * @param obj
     */
    public void checkItem(String obj){
        //select接口识别
        switch (obj){
            case "答案":
                isAnswer = true;
                upData();
                break;
            case "法律":
                isAnswer = false;
                upData();
                break;
            case "观点":
                selectPointBg();
                break;
            case "案例":
                selectCaseBg();
                break;
        }
    }

    //select接口识别错误，重新调取cause接口
    public void endBack(String content){
        Intent intent = new Intent();
        intent.putExtra("value",content);
        setResult(20, intent);
        finish();
    }

    Context myContext;
    @Override
    public Context getContext() {
        if (myContext == null)
            myContext = StandardQuestionDialogActivity.this;
        return myContext;
    }
}
