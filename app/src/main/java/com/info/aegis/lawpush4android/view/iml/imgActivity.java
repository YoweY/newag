package com.info.aegis.lawpush4android.view.iml;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.HttpHelper;
import com.info.aegis.lawpush4android.view.detail.ImageDetailFragment;

import java.util.ArrayList;



/**
 * Created by SFS on 2017/3/24.
 * Description :
 */

public class imgActivity extends BaseActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";


    private ViewPager mPager;
    private int pagerPosition;
    private ImageView mImageView;
    private String url;
    private ArrayList<String> urls;
    private int position;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        position = getIntent().getIntExtra("position", 0);
        Log.d("imgActivity", "url:" + urls);

        initView();
    }

    private void initView() {


        setContentView(R.layout.img_dialog);
        mImageView = (ImageView) findViewById(R.id.Dialog_img);
        Log.d("imgActivity", "position:" + position);
        url = urls.get(position);
        loadImage(HttpHelper.BASEIMGURL + url, mImageView);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        /**
//         * 判断滑动距离 进行滑动或跳转
//         */
//        mImageView.setOnTouchListener(new View.OnTouchListener() {
//
//            int startX = 0;
//            int startY = 0;
//            int endX = 0;
//            int endY = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = (int) event.getRawX();
//                        startY = (int) event.getRawY();
//                        stopSpeak();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        endX = (int) event.getRawX();
//                        endY = (int) event.getRawY();
//                        int dX = endX - startX;
//                        int dY = endY - startY;
//
//                        if (Math.abs(dX) < 100 && Math.abs(dY) < 100) {
//                            finish();
//                        }
//                        break;
//
//                    default:
//                        break;
//                }
//
//                return false;
//            }
//        });
    }

//    private void initView() {
//        setContentView(R.layout.image_detail_pager);
//        mPager = (ViewPager) findViewById(R.id.pager);
//        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
//        mPager.setAdapter(mAdapter);
//        mPager.setCurrentItem(position);
//    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImageDetailFragment.newInstance(url);
        }
    }
}