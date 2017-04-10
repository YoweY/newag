package com.info.aegis.lawpush4android.view.iml;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.MsgTagBean;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.model.bean.entity.law.MsgCauseBean;
import com.info.aegis.lawpush4android.presenter.ChatPresenter;
import com.info.aegis.lawpush4android.presenter.adapter.ChatAdapter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.OnDoubleClickListener;
import com.info.aegis.lawpush4android.view.ChatActivityService;
import com.info.aegis.lawpush4android.view.handler.ChatHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aegis.aegisxunfei.MyLawPushApp;

import static com.info.aegis.lawpush4android.LawPushApp.USER;

public class ChatActivity extends BaseActivity implements ChatActivityService, View.OnClickListener ,View.OnTouchListener{

    public ListView msgListView;
    public ChatAdapter adapter;
    public ArrayList<Msg> msgList;
    private Context myContext;
    public ImageButton newSessionBtn, mHelpBtn;
    private ImageView voiceBgImageView, mVoiceBgMicImageView, mVoiceAnimationOutImg, mVoiceAnimationInImg, mVoiceAnimationMicImg;

    private ChatPresenter chatPresenter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    /**
     * rabbitMQ相关
     * https://www.cloudamqp.com/blog/2015-07-29-rabbitmq-on-android.html
     * http://blog.csdn.net/lqc1992/article/details/51746493
     */
    ConnectionFactory factoryShowMode = new ConnectionFactory();

    private int currentChatType;
    private static String listFirstMsgString = "您好，我是法律智能问答机器人---小法，请问有什么可以帮助您的吗？";

    private AnimationDrawable animationDrawable;
    private RelativeLayout mVoiceRelay;
    private Switch mSwitch;

    private TextView godSwitchText, godShowText;
    private LinearLayout mTop1, mTop2, mTop3, mTop4;
    String questionContent = "", sceneId = "";
    private TextView mChengxuText, mFaGuiText, mFaLvText, mChatText;
    int topSelectType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        AppManager.getAppManager().addActivity(ChatActivity.this);

        chatPresenter = new ChatPresenter(ChatActivity.this);
        handler = new ChatHandler(ChatActivity.this);
        myHandler = new MyHandler();//当前页面的

        initView();
        initEvent();

        //获取从导航页传递到过来的环境类型
        Intent intent = getIntent();
        currentChatType = intent.getIntExtra("chatType", 3);
        questionContent = intent.getStringExtra("questionContent");
        if (questionContent == null)
            questionContent = "";
        sceneId = intent.getStringExtra("sceneId");
        if (sceneId == null)
            sceneId = "";
        if (!questionContent.equals("")) {
            if (sceneId != null && !sceneId.equals("")) {
                chatPresenter.getSceneQuestion(questionContent, sceneId);
            } else {
                chatPresenter.getPredictCause(questionContent);
            }
        }
        sharedPreferences = getSharedPreferences(USER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //初始化数据
        initData();
        Log.e("lawPush", " chatActivity-----onCreate");
    }



    @Override
    public void onResume() {
        super.onResume();
        LawPushApp.setCurrntActivity("ChatActivity");
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msgList != null && msgList.size() == 1 && currentChatType != 0) {
                    startSpeak(listFirstMsgString);
                } else {
                    startSpeak("你好");
                }
            }
        }).start();
        if (LawPushApp.isGodModeStartIng()) {
            godShowText.setVisibility(View.VISIBLE);
            godMode();
            showMode();
        } else {
            godShowText.setVisibility(View.GONE);
        }

        if (LawPushApp.isNeedContinueListen()) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }
        Log.e("lawPush", " chatActivity-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        Constant.stopCountTime();
        hideGuideDialog();
        if (threadPoolExecutorTest != null)
            threadPoolExecutorTest.shutdownNow();
        if (threadPoolExecutorHuman != null)
            threadPoolExecutorHuman.shutdownNow();
        if (showModeThreadPoolExecute != null)
            showModeThreadPoolExecute.shutdownNow();
        if (godModeThreadPoolExecute != null)
            godModeThreadPoolExecute.shutdownNow();
        Log.e("lawPush", " chatActivity-----onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lawPush", " chatActivity-----onStop");
    }

    /**
     * 返回事件捕捉
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Log.e("lawPush", " chatActivity-----onBackPressed");
    }

    @Override
    public Context getContext() {
        if (myContext == null) {
            myContext = ChatActivity.this;
        }
        Log.e("lawPush", " chatActivity-----getContext");
        return myContext;
    }


    void startUpVideo(){
        // step 1
    }

    @Override
    public void onClick(View view) {
        Constant.stopCountTime();
        stopSpeak();
        topSelectType = 0;
        switch (view.getId()) {
            case R.id.mVoiceRelativeLayout:
                startFrontVoiceRecognizeListening();
                break;
            case R.id.newSession:
                LawPushApp.setChatChatSaveIngTimes(0);
                resetData();
                break;
            case R.id.help:
                Intent intent = new Intent(myContext, HelpActivity.class);
                startActivity(intent);
//            sendToServer("让我们测试一下。");
//                chatPresenter.getExample("婚后我找我姐借钱买的房子，离婚的话，他不还咋办？");
//                startUpVideo();
                break;
            case R.id.chatLL1:
                //程序引导
                setTopSelectBg(1);
                resetData();
                break;
            case R.id.chatLL2:
                //法规查询
                setTopSelectBg(5);
                resetData();
                break;
            case R.id.chatLL3:
                //法律问答
                setTopSelectBg(4);
                resetData();
                break;
            case R.id.chatLL4:
                //随便聊聊
                resetData();
                setTopSelectBg(3);
                break;
        }
        Log.e("lawPush", " chatActivity-----onClick");
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
        Log.e("lawPush", " chatActivity-----onTouch");
        return false;
    }

    /**
     * 启动动画
     */
    public void startAnimation() {
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
        animationDrawable.start();
        Log.e("lawPush", " chatActivity-----startAnimation");
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
        Log.e("lawPush", " chatActivity-----stopAnimation");
    }

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        showOnChatUI(content);
        //终止上帝模式中的计时
        Constant.stopCountTime();
        //如果处于上帝模式，不响应语音请求
        if (!LawPushApp.isGodModeStartIng()) {
            //先判断本地
            if (content.matches(".{0,5}程序.{0,5}")){
                topSelectType = 0;
                resetData();
                setTopSelectBg(1);
                return;
            }
            if (content.matches(".{0,5}聊.{0,5}")){
                topSelectType = 0;
                resetData();
                setTopSelectBg(3);
                return;
            }
            if (content.matches(".{0,5}((法律咨询)|(法律问题)|(法律援助)|(法律问答)).{0,5}") || content.matches(".{0,3}咨询.{0,3}")){
                topSelectType = 0;
                resetData();
                setTopSelectBg(4);
                return;
            }
            if (content.matches(".{0,5}法.?查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}") || content.matches(".{0,5}法.{1,3}法.{1,3}")){
                topSelectType = 0;
                resetData();
                setTopSelectBg(5);
                return;
            }
            //
            String choices = "";
            if (LawPushApp.getScenesBeanList().size() != 0) {
                for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
                    choices = choices + LawPushApp.getScenesBeanList().get(i).getName() + ",";
                }
            }
            if (choices.endsWith(","))
                choices = choices.substring(0, choices.length() - 1);
            chatPresenter.getSelect(choices, content);
            hideGuideDialog();
        } else {
            Constant.startCountTime(ChatActivity.this, adapter, msgList);
        }
        Log.e("lawPush", " chatActivity-----sendToServer");
    }

    /**
     * select接口返回了标准的内容
     * @param content
     */
    public void checkItem(String content) {
        ScenesBean scenesBean = new ScenesBean();
        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
            if (LawPushApp.getScenesBeanList().get(i).getName().equals(content)) {
                scenesBean = LawPushApp.getScenesBeanList().get(i);
                break;
            }
        }
        chatPresenter.getSceneQuestion(scenesBean.getName(),scenesBean.getId());
    }

    /**
     * select接口中没有返回标准内容，重新判断
     * @param content
     */
    public void rePreCause(String content) {
        //判断是否是语音命令切换场景
        judgeCurrentActivityChatType(content);
        //查看咨询报告
        if (content.contains("咨询报告") || content.contains("详细内容")) {
            if (content.length() < 15) {
                Intent intent = new Intent(ChatActivity.this, DataDetailActivity.class);
                startActivity(intent);
                return;
            }
        }

        if (content.contains("参考答案")) {
            if (content.length() < 15) {
                boolean temp = false;
                for (int i = msgList.size() - 1; i >= 0; i--) {
                    Msg msg = msgList.get(i);
                    if (msg.getType() == Msg.PROGRAMER_QUESTION) {
                        Intent intent = new Intent(ChatActivity.this, LawDetailActivity.class);
                        intent.putExtra("type", 5);
                        intent.putExtra("pq_content", msg.getProgramQuestionAnswer());
                        startActivity(intent);
                        temp = true;
                        break;
                    }
                }
                if (temp)
                    return;
            }
        }

        if (content.contains("相关法律") || content.contains("法律法规")) {
            if (content.length() < 15) {
                boolean temp = false;
                for (int i = msgList.size() - 1; i >= 0; i--) {
                    Msg msg = msgList.get(i);
                    if (msg.getType() == Msg.TYPE_RESULT) {
                        if (msg.getLaw() != null) {
                            Law law = msg.getLaw();
                            skipToLawDetail(law.getId(), law.getTitle(), Constant.LAW_INFO);
                            temp = true;
                            break;
                        }
                    }
                    if (msg.getType() == Msg.PROGRAMER_QUESTION) {
                        if (msg.getLawList().size() != 0) {
                            if (msg.getLawList().get(0).getContent() != null) {
                                Law law = msg.getLawList().get(0);
                                skipToLawDetail(law.getId(), law.getTitle(), Constant.LAW_INFO);
                                temp = true;
                                break;
                            }
                        }
                    }
                }
                if (temp)
                    return;
            }
        }
        if (content.contains("司法观点")) {
            //进入司法观点
            boolean temp = false;
            for (int i = msgList.size() - 1; i >= 0; i--) {
                Msg msg = msgList.get(i);
                if (msg.getType() == Msg.TYPE_RESULT) {
                    if (msg.getPoint() != null) {
                        JudicialPoint judicialPoint = msg.getPoint();
                        skipToLawDetail(judicialPoint.getId(), judicialPoint.getTitle(), Constant.LAW_POINTS);
                        temp = true;
                        break;
                    }
                }
            }
            if (temp)
                return;
        }
        if (content.contains("指导案例") || content.contains("相关案例") || content.contains("精选案例")) {
            //进入指导案例
            boolean temp = false;
            for (int i = msgList.size() - 1; i >= 0; i--) {
                Msg msg = msgList.get(i);
                if (msg.getType() == Msg.TYPE_RESULT) {
                    if (msg.getGuidanceCase() != null) {
                        GuidanceCase guidanceCase = msg.getGuidanceCase();
                        skipToLawDetail(guidanceCase.getId(), guidanceCase.getTitle(), Constant.LAW_GUIDE_CASE);
                        temp = true;
                        break;
                    }
                }
            }
            if (temp)
                return;
        }

        //判断是否处于多轮问答模式中
        if (LawPushApp.getMultipleValue() == 0)
            chatPresenter.getPredictCause(content);
        else if (LawPushApp.getMultipleValue() == 1)
            chatPresenter.getMultiple(LawPushApp.getResultId(), content);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("lawPush", " chatActivity-----onActivityResult");
        if (!LawPushApp.getDoubleAnswerDoctext().equals("")){
            Msg msg = new Msg();
            msg.setContent(LawPushApp.getDoubleAnswerDoctext());
            msg.setChoiceMsgs(LawPushApp.getDoubleAnswerChoice());
            msg.setType(Msg.SIMILAR_CAUSE_TYPE);
            msg.setSIMILAR_QUESTION_TYPE(3);
            msg.setResultId(LawPushApp.getResultId());
            msgList.add(msg);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgList.size() -2);

        }

        Log.e("lawPush", " chatActivity-----onActivityResult--result code:"+resultCode);
        if (resultCode == 0){
            if (LawPushApp.getDoubleAnswerChoice() != null){
                List<String> stringList = new ArrayList<>();
                for (int i = 0; i < LawPushApp.getDoubleAnswerChoice().length; i++) {
                    stringList.add(LawPushApp.getDoubleAnswerChoice()[i].getContent());
                }
                dealDialogSimilarCauseResult(stringList);
                LawPushApp.setDoubleAnswerDoctext("");
                LawPushApp.setDoubleAnswerChoice(null);
                return;
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 13) {
                //2个tag多选返回结果
                if (data.getBooleanExtra("value", false)) {
                    //接着请求
                    String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        JSONArray jsonArray = jsonObject.getJSONArray("value");
                        List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MsgTagBean msgTagBean = new MsgTagBean();
                            msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                            msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                            msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                            msgTagBeanList.add(msgTagBean);
                        }
                        dealDialogMoreTagResult(msgTagBeanList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //结束
                    return;
                }
            }
            if (requestCode == 10) {
                //多个tag多选返回结果
                if (data.getBooleanExtra("value", false)) {
                    //接着请求
                    String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        JSONArray jsonArray = jsonObject.getJSONArray("value");
                        List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MsgTagBean msgTagBean = new MsgTagBean();
                            msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                            msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                            msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                            msgTagBeanList.add(msgTagBean);
                        }
                        dealDialogMoreTagResult(msgTagBeanList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //结束
                    return;
                }
            }
            if (requestCode == 12) {
                //2个案由单选返回的结果
                if (data.getBooleanExtra("value", false)) {
                    //接着请求
                    String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                    try {
                        List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(value).getJSONObject("value");
                        MsgCauseBean msgCauseBean = new MsgCauseBean();
                        msgCauseBean.setName(jsonObject.getString("name"));
                        msgCauseBean.setValue(jsonObject.optInt("value") + "");
                        msgCauseBeanList.add(msgCauseBean);
                        dealDialogCauseResult(msgCauseBeanList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("lawPush", "   解析异常：" + e.toString());
                    }

                } else {
                    //结束
                    return;
                }
            }
            if (requestCode == 11) {
                //多个案由单选返回的结果
                if (data.getBooleanExtra("value", false)) {
                    //接着请求
                    String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        JSONArray jsonArray = jsonObject.getJSONArray("value");
                        List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MsgCauseBean msgCauseBean = new MsgCauseBean();
                            msgCauseBean.setName(jsonArray.getJSONObject(i).getString("name"));
                            msgCauseBean.setValue(jsonArray.getJSONObject(i).optInt("value") + "");
                            msgCauseBeanList.add(msgCauseBean);
                        }
                        dealDialogCauseResult(msgCauseBeanList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //结束
                    return;
                }
            }
            if (requestCode == 14) {
                //多个choices单选返回的结果
                if (data.getBooleanExtra("value", false)) {
                    List<String> stringList = data.getStringArrayListExtra("arrayTag");
                    dealDialogSimilarCauseResult(stringList);
                } else {
                    //结束
                    LawPushApp.setMultipleValue(0);
                    return;
                }
            }
            if (requestCode == 15) {
                //2个choices单选返回的结果
                if (data.getBooleanExtra("value", false)) {
                    String string = data.getStringExtra("arrayTag");
                    List<String> stringList = new ArrayList<>();
                    stringList.add(string);
                    dealDialogSimilarCauseResult(stringList);
                } else {
                    //结束
                    LawPushApp.setMultipleValue(0);
                    return;
                }
            }
            if (requestCode == 16) {
                //1个choices单选返回的结果
                if (data.getBooleanExtra("value", false)) {
                    String string = data.getStringExtra("arrayTag");
                    List<String> stringList = new ArrayList<>();
                    stringList.add(string);
                    dealDialogSimilarCauseResult(stringList);
                } else {
                    //结束
                    LawPushApp.setMultipleValue(0);
                    return;
                }
            }
        }

        if (resultCode == 20) {
            //标准实体问题界面返回的，重新调取cause接口
            String content = data.getStringExtra("value");
            showOnChatUI(content);
            chatPresenter.getPredictCause(content);
        }


        if (resultCode == 11) {
            //DialogActivity和chooseActivity中调取select接口返回值有问题，此处重新调取cause接口
            String content = data.getStringExtra("value");
            showOnChatUI(content);
            chatPresenter.getPredictCause(content);
        }


        if (resultCode == 12) {
            //DialogActivity中调取select接口返回值
            if (requestCode == 14) {
                //多个choices单选返回的结果
                List<String> stringList = data.getStringArrayListExtra("arrayTag");
                dealDialogSimilarCauseResult(stringList);
            }
            if (requestCode == 11) {
                //多个案由单选返回的结果
                //接着请求
                String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");
                    List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MsgCauseBean msgCauseBean = new MsgCauseBean();
                        msgCauseBean.setName(jsonArray.getJSONObject(i).getString("name"));
                        msgCauseBean.setValue(jsonArray.getJSONObject(i).getString("value"));
                        msgCauseBeanList.add(msgCauseBean);
                    }
                    dealDialogCauseResult(msgCauseBeanList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 10) {
                //多个tag多选返回结果
                //接着请求
                String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");
                    List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MsgTagBean msgTagBean = new MsgTagBean();
                        msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                        msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                        msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                        msgTagBeanList.add(msgTagBean);
                    }
                    dealDialogMoreTagResult(msgTagBeanList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 16) {
                //1个choices单选返回的结果
                String string = data.getStringExtra("arrayTag");
                List<String> stringList = new ArrayList<>();
                stringList.add(string);
                dealDialogSimilarCauseResult(stringList);
            }
            if (requestCode == 15) {
                //2个choices单选返回的结果
                String string = data.getStringExtra("arrayTag");
                List<String> stringList = new ArrayList<>();
                stringList.add(string);
                dealDialogSimilarCauseResult(stringList);
            }
            if (requestCode == 12) {
                //2个案由单选返回的结果
                //接着请求
                String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                try {
                    List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(value);
                    MsgCauseBean msgCauseBean = new MsgCauseBean();
                    msgCauseBean.setName(jsonObject.getString("name"));
                    msgCauseBean.setValue(jsonObject.getString("value"));
                    msgCauseBeanList.add(msgCauseBean);
                    dealDialogCauseResult(msgCauseBeanList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 13) {
                //2个tag多选返回结果
                //接着请求
                String value = "{value:" + data.getStringExtra("arrayTag") + "}";
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");
                    List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MsgTagBean msgTagBean = new MsgTagBean();
                        msgTagBean.setEnName(jsonArray.getJSONObject(i).getString("enName"));
                        msgTagBean.setType(jsonArray.getJSONObject(i).getInt("type"));
                        msgTagBean.setZhName(jsonArray.getJSONObject(i).getString("zhName"));
                        msgTagBeanList.add(msgTagBean);
                    }
                    dealDialogMoreTagResult(msgTagBeanList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void showOnChatUI(String result) {
        super.showOnChatUI(result);
        Msg msg = new Msg(result, Msg.TYPE_SEND);
        msgList.add(msg);
        adapter.notifyDataSetChanged();
        msgListView.setSelection(msgList.size() - 1);
        Log.e("lawPush", " chatActivity-----showOnChatUI");
    }

    @Override
    public void showOnChatUIRobbot(String result) {
        super.showOnChatUIRobbot(result);
        Msg msg = new Msg(result, Msg.TYPE_RECEIVED);
        msgList.add(msg);
        adapter.notifyDataSetChanged();
        msgListView.setSelection(msgList.size() - 1);
        Log.e("lawPush", " chatActivity-----showOnChatUIRobbot");
    }

    @Override
    public void setType(int type) {
        Log.e("lawPush", " chatActivity-----setType");
    }

    @Override
    void showGuideDialog() {
        super.showGuideDialog();
        hideGuideDialog();
        createDialog("现在是上班时间，我不可以闲聊太久，请问有什么法律问题能帮到您的吗？");
        Log.e("lawPush", " chatActivity-----showGuideDialog");
    }

    @Override
    void hideGuideDialog() {
        super.hideGuideDialog();
        if (guideDialog != null) {
            guideDialog.dismiss();
            LawPushApp.setIsGuideDialogShow(false);
        }
        Log.e("lawPush", " chatActivity-----hideGuideDialog");
    }

    private void initData() {
        LawPushApp.setChatTypt(currentChatType);
        setupConnectionShowModeFactory();
        setTopSelectBg(currentChatType);
        initListViewMsgs();
    }

    /**
     * 初始化页面基础信息
     */
    public void initListViewMsgs() {

        if (msgList == null) {
            msgList = new ArrayList<>();
        } else {
            msgList.clear();
        }
        initListFirstMsg();

        Msg msg = new Msg(listFirstMsgString, Msg.TYPE_RECEIVED);
        msgList.add(msg);

        if (!questionContent.equals("")) {
            Msg msg1 = new Msg(questionContent, Msg.TYPE_SEND);
            msgList.add(msg1);
        }

        if (adapter == null) {
            adapter = new ChatAdapter(ChatActivity.this, msgList, chatPresenter, msgListView);
            msgListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    //重置当前页面所有的临时数据
    void resetData() {
        questionContent = "";
        //清除sp文件保存的数据
        if (editor != null) {
            editor.clear();
            editor.commit();
        }
        initListViewMsgs();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        if (chatPresenter != null)
            chatPresenter.getTagStack().removeAllElements();
    }

    String initListFirstMsg() {
        switch (LawPushApp.getChatTypt()) {
            case 1:
                listFirstMsgString = Constant.randomProgramQuestion();
                break;
            case 5:
                listFirstMsgString = Constant.randomLawInfo();
                break;
            case 4:
                listFirstMsgString = Constant.randomLawQA();
                break;
            case 3:
                listFirstMsgString = Constant.randomChatUI();
                break;
        }
        return listFirstMsgString;
    }

    private void initView() {
        godShowText = (TextView) findViewById(R.id.id_sosText);
        godSwitchText = (TextView) findViewById(R.id.id_set_god_mode);
        mSwitch = (Switch) findViewById(R.id.id_switch);
        mVoiceRelay = (RelativeLayout) findViewById(R.id.mVoiceRelativeLayout);
        voiceBgImageView = (ImageView) findViewById(R.id.voice);
        newSessionBtn = (ImageButton) findViewById(R.id.newSession);
        mHelpBtn = (ImageButton) findViewById(R.id.help);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        mVoiceAnimationOutImg = (ImageView) findViewById(R.id.animationIV);
        mVoiceAnimationInImg = (ImageView) findViewById(R.id.animationIV1);
        mVoiceAnimationMicImg = (ImageView) findViewById(R.id.animationIV2);
        animationDrawable = (AnimationDrawable) mVoiceAnimationMicImg.getDrawable();
        mVoiceBgMicImageView = (ImageView) findViewById(R.id.voiceBg);

        mTop1 = (LinearLayout) findViewById(R.id.chatLL1);
        mTop2 = (LinearLayout) findViewById(R.id.chatLL2);
        mTop3 = (LinearLayout) findViewById(R.id.chatLL3);
        mTop4 = (LinearLayout) findViewById(R.id.chatLL4);
        // 四大场景的字体
        mChengxuText = (TextView) findViewById(R.id.chengxu_text);
        mFaGuiText = (TextView) findViewById(R.id.fagui_text);
        mFaLvText = (TextView) findViewById(R.id.falv_Text);
        mChatText = (TextView) findViewById(R.id.chat_text);
    }

    private void initEvent() {

        mTop1.setOnClickListener(this);
        mTop2.setOnClickListener(this);
        mTop3.setOnClickListener(this);
        mTop4.setOnClickListener(this);
        voiceBgImageView.setOnClickListener(this);
        newSessionBtn.setOnClickListener(this);
        mHelpBtn.setOnClickListener(this);
        mVoiceRelay.setOnClickListener(this);

        //上帝模式开关
        godSwitchText.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                if (LawPushApp.isGodModeStartIng()) {
                    //关闭上帝模式
                    LawPushApp.setIsGodModeStartIng(false);
                    godShowText.setVisibility(View.INVISIBLE);
                    //关闭演示模式
                    try {
                        showModeChannel.close();
                        showModeConnection.close();
                        showModeThreadPoolExecute.shutdownNow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("lawPush", "         关闭演示模式报错");
                    }
                    //关闭上帝模式
                    try {
                        godModeChannel.close();
                        godModeConnection.close();
                        godModeThreadPoolExecute.shutdownNow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("lawPush", "         关闭上帝模式报错");
                    }
                } else {
                    //开启上帝模式
                    //开启上帝模式，实时监听服务端返回的消息，
                    // 此时，任何请求都不需要调取接口了，MQ队列中返回什么，则呈现什么
                    godShowText.setVisibility(View.VISIBLE);
                    LawPushApp.setIsGodModeStartIng(true);
                    godMode();
                    showMode();
                }
            }
        }));

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LawPushApp.setIsNeedContinueListen(true);
                    startAnimation();
                    startBackVoiceRecognizeListening();
                } else {
                    LawPushApp.setIsNeedContinueListen(false);
                    stopAnimation();
                    startVoiceWakeUp();
                }
            }
        });

        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (msgList.get(position).getType() == Msg.CHAT_CHAT) {
                    startSpeak(msgList.get(position).getContent());
                }
            }
        });

        msgListView.setOnTouchListener(this);


    }

    /**
     * 设置导航页的背景
     *
     * @param type
     */
    public void setTopSelectBg(int type) {
        switch (type) {
            case 5:
                //法规查询
                setTypeBgLawsRegulations();
                LawPushApp.setChatChatSaveIngTimes(0);
                break;
            case 4:
                //法律问答
                setTypeBgLawQuestion();
                LawPushApp.setChatChatSaveIngTimes(0);
                break;
            case 3:
                //随便俩聊
                setTypeBgChatChat();
                break;
            case 2:
                //程序指导
            case 1:
                setTypeBgProgram();
                LawPushApp.setChatChatSaveIngTimes(0);
                break;
            case 12:
                setTypeBgProgram();
                LawPushApp.setChatChatSaveIngTimes(0);
                break;
        }
        if (msgList != null) {
            if (topSelectType == 0)
                startSpeak(listFirstMsgString);
        }

    }

    /**
     * 设置随便聊聊背景
     */
    private void setTypeBgChatChat() {
        LawPushApp.setChatTypt(3);
        mTop1.setBackgroundResource(R.mipmap.chengxunormal);
        mTop2.setBackgroundResource(R.mipmap.faguinormal);
        mTop3.setBackgroundResource(R.mipmap.falvnormal);
        mTop4.setBackgroundResource(R.mipmap.chatselect2);
        mChatText.setTextColor(getResources().getColor(R.color.Chat_normal));
        mChengxuText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaGuiText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaLvText.setTextColor(getResources().getColor(R.color.Chat_select));
        listFirstMsgString = Constant.randomChatUI();
        if (msgList != null)
            msgList.get(0).setContent(listFirstMsgString);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 设置法律问答背景
     */
    private void setTypeBgLawQuestion() {
        LawPushApp.setChatTypt(4);
        mTop1.setBackgroundResource(R.mipmap.chengxunormal);
        mTop2.setBackgroundResource(R.mipmap.faguinormal);
        mTop3.setBackgroundResource(R.mipmap.falvselect);
        mTop4.setBackgroundResource(R.mipmap.chatnormal2);
        listFirstMsgString = Constant.randomLawQA();
        mChatText.setTextColor(getResources().getColor(R.color.Chat_select));
        mChengxuText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaGuiText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaLvText.setTextColor(getResources().getColor(R.color.Chat_normal));
        if (msgList != null)
            msgList.get(0).setContent(listFirstMsgString);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 设置法律法规背景
     */
    private void setTypeBgLawsRegulations() {
        LawPushApp.setChatTypt(5);
        mTop1.setBackgroundResource(R.mipmap.chengxunormal);
        mTop2.setBackgroundResource(R.mipmap.faguiselect);
        mTop3.setBackgroundResource(R.mipmap.falvnormal);
        mTop4.setBackgroundResource(R.mipmap.chatnormal2);
        listFirstMsgString = Constant.randomLawInfo();
        mChatText.setTextColor(getResources().getColor(R.color.Chat_select));
        mChengxuText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaGuiText.setTextColor(getResources().getColor(R.color.Chat_normal));
        mFaLvText.setTextColor(getResources().getColor(R.color.Chat_select));
        if (msgList != null)
            msgList.get(0).setContent(listFirstMsgString);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    /**
     * 设置程序引导背景
     */
    private void setTypeBgProgram() {
        LawPushApp.setChatTypt(1);
        mTop1.setBackgroundResource(R.mipmap.chengxuselect);
        mTop2.setBackgroundResource(R.mipmap.faguinormal);
        mTop3.setBackgroundResource(R.mipmap.falvnormal);
        mTop4.setBackgroundResource(R.mipmap.chatnormal2);
        listFirstMsgString = Constant.randomProgramQuestion();
        mChatText.setTextColor(getResources().getColor(R.color.Chat_select));
        mChengxuText.setTextColor(getResources().getColor(R.color.Chat_normal));
        mFaGuiText.setTextColor(getResources().getColor(R.color.Chat_select));
        mFaLvText.setTextColor(getResources().getColor(R.color.Chat_select));
        if (msgList != null)
            msgList.get(0).setContent(listFirstMsgString);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }


    /**
     * 语音 控制勾选框框
     *
     * @param options
     */
    public void modifyCheckBox(String[] options) {
        Msg lastMsg = null;
        int i = msgList.size() - 1;
        for (; i >= 0; i--) {
            if (msgList.get(i).getChoiceMsgs() != null) {
                lastMsg = msgList.get(i);
                break;
            }
        }
        //勾选相应的框框
        if (lastMsg != null) {
            for (Msg.ChoiceMsg choiceMsg : lastMsg.getChoiceMsgs()) {
                boolean isTrue = false;
                for (String option : options) {
                    if (choiceMsg.getContent().equals(option)) {
                        choiceMsg.setChecked(true);
                        isTrue = true;
                    }
                    if (isTrue)
                        break;;
                }
                if (isTrue)
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 使得列表显示到最后一项
     */
    public void more(Msg msg) {
        int i = msgList.indexOf(msg);
        if (i == msgList.size() - 1) {
            msgListView.setSelection(msgList.size());
        }
    }

    /**
     * 判断要跳转的类型
     * @param content
     */
    void judgeCurrentActivityChatType(String content) {
        if (content.matches(".{0,6}程序引导.{0,3}") || content.matches(".{0,5}我.{1}[(立案)|(起诉)].{0,3}")) {
            //程序引导
            topSelectType = 0;
            setTopSelectBg(1);
            resetData();
            return;
        }
        if (content.matches(".{0,5}法.{1}查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}") || content.matches(".{0,5}法.{1,3}法.{1,3}")) {
            //法规法规
            topSelectType = 0;
            setTopSelectBg(5);
            resetData();
            return;
        }
        if (content.contains("法律咨询") || content.contains("法律问题") || content.contains("法律援助") || content.matches(".{0,6}法律咨询") || content.contains("法律问答")
                || content.matches(".{0,6}咨询.{0,2}法律.{0,3}")) {
            topSelectType = 0;
            setTopSelectBg(4);
            resetData();
            return;
        }
        if (content.matches(".{0,7}聊.{1,5}")) {
            //随便聊聊
            topSelectType = 0;
            setTopSelectBg(3);
            resetData();
        }
    }

    /**
     * rabbitMQ设置连接参数
     */
    private void setupConnectionShowModeFactory() {
        factoryShowMode.setHost("180.96.11.68");
        factoryShowMode.setPort(5672);
        factoryShowMode.setUsername("aegis");
        factoryShowMode.setPassword("shield");
    }

    /**
     * 跳转到详情页
     */
    private void skipToLawDetail(String id, String name, int type) {
        Intent intent = new Intent(ChatActivity.this, LawDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void robbotRead(String content) {
        startSpeak(content);
    }

    //救场语处理
    public void saveSOS() {
        String contentValue = "不知道";
        SharedPreferences sharedPreferences = getSharedPreferences("saveSP", MODE_PRIVATE);
        String value;
        switch (LawPushApp.getChatTypt()) {
            case 1:
                value = "{value:" + sharedPreferences.getString("chengxuyindao", "") + "}";
                break;
            case 5:
                value = "{value:" + sharedPreferences.getString("falvfaguiArray", "") + "}";
                break;
            case 4:
                value = "{value:" + sharedPreferences.getString("falvwentiArray", "") + "}";
                break;
            case 3:
                value = "{value:" + sharedPreferences.getString("suibianliaoliao", "") + "}";
                break;
            default:
                value = "{value:" + sharedPreferences.getString("quanju", "") + "}";
        }
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            int length = jsonArray.length();
            Random random = new Random();
            int index = random.nextInt(length);
            JSONObject subObject = jsonArray.getJSONObject(index);
            contentValue = subObject.optString("content");

            String motion = "{motions:" + subObject.getJSONArray("motion").toString() + "}";
            robotPerformAction(new JSONObject(motion));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Msg msg = new Msg(contentValue, Msg.CHAT_CHAT);
        receiveMsg(msg);
        Constant.setGodTextContent(contentValue, 0, false, 0);
    }

    ExecutorService threadPoolExecutorHuman;
    /**
     * 原先的人工处理平台Human = 1
     */
    public void subscribe() {
        if (threadPoolExecutorHuman != null)
            threadPoolExecutorHuman.shutdownNow();
        threadPoolExecutorHuman = Executors.newFixedThreadPool(1);
        threadPoolExecutorHuman.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = factoryShowMode.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare("lawPush_androidChat", false, false, false, null);
                    //设置最大服务转发消息数量
                    int prefetchCount = 1;
                    channel.basicQos(prefetchCount);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    // 指定消费队列
                    boolean ack = false; //打开应答机制
                    channel.basicConsume("lawPush_androidChat", ack, consumer);
                    String qidString = ChatActivity.this.getSharedPreferences(LawPushApp.USER, Context.MODE_PRIVATE).getString("qid", "");
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        //取出id的值
                        JSONObject jsonObject = new JSONObject(message);
                        String id = jsonObject.getString("id");//服务器返回的qid值
                        if (id.equals(qidString)) {
                            //相等   接收处理
                            //发送应答
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                            chatPresenter.dealServerMessageRabbitMQ(jsonObject);
                            Constant.stopCountTime();
                            channel.close();
                            connection.close();
                            threadPoolExecutorHuman.shutdownNow();
                            break;
                        } else {
                            //发送拒绝
                            channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                            // 发送到消息队列尾部中
                            channel.basicPublish("", "lawPush_androidChat", null, message.getBytes("UTF-8"));
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e("lawPush", "rabbitMQ InterruptedException : " + e.getClass().getName());
                } catch (Exception e1) {
                    Log.e("lawPush", "rabbitMQ Exception : " + e1.getClass().getName());
                }
            }
        });
    }

    /**
     * 演示模式，实时上传聊天内容
     */
    Connection showModeConnection;
    Channel showModeChannel;
    public ExecutorService showModeThreadPoolExecute;

    public void showMode() {
        try {
            showModeChannel.close();
            showModeConnection.close();
            showModeThreadPoolExecute.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showModeThreadPoolExecute = Executors.newFixedThreadPool(1);
        showModeThreadPoolExecute.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    showModeConnection = factoryShowMode.newConnection();
                    showModeChannel = showModeConnection.createChannel();
                    showModeChannel.queueDeclare("lawPush_god_mode_human", false, false, false, null);
                    //设置最大服务转发消息数量
                    int prefetchCount = 1;
                    showModeChannel.basicQos(prefetchCount);
                    while (true) {
                        // 每隔一秒钟，从全局变量中取出聊天内容
                        Thread.sleep(200);
                        if (!LawPushApp.getCurrentShowModeUpContent().equals("")) {
                            showModeChannel.basicPublish("", "lawPush_god_mode_human", null, LawPushApp.getCurrentShowModeUpContent().getBytes());
                            Log.e("lawPush", "上传人工平台内容---" + LawPushApp.getCurrentShowModeUpContent());
                            JSONObject jsonObject = new JSONObject(LawPushApp.getCurrentShowModeUpContent());
                            String content = jsonObject.getString("text");
                            editor.putString("content", content);
                            editor.putString("qid", "");
                            editor.commit();
                            LawPushApp.setCurrentShowModeUpContent("");

                        }

                    }

                } catch (Exception e1) {
                    Log.e("lawPush", "rabbitMQ Exceptionaasas : " + e1.toString());
                }
            }
        });
    }

    /**
     * 上帝模式
     */
    Connection godModeConnection;
    Channel godModeChannel;
    public ExecutorService godModeThreadPoolExecute;

    public void godMode() {
        try {
            godModeChannel.close();
            godModeConnection.close();
            godModeThreadPoolExecute.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        godModeThreadPoolExecute = Executors.newFixedThreadPool(1);
        godModeThreadPoolExecute.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    godModeConnection = factoryShowMode.newConnection();
                    godModeChannel = godModeConnection.createChannel();
                    godModeChannel.queueDeclare("lawPush_god_mode_android", false, false, false, null);
                    //设置最大服务转发消息数量
                    int prefetchCount = 1;
                    godModeChannel.basicQos(prefetchCount);
                    QueueingConsumer consumer = new QueueingConsumer(godModeChannel);
                    // 指定消费队列
                    boolean ack = false; //打开应答机制
                    godModeChannel.basicConsume("lawPush_god_mode_android", ack, consumer);
                    //打开开关
                    Constant.setGodTextContent("", 2, false, 0);
                    godModeChannel.basicPublish("", "lawPush_god_mode_human", null, LawPushApp.getCurrentShowModeUpContent().getBytes());
                    Log.e("lawPush", "开启上帝模式时，一开始向lawPush_god_mode_human队列中传递的消息：" + LawPushApp.getCurrentShowModeUpContent());
                    LawPushApp.setCurrentShowModeUpContent("");
                    Log.e("lawPush", "   开启了上帝模式");
                    while (true) {
                        Log.e("lawPush", "   进入while(true)循环，开始阻塞。。。");
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        Log.e("lawPush", "上帝返回的信息是" + message);

                        JSONObject jsonObject = new JSONObject(message);
                        String deviceId = jsonObject.optString("deviceId");//服务器返回的deviceId值
                        if (deviceId.equals(LawPushApp.getCurrentDeviceId())) {
                            //相等设备id   接收处理
                            //发送应答
                            godModeChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                            String similarContent = jsonObject.optString("similarContent");
                            if (similarContent != null && !similarContent.equals("") && !similarContent.equals("null") && !similarContent.equals("undefined")) {
                                chatPresenter.getPredictCause(similarContent);
                            } else {
                                chatPresenter.dealServerMessageRabbitMQ(jsonObject);
                            }
                        } else {
                            //发送拒绝
                            godModeChannel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                            // 发送到消息队列尾部中
                            godModeChannel.basicPublish("", "lawPush_god_mode_android", null, message.getBytes("UTF-8"));
                        }

                    }
                } catch (Exception e1) {
                    Log.e("lawPush", "     上帝模式出了问题" + e1.toString());
                }
            }
        });
    }

    /**
     * 处理多个tag多选返回dialog类中返回的结果
     *
     * @param msgTagBeanList
     */
    void dealDialogMoreTagResult(List<MsgTagBean> msgTagBeanList) {
        String[] tagChosen, tagFatherChoosen;
        int totalNum = 0;
        boolean isHasFatherTag = false;
//
        for (int i = 0; i < msgTagBeanList.size(); i++) {
            MsgTagBean msgTagBean = msgTagBeanList.get(i);
            if (msgTagBean != null) {
                totalNum++;
            }
        }
        if (totalNum != 0) {
            //有选中的项
            //判断标签类型，存在父标签，则继续getSon获取子标签，不存在，则直接拿结果
            for (int i = 0; i < msgTagBeanList.size(); i++) {
                MsgTagBean msgTagBean = msgTagBeanList.get(i);
                if (msgTagBean != null) {
                    if (msgTagBean.getType() == 0) {
                        //存在父标签
                        isHasFatherTag = true;
                    }

                }
            }
            if (isHasFatherTag) {
                //存在父标签
                tagFatherChoosen = new String[totalNum];
                for (int i = 0; i < msgTagBeanList.size(); i++) {
                    MsgTagBean msgTagBean = msgTagBeanList.get(i);
                    if (msgTagBean != null) {
                        //将被勾选的标签全部传递过去，Son方法中有循环调用，直至栈为空
                        tagFatherChoosen[i] = msgTagBeanList.get(i).getEnName();
                    }
                }
                chatPresenter.getSon(tagFatherChoosen);
            } else {
                //直接拿子标签获取结果
                tagChosen = new String[totalNum];
                for (int i = 0; i < msgTagBeanList.size(); i++) {
                    MsgTagBean msgTagBean = msgTagBeanList.get(i);
                    if (msgTagBean != null) {
                        //子标签
                        tagChosen[i] = msgTagBeanList.get(i).getEnName();
                    }
                }

                String tagStr = "";
                for (int i = 0; i < tagChosen.length; i++) {
                    String value = getSharedPreferences(USER, Context.MODE_PRIVATE).getString(tagChosen[i], "");
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        String enName = jsonObject.getString("enName");
                        tagStr += enName + ",";
                        chatPresenter.getTagStack().push(enName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tagStr = tagChosen[i];
                    }
                }
                if (tagStr.length() > 0) {
                    //去掉最后一个逗号
                    if (tagStr.substring(tagStr.length() - 1).equals(",")) {
                        tagStr = tagStr.substring(0, tagStr.length() - 1);
                    }
                }
                //存储用户已勾选的标签

                String curTagChosen = sharedPreferences.getString("tagChosen", "");
                if (!curTagChosen.equals("") && !tagStr.equals("")) {
                    curTagChosen += ",";
                }
                editor.putString("tagChosen", curTagChosen + tagStr);
                editor.commit();
                chatPresenter.getTag();
            }
            //将聊天页的标签项勾选上
            String[] choosenStringArray = new String[msgTagBeanList.size()];
            for (int i = 0; i < msgTagBeanList.size(); i++) {
                choosenStringArray[i] = msgTagBeanList.get(i).getZhName();
            }
            modifyCheckBox(choosenStringArray);
        } else {
            //没有选中任何项
        }
    }

    void dealDialogCauseResult(List<MsgCauseBean> msgCauseBeanList) {
        //直接调用getSon接口
        chatPresenter.getFather(msgCauseBeanList.get(0).getValue());
        //将聊天页的标签项勾选上
        String[] choosenStringArray = new String[msgCauseBeanList.size()];
        for (int i = 0; i < msgCauseBeanList.size(); i++) {
            choosenStringArray[i] = msgCauseBeanList.get(i).getName();
        }
        modifyCheckBox(choosenStringArray);
    }

    private void dealDialogSimilarCauseResult(List<String> stringList) {
        if (LawPushApp.getCurrentSimilarCauseType() == 1)
            //example
            chatPresenter.getExample(stringList.get(0));
        if (LawPushApp.getCurrentSimilarCauseType() == 2)
            //cause接口
            chatPresenter.getPredictCause(stringList.get(0));
        if (LawPushApp.getCurrentSimilarCauseType() == 3)
            //调用multiple接口
            chatPresenter.getMultiple(LawPushApp.getResultId(), stringList.get(0));
        Log.e("lawPush", " chatActivity-----onActivityResult--causetype:"+LawPushApp.getCurrentSimilarCauseType());
        //将聊天页的标签项勾选上
        String[] choosenStringArray = new String[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            choosenStringArray[i] = stringList.get(i);
        }
        modifyCheckBox(choosenStringArray);
    }

    /**
     * 接收并处理后台返回的信息
     *
     * @param msg
     */
    public void receiveMsg(final Msg msg) {
        topSelectType = 1;
        msgList.add(msg);
        adapter.notifyDataSetChanged();
        msgListView.setSelection(msgList.size() - 1);
        String speakContent = "";
        startCountDownTimer();
        switch (msg.getType()) {
            case Msg.TYPE_CAUSE:
                if (msg.getContent() != null) {
                    speakContent = msg.getContent();
                }
                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                    speakContent += msg.getChoiceMsgs()[i].getContent() + "。";
                    if (i > 3)
                        break;
                }
                //仅有一个案由，不显示，放入到下一个文案中朗读并显示出来

                if (msg.getChoiceMsgs().length == 2) {
                    //跳横排界面，单选
                    Intent intent = new Intent(this, ChooseActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", true);
                    intent.putExtra("type", "cause");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        MsgCauseBean msgCauseBean = new MsgCauseBean();
                        msgCauseBean.setName(msg.getChoiceMsgs()[i].getContent());
                        msgCauseBean.setValue(msg.getChoiceMsgs()[i].getCaseCauseId());
                        msgCauseBeanList.add(msgCauseBean);
                    }
                    intent.putExtra("tagArray", msgCauseBeanList.toString());
                    startActivityForResult(intent, 12);
                }
                if (msg.getChoiceMsgs().length > 2) {
                    //跳转竖排页面，而且是单选
                    Intent intent = new Intent(this, DialogActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", true);
                    intent.putExtra("type", "cause");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        MsgCauseBean msgCauseBean = new MsgCauseBean();
                        msgCauseBean.setName(msg.getChoiceMsgs()[i].getContent());
                        msgCauseBean.setValue(msg.getChoiceMsgs()[i].getCaseCauseId());
                        msgCauseBeanList.add(msgCauseBean);
                    }
                    intent.putExtra("tagArray", msgCauseBeanList.toString());
                    startActivityForResult(intent, 11);
                }
                break;
            case Msg.TYPE_TAG:
                if (msg.getContent() != null) {
                    speakContent = msg.getContent();
                }
                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                    speakContent += msg.getChoiceMsgs()[i].getContent() + "。";
                    if (i > 3)
                        break;
                }
                //仅有一个标签不显示，放入到下一个文案中朗读并显示出来

                if (msg.getChoiceMsgs().length == 2) {
                    //跳横排界面，多选
                    Intent intent = new Intent(this, ChooseActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", false);
                    intent.putExtra("type", "tag");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        MsgTagBean msgTagBean = new MsgTagBean();
                        msgTagBean.setZhName(msg.getChoiceMsgs()[i].getContent());
                        msgTagBean.setEnName(msg.getChoiceMsgs()[i].getEnName());
                        msgTagBean.setType(msg.getChoiceMsgs()[i].getType());
                        msgTagBeanList.add(msgTagBean);
                    }
                    intent.putExtra("tagArray", msgTagBeanList.toString());
                    startActivityForResult(intent, 13);
                }

                if (msg.getChoiceMsgs().length > 2) {
                    //跳竖排全屏界面，多选
                    Intent intent = new Intent(this, DialogActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", false);
                    intent.putExtra("type", "tag");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        MsgTagBean msgTagBean = new MsgTagBean();
                        msgTagBean.setEnName(msg.getChoiceMsgs()[i].getEnName());
                        msgTagBean.setType(msg.getChoiceMsgs()[i].getType());
                        msgTagBean.setZhName(msg.getChoiceMsgs()[i].getContent());
                        msgTagBeanList.add(msgTagBean);
                    }
                    intent.putExtra("tagArray", msgTagBeanList.toString());
                    startActivityForResult(intent, 10);
                }
                break;
            case Msg.TYPE_RECEIVED:
                speakContent = "";
                break;
            case Msg.TYPE_MORE:
                speakContent = msg.getContent();
                break;
            case Msg.TYPE_QR:
                speakContent = "";
                msgListView.setSelection(msgList.size() - 3);
                break;
            case Msg.PROGRAMER_QUESTION:

                Intent answer = new Intent(myContext, ProgramQuestionDialogActivity.class);
                if (msg.getProgramQuestionAnswer() != null) {
                    answer.putExtra("isAnswer", true);
                    /////////////
                   String programQuestionAnswer = msg.getProgramQuestionAnswer();
//                    String programQuestionAnswer = "您的问题属于@@没听懂、没听清1.png##机动车交通师傅纠纷,小法为你找到以下相似问题";;

                    String[] splitProgramQuestionAnswer = programQuestionAnswer.split("@@");
                    if (splitProgramQuestionAnswer.length > 1) {
                        final String[] img = splitProgramQuestionAnswer[1].split("##");
                        if (splitProgramQuestionAnswer[0] != null && img.length == 1) {
                            answer.putExtra("answer", splitProgramQuestionAnswer[0]);
                        }
                        if (img.length > 1 && img[1] != null) {
                            answer.putExtra("answer", splitProgramQuestionAnswer[0]+img[1]);
                        }
                        if (splitProgramQuestionAnswer[0] == null && img.length > 1 && img[1] != null) {
                            answer.putExtra("answer", splitProgramQuestionAnswer[0]+img[1]);
                        }
                    }else{
                        answer.putExtra("answer", splitProgramQuestionAnswer[0]);
                    }

                }
                if (msg.getLawList().size() > 0) {
                    answer.putExtra("content", msg.getLawList().get(0).getContent());
                    answer.putExtra("title", msg.getLawList().get(0).getTitle());
                }
                startActivity(answer);


                break;
            case Msg.TYPE_RESULT:

                // TODO: 2017/3/14   跳转到实体问题的全屏页面
                Intent detail = new Intent(myContext, StandardQuestionDialogActivity.class);
                if (msg.getProgramQuestionAnswer() != null) {
                    detail.putExtra("isAnswer", true);
                    detail.putExtra("answer", msg.getProgramQuestionAnswer());
                }
                if (msg.getLaw() != null) {
                    detail.putExtra("content", msg.getLaw().getContent());
                    detail.putExtra("title", msg.getLaw().getTitle());
                }
                if (msg.getPoint() != null) {
                    detail.putExtra("pointTitle", msg.getPoint().getTitle());  //    司法观点标题
                    detail.putExtra("pointContent", msg.getPoint().getContent());//  司法观点内容
                }
                if (msg.getGuidanceCase() != null) {
                    detail.putExtra("guidanceCaseTitle", msg.getGuidanceCase().getTitle());  //    指导案例标题
                    detail.putExtra("guidanceCaseContent", msg.getGuidanceCase().getContent());//  指导案例内容
                }
                if (msg.getCaseInfo() != null) {
                    detail.putExtra("caseInfoTitle", msg.getCaseInfo().getTitle());  //    相关案例标题
                    detail.putExtra("caseInfoContent", msg.getCaseInfo().getContent());//  相关案例内容
                }
                startActivityForResult(detail,20);
                break;
            case Msg.SATISFACTION:
                speakContent = "";
                break;
            case Msg.CHAT_CHAT:

                String chatContent = msg.getContent();
                //     String content = "您的问题属于@@没听懂、没听清1.png##机动车交通师傅纠纷,小法为你找到以下相似问题";
                String[] chatAnswerImg = chatContent.split("@@");
                if (chatAnswerImg.length > 1) {
                    final String[] img = chatAnswerImg[1].split("##");
                    if (chatAnswerImg[0] != null && img.length == 1) {
                        speakContent=chatAnswerImg[0];
                    }
                    if (img.length > 1 && img[1] != null) {
                        speakContent=chatAnswerImg[0]+img[1];
                    }
                    if (chatAnswerImg[0] == null && img.length > 1 && img[1] != null) {
                        speakContent=chatAnswerImg[0]+img[1];
                    }
                }else{
                    speakContent = msg.getContent();
                }


                break;
            case Msg.CHAT_LAWS:
                //法律法规场景
                if (msg.getLawList().size() > 0) {
                    String title = msg.getLawList().get(0).getTitle().replace("中华人民共和国", ",");
                    speakContent = "我为您找到了如下法律法规。" + title + msg.getLawList().get(0).getContent();
                } else {
                    speakContent = "对不起,这个问题有点难哦,我现在还不会呦!";
                }
                break;
            case Msg.CHAT_SOS_TEXT:
                speakContent = msg.getContent();
                if (LawPushApp.getChatTypt() != 3 || LawPushApp.getMultipleValue() == 1) {
                    if (speakContent.length() > 22) {
                        Intent intentss = new Intent(ChatActivity.this, TextActivity.class);
                        intentss.putExtra("content", msg.getContent());
                        if (msg.getMotionsString() != null && !msg.getMotionsString().equals(""))
                            intentss.putExtra("motion", msg.getMotionsString());
                        startActivityForResult(intentss, 20);
                    }
                }
                break;
            case Msg.HOT_CAUSE_TYPE:
                speakContent = msg.getContent();
                LawPushApp.setCurrentSimilarCauseType(2);
                //跳转竖排页面，而且是单选
                Intent intentHot = new Intent(this, DialogActivity.class);
                intentHot.putExtra("title", msg.getContent());
                intentHot.putExtra("isOne", true);
                intentHot.putExtra("type", "choices");
                if (msg.getMotionsString() != null)
                    intentHot.putExtra("motion", msg.getMotionsString());
                ArrayList<String> stringListHot = new ArrayList<>();
                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                    stringListHot.add(msg.getChoiceMsgs()[i].getContent());
                }
                intentHot.putStringArrayListExtra("tagArray", stringListHot);
                startActivityForResult(intentHot, 14);
                break;
            case Msg.SIMILAR_CAUSE_TYPE:
                speakContent = msg.getContent();
                LawPushApp.setCurrentSimilarCauseType(msg.getSIMILAR_QUESTION_TYPE());
                if (msg.getChoiceMsgs().length == 1) {
                    //跳横排界面，单选
                    Intent intent = new Intent(this, ChooseActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", true);
                    intent.putExtra("type", "choicesOne");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        stringArrayList.add(msg.getChoiceMsgs()[i].getContent());
                    }
                    intent.putStringArrayListExtra("tagArray", stringArrayList);
                    startActivityForResult(intent, 16);
                }
                if (msg.getChoiceMsgs().length == 2) {
                    //跳横排界面，单选
                    Intent intent = new Intent(this, ChooseActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", true);
                    intent.putExtra("type", "choices");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        stringArrayList.add(msg.getChoiceMsgs()[i].getContent());
                    }
                    intent.putStringArrayListExtra("tagArray", stringArrayList);
                    startActivityForResult(intent, 15);
                }

                if (msg.getChoiceMsgs().length > 2) {
                    //跳转竖排页面，而且是单选
                    Intent intent = new Intent(this, DialogActivity.class);
                    intent.putExtra("title", msg.getContent());
                    intent.putExtra("isOne", true);
                    intent.putExtra("type", "choices");
                    if (msg.getMotionsString() != null)
                        intent.putExtra("motion", msg.getMotionsString());
                    ArrayList<String> stringList = new ArrayList<>();
                    for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                        stringList.add(msg.getChoiceMsgs()[i].getContent());
                    }
                    intent.putStringArrayListExtra("tagArray", stringList);
                    startActivityForResult(intent, 14);
                }
                break;
            case Msg.CHAT_WEATHER:
                speakContent = msg.getContent();
                break;
            case Msg.ANSWER_PICTURES:

//                if (msg.getContent() != null) {
//                    String content = msg.getContent();
//                    String[] split = content.split("@@");
//                    speakContent = split[0]+",您可以选择点击图片,查看大图!";
//                }
                //////////////////////

                String answerContent = msg.getContent();
                //     String content = "您的问题属于@@没听懂、没听清1.png##机动车交通师傅纠纷,小法为你找到以下相似问题";
                String[] answerImg = answerContent.split("@@");
                if (answerImg.length > 1) {
                    final String[] img = answerImg[1].split("##");
                    if (answerImg[0] != null && img.length == 1) {
                        speakContent=answerImg[0]+",您可以选择点击图片,查看大图!";

                    }
                    if (img.length > 1 && img[1] != null) {
                        speakContent=answerImg[0]+img[1]+",您可以选择点击图片,查看大图!";

                    }
                    if (answerImg[0] == null && img.length > 1 && img[1] != null) {
                        speakContent=answerImg[0]+img[1]+",您可以选择点击图片,查看大图!";
                    }
                }else{
                    speakContent =answerContent ;
                }
                break;
            default:
                break;
        }
        startSpeak(speakContent);

    }

    /**
     * 连续两次救场后或者连续三次随便聊聊后，提示用户进行法律相关问题提问
     */
    public void chatSaveScene() {
        LawPushApp.setChatChatIngTimes(0);
        LawPushApp.setChatChatSaveIngTimes(0);
        LawPushApp.setIsGuideUserScene(true);
        Log.e("lawPush", "  连续两次救场后或者连续三次随便聊聊后，提示用户进行法律相关问题提问  ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (!LawPushApp.isVoiceSpeaking()) {
                        Message message = new Message();
                        message.what = 1;
                        myHandler.sendMessage(message);
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    MyHandler myHandler;
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showGuideDialog();
                    break;
            }
            ;
        }
    }

    View dialogParentView;
    Dialog guideDialog;

    void createDialog(String title) {
        LawPushApp.setIsGuideDialogShow(true);
        startSpeak(title);
        try {
            dialogParentView = LayoutInflater.from(myContext).inflate(R.layout.activity_version_court, null);
            TextView titleView ,textView01,textView02,textView03,textView04;
            ImageView imageView01, imageView02, imageView03, imageView04, imageView05, imageView06, imageView07, imageView08;
            imageView01 = (ImageView) dialogParentView.findViewById(R.id.imgPosition1);
            imageView02 = (ImageView) dialogParentView.findViewById(R.id.imgPosition2);
            imageView03 = (ImageView) dialogParentView.findViewById(R.id.imgPosition3);
            imageView04 = (ImageView) dialogParentView.findViewById(R.id.imgPosition4);
            imageView05 = (ImageView) dialogParentView.findViewById(R.id.imgPosition5);
            imageView06 = (ImageView) dialogParentView.findViewById(R.id.imgPosition6);
            imageView07 = (ImageView) dialogParentView.findViewById(R.id.imgPosition7);
            imageView08 = (ImageView) dialogParentView.findViewById(R.id.imgPosition8);
            titleView = (TextView) dialogParentView.findViewById(R.id.guide_dialog_text);

            textView01 = (TextView) dialogParentView.findViewById(R.id.id_textPosition1);
            textView02 = (TextView) dialogParentView.findViewById(R.id.id_textPosition2);
            textView03 = (TextView) dialogParentView.findViewById(R.id.id_textPosition3);
            textView04 = (TextView) dialogParentView.findViewById(R.id.id_textPosition4);

            titleView.setText(title);
            textView01.setText(LawPushApp.getScenesBeanListFirstSome().get(0).getName());
            textView02.setText(LawPushApp.getScenesBeanListFirstSome().get(1).getName());
            textView03.setText(LawPushApp.getScenesBeanListFirstSome().get(2).getName());
            textView04.setText(LawPushApp.getScenesBeanListFirstSome().get(3).getName());

            imageView01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compareScene(LawPushApp.getScenesBeanListFirstSome().get(0));
                    hideGuideDialog();
                }
            });
            imageView02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compareScene(LawPushApp.getScenesBeanListFirstSome().get(1));
                    hideGuideDialog();
                }
            });
            imageView03.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compareScene(LawPushApp.getScenesBeanListFirstSome().get(2));
                    hideGuideDialog();
                }
            });
            imageView04.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compareScene(LawPushApp.getScenesBeanListFirstSome().get(3));
                    hideGuideDialog();
                }
            });
            imageView05.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTopSelectBg(3);
                    hideGuideDialog();
                }
            });
            imageView06.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTopSelectBg(4);
                    hideGuideDialog();
                }
            });
            imageView07.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTopSelectBg(5);
                    hideGuideDialog();
                }
            });
            imageView08.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFrontVoiceRecognizeListening();
                }
            });

            guideDialog = new Dialog(myContext, R.style.loading_dialog2);
            guideDialog.setContentView(dialogParentView);
            guideDialog.setCanceledOnTouchOutside(true);
            guideDialog.show();

            dialogParentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideGuideDialog();
                }
            });
            guideDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    LawPushApp.setIsGuideDialogShow(false);
                    LawPushApp.setChatChatSaveIngTimes(0);
                    stopSpeak();
                    startCountDownTimer();
                }
            });

            WindowManager.LayoutParams params = guideDialog.getWindow().getAttributes();
            params.width = 1920;
            params.height = 1180;
            guideDialog.getWindow().setAttributes(params);
        } catch (Exception e) {
            Log.e("lawPush", "  error:" + e.toString());
        }
    }


    void compareScene(ScenesBean mSceneBean) {
        guideDialog.cancel();
        hideGuideDialog();
        Msg msg = new Msg(mSceneBean.getName(), Msg.TYPE_SEND);
        msgList.add(msg);
        msgListView.setSelection(msgList.size() - 1);
        adapter.notifyDataSetChanged();

        ScenesBean scenesBean = null;
        for (int i = 0; i <LawPushApp.getScenesBeanList().size() ; i++) {
            if (mSceneBean.getParent().equals(LawPushApp.getScenesBeanList().get(i).getId())){
                scenesBean = LawPushApp.getScenesBeanList().get(i);
                break;
            }
        }
        if (scenesBean == null){
            setTopSelectBg(3);
            return;
        }
        switch (scenesBean.getName()) {
            case "程序引导":
                topSelectType = 0;
                setTopSelectBg(1);
                break;
            case "法律法规":
                topSelectType = 0;
                setTopSelectBg(5);
                break;
            case "法律问题":
                topSelectType = 0;
                setTopSelectBg(4);
                break;
            case "随便聊聊":
                topSelectType = 0;
                setTopSelectBg(3);
                break;
            default:
                break;
        }
        chatPresenter.getSceneQuestion(mSceneBean.getName(), mSceneBean.getId());
        guideDialog.cancel();
    }

    ExecutorService threadPoolExecutorTest;

    /**
     * 测试动作
     */
    public void testActionMQ() {
        if (threadPoolExecutorTest != null)
            threadPoolExecutorTest.shutdownNow();
        threadPoolExecutorTest = Executors.newFixedThreadPool(1);
        threadPoolExecutorTest.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = factoryShowMode.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare("lawPush_motion_test", false, false, false, null);
                    //设置最大服务转发消息数量
                    int prefetchCount = 1;
                    channel.basicQos(prefetchCount);
                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    // 指定消费队列
                    boolean ack = false; //打开应答机制
                    channel.basicConsume("lawPush_motion_test", ack, consumer);
                    Log.e("lawPush", "   创建消息队列成功");
                    while (true) {

                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        String message = new String(delivery.getBody());
                        Log.e("lawPush", "   message---" + message);

                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        String value = "{motions:" + message + "}";
                        robotPerformAction(new JSONObject(value));
                    }
                } catch (InterruptedException e) {
                    Log.e("lawPush", "rabbitMQ InterruptedException : " + e.toString());
                } catch (Exception e1) {
                    Log.e("lawPush", "rabbitMQ Exception : " + e1.toString());
                }
            }
        });
    }
}
