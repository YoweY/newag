package com.info.aegis.lawpush4android.view.iml;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.MsgTagBean;
import com.info.aegis.lawpush4android.model.bean.entity.law.MsgCauseBean;
import com.info.aegis.lawpush4android.presenter.DialogPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.DialogActivityService;
import com.info.aegis.lawpush4android.view.handler.DialogHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/2/22.
 * Description :
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class DialogActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, DialogActivityService {

    private ImageView mGifImg;
    private TextView mSelect;
    private TextView mTitle;
    public ListView mListView;
    private TextView mNo;
    private TextView mYes;
    private MyCountDown time;
    private mySelectAdapter adapter;
    boolean[] isSelect = null;
    private ImageView mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg,voiceBgImageView,mVoiceBgMicImageView;
    private AnimationDrawable mVoiceAnimationDrawable;
    private RelativeLayout mVoiceRelay;
    /**
     * 是否是单选
     */
    boolean isOne = false;

    private ArrayList<MsgTagBean> selectTagArray = new ArrayList<>();
    private ArrayList<MsgCauseBean> selectCauseArray = new ArrayList<>();
    private ArrayList<String> selectChoicesArray = new ArrayList<>();
    List<MsgTagBean> arrayTagList = new ArrayList<>();
    List<MsgCauseBean> arrayCauseList = new ArrayList<>();
    ArrayList<String> choicesStringList = new ArrayList<>();
    private JSONArray jsonArray;
 //   Integer[] mImg = {R.drawable.jqr, R.drawable.jqr3};
 //   private Random random;
 //   private int m;
    private String title;
    public ArrayList<Msg> msgList;
    private DialogPresenter dialogPresenter;
    String motionValue = "",speakContent = "";

    private List<String> compareStringList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);
        AppManager.getAppManager().addActivity(DialogActivity.this);
        dialogPresenter = new DialogPresenter(DialogActivity.this);
        handler = new DialogHandler(DialogActivity.this);

        time = new MyCountDown(30000, 1000);
        time.start();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        isOne = intent.getBooleanExtra("isOne", false);
        String type = intent.getStringExtra("type");

        speakContent = speakContent + title;

        initView();
        initMsg();

        if (intent.getStringExtra("motion") != null && !intent.getStringExtra("motion").equals("")){
             motionValue = intent.getStringExtra("motion");
        }
        if (isOne && type.equals("choices")) {
            //单选choices选项
            choicesStringList = intent.getStringArrayListExtra("tagArray");
            for (int i = 0; i < choicesStringList.size(); i++) {
                selectChoicesArray.add(i, null);
                speakContent = speakContent + ","+(i+1)+choicesStringList.get(i);
                compareStringList.add(choicesStringList.get(i));
            }
        }
        if (isOne && type.equals("cause")) {
            String value = "{value:" + intent.getStringExtra("tagArray") + "}";
            //单选案由
            try {
                JSONObject jsonObject = new JSONObject(value);
                jsonArray = jsonObject.getJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    selectCauseArray.add(i, null);

                    MsgCauseBean msgCauseBean = new MsgCauseBean();
                    msgCauseBean.setName(jsonArray.getJSONObject(i).getString("name"));
                    msgCauseBean.setValue(jsonArray.getJSONObject(i).getString("value"));

                    arrayCauseList.add(msgCauseBean);

                    speakContent = speakContent +","+(i+1)+msgCauseBean.getName();
                    compareStringList.add(msgCauseBean.getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!isOne && type.equals("tag")) {
            String value = "{value:" + intent.getStringExtra("tagArray") + "}";
            //多选tag
            try {
                JSONObject jsonObject = new JSONObject(value);
                jsonArray = jsonObject.getJSONArray("value");
                for (int i = 0; i < jsonArray.length(); i++) {
                    selectTagArray.add(i, null);

                    MsgTagBean msgTagBean = new MsgTagBean();
                    msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                    msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                    msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                    arrayTagList.add(msgTagBean);
                    speakContent = speakContent +","+(i+1)+msgTagBean.getZhName();
                    compareStringList.add(msgTagBean.getZhName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mTitle.setText("       " + title);
        adapter = new mySelectAdapter(arrayTagList, arrayCauseList, choicesStringList, this);
        mListView.setAdapter(adapter);

        if (arrayCauseList.size() == 0) {
            isSelect = new boolean[arrayTagList.size()];
            for (int i = 0; i < arrayTagList.size(); i++) {
                isSelect[i] = false;
            }
        } else {
            isSelect = new boolean[arrayCauseList.size()];
            for (int i = 0; i < arrayCauseList.size(); i++) {
                isSelect[i] = false;
            }
        }
        Log.e("lawPush","DialogActivity-----onCreate");
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

        LawPushApp.setCurrntActivity("DialogActivity");
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
        Log.e("lawPush","DialogActivity-----onResume");
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
        AppManager.getAppManager().removeActivityStack(DialogActivity.this);
        Log.e("lawPush","DialogActivity-----onPause");
        finish();
    }

    private void initMsg() {
        if (msgList == null) {
            msgList = new ArrayList<>();
        } else {
            msgList.clear();
        }
    }

    /**
     * 初始化视图
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        mGifImg = (ImageView) findViewById(R.id.chat_dialog_img);
        mSelect = (TextView) findViewById(R.id.chat_dialog_select);
        mTitle = (TextView) findViewById(R.id.chat_dialog_title);
        mListView = (ListView) findViewById(R.id.chat_dialog_listView);
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

        mNo.setOnClickListener(this);
        mYes.setOnClickListener(this);

        if (isOne) {
            mSelect.setText("单选");
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        } else {
            mSelect.setText("多选");
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        mVoiceRelay.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setScrollbarFadingEnabled(true);
        mListView.setScrollBarFadeDuration(1000);

//        random = new Random();
//        m = random.nextInt(mImg.length);

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
            case R.id.chat_dialog_no:
                Intent noIntent = new Intent();
                noIntent.putExtra("value", false);
                LawPushApp.setDoubleAnswerDoctext("");
                setResult(RESULT_OK, noIntent);
                finish();
                break;
            case R.id.chat_dialog_yes:

                deleteNull();
                Intent intent = new Intent();
                if (arrayTagList.size() != 0) {
                    if (selectTagArray.size() == 0)
                        intent.putExtra("value", false);
                    else
                        intent.putExtra("value", true);
                    intent.putExtra("arrayTag", selectTagArray.toString());
                }
                if (arrayCauseList.size() != 0) {
                    if (selectCauseArray.size() == 0)
                        intent.putExtra("value", false);
                    else
                        intent.putExtra("value", true);
                    intent.putExtra("arrayTag", selectCauseArray.toString());
                }
                if (choicesStringList.size() != 0) {
                    if (selectChoicesArray.size() == 0)
                        intent.putExtra("value", false);
                    else
                        intent.putExtra("value", true);
                    intent.putStringArrayListExtra("arrayTag", selectChoicesArray);
                }
                if (!LawPushApp.getDoubleAnswerDoctext().equals("")){
                    setResult(110, intent);
                }else {
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.chat_dialog_select:
                isOne = !isOne;
                break;
            case R.id.mVoiceRelativeLayout:
//                stopAnimation();
                startFrontVoiceRecognizeListening();
                break;
        }
    }

    private Context dialogActivity;

    @Override
    public Context getContext() {
        if (dialogActivity == null) {
            dialogActivity = DialogActivity.this;
        }
        return dialogActivity;
    }

    @Override
    public void setType(int type) {
    }

    /**
     * 定时器
     */
    protected class MyCountDown extends CountDownTimer {

         MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {


            mYes.setText("确认选择(" + millisUntilFinished / 1000 + "s)");

        }

        @Override
        public void onFinish() {
            deleteNull();
            Intent intent = new Intent();
            if (arrayTagList.size() != 0) {
                if (selectTagArray.size() == 0)
                    intent.putExtra("value", false);
                else
                    intent.putExtra("value", true);
                intent.putExtra("arrayTag", selectTagArray.toString());
            }
            if (arrayCauseList.size() != 0) {
                if (selectCauseArray.size() == 0)
                    intent.putExtra("value", false);
                else
                    intent.putExtra("value", true);
                intent.putExtra("arrayTag", selectCauseArray.toString());
            }
            if (choicesStringList.size() != 0){
                if (selectChoicesArray.size() == 0)
                    intent.putExtra("value", false);
                else
                    intent.putExtra("value", true);
                intent.putStringArrayListExtra("arrayTag", selectChoicesArray);
            }
            if (!LawPushApp.getDoubleAnswerDoctext().equals("")){
                setResult(110, intent);
            }else{
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }


    private void deleteNull() {

        int arraySize = selectTagArray.size();
        for (int i = 0; i < arraySize; i++) {
            MsgTagBean msgTagBean = selectTagArray.get(i);
            if (msgTagBean == null) {
                selectTagArray.remove(i);
                i--;
                arraySize--;
            }
        }

        int arraySize2 = selectCauseArray.size();
        for (int i = 0; i < arraySize2; i++) {
            MsgCauseBean msgCauseBean = selectCauseArray.get(i);
            if (msgCauseBean == null) {
                selectCauseArray.remove(i);
                i--;
                arraySize2--;
            }
        }

        int arraySize3 = selectChoicesArray.size();
        for (int i = 0; i < arraySize3; i++) {
            String string = selectChoicesArray.get(i);
            if (string == null) {
                selectChoicesArray.remove(i);
                i--;
                arraySize3--;
            }
        }
    }

    class mySelectAdapter extends BaseAdapter {
        private int selectedPosition = -1;// 选中的位置
        private List<MsgTagBean> msgTagBeanList;
        private List<MsgCauseBean> msgCauseBeanList;
        private List<String> choicesStringList;
        private Context ctx;


         mySelectAdapter(List<MsgTagBean> msgTagBeanList, List<MsgCauseBean> msgCauseBeanList, List<String> choicesStringList, Context ctx) {
            this.msgTagBeanList = msgTagBeanList;
            this.msgCauseBeanList = msgCauseBeanList;
            this.choicesStringList = choicesStringList;
            this.ctx = ctx;
        }

         void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        @Override
        public int getCount() {
            if (msgCauseBeanList.size() != 0)
                return msgCauseBeanList.size();
            if (msgTagBeanList.size() != 0)
                return msgTagBeanList.size();
            if (choicesStringList.size() != 0)
                return choicesStringList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (msgCauseBeanList.size() != 0)
                return msgCauseBeanList.get(position);
            if (msgTagBeanList.size() != 0)
                return msgTagBeanList.get(position);
            if (choicesStringList.size() != 0)
                return choicesStringList.size();
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            SelectHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(ctx, R.layout.item_chatdialog, null);
                holder = new SelectHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (SelectHolder) convertView.getTag();
            }
            String dialogDatas = "";
            if (msgCauseBeanList.size() != 0)
                dialogDatas = msgCauseBeanList.get(position).getName();
            if (msgTagBeanList.size() != 0)
                dialogDatas = msgTagBeanList.get(position).getZhName();
            if (choicesStringList.size() != 0)
                dialogDatas = choicesStringList.get(position);

            holder.mPosition.setText(position + 1 + "");
            holder.mContent.setText(dialogDatas);
            if (isOne) {
                if (position % 2 == 0) {
                    if (selectedPosition == position) {
                        convertView.setSelected(true);
                        convertView.setPressed(true);
                        holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_yes));
                        holder.mPosition.setTextColor(getResources().getColor(R.color.white));
                        holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen1);
                    holder.mLayout.setBackgroundResource(R.mipmap.yixuanbeij);
                 //       holder.mLayout.setBackgroundResource(R.drawable.bg);

                        if (msgCauseBeanList.size() != 0)
                            selectCauseArray.set(0, arrayCauseList.get(position));
                        if (choicesStringList.size() != 0)
                            selectChoicesArray.set(0, choicesStringList.get(position));
                    } else {
                        convertView.setSelected(false);
                        convertView.setPressed(false);
                        holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_no));
                        holder.mPosition.setTextColor(getResources().getColor(R.color.dialog_choose_yes));
                        holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen2);
                        holder.mLayout.setBackgroundResource(R.color.Tra);
                    }
                } else {
                    if (selectedPosition == position) {
                        convertView.setSelected(true);
                        convertView.setPressed(true);
                        holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_yes));
                        holder.mPosition.setTextColor(getResources().getColor(R.color.white));
                        holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen1);
                        holder.mLayout.setBackgroundResource(R.mipmap.yixuanbeij);
                        if (msgCauseBeanList.size() != 0)
                            selectCauseArray.set(0, arrayCauseList.get(position));
                        if (choicesStringList.size() != 0)
                            selectChoicesArray.set(0, choicesStringList.get(position));
                    } else {
                        convertView.setSelected(false);
                        convertView.setPressed(false);
                        holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_no));
                        holder.mPosition.setTextColor(getResources().getColor(R.color.dialog_choose_yes));
                        holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen2);
                        holder.mLayout.setBackgroundResource(R.color.Tra);
                    }
                }
            } else {
                if (mListView.isItemChecked(position)) {
                    holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_yes));
                    holder.mPosition.setTextColor(getResources().getColor(R.color.white));
                    holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen1);
                    holder.mLayout.setBackgroundResource(R.mipmap.yixuanbeij);
                    selectTagArray.set(position, arrayTagList.get(position));
                } else {
                    holder.mContent.setTextColor(getResources().getColor(R.color.dialog_select_no));
                    holder.mPosition.setTextColor(getResources().getColor(R.color.dialog_choose_yes));
                    holder.mPosition.setBackgroundResource(R.mipmap.shuzidiwen2);
                    holder.mLayout.setBackgroundResource(R.color.Tra);
                    selectTagArray.set(position, null);
                }
            }


            return convertView;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isOne){
            adapter.setSelectedPosition(position);
            adapter.notifyDataSetInvalidated();
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    class SelectHolder {
         TextView mPosition, mContent;
         LinearLayout mLayout;

         SelectHolder(View convertView) {
            this.mPosition = (TextView) convertView.findViewById(R.id.item_chatDialog_position);
            this.mContent = (TextView) convertView.findViewById(R.id.item_chatDialog_content);
            this.mLayout = (LinearLayout) convertView.findViewById(R.id.item_chatDialog_layout);
        }
    }

    @Override
    public void sendToServer(String content) {
        boolean isRight = false;
        float value = 0;
        for (int i = 0; i < compareStringList.size(); i++) {
            value = Constant.getSimilarityRatio(content,compareStringList.get(i));
            if (content.contains(compareStringList.get(i)))
                value = 1;
            if (value > 0.4){
                isRight = true;
            }
        }

        if (isRight || content.contains("个") || content.contains("第")){
            String choices = "";
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
            if (choicesStringList.size() != 0) {
                for (int i = 0; i < choicesStringList.size(); i++) {
                    choices = choices + choicesStringList.get(i) + ",";
                }
            }
            if (choices.endsWith(","))
                choices = choices.substring(0,choices.length() - 1);
            dialogPresenter.getSelect(choices,content);
            return;
        }
        if (LawPushApp.getVoiceListeningType()==2) {
            LawPushApp.setMultipleValue(0);
            endBack(content);
        }
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
        MyLog.e("lawPush","obj---"+obj);

        if (arrayCauseList.size() != 0){
            for (int i = 0; i < arrayCauseList.size(); i++) {
                if (arrayCauseList.get(i).getName().equals(obj)){
                    selectCauseArray.set(0,arrayCauseList.get(i));
                    break;
                }
            }
            deleteNull();
            intent.putExtra("arrayTag", selectCauseArray.toString());
        }
        if (arrayTagList.size() != 0){
            for (int i = 0; i < arrayTagList.size(); i++) {
                if (arrayTagList.get(i).getZhName().equals(obj)){
                    selectTagArray.set(0,arrayTagList.get(i));
                    break;
                }
            }
            deleteNull();
            intent.putExtra("arrayTag", selectTagArray.toString());
        }
        if (choicesStringList.size() != 0){
            for (int i = 0; i < choicesStringList.size(); i++) {
                if (choicesStringList.get(i).equals(obj)){
                    selectChoicesArray.set(0,choicesStringList.get(i));
                    break;
                }
            }
            deleteNull();
            intent.putStringArrayListExtra("arrayTag", selectChoicesArray);
        }
        if (!LawPushApp.getDoubleAnswerDoctext().equals("")){
            setResult(110, intent);
        }else{
            setResult(12, intent);
        }
        finish();
    }

    //select接口识别错误，重新调取cause接口
    public void endBack(String content){
        Intent intent = new Intent();
        intent.putExtra("value",content);
        if (!LawPushApp.getDoubleAnswerDoctext().equals("")){
            setResult(110, intent);
        }else{
            setResult(11, intent);
        }
        finish();
    }
}
