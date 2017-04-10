package com.info.aegis.lawpush4android.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.http.ChatService;
import com.info.aegis.lawpush4android.http.iml.ChatServiceImpl;
import com.info.aegis.lawpush4android.model.bean.GuideSceneBean;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.MsgTagBean;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.model.bean.entity.law.CaseInfo;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.model.bean.entity.law.MsgCauseBean;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.MyToast;
import com.info.aegis.lawpush4android.view.BaseActivityService;
import com.info.aegis.lawpush4android.view.ChatActivityService;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aegis.aegisxunfei.MyConstant;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mcs on 2016/12/29.
 */

abstract class BasePresenter {
    ExecutorService threadPoolExecutor;
    ChatService chatService;
    BaseActivityService baseActivityService;
    Context appContext;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor myEditor;
    /**
     * Stack  Vector的子类，后进先出
     */
    private Stack<String> tagStack;

    BasePresenter(Context context) {
        appContext = context.getApplicationContext();
    }

    void init() {
        chatService = new ChatServiceImpl();
        threadPoolExecutor = Executors.newFixedThreadPool(3);

        tagStack = new Stack<>();
        sharedPreferences = appContext.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE);
        myEditor = sharedPreferences.edit();
    }

    /**
     * 分析
     *
     * @param content
     * @param options
     * @param type
     */
    public void getAnalyse(final String content, final String options, final Integer type) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject result = chatService.getAnalyse(appContext, content, options, type);
//                    Constant.hideProgressDialog();
                    JSONArray choices = result.optJSONArray("choice");
                    String[] tagChosen = null;
                    String[] tagIds = null;
                    if (choices != null) {
                        tagChosen = new String[choices.length()];
                        tagIds = new String[choices.length()];
                        for (int i = 0; i < choices.length(); i++) {
                            tagChosen[i] = choices.getString(i);
                            tagIds[i] = appContext.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString(tagChosen[i], "");
                        }
                    }

                    String action = result.getString("action");
                    Log.e("lawPush", "action---------------" + action);
                    Message m = new Message();
                    switch (action) {
                        case "father":
                            //根据语音输入勾选
                            m.what = Constant.FATHER;
                            m.obj = tagChosen;
                            if (tagIds != null) {
                                getFather(tagIds[0]);
                            }
                            break;
                        case "son":
                            //根据语音输入勾选
                            m.what = Constant.SON;
                            m.obj = tagChosen;
                            break;
                        case "tag":
                            //查看结果
                            m.what = Constant.RESULT;
                            m.obj = tagChosen;
                            break;
                        case "back":
                            //返回上一页    首页怎么处理？
                            m.what = Constant.BACK;
                            break;
                        case "home":
                            //返回首页
                            m.what = Constant.HOME;
                            break;
                        case "new":
                            //开始新的会话
                            m.what = Constant.NEW;
                            LocalBroadcastManager instance = LocalBroadcastManager.getInstance(appContext);
                            instance.sendBroadcast(new Intent("new"));
                            break;
                        case "law":
                            m.what = Constant.LAW;
                            break;
                        case "point":
                            m.what = Constant.POINT;
                            break;
                        case "case":
                            m.what = Constant.CASE;
                            break;
                        case "guide":
                            m.what = Constant.GUIDE;
                            break;
                        case "more":
                            //标签页查看更多
                            m.what = Constant.SAY_MORE;
                            break;
                        case "help":
                            m.what = Constant.HELP;
                            break;
                        case "detail":
                            //查看详情，进入咨询报告页
                            m.what = Constant.DETAIL;
                            break;
                        default:
                            throw new Exception("err action!!!");
                    }
                    baseActivityService.getHandler().sendMessage(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getPredictCause(final String content) {
        //保存当前问题的content  即title
        saveQuestion(content);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Constant.stopCountTime();
                    JSONObject jsonObject = chatService.getCause(appContext, content);
                    Log.e("lawPush", "     cause--" + jsonObject.toString());
                    Constant.setGodTextContent(jsonObject.toString(), 0, false, 2);
                    myEditor.putString("qid", jsonObject.optString("qid"));
                    myEditor.putString("content", content);
                    myEditor.commit();
                    int human = jsonObject.optInt("human");
                    int type = jsonObject.optInt("type");
                    String answerText = jsonObject.optString("answer");
                    JSONArray lawsArray = jsonObject.optJSONArray("laws");
                    JSONArray similarQuestion = jsonObject.optJSONArray("similar_question");
                    String errorMsg = jsonObject.optString("data");
                    int multipleValue = jsonObject.optInt("multi");
                    String resultId = jsonObject.optString("result_id");
                    JSONArray picturesArray = jsonObject.optJSONArray("pictures");
                    JSONObject motionObject = jsonObject.optJSONObject("motion");
                    int save = jsonObject.optInt("save");

                    if (save == 1) {
                        //救场语
                        LawPushApp.setChatChatSaveIngTimes(LawPushApp.getChatChatSaveIngTimes() + 1);
                        if (LawPushApp.getChatChatSaveIngTimes() > 4) {
                            //提示用户询问法律相关的问题
                            Msg msg = new Msg();
                            msg.setType(Msg.CHAT_SAVE_SCENE);
                            if (motionObject != null)
                                msg.setMotionsString(motionObject.toString());
                            msg.setContent("我们来问问法律相关的问题吧。");
                            Message m = new Message();
                            m.what = Constant.CHAT_SAVE_SCENE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);
                            LawPushApp.CHAT_CHAT_ING_TIMES = 0;
                        }
                    } else {
                        LawPushApp.setChatChatSaveIngTimes(0);
                    }

                    if (motionObject != null)
                        Log.e("lawPush", " action---" + motionObject.toString());

                    //多轮问答
                    LawPushApp.setMultipleValue(multipleValue);
                    LawPushApp.setResultId(resultId);

                    //先更改聊天界面上方四个按钮的状态
                    Message msgTypeqwqw = new Message();
                    msgTypeqwqw.what = Constant.CHAT_TYPE;
                    if (type == 0)
                        type = 3;//改为随便聊聊
                    msgTypeqwqw.obj = type;
                    baseActivityService.getHandler().sendMessage(msgTypeqwqw);
                    if (type != 3)
                        LawPushApp.CHAT_CHAT_ING_TIMES = 0;

                    if (multipleValue == 1) {

                        if (null != answerText) {
                            Msg msg = new Msg();
                            msg.setContent(answerText);
                            ArrayList<String> pictures = new ArrayList<String>();
                            if (picturesArray != null && picturesArray.length() > 0) {
                                for (int i = 0; i < picturesArray.length(); i++) {
                                    //  ["aaa.jpg","bbb.jpg","ccc.jpg"]
                                    String keys = picturesArray.get(i).toString().replace("\"", "");

                                    pictures.add(keys);

                                }
                                msg.setPictures(pictures);
                                msg.setType(Msg.ANSWER_PICTURES);
                                Message message = new Message();
                                message.what = Constant.NEW_MESSAGE;
                                message.obj = msg;
                                baseActivityService.getHandler().sendMessage(message);
                                return;
                            }


                        }




                        //多轮问答
                        JSONArray choiceArray = jsonObject.optJSONArray("choices");
                        Msg.ChoiceMsg[] choiceMsgs = null;
                        if (type == 12 && choiceArray != null && choiceArray.length() != 0 && !choiceArray.getString(0).equals("")) {
                            //分两次展示
                            choiceMsgs = new Msg.ChoiceMsg[choiceArray.length()];
                            List<String> stringList = new ArrayList<String>();
                            for (int i = 0; i < choiceArray.length(); i++) {
                                stringList.add(choiceArray.getString(i));
                            }
                            for (int j = 0; j < stringList.size(); j++) {
                                choiceMsgs[j] = new Msg.ChoiceMsg();
                                choiceMsgs[j].setContent(stringList.get(j));
                            }
                            LawPushApp.setDoubleAnswerDoctext(jsonObject.optString("doc"));
                            LawPushApp.setDoubleAnswerChoice(choiceMsgs);
                            LawPushApp.setCurrentSimilarCauseType(3);

                            Msg msg = new Msg();
                            msg.setType(Msg.CHAT_SOS_TEXT);
                            if (motionObject != null)
                                msg.setMotionsString(motionObject.toString());
                            msg.setContent(answerText);
                            Message m = new Message();
                            m.what = Constant.NEW_MESSAGE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        }

                        Msg msg = new Msg();
                        if (choiceArray != null && choiceArray.length() != 0 && !choiceArray.getString(0).equals("")) {
                            choiceMsgs = new Msg.ChoiceMsg[choiceArray.length()];
                            List<String> stringList = new ArrayList<String>();
                            for (int i = 0; i < choiceArray.length(); i++) {
                                stringList.add(choiceArray.getString(i));
                            }
                            for (int j = 0; j < stringList.size(); j++) {
                                choiceMsgs[j] = new Msg.ChoiceMsg();
                                choiceMsgs[j].setContent(stringList.get(j));
                            }
                            msg.setType(Msg.SIMILAR_CAUSE_TYPE);
                            if (motionObject != null)
                                msg.setMotionsString(motionObject.toString());
                            msg.setChoiceMsgs(choiceMsgs);
                            msg.setSIMILAR_QUESTION_TYPE(3);
                            msg.setResultId(resultId);
                        } else {
                            msg.setType(Msg.CHAT_SOS_TEXT);
                            LawPushApp.setMultipleValue(0);
                        }
                        if (answerText.equals("")) {
                            msg.setContent("请问您想了解以下哪种情形?");
                        } else {
                            msg.setContent(answerText);
                        }
                        Message message = new Message();
                        message.what = Constant.NEW_MESSAGE;
                        message.obj = msg;
                        baseActivityService.getHandler().sendMessage(message);
                        return;
                    }
                    if (multipleValue == 0) {
                        //正常流程
                        if (human == 1) {
                            Log.e("lawPush", "人工问题，改为讯飞语义");
                            //先更改聊天界面上方四个按钮的状态
                            Message msgType = new Message();
                            msgType.what = Constant.CHAT_TYPE;
                            msgType.obj = 3;
                            baseActivityService.getHandler().sendMessage(msgType);

                            Message m = new Message();
                            m.what = 2222;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        }

                        if (!errorMsg.equals("")) {
                            type = 3;
                        }

                        if (type == 20) {
                            //天气
                            int aqi = jsonObject.optInt("aqi");//空气质量指数
                            String detail = jsonObject.optString("detail");
                            String fengli = jsonObject.optString("fengli");
                            String fengxiang = jsonObject.optString("fengxiang");
                            String high = "最" + jsonObject.optString("high");
                            String low = "最" + jsonObject.optString("low");
                            String pm = jsonObject.optString("pm25");
                            String quality = jsonObject.optString("quality");
                            String shidu = jsonObject.optString("shidu");
                            String sunrise = jsonObject.optString("sunrise");
                            String sunset = jsonObject.optString("sunset");
                            String undatetime = jsonObject.optString("updatetime");
                            String weather = jsonObject.optString("weather");
                            String wendu = jsonObject.optInt("wendu") + "℃";

                            String content = "今日天气\n" + weather + "\n" + low + "\n" + high + "\n现在温度：" + wendu + "\n风向：" + fengxiang + "\n风力：" + fengli + "\nPM2.5指数：" + pm + "\n空气质量：" + quality + "\n湿度：" + shidu + "\n" + detail;
                            Msg msg = new Msg();
                            msg.setType(Msg.CHAT_WEATHER);
                            msg.setContent(content);
                            Message m = new Message();
                            m.what = Constant.NEW_MESSAGE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        }

                        if (type == 0 || type == 3) {

                            LawPushApp.CHAT_CHAT_ING_TIMES++;
                            if (LawPushApp.CHAT_CHAT_ING_TIMES > 4) {
                                Msg msg = new Msg();
                                msg.setType(Msg.CHAT_SAVE_SCENE);
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                msg.setContent("我们来问问法律相关的问题吧。");
                                Message m = new Message();
                                m.what = Constant.CHAT_SAVE_SCENE;
                                m.obj = msg;
                                baseActivityService.getHandler().sendMessage(m);
                            }
                            if (answerText.equals("")) {
                                Message m = new Message();
                                m.what = 2222;
                                baseActivityService.getHandler().sendMessage(m);
                            } else {
                                if (similarQuestion != null && similarQuestion.length() != 0) {
                                    Msg.ChoiceMsg[] choiceMsgs = null;
                                    choiceMsgs = new Msg.ChoiceMsg[similarQuestion.length()];
                                    List<String> stringList = new ArrayList<String>();
                                    for (int i = 0; i < similarQuestion.length(); i++) {
                                        stringList.add(similarQuestion.getString(i));
                                    }
                                    for (int j = 0; j < stringList.size(); j++) {
                                        choiceMsgs[j] = new Msg.ChoiceMsg();
                                        choiceMsgs[j].setContent(stringList.get(j));
                                    }
                                    Msg msg = new Msg();
                                    msg.setType(Msg.SIMILAR_CAUSE_TYPE);
                                    msg.setChoiceMsgs(choiceMsgs);
                                    msg.setSIMILAR_QUESTION_TYPE(2);
                                    if (answerText.equals("")) {
                                        msg.setContent("为您找到以下相似问题：");
                                    } else {
                                        msg.setContent(answerText);
                                    }
                                    if (motionObject != null)
                                        msg.setMotionsString(motionObject.toString());
                                    Message message = new Message();
                                    message.what = Constant.NEW_MESSAGE;
                                    message.obj = msg;
                                    baseActivityService.getHandler().sendMessage(message);
                                } else {
                                    Msg msg = new Msg();
                                    msg.setType(Msg.CHAT_CHAT);
                                    if (motionObject != null)
                                        msg.setMotionsString(motionObject.toString());
                                    msg.setContent(answerText);
                                    Message m = new Message();
                                    m.what = Constant.NEW_MESSAGE;
                                    m.obj = msg;
                                    baseActivityService.getHandler().sendMessage(m);
                                }
                            }
                            return;
                        }
                        if (type == 5) {
                            if (lawsArray == null || lawsArray.length() == 0) {
                                Message m = new Message();
                                m.what = 2222;
                                baseActivityService.getHandler().sendMessage(m);
                            } else {
                                List<Law> lawlists = new ArrayList<>();
                                JSONObject lawObj;
                                for (int i = 0; i < lawsArray.length(); i++) {
                                    Law law = new Law();
                                    Iterator<String> keys = lawsArray.optJSONObject(i).keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        lawObj = lawsArray.optJSONObject(i).optJSONObject(key);
                                        law.setTitle(lawObj.optString("title"));
                                        law.setId(lawObj.optString("id"));
                                        law.setContent(lawObj.optString("content"));
                                        law.setTag(lawObj.optString("tag"));
                                    }
                                    lawlists.add(law);
                                }

                                Msg msg = new Msg();
                                msg.setType(Msg.CHAT_LAWS);
                                msg.setLawList(lawlists);
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                Message message = new Message();
                                message.what = Constant.NEW_MESSAGE;
                                message.obj = msg;
                                baseActivityService.getHandler().sendMessage(message);
                            }
                            return;
                        }
                        if (type == 4) {

                            if (null != answerText) {
                                Msg msg = new Msg();
                                msg.setContent(answerText);
                                ArrayList<String> pictures = new ArrayList<String>();

//                                    pictures.add("Penguins.jpg");
//                                    pictures.add("Penguins.jpg");
//                                    pictures.add("Penguins.jpg");
                                if (picturesArray != null && picturesArray.length() > 0) {
                                    for (int i = 0; i < picturesArray.length(); i++) {
                                        //  ["aaa.jpg","bbb.jpg","ccc.jpg"]
                                        String keys = picturesArray.get(i).toString().replace("\"", "");
                                        pictures.add(keys);
                                    }
                                    msg.setPictures(pictures);
                                    msg.setType(Msg.ANSWER_PICTURES);
                                    Message message = new Message();
                                    message.what = Constant.NEW_MESSAGE;
                                    message.obj = msg;
                                    baseActivityService.getHandler().sendMessage(message);
                                    return;
                                }

                            }

                            if (human == 1) {
                                Message m = new Message();
                                m.what = 2222;
                                baseActivityService.getHandler().sendMessage(m);
                            } else if (similarQuestion != null && similarQuestion.length() != 0) {
                                Msg.ChoiceMsg[] choiceMsgs = null;
                                choiceMsgs = new Msg.ChoiceMsg[similarQuestion.length()];
                                List<String> stringList = new ArrayList<String>();
                                for (int i = 0; i < similarQuestion.length(); i++) {
                                    stringList.add(similarQuestion.getString(i));
                                }
                                for (int j = 0; j < stringList.size(); j++) {
                                    choiceMsgs[j] = new Msg.ChoiceMsg();
                                    choiceMsgs[j].setContent(stringList.get(j));
                                }
                                Msg msg = new Msg();
                                msg.setType(Msg.SIMILAR_CAUSE_TYPE);
                                msg.setChoiceMsgs(choiceMsgs);
                                msg.setSIMILAR_QUESTION_TYPE(1);
                                if (answerText.equals("")) {
                                    msg.setContent("为您找到以下相似问题：");
                                } else {
                                    msg.setContent(answerText);
                                }
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                Message message = new Message();
                                message.what = Constant.NEW_MESSAGE;
                                message.obj = msg;
                                baseActivityService.getHandler().sendMessage(message);
                            } else {
                                JSONArray causeArray = jsonObject.getJSONArray("cause");

                                Msg.ChoiceMsg[] choiceMsgs = null;
                                if (causeArray != null) {
                                    choiceMsgs = new Msg.ChoiceMsg[causeArray.length()];
                                    String vasue = null;
                                    for (int i = 0; i < causeArray.length(); i++) {
                                        Iterator<String> itr = causeArray.getJSONObject(i).keys();
                                        while (itr.hasNext()) {
                                            String caseCauseId = itr.next();
                                            choiceMsgs[i] = new Msg.ChoiceMsg();
                                            vasue = causeArray.optJSONObject(i).optString(caseCauseId);
                                            choiceMsgs[i].setContent(causeArray.getJSONObject(i).getString(caseCauseId));
                                            choiceMsgs[i].setCaseCauseId(caseCauseId);
                                            myEditor.putString(choiceMsgs[i].getContent(), caseCauseId);
                                            myEditor.commit();
                                        }
                                        if (vasue.contains("null")) {
                                            Message m = new Message();
                                            m.what = 2222;
                                            baseActivityService.getHandler().sendMessage(m);
                                            return;
                                        }
                                    }

                                    if (choiceMsgs.length == 1) {
                                        //不显示案由，直接进行getFather
                                        LawPushApp.setJustOneCauseText(choiceMsgs[0].getContent());
                                        getFather(sharedPreferences.getString(choiceMsgs[0].getContent(), ""));
                                        return;
                                    }
                                    LawPushApp.setJustOneCauseText("");

                                    //显示返回的多个案由
                                    if (!choiceMsgs[0].getContent().equals("null")) {
                                        Msg msg = new Msg();
                                        msg.setType(Msg.TYPE_CAUSE);
                                        msg.setChoiceMsgs(choiceMsgs);
                                        if (motionObject != null)
                                            msg.setMotionsString(motionObject.toString());
                                        if (motionObject != null)
                                            msg.setMotionsString(motionObject.toString());
                                        if (answerText.equals(""))
                                            msg.setContent("您的问题属于以下哪种情形？");
                                        else
                                            msg.setContent(answerText);
                                        Message m = new Message();
                                        m.what = Constant.NEW_MESSAGE;
                                        m.obj = msg;
                                        baseActivityService.getHandler().sendMessage(m);
                                    }
                                } else {
                                    MyLog.e("lawPush", "wo shi nhu;lll");
                                }
                            }
                            return;
                        }
                        if (type == 1 || type == 2) {

                            if (null != answerText) {
                                Msg msg = new Msg();
                                msg.setContent(answerText);
                                ArrayList<String> pictures = new ArrayList<String>();
                                if (picturesArray != null && picturesArray.length() > 0) {
                                    for (int i = 0; i < picturesArray.length(); i++) {
                                        //  ["aaa.jpg","bbb.jpg","ccc.jpg"]
                                        String keys = picturesArray.get(i).toString().replace("\"", "");

                                        pictures.add(keys);

                                    }
                                    msg.setPictures(pictures);
                                    msg.setType(Msg.ANSWER_PICTURES);
                                    Message message = new Message();
                                    message.what = Constant.NEW_MESSAGE;
                                    message.obj = msg;
                                    baseActivityService.getHandler().sendMessage(message);
                                    return;
                                }


                            }

                            if (answerText.equals("")) {
                                if (lawsArray == null || lawsArray.length() == 0) {
                                    Message m = new Message();
                                    m.what = 2222;
                                    baseActivityService.getHandler().sendMessage(m);
                                    return;
                                } else {
                                    List<Law> lawlists = new ArrayList<>();
                                    JSONObject lawObj;
                                    for (int i = 0; i < lawsArray.length(); i++) {
                                        Law law = new Law();
                                        Iterator<String> keys = lawsArray.optJSONObject(i).keys();
                                        while (keys.hasNext()) {
                                            String key = keys.next();
                                            lawObj = lawsArray.optJSONObject(i).optJSONObject(key);
                                            law.setTitle(lawObj.optString("title"));
                                            law.setId(lawObj.optString("id"));
                                            law.setContent(lawObj.optString("content"));
                                            law.setTag(lawObj.optString("tag"));
                                        }
                                        lawlists.add(law);
                                    }

                                    Msg msg = new Msg();
                                    msg.setType(Msg.CHAT_LAWS);
                                    msg.setLawList(lawlists);
                                    if (motionObject != null)
                                        msg.setMotionsString(motionObject.toString());
                                    Message message = new Message();
                                    message.what = Constant.NEW_MESSAGE;
                                    message.obj = msg;
                                    baseActivityService.getHandler().sendMessage(message);
                                }

                                return;
                            }
                            if (similarQuestion != null && similarQuestion.length() != 0) {
                                Msg.ChoiceMsg[] choiceMsgs = null;
                                choiceMsgs = new Msg.ChoiceMsg[similarQuestion.length()];
                                List<String> stringList = new ArrayList<String>();
                                for (int i = 0; i < similarQuestion.length(); i++) {
                                    stringList.add(similarQuestion.getString(i));
                                }
                                for (int j = 0; j < stringList.size(); j++) {
                                    choiceMsgs[j] = new Msg.ChoiceMsg();
                                    choiceMsgs[j].setContent(stringList.get(j));
                                }
                                Msg msg = new Msg();
                                msg.setType(Msg.SIMILAR_CAUSE_TYPE);
                                msg.setChoiceMsgs(choiceMsgs);
                                msg.setSIMILAR_QUESTION_TYPE(2);
                                if (answerText.equals("")) {
                                    msg.setContent("为您找到以下相似问题：");
                                } else {
                                    msg.setContent(answerText);
                                }
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                Message message = new Message();
                                message.what = Constant.NEW_MESSAGE;
                                message.obj = msg;
                                baseActivityService.getHandler().sendMessage(message);
                                return;
                            }

                            List<Law> lawList = new ArrayList<>();
                            if (lawsArray != null) {
                                for (int i = 0; i < lawsArray.length(); i++) {
                                    Law law = new Law();
                                    law.setTitle(lawsArray.getJSONObject(i).getString("title"));
                                    law.setContent(lawsArray.getJSONObject(i).getString("content"));
                                    law.setId(lawsArray.getJSONObject(i).getString("id"));
                                    lawList.add(law);
                                }
                            }
                            Msg msg = new Msg();
                            msg.setType(Msg.PROGRAMER_QUESTION);
                            msg.setLawList(lawList);
                            msg.setProgramQuestionAnswer(answerText);
                            if (motionObject != null)
                                msg.setMotionsString(motionObject.toString());
                            Message m = new Message();
                            m.what = Constant.NEW_MESSAGE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);


                            return;
                        }

                        //百度百科 type  7
                        if (type == 7) {
                            if (answerText.equals("")) {
                                Message m = new Message();
                                m.what = 2222;
                                baseActivityService.getHandler().sendMessage(m);
                            } else {
                                Msg msg = new Msg();
                                msg.setType(Msg.CHAT_SOS_TEXT);
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                msg.setContent(answerText);
                                Message m = new Message();
                                m.what = Constant.NEW_MESSAGE;
                                m.obj = msg;
                                baseActivityService.getHandler().sendMessage(m);
                            }

                            return;
                        }
                        // 百度知道 type 8
                        if (type == 8) {
                            if (answerText.equals("")) {
                                Message m = new Message();
                                m.what = 2222;
                                baseActivityService.getHandler().sendMessage(m);
                            } else {
                                Msg msg = new Msg();
                                msg.setType(Msg.CHAT_SOS_TEXT);
                                if (motionObject != null)
                                    msg.setMotionsString(motionObject.toString());
                                msg.setContent(answerText);
                                Message m = new Message();
                                m.what = Constant.NEW_MESSAGE;
                                m.obj = msg;
                                baseActivityService.getHandler().sendMessage(m);
                            }

                            return;
                        }
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("BasePresenter", "e:" + e);
                }
            }
        });
    }

    private void saveQuestion(String content) {
        myEditor.putString("content", content);
        myEditor.commit();
    }

    public String getQuestion() {
        return sharedPreferences.getString("content", "");
    }

    public Stack<String> getTagStack() {
        return tagStack;
    }

    public void more(Msg msg) {
        Message message = new Message();
        message.what = Constant.MORE;
        message.obj = msg;
        baseActivityService.getHandler().sendMessage(message);
    }

    public void getExample(final String content) {
        //先显示用户选择的问题
        Msg msg = new Msg();
        msg.setType(Msg.TYPE_SEND);
        msg.setContent(content);
        Message m = new Message();
        m.what = Constant.NEW_MESSAGE;
        m.obj = msg;
        baseActivityService.getHandler().sendMessage(m);
        //将提问的内容存储到SP中
        saveQuestion(content);
        //发起请求
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject result = chatService.getExample(appContext, content);
                    myEditor.putString("qid", result.getString("qid"));
                    myEditor.commit();
                    //处理查询结果，并显示
                    sendResultMsg(result);
                    //显示 “需要or不需要的布局”
//                    Msg msg1 = new Msg();
//                    msg1.setType(Msg.TYPE_MORE);
//                    msg1.setContent("");
//                    Message receiveMsg = new Message();
//                    receiveMsg.what = Constant.NEW_MESSAGE;
//                    receiveMsg.obj = msg1;
//                    baseActivityService.getHandler().sendMessage(receiveMsg);
                    showQR();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 处理MQ发给客户端的消息
     *
     * @param jsonObject
     */
    public void dealServerMessageRabbitMQ(JSONObject jsonObject) {

        Constant.stopCountTime();

        JSONObject tagsObject = null;
        JSONObject caseCausesObject = null;
        String answerText = "";
        tagsObject = jsonObject.optJSONObject("tags");
        caseCausesObject = jsonObject.optJSONObject("caseCauses");
        answerText = jsonObject.optString("text");


        try {
            if (caseCausesObject != null && !jsonObject.getString("caseCauses").equals("null")) {
                List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                Iterator<String> key = caseCausesObject.keys();
                while (key.hasNext()) {
                    String name = key.next();
                    int value = caseCausesObject.getInt(name);
                    MsgCauseBean msgCauseBean = new MsgCauseBean();
                    msgCauseBean.setName(name);
                    msgCauseBean.setValue(value + "");
                    msgCauseBeanList.add(msgCauseBean);
                }

                Msg.ChoiceMsg[] choiceMsgs = null;
                choiceMsgs = new Msg.ChoiceMsg[msgCauseBeanList.size()];
                for (int i = 0; i < msgCauseBeanList.size(); i++) {
                    choiceMsgs[i] = new Msg.ChoiceMsg();
                    choiceMsgs[i].setContent(msgCauseBeanList.get(i).getName());
                    choiceMsgs[i].setCaseCauseId(msgCauseBeanList.get(i).getValue());
                    myEditor.putString(choiceMsgs[i].getContent(), msgCauseBeanList.get(i).getValue() + "");
                    myEditor.commit();
                }

                if (choiceMsgs.length == 1) {
                    //不显示案由，直接进行getFather
                    LawPushApp.setJustOneCauseText(choiceMsgs[0].getContent());
                    getFather(sharedPreferences.getString(choiceMsgs[0].getContent(), ""));
                    return;
                }
                LawPushApp.setJustOneCauseText("");

                //显示案由
                if (!choiceMsgs[0].getContent().equals("null")) {
                    Msg msg = new Msg();
                    msg.setType(Msg.TYPE_CAUSE);
                    msg.setChoiceMsgs(choiceMsgs);
                    if (answerText.equals("") || answerText.equals("null"))
                        msg.setContent("您的问题属于：");
                    else
                        msg.setContent(answerText);
                    Message m = new Message();
                    m.what = Constant.NEW_MESSAGE;
                    m.obj = msg;
                    baseActivityService.getHandler().sendMessage(m);
                }
                return;
            }
            if (!jsonObject.getString("tags").equals("null") && tagsObject != null) {
                //默认全都是父标签
                List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                Iterator<String> key = tagsObject.keys();
                while (key.hasNext()) {
                    String name = key.next();
                    String value = tagsObject.getString(name);

                    MsgTagBean msgTagBean = new MsgTagBean();
                    msgTagBean.setZhName(name);
                    msgTagBean.setEnName(value);
                    msgTagBean.setType(0);
                    msgTagBeanList.add(msgTagBean);
                }

                Msg.ChoiceMsg[] choiceMsgs = null;
                choiceMsgs = new Msg.ChoiceMsg[msgTagBeanList.size()];
                for (int i = 0; i < msgTagBeanList.size(); i++) {
                    String enName = msgTagBeanList.get(i).getEnName();
                    String zhName = msgTagBeanList.get(i).getZhName();
                    int tagType = msgTagBeanList.get(i).getType();

                    choiceMsgs[i] = new Msg.ChoiceMsg();
                    choiceMsgs[i].setEnName(enName);
                    choiceMsgs[i].setContent(zhName);
                    choiceMsgs[i].setType(tagType);

                    MsgTagBean msgTagBean = new MsgTagBean();
                    msgTagBean.setEnName(enName);
                    msgTagBean.setZhName(zhName);
                    msgTagBean.setType(tagType);

                    myEditor.putString(choiceMsgs[i].getContent(), msgTagBean.toString());
                    myEditor.commit();

                }

                //判断返回的标签个数
                if (choiceMsgs.length == 1) {
//                只有一个tag，直接帮用户自动进行提问
                    String value = sharedPreferences.getString(choiceMsgs[0].getContent(), "");
                    JSONObject jsonObject1 = new JSONObject(value);
                    String[] sss = {jsonObject1.optString("enName")};
                    LawPushApp.setJustOneTagText(choiceMsgs[0].getContent());
                    getSon(sss);
                    return;
                }
                //显示返回的tag
                Msg msg = new Msg();
                msg.setType(Msg.TYPE_TAG);
                msg.setChoiceMsgs(choiceMsgs);
                if (answerText.equals("") || answerText.equals("null")) {
                    msg.setContent("请结合实际情况进行选择");
                } else {
                    msg.setContent(answerText);
                }

                Message m1 = new Message();
                m1.what = Constant.NEW_MESSAGE;
                m1.obj = msg;
                baseActivityService.getHandler().sendMessage(m1);
                return;
            }
            Msg msg = new Msg();
            msg.setType(Msg.CHAT_SOS_TEXT);
            if (answerText.equals(""))
                msg.setContent("哈哈，打盹了，再试一次吧");
            else
                msg.setContent(answerText);
            Message m = new Message();
            m.what = Constant.NEW_MESSAGE;
            m.obj = msg;
            baseActivityService.getHandler().sendMessage(m);
        } catch (JSONException e) {
            e.printStackTrace();
            MyLog.e("lawPush", "  解析报错" + e.toString());
            Message m = new Message();
            m.what = 2222;
            baseActivityService.getHandler().sendMessage(m);
        }
    }

    public void getFather(final String caseCauseId) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //判断是不是非标问题
                    JSONArray tags = null;
                    if (Constant.HUMAN_CHAT == 1) {
                        MyLog.e("lawPush", " 非标问题,弹出加载框...调用human接口，传递caseCauseId,并进行MQ队列监听");
                        chatService.getHuman(appContext, caseCauseId);
                        Constant.HUMAN_CHAT = 2;
                        Message m = new Message();
                        m.what = Constant.RECEICE_SERVER_MESSAGE;
                        m.arg1 = 1;
                        baseActivityService.getHandler().sendMessage(m);
                    } else {
                        MyLog.e("lawPush", " getfather() 标准问题");
                        JSONObject jsonObject = chatService.getFather(appContext, caseCauseId);
                        tags = jsonObject.optJSONArray("tags");
                        String wenanText = jsonObject.optString("text");
                        ((ChatActivityService) baseActivityService).setType(1);
                        Msg.ChoiceMsg[] choiceMsgs = null;
                        if (tags != null) {
                            choiceMsgs = new Msg.ChoiceMsg[tags.length()];
                            for (int i = 0; i < tags.length(); i++) {
                                String enName = tags.getJSONObject(i).getString("enName");
                                String zhName = tags.getJSONObject(i).getString("zhName");
                                int tagType = tags.getJSONObject(i).optInt("type");

                                choiceMsgs[i] = new Msg.ChoiceMsg();
                                choiceMsgs[i].setEnName(enName);
                                choiceMsgs[i].setContent(zhName);
                                choiceMsgs[i].setType(tagType);

                                MsgTagBean msgTagBean = new MsgTagBean();
                                msgTagBean.setEnName(enName);
                                msgTagBean.setZhName(zhName);
                                msgTagBean.setType(tagType);

                                myEditor.putString(choiceMsgs[i].getContent(), msgTagBean.toString());
                                myEditor.commit();

                            }
                        }

                        //判断返回的标签个数
                        if (choiceMsgs.length == 1) {
                            //只有一个tag，直接帮用户自动进行提问
                            //先判断标签类型
                            if (choiceMsgs[0].getType() == 0) {
                                //父标签,接着请求son接口
                                String value = sharedPreferences.getString(choiceMsgs[0].getContent(), "");
                                JSONObject jsonObject1 = new JSONObject(value);
                                String[] sss = {jsonObject1.optString("enName")};
                                LawPushApp.setJustOneTagText(choiceMsgs[0].getContent());
                                getSon(sss);
                            } else {
                                //子标签，直接拿结果
                                myEditor.putString("tagChosen", choiceMsgs[0].getEnName());
                                myEditor.commit();
                                LawPushApp.setJustOneTagText(choiceMsgs[0].getContent());
                                getTag();
                            }
                            return;
                        }
                        if (choiceMsgs.length == 0) {
                            //本地就场
                            Log.e("lawPush", "讯飞语义库");
                            Message m = new Message();
                            m.what = 2222;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        }
                        LawPushApp.setJustOneTagText("");
                        //显示返回的tag
                        Msg msg = new Msg();
                        msg.setType(Msg.TYPE_TAG);
                        msg.setChoiceMsgs(choiceMsgs);
                        if (wenanText.equals("")) {
                            msg.setContent("请结合实际情况进行选择");
                        } else {
                            msg.setContent(wenanText);
                        }
                        if (!LawPushApp.getJustOneCauseText().equals("")) {
                            msg.setContent("您的问题属于" + LawPushApp.getJustOneCauseText() + msg.getContent());
                        }
                        LawPushApp.setJustOneCauseText("");

                        Message m1 = new Message();
                        m1.what = Constant.NEW_MESSAGE;
                        m1.obj = msg;
                        baseActivityService.getHandler().sendMessage(m1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Message m = new Message();
                    m.what = 2222;
                    baseActivityService.getHandler().sendMessage(m);
                }
            }
        });
    }

    public void getSon(final String[] tagChosen) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (tagChosen != null) {
                        String tagStr = "";
                        for (int i = tagChosen.length - 1; i > 0; i--) {
                            //逐个压栈
                            tagStack.push(tagChosen[i]);
                            tagStr += tagChosen[i] + ",";
                        }
                        tagStack.push(tagChosen[0]);
                        tagStr += tagChosen[0];
                        //存储用户已勾选的标签
                        String curTagChosen = sharedPreferences.getString("tagChosen", "");
                        if (!curTagChosen.equals("")) {
                            curTagChosen += ",";
                        }
                        //以逗号分隔，存储在SP中,保存当前选中过的标签
                        myEditor.putString("tagChosen", curTagChosen + tagStr);
                        myEditor.commit();
                    }
                    //取出栈顶的元素,进行请求
                    String s = tagStack.pop();
                    JSONObject result = chatService.getSon(appContext, s);

                    if (!result.optString("qid").equals("")) {
                        myEditor.putString("qid", result.optString("qid"));
                        myEditor.commit();
                    }
                    //分析请求结果
                    JSONArray tags = result.getJSONArray("tags");
                    while (tags.length() == 0) {
                        //如果返回tag数量为0
                        if (tagStack.empty()) {
                            //如果栈为空，则查看结果，并在之后返回二维码
                            getTag();
                            return;
                        }
                        //如果栈不为空，则再次从栈中取出栈顶元素进行查找
                        tags = chatService.getSon(appContext, tagStack.pop()).getJSONArray("tags");
                    }

                    Msg.ChoiceMsg[] choiceMsgs = null;
                    choiceMsgs = new Msg.ChoiceMsg[tags.length()];

                    for (int i = 0; i < tags.length(); i++) {
                        String tagId = tags.getJSONObject(i).getString("enName");
                        choiceMsgs[i] = new Msg.ChoiceMsg();
                        choiceMsgs[i].setContent(tags.getJSONObject(i).getString("zhName"));
                        choiceMsgs[i].setType(tags.getJSONObject(i).optInt("type"));
                        choiceMsgs[i].setEnName(tagId);
                        MsgTagBean msgTagBean = new MsgTagBean();
                        msgTagBean.setEnName(tagId);
                        msgTagBean.setZhName(tags.getJSONObject(i).getString("zhName"));
                        msgTagBean.setType(tags.getJSONObject(i).optInt("type"));
                        myEditor.putString(choiceMsgs[i].getContent(), msgTagBean.toString());
                        myEditor.commit();
                    }

                    //判断返回的标签个数
                    if (choiceMsgs.length == 1) {
                        //只有一个tag，直接帮用户自动进行提问
                        //先判断标签类型
                        if (choiceMsgs[0].getType() == 0) {
                            //父标签,接着请求son接口
                            String value = sharedPreferences.getString(choiceMsgs[0].getContent(), "");
                            JSONObject jsonObject1 = new JSONObject(value);
                            String[] sss = {jsonObject1.optString("enName")};
                            LawPushApp.setJustOneTagText(choiceMsgs[0].getContent());
                            getSon(sss);
                        } else {
                            //子标签，直接拿结果
                            myEditor.putString("tagChosen", choiceMsgs[0].getContent());
                            myEditor.commit();
                            LawPushApp.setJustOneTagText(choiceMsgs[0].getContent());
                            getTag();
                        }
                        return;
                    }

                    String text = result.optString("text");
                    Msg msg = new Msg();
                    msg.setType(Msg.TYPE_TAG);
                    msg.setChoiceMsgs(choiceMsgs);
                    if (text.equals("")) {
                        msg.setContent("请结合实际情况选择一下相关情形：");
                    } else {
                        msg.setContent(text);
                    }

                    if (!LawPushApp.getJustOneTagText().equals("")) {
                        msg.setContent(LawPushApp.getJustOneTagText());
                    }
                    LawPushApp.setJustOneTagText("");
                    if (!LawPushApp.getJustOneCauseText().equals("")) {
                        msg.setContent("您的问题属于" + LawPushApp.getJustOneCauseText() + "下的" + msg.getContent() + "情形,请结合实际情况选择一下相关情形：");
                    }
                    LawPushApp.setJustOneCauseText("");

                    if (msg.getContent().equals(""))
                        msg.setContent("请结合实际情况选择一下相关情形：");

                    Message m = new Message();
                    m.what = Constant.NEW_MESSAGE;
                    m.obj = msg;
                    baseActivityService.getHandler().sendMessage(m);

                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.e("lawPush", "  son---" + e.toString());
                }
            }
        });
    }

    /**
     * 结果
     */
    public void getTag() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = chatService.getTag(appContext);
                    Constant.setGodTextContent(jsonObject.toString(), 0, false, 3);
                    sendResultMsg(jsonObject);
                    showQR();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendResultMsg(JSONObject result) throws JSONException {

        JSONArray pointContent = result.optJSONArray("points");
        JSONArray lawContent = result.optJSONArray("laws");
        JSONArray caseContent = result.optJSONArray("cases");
        JSONArray guideContent = result.optJSONArray("guides");
        String answerText = result.optString("test");
        Law law = null;
        if (lawContent == null || lawContent.length() == 0) {
            MyLog.e("lawPush", "run: null law");
        } else {
            law = new Law();
            JSONObject item = lawContent.optJSONObject(0).optJSONObject(lawContent.optJSONObject(0).keys().next());
            law.setTitle(item.optString("title"));
            law.setContent(formatContent(item.optString("content")));
            law.setId(item.optString("id"));
        }
        JudicialPoint point = null;
        if (pointContent == null || pointContent.length() == 0) {
            MyLog.e("lawPush", "run: null point");
        } else {
            point = new JudicialPoint();
            JSONObject item = pointContent.optJSONObject(0).optJSONObject(pointContent.optJSONObject(0).keys().next());
            point.setTitle(item.optString("title"));
            point.setContent(formatContent(item.optString("content")));
            point.setId(item.optString("id"));
        }
        CaseInfo cases = null;
        if (caseContent == null || caseContent.length() == 0) {
            MyLog.e("lawPush", "run: null case");
        } else {
            cases = new CaseInfo();
            JSONObject item = caseContent.optJSONObject(0).optJSONObject(caseContent.optJSONObject(0).keys().next());
            cases.setTitle(item.optString("title"));
            cases.setContent(formatContent(item.optString("content")));
            cases.setId(item.optString("id"));
        }
        GuidanceCase guidanceCase = null;
        if (guideContent == null || guideContent.length() == 0 || guideContent.toString().equals("[]")) {
            MyLog.e("lawPush", "run: null guidanceCase");
        } else {
            guidanceCase = new GuidanceCase();
            JSONObject item = guideContent.optJSONObject(0).optJSONObject(guideContent.optJSONObject(0).keys().next());
            guidanceCase.setTitle(item.optString("title"));
            guidanceCase.setContent(formatContent(item.optString("gist")));
            guidanceCase.setId(item.optString("id"));
        }

        Msg msg = new Msg();
        if (answerText.equals(""))
            msg.setContent("我为您找到了如下相关法律知识，您可以查看详细结果：");
        else
            msg.setContent(answerText);
        if (!LawPushApp.getJustOneTagText().equals("")) {
            msg.setContent(LawPushApp.getJustOneTagText());
        }
        LawPushApp.setJustOneTagText("");
        if (!LawPushApp.getJustOneCauseText().equals("")) {
            msg.setContent("您的问题属于" + LawPushApp.getJustOneCauseText() + "下的" + msg.getContent() + "情形,我为您找到了如下相关法律知识，您可以查看详细结果：");
        }
        LawPushApp.setJustOneCauseText("");
        if (msg.getContent().equals(""))
            msg.setContent("我为您找到了如下相关法律知识，您可以查看详细结果：");

        msg.setType(Msg.TYPE_RESULT);
        msg.setLaw(law);
        msg.setPoint(point);
        msg.setCaseInfo(cases);
        msg.setGuidanceCase(guidanceCase);

        Message m = new Message();
        m.what = Constant.NEW_MESSAGE;
        m.obj = msg;
        baseActivityService.getHandler().sendMessage(m);
    }

    private String formatContent(String content) {
        //调整内容长度
        return content;
    }


    /**
     * 显示二维码
     */
    public void showQR() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Msg msg1 = new Msg();
                msg1.setType(Msg.TYPE_QR);
                msg1.setContent("");
                String qidsss = sharedPreferences.getString("qid", "");
                Log.e("lawPush", "           qid:" + qidsss);
                Request<Bitmap> request = NoHttp.createImageRequest("http://180.96.11.69:8500/api/tools/qr_code?" +
                        "url=https://law.push.aegis-info.com/newDataPage/" + qidsss, RequestMethod.GET);
                Response<Bitmap> response = NoHttp.startRequestSync(request);

                msg1.setQrBitmap(response.get());

                Message receiveMsg = new Message();
                receiveMsg.what = Constant.NEW_MESSAGE;
                receiveMsg.obj = msg1;
                baseActivityService.getHandler().sendMessage(receiveMsg);
            }
        }).start();

    }

    /**
     * 多轮问答
     *
     * @param choice
     */
    public void getMultiple(final String resultId, final String choice) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = chatService.getMultiple(appContext, resultId, choice);
                Constant.setGodTextContent(jsonObject.toString(), 0, false, 4);
                String errorMsg = jsonObject.optString("data");
                String answerText = jsonObject.optString("answer");
                String resetId = jsonObject.optString("result_id");
                int multileValue = jsonObject.optInt("multi");
                JSONArray choiceArray = jsonObject.optJSONArray("choices");
                LawPushApp.setResultId(resetId);
                LawPushApp.setMultipleValue(multileValue);
                int type = jsonObject.optInt("type");
                ;
                int typeeeee = 0;
                if (!errorMsg.equals("")) {
                    typeeeee = 3;
                }
                if (typeeeee == 3) {
                    Message m = new Message();
                    m.what = 2222;
                    baseActivityService.getHandler().sendMessage(m);
                    return;
                }


                if (type == 12 && choiceArray != null && choiceArray.length() != 0 && !choiceArray.optString(0).equals("")) {
                    //分两次展示
                    Msg.ChoiceMsg[] choiceMsgs = null;
                    choiceMsgs = new Msg.ChoiceMsg[choiceArray.length()];
                    List<String> stringList = new ArrayList<String>();
                    for (int i = 0; i < choiceArray.length(); i++) {
                        stringList.add(choiceArray.optString(i));
                    }
                    for (int j = 0; j < stringList.size(); j++) {
                        choiceMsgs[j] = new Msg.ChoiceMsg();
                        choiceMsgs[j].setContent(stringList.get(j));
                    }
                    LawPushApp.setDoubleAnswerDoctext(jsonObject.optString("doc"));
                    LawPushApp.setDoubleAnswerChoice(choiceMsgs);
                    LawPushApp.setCurrentSimilarCauseType(3);
                    LawPushApp.setResultId(resetId);

                    Msg msg = new Msg();
                    msg.setType(Msg.CHAT_SOS_TEXT);
                    msg.setContent(answerText);

                    Message m = new Message();
                    m.what = Constant.NEW_MESSAGE;
                    m.obj = msg;
                    baseActivityService.getHandler().sendMessage(m);
                    return;
                }

                try {
                    JSONArray choiceArraysds = jsonObject.optJSONArray("choices");
                    if (choiceArraysds != null && choiceArraysds.length() != 0 && !choiceArraysds.getString(0).equals("")) {
                        if (choiceArraysds.length() == 1 && choiceArraysds.get(0).equals("")) {
                            Msg msg = new Msg();
                            msg.setType(Msg.CHAT_SOS_TEXT);
                            msg.setContent(answerText);
                            Message m = new Message();
                            m.what = Constant.NEW_MESSAGE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        } else {

                            Msg.ChoiceMsg[] choiceMsgs = null;
                            choiceMsgs = new Msg.ChoiceMsg[choiceArraysds.length()];
                            List<String> stringList = new ArrayList<String>();
                            for (int i = 0; i < choiceArraysds.length(); i++) {
                                stringList.add(choiceArraysds.getString(i));
                            }
                            for (int j = 0; j < stringList.size(); j++) {
                                choiceMsgs[j] = new Msg.ChoiceMsg();
                                choiceMsgs[j].setContent(stringList.get(j));
                            }
                            Msg msg = new Msg();
                            msg.setType(Msg.SIMILAR_CAUSE_TYPE);
                            msg.setChoiceMsgs(choiceMsgs);
                            msg.setResultId(resetId);
                            msg.setSIMILAR_QUESTION_TYPE(3);
                            if (answerText.equals("")) {
                                msg.setContent("为您找到以下相似问题：");
                            } else {
                                msg.setContent(answerText);
                            }
                            Message message = new Message();
                            message.what = Constant.NEW_MESSAGE;
                            message.obj = msg;
                            baseActivityService.getHandler().sendMessage(message);
                        }

                    } else {
                        if (answerText.equals("")) {
                            Message m = new Message();
                            m.what = 2222;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        } else {
                            Msg msg = new Msg();
                            msg.setType(Msg.CHAT_SOS_TEXT);
                            msg.setContent(answerText);
                            Message m = new Message();
                            m.what = Constant.NEW_MESSAGE;
                            m.obj = msg;
                            baseActivityService.getHandler().sendMessage(m);
                            return;
                        }
                    }
                } catch (Exception e) {
                    Message m = new Message();
                    m.what = 2222;
                    baseActivityService.getHandler().sendMessage(m);
                    LawPushApp.setMultipleValue(0);
                }
            }
        });
    }

    /**
     * 用户语音选项识别接口
     * 返回结果为空时，则再调用Cause接口
     *
     * @param choices
     * @param choice
     */
    public void getSelect(final String choices, final String choice) {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = chatService.getSelect(appContext, choices, choice);
                String errorMsg = jsonObject.optString("data");
                JSONArray valueArray = jsonObject.optJSONArray("choice");
                String value = "";
                if (valueArray != null && valueArray.length() != 0) {
                    for (int i = 0; i < valueArray.length(); i++) {
                        try {
                            value = value + "," + valueArray.getString(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (value.equals("")) {
                    try {
                        value = jsonObject.getString("choice");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (value.startsWith(","))
                    value = value.substring(1, value.length());
                if (!errorMsg.equals("") || choice == null || choice.equals("") || value.equals("")) {
//               调取cause接口
                    LawPushApp.setMultipleValue(0);
                    Message m = new Message();
                    m.what = 101;
                    m.obj = choice;
                    baseActivityService.getHandler().sendMessage(m);
                    return;
                }
                //进行匹配选项
                Message m = new Message();
                m.what = 102;
                m.obj = value;
                baseActivityService.getHandler().sendMessage(m);
            }
        });
    }

    public void getInitData() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {

                JSONObject jsonObject = chatService.getInitData(appContext, LawPushApp.getCurrentDeviceId());
                MyLog.e("lawPush", "     jsonObject-----" + jsonObject.toString());
                JSONObject infoObject = jsonObject.optJSONObject("info");
                JSONArray scenesArray = jsonObject.optJSONArray("scenes");
                JSONArray saveArray = jsonObject.optJSONArray("save");
                String errorMsg = jsonObject.optString("data");
                if (!errorMsg.equals("")) {
                    //报错
                    Message m = new Message();
                    m.what = 106;
                    baseActivityService.getHandler().sendMessage(m);
                    return;
                }

                try {
                    if (scenesArray != null && scenesArray.length() != 0 && !scenesArray.getJSONObject(0).toString().equals("")) {
                        List<ScenesBean> scenesBeanList = new ArrayList<>();
                        for (int i = 0; i < scenesArray.length(); i++) {

                            JSONObject subObject = scenesArray.getJSONObject(i);
                            ScenesBean scenesBean = new ScenesBean();
                            scenesBean.setGrade(subObject.optInt("grade"));
                            scenesBean.setName(subObject.optString("name"));
                            scenesBean.setId(subObject.optString("id"));
                            scenesBean.setPriority(subObject.optInt("priority"));
                            scenesBean.setParent(subObject.optString("parent"));

                            MyLog.e("lawPush", "scenesBean----" + scenesBean.toString());

                            JSONArray subSubArray = subObject.optJSONArray("sons");
                            if (subSubArray != null && subSubArray.length() != 0) {
                                //一级案由
                                List<String> stringList = new ArrayList<>();
                                for (int j = 0; j < subSubArray.length(); j++) {
                                    stringList.add(subSubArray.getString(j));
                                }
                                scenesBean.setSonStringList(stringList);
                            }
                            scenesBeanList.add(scenesBean);
                        }

                        LawPushApp.setScenesBeanList(scenesBeanList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (saveArray != null) {
                    JSONArray programArray = saveArray.optJSONObject(0).optJSONArray("程序引导");
                    JSONArray falvfaguiArray = saveArray.optJSONObject(0).optJSONArray("法律法规");
                    JSONArray falvwentiArray = saveArray.optJSONObject(0).optJSONArray("法律问题");
                    JSONArray suibianliaoliao = saveArray.optJSONObject(0).optJSONArray("随便聊聊");
                    JSONArray quanju = saveArray.optJSONObject(0).optJSONArray("全局");

//                    MyLog.e("lawPush","     programArray---"+programArray.toString());
//                    MyLog.e("lawPush","     quanju---"+quanju.toString());
//
                    SharedPreferences.Editor editorR = appContext.getSharedPreferences("saveSP", MODE_PRIVATE).edit();
                    if (programArray != null && programArray.length() != 0 && !programArray.optJSONObject(0).toString().equals("")) {
                        editorR.putString("chengxuyindao", programArray.toString());
                        editorR.commit();
                    }
                    if (falvfaguiArray != null && falvfaguiArray.length() != 0 && !falvfaguiArray.optJSONObject(0).toString().equals("")) {
                        editorR.putString("falvfaguiArray", falvfaguiArray.toString());
                        editorR.commit();
                    }
                    if (falvwentiArray != null && falvwentiArray.length() != 0 && !falvwentiArray.optJSONObject(0).toString().equals("")) {
                        editorR.putString("falvwentiArray", falvwentiArray.toString());
                        editorR.commit();
                    }
                    if (suibianliaoliao != null && suibianliaoliao.length() != 0 && !suibianliaoliao.optJSONObject(0).toString().equals("")) {
                        editorR.putString("suibianliaoliao", suibianliaoliao.toString());
                        editorR.commit();
                    }
                    if (quanju != null && quanju.length() != 0 && !quanju.optJSONObject(0).toString().equals("")) {
                        editorR.putString("quanju", quanju.toString());
                        editorR.commit();
                    }
                }

                if (infoObject != null) {
                    MyLog.e("lawPush", " info----" + infoObject.toString());
                    String eventArea = infoObject.optString("eventArea");
                    String eventDetail = infoObject.optString("eventDetail");
                    int eventId = infoObject.optInt("eventId");
                    int gender = infoObject.optInt("eventArea");
                    int height = infoObject.optInt("height");
                    String hometown = infoObject.optString("hometown");
                    String id = infoObject.optString("id");
                    String name = infoObject.optString("name");
                    String position = infoObject.optString("position");
                    String time = infoObject.optString("time");
                    int weight = infoObject.optInt("weight");
                    int industryId = infoObject.optInt("industryId");

                    SharedPreferences.Editor roboteEditor = appContext.getSharedPreferences("robbotSP", MODE_PRIVATE).edit();
                    roboteEditor.putString("eventArea", eventArea);
                    roboteEditor.putString("eventDetail", eventDetail);
                    roboteEditor.putString("eventId", eventId + "");
                    roboteEditor.putString("gender", gender + "");
                    roboteEditor.putString("height", height + "");
                    roboteEditor.putString("hometown", hometown);
                    roboteEditor.putString("id", id);
                    roboteEditor.putString("name", name);
                    roboteEditor.putString("position", position);
                    roboteEditor.putString("time", time);
                    roboteEditor.putString("weight", weight + "");
                    roboteEditor.putString("industryId", industryId + "");
                    roboteEditor.commit();

                    Message m = new Message();
                    m.what = 107;
                    baseActivityService.getHandler().sendMessage(m);

                }

            }
        });
    }

}
