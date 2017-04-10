package com.info.aegis.lawpush4android;

import android.util.Log;

import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.view.iml.BaseActivity;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import java.util.List;
import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by Administrator on 2016/12/14.
 */

public class LawPushApp extends MyLawPushApp {

    public static final String USER = "user";

    //当前的聊天页的场景
    public static int CHAT_TYPT;

    //上一次操作的时间
    public static long LASETIME;

    //仅返回一个标签时保存下内容
    public static String JUST_ONE_TAG_TEXT;

    /**
     * 仅返回一个案由时保存下内容
     */
    public static String JUST_ONE_CAUSE_TEXT;

    //多轮问答   1是 0否
    public static int MULTIPLE_VALUE;

    //多轮问答中的id
    public static String RESULT_ID;

    //无UI界面的监听器监听的内容
    public static String CURRENT_BACK_RECONIGNIZE;

    //上帝模式是否已经开启
    public static boolean IS_GOD_MODE_START_ING;

    //返回的相似问题列表中，记录应当调用的接口
    public static int CURRENT_SIMILAR_CAUSE_TYPE;

    //服务端场景配置
    public static List<ScenesBean> scenesBeanList;

    //服务端场景配置中proitroy==5的前几项  11
    public static List<ScenesBean> scenesBeanListFirstSome;

    //随便聊聊持续的次数
    public static int CHAT_CHAT_ING_TIMES;

    //随便聊聊中连续救场的次数
    public static int CHAT_CHAT_SAVE_ING_TIMES;

    //当前的音量值
    public static int CURRENT_VOICE_VOLUME;

    //上一次检测音量值的时刻
    public static long LAST_VOICE_VOLUME_TIME;

    //是否引到用户进行场景选择
    public static boolean isGuideUserScene = false;

    //答案分两次展示，这里保存第一个doc字段
    public static String DOUBLE_ANSWER_DOCTEXT;
    //答案分两次展示，这里保存第二个choice字段
    public static Msg.ChoiceMsg [] DOUBLE_ANSWER_CHOICE;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化网络请求框架
        NoHttp.initialize(this);
        Logger.setTag("Ageis_lawPush");
        Logger.setDebug(false);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志

        setCurrntActivity("");
        setChatTypt(3);
        setVoiceListeneringContent("");
        setMultipleValue(0);
        setResultId("");
        setCurrentBackReconignize("");
        setCurrentShowModeUpContent("");
        setJustOneTagText("");
        setJustOneCauseText("");
        setCurrentSimilarCauseType(0);
        setDoubleAnswerDoctext("");


    }

    public static String getDoubleAnswerDoctext() {
        return DOUBLE_ANSWER_DOCTEXT;
    }

    public static void setDoubleAnswerDoctext(String doubleAnswerDoctext) {
        DOUBLE_ANSWER_DOCTEXT = doubleAnswerDoctext;
    }

    public static Msg.ChoiceMsg[] getDoubleAnswerChoice() {
        return DOUBLE_ANSWER_CHOICE;
    }

    public static void setDoubleAnswerChoice(Msg.ChoiceMsg[] doubleAnswerChoice) {
        DOUBLE_ANSWER_CHOICE = doubleAnswerChoice;
    }

    public static List<ScenesBean> getScenesBeanListFirstSome() {
        return scenesBeanListFirstSome;
    }

    public static void setScenesBeanListFirstSome(List<ScenesBean> scenesBeanListFirstSome) {
        LawPushApp.scenesBeanListFirstSome = scenesBeanListFirstSome;
    }

    public static boolean isGuideUserScene() {
        return isGuideUserScene;
    }

    public static void setIsGuideUserScene(boolean isGuideUserScene) {
        LawPushApp.isGuideUserScene = isGuideUserScene;
    }

    public static long getLastVoiceVolumeTime() {
        return LAST_VOICE_VOLUME_TIME;
    }

    public static void setLastVoiceVolumeTime(long lastVoiceVolumeTime) {
        LAST_VOICE_VOLUME_TIME = lastVoiceVolumeTime;
    }

    public static int getCurrentVoiceVolume() {
        return CURRENT_VOICE_VOLUME;
    }

    public static void setCurrentVoiceVolume(int currentVoiceVolume) {
        CURRENT_VOICE_VOLUME = currentVoiceVolume;
    }

    public static int getChatChatIngTimes() {
        return CHAT_CHAT_ING_TIMES;
    }

    public static void setChatChatIngTimes(int chatChatIngTimes) {
        CHAT_CHAT_ING_TIMES = chatChatIngTimes;
    }

    public static int getChatChatSaveIngTimes() {
        return CHAT_CHAT_SAVE_ING_TIMES;
    }

    public static void setChatChatSaveIngTimes(int chatChatSaveIngTimes) {
        CHAT_CHAT_SAVE_ING_TIMES = chatChatSaveIngTimes;
    }

    public static List<ScenesBean> getScenesBeanList() {
        return scenesBeanList;
    }

    public static void setScenesBeanList(List<ScenesBean> scenesBeanList) {
        LawPushApp.scenesBeanList = scenesBeanList;
    }

    public static int getChatTypt() {
        return CHAT_TYPT;
    }

    public static void setChatTypt(int chatTypt) {
        CHAT_TYPT = chatTypt;
    }

    public static long getLaseTime() {
        return LASETIME;
    }

    public static void setLaseTime(long laseTime) {
        LASETIME = laseTime;
    }

    public static String getJustOneCauseText() {
        return JUST_ONE_CAUSE_TEXT;
    }

    public static void setJustOneCauseText(String justOneCauseText) {
        JUST_ONE_CAUSE_TEXT = justOneCauseText;
    }

    public static String getJustOneTagText() {
        return JUST_ONE_TAG_TEXT;
    }

    public static void setJustOneTagText(String justOneTagText) {
        JUST_ONE_TAG_TEXT = justOneTagText;
    }

    public static int getMultipleValue() {
        return MULTIPLE_VALUE;
    }

    public static void setMultipleValue(int multipleValue) {
        MULTIPLE_VALUE = multipleValue;
    }

    public static String getResultId() {
        return RESULT_ID;
    }

    public static void setResultId(String resultId) {
        RESULT_ID = resultId;
    }

    public static String getCurrentBackReconignize() {
        return CURRENT_BACK_RECONIGNIZE;
    }

    public static void setCurrentBackReconignize(String currentBackReconignize) {
        CURRENT_BACK_RECONIGNIZE = currentBackReconignize;
    }

    public static boolean isGodModeStartIng() {
        return IS_GOD_MODE_START_ING;
    }

    public static void setIsGodModeStartIng(boolean isGodModeStartIng) {
        IS_GOD_MODE_START_ING = isGodModeStartIng;
    }

    public static int getCurrentSimilarCauseType() {
        return CURRENT_SIMILAR_CAUSE_TYPE;
    }

    public static void setCurrentSimilarCauseType(int currentSimilarCauseType) {
        CURRENT_SIMILAR_CAUSE_TYPE = currentSimilarCauseType;
    }

    public static BaseActivity.MyCountDown mCountDownTimer;

    public static BaseActivity.MyCountDown getmCountDownTimer() {
        return mCountDownTimer;
    }

    public static void setmCountDownTimer(BaseActivity.MyCountDown mCountDownTimer) {
        LawPushApp.mCountDownTimer = mCountDownTimer;
    }
}
