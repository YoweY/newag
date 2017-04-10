package com.info.aegis.lawpush4android.view.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.HttpHelper;

/**
 * Created by SFS on 2017/3/29.
 * Description :
 */

public class ImageDetailFragment extends Fragment {


        private String mImageUrl;
        private ImageView mImageView;

        public static ImageDetailFragment newInstance(String imageUrl) {
            final ImageDetailFragment f = new ImageDetailFragment();
            final Bundle args = new Bundle();
            args.putString("url", imageUrl);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.img_dialog, container, false);
            mImageView = (ImageView) v.findViewById(R.id.Dialog_img);
//            mAttacher = new PhotoViewAttacher(mImageView);
//            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//                @Override
//                public void onPhotoTap(View arg0, float arg1, float arg2) {
//                    getActivity().finish();
//                }
//            });

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            loadImage(HttpHelper.BASEIMGURL+mImageUrl,mImageView);
        }


    /**
     * 默认宽和高的加载图片
     *
     * @param o  可以是  url 可以是 Drawable  可以是文件
     * @param iv 需要显示的控件
     */
    public void loadImage(Object o, ImageView iv) {
        Glide.with(iv.getContext())   //使得glide更容易使用，因为能接收context，activity，fragment对象
                .load(o)
                .priority(Priority.HIGH)    //设置优先级
                .placeholder(android.R.drawable.stat_sys_download_done)    //加载中显示的图片
                .error(android.R.drawable.stat_notify_error)//加载失败显示的图片
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()    //缩放图像，整个显示在控件，尽可能的填满
                .into(iv);
    }
    }
