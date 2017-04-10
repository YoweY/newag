package com.info.aegis.lawpush4android.presenter;

import android.os.Message;
import android.util.Log;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.ChatActivityService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;

/**
 * Created by Administrator on 2016/12/13.
 */

public class ChatPresenter extends BasePresenter {
    public ChatPresenter(ChatActivityService chatActivityService) {
        super(chatActivityService.getContext());
        this.baseActivityService = chatActivityService;
        init();
    }
    public void voiceRead(String content){
        Message m = new Message();
        m.what = Constant.VOICE_READ;
        m.obj = content;
        baseActivityService.getHandler().sendMessage(m);
    }

    public void getSceneQuestion(final String sceneContent, final String sessionId){
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = chatService.getSceneQuestion(appContext,sessionId);
                JSONArray hotQuestionArray = jsonObject.optJSONArray("hot");
                String errorMsg = jsonObject.optString("data");
                if (!errorMsg.equals("") ) {
//               调取cause接口
                    LawPushApp.setMultipleValue(0);
                    Message m = new Message();
                    m.what = 101;
                    m.obj = sceneContent;
                    baseActivityService.getHandler().sendMessage(m);
                    return;
                }
                Msg.ChoiceMsg[] choiceMsgs = null;
                choiceMsgs = new Msg.ChoiceMsg[hotQuestionArray.length()];
                List<String> stringList = new ArrayList<String>();
                for (int i = 0; i < hotQuestionArray.length(); i++) {
                    stringList.add(hotQuestionArray.optString(i));
                }
                for (int j = 0; j < stringList.size(); j++) {
                    choiceMsgs[j] = new Msg.ChoiceMsg();
                    choiceMsgs[j].setContent(stringList.get(j));
                }
                Message msgType = new Message();
                msgType.what = Constant.CHAT_TYPE;
                msgType.obj = 1;
                baseActivityService.getHandler().sendMessage(msgType);

                Msg msg = new Msg();
                msg.setType(Msg.HOT_CAUSE_TYPE);
                msg.setChoiceMsgs(choiceMsgs);
                msg.setSIMILAR_QUESTION_TYPE(2);
                msg.setContent("为您找到以下热门相关问题：");
                Message message = new Message();
                message.what = Constant.NEW_MESSAGE;
                message.obj = msg;
                baseActivityService.getHandler().sendMessage(message);
            }
        });
    }
}
