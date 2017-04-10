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
//import com.qihancloud.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
//import com.qihancloud.opensdk.function.unit.HeadMotionManager;
//
//public class HeadActivity extends BaseActivity implements View.OnClickListener{
//    HeadMotionManager headMotionManager;
//    RelativeAngleHeadMotion relativeAngleHeadMotion;
//    EditText editText;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_head);
//        editText = (EditText) findViewById(R.id.id_edittext);
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
//            case R.id.btn1:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_UP,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//
//                break;
//            case R.id.btn2:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_DOWN,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn3:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_LEFT,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn4:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_RIGHT,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn5:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_LEFTUP,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn6:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_RIGHTUP,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn7:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_LEFTDOWN,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn8:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_RIGHTDOWN,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//            case R.id.btn9:
//                relativeAngleHeadMotion = new RelativeAngleHeadMotion(
//                        RelativeAngleHeadMotion.ACTION_STOP,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doRelativeAngleMotion(relativeAngleHeadMotion);
//                break;
//        }
//    }
//}
