//package com.info.aegis.lawpush4android.view.iml;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.info.aegis.lawpush4android.LawPushApp;
//import com.info.aegis.lawpush4android.R;
//import com.info.aegis.lawpush4android.model.bean.ScenesBean;
//import com.info.aegis.lawpush4android.presenter.ShowGuideDialogPresenter;
//import com.info.aegis.lawpush4android.view.ShowGuideDialogActivityService;
//import com.info.aegis.lawpush4android.view.handler.ShowGuideDialogActivityHandler;
//
///**
// * Created by SFS on 2017/3/21.
// * Description :
// */
//
//public class ShowGuideDialogActivity extends BaseActivity implements View.OnClickListener, ShowGuideDialogActivityService {
//
//    private ShowGuideDialogPresenter showGuideDialogPresenter;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_version_court);
//        showGuideDialogPresenter = new ShowGuideDialogPresenter(this);
//        handler = new ShowGuideDialogActivityHandler(this);
//        initView();
//
//    }
//
//    private void initView() {
//        //图片
//        ImageView mImgPosition1 = (ImageView) findViewById(R.id.imgPosition1);
//        ImageView mImgPosition2 = (ImageView) findViewById(R.id.imgPosition2);
//        ImageView mImgPosition3 = (ImageView) findViewById(R.id.imgPosition3);
//        ImageView mImgPosition4 = (ImageView) findViewById(R.id.imgPosition4);
//        ImageView mImgPosition5 = (ImageView) findViewById(R.id.imgPosition5);
//        ImageView mImgPosition6 = (ImageView) findViewById(R.id.imgPosition6);
//        ImageView mImgPosition7 = (ImageView) findViewById(R.id.imgPosition7);
//        ImageView mImgPosition8 = (ImageView) findViewById(R.id.imgPosition8);
//        //  文字
//        TextView mIdTextPosition1 = (TextView) findViewById(R.id.id_textPosition1);
//        TextView mIdTextPosition2 = (TextView) findViewById(R.id.id_textPosition2);
//        TextView mIdTextPosition3 = (TextView) findViewById(R.id.id_textPosition3);
//        TextView mIdTextPosition4 = (TextView) findViewById(R.id.id_textPosition4);
//            //  点击监听
//        mImgPosition1.setOnClickListener(this);
//        mImgPosition2.setOnClickListener(this);
//        mImgPosition3.setOnClickListener(this);
//        mImgPosition4.setOnClickListener(this);
//        mImgPosition5.setOnClickListener(this);
//        mImgPosition6.setOnClickListener(this);
//        mImgPosition7.setOnClickListener(this);
//        mImgPosition8.setOnClickListener(this);
//        mIdTextPosition1.setOnClickListener(this);
//        mIdTextPosition2.setOnClickListener(this);
//        mIdTextPosition3.setOnClickListener(this);
//        mIdTextPosition4.setOnClickListener(this);
//        //  文字设置数据
//        mIdTextPosition1.setText(LawPushApp.getScenesBeanListFirstSome().get(0).getName());
//        mIdTextPosition2.setText(LawPushApp.getScenesBeanListFirstSome().get(1).getName());
//        mIdTextPosition3.setText(LawPushApp.getScenesBeanListFirstSome().get(2).getName());
//        mIdTextPosition4.setText(LawPushApp.getScenesBeanListFirstSome().get(3).getName());
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.imgPosition1:
//            case R.id.id_textPosition1:
//                compareScene(LawPushApp.getScenesBeanListFirstSome().get(0));
//                break;
//            case R.id.imgPosition2:
//            case R.id.id_textPosition2:
//                compareScene(LawPushApp.getScenesBeanListFirstSome().get(1));
//
//                break;
//            case R.id.imgPosition3:
//            case R.id.id_textPosition3:
//                compareScene(LawPushApp.getScenesBeanListFirstSome().get(2));
//                break;
//            case R.id.imgPosition4:
//            case R.id.id_textPosition4:
//                compareScene(LawPushApp.getScenesBeanListFirstSome().get(3));
//                break;
//            case R.id.imgPosition5:
//                JumpActivity(3, "", "");
//                break;
//            case R.id.imgPosition6:
//                JumpActivity(4, "", "");
//                break;
//            case R.id.imgPosition7:
//                JumpActivity(5, "", "");
//                break;
//            case R.id.imgPosition8:
//                startFrontVoiceRecognizeListening();
//                break;
//
//        }
//    }
//
//    void compareScene(ScenesBean mSceneBean) {
//        ScenesBean scenesBean = null;
//        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
//            if (mSceneBean.getParent().equals(LawPushApp.getScenesBeanList().get(i).getId())) {
//                scenesBean = LawPushApp.getScenesBeanList().get(i);
//                break;
//            }
//        }
//        if (scenesBean == null) {
//            JumpActivity(3, mSceneBean.getName(), "");
//            return;
//        }
//
//        switch (scenesBean.getName()) {
//            case "程序引导":
//                JumpActivity(1, mSceneBean.getName(), mSceneBean.getId());
//                break;
//            case "法律法规":
//                JumpActivity(5, mSceneBean.getName(), mSceneBean.getId());
//                break;
//            case "法律问题":
//                JumpActivity(4, mSceneBean.getName(), mSceneBean.getId());
//                break;
//            case "陪你聊天":
//                JumpActivity(3, mSceneBean.getName(), mSceneBean.getId());
//                break;
//            default:
//                JumpActivity(3, mSceneBean.getName(), mSceneBean.getId());
//                break;
//        }
//    }
//
//    @Override
//    public void sendToServer(String content) {
//        //先调取select接口匹配
//        String choices = "";
//        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
//            choices = choices + LawPushApp.getScenesBeanList().get(i).getName() + ",";
//        }
//        if (choices.endsWith(","))
//            choices = choices.substring(0, choices.length() - 1);
//        showGuideDialogPresenter.getSelect(choices, content);
//
//
//    }
//
//    /**
//     * 判断要跳转的类型
//     *
//     * @param content  内容
//     */
//    void judgeActivityChatType(String content) {
//        if (content.matches(".{0,6}程序引导.{0,3}") || content.matches(".{0,5}我.{1}[(立案)|(起诉)].{0,3}")) {
//            //程序引导
//            JumpActivity(1, "", "");
//            return;
//        }
//        if (content.matches(".{0,5}法.{1}查询.{0,3}") || content.matches(".{0,5}查.{1,3}法.{1,3}") || content.matches(".{0,5}法.{1,3}法.{1,3}")) {
//            //法规法规
//            JumpActivity(5, "", "");
//            return;
//        }
//        if (content.contains("法律咨询") || content.contains("法律问题") || content.contains("法律援助") || content.matches(".{0,6}法律咨询") || content.contains("法律问答")
//                || content.matches(".{0,6}咨询.{0,2}法律.{0,3}")) {
//            JumpActivity(4, "", "");
//            return;
//        }
//        if (content.matches(".{0,7}聊.{1,5}")) {
//            JumpActivity(3, content, "");
//        }
//        JumpActivity(3, content, "");
//    }
//
//    /**
//     * @param type            跳转聊天页的类型
//     * @param questionContent 跳转过去后，如果需要进行hot接口，则传值，并将sceneId加上
//     */
//    private void JumpActivity(int type, String questionContent, String sceneId) {
//        Intent intent;
//        intent = new Intent(ShowGuideDialogActivity.this, ChatActivity.class);
//        intent.putExtra("chatType", type);
//        intent.putExtra("questionContent", questionContent);
//        intent.putExtra("sceneId", sceneId);
//        startActivity(intent);
//        finish();
//    }
//
//    /**
//     * 匹配select接口中返回的数据
//     *
//     * @param content  选择的内容
//     */
//    public void checkItem(String content) {
//        ScenesBean scenesBean = new ScenesBean();
//        for (int i = 0; i < LawPushApp.getScenesBeanList().size(); i++) {
//            if (LawPushApp.getScenesBeanList().get(i).getName().equals(content)) {
//                scenesBean = LawPushApp.getScenesBeanList().get(i);
//                break;
//            }
//        }
//        JumpActivity(1, scenesBean.getName(), scenesBean.getId());
//    }
//
//    /**
//     * select接口无数据返回，重新进入下一个activity
//     */
//    public void rePreCause(String content) {
//        judgeActivityChatType(content);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//    }
//
//    public void initFailed() {
//    }
//
//    public void initSuccess() {
//    }
//}
