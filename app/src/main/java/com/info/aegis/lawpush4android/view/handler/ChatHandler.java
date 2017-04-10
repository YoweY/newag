package com.info.aegis.lawpush4android.view.handler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.annotation.BoolRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.entity.law.CaseInfo;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.LawDetailActivity;

import org.json.JSONObject;

/**
 * Created by mcs on 2016/12/29.
 */
public class ChatHandler extends BaseHandler {


    public ChatHandler(ChatActivity activity) {
        super(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        final ChatActivity chatActivity = (ChatActivity) reference.get();
        switch (message.what) {
            case Constant.VOICE_READ:
                chatActivity.startSpeak((String) message.obj);
                break;
            case 101:
                chatActivity.rePreCause((String)message.obj);
                break;
            case 102:
                chatActivity.checkItem((String)message.obj);
                break;
            case Constant.CHAT_SAVE_SCENE:
                chatActivity.chatSaveScene();
                break;
            case Constant.NEW_MESSAGE:
                chatActivity.receiveMsg((Msg) message.obj);
                break;
            case Constant.FATHER:
                //
                chatActivity.modifyCheckBox((String[]) message.obj);
                chatActivity.msgListView.setSelection(chatActivity.msgList.size());
                break;
            case Constant.SON:
                //
                chatActivity.modifyCheckBox((String[]) message.obj);
                chatActivity.msgListView.setSelection(chatActivity.msgList.size());
                //屏幕可见区域

                for (int i = chatActivity.msgListView.getLastVisiblePosition() - chatActivity.msgListView.getFirstVisiblePosition(); i >= 0; i--) {
                    if (chatActivity.msgListView.getItemAtPosition(i) != null) {
                        Button nextButton = (Button) chatActivity.msgListView.getChildAt(i).findViewById(R.id.nextButton);
                        if (nextButton != null) {
                            nextButton.callOnClick();
                            break;
                        }
                    }
                }

                break;
            case Constant.SAY_MORE:
                //查看更多标签页
//                if (context.msgListView.getChildAt(2) != null) {
//                    RelativeLayout more = (RelativeLayout) context.msgListView.getChildAt(2).findViewById(R.id.moreRelay);
//                    more.performClick();
//                }
                for (int i = chatActivity.msgListView.getLastVisiblePosition() - chatActivity.msgListView.getFirstVisiblePosition(); i >= 0; i--) {
                    if (chatActivity.msgListView.getItemAtPosition(i) != null) {
                        RelativeLayout more = (RelativeLayout) chatActivity.msgListView.getChildAt(i).findViewById(R.id.moreRelay);
                        if (more != null) {
                            more.performClick();
                            break;
                        }
                    }
                }
                break;
            case Constant.MORE:
                chatActivity.more((Msg) message.obj);
                break;
            case Constant.RESULT:
                //用户说出查看结果，调用resultButton的点击
                chatActivity.modifyCheckBox((String[]) message.obj);
                chatActivity.msgListView.setSelection(chatActivity.msgList.size());
//                for (int i = context.msgListView.getLastVisiblePosition() - context.msgListView.getFirstVisiblePosition(); i >= 0; i--) {
//                    if (context.msgListView.getItemAtPosition(i) != null) {
//                        Button resultButton = (Button) context.msgListView.getChildAt(i).findViewById(R.id.resultButton);
//                        if (resultButton != null) {
//                            resultButton.callOnClick();
//                            break;
//                        }
//                    }
//                }
                break;
            case Constant.NEW:
                //开始新的会话
                chatActivity.newSessionBtn.callOnClick();
                break;
            case Constant.LAW:
                //进入法条
                for (int i = chatActivity.msgList.size() - 1; i >= 0; i--) {
                    Msg msg = chatActivity.msgList.get(i);
                    if (msg.getType() == Msg.TYPE_RESULT) {
                        if (msg.getLaw() != null) {
                            Law law = msg.getLaw();
                            skipToLawDetail(law.getId(), law.getTitle(), Constant.LAW_INFO);
                        }
                        break;
                    }
                }
                break;
            case Constant.CASE:
                //进入案例
                for (int i = chatActivity.msgList.size() - 1; i >= 0; i--) {
                    Msg msg = chatActivity.msgList.get(i);
                    if (msg.getType() == Msg.TYPE_RESULT) {
                        if (msg.getCaseInfo() != null) {
                            CaseInfo caseInfo = msg.getCaseInfo();
                            skipToLawDetail(caseInfo.getId(), caseInfo.getTitle(), Constant.LAW_CASE);
                        }
                        break;
                    }
                }
                break;
            case Constant.GUIDE:
                //进入指导案例
                for (int i = chatActivity.msgList.size() - 1; i >= 0; i--) {
                    Msg msg = chatActivity.msgList.get(i);
                    if (msg.getType() == Msg.TYPE_RESULT) {
                        if (msg.getGuidanceCase() != null) {
                            GuidanceCase guidanceCase = msg.getGuidanceCase();
                            skipToLawDetail(guidanceCase.getId(), guidanceCase.getTitle(), Constant.LAW_GUIDE_CASE);
                        }
                        break;
                    }
                }
                break;
            case Constant.POINT:
                //进入司法观点
                for (int i = chatActivity.msgList.size() - 1; i >= 0; i--) {
                    Msg msg = chatActivity.msgList.get(i);
                    if (msg.getType() == Msg.TYPE_RESULT) {
                        if (msg.getPoint() != null) {
                            JudicialPoint judicialPoint = msg.getPoint();
                            skipToLawDetail(judicialPoint.getId(), judicialPoint.getTitle(), Constant.LAW_POINTS);
                        }
                        break;
                    }
                }
                break;
            case Constant.RECEICE_SERVER_MESSAGE:
                //非标问题，弹出加载框进行处理
                if (message.arg1 == 1){
                    //开始计时救场
                    Constant.startCountTime(chatActivity,chatActivity.adapter,chatActivity.msgList);
                    //开启队列请求监听
                    chatActivity.subscribe();
                }else if (message.arg1 == 2){
                    //隐藏对话框
//                    Constant.hideProgressDialog();
                }


                break;
            case 1111:
                chatActivity.msgList.get((chatActivity.msgList.size() -1)).getChoiceMsgs()[0].setChecked(true);
                chatActivity.msgList.get((chatActivity.msgList.size() -1)).setFlag(1);
                chatActivity.adapter.notifyDataSetChanged();
                break;
            // TODO: 2017/2/10   在 聊天页面进行导航背景选择
            case Constant.CHAT_TYPE:
                chatActivity.setTopSelectBg((int)message.obj);
                break;
            case 2222:
                //救场语处理
                chatActivity.saveSOS();
                break;
            case Constant.CHOICE_SEND:
                chatActivity.showOnChatUI((String) message.obj);
                break;
        }
    }

    /**
     * 跳转到详情页2
     */
    private void skipToLawDetail(String id, String name, int type) {
        Activity activity = reference.get();
        Intent intent = new Intent(activity, LawDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        activity.startActivity(intent);
    }
}