package com.info.aegis.lawpush4android.http;

import android.content.Context;

import com.info.aegis.lawpush4android.model.bean.Msg;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

/**
 * Created by mcs on 2016/12/12.
 */
public interface ChatService {
    JSONObject getCause(Context context, String content);

    JSONObject getFather(Context context, String caseCauseId);

    JSONObject getSon(Context context, String tag);

    JSONObject getTag(Context context);

    JSONObject getAnalyse(Context context, String content, String options, Integer type);

    JSONObject      getReport(Context context);

    JSONObject getExample(Context context, String content);

    JSONObject getDetail(Context context, String id, Integer type);

    JSONObject getHuman(Context context,  String cacseCauseIds);

    JSONObject getMultiple(Context context,String rld,String choice);

    JSONObject getSelect(Context context,String choices,String choice);

    JSONObject getInitData(Context context,String robbotId);

    JSONObject getSceneQuestion(Context context, String sceneId);
}
