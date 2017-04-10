//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.utils.MyToast;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
//import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
//import com.qihancloud.opensdk.function.unit.WheelMotionManager;
//
//public class WheelActivity extends BaseActivity implements View.OnClickListener {
//    WheelMotionManager wheelMotionManager;
//    RelativeAngleWheelMotion relativeAngleWheelMotion;
//    DistanceWheelMotion distanceWheelMotion;
//    EditText editText;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wheel);
//        editText = (EditText) findViewById(R.id.edt);
//
//        findViewById(R.id.btn1).setOnClickListener(this);
//        findViewById(R.id.btn2).setOnClickListener(this);
//        findViewById(R.id.btn3).setOnClickListener(this);
//        findViewById(R.id.btn4).setOnClickListener(this);
//        findViewById(R.id.btn5).setOnClickListener(this);
//        findViewById(R.id.btn6).setOnClickListener(this);
//        findViewById(R.id.btn7).setOnClickListener(this);
//        findViewById(R.id.btn8).setOnClickListener(this);
//        findViewById(R.id.btn9).setOnClickListener(this);
//        findViewById(R.id.btn10).setOnClickListener(this);
//        findViewById(R.id.btn11).setOnClickListener(this);
//        findViewById(R.id.btn12).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//         wheelMotionManager= (WheelMotionManager)getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn1:
//                relativeAngleWheelMotion = new RelativeAngleWheelMotion(
//                        RelativeAngleWheelMotion.TURN_LEFT, 5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
//
//                break;
//            case R.id.btn2:
//                relativeAngleWheelMotion = new RelativeAngleWheelMotion(
//                        RelativeAngleWheelMotion.TURN_RIGHT, 5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
//                break;
//            case R.id.btn3:
//                relativeAngleWheelMotion = new RelativeAngleWheelMotion(
//                        RelativeAngleWheelMotion.TURN_STOP, 5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doRelativeAngleMotion(relativeAngleWheelMotion);
//                break;
//            case R.id.btn4:
//                 distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_FORWARD_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn5:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_BACK_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn6:
////                distanceWheelMotion = new DistanceWheelMotion(
////                        DistanceWheelMotion.ACTION_LEFT_RUN,  5,Integer.parseInt(editText.getText().toString())
////                );
////                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//                MyToast.show(getContext(),"功能在1.1.2中被取消了，可以先转弯，再向前走",2000);
//
//                break;
//            case R.id.btn7:
////                distanceWheelMotion = new DistanceWheelMotion(
////                        DistanceWheelMotion.ACTION_RIGHT_RUN,  5,Integer.parseInt(editText.getText().toString())
////                );
////                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//                MyToast.show(getContext(),"功能在1.1.2中被取消了，可以先转弯，再向前走",2000);
//                break;
//            case R.id.btn8:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_LEFT_FORWARD_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn9:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_RIGHT_FORWARD_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn10:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_LEFT_BACK_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn11:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_RIGHT_BACK_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//            case R.id.btn12:
//                distanceWheelMotion = new DistanceWheelMotion(
//                        DistanceWheelMotion.ACTION_STOP_RUN,  5,Integer.parseInt(editText.getText().toString())
//                );
//                wheelMotionManager.doDistanceMotion(distanceWheelMotion);
//
//                break;
//        }
//    }
//}
