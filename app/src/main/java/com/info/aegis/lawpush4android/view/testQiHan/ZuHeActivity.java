//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.unit.HeadMotionManager;
//
//public class ZuHeActivity extends BaseActivity implements View.OnClickListener{
//
//    HeadMotionManager headMotionManager;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zu_he);
//
//        findViewById(R.id.id_btn1).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//        headMotionManager= (HeadMotionManager)getUnitManager(FuncConstant.HEADMOTION_MANAGER);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.id_btn1:
//                //抬头 + 睁大眼睛
//                //采用头部绝对角度
//
//                break;
//        }
//    }
//}
