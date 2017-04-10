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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import aegis.aegisxunfei.speech.setting.IatSettings;
import aegis.aegisxunfei.speech.setting.TtsSettings;
import aegis.aegisxunfei.speech.util.JsonParser;

/**
 * Created by mcs on 2016/12/29.
 */


public abstract class MyBaseActivity extends AppCompatActivity {

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
        Log.e("lawPush", " base-----onStop");
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
        mSpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, mIatSharedPreferences.getString("iat_vadeos_preference", "60000"));
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
            Log.e("lawPush", "错误码：error:"+error.getErrorCode()+"，描述："+error.getErrorDescription());
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

                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
                else
                    startVoiceWakeUp();
                startCountDownTimer();
            } else {
                Log.e("lawPush", "语音合成完成后，出现了错误：" + error.getPlainDescription(true) + "");

                if (MyLawPushApp.isNeedContinueListen()) {
                    startBackVoiceRecognizeListening();
                } else {
                    startVoiceWakeUp();
                }
                stopCountDownTime();
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
//        if (systemManager != null)
//            return systemManager.getDeviceId().getResult();
//        else {
//            return "0123456789";
//        }
        return "0123456789";
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
    }

    /**
     * 归位
     */
    public void robotHandToReset() {

    }


    /**
     * 主动打招呼
     */
    void robbotActionSayHello() {
    }

    //机器人动作解析执行接口
    public void robotPerformAction(final JSONObject jsonObject) {
    }
}
