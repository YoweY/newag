//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.utils.MyToast;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.unit.HardWareManager;
//import com.qihancloud.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;
//
//public class VoiceFromActivity extends BaseActivity {
//    HardWareManager hardWareManager;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_voice_from);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//        hardWareManager = (HardWareManager)getUnitManager(FuncConstant.HARDWARE_MANAGER);
//        hardWareManager.setOnHareWareListener(new VoiceLocateListener() {
//            @Override
//            public void voiceLocateResult(int angle) {
//                //TODO 获取到声源定位角度，进行处理
//                MyToast.show(VoiceFromActivity.this,"angle:"+angle,2000);
//            }
//        });
//
//    }
//}
