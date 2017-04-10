package com.info.aegis.lawpush4android.view.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.http.CallServerNohttp;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 指导案例fragment
 */
public class GuidesFragment extends Fragment {

    private View parentView;
    /**
     * 请求id
     */
    private String id = "";

    private TextView titleText;

    private LinearLayout contentLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_guides, container, false);
        id = getArguments().getString("id");
        initView();
        initData();

        return parentView;
    }

    private void initView() {
        titleText = (TextView) parentView.findViewById(R.id.id_title);
        contentLay = (LinearLayout) parentView.findViewById(R.id.id_content);
    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("type", 4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(getString(R.string.baseUrl) + "detail", RequestMethod.POST);
        request.add("json", jsonObject.toString());
        CallServerNohttp.getInstance().add(0, request, jsonObjectOnResponseListener);

    }

    OnResponseListener<JSONObject> jsonObjectOnResponseListener = new OnResponseListener<JSONObject>() {
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
                    JSONArray contentJson = dataJson.getJSONArray("content");
                    if (contentJson.length() != 0) {
                        for (int i = 0; i < contentJson.length(); i++) {
                            JSONObject subContentObject = contentJson.getJSONObject(i);
                            String key = subContentObject.keys().next();
                            View subView = LayoutInflater.from(getActivity()).inflate(R.layout.law_detail_title, null);
                            TextView subTitleText = (TextView) subView.findViewById(R.id.id_title);
                            TextView subContentText = (TextView) subView.findViewById(R.id.id_contentText);
                            subTitleText.setText(key);
                            subContentText.setText(subContentObject.getString(key));
                            contentLay.addView(subView);
                        }

                    } else {
                        Toast.makeText(getActivity(), "小法打盹了", Toast.LENGTH_LONG).show();
                    }
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
