package com.info.aegis.lawpush4android.view.iml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.presenter.MyBroadcastReceiver;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.DataDetail;
import com.info.aegis.lawpush4android.presenter.DataDetailPresenter;
import com.info.aegis.lawpush4android.presenter.adapter.ContentAdapter;
import com.info.aegis.lawpush4android.presenter.adapter.MenuAdapter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.view.DateDetailActivityService;
import com.info.aegis.lawpush4android.view.handler.DataDetailHandler;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;

import aegis.aegisxunfei.MyLawPushApp;


public class DataDetailActivity extends BaseActivity implements DateDetailActivityService {

    /**
     * 右侧菜单
     */
    private ListView mRightMenu;
    /**
     * 左侧内容
     */
    private ListView mLeftHome;


    private MenuAdapter menuAdapter;
    private ContentAdapter contentAdapter;


    private int currentItem;
    /**
     * 数据源
     */
    private String data[] = {"法律法规", "诉讼请求", "证据分析", "相关案例", "司法观点", "诉讼成本", "诉讼流程", " 二 维 码 "};

    /**
     * 里面存放右边ListView需要显示标题的条目position
     */
    private ArrayList<Integer> showTitle;
    public int width;
    public int height;
    private RelativeLayout mBack;

    private FrameLayout mFramLayout;
    private RelativeLayout mRelativeLayout;
    private DrawerLayout mDrawerlayout;
    private boolean isOpen = true;
    private View mMenuView;
    private ImageView mRightBack;
    private View mHomeView;
    private View mLeftOpen;
    private ImageView mOpen;
    private ImageView mMenuVoice;

    private Context context;
    private DataDetailPresenter presenter;
    private int type;

    public static final int DATA = 1;
    private String qid;
    private RelativeLayout mLoadPager;
    private DataDetail detail;
    private MyBroadcastReceiver receiver;

    private String speakContent = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        width = metric.widthPixels;
        // 屏幕高度（像素）
        height = metric.heightPixels;
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_data_detail);

        AppManager.getAppManager().addActivity(DataDetailActivity.this);

        handler = new DataDetailHandler(DataDetailActivity.this);
        presenter = new DataDetailPresenter(this);
        presenter.getReport();

        currentItem = getIntent().getIntExtra("item", 0);
        if (currentItem > data.length - 1) {
            currentItem = data.length - 1;
        }

        qid = getSharedPreferences(LawPushApp.USER, Context.MODE_PRIVATE).getString("qid", "");
        initView();
        onListener();
        Log.e("lawPush","DataDetailActivity-----onCreate");
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
        LawPushApp.setCurrntActivity("DataDetailActivity");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSpeak("您好,以下是详细的咨询报告，仅供参考");
            }
        }).start();
        startCountDownTimer();
        Log.e("lawPush","DataDetailActivity-----onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        stopSpeak();
        releaseAllVoiceResource();
        stopCountDownTime();
        hideGuideDialog();
        Log.e("lawPush","DataDetailActivity-----onPause");
        AppManager.getAppManager().removeActivityStack(DataDetailActivity.this);
        finish();

    }

    /**
     * 初始化View
     */
    private void initView() {
        mBack = (RelativeLayout) findViewById(R.id.id_back_relay);
        //加载页面
        mLoadPager = (RelativeLayout) findViewById(R.id.fl_load);
        mLoadPager.setVisibility(View.VISIBLE);
        final TextView mLoadText = (TextView) mLoadPager.findViewById(R.id.load_text);
        mLoadText.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadText.setText("小法正在努力生成咨询报告中.....");
                    }
                });
            }
        },5000);
        //获取抽屉控件
        mDrawerlayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mFramLayout = (FrameLayout) findViewById(R.id.fl_menu);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.fl_content);
        //获取菜单页面视图
        mMenuView = View.inflate(this, R.layout.item_menu, null);
        mRightBack = (ImageView) mMenuView.findViewById(R.id.right_back);
        mMenuVoice = (ImageView) mMenuView.findViewById(R.id.item_menu_voice);
        mRightMenu = (ListView) mMenuView.findViewById(R.id.lv_menu);

        mFramLayout.addView(mMenuView);
        //添加 HOME内容
        mHomeView = View.inflate(this, R.layout.item_home, null);
        mLeftHome = (ListView) mHomeView.findViewById(R.id.lv_home);
        mRelativeLayout.addView(mHomeView);
        //添加打开菜单的按钮
        mLeftOpen = View.inflate(this, R.layout.open_menu, null);
        mRelativeLayout.addView(mLeftOpen);
        mOpen = (ImageView) mLeftOpen.findViewById(R.id.left_back);
        showTitle = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (i == 0) {
                showTitle.add(i);

            } else if (!TextUtils.equals(data[i], data[data.length - 1])) {
                showTitle.add(i);

            } else if (i == data.length - 1) {
                showTitle.add(i);
            }
        }
        onListener();
    }

    /**
     * 设置数据
     *
     * @param detail
     */
    public void setData(DataDetail detail) {

        if (detail == null) {
            setContentView(R.layout.error);
            mBack = (RelativeLayout) findViewById(R.id.id_back_relay);
            onListener();
            return;
        }
        this.detail = detail;
        mLoadPager.setVisibility(View.GONE);
        contentAdapter = new ContentAdapter(DataDetailActivity.this, data, detail);
        contentAdapter.setWidth(width);
        contentAdapter.setQid(qid);
        mLeftHome.setAdapter(contentAdapter);
        //左侧默认选中
        mLeftHome.setSelection(currentItem);
        menuAdapter = new MenuAdapter(this, data);
        menuAdapter.setSelectItem(currentItem);
        menuAdapter.setWidth(width);
        menuAdapter.setHeight(height);
        mRightMenu.setAdapter(menuAdapter);


        new Thread() {
            @Override
            public void run() {

                Request<Bitmap> request = NoHttp.createImageRequest("http://180.96.11.69:8500/api/tools/qr_code?" +
                        "url=https://law.push.aegis-info.com/newDataPage/" + qid, RequestMethod.GET);
                final Response<Bitmap> response = NoHttp.startRequestSync(request);

                mMenuVoice.post(new Runnable() {
                    @Override
                    public void run() {
                        mMenuVoice.setImageBitmap(response.get());
                    }
                });

            }
        }.start();

    }

    /**
     * 设置监听
     */
    private void onListener() {

        mRightMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuAdapter.setSelectItem(position);
                menuAdapter.notifyDataSetInvalidated();
                mLeftHome.setSelection(position);
                String point = "";
                switch (position) {
                    case 0:
                        for (int i = 0; i < detail.getLaws().size(); i++) {
                            point += detail.getLaws().get(i).getName() + ",";
                        }
                        point = point.replace("<", " ,");
                        point = point.replace(">", " ,");
                        speakContent = "您好,您当前查看的是与该问题相关的法律法规,";
                        point = "";
                        break;
                    case 1:
                        if (detail.getClaims() != null && detail.getClaims().size() > 0) {
                            speakContent = "您当前查看的是与该问题相关的诉讼请求,以及相关的败诉原因,您当前的诉讼请求为," + detail.getClaims().get(0).getName() + ",支持率为百分之" + detail.getClaims().get(0).getSupport() + ",驳回率为百分之" +
                                    detail.getClaims().get(0).getReject() + ",败诉原因分析,败诉原因有,举证不力,欠缺法律依据,不满足法定条件,欠缺事实依据,夫妻感情尚未破裂,考虑子女成长,不在审理范围,影响案件胜败诉的因素很多，以上数据是根据现有的以往判决书进行统计得出，不能作为法院最终判案的依据。";

                        } else {
                            speakContent = "您当前查看的是与该问题相关的诉讼请求,以及败诉原因分析";
                        }

                        break;
                    case 2:
                        speakContent = "您当前查看的是与该问题相关的证据分析";

                        break;
                    case 3:
                        speakContent = "您当前查看的是与该问题相关的案例";
                        break;
                    case 4:
                        for (int i = 0; i < detail.getPoints().size(); i++) {
                            point += detail.getPoints().get(i).getTitle() + ",";
                        }
                        speakContent = "您当前查看的是与该问题相关的司法观点";
                        point = "";
                        break;
                    case 5:
                        speakContent = "以下是相关的诉讼成本分析";
                        break;
                    case 6:
                        speakContent = "以下是相关的诉讼流程";
                        break;
                    case 7:
                        speakContent = "请扫描二维码，保存咨询报告!";
                        break;
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
                startCountDownTimer();
            }
        });
        mLeftHome.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
                menuAdapter.notifyDataSetChanged();
                startCountDownTimer();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    return;
                }
               stopCountDownTime();
                mRightMenu.setClickable(false);
                mRightMenu.setFocusable(false);
                int current = showTitle.indexOf(firstVisibleItem);
                if (currentItem != current && current >= 0) {
                    currentItem = current;
                    menuAdapter.setSelectItem(currentItem);
                    menuAdapter.notifyDataSetInvalidated();
                }
                //右面菜单选中倒数第二条
                if (visibleItemCount + firstVisibleItem == totalItemCount - 1) {
                    mRightMenu.setSelection(totalItemCount - 1);
                    menuAdapter.setSelectItem(totalItemCount - 2);
                    menuAdapter.notifyDataSetInvalidated();
                }
                // 右面菜单选中倒数第一行
                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    mRightMenu.setSelection(totalItemCount);
                    menuAdapter.setSelectItem(totalItemCount - 1);
                    menuAdapter.notifyDataSetInvalidated();
                }

            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.openDrawer(Gravity.RIGHT);
            }
        });
        mRightBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerlayout.closeDrawers();
            }
        });


        mMenuVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Request<Bitmap> request = NoHttp.createImageRequest("http://180.96.11.69:8500/api/tools/qr_code?" +
                                "url=https://law.push.aegis-info.com/newDataPage/" + qid, RequestMethod.GET);
                        final Response<Bitmap> response = NoHttp.startRequestSync(request);

                        mMenuVoice.post(new Runnable() {
                            @Override
                            public void run() {
                                showCodeWindow(mMenuVoice, response.get());
                            }
                        });
                    }
                }.start();

            }
        });
    }

    @Override
    public Context getContext() {
        return context == null ? DataDetailActivity.this : context;
    }

    /**
     * 二维码对话框
     *
     * @param parent
     * @param bt
     */
    private void showCodeWindow(View parent, Bitmap bt) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        View windowView = mLayoutInflater.inflate(R.layout.two_code_window, null);
        ImageView mImg = (ImageView) windowView.findViewById(R.id.item_two_code);

        PopupWindow mImgWindow = new PopupWindow(windowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mImg.setImageBitmap(bt);
        ColorDrawable cd = new ColorDrawable(0x000000);
        mImgWindow.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mImgWindow.setOutsideTouchable(true);
        mImgWindow.setFocusable(true);
        mImgWindow.showAtLocation((View) parent.getParent(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        mImgWindow.update();
        mImgWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
    }

    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        if (content.contains("法律法规")) {
            mLeftHome.setSelection(0);
            speakContent = "您好,您当前查看的是与该问题相关的法律法规,";
            return;
        }
        if (content.contains("诉讼请求")) {
                speakContent = "您当前查看的是与该问题相关的诉讼请求,以及败诉原因分析";
            return;
        }
        if (content.contains("证据分析")) {
            mLeftHome.setSelection(2);
            speakContent = "您当前查看的是与该问题相关的证据分析";
            return;
        }
        if (content.contains("案例")) {
            mLeftHome.setSelection(3);
            speakContent = "您当前查看的是与该问题相关的案例";
            return;
        }
        if (content.contains("司法观点")) {
            mLeftHome.setSelection(4);
            speakContent = "您当前查看的是与该问题相关的司法观点";
            return;
        }
        if (content.contains("诉讼成本")) {
            mLeftHome.setSelection(5);
            speakContent = "以下是相关的诉讼成本分析";
            return;
        }
        if (content.contains("诉讼流程")) {
            mLeftHome.setSelection(6);
            speakContent = "以下是相关的诉讼流程";
            return;
        }
        if (content.contains("二维码")) {
            speakContent = "请扫描二维码，保存咨询报告!";
            mLeftHome.setSelection(7);

            return;
        }
        startSpeak(speakContent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        overCurrentActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overCurrentActivity();
    }

    void overCurrentActivity() {
        if (handler != null) {
            handler.removeMessages(DATA);
            handler = null;
            System.gc();
        }
        AppManager.getAppManager().finishActivity(DataDetailActivity.this);
    }

}