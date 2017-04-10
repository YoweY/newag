package aegis.aegisxunfei;

import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;

/**
 * Created by Administrator on 2016/12/14.
 */

public class MyLawPushApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用半角“,”分隔。
        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
        SpeechUtility.createUtility(MyLawPushApp.this, "appid=" + getString(R.string.app_id));
        setCurrentDeviceId("0123456789");
        setIsNeedContinueListen(true);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VoiceWakeuper wakeuper = VoiceWakeuper.getWakeuper();
        if (wakeuper != null) {
            Log.e("LawPushApp", "onTerminate: destroy wakeuperListener");
            wakeuper.destroy();
        }
    }

    //UI对话框中正在监听的被识别的内容
    public static String VOICE_LISTENERING_CONTENT;
    //记录当前设备id
    public static String CURRENT_DEVICE_ID;
    //记录当前要传给人工平台的内容
    public static String CURRENT_SHOW_MODE_UP_CONTENT;

    //是否需要开启持续监听
    public static boolean IS_NEED_CONTINUE_LISTEN;
    //base类中
    public static String CURRENT_VOICE_QUESTION_BASE;
    //是否在界面无响应10秒后，提示用户进行法律相关问题的提问
    public static boolean hasGudeiUserSceneAfterTenSecond = false;
    //当前所处的activity
    public static String CURRNT_ACTIVITY;
    //记录上一次触摸头部时的时间戳
    public static long LAST_TOUCH_HEAD_TIME;
    //是否处于移动模式
    public static boolean IS_MOVE_MODE = false;
    //上一次进行打招呼的时间
    public static long LAST_SAY_HELLO_TIME;
    //上一次声源定位的时刻
    public static long LAST_VOICE_FROM_TIME;
    //上一次进行动作的时间
    public static long LAST_ACTION_TIME;
    //上一次被唤醒的时间
    public static long LAST_WAKE_UP_TIME;

    //引导弹框是否显示出来了
    public static boolean IS_GUIDE_DIALOG_SHOW  = false;

    //当前语音识别的类型   1是后台监听，2是UI界面监听
    public static int VOICE_LISTENING_TYPE;
    //当前是否正在
    public static boolean IS_VOICE_SPEAKING = false;

    public static boolean isVoiceSpeaking() {
        return IS_VOICE_SPEAKING;
    }

    public static void setIsVoiceSpeaking(boolean isVoiceSpeaking) {
        IS_VOICE_SPEAKING = isVoiceSpeaking;
    }

    public static int getVoiceListeningType() {
        return VOICE_LISTENING_TYPE;
    }

    public static void setVoiceListeningType(int voiceListeningType) {
        VOICE_LISTENING_TYPE = voiceListeningType;
    }

    public static boolean isGuideDialogShow() {
        return IS_GUIDE_DIALOG_SHOW;
    }

    public static void setIsGuideDialogShow(boolean isGuideDialogShow) {
        IS_GUIDE_DIALOG_SHOW = isGuideDialogShow;
    }

    public static long getLastTouchHeadTime() {
        return LAST_TOUCH_HEAD_TIME;
    }

    public static void setLastTouchHeadTime(long lastTouchHeadTime) {
        LAST_TOUCH_HEAD_TIME = lastTouchHeadTime;
    }

    public static boolean isMoveMode() {
        return IS_MOVE_MODE;
    }

    public static void setIsMoveMode(boolean isMoveMode) {
        IS_MOVE_MODE = isMoveMode;
    }

    public static long getLastSayHelloTime() {
        return LAST_SAY_HELLO_TIME;
    }

    public static void setLastSayHelloTime(long lastSayHelloTime) {
        LAST_SAY_HELLO_TIME = lastSayHelloTime;
    }

    public static long getLastVoiceFromTime() {
        return LAST_VOICE_FROM_TIME;
    }

    public static void setLastVoiceFromTime(long lastVoiceFromTime) {
        LAST_VOICE_FROM_TIME = lastVoiceFromTime;
    }

    public static long getLastActionTime() {
        return LAST_ACTION_TIME;
    }

    public static void setLastActionTime(long lastActionTime) {
        LAST_ACTION_TIME = lastActionTime;
    }

    public static long getLastWakeUpTime() {
        return LAST_WAKE_UP_TIME;
    }

    public static void setLastWakeUpTime(long lastWakeUpTime) {
        LAST_WAKE_UP_TIME = lastWakeUpTime;
    }

    public static String getCurrntActivity() {
        return CURRNT_ACTIVITY;
    }

    public static void setCurrntActivity(String currntActivity) {
        CURRNT_ACTIVITY = currntActivity;
    }

    public static boolean isHasGudeiUserSceneAfterTenSecond() {
        return hasGudeiUserSceneAfterTenSecond;
    }

    public static void setHasGudeiUserSceneAfterTenSecond(boolean hasGudeiUserSceneAfterTenSecond) {
        MyLawPushApp.hasGudeiUserSceneAfterTenSecond = hasGudeiUserSceneAfterTenSecond;
    }

    public static String getCurrentVoiceQuestionBase() {
        return CURRENT_VOICE_QUESTION_BASE;
    }

    public static void setCurrentVoiceQuestionBase(String currentVoiceQuestionBase) {
        CURRENT_VOICE_QUESTION_BASE = currentVoiceQuestionBase;
    }

    public static boolean isNeedContinueListen() {
        return IS_NEED_CONTINUE_LISTEN;
    }

    public static void setIsNeedContinueListen(boolean isNeedContinueListen) {
        IS_NEED_CONTINUE_LISTEN = isNeedContinueListen;
    }

    public static String getCurrentShowModeUpContent() {
        return CURRENT_SHOW_MODE_UP_CONTENT;
    }

    public static void setCurrentShowModeUpContent(String currentShowModeUpContent) {
        CURRENT_SHOW_MODE_UP_CONTENT = currentShowModeUpContent;
    }

    public static String getCurrentDeviceId() {
        return CURRENT_DEVICE_ID;
    }

    public static void setCurrentDeviceId(String currentDeviceId) {
        CURRENT_DEVICE_ID = currentDeviceId;
    }

    public static String getVoiceListeneringContent() {
        return VOICE_LISTENERING_CONTENT;
    }

    public static void setVoiceListeneringContent(String voiceListeneringContent) {
        VOICE_LISTENERING_CONTENT = voiceListeneringContent;
    }
}
