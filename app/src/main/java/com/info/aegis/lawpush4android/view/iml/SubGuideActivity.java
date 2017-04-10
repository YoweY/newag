package com.info.aegis.lawpush4android.view.iml;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.presenter.SubGuidePresenter;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.utils.MyLog;
import com.info.aegis.lawpush4android.view.SubGuideActivityService;
import com.info.aegis.lawpush4android.view.widget.Rotate3DAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SFS on 2017/2/24.
 * Description :
 */

public class SubGuideActivity extends BaseActivity implements View.OnClickListener {


    private TextView mOneText1, mOneText2, mOneText3, mOneText4, mOneText5, mOneText6;
    //背景图片的位置
    private ImageView mImgPosition1, mImgPosition2, mImgPosition3, mImgPosition4, mImgPosition5, mImgPosition6;
    //  图片的内容
    private ImageView mIngContent1, mIngContent2, mIngContent3, mIngContent4, mIngContent5, mIngContent6;
    private RelativeLayout mOneLinearLayout;
    private int item;
    private ImageView mGifImg;
    private TextView mText;

//    private String[] Datas1 = {"立案侦查", "强制措施", "批捕&\n决定逮捕", "特别程序", "检查监督", "申诉举报控告"};
//    private String[] Datas3 = {"立案侦查", "强制措施", "批捕&\n决定逮捕", "特别程序", "检查监督", "申诉举报控告"};
//    private String[] Datas4 = {"立案侦查", "强制措施", "批捕&\n决定逮捕", "特别程序", "检查监督", "申诉举报控告"};
    private ObjectAnimator animator;
    private String[] datas = new String[6];
    private int[] Ids = new int[6];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subguide);
        AppManager.getAppManager().addActivity(SubGuideActivity.this);
        item = getIntent().getIntExtra("item", 3);
        //如果item为4，则直接调用father接口，不需要调用cause接口
        String value = "{value:" + getIntent().getStringExtra("Data") + "}";
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < 6; i++) {
                datas[i] = jsonArray.getJSONObject(i).getString("name");
                Ids[i]=jsonArray.optJSONObject(i).optInt("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView(datas);

//        Glide
//                .with(this)
//                .load(R.drawable.jqr)
//                .placeholder(R.drawable.jiqiren)
//                .error(R.drawable.jiqiren)
//                .into(mGifImg);


    }

    @Override
    public void onResume() {
        super.onResume();
        LawPushApp.setCurrntActivity("SubGuideActivity");
        item = getIntent().getIntExtra("item", 3);
        startSpeak("请问您需要咨询哪一方面的问题?");
        //如果item为4，则直接调用father接口，不需要调用cause接口
        String value = "{value:" + getIntent().getStringExtra("Data") + "}";
        try {
            JSONObject jsonObject = new JSONObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < 6; i++) {
                datas[i] = jsonArray.getJSONObject(i).getString("name");
                Ids[i]=jsonArray.optJSONObject(i).optInt("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initView(datas);

    }

    private void initView( String [] ssdds) {

        mGifImg = (ImageView) findViewById(R.id.sub_guide_GifImg);
        mText = (TextView) findViewById(R.id.sub_guide_Text);

        //程序引导的控件
        //文字
        mOneText1 = (TextView) findViewById(R.id.sub_guide_one_text1);
        mOneText2 = (TextView) findViewById(R.id.sub_guide_one_text2);
        mOneText3 = (TextView) findViewById(R.id.sub_guide_one_text3);
        mOneText4 = (TextView) findViewById(R.id.sub_guide_one_text4);
        mOneText5 = (TextView) findViewById(R.id.sub_guide_one_text5);
        mOneText6 = (TextView) findViewById(R.id.sub_guide_one_text6);
        mOneLinearLayout = (RelativeLayout) findViewById(R.id.sub_guide_one_linearLayout);
        //背景
        mImgPosition1 = (ImageView) findViewById(R.id.imgPosition1);
        mImgPosition2 = (ImageView) findViewById(R.id.imgPosition2);
        mImgPosition3 = (ImageView) findViewById(R.id.imgPosition3);
        mImgPosition4 = (ImageView) findViewById(R.id.imgPosition4);
        mImgPosition5 = (ImageView) findViewById(R.id.imgPosition5);
        mImgPosition6 = (ImageView) findViewById(R.id.imgPosition6);
        //图片
        mIngContent1 = (ImageView) findViewById(R.id.mImg_content1);
        mIngContent2 = (ImageView) findViewById(R.id.mImg_content2);
        mIngContent3 = (ImageView) findViewById(R.id.mImg_content3);
        mIngContent4 = (ImageView) findViewById(R.id.mImg_content4);
        mIngContent5 = (ImageView) findViewById(R.id.mImg_content5);
        mIngContent6 = (ImageView) findViewById(R.id.mImg_content6);
        //点击监听

        mImgPosition1.setOnClickListener(this);
        mImgPosition2.setOnClickListener(this);
        mImgPosition3.setOnClickListener(this);
        mImgPosition4.setOnClickListener(this);
        mImgPosition5.setOnClickListener(this);
        mImgPosition6.setOnClickListener(this);

        mIngContent1.setOnClickListener(this);
        mIngContent2.setOnClickListener(this);
        mIngContent3.setOnClickListener(this);
        mIngContent4.setOnClickListener(this);
        mIngContent5.setOnClickListener(this);
        mIngContent6.setOnClickListener(this);

        mOneText1.setOnClickListener(this);
        mOneText2.setOnClickListener(this);
        mOneText3.setOnClickListener(this);
        mOneText4.setOnClickListener(this);
        mOneText5.setOnClickListener(this);
        mOneText6.setOnClickListener(this);

//        //法律 查询的页面
//        mThreeText1 = (TextView) findViewById(R.id.sub_guide_three_text1);
//        mThreeText2 = (TextView) findViewById(R.id.sub_guide_three_text2);
//        mThreeText3 = (TextView) findViewById(R.id.sub_guide_three_text3);
//        mThreeText4 = (TextView) findViewById(R.id.sub_guide_three_text4);
//        mThreeText5 = (TextView) findViewById(R.id.sub_guide_three_text5);
//        mThreeText6 = (TextView) findViewById(R.id.sub_guide_three_text6);
//        mThreeLinearLayout = (LinearLayout) findViewById(R.id.sub_guide_three_linearLayout);
//        mThreeText1.setOnClickListener(this);
//        mThreeText2.setOnClickListener(this);
//        mThreeText3.setOnClickListener(this);
//        mThreeText4.setOnClickListener(this);
//        mThreeText5.setOnClickListener(this);
//        mThreeText6.setOnClickListener(this);
//        //随便聊聊的页面控件
//        mFourText1 = (TextView) findViewById(R.id.sub_guide_four_text1);
//        mFourText2 = (TextView) findViewById(R.id.sub_guide_four_text2);
//        mFourText3 = (TextView) findViewById(R.id.sub_guide_four_text3);
//        mFourText4 = (TextView) findViewById(R.id.sub_guide_four_text4);
//        mFourText5 = (TextView) findViewById(R.id.sub_guide_four_text5);
//        mFourText6 = (TextView) findViewById(R.id.sub_guide_four_text6);
//        mFourLinearLayout = (LinearLayout) findViewById(R.id.sub_guide_four_linearLayout);
//        mFourText1.setOnClickListener(this);
//        mFourText2.setOnClickListener(this);
//        mFourText3.setOnClickListener(this);
//        mFourText4.setOnClickListener(this);
//        mFourText5.setOnClickListener(this);
//        mFourText6.setOnClickListener(this);
        switch (item) {
            case 1:
//                mText.setText("程序引导");
//                mOneText1.setText(ssdds[0]);
//                mOneText2.setText(ssdds[1]);
//                mOneText3.setText(ssdds[2]);
//                mOneText4.setText(ssdds[3]);
//                mOneText5.setText(ssdds[4]);
//                mOneText6.setText(ssdds[5]);
                mText.setText("程序引导");
                mOneText1.setText(ssdds[0]);
                if (ssdds[0].length()>10)
                    mOneText1.setTextSize(40f);
                mOneText2.setText(ssdds[1]);
                if (ssdds[1].length()>10)
                    mOneText2.setTextSize(40f);
                mOneText3.setText(ssdds[2]);
                if (ssdds[2].length()>10)
                    mOneText3.setTextSize(40f);
                    mOneText4.setText(ssdds[3]);
                if (ssdds[3].length()>10)
                    mOneText4.setTextSize(20f);
                    mOneText5.setText(ssdds[4]);
                if (ssdds[4].length()>10)
                    mOneText5.setTextSize(20f);
                mOneText6.setText(ssdds[5]);
                if (ssdds[5].length()>10)
                    mOneText6.setTextSize(25f);
                break;
            case 4:
                mText.setText("法律咨询");
                mOneText1.setText(ssdds[0]);
//                if (ssdds[0].length()>10)
//                    mOneText1.setTextSize(40f);
                mOneText2.setText(ssdds[1]);
//                if (ssdds[1].length()>10)
//                    mOneText2.setTextSize(40f);
                mOneText3.setText(ssdds[2]);
//                if (ssdds[2].length()>10)
//                    mOneText3.setTextSize(40f);
                mOneText4.setText(ssdds[3]);
                mOneText5.setText(ssdds[4]);
                mOneText6.setText(ssdds[5]);

                break;
        }
    }

    @Override
    public void onClick(View v) {

        // 环境类型 1 程序引导 5 法律法规 4 法律问答 3 随便聊聊
        int type = 3;
        int position = 0;
        switch (v.getId()) {
            case R.id.sub_guide_one_text1:
            case R.id.imgPosition1:
            case R.id.mImg_content1:
                type = 1;
                position = 1;
                applyRotation(mImgPosition1, position);
                applyRotation(mIngContent1, position);
                applyRotation(mOneText1, position);

                break;
            case R.id.sub_guide_one_text2:
            case R.id.imgPosition2:
            case R.id.mImg_content2:
                type = 1;
                position = 2;
                applyRotation(mImgPosition2, position);
                applyRotation(mIngContent2, position);
                applyRotation(mOneText2, position);

                break;
            case R.id.sub_guide_one_text3:
            case R.id.imgPosition3:
            case R.id.mImg_content3:
                type = 1;
                position = 3;
                applyRotation(mImgPosition3, position);
                applyRotation(mIngContent3, position);
                applyRotation(mOneText3, position);

                break;
            case R.id.sub_guide_one_text4:
            case R.id.imgPosition4:
            case R.id.mImg_content4:
                type = 1;
                position = 4;
                applyRotation(mImgPosition4, position);
                applyRotation(mIngContent4, position);
                applyRotation(mOneText4, position);

                break;
            case R.id.sub_guide_one_text5:
            case R.id.imgPosition5:
            case R.id.mImg_content5:
                type = 1;
                position = 5;
                applyRotation(mImgPosition5, position);
                applyRotation(mIngContent5, position);
                applyRotation(mOneText5, position);

                break;
            case R.id.sub_guide_one_text6:
            case R.id.imgPosition6:
            case R.id.mImg_content6:
                type = 1;
                position = 6;
                applyRotation(mImgPosition6,position);
                applyRotation(mIngContent6, position);
                applyRotation(mOneText6, position);

                break;
        }


    }

    private void JumpActivity(int position) {

        Intent intent = new Intent(SubGuideActivity.this, ChatActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("position", position);
        intent.putExtra("Data",datas);
        intent.putExtra("Ids",Ids);
        intent.putExtra("question",datas[position-1]);
        startActivity(intent);
        overridePendingTransition(R.anim.in,R.anim.out);
        finish();
    }



    private void applyRotation(View view, int position) {
        // 计算中心点
        final float centerX = view.getWidth() / 2.0f;
        final float centerY = view.getHeight() / 2.0f;

        final Rotate3DAnimation rotation = new Rotate3DAnimation(0, -90,
                centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateInterpolator());
        // 设置监听
        rotation.setAnimationListener(new DisplayNextView(view,position));

        view.startAnimation(rotation);
    }

    /**
     * 翻转动画
     */
    private final class DisplayNextView implements Animation.AnimationListener {

        private View view;
        private int position;

        public DisplayNextView(View view, int position) {

            this.view = view;
            this.position = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        // 动画结束
        public void onAnimationEnd(Animation animation) {
            //  view.post(new SwapViews(view));
            JumpActivity(position);
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {

        private View view;

        public SwapViews(View view) {

            this.view = view;
        }

        public void run() {
            final float centerX = view.getWidth() / 2.0f;
            final float centerY = view.getHeight() / 2.0f;
            Rotate3DAnimation rotation = null;

            view.requestFocus();

            rotation = new Rotate3DAnimation(-90, -180, centerX, centerY, 310.0f,
                    false);
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            // 开始动画
            view.startAnimation(rotation);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppManager.getAppManager().finishActivity(SubGuideActivity.this);
        finish();
    }
}
