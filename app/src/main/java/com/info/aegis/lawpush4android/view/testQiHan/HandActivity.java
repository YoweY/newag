//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.beans.handmotion.AbsoluteAngleHandMotion;
//import com.qihancloud.opensdk.function.beans.handmotion.RelativeAngleHandMotion;
//import com.qihancloud.opensdk.function.unit.HandMotionManager;
//
//public class HandActivity extends BaseActivity implements View.OnClickListener{
//    EditText editText;
//    HandMotionManager handMotionManager;
//    RelativeAngleHandMotion relativeAngleHandMotion;
//    AbsoluteAngleHandMotion absoluteAngleHandMotion;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_hand);
//        editText = (EditText) findViewById(R.id.id_edittext);
//
//        findViewById(R.id.btn1).setOnClickListener(this);
//        findViewById(R.id.btn2).setOnClickListener(this);
//        findViewById(R.id.btn3).setOnClickListener(this);
//        findViewById(R.id.btn4).setOnClickListener(this);
//        findViewById(R.id.btn5).setOnClickListener(this);
//        findViewById(R.id.btn6).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//        handMotionManager= (HandMotionManager)getUnitManager(FuncConstant.HANDMOTION_MANAGER);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn1:
//                relativeAngleHandMotion = new RelativeAngleHandMotion( RelativeAngleHandMotion.PART_LEFT,5,
//                        RelativeAngleHandMotion.ACTION_UP,
//                        Integer.parseInt(editText.getText().toString())
//                );
//                handMotionManager.doRelativeAngleMotion(relativeAngleHandMotion);
//                break;
//            case R.id.btn2:
//                relativeAngleHandMotion = new RelativeAngleHandMotion( RelativeAngleHandMotion.PART_LEFT,5,
//                        RelativeAngleHandMotion.ACTION_DOWN,
//                        Integer.parseInt(editText.getText().toString())
//                );
//                handMotionManager.doRelativeAngleMotion(relativeAngleHandMotion);
//                break;
//            case R.id.btn3:
//                relativeAngleHandMotion = new RelativeAngleHandMotion( RelativeAngleHandMotion.PART_RIGHT,5,
//                        RelativeAngleHandMotion.ACTION_UP,
//                        Integer.parseInt(editText.getText().toString())
//                );
//                handMotionManager.doRelativeAngleMotion(relativeAngleHandMotion);
//                break;
//            case R.id.btn4:
//                relativeAngleHandMotion = new RelativeAngleHandMotion( RelativeAngleHandMotion.PART_RIGHT,5,
//                        RelativeAngleHandMotion.ACTION_DOWN,
//                        Integer.parseInt(editText.getText().toString())
//                );
//                handMotionManager.doRelativeAngleMotion(relativeAngleHandMotion);
//                break;
//            case R.id.btn5:
//                absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
//                        AbsoluteAngleHandMotion.PART_LEFT,5,Integer.parseInt(editText.getText().toString())
//                );
//
//                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
//                break;
//            case R.id.btn6:
//                absoluteAngleHandMotion = new AbsoluteAngleHandMotion(
//                        AbsoluteAngleHandMotion.PART_RIGHT,5,Integer.parseInt(editText.getText().toString())
//                );
//
//                handMotionManager.doAbsoluteAngleMotion(absoluteAngleHandMotion);
//                break;
//
//        }
//    }
//}
