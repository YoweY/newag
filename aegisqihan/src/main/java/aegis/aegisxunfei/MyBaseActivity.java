package aegis.aegisxunfei;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.sunflower.FlowerCollector;
import com.qihancloud.opensdk.base.BindBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
import com.qihancloud.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;
import com.qihancloud.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.WakenListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aegis.aegisxunfei.robbot_action.Head;
import aegis.aegisxunfei.robbot_action.Led;
import aegis.aegisxunfei.robbot_action.RobbotActionBean;
import aegis.aegisxunfei.robbot_action.Wheel;
import aegis.aegisxunfei.robbot_action.Wing;
import aegis.aegisxunfei.speech.setting.IatSettings;
import aegis.aegisxunfei.speech.setting.TtsSettings;
import aegis.aegisxunfei.speech.util.JsonParser;

/**
 * Created by mcs on 2016/12/29.
 */


public abstract class MyBaseActivity extends BindBaseActivity {

    // 语音听写对象
    SpeechRecognizer mSpeechRecognizer;
    // 语音听写UI
    RecognizerDialog mRecognizerDialog;
    // 用HashMap存储听写结果
    HashMap<String, String> mIatResultHashMap = new LinkedHashMap<>();

    // 语音唤醒对象
    VoiceWakeuper mVoiceWakeuper;
    //门限值
    int curThresh = 0;
    String keep_alive = "1";
    String ivwNetMode = "1";

    SharedPreferences mIatSharedPreferences;
    SharedPreferences mTtsSharedPreferences;

    // 语音合成对象
    SpeechSynthesizer mSpeechSynthesizer;
    // 默认发音人
    String voicer = "vinn";

    // 语义理解对象（文本到语义）。
    TextUnderstander mTextUnderstander;

    // 设置听写引擎
    String mEngineType = SpeechConstant.TYPE_CLOUD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("lawPush", " MyBase-----onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("lawPush", " MyBase-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("lawPush", " MyBase-----onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lawPush", " MyBase-----onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechSynthesizer != null){
            mSpeechSynthesizer.stopSpeaking();
            mSpeechSynthesizer.destroy();
        }
    }

    /**
     * 开始语音唤醒监听
     */
    public void startVoiceWakeUp() {
        releaseAllVoiceResource();
        initVoice();
        robotHandToReset();
        //非空判断，防止因空指针使程序崩溃
        mVoiceWakeuper = VoiceWakeuper.getWakeuper();
        if (mVoiceWakeuper != null) {
            // 清空参数
            mVoiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            curThresh = getSharedPreferences("robbotSP", MODE_PRIVATE).getInt("voiceLimit", 0);
            mVoiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // 设置唤醒模式
            mVoiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mVoiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
            // 设置闭环优化网络模式
            mVoiceWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
            // 设置唤醒资源路径，保存最近一分钟的音频
            mVoiceWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, getResource());

            // 启动唤醒
            mVoiceWakeuper.startListening(mWakeuperListener);
            Log.e("lawPush", "启动了语音唤醒监听");
        } else {
            Log.e("lawPush", "语音唤醒未初始化");
        }
    }

    /**
     * 显示前台可见语音识别监听
     */
    public void startFrontVoiceRecognizeListening() {
        MyLawPushApp.setVoiceListeningType(2);
        releaseAllVoiceResource();
        initVoice();
        robotHandToReset();

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, mIatSharedPreferences.getString("iat_vadbos_preference", "3000"));
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, mIatSharedPreferences.getString("iat_vadeos_preference", "1500"));
        // 显示听写对话框
        mRecognizerDialog.setListener(mRecognizerDialogListener);
        try {
            mRecognizerDialog.show();
            Log.e("lawPush", " 语音听写对话框展示");
        } catch (Exception e) {
            Log.e("lawPush", " 语音听写对话框展示的时候出现了问题：" + e.toString());
        }
    }

    /**
     * 开启后台语音识别监听
     */
    public void startBackVoiceRecognizeListening() {
        MyLawPushApp.setVoiceListeningType(1);
        releaseAllVoiceResource();
        initVoice();
        robotHandToReset();

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, mIatSharedPreferences.getString("iat_vadbos_preference", "60000"));
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, mIatSharedPreferences.getString("iat_vadeos_preference", "10000"));
        mSpeechRecognizer.startListening(mRecognizerListener);
    }

    /**
     * 释放所有的录音资源
     */
    public void releaseAllVoiceResource() {
        mVoiceWakeuper = VoiceWakeuper.getWakeuper();
        if (mVoiceWakeuper != null && mVoiceWakeuper.isListening()) {
            mVoiceWakeuper.cancel();
            mVoiceWakeuper.destroy();
        }
        if (mSpeechRecognizer != null && mSpeechRecognizer.isListening()) {
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
    }



    public void initSpeakParama(){
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, mTtsSharedPreferences.getString("speed_preference", "70"));
            //设置合成音调
            mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, mTtsSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, mTtsSharedPreferences.getString("volume_preference", "50"));
        } else {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合 成发音人 voicer为空，默认通过语记界面指定发音人。
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "voicer");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, mTtsSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    public void initVoice() {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mSpeechRecognizer = SpeechRecognizer.createRecognizer(this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mRecognizerDialog = new RecognizerDialog(this, mInitListener);



        mIatSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        mTtsSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);
        mTextUnderstander = TextUnderstander.createTextUnderstander(this, textUnderstanderListener);
        // 设置参数
        // 清空参数
        mSpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        mSpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mSpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mIatSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mSpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mSpeechRecognizer.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, mIatSharedPreferences.getString("iat_vadbos_preference", "3000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, mIatSharedPreferences.getString("iat_vadeos_preference", "1500"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mSpeechRecognizer.setParameter(SpeechConstant.ASR_PTT, mIatSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");

        mVoiceWakeuper = VoiceWakeuper.createWakeuper(this, null);

        mRecognizerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!MyLawPushApp.getVoiceListeneringContent().equals("")) {
                    dealVoiceContent(MyLawPushApp.getVoiceListeneringContent());
                    MyLawPushApp.setVoiceListeneringContent("");
                }
                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
                else
                    startVoiceWakeUp();
            }
        });
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = getResult(results);
            MyLawPushApp.setVoiceListeneringContent(result);
            //这里可以识别多个文本
            if (isLast) {
                MyLawPushApp.setVoiceListeneringContent("");
                dealVoiceContent(result);
                Log.e("lawPush", "前台监听器识别到的文本为---" + result);
                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
                else
                    startVoiceWakeUp();

            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            Log.e("lawPush", "前台可见监听识别出错：" + error.getPlainDescription(true));
            mRecognizerDialog.dismiss();
            startSpeak("没听清，再说一遍吧");
        }
    };


    /**
     * 无界面听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            Log.e("lawPush", "sdk内部录音机已经准备好了，用户可以开始语音输入");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            Log.e("lawPush", "错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。");
            if (MyLawPushApp.isNeedContinueListen())
                startBackVoiceRecognizeListening();
            else
                startVoiceWakeUp();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.e("lawPush", "检测到了语音的尾端点，已经进入识别过程，不再接受语音输入");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String result = JsonParser.parseIatResult(results.getResultString());
            Log.e("lawPush", "非UI 监听器 识别到的内容:" + result);
            if (result.length() > 1) {
                dealVoiceContent(result);
            }
            if (isLast) {
                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
                else
                    startVoiceWakeUp();
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            Log.e("lawPush","非UI 监听器 发生了onEvent事件，还未处理，eventType:"+eventType);
        }
    };

    public void startCountDownTimer() {
    }

    public void stopCountDownTime() {
    }

    /**
     * 语音合成部分的监听
     */
    SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            Log.e("lawPush", "开始说话");
            startVoiceWakeUp();
            stopCountDownTime();
            MyLawPushApp.setIsVoiceSpeaking(true);
        }

        @Override
        public void onSpeakPaused() {
            Log.e("lawPush", "暂停说话");
        }

        @Override
        public void onSpeakResumed() {
            Log.e("lawPush", "继续说话");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.e("lawPush", "语音合成结束，正常");
                startCountDownTimer();
                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
                else
                    startVoiceWakeUp();

            } else {
                Log.e("lawPush", "语音合成完成后，出现了错误：" + error.getPlainDescription(true) + "");

                if (MyLawPushApp.isNeedContinueListen()) {
                    startBackVoiceRecognizeListening();
                } else {
                    startVoiceWakeUp();
                }
            }

            MyLawPushApp.setIsVoiceSpeaking(false);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
        }
    };

    /**
     * 初始化监听器（文本到语义）。
     */
    InitListener textUnderstanderListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("lawPuhs", "初始化文本到语义失败,错误码：" + code);
            } else {
                Log.e("lawPuhs", "初始化文本到语义成功");
            }
        }
    };

    private String getResource() {
        String resPath = ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + getString(R.string.app_id) + ".jet");
        Log.e("lawPush", "设置唤醒资源路径 resPath: " + resPath);
        return resPath;
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("lawPush", "初始化监听失败,错误码：" + code);
            }
        }
    };

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("lawPush", "初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * UI语音合成结果
     *
     * @param results
     * @return
     */
    String getResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResultHashMap.put(sn, text);

        StringBuilder resultBuffer = new StringBuilder();
        for (String key : mIatResultHashMap.keySet()) {
            resultBuffer.append(mIatResultHashMap.get(key));
        }
        return resultBuffer.toString();
    }

    /**
     * 唤醒监听器
     */
    public WakeuperListener mWakeuperListener = new WakeuperListener() {
        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void onResult(WakeuperResult result) {
            startFrontVoiceRecognizeListening();
        }

        @Override
        public void onError(SpeechError error) {
            Log.e("lawPush", error.getPlainDescription(true) + "唤醒监听器");
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
        }

        @Override
        public void onVolumeChanged(int volume) {
        }
    };

    //进行服务请求
    public void sendToServer(String content) {
    }

    //用户提问的内容
    public void showOnChatUI(String result) {
    }

    //机器人回答的内容
    public void showOnChatUIRobbot(String result) {
    }

    //调用语音合成
    public void startSpeak(String content) {
        releaseAllVoiceResource();
        stopCountDownTime();
        stopSpeak();
        if (content == null )
            content = "您好~";
        if (content != null && content.equals(""))
            content = "您好~";
        mSpeechSynthesizer.startSpeaking(content, mTtsListener);
    }

    //取消语音合成
    public void stopSpeak() {
        if (mSpeechSynthesizer != null){
            mSpeechSynthesizer.pauseSpeaking();
            mSpeechSynthesizer.stopSpeaking();

        }
    }

    /**
     * 暂停语音
     */
    public void pauseSpeak(){
        if (mSpeechSynthesizer != null){
            mSpeechSynthesizer.pauseSpeaking();
        }
    }

    /**
     * 继续语音
     */
    public void resumeSpeak(){
        if (mSpeechSynthesizer != null){
            mSpeechSynthesizer.resumeSpeaking();
        }
    }


//    //判断当前是否正在朗读
//    public boolean isSpeakingNow() {
//        if (mSpeechSynthesizer != null) {
//            if (mSpeechSynthesizer.isSpeaking())
//                return true;
//            else
//                return false;
//        }
//        return false;
//    }

    //设置语速稍微快一点
    public void speakFaster() {
        int currentVoiceSpeed = Integer.parseInt(mTtsSharedPreferences.getString("speed_preference", "50"));
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, (currentVoiceSpeed + 10) + "");
        mTtsSharedPreferences.edit().putString("speed_preference", (currentVoiceSpeed + 10) + "").apply();
    }

    //设置语速更快一点
    public void speakMoreFaster() {
        int currentVoiceSpeed = Integer.parseInt(mTtsSharedPreferences.getString("speed_preference", "50"));
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, (currentVoiceSpeed + 20) + "");
        mTtsSharedPreferences.edit().putString("speed_preference", (currentVoiceSpeed + 20) + "").apply();
    }

    //设置语速慢一点
    public void speakSlower() {
        int currentVoiceSpeed = Integer.parseInt(mTtsSharedPreferences.getString("speed_preference", "50"));
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, (currentVoiceSpeed - 10) + "");
        mTtsSharedPreferences.edit().putString("speed_preference", (currentVoiceSpeed - 10) + "").apply();
    }

    public String getDeviceId() {
        if (systemManager != null)
            return systemManager.getDeviceId().getResult();
        else {
            return "0123456789";
        }
    }

    public void dealVoiceContent(String result) {
    }

    /**
     * 机器人的动作
     *
     * @param type
     */
    public void robbotAction(int type) {
        startSpeak(MyConstant.randomRobbotAction());
        switch (type) {
            case 10:
                //运动模式
                try {
                    distanceWheelMotion = new DistanceWheelMotion(
                            DistanceWheelMotion.ACTION_FORWARD_RUN, 5, 100);
                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }
                startSpeak("运动模式开启啦！");
                break;
            case 1:
                //向前走
                try {
                    distanceWheelMotion = new DistanceWheelMotion(
                            DistanceWheelMotion.ACTION_FORWARD_RUN, 5, 100);
                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }
                break;
            case 2:
                //向左
                try {
                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                            RelativeAngleWheelMotion.TURN_LEFT, 5, 90
                    );
                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                break;
            case 3:
                //向右
                try {
                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                            RelativeAngleWheelMotion.TURN_RIGHT, 5, 90
                    );
                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                break;
            case 4:
                //向后
                try {
                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                            RelativeAngleWheelMotion.TURN_LEFT, 5, 180
                    );
                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                break;
            case 5:
                //转一圈
                try {
                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                            RelativeAngleWheelMotion.TURN_LEFT, 5, 360
                    );
                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                break;
            case 6:
                //握手
                try {
                    AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                            AbsoluteAngleHandMotion.PART_RIGHT, 5, 90);
                    handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                startSpeak("很高兴认识你");
                break;
            case 7:
                startSpeak("很高兴为您服务，立案庭在左手边50米。");
                try {
                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                            RelativeAngleWheelMotion.TURN_LEFT, 5, 90
                    );
                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action open error:" + e.toString());
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                                    AbsoluteAngleHandMotion.PART_RIGHT, 5, 180);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        } catch (Exception e) {
                            Log.e("lawPush", " robot action open error:" + e.toString());
                        }

                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                                    AbsoluteAngleHandMotion.PART_LEFT, 5, 20);
                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                        } catch (Exception e) {
                            Log.e("lawPush", " robot action open error:" + e.toString());
                        }

                    }
                }).start();

                break;
        }
    }

    /**
     * 归位
     */
    public void robotHandToReset() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                            AbsoluteAngleHandMotion.PART_RIGHT, 5, 180);
                    handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action reset error:" + e.toString());
                }

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                            AbsoluteAngleHandMotion.PART_LEFT, 5, 180);
                    handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action reset error:" + e.toString());
                }

            }
        }).start();

        //头部水平
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
                            AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, 90
                    );
                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action reset error:" + e.toString());
                }


            }
        }).start();
        //头部垂直
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
                            AbsoluteAngleHeadMotion.ACTION_VERTICAL, 50
                    );
                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
                } catch (Exception e) {
                    Log.e("lawPush", " robot action reset error:" + e.toString());
                }


            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE, (byte) 10, (byte) 3));
                } catch (Exception e) {
                    Log.e("lawPush", " robot action reset error:" + e.toString());
                }

            }
        }).start();
    }

    WheelMotionManager wheelMotionManager;
    DistanceWheelMotion distanceWheelMotion;
    RelativeAngleWheelMotion relativeAngleWheelMotion;
    HandMotionManager handMotionManager;
    HeadMotionManager headMotionManager;
    HardWareManager hardWareManager;
    SystemManager systemManager;
    MediaManager mediaManager;
    SpeechManager speechManager;

    @Override
    protected void onMainServiceConnected() {
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        MyLawPushApp.setCurrentDeviceId(systemManager.getDeviceId().getResult());

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speechManager.setOnSpeechListener(new WakenListener() {
            @Override
            public void onWakeUp() {
                Log.e("lawPush", "发生了唤醒事件");

            }

            @Override
            public void onSleep() {
                Log.e("lawPush", "发生了休眠事件");
            }
        });

        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager.setOnHareWareListener(new TouchSensorListener() {
            @Override
            public void onTouch(int part) {
                if (part == 11 || part == 12 || part == 13) {
                    //触发语音监听
                    long currenTime = System.currentTimeMillis();
                    if (currenTime - MyLawPushApp.getLastTouchHeadTime() > 5000) {
                        Log.e("lawPush", "头部触发唤醒");
                        MyLawPushApp.setLastTouchHeadTime(currenTime);
                        startFrontVoiceRecognizeListening();
                        MyLawPushApp.setIsMoveMode(false);
                    }
                }

            }
        });

        //人脸识别
        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);
        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(FaceRecognizeBean faceRecognizeBean) {

                Log.e("lawPush", "检测到人脸");
                //主动打招呼
                //说欢迎语，并向前
                long time = System.currentTimeMillis();
                if (time - MyLawPushApp.getLastSayHelloTime() > 15000) {
                    if (!MyLawPushApp.isVoiceSpeaking() && !MyLawPushApp.isGuideDialogShow() && MyLawPushApp.getCurrntActivity().equals("SplashActivity")&&!MyLawPushApp.IS_MOVE_MODE) {
                        if (mRecognizerDialog != null && mRecognizerDialog.isShowing())
                            return;
                        MyLawPushApp.setLastSayHelloTime(time);
                        robbotActionSayHello();
                    }
                }
            }
        });

        //声源定位
        hardWareManager.setOnHareWareListener(new VoiceLocateListener() {
            @Override
            public void voiceLocateResult(int angle) {

                long time = System.currentTimeMillis();
                if (time - MyLawPushApp.getLastVoiceFromTime() < 3000) {
                    return;
                }else {
                    MyLawPushApp.setLastVoiceFromTime(time);
                }

                if (!MyLawPushApp.isVoiceSpeaking()) {
                    if (angle > 45 && angle < 180) {
                            relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                    RelativeAngleWheelMotion.TURN_RIGHT, 5, angle
                            );
                            wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                    }
                    if (angle > 180 && angle < 315) {
                            if (angle > 180)
                                angle = 360 - angle;
                            relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                    RelativeAngleWheelMotion.TURN_LEFT, 5, angle
                            );
                            wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                    }
                }
            }
        });

//        //PIR检测
        hardWareManager.setOnHareWareListener(new PIRListener() {
            @Override
            public void onPIRCheckResult(boolean isCheck, int part) {
//                System.out.print((part == 1 ? "正面" : "背后") + "PIR被触发");
                //isCheck为true表示有人到来
                //isCheck为false表示有人离开
                Log.e("lawPush", "PIR检测    isCheck:" + isCheck + ",part:" + part);
                long time = System.currentTimeMillis();
                if (time - MyLawPushApp.getLastSayHelloTime() > 20000) {
                    if (!MyLawPushApp.isVoiceSpeaking()& !MyLawPushApp.isGuideDialogShow() && MyLawPushApp.getCurrntActivity().equals("SplashActivity")&&!MyLawPushApp.IS_MOVE_MODE ) {
                        if (mRecognizerDialog != null && mRecognizerDialog.isShowing())
                            return;
                        if (isCheck && part == 1) {
                            MyLawPushApp.setLastSayHelloTime(time);
                            robbotActionSayHello();
                        }
                        if (!isCheck && part == 1) {
                            startSpeak("谢谢，再见~");
                        }
                    }
                }


            }
        });

    }

    /**
     * 主动打招呼
     */
    void robbotActionSayHello() {

        startSpeak(MyConstant.randomSayHello());
        try {
            robotPerformAction(new JSONObject("{\"motions\":[[{\"position\":\"\",\"distance\":200,\"type\":\"wheel\",\"color\":\"\"," +
                    "\"direction\":\"forward\"}],[{\"position\":\"right\",\"type\":\"wing\",\"color\":\"\",\"angle\":190,\"direction\":" +
                    "\"up\"},{\"position\":\"\",\"type\":\"head\",\"color\":\"\",\"angle\":26,\"direction\":\"up\"},{\"position\":\"right\"," +
                    "\"type\":\"wing\",\"color\":\"\",\"angle\":90,\"direction\":\"down\"},{\"position\":\"right\",\"type\":\"wing\",\"color\":" +
                    "\"\",\"angle\":190,\"direction\":\"up\"},{\"position\":\"right\",\"type\":\"wing\",\"color\":\"\",\"angle\":90,\"direction\":\"down\"}]]}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 机器人动作
     *
     * @param jsonObject
     */
    List<Wheel> wheelList = new ArrayList<>();
    List<Wing> wingList = new ArrayList<>();
    List<Head> headList = new ArrayList<>();
    List<Led> ledList = new ArrayList<>();
    long totalArrayTime = 0;
    int lastWingAngle, lastHeadAngle;
    ExecutorService robbotActionExecutorService;

    //机器人动作解析执行接口
    public void robotPerformAction(final JSONObject jsonObject) {
        long time = System.currentTimeMillis();
        if (time - MyLawPushApp.getLastActionTime() < 2000) {
            MyLawPushApp.setLastActionTime(time);
            return;
        }
        try {
            robbotActionExecutorService.shutdownNow();
        } catch (Exception e) {

        }
        robbotActionExecutorService = Executors.newFixedThreadPool(1);
        robbotActionExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<RobbotActionBean> robbotActionBeanList = new ArrayList<>();
                    final JSONArray totalJsonArray = jsonObject.getJSONArray("motions");

                    for (int m = 0; m < totalJsonArray.length(); m++) {
                        Log.e("lawPush", "执行到第" + m + "组动作");
                        JSONArray jsonArray = totalJsonArray.getJSONArray(m);
                        robbotActionBeanList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            RobbotActionBean robbotActionBean = new RobbotActionBean();
                            robbotActionBean.setType(jsonObject1.optString("type"));
                            robbotActionBean.setDirection(jsonObject1.optString("direction"));
                            robbotActionBean.setPosition(jsonObject1.optString("position"));
                            robbotActionBean.setColor(jsonObject1.optString("color"));
                            robbotActionBean.setAngle(jsonObject1.optInt("angle"));
                            robbotActionBean.setDistance(jsonObject1.optInt("distance"));
                            robbotActionBean.setTime(jsonObject1.optInt("time"));
                            robbotActionBean.setFlickering(jsonObject1.optBoolean("isFlickering"));
                            int speed = jsonObject1.optInt("speed");
                            if (speed == 0)
                                robbotActionBean.setSpeed(5);
                            else
                                robbotActionBean.setSpeed(speed);
                            robbotActionBeanList.add(robbotActionBean);
                        }

                        wheelList.clear();
                        wingList.clear();
                        headList.clear();
                        ledList.clear();

                        totalArrayTime = 1000;
                        lastWingAngle = 180;
                        lastHeadAngle = 90;

                        for (int i = 0; i < robbotActionBeanList.size(); i++) {
                            RobbotActionBean actionBean = robbotActionBeanList.get(i);
                            switch (actionBean.getType()) {
                                case "wheel":
                                    Wheel wheel = new Wheel();
                                    wheel.setDirection(actionBean.getDirection());
                                    wheel.setDistance(actionBean.getDistance());
                                    wheel.setSpeed(actionBean.getSpeed());
                                    wheel.setAngle(actionBean.getAngle());
                                    wheelList.add(wheel);
                                    break;
                                case "wing":
                                    //翅膀运动
                                    Wing wing = new Wing();
                                    wing.setDirection(actionBean.getDirection());
                                    wing.setPosition(actionBean.getPosition());
                                    wing.setSpeed(actionBean.getSpeed());
                                    wing.setAngle(actionBean.getAngle());
                                    wingList.add(wing);
                                    break;
                                case "head":
                                    Head head = new Head();
                                    head.setDirection(actionBean.getDirection());
                                    head.setAngle(actionBean.getAngle());
                                    headList.add(head);
                                    break;
                                case "LED":
                                    Led led = new Led();
                                    led.setPosition(actionBean.getPosition());
                                    led.setColor(actionBean.getColor());
                                    led.setTime(actionBean.getTime());
                                    led.setFlickering(actionBean.isFlickering());
                                    ledList.add(led);
                                    break;
                            }
                        }

                        //计算时间
                        long totalWheelTime = 1000;
                        if (wheelList.size() != 0) {
                            for (int i = 0; i < wheelList.size(); i++) {
                                int sleepTime = 0;
                                switch (wheelList.get(i).getDirection()) {
                                    case "forward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "backward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "right":
                                        sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "left":
                                        sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "right_forward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "left_forward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "right_backward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "left_backward":
                                        sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                        break;
                                    case "left_circle":
                                        sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 100;
                                        break;
                                    case "right_circle":
                                        sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 100;
                                        break;
                                }
                                totalWheelTime = totalWheelTime + sleepTime;
                            }
                        }
                        long totalWingTime = 1000;
                        if (wingList.size() != 0) {
                            for (int i = 0; i < wingList.size(); i++) {
                                Wing wing = wingList.get(i);
                                int value = Math.abs(lastWingAngle - wing.getAngle());
                                lastWingAngle = wing.getAngle();
                                long sleepTime = value / wing.getSpeed() * 70;
                                ;
                                totalWingTime = totalWingTime + sleepTime;
                            }
                        }
                        long totalHeadTime = 1000;
                        if (headList.size() != 0) {
                            for (int i = 0; i < headList.size(); i++) {
                                int value = Math.abs(lastHeadAngle - headList.get(i).getAngle());
                                lastHeadAngle = headList.get(i).getAngle();
                                long sleepTime = value / 18 * 300;
                                totalHeadTime = totalHeadTime + sleepTime;
                            }
                        }
                        long maxTime = (totalWheelTime > totalWingTime ? totalWheelTime : totalWingTime) > totalHeadTime ? (totalWheelTime > totalWingTime ? totalWheelTime : totalWingTime) : totalHeadTime;
                        totalArrayTime = totalArrayTime + maxTime;

                        lastWingAngle = 180;
                        lastHeadAngle = 90;

                        if (wheelList.size() != 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < wheelList.size(); i++) {
                                            int sleepTime = 0;
                                            switch (wheelList.get(i).getDirection()) {
                                                case "forward":
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_FORWARD_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 前进:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "backward":
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_BACK_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 后退:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "right":
                                                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                                            RelativeAngleWheelMotion.TURN_RIGHT, wheelList.get(i).getSpeed(), wheelList.get(i).getAngle()
                                                    );
                                                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 右转:" + wheelList.get(i).getAngle() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "left":
                                                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                                            RelativeAngleWheelMotion.TURN_LEFT, wheelList.get(i).getSpeed(), wheelList.get(i).getAngle()
                                                    );
                                                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 左转:" + wheelList.get(i).getAngle() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "right_forward":
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_RIGHT_FORWARD_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 右前方行进:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "left_forward":
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_LEFT_FORWARD_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 左前方行进:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "right_backward":
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_RIGHT_BACK_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 右后方行进:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "left_backward":
                                                    wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
                                                    distanceWheelMotion = new DistanceWheelMotion(
                                                            DistanceWheelMotion.ACTION_LEFT_BACK_RUN, wheelList.get(i).getSpeed(), wheelList.get(i).getDistance());
                                                    wheelMotionManager.doDistanceMotion(distanceWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 左后方行进:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getDistance() / wheelList.get(i).getSpeed() * 250;
                                                    break;
                                                case "left_circle":
                                                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                                            RelativeAngleWheelMotion.TURN_LEFT, wheelList.get(i).getSpeed(), 360
                                                    );
                                                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 左转弯:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 100;
                                                    break;
                                                case "right_circle":
                                                    relativeAngleWheelMotion = new RelativeAngleWheelMotion(
                                                            RelativeAngleWheelMotion.TURN_RIGHT, wheelList.get(i).getSpeed(), 360
                                                    );
                                                    wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
                                                    Log.e("lawPush", "  robot action  wheel 右转弯:" + wheelList.get(i).getDirection() + wheelList.get(i).getDistance() + "，速度：" + wheelList.get(i).getSpeed());
                                                    sleepTime = wheelList.get(i).getAngle() / wheelList.get(i).getSpeed() * 100;
                                                    break;
                                            }
                                            try {
                                                Log.e("lawPush", " 轮子sleepTime：" + sleepTime);
                                                Thread.sleep(sleepTime);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }).start();
                        }
                        if (wingList.size() != 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < wingList.size(); i++) {
                                            final Wing wing = wingList.get(i);
                                            int value = Math.abs(lastWingAngle - wing.getAngle());
                                            lastWingAngle = wing.getAngle();
                                            long sleepTime = value / wing.getSpeed() * 70;
                                            ;
                                            switch (wing.getPosition()) {
                                                case "all":
                                                    //同时做绝对角度
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_LEFT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);

                                                        }
                                                    }).start();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_RIGHT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
                                                        }
                                                    }).start();

                                                    Log.e("lawPush", "  robot action  wing:" + "位置：" + wing.getPosition() + "方向：" + wing.getDirection() + "，角度：" + wing.getAngle() + ",速度：" + wing.getSpeed());
                                                    break;
                                                case "right":
                                                    switch (wing.getDirection()) {
                                                        case "up":
                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion1 = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_RIGHT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion1);
                                                            Log.e("lawPush", "  robot action  wing:" + "位置：" + wing.getPosition() + "方向：" + wing.getDirection() + "，角度：" + wing.getAngle() + ",速度：" + wing.getSpeed());
                                                            break;
                                                        case "down":

                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion2 = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_RIGHT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion2);
                                                            Log.e("lawPush", "  robot action  wing:" + "位置：" + wing.getPosition() + "方向：" + wing.getDirection() + "，角度：" + wing.getAngle() + ",速度：" + wing.getSpeed());
                                                            break;
                                                    }
                                                    break;
                                                case "left":
                                                    switch (wing.getDirection()) {
                                                        case "up":
                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion1 = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_LEFT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion1);
                                                            Log.e("lawPush", "  robot action  wing:" + "位置：" + wing.getPosition() + "方向：" + wing.getDirection() + "，角度：" + wing.getAngle() + ",速度：" + wing.getSpeed());
                                                            break;
                                                        case "down":
                                                            AbsoluteAngleHandMotion absoluteAngleHandMotion2 = new AbsoluteAngleHandMotion(
                                                                    AbsoluteAngleHandMotion.PART_LEFT, wing.getSpeed(), wing.getAngle()
                                                            );
                                                            handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion2);
                                                            Log.e("lawPush", "  robot action  wing:" + "位置：" + wing.getPosition() + "方向：" + wing.getDirection() + "，角度：" + wing.getAngle() + ",速度：" + wing.getSpeed());
                                                            break;
                                                    }
                                                    break;
                                            }
                                            try {
                                                Log.e("lawPush", " 翅膀 sleepTime：" + sleepTime);
                                                Thread.sleep(sleepTime);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }).start();
                        }
                        if (headList.size() != 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < headList.size(); i++) {
                                            Head head = headList.get(i);
                                            switch (head.getDirection()) {
                                                case "up":
                                                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion1 = new AbsoluteAngleHeadMotion(
                                                            AbsoluteAngleHeadMotion.ACTION_VERTICAL, head.getAngle());
                                                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion1);
                                                    Log.e("lawPush", "  robot action  head:" + " 方向：" + head.getDirection() + ",角度：" + head.getAngle());
                                                    break;
                                                case "down":
                                                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion2 = new AbsoluteAngleHeadMotion(
                                                            AbsoluteAngleHeadMotion.ACTION_VERTICAL, head.getAngle());
                                                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion2);
                                                    Log.e("lawPush", "  robot action  head:" + " 方向：" + head.getDirection() + ",角度：" + head.getAngle());
                                                    break;
                                                case "right":
                                                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion3 = new AbsoluteAngleHeadMotion(
                                                            AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, head.getAngle());
                                                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion3);
                                                    Log.e("lawPush", "  robot action  head:" + " 方向：" + head.getDirection() + ",角度：" + head.getAngle());
                                                    break;
                                                case "left":
                                                    AbsoluteAngleHeadMotion absoluteAngleHeadMotion4 = new AbsoluteAngleHeadMotion(
                                                            AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, head.getAngle());
                                                    headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion4);
                                                    Log.e("lawPush", "  robot action  head:" + " 方向：" + head.getDirection() + ",角度：" + head.getAngle());
                                                    break;
                                            }
                                            try {
                                                int value = Math.abs(lastHeadAngle - head.getAngle());
                                                lastHeadAngle = head.getAngle();
                                                long sleepTime = value / 18 * 300;
                                                Log.e("lawPush", " sleepTime：" + sleepTime);
                                                Thread.sleep(sleepTime);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {

                                    }

                                }
                            }).start();
                        }
                        if (ledList.size() != 0) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < ledList.size(); i++) {
                                        Led led = ledList.get(i);
                                        switch (led.getPosition()) {
                                            case "all":
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {

                                                        }
                                                        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                        Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM": {
                                                        hardWareManager.setLED(new LED(LED.PART_ALL, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                        Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                    }
                                                    break;
                                                }
                                                break;
                                            case "right":
//                                    右翅膀LED灯
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM": {
                                                        hardWareManager.setLED(new LED(LED.PART_RIGHT_HAND, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                        Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                    }
                                                    break;
                                                }
                                                break;
                                            case "left":
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM": {
                                                        hardWareManager.setLED(new LED(LED.PART_LEFT_HAND, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                        Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                    }

                                                    break;
                                                }
                                                break;
                                            case "bottom":
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM": {
                                                        hardWareManager.setLED(new LED(LED.PART_WHEEL, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                        Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                    }

                                                    break;
                                                }
                                                break;
                                            case "head_left":
//                                    头部左方LED灯'
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_LEFT_HEAD, LED.MODE_FLICKER_RANDOM_THREE_GROUP, (byte) led.getTime(), (byte) 7));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                }
                                                break;
                                            case "head_right":
                                                switch (led.getColor()) {
                                                    case "WHITE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_WHITE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RED":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_RED, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "GREEN":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_GREEN, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PINK":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_PINK, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "PURPLE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_PURPLE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "BLUE":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_BLUE, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "YELLOW":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_YELLOW, (byte) 10, (byte) 0));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                    case "RANDOM":
                                                        if (led.isFlickering()) {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_RANDOM, (byte) led.getTime(), (byte) 7));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        } else {
                                                            hardWareManager.setLED(new LED(LED.PART_RIGHT_HEAD, LED.MODE_FLICKER_RANDOM_THREE_GROUP, (byte) led.getTime(), (byte) 7));
                                                            Log.e("lawPush", "  robot action  led： " + "灯位置：" + led.getPosition() + ",灯颜色：" + led.getColor());
                                                        }
                                                        break;
                                                }
                                                break;
                                        }
                                    }
                                }
                            }).start();
                        }
                        try {
                            Log.e("lawPush", "第" + m + " 组休眠时间----" + totalArrayTime);
                            Thread.sleep(totalArrayTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //休眠5秒后，回到初始状态
                    try {
                        Log.e("lawPush", " 执行完毕---2秒后重置-");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    robotHandToReset();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("lawPush", " 解析动作----" + e.toString());
                }
            }
        });

    }
}
