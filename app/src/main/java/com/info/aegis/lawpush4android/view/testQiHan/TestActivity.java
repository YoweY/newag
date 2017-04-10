//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import android.view.View;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.utils.Constant;
//
//
//public class TestActivity extends AppCompatActivity implements View.OnClickListener {
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        findViewById(R.id.button).setOnClickListener(this);
//        findViewById(R.id.button2).setOnClickListener(this);
//        findViewById(R.id.button3).setOnClickListener(this);
//        findViewById(R.id.button4).setOnClickListener(this);
//        findViewById(R.id.button5).setOnClickListener(this);
//        findViewById(R.id.button6).setOnClickListener(this);
//        findViewById(R.id.button7).setOnClickListener(this);
//        findViewById(R.id.button8).setOnClickListener(this);
//        findViewById(R.id.button9).setOnClickListener(this);
//        findViewById(R.id.button10).setOnClickListener(this);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.button:
//                startActivity(new Intent(TestActivity.this,VoiceFromActivity.class));
//                break;
//            case R.id.button2:
//                startActivity(new Intent(TestActivity.this,LightActivity.class));
//                break;
//            case R.id.button3:
//                startActivity(new Intent(TestActivity.this,TouchActivity.class));
//                break;
//            case R.id.button4:
//                startActivity(new Intent(TestActivity.this,HeadActivity.class));
//                break;
//            case R.id.button5:
//                startActivity(new Intent(TestActivity.this,Head2Activity.class));
//                break;
//            case R.id.button6:
//                startActivity(new Intent(TestActivity.this,HandActivity.class));
//                break;
//            case R.id.button7:
//                startActivity(new Intent(TestActivity.this,WheelActivity.class));
//                break;
//            case R.id.button8:
//                startActivity(new Intent(TestActivity.this,EmotionActivity.class));
//                break;
//            case R.id.button9:
////                Constant.createLoadingDialog(TestActivity.this,"测试Dialog",1);
//                break;
//            case R.id.button10:
//                startActivity(new Intent(TestActivity.this,ZuHeActivity.class));
//                break;
//        }
//    }
//}
