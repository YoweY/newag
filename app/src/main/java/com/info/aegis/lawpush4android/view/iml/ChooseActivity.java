package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.MsgTagBean;
import com.info.aegis.lawpush4android.model.bean.entity.law.MsgCauseBean;
import com.info.aegis.lawpush4android.presenter.ChoosePresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.ChooseActivityService;
import com.info.aegis.lawpush4android.view.handler.ChooseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/2/22.
 * Description :  单选
 */

public class ChooseActivity extends BaseActivity implements View.OnClickListener, ChooseActivityService {

    private ImageView mGifImg;
    private TextView mSelect;
    private TextView mTitle;
    private ListView mListView;
    private TextView mNo;
    private TextView mYes;
    private MyCountDown time;
    /**
     * 是否
     */
    boolean isYes = false;
    private LinearLayout mLayoutYes;
    private LinearLayout mLayoutNo;
    private ImageView mYesImg;
    private ImageView mNoImg;
    private TextView mYesText;
    private TextView mNoText;
  //  Integer[] mImg = {R.drawable.jqr, R.drawable.jqr3};
//    private Random random;
//    private int m;
    private ImageView mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg,voiceBgImageView,mVoiceBgMicImageView;
    private AnimationDrawable mVoiceAnimationDrawable;
    private RelativeLayout mVoiceRelay;
    private String title;
    private boolean isOne;
    private boolean mLeftIsClick=false;
    private boolean mRightClick=false;

    private ArrayList<MsgCauseBean> selectCauseArray = new ArrayList<>();
    private ArrayList<MsgTagBean> selectTagArray = new ArrayList<>();

    List<MsgCauseBean> arrayCauseList = new ArrayList<>();
    List<MsgTagBean> arrayTagList = new ArrayList<>();
    List<String> arrayChoicesList = new ArrayList<>();
    List<String> arrayChoicesOneList = new ArrayList<>();

    private ChoosePresenter choosePresenter;
    String motionValue = "",speakContent = "";

    private List<String> compareStringList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        time = new MyCountDown(50000, 1000);
        time.start();
        AppManager.getAppManager().addActivity(ChooseActivity.this);
        choosePresenter = new ChoosePresenter(ChooseActivity.this);
        handler = new ChooseHandler(ChooseActivity.this);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        isOne = intent.getBooleanExtra("isOne", false);
        String type = intent.getStringExtra("type");


        initView();
        mTitle.setText("       " + title);
        speakContent = speakContent + title;

        if (intent.getStringExtra("motion") != null && !intent.getStringExtra("motion").equals("")){
            motionValue = intent.getStringExtra("motion");
        }

        if (isOne && type.equals("choicesOne")){
            arrayChoicesOneList = intent.getStringArrayListExtra("tagArray");
            mYesText.setText(arrayChoicesOneList.get(0));
            compareStringList.add(arrayChoicesOneList.get(0));
            mLayoutNo.setVisibility(View.GONE);
            speakContent = speakContent + arrayChoicesOneList.get(0);
        }
        if (isOne && type.equals("choices")){
            arrayChoicesList = intent.getStringArrayListExtra("tagArray");
            mYesText.setText(arrayChoicesList.get(0));
            mNoText.setText(arrayChoicesList.get(1));
            compareStringList.add(arrayChoicesList.get(0));
            compareStringList.add(arrayChoicesList.get(1));
            speakContent = speakContent + "1"+arrayChoicesList.get(0) +",2"+ arrayChoicesList.get(1);
        }
        if (isOne && type.equals("cause")) {
            //单选案由
            String value = "{value:" + intent.getStringExtra("tagArray") + "}";
            try {
                JSONObject jsonObject = new JSONObject(value);
                JSONArray jsonArray = jsonObject.getJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    selectCauseArray.add(i, null);
                    MsgCauseBean msgCauseBean = new MsgCauseBean();
                    msgCauseBean.setName(jsonArray.getJSONObject(i).getString("name"));
                    msgCauseBean.setValue(jsonArray.getJSONObject(i).getString("value"));
                    arrayCauseList.add(msgCauseBean);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mYesText.setText(arrayCauseList.get(0).getName());
            mNoText.setText(arrayCauseList.get(1).getName());

            compareStringList.add(arrayCauseList.get(0).getName());
            compareStringList.add(arrayCauseList.get(1).getName());

            speakContent = speakContent + "1"+arrayCauseList.get(0).getName()+","+arrayCauseList.get(1).getName();
        }
        if (!isOne && type.equals("tag")) {
            //多选标签
            String value = "{value:" + intent.getStringExtra("tagArray") + "}";
            try {
                JSONObject jsonObject = new JSONObject(value);
                JSONArray jsonArray = jsonObject.getJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    selectTagArray.add(i, null);
                    MsgTagBean msgTagBean = new MsgTagBean();
                    msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                    msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                    msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                    arrayTagList.add(msgTagBean);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mYesText.setText(arrayTagList.get(0).getZhName());
            mNoText.setText(arrayTagList.get(1).getZhName());

            compareStringList.add(arrayTagList.get(0).getZhName());
            compareStringList.add(arrayTagList.get(1).getZhName());

            speakContent = speakContent + "1"+arrayTagList.get(0).getZhName()+",2"+arrayTagList.get(1).getZhName();
        }
        initListener();
        if (isOne){
            mSelect.setText("单选");
        }else{
            mSelect.setText("多选");
        }
        Log.e("lawPush","ChooseActivity-------onCreate");
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
        LawPushApp.setCurrntActivity("ChooseActivity");
        if (!motionValue.equals("")){
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
        Log.e("lawPush","ChooseActivity-------onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (time!= null)
            time.cancel();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        AppManager.getAppManager().removeActivityStack(ChooseActivity.this);
        Log.e("lawPush","ChooseActivity-------onPause");
        finish();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mGifImg = (ImageView) findViewById(R.id.chat_dialog_img);
        mSelect = (TextView) findViewById(R.id.chat_dialog_select);
        mTitle = (TextView) findViewById(R.id.chat_dialog_title);
        mLayoutYes = (LinearLayout) findViewById(R.id.item_choose_yes);
        mYesImg = (ImageView) findViewById(R.id.item_choose_yes_img);
        mYesText = (TextView) findViewById(R.id.item_choose_yes_text);
        mLayoutNo = (LinearLayout) findViewById(R.id.item_choose_no);
        mNoImg = (ImageView) findViewById(R.id.item_choose_no_img);
        mNoText = (TextView) findViewById(R.id.item_choose_no_text);
        mNo = (TextView) findViewById(R.id.chat_dialog_no);
        mYes = (TextView) findViewById(R.id.chat_dialog_yes);
        //持续录音
        mVoiceAnimationOutImg = (ImageView) findViewById(R.id.animationIV);
        mVoiceAnimationInImg = (ImageView) findViewById(R.id.animationIV1);
        mVoiceAnimationMicImg = (ImageView) findViewById(R.id.animationIV2);
        mVoiceAnimationDrawable = (AnimationDrawable) mVoiceAnimationMicImg.getDrawable();
        voiceBgImageView = (ImageView) findViewById(R.id.voice);
        mVoiceBgMicImageView = (ImageView) findViewById(R.id.voiceBg);
        mVoiceRelay = (RelativeLayout) findViewById(R.id.mVoiceRelativeLayout);

        mVoiceRelay.setOnClickListener(this);
        mLayoutYes.setOnClickListener(this);
        mLayoutNo.setOnClickListener(this);
        mNo.setOnClickListener(this);
        mYes.setOnClickListener(this);

     //   random = new Random();
   //     m = random.nextInt(mImg.length);
    }
    /**
     * 初始化监听
     */
    private void initListener() {
        //单选的情况
        if (isOne) {
            mLayoutYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickYes();
                }
            });
            mLayoutNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickNo();
                }
            });
        } else {

            mLayoutYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLeftIsClick){
                        mLayoutYes.setBackgroundResource(R.color.Tra);
                        mYesImg.setBackgroundResource(R.mipmap.feixuanze);
                        mYesText.setTextColor(getResources().getColor(R.color.choose_text));
                        mLeftIsClick = false;
                        if (isOne)
                            mRightClick = true;
                    }else{
                        mLayoutYes.setBackgroundResource(R.drawable.shape_choose_bg);
                        mYesImg.setBackgroundResource(R.mipmap.yixuananniu);
                        mYesText.setTextColor(getResources().getColor(R.color.white));
                        mLeftIsClick = true;
                        if (isOne)
                            mRightClick = false;
                    }
                }
            });

            mLayoutNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRightClick){
                        mLayoutNo.setBackgroundResource(R.color.Tra);
                        mNoImg.setBackgroundResource(R.mipmap.feixuanze);
                        mNoText.setTextColor(getResources().getColor(R.color.choose_text));
                        mRightClick = false;
                        if (isOne)
                            mLeftIsClick = true;
                    }else{
                        mLayoutNo.setBackgroundResource(R.drawable.shape_choose_bg);
                        mNoImg.setBackgroundResource(R.mipmap.yixuananniu);
                        mNoText.setTextColor(getResources().getColor(R.color.white));
                        mRightClick = true;
                        if (isOne)
                            mLeftIsClick = false;
                    }
                }
            });
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
    public void onClick(View v) {
        stopSpeak();
        switch (v.getId()) {
            case R.id.mVoiceRelativeLayout:
                startFrontVoiceRecognizeListening();
                break;

            case R.id.chat_dialog_no:
                Intent noIntent = new Intent();
                noIntent.putExtra("value", false);
                setResult(RESULT_OK, noIntent);
                finish();
                break;
            case R.id.chat_dialog_yes:
                Intent intent = new Intent();
                if (arrayChoicesOneList.size() != 0){
                    if (isYes){
                        //选择左边的
                        intent.putExtra("value",true);
                        intent.putExtra("arrayTag",arrayChoicesOneList.get(0).toString());
                    }else{
                        intent.putExtra("value",false);
                    }
                }
                if (arrayChoicesList.size() != 0){
                    intent.putExtra("value",true);
                    if (isYes){
                        //选择左边的
                        intent.putExtra("arrayTag",arrayChoicesList.get(0).toString());
                    }else{
                        //选择的右边
                        intent.putExtra("arrayTag",arrayChoicesList.get(1).toString());
                    }
                }
                if (arrayCauseList.size() != 0){
                    intent.putExtra("value",true);
                    if (isYes){
                        //选择左边的
                        intent.putExtra("arrayTag",arrayCauseList.get(0).toString());
                    }else{
                        //选择的右边
                        intent.putExtra("arrayTag",arrayCauseList.get(1).toString());
                    }
                }
                if (arrayTagList.size() != 0){
                    if (!mLeftIsClick)
                        arrayTagList.remove(0);
                    if (!mRightClick){
                        if (arrayTagList.size() == 2)
                            arrayTagList.remove(1);
                        else if (arrayTagList.size() == 1)
                            arrayTagList.remove(0);
                    }
                    if (arrayTagList == null || arrayTagList.size() == 0)
                        intent.putExtra("value",false);
                    else
                        intent.putExtra("value",true);
                    intent.putExtra("arrayTag",arrayTagList.toString());
                }

                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }

    /**
     * 选择否
     */
    private void ClickNo() {
        mLayoutYes.setBackgroundResource(R.color.Tra);
        mYesImg.setBackgroundResource(R.mipmap.feixuanze);
        mYesText.setTextColor(getResources().getColor(R.color.choose_text));
        mLayoutNo.setBackgroundResource(R.drawable.shape_choose_bg);
        mNoImg.setBackgroundResource(R.mipmap.yixuananniu);
        mNoText.setTextColor(getResources().getColor(R.color.white));
        isYes = false;
    }

    /**
     * 选择是
     */
    private void ClickYes() {
        mLayoutYes.setBackgroundResource(R.drawable.shape_choose_bg);
        mYesImg.setBackgroundResource(R.mipmap.yixuananniu);
        mYesText.setTextColor(getResources().getColor(R.color.white));
        mLayoutNo.setBackgroundResource(R.color.Tra);
        mNoImg.setBackgroundResource(R.mipmap.feixuanze);
        mNoText.setTextColor(getResources().getColor(R.color.choose_text));
        isYes = true;
    }

    private Context myContext;
    @Override
    public Context getContext() {
        if (myContext == null) {
            myContext = ChooseActivity.this;
        }
        return myContext;
    }


    /**
     * 定时器
     */
    protected class MyCountDown extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            mYes.setText("确认选择(" + millisUntilFinished / 1000 + "s)");

        }

        @Override
        public void onFinish() {
            Intent intent = new Intent();

            if (arrayChoicesOneList.size() != 0){
                if (isYes){
                    //选择左边的
                    intent.putExtra("value",true);
                    intent.putExtra("arrayTag",arrayChoicesOneList.get(0).toString());
                }else{
                    intent.putExtra("value",false);
                }
            }
            if (arrayChoicesList.size() != 0){
                intent.putExtra("value",true);
                if (isYes){
                    //选择左边的
                    intent.putExtra("arrayTag",arrayChoicesList.get(0).toString());
                }else{
                    //选择的右边
                    intent.putExtra("arrayTag",arrayChoicesList.get(1).toString());
                }
            }
            if (arrayCauseList.size() != 0){
                intent.putExtra("value",true);
                if (isYes){
                    //选择左边的
                    intent.putExtra("arrayTag",arrayCauseList.get(0).toString());
                }else{
                    //选择的右边
                    intent.putExtra("arrayTag",arrayCauseList.get(1).toString());
                }
            }
            if (arrayTagList.size() != 0){
                if (!mLeftIsClick)
                    arrayTagList.remove(0);
                if (!mRightClick){
                    if (arrayTagList.size() == 2)
                        arrayTagList.remove(1);
                    else if (arrayTagList.size() == 1)
                        arrayTagList.remove(0);
                }
                if (arrayTagList == null || arrayTagList.size() == 0)
                    intent.putExtra("value",false);
                else
                    intent.putExtra("value",true);
                intent.putExtra("arrayTag",arrayTagList.toString());
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void sendToServer(String content) {
        boolean isRight = false;
        float value = 0;
        for (int i = 0; i < compareStringList.size(); i++) {
            value = Constant.getSimilarityRatio(content,compareStringList.get(i));
            if (value > 0.4){
                isRight = true;
            }
        }

        if ( isRight || content.contains("个") || content.contains("第")){
            String choices = "";
            if (arrayChoicesOneList.size() != 0) {
                for (int i = 0; i < arrayChoicesOneList.size(); i++) {
                    choices = choices + arrayChoicesOneList.get(i);
                }
            }
            if (arrayChoicesList.size() != 0) {
                for (int i = 0; i < arrayChoicesList.size(); i++) {
                    choices = choices + arrayChoicesList.get(i) + ",";
                }
            }
            if (arrayCauseList.size() != 0) {
                for (int i = 0; i < arrayCauseList.size(); i++) {
                    choices = choices + arrayCauseList.get(i).getName() + ",";
                }
            }
            if (arrayTagList.size() != 0) {
                for (int i = 0; i < arrayTagList.size(); i++) {
                    choices = choices + arrayTagList.get(i).getZhName() + ",";
                }
            }
            if (choices.endsWith(","))
                choices = choices.substring(0,choices.length() - 1);
            choosePresenter.getSelect(choices,content);
            return;
        }
        if (LawPushApp.getVoiceListeningType()==2){
            LawPushApp.setMultipleValue(0);
            endBack(content);
        }
    }

    //select接口识别错误，重新调取cause接口
    public void endBack(String content){
        Intent intent = new Intent();
        intent.putExtra("value",content);
        setResult(11, intent);
        finish();
    }

    /**
     * 匹配选项
     * @param obj
     */
    public void checkItem(String obj){
        //select接口识别
        Intent intent = new Intent();

        if (isOne){
            if (obj.contains(",")){
                obj = obj.split(",")[0];
            }
        }

        if (arrayChoicesOneList.size() != 0){
            for (int i = 0; i < arrayChoicesOneList.size() ;i++) {
                if (arrayChoicesOneList.get(i).equals(obj)){
                    intent.putExtra("arrayTag",arrayChoicesOneList.get(i).toString());
                    break;
                }
            }
        }
        if (arrayChoicesList.size() != 0){
            for (int i = 0; i < arrayChoicesList.size() ;i++) {
                if (arrayChoicesList.get(i).equals(obj)){
                    intent.putExtra("arrayTag",arrayChoicesList.get(i).toString());
                    break;
                }
            }
        }
        if (arrayCauseList.size() != 0){
            for (int i = 0; i < arrayCauseList.size() ;i++) {
                if (arrayCauseList.get(i).getName().equals(obj)){
                    intent.putExtra("arrayTag",arrayCauseList.toString());
                    break;
                }
            }
        }
        if (arrayTagList.size() != 0){
            for (int i = 0; i < arrayTagList.size() ;i++) {
                if (arrayTagList.get(i).getZhName().equals(obj)){
                    intent.putExtra("arrayTag",arrayTagList.toString());
                    break;
                }
            }
        }
        setResult(12, intent);
        finish();
    }
}
