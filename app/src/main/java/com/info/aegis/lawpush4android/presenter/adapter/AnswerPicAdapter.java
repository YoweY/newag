package com.info.aegis.lawpush4android.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.HttpHelper;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.imgActivity;

import java.util.ArrayList;


/**
 * Created by SFS on 2017/3/29.
 * Description :
 */

class AnswerPicAdapter extends SubBaseAdapter<String> {
    private ArrayList<String> pics;
    private Context ctx;
    private ImageView picView;

    AnswerPicAdapter(ArrayList<String> strings, Context ctx) {
        super(strings, ctx);
        this.pics = strings;
        this.ctx = ctx;
    }

    @Override
    protected View getContentView(final int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.answer_picture_item, null);
            picView = (ImageView) convertView.findViewById(R.id.answer_pic);
        }
        final String picUrl = pics.get(position);
        Log.d("AnswerPicAdapter", picUrl);
        if (ctx instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) ctx;
            if (pics.size() == 1) {
                chatActivity.loadImage(HttpHelper.BASEIMGURL + picUrl, picView, 1080, 866);
            } else if (pics.size() == 2) {
                chatActivity.loadImage(HttpHelper.BASEIMGURL + picUrl, picView, 540, 400);
            } else if (pics.size() == 4) {
                chatActivity.loadImage(HttpHelper.BASEIMGURL + picUrl, picView, 540, 400);
            } else {
                chatActivity.loadImage(HttpHelper.BASEIMGURL + picUrl, picView, 342, 272);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, imgActivity.class);
                intent.putExtra(imgActivity.EXTRA_IMAGE_URLS, pics);
                intent.putExtra("position", position);
                Log.e("AnswerPicAdapter", position + ">>>>>>>>position");

                ctx.startActivity(intent);
            }
        });
        return convertView;
    }

}
