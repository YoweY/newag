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
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 司法观点fragment
 */
public class PointsFragment extends Fragment {
    private View parentView;
    /**
     * 请求id
     */
    private String id = "";
    private TextView titleText;
    private TextView contentText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_points, container, false);
        id = getArguments().getString("id");
        initView();
        initData();
        return parentView;
    }

    private void initView() {
        titleText = (TextView) parentView.findViewById(R.id.id_title);
        contentText = (TextView) parentView.findViewById(R.id.id_contentText);
    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("type",3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(getString(R.string.baseUrl) + "detail", RequestMethod.POST);
        request.add("json",jsonObject.toString());
        CallServerNohttp.getInstance().add(0,request,jsonObjectHttpListener);

    }

    OnResponseListener<JSONObject> jsonObjectHttpListener = new OnResponseListener<JSONObject>() {
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
