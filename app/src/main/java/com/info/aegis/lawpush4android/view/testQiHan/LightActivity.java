//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.content.DialogInterface;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.beans.LED;
//import com.qihancloud.opensdk.function.unit.HardWareManager;
//
//public class LightActivity extends BaseActivity implements View.OnClickListener {
//    HardWareManager hardWareManager;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_light);
//
//        findViewById(R.id.btn1).setOnClickListener(this);
//        findViewById(R.id.btn2).setOnClickListener(this);
//        findViewById(R.id.btn3).setOnClickListener(this);
//        findViewById(R.id.btn4).setOnClickListener(this);
//        findViewById(R.id.btn5).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//        hardWareManager = (HardWareManager)getUnitManager(FuncConstant.HARDWARE_MANAGER);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn1:
//                hardWareManager.switchWhiteLight(true);
//                break;
//            case R.id.btn2:
//                hardWareManager.switchWhiteLight(false);
//                break;
//            case R.id.btn3:
//                hardWareManager.setLED(new LED(LED.PART_ALL,LED. MODE_FLICKER_RANDOM,(byte)10,(byte)7));
//                break;
//            case R.id.btn4:
//                hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_FLICKER_RED,(byte)10));
//                break;
//            case R.id.btn5:
//                hardWareManager.setLED(new LED(LED.PART_ALL,LED.MODE_CLOSE));
//                break;
//        }
//    }
//}
