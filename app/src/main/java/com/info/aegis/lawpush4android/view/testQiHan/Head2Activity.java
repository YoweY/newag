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
//import com.qihancloud.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion;
//import com.qihancloud.opensdk.function.unit.HeadMotionManager;
//
//public class Head2Activity extends BaseActivity implements View.OnClickListener {
//    EditText editText;
//    AbsoluteAngleHeadMotion absoluteAngleHeadMotion;
//    HeadMotionManager headMotionManager;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_head2);
//        editText = (EditText) findViewById(R.id.id_edittext);
//
//        findViewById(R.id.btn1).setOnClickListener(this);
//        findViewById(R.id.btn2).setOnClickListener(this);
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
//                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
//                        AbsoluteAngleHeadMotion.ACTION_VERTICAL,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
//                break;
//            case R.id.btn2:
//                absoluteAngleHeadMotion = new AbsoluteAngleHeadMotion(
//                        AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,Integer.parseInt(editText.getText().toString())
//                );
//                headMotionManager.doAbsoluteAngleMotion(absoluteAngleHeadMotion);
//                break;
//        }
//    }
//}
