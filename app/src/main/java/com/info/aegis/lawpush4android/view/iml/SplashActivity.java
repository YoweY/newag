package com.info.aegis.lawpush4android.view.iml;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.presenter.SplashPresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.VoiceRecording;
import com.info.aegis.lawpush4android.view.SplashActivityService;
import com.info.aegis.lawpush4android.view.handler.SplashHandler;
import com.info.aegis.lawpush4android.view.widget.ChangeSpeedScroller;
import com.info.aegis.lawpush4android.view.widget.CycleViewPager;
import com.qihancloud.opensdk.function.unit.MediaManager;
import com.qihancloud.opensdk.function.unit.interfaces.media.MediaStreamListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * Created by SFS on 2017/2/26.
 * Description :   广告页面
 */
public class SplashActivity extends BaseActivity implements SplashActivityService {
    private CycleViewPager mViewPager;
    private CyclePagerAdapter adapter;
    private ImageView[] mPoints;
    private int[] Imgs = {R.drawable.one, R.drawable.two};

    private LinearLayout mLayout;
    private ImageView mPoint;
    private LinearLayout.LayoutParams params;
    private SplashPresenter splashPresenter;
    private SharedPreferences robbotSP;
    ProgressDialog progressDialog;
    //  轮播图 第一次进入的标记
    private boolean isFirst = false;
    private boolean isSecond = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppManager.getAppManager().addActivity(SplashActivity.this);

        splashPresenter = new SplashPresenter(this);
        handler = new SplashHandler(this);
        robbotSP = getSharedPreferences("robbotSP", MODE_PRIVATE);

        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置样式为圆形样式
        progressDialog.show();
        initView();
        initData();
        Log.e("lawPush", " SplashActivity-----onCreate");

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

        isFirst = true;
        isSecond = true;
        LawPushApp.setCurrntActivity("SplashActivity");
        LawPushApp.setChatChatSaveIngTimes(0);
        Log.e("lawPush", " SplashActivity-----onResume");
        if (!mViewPager.isScroll){
            mViewPager.handler.sendEmptyMessageDelayed(1,500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewPager.isScroll) {
            mViewPager.stopScroll();
        }
        progressDialog.cancel();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        Log.e("lawPush", " SplashActivity-----onPause");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent("com.info.aegis.lawpush4android.exit");
        sendBroadcast(intent);
        finish();
        Log.e("lawPush", " SplashActivity-----onBackPressed");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lawPush", " SplashActivity-----onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("lawPush", " SplashActivity-----onDestroy");
    }

    @Override
    void hideGuideDialog() {
        super.hideGuideDialog();
        if (guideDialog != null) {
            guideDialog.dismiss();
            LawPushApp.setIsGuideDialogShow(false);
        }
        Log.e("lawPush", " SplashActivity-----hideGuideDialog");
    }

    @Override
    void showGuideDialog() {
        super.showGuideDialog();
        createDialog("您好，请问您需要什么帮助吗？");
        Log.e("lawPush", " SplashActivity-----showGuideDialog");
    }

    /**
     * select失败后，再次判断本地要跳转的类型
     *
     * @param content
     */
    void judgeActivityChatType(String content) {
        if (content.matches(".{0,5}程序.{0,5}")) {
            //程序引导
            JumpActivity(1, "", "");
            return;
        }
        if (content.matches(".{0,5}聊.{0,5}")) {
            JumpActivity(3, "", "");
            return;
        }
        if (content.matches(".{0,5}((法律咨询)|(法律问题)|(法律援助)|(咨询)|(法律问答)).{0,5}")|| content.matches(".{0,3}咨询.{0,3}")) {
            JumpActivity(4, "", "");
            return;
        }
        if (content.matches(".{0,5}法.?查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}") || content.matches(".{0,5}法.{1,3}法.{1,3}")) {
            //法规法规
            JumpActivity(5, "", "");
            return;
        }
        JumpActivity(3, content, "");
    }

    @Override
    public void sendToServer(String content) {
        //先匹配本地命令，再匹配select
        if (content.matches(".{0,5}程序.{0,5}")){
            JumpActivity(1,"","");
            return;
        }
        if (content.matches(".{0,5}聊.{0,5}")){
            JumpActivity(3,"","");
            return;
        }
        if (content.matches(".{0,5}((法律咨询)|(法律问题)|(法律援助)|(咨询)|(法律问答)).{0,5}")|| content.matches(".{0,3}咨询.{0,3}")){
            JumpActivity(4,"","");
            return;
        }
        if (content.matches(".{0,5}法.?查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}") || content.matches(".{0,5}法.{1,3}法.{1,3}")){
            JumpActivity(5,"","");
            return;
        }
        //调取select接口匹配
        String choices = "";
        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
            choices = choices + LawPushApp.getScenesBeanList().get(i).getName() + ",";
        }
        if (choices.endsWith(","))
            choices = choices.substring(0, choices.length() - 1);
        splashPresenter.getSelect(choices, content);
        Log.e("lawPush", " SplashActivity-----sendToServer");
        return;
    }

    /**
     * select接口无数据返回，重新进入下一个activity
     *
     * @param content
     */
    public void rePreCause(String content) {
        hideGuideDialog();
        judgeActivityChatType(content);
    }

    /**
     * 匹配select接口中返回的数据
     *
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
        if (scenesBean == null)
            JumpActivity(1, content, "");
        else
            JumpActivity(1, scenesBean.getName(), scenesBean.getId());
    }

    private Context myContext;
    @Override
    public Context getContext() {
        if (myContext == null)
            myContext = SplashActivity.this;
        Log.e("lawPush", " SplashActivity-----getContext");
        return myContext;
    }

    View dialogParentView;
    Dialog guideDialog;
    VoiceRecording mVoiceRecording;

    void createDialog(String title) {
        LawPushApp.setIsGuideDialogShow(true);
        startCountDownTimer();
        try {

            dialogParentView = LayoutInflater.from(myContext).inflate(R.layout.activity_version_court, null);
            TextView titleView, textView01, textView02, textView03, textView04;
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
                    JumpActivity(3, "", "");
                }
            });
            imageView06.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpActivity(4, "", "");
                }
            });
            imageView07.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpActivity(5, "", "");
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

            startSpeak(title);

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
                    stopCountDownTime();
                    stopSpeak();
                }
            });

            WindowManager.LayoutParams params = guideDialog.getWindow().getAttributes();
            params.width = 1920;
            params.height = 1280;
            guideDialog.getWindow().setAttributes(params);

            LawPushApp.setIsGuideDialogShow(true);
            progressDialog.dismiss();

        } catch (Exception e) {
            Log.e("lawPush", "  error:" + e.toString());
        }
    }


    void compareScene(ScenesBean mSceneBean) {
        progressDialog.show();
        ScenesBean scenesBean = null;
        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
            if (mSceneBean.getParent().equals(LawPushApp.getScenesBeanList().get(i).getId())) {
                scenesBean = LawPushApp.getScenesBeanList().get(i);
                break;
            }
        }
        if (scenesBean == null) {
            JumpActivity(3, mSceneBean.getName(), "");
            return;
        }
        JumpActivity(3, mSceneBean.getName(), mSceneBean.getId());
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LawPushApp.setCurrentDeviceId(getDeviceId());
                splashPresenter.getInitData();
            }
        }).start();
    }

    /**
     * 用来进行沉浸式状态栏的设置
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void initFailed() {
        progressDialog.cancel();
        new android.support.v7.app.AlertDialog.Builder(SplashActivity.this).setTitle("初始化错误").setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppManager.getAppManager().finishAllActivity();
            }
        }).setCancelable(false).show();
    }

    public void initSuccess() {
        progressDialog.cancel();
        String name = robbotSP.getString("name", "初始化失败");
        String[] valueArray = name.split(",");
        if (valueArray.length != 2) {
            initFailed();
            return;
        }
        List<ScenesBean> scenesBeanList = new ArrayList<>();
        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
            ScenesBean scenesBean = LawPushApp.getScenesBeanList().get(i);
            if (scenesBean.getPriority() > 4) {
                scenesBeanList.add(scenesBean);
                Log.e("lawPush", "全屏弹框塞数据源：" + i + ", 内容：" + scenesBean.getName());
                if (scenesBeanList.size() > 3) {
                    LawPushApp.setScenesBeanListFirstSome(scenesBeanList);
                    break;
                }
            }
        }

    }

    /**
     * @param type            跳转聊天页的类型
     * @param questionContent 跳转过去后，如果需要进行hot接口，则传值，并将sceneId加上
     * @param sceneId
     */
    private void JumpActivity(int type, String questionContent, String sceneId) {
        Intent intent;
        intent = new Intent(SplashActivity.this, ChatActivity.class);
        intent.putExtra("chatType", type);
        intent.putExtra("questionContent", questionContent);
        intent.putExtra("sceneId", sceneId);
        startActivity(intent);
    }

    private void initView() {
        mViewPager = (CycleViewPager) findViewById(R.id.CycleViewPager);
        mLayout = (LinearLayout) findViewById(R.id.LinearLayout);
        //创建底部导航点
        mPoints = new ImageView[Imgs.length];
        for (int i = 0; i < Imgs.length; i++) {
            mPoint = new ImageView(this);
            params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(10, 0, 15, 0);
            mPoint.setLayoutParams(params);
            mPoints[i] = mPoint;
            if (i == 0) {
                // 默认选中第一张图片
                mPoints[i].setBackgroundResource(R.drawable.shape_point_select);
            } else {
                mPoints[i].setBackgroundResource(R.drawable.shape_point_normal);
            }
            mLayout.addView(mPoint);
        }
        //设置适配器
        adapter = new CyclePagerAdapter();
        mViewPager.setAdapter(adapter);
        //设置滑动速度
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ChangeSpeedScroller scroller = new ChangeSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
            scroller.setDuration(500);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int item;
                if (position == 0) {
                    position = adapter.getCount() - 1;
                } else if (position == adapter.getCount() + 1) {
                    position = 0;
                } else {
                    position -= 1;
                }
                item = position;
                for (int i = 0; i < Imgs.length; i++) {
                    mPoints[item].setBackgroundResource(R.drawable.shape_point_select);
                    if (item != i) {
                        mPoints[i].setBackgroundResource(R.drawable.shape_point_normal);
                    }
                }
//                stopSpeak();

            }



        });
        /**
         * 判断滑动距离 进行滑动或跳转
         */
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            int startX = 0;
            int startY = 0;
            int endX = 0;
            int endY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        stopSpeak();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        endX = (int) event.getRawX();
                        endY = (int) event.getRawY();
                        int dX = endX - startX;
                        int dY = endY - startY;

                        if (Math.abs(dX) < 100 && Math.abs(dY) < 100) {
                            showGuideDialog();
                        }
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

    }

    /**
     * ViewPager 的 适配器
     */
    class CyclePagerAdapter extends PagerAdapter {

        private ImageView imageView;

        @Override
        public int getCount() {
            return Imgs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            imageView = new ImageView(SplashActivity.this);
            imageView.setImageResource(Imgs[position]);
            if (mViewPager.getCurrentItem()/2!= 0) {
                if (isSecond){
                    isSecond=!isSecond;
                    if (LawPushApp.getVoiceListeningType() != 2 && !LawPushApp.isGuideDialogShow())
                        startSpeak("下面教您如何与我沟通，你可以选择摸我的头或者呼喊“小法小法”，此时页面会出现话筒的提示图案，这时代表我在认真听你说话。记住喔~");

                }
            }
            if (mViewPager.getCurrentItem() /2==0) {
                if (isFirst) {
                    if (LawPushApp.getVoiceListeningType() != 2 && !LawPushApp.isGuideDialogShow())
                        startSpeak("我不仅能解答5000个关于生活中遇到的法律问题和2000个诉讼中涉及的流程问题而且还可以陪你聊聊天喔 ");
                    isFirst = !isFirst;
                }
            }

            container.addView(imageView);

            return imageView;
        }
    }
}
