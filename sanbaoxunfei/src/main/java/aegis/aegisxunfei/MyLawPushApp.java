package aegis.aegisxunfei;

import android.app.Application;
import android.util.Log;

import cn.tee3.avd.AVImporter;
import cn.tee3.avd.ErrorCode;
import cn.tee3.avd.User;

/**
 * Created by Administrator on 2016/12/14.
 */

public class MyLawPushApp extends Application {


    //叁体
    String          room_id = "r6604";
    static AVImporter mAVimporter = null;
    static boolean        testTnputTSzero = false;
    //
    String serverurl = "nice2meet.cn:8080";
//    String serverurl = "3tee.cn:8080";
    //    String accessKey = "F89EB5C71E494850A061CC0C5F42C177";
//    String accessKey = "835B31B53A484A7780282D469EEE14D1";
    String accessKey = "F89EB5C71E494850A061CC0C5F42C177";
    //    String secretKey = "DDDF7445961C4D27A7DCE106001BBB4F";
//    String secretKey = "F8052B3D29C548C185ACFD165FC251FF";
    String secretKey = "DDDF7445961C4D27A7DCE106001BBB4F";
    static int kSampleRate = 8000;

    private void startImporter() {
        // step 2
        if (null == mAVimporter) {
            mAVimporter = AVImporter.obtain(room_id);
            mAVimporter.setListener(new AVImporter.Listener() {
                @Override
                public void onStatus(int result) {
                    Log.e("lawPush", "AVImporter onStatus, result="+result);
                }

                @Override
                public void onError(int reason) {
                    Log.e("lawPush", "AVImporter onError, reason="+reason);
                }
            });
        }
        User user = new User("user1111", "aaaa", "");
        mAVimporter.enableAudio(true);
        mAVimporter.enableVideo(true);
        mAVimporter.join(user, new AVImporter.RoomJoinResultListener() {
            @Override
            public void onRoomJoinResult(String roomId, int result) {
                Log.e("lawPush", "AVImporter onRoomJoinResult, roomId="+roomId+",result="+result);
                if (ErrorCode.AVD_OK != result) {
                    return ;
                }
            }
        });
    }

    private void stopImporter()  {
        if (null == mAVimporter) {
            Log.e("lawPush","stopImporter null == mAVimporter");
            return;
        }
        AVImporter.destoryImporter(mAVimporter);
        mAVimporter = null;
        Log.e("lawPush","stopImporter null == mAVimporter完成");
    }
    @Override
    public void onCreate() {
        super.onCreate();

        setCurrentDeviceId("0123456789");
        setLastTouchHeadTime(System.currentTimeMillis());
        setIsNeedContinueListen(true);

        // step 1
        AVImporter.setLogParams("debug info", "/sdcard/fakecamera.log");
        AVImporter.initEngine(getBaseContext(), serverurl, accessKey, secretKey,
                new AVImporter.EngineInitResultListener() {
                    @Override
                    public void onEngineInitResult(int result) {
                        Log.e("lawPush","    onEngineInitResult:"+result);
                        if (ErrorCode.AVD_OK  !=  result) {
                            return;
                        }
                        startImporter();
                    }
                });
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d("lawPush", "onTerminate");
        super.onTerminate();
        stopImporter();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d("lawPush", "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d("lawPush", "onTrimMemory");
        super.onTrimMemory(level);
    }

    //UI对话框中正在监听的被识别的内容
    public static String VOICE_LISTENERING_CONTENT;
    //记录当前设备id
    public static String CURRENT_DEVICE_ID;
    //记录当前要传给人工平台的内容
    public static String CURRENT_SHOW_MODE_UP_CONTENT;
    //记录上一次持续监听时，拿到内容的时刻
    public static long LAST_TIME_CONTINUES_LISTENERING;
    //记录上一次持续监听时,由于间隔时间小于2秒，保存的会话内容
    public static String LAST_TIME_CONTINUES_LISTENERING_CONTENT;
    //是否需要开启持续监听
    public static boolean IS_NEED_CONTINUE_LISTEN;
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
    //当前是否正在说话
    public static boolean IS_VOICE_SPEAKING = false;

    public static AVImporter getmAVimporter() {
        return mAVimporter;
    }

    public void setmAVimporter(AVImporter mAVimporter) {
        this.mAVimporter = mAVimporter;
    }

    public static boolean isTestTnputTSzero() {
        return testTnputTSzero;
    }

    public void setTestTnputTSzero(boolean testTnputTSzero) {
        this.testTnputTSzero = testTnputTSzero;
    }

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

    public static long getLastWakeUpTime() {
        return LAST_WAKE_UP_TIME;
    }

    public static void setLastWakeUpTime(long lastWakeUpTime) {
        LAST_WAKE_UP_TIME = lastWakeUpTime;
    }

    public static long getLastActionTime() {
        return LAST_ACTION_TIME;
    }

    public static void setLastActionTime(long lastActionTime) {
        LAST_ACTION_TIME = lastActionTime;
    }

    public static long getLastVoiceFromTime() {
        return LAST_VOICE_FROM_TIME;
    }

    public static void setLastVoiceFromTime(long lastVoiceFromTime) {
        LAST_VOICE_FROM_TIME = lastVoiceFromTime;
    }

    public static long getLastSayHelloTime() {
        return LAST_SAY_HELLO_TIME;
    }

    public static void setLastSayHelloTime(long lastSayHelloTime) {
        LAST_SAY_HELLO_TIME = lastSayHelloTime;
    }

    public static boolean isMoveMode() {
        return IS_MOVE_MODE;
    }

    public static void setIsMoveMode(boolean isMoveMode) {
        IS_MOVE_MODE = isMoveMode;
    }

    public static long getLastTouchHeadTime() {
        return LAST_TOUCH_HEAD_TIME;
    }

    public static void setLastTouchHeadTime(long lastTouchHeadTime) {
        LAST_TOUCH_HEAD_TIME = lastTouchHeadTime;
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

    public static boolean isNeedContinueListen() {
        return IS_NEED_CONTINUE_LISTEN;
    }

    public static void setIsNeedContinueListen(boolean isNeedContinueListen) {
        IS_NEED_CONTINUE_LISTEN = isNeedContinueListen;
    }

    public static String getLastTimeContinuesListeneringContent() {
        return LAST_TIME_CONTINUES_LISTENERING_CONTENT;
    }

    public static void setLastTimeContinuesListeneringContent(String lastTimeContinuesListeneringContent) {
        LAST_TIME_CONTINUES_LISTENERING_CONTENT = lastTimeContinuesListeneringContent;
    }

    public static long getLastTimeContinuesListenering() {
        return LAST_TIME_CONTINUES_LISTENERING;
    }

    public static void setLastTimeContinuesListenering(long lastTimeContinuesListenering) {
        LAST_TIME_CONTINUES_LISTENERING = lastTimeContinuesListenering;
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
