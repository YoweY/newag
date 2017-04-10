package com.info.aegis.lawpush4android.presenter;

import android.os.Message;

import com.info.aegis.lawpush4android.model.bean.entity.law.DataDetail;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.JsonUtils;
import com.info.aegis.lawpush4android.view.DateDetailActivityService;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;

import org.json.JSONObject;

/**
 * Created by 申富生 on 2016/12/21.
 * Description : 数据详情的Presenter
 */

public class DataDetailPresenter extends BasePresenter {

    private DateDetailActivityService dateDetailActivityService;


    public DataDetailPresenter(DateDetailActivityService dateDetailActivityService) {
        super(dateDetailActivityService.getContext());
        this.baseActivityService = dateDetailActivityService;
        init();
    }

    public void getReport() {

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject result = chatService.getReport(appContext);
                    DataDetail dataDetail = JsonUtils.parserDataDetailJson(result.toString());
                    Message msg = new Message();
                    msg.what = DataDetailActivity.DATA;
                    msg.obj = dataDetail;
                    baseActivityService.getHandler().sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
