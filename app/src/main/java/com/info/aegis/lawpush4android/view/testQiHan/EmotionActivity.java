//package com.info.aegis.lawpush4android.view.testQiHan;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.utils.MyLog;
//import com.info.aegis.lawpush4android.utils.MyToast;
//import com.info.aegis.lawpush4android.view.iml.BaseActivity;
//import com.qihancloud.opensdk.beans.FuncConstant;
//import com.qihancloud.opensdk.function.beans.EmotionsType;
//import com.qihancloud.opensdk.function.unit.SystemManager;
//
//public class EmotionActivity extends BaseActivity implements View.OnClickListener {
//    SystemManager systemManager;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_emotion);
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
//        findViewById(R.id.btn13).setOnClickListener(this);
//        findViewById(R.id.btn14).setOnClickListener(this);
//        findViewById(R.id.btn15).setOnClickListener(this);
//        findViewById(R.id.btn16).setOnClickListener(this);
//        findViewById(R.id.btn17).setOnClickListener(this);
//        findViewById(R.id.btn18).setOnClickListener(this);
//        findViewById(R.id.btn19).setOnClickListener(this);
//        findViewById(R.id.btn20).setOnClickListener(this);
//        findViewById(R.id.btn21).setOnClickListener(this);
//        findViewById(R.id.btn22).setOnClickListener(this);
//    }
//
//    @Override
//    protected void onMainServiceConnected() {
//         systemManager= (SystemManager)getUnitManager(FuncConstant. SYSTEM_MANAGER);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn1:
//                systemManager.showEmotion( EmotionsType.ARROGANCE  );
//                break;
//            case R.id.btn2:
//                systemManager.showEmotion( EmotionsType. SURPRISE  );
//                break;
//            case R.id.btn3:
//                systemManager.showEmotion( EmotionsType. WHISTLE  );
//                break;
//            case R.id.btn4:
//                systemManager.showEmotion( EmotionsType. LAUGHTER  );
//                break;
//            case R.id.btn5:
//                systemManager.showEmotion( EmotionsType. GOODBYE  );
//                break;
//            case R.id.btn6:
//                systemManager.showEmotion( EmotionsType. SHY  );
//                break;
//            case R.id.btn7:
//                systemManager.showEmotion( EmotionsType. SWEAT  );
//                break;
//            case R.id.btn8:
//                systemManager.showEmotion( EmotionsType. SNICKER  );
//                break;
//            case R.id.btn9:
//                systemManager.showEmotion( EmotionsType. PICKNOSE  );
//                break;
//            case R.id.btn10:
//                systemManager.showEmotion( EmotionsType. CRY  );
//                break;
//            case R.id.btn11:
//                systemManager.showEmotion( EmotionsType. ABUSE  );
//                break;
//            case R.id.btn12:
//                systemManager.showEmotion( EmotionsType. ANGRY  );
//                break;
//            case R.id.btn13:
//                systemManager.showEmotion( EmotionsType. KISS  );
//                break;
//            case R.id.btn14:
//                systemManager.showEmotion( EmotionsType. SLEEP  );
//                break;
//            case R.id.btn15:
//                systemManager.showEmotion( EmotionsType. SMILE  );
//                break;
//            case R.id.btn16:
//                systemManager.showEmotion( EmotionsType. GRIEVANCE  );
//                break;
//            case R.id.btn17:
//                systemManager.showEmotion( EmotionsType. QUESTION  );
//                break;
//            case R.id.btn18:
//                systemManager.showEmotion( EmotionsType. FAINT  );
//                break;
//            case R.id.btn19:
//                systemManager.showEmotion( EmotionsType. PRISE);
//                break;
//            case R.id.btn20:
//                systemManager.showEmotion( EmotionsType. NORMAL  );
//                break;
//            case R.id.btn21:
//                MyToast.show(getContext(),systemManager.getDeviceId().getResult()+"",2000);
//                MyLog.e("lawPush",systemManager.getDeviceId().getResult()+"");
//                break;
//            case R.id.btn22:
//                systemManager.doHomeAction();
//                break;
//
//        }
//    }
//}
