//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.utils.MyToast;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.unit.HardWareManager;
//import com.qihancloud.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
//
//public class TouchActivity extends BaseActivity implements View.OnClickListener{
//
//    HardWareManager hardWareManager;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_touch);
//
////        findViewById(R.id.btn1).setOnClickListener(this);
////        findViewById(R.id.btn2).setOnClickListener(this);
////        findViewById(R.id.btn3).setOnClickListener(this);
////        findViewById(R.id.btn4).setOnClickListener(this);
////        findViewById(R.id.btn5).setOnClickListener(this);
////        findViewById(R.id.btn6).setOnClickListener(this);
////        findViewById(R.id.btn7).setOnClickListener(this);
////        findViewById(R.id.btn8).setOnClickListener(this);
////        findViewById(R.id.btn9).setOnClickListener(this);
////        findViewById(R.id.btn10).setOnClickListener(this);
////        findViewById(R.id.btn11).setOnClickListener(this);
////        findViewById(R.id.btn12).setOnClickListener(this);
////        findViewById(R.id.btn13).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//        hardWareManager = (HardWareManager)getUnitManager(FuncConstant.HARDWARE_MANAGER);
//
//        hardWareManager.setOnHareWareListener(new TouchSensorListener() {
//            @Override
//            public void onTouch(int part) {
//                switch (part){
//                    case 1:
//                        MyToast.show(TouchActivity.this,"下巴右侧传感器",2000);
//                        break;
//                    case 2:
//                        MyToast.show(TouchActivity.this,"下巴左侧传感器",2000);
//                        break;
//                    case 3:
//                        MyToast.show(TouchActivity.this,"胸前左侧传感器",2000);
//                        break;
//                    case 4:
//                        MyToast.show(TouchActivity.this,"胸前右侧传感器",2000);
//                        break;
//                    case 5:
//                        MyToast.show(TouchActivity.this,"后脑勺左侧传感器",2000);
//                        break;
//                    case 6:
//                        MyToast.show(TouchActivity.this,"后脑勺右侧传感器",2000);
//                        break;
//                    case 7:
//                        MyToast.show(TouchActivity.this,"背部左侧传感器",2000);
//                        break;
//                    case 8:
//                        MyToast.show(TouchActivity.this,"背部右侧传感器",2000);
//                        break;
//                    case 9:
//                        MyToast.show(TouchActivity.this,"左翅传感器",2000);
//                        break;
//                    case 10:
//                        MyToast.show(TouchActivity.this,"右翅传感器",2000);
//                        break;
//                    case 11:
//                        MyToast.show(TouchActivity.this,"头顶中间处传感器",2000);
//                        break;
//                    case 12:
//                        MyToast.show(TouchActivity.this,"头顶左前处传感器",2000);
//                        break;
//                    case 13:
//                        MyToast.show(TouchActivity.this,"头顶右前传感器",2000);
//                        break;
//
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//}
