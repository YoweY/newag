package com.info.aegis.lawpush4android.view.iml;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.presenter.GuidePresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.MyToast;
import com.info.aegis.lawpush4android.utils.OnDoubleClickListener;
import com.info.aegis.lawpush4android.view.GuideActivityService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SFS on 2017/2/9.
 * Description :
 */

public class GuideActivity extends BaseActivity implements View.OnClickListener, GuideActivityService {


    private RelativeLayout mText1, mText2, mText3, mText4;

    private SharedPreferences robbotSP;
    private SharedPreferences.Editor robotEditor;
    private RelativeLayout mbg;
    private View updata;
    private TextView mName;
    private TextView mContent;
    private Intent service;

    private LocationClient mLocationClient = null;
    private GuidePresenter guidePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        AppManager.getAppManager().addActivity(GuideActivity.this);

        robbotSP = getSharedPreferences("robbotSP", MODE_PRIVATE);
        robotEditor = robbotSP.edit();
        initView();
//        mLocationClient = new LocationClient(getApplicationContext());
//        BDLocationListener myListener = new MyLocationListener(GuideActivity.this,mLocationClient);
//        //声明LocationClient类
//        mLocationClient.registerLocationListener( myListener );
//        //注册监听函数
//        initLocation();
//        mLocationClient.start();
        MyLog.e("lawPush", " guide-----onCreate");

        findViewById(R.id.id_setting).setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                final View parentView = LayoutInflater.from(GuideActivity.this).inflate(R.layout.layout_dialog_setting, null);
                new android.support.v7.app.AlertDialog.Builder(GuideActivity.this).setTitle("确认？").setView(parentView).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) parentView.findViewById(R.id.id_edittext);
                        if (editText.getText().toString().equals("aegis2016"))
                            startActivity(new Intent(GuideActivity.this, SettingActivity.class));
                        else
                            dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackground();
        LawPushApp.setCurrntActivity("GuideActivity");
        MyLog.e("lawPush", " guide-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        AppManager.getAppManager().finishActivity(GuideActivity.this);
        finish();
        MyLog.e("lawPush", " guide-----onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.e("lawPush", " guide-----onStop");
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        onDestroy();
//    }

    private void initView() {
        mText1 = (RelativeLayout) findViewById(R.id.guide_rl1);
        mText2 = (RelativeLayout) findViewById(R.id.guide_rl2);
        mText3 = (RelativeLayout) findViewById(R.id.guide_rl3);
        mText4 = (RelativeLayout) findViewById(R.id.guide_rl4);
        mbg = (RelativeLayout) findViewById(R.id.relativeLayout);
        mName = (TextView) findViewById(R.id.text1);
        mContent = (TextView) findViewById(R.id.text2);

        mText1.setOnClickListener(this);
        mText2.setOnClickListener(this);
        mText3.setOnClickListener(this);
        mText4.setOnClickListener(this);

        mbg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopSpeak();
                return false;
            }
        });

        findViewById(R.id.open_Map).setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                MyLog.e("shen", "打不开地图");
//       startActivity(new Intent(GuideActivity.this, RoutePlanDemo.class));
            }
        }));


        String name = robbotSP.getString("name", "初始化失败");
        String[] valueArray = name.split(",");
        mName.setText(valueArray[0]);
        mContent.setText(valueArray[1]);
    }

    @Override
    public void onClick(View v) {
        // 环境类型 1 程序引导 5 法律法规 4 法律问答 5 随便聊聊
        int type = 3;
        switch (v.getId()) {
            case R.id.guide_rl1:
                setBackground1();
                type = 1;
                //    mLocationClient.start();
                break;
            case R.id.guide_rl2:
                setBackground2();
                type = 5;
//                MyToast.show(getContext(),LawPushApp.getCurrentLocation(),2000);
                break;
            case R.id.guide_rl3:
                setBackground3();
                type = 4;

                break;
            case R.id.guide_rl4:
                setBackground4();
                type = 3;
                break;

        }
        JumpActivity(type);

    }


    /**
     * 随便聊聊的背景设置
     */
    private void setBackground4() {
        mText4.setBackgroundResource(R.mipmap.circle_choose);
        mText3.setBackgroundResource(R.mipmap.circle_normal);
        mText2.setBackgroundResource(R.mipmap.circle_normal);
        mText1.setBackgroundResource(R.mipmap.circle_normal);
    }

    /**
     * 法律问答的背景设置
     */
    private void setBackground3() {
        mText4.setBackgroundResource(R.mipmap.circle_normal);
        mText3.setBackgroundResource(R.mipmap.circle_choose);
        mText2.setBackgroundResource(R.mipmap.circle_normal);
        mText1.setBackgroundResource(R.mipmap.circle_normal);
    }

    /**
     * 法律法规的背景设置
     */
    private void setBackground2() {
        mText4.setBackgroundResource(R.mipmap.circle_normal);
        mText3.setBackgroundResource(R.mipmap.circle_normal);
        mText2.setBackgroundResource(R.mipmap.circle_choose);
        mText1.setBackgroundResource(R.mipmap.circle_normal);
    }

    /**
     * 程序引导的背景设置
     */
    private void setBackground1() {
        mText4.setBackgroundResource(R.mipmap.circle_normal);
        mText3.setBackgroundResource(R.mipmap.circle_normal);
        mText2.setBackgroundResource(R.mipmap.circle_normal);
        mText1.setBackgroundResource(R.mipmap.circle_choose);
    }

    /**
     * 默认的背景设置
     */
    private void setBackground() {
        mText4.setBackgroundResource(R.mipmap.circle_normal);
        mText3.setBackgroundResource(R.mipmap.circle_normal);
        mText2.setBackgroundResource(R.mipmap.circle_normal);
        mText1.setBackgroundResource(R.mipmap.circle_normal);
    }

    private void JumpActivity(int type) {
        SystemClock.sleep(200);
        Intent intent;
        if (type == 1) {
            intent = new Intent(GuideActivity.this, SecondGuideActivity.class);
            intent.putExtra("item", type);
            List<ScenesBean> dataList = new ArrayList<>();
//            if (LawPushApp.getScenesBeanList().size() != 0) {
//                for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
//                    ScenesBean scenesBean = LawPushApp.getScenesBeanList().get(i);
//                    if (scenesBean.getGrade() == 1 && scenesBean.getName().equals("程序引导")) {
//                        if (scenesBean.getSonStringList() != null) {
//                            List<String> stringList = scenesBean.getSonStringList();
//                            for (int j = 0; j < LawPushApp.getScenesBeanList().size(); j++) {
//                                for (int m = 0; m < stringList.size(); m++) {
//                                    if (stringList.get(m).equals(LawPushApp.getScenesBeanList().get(j).getId())) {
//                                        MyLog.e("lawPush", "         stringList.get(m)---" + stringList.get(m));
//                                        dataList.add(LawPushApp.getScenesBeanList().get(j));
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
            intent.putExtra("Data", dataList.toString());
            startActivity(intent);
            return;
        }
//        if (type == 4) {
//            intent = new Intent(GuideActivity.this, SubGuideActivity.class);
//            intent.putExtra("item", type);
//            List<ScenesBean> dataList = new ArrayList<>();
//            if (LawPushApp.getScenesBeanList().size() != 0) {
//                for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
//                    ScenesBean scenesBean = LawPushApp.getScenesBeanList().get(i);
//                    if (scenesBean.getGrade() == 1 && scenesBean.getName().equals("法律问题")) {
//                        if (scenesBean.getSonStringList() != null) {
//                            List<String> stringList = scenesBean.getSonStringList();
//                            for (int j = 0; j < LawPushApp.getScenesBeanList().size(); j++) {
//                                for (int m = 0; m < stringList.size(); m++) {
//                                    if (stringList.get(m).equals(LawPushApp.getScenesBeanList().get(j).getId())) {
//                                        MyLog.e("lawPush", "         stringList.get(m)---" + stringList.get(m));
//                                        dataList.add(LawPushApp.getScenesBeanList().get(j));
//                                    }
//                                }
//                            }
//                        }
//                        break;
//                    }
//                }
//            }
//            intent.putExtra("Data", dataList.toString());
//            startActivity(intent);
//            return;
//        }

        intent = new Intent(GuideActivity.this, ChatActivity.class);
        intent.putExtra("item", type);
        startActivity(intent);
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void sendToServer(String content) {
        if (content.matches(".{0,6}程序引导.{0,3}") || content.matches(".{0,5}我.{1}[(立案)|(起诉)].{0,3}")) {
            //程序引导
            JumpActivity(1);
            return;
        }
        if (content.matches(".{0,5}法.{1}查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}")) {
            //法规查询
            JumpActivity(5);
            return;
        }
        if (content.contains("法律咨询") || content.contains("法律问题") || content.contains("法律援助") || content.matches(".{0,6}法律咨询") || content.contains("法律问答")
                || content.matches(".{0,6}咨询.{0,2}法律.{0,3}")
                ) {
            JumpActivity(4);
            return;
        }
        if (content.contains("聊聊") || content.matches(".{0,7}聊点.{1,5}")) {
            //随便聊聊
            JumpActivity(3);
            return;
        }
        JumpActivity(3);// // TODO: 2017/2/14 为啥传9？？
    }

    /**
     * 配置法院
     *
     * @param view 显示的控件
     */
    public void showSetCourtNameDialog(final TextView view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("修改法院所在的省份地区!");
        dialogBuilder.setMessage("请输入详细的省份");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().length() != 0 && !edt.getText().toString().equals("")) {
                    //编辑框不为空
                    robotEditor.putString("name1", edt.getText().toString());
                    robotEditor.commit();
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(edt.getText().toString());
                        }
                    });

                } else {
                    MyToast.showShort(getContext(), "修改失败,请重新输入!");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MyToast.show(GuideActivity.this, "取消设置", 2000);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showSetCourtContentDialog(final TextView view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("修改法院名称");
        dialogBuilder.setMessage("请输入需要修改的法院名称");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().length() != 0 && !edt.getText().toString().equals("")) {
                    //编辑框不为空
                    robotEditor.putString("name1", edt.getText().toString());
                    robotEditor.commit();
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setText(edt.getText().toString());
                        }
                    });

                } else {
                    MyToast.showShort(getContext(), "修改失败,请重新输入!");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MyToast.show(GuideActivity.this, "取消设置", 2000);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void updateVoice() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("修改音色识别度");
        dialogBuilder.setMessage("请输入音色的值,最小为-50,数值越小,识别度越高,当前门限值为 : " + getSharedPreferences("robbotSP", MODE_PRIVATE).getInt("voice_limit", -50));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().length() != 0 && !edt.getText().toString().equals("")) {
                    //编辑框不为空
                    Log.d("shen", "Integer):" + Integer.valueOf(edt.getText().toString()));
                    robotEditor.putInt("voice_limit", Integer.valueOf(edt.getText().toString()));
                    robotEditor.commit();
                    finish();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MyToast.show(GuideActivity.this, "取消设置", 2000);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

//        高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
//
//        低功耗定位模式：这种定位模式下，不会使用GPS进行定位，只会使用网络定位（WiFi定位和基站定位）；
//
//        仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }
}
