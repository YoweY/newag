package aegis.aegisxunfei;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihancloud.opensdk.base.BindBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.FaceRecognizeBean;
import com.qihancloud.opensdk.function.beans.LED;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.beans.StreamOption;
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
import com.qihancloud.opensdk.function.unit.interfaces.media.MediaStreamListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.SpeakListener;
import com.qihancloud.opensdk.function.unit.interfaces.speech.WakenListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import aegis.aegisxunfei.robbot_action.Head;
import aegis.aegisxunfei.robbot_action.Led;
import aegis.aegisxunfei.robbot_action.RobbotActionBean;
import aegis.aegisxunfei.robbot_action.Wheel;
import aegis.aegisxunfei.robbot_action.Wing;
import aegis.aegisxunfei.voiceRecord.VoiceLineView;
import cn.tee3.avd.AVImporter;
import cn.tee3.avd.ErrorCode;
import cn.tee3.avd.User;

/**
 * Created by mcs on 2016/12/29.
 */


public abstract class MyBaseActivity extends BindBaseActivity {



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
        if (speechManager != null){
            speechManager.doSleep();
        }
        if (mediaManager != null)
            mediaManager.closeStream();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lawPush", " MyBase-----onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //显示正在监听的语音框
    public Dialog voiceListeningDialog;
    private VoiceLineView voiceLineView;
    private TextView timeTextView;
    private int countTime = 0;
    TimeHandler timeHandler;
    public void createVoiceListeningLoadingDialog(Context context) {
        Log.e("lawPush","myBase---createVoiceListeningLoadingDialog");
        hideLoadingDialog();
        View parentView = LayoutInflater.from(context).inflate(R.layout.voice_listening_dialog,null);
        ImageView outRoundImg = (ImageView) parentView.findViewById(R.id.id_round_img);
        timeTextView = (TextView) parentView.findViewById(R.id.id_time);
        voiceLineView = (VoiceLineView) parentView.findViewById(R.id.voicLine);
        voiceListeningDialog = new Dialog(context,R.style.voice_listening_loading_dialog);
        voiceListeningDialog.setContentView(parentView);
        voiceListeningDialog.show();
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.progress_dialog_anim);
        outRoundImg.startAnimation(hyperspaceJumpAnimation);

        countTime = 0;
        timeHandler = new TimeHandler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countTime ++;
                Message message = new Message();
                message.arg1 = countTime;
                timeHandler.sendMessage(message);
            }
        }, 0, 1000);
    }

    // 1.创建Timer对象
    Timer timer;

    class TimeHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            timeTextView.setText(MyConstant.formatMillTool(msg.arg1));
        }
    }

    //隐藏正在监听的语音框
    public void hideLoadingDialog() {
        if (voiceListeningDialog != null){
            voiceListeningDialog.cancel();
            voiceListeningDialog = null;
        }
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始语音唤醒监听
     */
    public void startVoiceWakeUp() {
        Log.e("lawPush","myBase---三宝的讯飞语音唤醒监听是在休眠后默认开启的");
    }

    /**
     * 显示前台可见语音识别监听
     */
    public void startFrontVoiceRecognizeListening() {
        MyLawPushApp.setVoiceListeningType(2);
        Log.e("lawPush","可见语音监听，有dialog显示");
        if (speechManager != null){
            createVoiceListeningLoadingDialog(MyBaseActivity.this);
            speechManager.doWakeUp();
        }else {
            Log.e("lawPush","speechManager  == null");
        }
    }

    /**
     * 开启后台语音识别监听
     */
    public void startBackVoiceRecognizeListening() {
        MyLawPushApp.setVoiceListeningType(1);
        Log.e("lawPush","后台持续监听，无dialog显示");
        if (speechManager != null)
            speechManager.doWakeUp();
        else
            Log.e("lawPush","speechManager  == null");
    }

    WheelMotionManager wheelMotionManager;
    DistanceWheelMotion distanceWheelMotion;
    RelativeAngleWheelMotion relativeAngleWheelMotion;
    HandMotionManager handMotionManager;
    HeadMotionManager headMotionManager;
    HardWareManager hardWareManager;
    SystemManager systemManager;
    public MediaManager mediaManager;
    SpeechManager speechManager;

    @Override
    protected void onMainServiceConnected() {
        Log.e("lawPush","   myBase   onMainServiceConnected");
        mediaManager= (MediaManager)getUnitManager(FuncConstant.MEDIA_MANAGER);
        StreamOption streamOption = new StreamOption();
        streamOption.setDecodType(StreamOption.HARDWARE_DECODE);
        streamOption.setChannel(StreamOption.SUB_STREAM);
        mediaManager.openStream(streamOption);
        mediaManager.setMediaListener(new MediaStreamListener() {
            @Override
            public void getVideoStream(byte[] bytes) {
//                Log.e("lawPush","  数据来啦："+bytes.length);
                if (null != MyLawPushApp.getmAVimporter() && MyLawPushApp.getmAVimporter().isWorking()) {
                    int ret = MyLawPushApp.getmAVimporter().video_input264Frame((MyLawPushApp.isTestTnputTSzero() ? 0 : System.nanoTime()), 1280, 720, bytes, bytes.length);
                    if (0 != ret) {
                        Log.e("lawPush", "video_input264Frame failed. ret="+ret);
                    }
                }
                else{
                    Log.e("lawPush", "null == mAVimporter || !mAVimporter.isWorking()");
                }
            }

            @Override
            public void getAudioStream(byte[] bytes) {
                Log.e("lawPush", "getAudioStream voice length:"+bytes.length+"   ,time:"+System.currentTimeMillis() );
                if (null != MyLawPushApp.getmAVimporter() && MyLawPushApp.getmAVimporter().isWorking()) {
                    int ret = MyLawPushApp.getmAVimporter().audio_inputPCMFrame((MyLawPushApp.testTnputTSzero ? 0 : System.nanoTime()), MyLawPushApp.kSampleRate, 1, bytes, bytes.length);
                    if (0 != ret) {
                        Log.e("lawPush", "video_inputRAWFrame failed. ret="+ret);
                    }
                }
                else{
                    Log.e("lawPush", "塞音频数据异常");
                }
            }
        });
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
                hideLoadingDialog();
                if (MyLawPushApp.isNeedContinueListen())
                    startBackVoiceRecognizeListening();
            }
        });

        speechManager.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(String s) {
                Log.e("lawPush","三宝识别语音文本内容："+s);
                if (s.length() > 1){
                    dealVoiceContent(s);
                }
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {
//                Log.e("lawPush","三宝识别语音音量："+i);
                if (voiceLineView != null)
                    voiceLineView.setVolume(i);
            }
        });

        speechManager.setOnSpeechListener(new SpeakListener() {
            @Override
            public void onSpeakFinish() {
                MyLawPushApp.setIsVoiceSpeaking(false);
            }

            @Override
            public void onSpeakProgress(int i) {

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
                        MyLawPushApp.setIsMoveMode(false);
                        startFrontVoiceRecognizeListening();
                    }
                }

            }
        });



        //人脸识别
        mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(FaceRecognizeBean faceRecognizeBean) {

                Log.e("lawPush", "检测到人脸");
                //主动打招呼
                //说欢迎语，并向前
                long time = System.currentTimeMillis();
                if (time - MyLawPushApp.getLastSayHelloTime() > 20000) {
                    if (!MyLawPushApp.isVoiceSpeaking() && !MyLawPushApp.isGuideDialogShow() && MyLawPushApp.getCurrntActivity().equals("SplashActivity")&&!MyLawPushApp.IS_MOVE_MODE) {
                        if (voiceListeningDialog != null && voiceListeningDialog.isShowing())
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
                if (time - MyLawPushApp.getLastVoiceFromTime() < 5000) {
                    return;
                }else {
                    MyLawPushApp.setLastVoiceFromTime(time);
                }

                if (!MyLawPushApp.isVoiceSpeaking()&&!MyLawPushApp.IS_MOVE_MODE) {
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
               //System.out.print((part == 1 ? "正面" : "背后") + "PIR被触发");
                //isCheck为true表示有人到来
                //isCheck为false表示有人离开
                Log.e("lawPush", "PIR检测    isCheck:" + isCheck + ",part:" + part);
                long time = System.currentTimeMillis();
                if (time - MyLawPushApp.getLastSayHelloTime() > 20000) {
                    if (!MyLawPushApp.isVoiceSpeaking()& !MyLawPushApp.isGuideDialogShow() && MyLawPushApp.getCurrntActivity().equals("SplashActivity")&&!MyLawPushApp.IS_MOVE_MODE ) {
                        if (voiceListeningDialog != null && voiceListeningDialog.isShowing())
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

        speechManager.doWakeUp();
    }

    public void startCountDownTimer() {
    }

    public void stopCountDownTime() {
    }

    //调用语音合成
    public void startSpeak(String content) {
        stopCountDownTime();
        stopSpeak();
        if (content == null )
            content = "您好~";
        if (content != null && content.equals(""))
            content = "您好~";
        if (speechManager != null){
            SpeakOption speakOption = new SpeakOption();
            speakOption.setSpeed(getSharedPreferences("robbotSP", MODE_PRIVATE).getInt("voiceSpeed",50));
            speechManager.startSpeak(content);
            MyLawPushApp.setIsVoiceSpeaking(true);
        }
    }

    //取消语音合成
    public void stopSpeak() {
        if (speechManager != null)
            speechManager.doSleep();
    }

    /**
     * 暂停语音
     */
    public void pauseSpeak(){
//        if (speechManager != null){
//            speechManager.pauseSpeaking();
//        }
    }

    /**
     * 继续语音
     */
    public void resumeSpeak(){
//        if (mSpeechSynthesizer != null){
//            mSpeechSynthesizer.resumeSpeaking();
//        }
    }


    //设置语速稍微快一点
    public void speakFaster() {
        SharedPreferences sharedPreferences = getSharedPreferences("robbotSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int voiceSpeed = sharedPreferences.getInt("voiceSpeed", 50) + 10;
        if (voiceSpeed > 100)
            voiceSpeed = 100;
        editor.putInt("voiceSpeed",voiceSpeed);
        editor.commit();
    }

    //设置语速更快一点
    public void speakMoreFaster() {
        SharedPreferences sharedPreferences = getSharedPreferences("robbotSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int voiceSpeed = sharedPreferences.getInt("voiceSpeed", 50) + 20;
        if (voiceSpeed > 100)
            voiceSpeed = 100;
        editor.putInt("voiceSpeed",voiceSpeed);
        editor.commit();
    }

    //设置语速慢一点
    public void speakSlower() {
        SharedPreferences sharedPreferences = getSharedPreferences("robbotSP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int voiceSpeed = sharedPreferences.getInt("voiceSpeed", 50) - 10;
        if (voiceSpeed < 10)
            voiceSpeed = 10;
        editor.putInt("voiceSpeed",voiceSpeed);
        editor.commit();
    }

    //用户提问的内容
    public void showOnChatUI(String result) {
    }

    //机器人回答的内容
    public void showOnChatUIRobbot(String result) {
    }

    //进行服务请求
    public void sendToServer(String content) {
    }

    public void initVoice(){}
    public void initSpeakParama(){}
    public void releaseAllVoiceResource(){}

    public String getDeviceId() {
        if (systemManager != null)
            return systemManager.getDeviceId().getResult();
        else {
            return "0123456789";
        }
    }

    public void dealVoiceContent(String result) {
        hideLoadingDialog();
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
