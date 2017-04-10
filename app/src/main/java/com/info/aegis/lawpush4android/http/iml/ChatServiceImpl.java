package com.info.aegis.lawpush4android.http.iml;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.http.ChatService;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.utils.MyLog2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/12/12.
 */
public class ChatServiceImpl implements ChatService {

    String post( String url, String json) throws IOException {
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        formEncodingBuilder.add("json", json);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).connectTimeout(15, TimeUnit.SECONDS).build();
        RequestBody formBody = formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        MyLog2.e("lawPush","        服务端返回内容------"+result);
        return result;
    }

    @Override
    public JSONObject getCause(Context context, String content) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("content", content);
            js.put("sessionId", String.valueOf(System.currentTimeMillis()));
            js.put("type",LawPushApp.getChatTypt());

            String industrid = context.getSharedPreferences("robbotSP", MODE_PRIVATE).getString("industryId","7");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(Integer.parseInt(industrid));
//            js.put("industry_ids",jsonArray);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "cause", js.toString())).getJSONObject("data");

        } catch (Exception e) {
            try {
                result = new JSONObject("{\"data\": {\"msg\": \"error\" }}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getHuman(Context context,  String caseCauseId) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("qid", context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", ""));
            js.put("title",context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("content", "") );
            js.put("case_cause_id", caseCauseId);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "human", js.toString())).getJSONObject("data");
            SharedPreferences.Editor editor = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).edit();
            editor.putString("caseCauseId", caseCauseId);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getFather(Context context, String caseCauseId) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("content", context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("content", ""));
            js.put("caseCauseId", caseCauseId);
            String qid = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", "");
            if (!qid.equals(""))
                js.put("qid", qid);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "father", js.toString())).getJSONObject("data");
            SharedPreferences.Editor editor = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).edit();
            editor.putString("caseCauseId", caseCauseId);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getSon(Context context, String tag) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            String qid = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", "");
            String content = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("content", "");
            if (!qid.equals(""))
                js.put("qid", qid);
            if (!content.equals(""))
                js.put("content", content);
            js.put("tag", tag);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "son", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getTag(Context context) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            String qid = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", "");
            if (!qid.equals(""))
                js.put("qid", qid);
            js.put("tags", context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("tagChosen", ""));
            MyLog.e("lawPUSH","     GETtag---"+js.toString());
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "tag", js.toString())).getJSONObject("data");
            MyLog.e("lawPUSH","     GETtag-result--"+result.toString());
            //结束，清空数据
            SharedPreferences.Editor editor = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).edit();
            editor.putString("tagChosen", "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param context
     * @param content
     * @param choiceContent
     * @param type
     * @return
     */
    @Override
    public JSONObject getAnalyse(Context context, String content, String choiceContent, Integer type) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("content", content);
            js.put("options", choiceContent);
            js.put("type", type);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "analyse", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getReport(Context context) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("qid", context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", ""));
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "report", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getExample(Context context, String content) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("content", content);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "example", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getDetail(Context context, String id, Integer type) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            js.put("id", id);
            js.put("type", type);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "detail", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getMultiple(Context context,  String rld, String choice) {
        JSONObject result = null;
        JSONObject js = new JSONObject();
        try {
            String qid = context.getSharedPreferences(LawPushApp.USER, MODE_PRIVATE).getString("qid", "");
            js.put("qid", qid);
            js.put("relate_id", rld);
            js.put("choice",choice);
            result = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "multi", js.toString())).getJSONObject("data");
        } catch (Exception e) {
            MyLog.e("lawPush","getMultiple --- error");
            try {
                result = new JSONObject("{\"data\": {\"msg\": \"error\" }}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public JSONObject getSelect(Context context, String choices, String choice) {
        JSONObject response = null;
        JSONObject request = new JSONObject();
        try {
            request.put("choices", choices);
            request.put("choice",choice);
            response = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "select", request.toString())).getJSONObject("data");
        } catch (Exception e) {
            MyLog.e("lawPush","getMultiple --- error");
            try {
                response = new JSONObject("{\"data\": {\"msg\": \"error\" }}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public JSONObject getInitData(Context context, String robbotId) {
        JSONObject response = null;
        JSONObject request = new JSONObject();
        try {
            request.put("robot_id", robbotId);
            request.put("api_key", "aegis");
            response = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "init", request.toString())).getJSONObject("data");
        } catch (Exception e) {
            MyLog.e("lawPush","getMultiple --- error");
            try {
                response = new JSONObject("{\"data\": {\"msg\": \"error\" }}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public JSONObject getSceneQuestion(Context context, String sceneId) {
        JSONObject response = null;
        JSONObject request = new JSONObject();
        try {
            request.put("scene_id", sceneId);
            response = new JSONObject(post(context.getResources().getString(R.string.baseUrl) + "scene_question", request.toString())).getJSONObject("data");
        } catch (Exception e) {
            MyLog.e("lawPush","getMultiple --- error");
            try {
                response = new JSONObject("{\"data\": {\"msg\": \"error\" }}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return response;
    }
}
