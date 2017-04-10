package com.info.aegis.lawpush4android.view.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.http.CallServerNohttp;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 法律知识Fragment
 */
public class  LawInfoFragment extends Fragment {
    private View parentView;
    /**
     * 请求id
     */
    private String id = "";
    private TextView titleText;
    private TextView contentText;
    private String pqContent = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_low_info, container, false);
        id = getArguments().getString("id");
        pqContent = getArguments().getString("pq_content");
        initView();
        initData();
        return parentView;
    }
    private void initView() {
        titleText = (TextView) parentView.findViewById(R.id.id_title);
        contentText = (TextView) parentView.findViewById(R.id.id_contentText);
    }
    private void initData() {
        if (pqContent!= null && !pqContent.equals("")){
            titleText.setText("参考答案         ");
            contentText.setText(pqContent);
        }else{
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id",id);
                jsonObject.put("type",2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Request<JSONObject> request = NoHttp.createJsonObjectRequest(getString(R.string.baseUrl) + "detail", RequestMethod.POST);
            request.add("json",jsonObject.toString());
            CallServerNohttp.getInstance().add(0,request,jsonObjectRestResponse);
        }
    }

    OnResponseListener<JSONObject> jsonObjectRestResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            JSONObject jsonObject = response.get();
            try {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                if (dataJson.isNull("id")) {
                    Toast.makeText(getActivity(), "小法打盹了", Toast.LENGTH_LONG).show();
                } else {
                    titleText.setText(dataJson.getString("title"));
                    contentText.setText("\u3000\u3000"+ dataJson.getString("content"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };
}
