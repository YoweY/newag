package com.info.aegis.lawpush4android.view.handler;

import android.app.Activity;
import android.os.Message;

import com.info.aegis.lawpush4android.model.bean.entity.law.DataDetail;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;

/**
 * Created by mcs on 2016/12/29.
 */

public class DataDetailHandler extends BaseHandler {
    public DataDetailHandler(Activity activity) {
        super(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        DataDetailActivity context = (DataDetailActivity) reference.get();
        switch (msg.what) {
            case DataDetailActivity.DATA:
                final DataDetail dataDetail = (DataDetail) msg.obj;
                context.setData(dataDetail);
                break;
        }
    }
}
