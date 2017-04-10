package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
import com.info.aegis.lawpush4android.presenter.ProgramQuestionDialogPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.ThreadPool;
import com.info.aegis.lawpush4android.view.ProgramQuestionActivityService;
import com.info.aegis.lawpush4android.view.handler.ProgramQuestionActivityHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/3/7.
 * Description :
 */

public class ProgramQuestionDialogActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, ProgramQuestionActivityService {

    private ImageView mTitleImg;
    private TextView mTitleText;
    private TextView mAlClose;


    private ImageView mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg, voiceBgImageView, mVoiceBgMicImageView;
    private AnimationDrawable mVoiceAnimationDrawable;

    private boolean isAnswer = false;
    private String mAnswer, mContent, mTitle;
    private FrameLayout mFrameLayout;
    String motionValue = "";
    String des = "";
    private View mScrollView;
    private RelativeLayout mVoiceRelay;
    private String speakContent = "";
    private TextView mAlAnswer;
    private TextView mAlLaw;
    ProgramQuestionDialogPresenter programQuestionDialogPresenter;
    private List<String> compareStringList = new ArrayList<>();

    ///////////////////

    protected static final int SHOWHIGH = 0;
    protected static final int REMOOVE = 1;
    private int start = 0;
    private int end = 1;
    private boolean flag = true;
    private TextView mContentText;
    private TextView mContentTitle;
    private Thread thread;

    private boolean isAnsAuto = false;
    private Thread msgThread;
    private ThreadPool threadPool;
    /**
     * 歌词 线程是否运行
     */
    private boolean isRunning = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(ProgramQuestionDialogActivity.this);
        setContentView(R.layout.activity_answerandlaw);
        programQuestionDialogPresenter = new ProgramQuestionDialogPresenter(ProgramQuestionDialogActivity.this);
        handler = new ProgramQuestionActivityHandler(ProgramQuestionDialogActivity.this);

        threadPool = ThreadPool.getThreadPool(1);
        initData();
        initView();
        compareStringList.add("答案");
        compareStringList.add("法律");
        Log.e("lawPush", " ProgramQuestionDialogActivity-----onCreate");
        isAnsAuto = true;
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

        LawPushApp.setCurrntActivity("ProgramQuestionDialogActivity");
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
        Log.e("lawPush", " ProgramQuestionDialogActivity-----onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        AppManager.getAppManager().removeActivityStack(ProgramQuestionDialogActivity.this);
        Log.e("lawPush", " ProgramQuestionDialogActivity-----onPause");
        isAnsAuto = false;
        threadPool.stopThread();

        finish();

    }

    private Context myContext;

    @Override
    public Context getContext() {
        if (myContext == null)
            myContext = ProgramQuestionDialogActivity.this;
        Log.e("lawPush", " ProgramQuestionDialogActivity-----getContext");
        return myContext;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //参考答案
        isAnswer = getIntent().getBooleanExtra("isAnswer", false);
        mAnswer = getIntent().getStringExtra("answer");
        //法律法规
        mContent = getIntent().getStringExtra("content");
        mTitle = getIntent().getStringExtra("title");
        if (getIntent().getStringExtra("motion") != null && !getIntent().getStringExtra("motion").equals("")) {
            motionValue = getIntent().getStringExtra("motion");
        }

    }

    /**
     * 初始化View
     */
    private void initView() {
        //  程序问题
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

        mAlClose = (TextView) findViewById(R.id.al_close);
        mAlAnswer = (TextView) findViewById(R.id.al_answer);
        mAlLaw = (TextView) findViewById(R.id.al_law);

        mScrollView = findViewById(R.id.al_ScrollView);
        mScrollView.setOnTouchListener(this);
        mAlClose.setOnClickListener(this);
        mAlAnswer.setOnClickListener(this);
        mAlLaw.setOnClickListener(this);
        mVoiceRelay.setOnClickListener(this);
        //  实体问题
    }

    /**
     * 更新数据
     */
    private void upData() {
        //  程序问题
        mFrameLayout.removeAllViews();
        View mContentView = View.inflate(this, R.layout.fram_law, null);
        mContentText = (TextView) mContentView.findViewById(R.id.al_content);
        mContentTitle = (TextView) mContentView.findViewById(R.id.al_title);
        if (isAnswer) {
            mTitleText.setText("参考答案");
            mTitleImg.setImageResource(R.mipmap.answerimg);
            mAlAnswer.setBackgroundResource(R.mipmap.selectimgbg);
            mAlLaw.setBackgroundResource(R.mipmap.normalimgbg);
            if (mAnswer != null) {
                des = "您好,您正在查看的是与该问题相关的答案!";
                speakContent = des + mAnswer + "-:";
                mContentText.setText(mAnswer);
                mContentTitle.setVisibility(View.GONE);
                Log.d("ProgramQuestionDialogAc", "  答案字符串长度" + mAnswer.length());

                splitString(des, mAnswer);
            } else {
                des = "您好,该问题暂无相关答案!!-:";
                speakContent = des;
                mContentTitle.setVisibility(View.INVISIBLE);
                mContentText.setText("\u3000\u3000" + "该问题暂无相关答案!");
            }
        } else {
            mTitleText.setText("法律法规");
            mTitleImg.setImageResource(R.mipmap.lawimg);
            mAlAnswer.setBackgroundResource(R.mipmap.normalimgbg);
            mAlLaw.setBackgroundResource(R.mipmap.selectimgbg);
            if (mTitle == null || mContent == null) {
                des = "您好,没有与该问题相匹配的法条信息!!-;";
                speakContent = des;
                mContentTitle.setVisibility(View.INVISIBLE);
                mContentText.setText("\u3000\u3000" + "您好,没有与该问题相匹配的法条信息!");
            } else {
                des = "您好,您正在查看的是与该问题相关的法条!" + mTitle;
                speakContent = des + mContent + "-;";
                mContentTitle.setVisibility(View.VISIBLE);
                mContentTitle.setText(mTitle);
                mContentText.setText(mContent);
                Log.d("ProgramQuestionDialogAc", "  答案字符串长度" + mContent.length());

                splitString(des, mContent);
            }

        }
        mFrameLayout.addView(mContentView);
        speakContent(speakContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_close:
                finish();
                break;
            case R.id.al_answer:
                mAlAnswer.setBackgroundResource(R.mipmap.selectimgbg);
                mAlLaw.setBackgroundResource(R.mipmap.normalimgbg);
                isAnswer = true;
                if (threadPool!=null)
                threadPool.stopThread();
                isRunning=false;
                upData();
                break;
            case R.id.al_law:
                mAlAnswer.setBackgroundResource(R.mipmap.normalimgbg);
                mAlLaw.setBackgroundResource(R.mipmap.selectimgbg);
                isAnswer = false;
                isRunning=false;
                if (threadPool!=null)
                    threadPool.stopThread();
                upData();
                break;

            case R.id.mVoiceRelativeLayout:
//                stopAnimation();
                startFrontVoiceRecognizeListening();
                break;
        }

        Log.e("lawPush", " ProgramQuestionDialogActivity-----onClick");
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
        Log.e("lawPush", " ProgramQuestionDialogActivity-----startAnimation");
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        mVoiceAnimationOutImg.clearAnimation();
        mVoiceAnimationInImg.clearAnimation();
        voiceBgImageView.clearAnimation();
        mVoiceAnimationMicImg.clearAnimation();
        mVoiceAnimationMicImg.setVisibility(View.GONE);
        mVoiceBgMicImageView.setImageResource(R.mipmap.button_mic);
        Log.e("lawPush", " ProgramQuestionDialogActivity-----stopAnimation");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopCountDownTime();
                pauseSpeak();
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                startCountDownTimer();
                resumeSpeak();

                break;
        }
        Log.e("lawPush", " ProgramQuestionDialogActivity-----onTouch");
        return false;
    }

    @Override
    public void setType(int type) {
    }

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        Log.e("lawPush", " ProgramQuestionDialogActivity-----sendToServer");
        //本地匹配
        boolean isHave = false;
        int index = 0;
        for (int i = 0; i < compareStringList.size(); i++) {
            if (content.contains(compareStringList.get(i))) {
                isHave = true;
                index = i;
                break;
            }
        }
        if (isHave) {
            switch (compareStringList.get(index)) {
                case "答案":
                    isAnswer = true;
                    upData();
                    break;
                case "法律":
                    isAnswer = false;
                    upData();
                    break;
            }
            return;
        }

        //服务端匹配
        boolean isRight = false;
        float value;
        for (int i = 0; i < compareStringList.size(); i++) {
            value = Constant.getSimilarityRatio(content, compareStringList.get(i));
            if (value > 0.4) {
                isRight = true;
                break;
            }
        }
        if (isRight) {
            String choices = "";
            programQuestionDialogPresenter.getSelect(choices, content);
            return;
        }
        if (LawPushApp.getVoiceListeningType() == 2) {
            LawPushApp.setMultipleValue(0);
            endBack(content);
        }
    }


    private void splitString(final String des, String content) {
        if (isAnswer) {
            content = (null == mAnswer ? "您好,没有与该问题相匹配的答案!" : content);
        } else {
            content = (null == mContent ? "您好,没有与该问题相匹配的法条信息!" : content);
        }

        final String finalContent = content;
        final String[] split = content.split("[。？；：;，!]");

        threadPool.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long) (des.length() / 3.2) * 1000);
                    while (flag) {
                        Bundle b = new Bundle();
                        for (int i = 0; i < split.length; i++) {
                            String aSplit = split[i];
                            Log.d("ProgramQuestionDialogAc", "  字符串" + aSplit);
                            start = 0;
                            end += aSplit.length() + 1;
                            if (end < finalContent.length()) {

                                b.putInt("start", start);
                                b.putInt("end", end - 1);
                                Message msg = myHandler.obtainMessage(SHOWHIGH, b);
                                myHandler.sendMessage(msg);
                            } else {
                                flag = false;
                                b.putInt("start", start);
                                b.putInt("end", finalContent.length());
                                Message msg = myHandler.obtainMessage(SHOWHIGH, b);
                                myHandler.sendMessage(msg);
                            }
                            long l = (long) ((aSplit.length() / 3.4) * 1000 - 200);
                            Log.d("ProgramQuestionDialogAc", "休眠时间" + l + ">>>>>>");
                            Thread.sleep(l);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOWHIGH:
                    Bundle b = (Bundle) msg.obj;
                    int mStart = b.getInt("start");
                    int mEnd;
                    SpannableStringBuilder ssb;
                    if (isAnswer) {
                        if (mAnswer != null) {
                            ssb = new SpannableStringBuilder(mAnswer);
                            mEnd = b.getInt("end") < mAnswer.length() ? b.getInt("end") : mAnswer.length();
                        } else {
                            ssb = new SpannableStringBuilder("您好,没有与该问题相匹配的答案!");
                            mEnd = b.getInt("end") < "您好,没有与该问题相匹配的答案!".length() ? b.getInt("end") : "您好,没有与该问题相匹配的答案!".length();
                        }

                    } else {
                        if (mContent != null) {
                            ssb = new SpannableStringBuilder(mContent);
                            mEnd = b.getInt("end") < mContent.length() ? b.getInt("end") : mContent.length();
                        } else {
                            ssb = new SpannableStringBuilder("您好,没有与该问题相匹配的法条信息!");
                            mEnd = b.getInt("end") < "您好,没有与该问题相匹配的法条信息!".length() ? b.getInt("end") : "您好,没有与该问题相匹配的法条信息!".length();
                        }
                    }
                    if (isRunning)
                    ssb.setSpan(new ForegroundColorSpan(myContext.getResources().getColor(R.color.dialog_select_yes)), mStart, mEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    // TODO: 2017/3/31   字体变颜色
                    Log.d("ProgramQuestionDialogAc", "开始" + mStart + ">>>>>>结束" + mEnd);
                    mContentText.setText(ssb);

                    break;
                case REMOOVE:
                    SpannableStringBuilder ss;
                    if (isAnswer) {
                        ss = new SpannableStringBuilder(null == mAnswer ? "您好,没有与该问题相匹配的答案!" : mAnswer);
                    } else {
                        ss = new SpannableStringBuilder(null == mContent ? "您好,没有与该问题相匹配的法条信息!" : mContent);
                    }
                    mContentText.setText(ss);
                    break;
            }
        }
    };


    private void speakContent(final String content) {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSpeak(content);

                try {
                    if (!isRunning){
                        return;
                    }
                    Thread.sleep((long) (speakContent.length() / 3.2) * 1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAnsAuto) {
                                if (speakContent.split("-")[1].equals(":")) {
                                    checkItem("法律");
                                }
                                isAnsAuto = false;
                                flag = true;
                                end = 1;
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
//        //noinspection SynchronizeOnNonFinalField
//        synchronized (thread) {
//            try {
//                thread.wait();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }


    /**
     * 匹配选项
     *
     * @param obj
     */
    public void checkItem(String obj) {
        //select接口识别
        switch (obj) {
            case "答案":
                isAnswer = true;
                upData();
                break;
            case "法律":
                isAnswer = false;
                upData();
                break;
        }
    }

    //select接口识别错误，重新调取cause接口
    public void endBack(String content) {
        Intent intent = new Intent();
        intent.putExtra("value", content);
        setResult(20, intent);
        finish();
    }
}
