package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.info.aegis.lawpush4android.R;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * Created by 申富生 on 2016/12/19.
 * Description : 二维码的holder
 */

public class ConsultHolder {
    public View convertView;
    private Context context;

    public ConsultHolder(View view, Context context, final String qid) {
        this.convertView = view;
        this.context = context;

        final ImageView mImg = (ImageView) convertView.findViewById(R.id.item_image);

        Log.d("ConsultHolder", qid);
        new Thread() {
            @Override
            public void run() {

                Request<Bitmap> request = NoHttp.createImageRequest("http://180.96.11.69:8500/api/tools/qr_code?" +
                        "url=https://law.push.aegis-info.com/newDataPage/" + qid, RequestMethod.GET);
                final Response<Bitmap> response = NoHttp.startRequestSync(request);

                mImg.post(new Runnable() {
                    @Override
                    public void run() {
                        mImg.setImageBitmap(response.get());
                    }
                });

            }
        }.start();


    }


}
