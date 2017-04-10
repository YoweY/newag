package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyCountDownTimer;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.handler.BaseHandler;

import aegis.aegisxunfei.MyBaseActivity;
import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by mcs on 2016/12/29.
 */



public abstract class BaseActivity extends MyBaseActivity {


    BaseHandler handler;
    private MyCountDown myCountDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        Log.e("lawPush", " base-----onCreate");
        myCountDownTimer = new MyCountDown(120000,1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("lawPush", " base-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.e("lawPush", " base-----onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e("lawPush", " base-----onStop");
        stopCountDownTime();
    }

    /**
     * 开启计时器
     */
//    @Override
    public void startCountDownTimer() {
       super.startCountDownTimer();
        if (myCountDownTimer.mCancelled){
            myCountDownTimer.start();
            Log.e("lawPush", "计时器重新开始");
        }

    }

    /**
     * 取消计时器
     */
//    @Override
    public void stopCountDownTime() {
       super.stopCountDownTime();
        if (!myCountDownTimer.mCancelled){
            myCountDownTimer.cancel();
            Log.e("lawPush", "计时器结束");
        }
    }

    @Override
    public void dealVoiceContent(String result) {
        super.dealVoiceContent(result);
        if (result.length() > 1 && result.length() < 10) {
            String temp = result;
            temp = temp.replaceAll("，|,|\\?|？|\\.|。|!|！", "");
            String[] valueArray = {"小法小法", "小花小花", "小宝小宝","想法想法","烧饭烧饭","小法小宝","想法小宝","想法小法","小法想法","小宝想法","香港香港","早饭早饭"};
            boolean isRight = false;
            for (int i = 0; i < valueArray.length; i++) {
                if (valueArray[i].equals(temp)) {
                    isRight = true;
                    startFrontVoiceRecognizeListening();
                    break;
                }
            }
            if (isRight)
                return;
        }
        if (result.matches(".{0,6}跟我走.{0,6}")) {
            //开启移动模式
            showOnChatUI(result);
            MyLawPushApp.setIsMoveMode(true);
            robbotAction(10);
            return;
        }
        if (MyLawPushApp.isMoveMode() && result.matches(".{0,6}((停)|(不要动了)|(到了)|(婷)).{0,6}")) {
            MyLawPushApp.setIsMoveMode(false);
            robotHandToReset();
            startSpeak("中止运动模式！");
            showOnChatUI(result);
            Log.e("lawPush","中止运动模式");
            return;
        }
        if (MyLawPushApp.IS_MOVE_MODE) {
            if (result.matches(".{0,3}((前)|(钱)|(来)|(过)).{0,3}")) {
                showOnChatUI(result);
                robbotAction(1);
                return;
            }
            if (result.matches(".{0,3}((右)|(又)|(享用)).{0,3}")) {
                showOnChatUI(result);
                robbotAction(3);
                return;
            }
            if (result.matches(".{0,3}((左)|(佐)).{0,3}")) {
                showOnChatUI(result);
                robbotAction(2);
                return;
            }
            if (result.matches(".{0,3}((候)|(后)|(吼)|(厚))")){
                showOnChatUI(result);
                robbotAction(4);
                return;
            }
            return;
        }

        //先判断文本是否处于本地命令集
        if (result.matches(".{0,5}((向前)|(过来)|(走一走)|(往前)|(走两步)|(到这边来)|(来)|(走啊)).{0,5}")){
            // TODO: 2017/2/13   添加向前走两步
            if (result.length() < 10) {
                showOnChatUI(result);
                robbotAction(1);
                return;
            }
        }
        if (result.matches(".{0,5}((向左转)|(往左转)|(往左走)).{0,5}")) {
            showOnChatUI(result);
            robbotAction(2);
            return;
        }
        if (result.matches(".{0,5}((向右转)|(想右转)|(我又转)|(往右转)|(往右走)).{0,5}")) {
            if (result.length() < 8) {
                showOnChatUI(result);
                robbotAction(3);
                return;
            }
        }
        if (result.matches(".{0,5}((向后转)|(往后转)|(往后传)|(向厚赚)|(转过去)|(赚过去)|(往厚走)|(后面)).{0,5}")) {
            if (result.length() < 7) {
                showOnChatUI(result);
                robbotAction(4);
                return;
            }
        }
        if (result.matches(".{0,3}((转圈)|(转一圈)|(专权)|(转个圈)).{0,3}")) {
            if (result.length() < 7) {
                showOnChatUI(result);
                robbotAction(5);
                return;
            }
        }
        if (result.matches(".{0,10}((握个手)|(握手)|(握握手)|(牵个手)|(牵手)(抬手)|(举手)|(拒收)|(聚首)|(巨兽)).{0,10}")) {
            if (result.length() < 20) {
                showOnChatUI(result);
                robbotAction(6);
                return;
            }
        }

        Constant.setGodTextContent(result, 0, true, 1);//将用户说的话返给上帝模式中，备用，上帝模式一旦开启，则可以从中取得数据
        if (result.length() < 12) {
            if (result.matches(".{0,5}((休息)|(歇会)|(修习)|(协会)|(写会)|(结束)).{0,6}")) {
                showOnChatUI(result);
                showOnChatUIRobbot("我休息会儿，有事再叫我哈~");
                startSpeak("我休息会儿，有事再叫我哈~");
                hideGuideDialog();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AppManager.getAppManager().finishActivity(ChatActivity.class);
                AppManager.getAppManager().finishActivity(HelpActivity.class);
                AppManager.getAppManager().finishActivity(LawDetailActivity.class);
                AppManager.getAppManager().finishActivity(DataDetailActivity.class);
                AppManager.getAppManager().finishActivity(ProgramQuestionDialogActivity.class);
                AppManager.getAppManager().finishActivity(StandardQuestionDialogActivity.class);
                AppManager.getAppManager().finishActivity(TextActivity.class);
                AppManager.getAppManager().finishActivity(ChooseActivity.class);
                AppManager.getAppManager().finishActivity(DialogActivity.class);
                return;
            }
        }
        if (result.length() < 12) {
            if (result.matches(".{0,7}((不需要)|(没有)|(重新提问)|(退出)|(返回)|(关闭)|(发挥)|(回到)|(阅读完毕)).{0,7}")) {
                if (!LawPushApp.getCurrntActivity().equals("SplashActivity")) {
                    showOnChatUI(result);
                    String string = Constant.randomRobbotAction();
                    showOnChatUIRobbot(string);
                    startSpeak(string);
                    AppManager.getAppManager().finishCurrentActivity();
                    hideGuideDialog();
                    return;
                }
            }
        }

        if (result.matches(".{0,5}帮助.{0,5}")) {
            startActivity(new Intent(BaseActivity.this, HelpActivity.class));
            startSpeak(Constant.randomRobbotAction());
            hideGuideDialog();
            return;
        }

        if (result.matches(".{0,5}((声音)|(神音)|(生意)|(圣衣)|(圣印)|(生硬)).{0,3}大.{0,5}")) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            // 变更声音
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            showOnChatUI(result);
            showOnChatUIRobbot("听听我说话音量是不是大点了");
            startSpeak("听听我说话音量是不是大点了");
            hideGuideDialog();
            return;
        }
        if (result.matches(".{0,5}((声音)|(神音)|(生意)|(圣衣)|(圣印)|(生硬)).{0,3}小.{0,5}")) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            // 变更声音
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            startSpeak("听听我说话音量是不是小点了");
            showOnChatUI(result);
            showOnChatUIRobbot("听听我说话音量是不是小点了");
            hideGuideDialog();
            return;
        }

        if (result.matches(".{0,5}((说话)|(说哈)|(说胡)).?快.{0,2}点.{0,5}")) {
            if (result.contains("在") || result.contains("再") || result.contains("更")) {
                //设置合成语速
                speakMoreFaster();
                startSpeak("听听我说话是不是快点了");
                showOnChatUI(result);
                showOnChatUIRobbot("听听我说话是不是快点了");
                hideGuideDialog();
                return;
            }
        }

        if (result.matches(".{0,7}快.{0,2}点.{0,7}")) {
            //设置合成语速
            speakFaster();
            startSpeak("听听我说话是不是快点了");
            showOnChatUI(result);
            showOnChatUIRobbot("听听我说话是不是快点了");
            hideGuideDialog();
            return;
        }

        if (result.matches(".{0,5}((说话)|(说哈)|(说胡)).?慢.{0,2}点.{0,5}")) {
            //设置合成语速
            speakSlower();
            startSpeak("听听我说话是不是慢点了");
            showOnChatUI(result);
            showOnChatUIRobbot("听听我说话是不是慢点了");
            hideGuideDialog();
            return;
        }
        if (result.matches(".{0,3}谢谢.{0,3}")) {
            robbotAction(7);
            showOnChatUI(result);
            showOnChatUIRobbot("很高兴为您服务，立案庭在左手边50米。");
            hideGuideDialog();
            return;
        }

        String[] arrayValue = {"小宝", "小法", "小花", "潇洒", "笑翻", "想法", "相反", "笑话", "想把",
                "肖法", "消化", "宝宝", "下班", "现在", "吃饭", "小胖", "想到", "要把", "再把", "像他", "叫他", "说话"};
        String[] arrayEnd = {"吗"};

        boolean isRight = false;
        //判断是语句中是不是包含小法等特定词
        for (int i = 0; i < arrayValue.length; i++) {
            if (result.contains(arrayValue[i])) {
                isRight = true;
                break;
            }
        }
        //判断结尾处是否是特定词
        String tempEnd = result.replaceAll("，|,|\\?|？|\\.|。|!|！", "");
        for (int j = 0; j < arrayEnd.length; j++) {
            if (tempEnd.endsWith(arrayEnd[j])) {
                isRight = true;
                break;
            }
        }

        //如果是语音唤醒的语音识别，则直接置为true
        if (LawPushApp.getVoiceListeningType() == 2) {
            isRight = true;
            LawPushApp.setVoiceListeningType(1);
        }
        //去除标签符号，如果长度大于3，直接响应
        String tempLength = result;
        tempLength = tempLength.replaceAll("，|,|\\?|？|\\.|。|!|！", "");
        if (tempLength.length() > 3)
            isRight = true;

        //如果包含以下特定词也响应
        String[] specialArray = {"干啥", "干什么", "你好", "怎么", "需要", "可以", "高利贷", "阅历", "月利", "你是"};
        for (int i = 0; i < specialArray.length; i++) {
            if (result.contains(specialArray[i])) {
                isRight = true;
                break;
            }
        }

        if (isRight) {
            if (Constant.isMeaningFul(result.trim())) {
                sendToServer(result);
            }
        }
    }

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
    }

    public Handler getHandler() {
        if (handler == null) {
            handler = new BaseHandler(BaseActivity.this);
        }
        return handler;
    }

    public class MyCountDown extends MyCountDownTimer {

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
            int timeValue = (int) (millisUntilFinished / 1000);
            Log.e("lawPushTime", "倒计时---" + timeValue);
            if (LawPushApp.getCurrntActivity().equals("SplashActivity") && LawPushApp.isGuideDialogShow() && timeValue < 90 && timeValue > 88) {
                hideGuideDialog();
                LawPushApp.setIsGuideDialogShow(false);
                Log.e("lawPushTime", "隐藏广告轮播页的引导弹框");
                return;
            }
            if (timeValue < 60 && timeValue > 40 && LawPushApp.getCurrntActivity().equals("ChatActivity") && !LawPushApp.isGuideDialogShow()) {
                LawPushApp.setHasGudeiUserSceneAfterTenSecond(true);
                showGuideDialog();
                Log.e("lawPushTime", "显示广告轮播页的引导弹框");
                return;
            }
        }

        @Override
        public void onFinish() {
            if (!LawPushApp.getCurrntActivity().equals("SplashActivity")) {
                startSpeak("我休息会儿，有事再叫我哈~");
                hideGuideDialog();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AppManager.getAppManager().finishActivity(HelpActivity.class);
                AppManager.getAppManager().finishActivity(LawDetailActivity.class);
                AppManager.getAppManager().finishActivity(DataDetailActivity.class);
                AppManager.getAppManager().finishActivity(ChatActivity.class);
                AppManager.getAppManager().finishActivity(ProgramQuestionDialogActivity.class);
                AppManager.getAppManager().finishActivity(StandardQuestionDialogActivity.class);
                AppManager.getAppManager().finishActivity(TextActivity.class);
                AppManager.getAppManager().finishActivity(ChooseActivity.class);
                AppManager.getAppManager().finishActivity(DialogActivity.class);
            }
        }
    }

    //隐藏引导场景的弹框
    void hideGuideDialog() {
    }

    //显示引导场景的弹框
    void showGuideDialog() {
    }

    /**
     * 有默认宽高的Glide加载图片
     *
     * @param o      可以是  url 可以是 Drawable  可以是文件
     * @param iv     需要显示的控件
     * @param width  宽
     * @param height 高
     */
    public void loadImage(Object o, ImageView iv, int width, int height) {
        Glide.with(this)   //使得glide更容易使用，因为能接收context，activity，fragment对象
                .load(o)
                .priority(Priority.HIGH)    //设置优先级
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.load)    //加载中显示的图片
                .error(R.drawable.error)//加载失败显示的图片
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
           //     .skipMemoryCache(true)
          //      .fitCenter()    //缩放图像，整个显示在控件，尽可能的填满
                .centerCrop()
                .into(iv);
    }

    /**
     * 默认宽和高的加载图片
     * @param o  可以是  url 可以是 Drawable  可以是文件
     * @param iv 需要显示的控件
     */
    public void loadImage(Object o, ImageView iv) {
        Glide.with(this)   //使得glide更容易使用，因为能接收context，activity，fragment对象
                .load(o)
                .priority(Priority.HIGH)    //设置优先级
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.load)    //加载中显示的图片
                .error(R.drawable.error)//加载失败显示的图片
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
       //         .skipMemoryCache(true)
                .fitCenter()    //缩放图像，整个显示在控件，尽可能的填满
                .into(iv);
    }
}




