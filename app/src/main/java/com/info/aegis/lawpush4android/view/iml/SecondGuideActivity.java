package com.info.aegis.lawpush4android.view.iml;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.utils.AppManager;
import com.info.aegis.lawpush4android.view.widget.Rotate3DAnimation;

/**
 * Created by SFS on 2017/3/8.
 * Description :
 */

public class SecondGuideActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mImgContent7, mImgPosition1, mImgPosition2, mImgPosition3, mImgContent1, mImgContent2, mImgPosition5, mImgContent5, mImgPosition6, mImgContent6, mImgPosition7, mImgContent3, mImgPosition8, mImgContent8, mImgPosition4, mImgContent4;
    private TextView mImgText1, mSubGuideText, mImgText2, mImgText5, mImgText6, mImgText7, mImgText3, mImgText4, mImgText8;

    private String[] datas = {"调解", "立案", "开庭", "诉讼费", "执行", "上诉", "保全", "审限"};
    private int item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        AppManager.getAppManager().addActivity(SecondGuideActivity.this);
        initView(datas);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LawPushApp.setCurrntActivity("SecondGuideActivity");
        item = getIntent().getIntExtra("item", 3);
        //如果item为4，则直接调用father接口，不需要调用cause接口
        initView(datas);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppManager.getAppManager().finishActivity(SecondGuideActivity.this);
        finish();
    }

    /**
     * 初始化View
     * @param datas
     */
    private void initView(String[] datas) {
        mSubGuideText = (TextView) findViewById(R.id.sub_guide_Text);
        mImgPosition1 = (ImageView) findViewById(R.id.imgPosition1);
        mImgContent1 = (ImageView) findViewById(R.id.mImg_content1);
        mImgText1 = (TextView) findViewById(R.id.img_text1);
        mImgPosition2 = (ImageView) findViewById(R.id.imgPosition2);
        mImgContent2 = (ImageView) findViewById(R.id.mImg_content2);
        mImgText2 = (TextView) findViewById(R.id.img_text2);
        mImgPosition3 = (ImageView) findViewById(R.id.imgPosition3);
        mImgContent3 = (ImageView) findViewById(R.id.mImg_content3);
        mImgText3 = (TextView) findViewById(R.id.img_text3);
        mImgPosition4 = (ImageView) findViewById(R.id.imgPosition4);
        mImgContent4 = (ImageView) findViewById(R.id.mImg_content4);
        mImgText4 = (TextView) findViewById(R.id.img_text4);
        mImgPosition5 = (ImageView) findViewById(R.id.imgPosition5);
        mImgContent5 = (ImageView) findViewById(R.id.mImg_content5);
        mImgText5 = (TextView) findViewById(R.id.img_text5);
        mImgPosition6 = (ImageView) findViewById(R.id.imgPosition6);
        mImgContent6 = (ImageView) findViewById(R.id.mImg_content6);
        mImgText6 = (TextView) findViewById(R.id.img_text6);
        mImgPosition7 = (ImageView) findViewById(R.id.imgPosition7);
        mImgContent7 = (ImageView) findViewById(R.id.mImg_content7);
        mImgText7 = (TextView) findViewById(R.id.img_text7);
        mImgPosition8 = (ImageView) findViewById(R.id.imgPosition8);
        mImgContent8 = (ImageView) findViewById(R.id.mImg_content8);
        mImgText8 = (TextView) findViewById(R.id.img_text8);
        //设置监听
        mImgPosition1.setOnClickListener(this);
        mImgContent1.setOnClickListener(this);
        mImgText1.setOnClickListener(this);
        mImgPosition2.setOnClickListener(this);
        mImgContent2.setOnClickListener(this);
        mImgText2.setOnClickListener(this);
        mImgPosition3.setOnClickListener(this);
        mImgContent3.setOnClickListener(this);
        mImgText3.setOnClickListener(this);
        mImgPosition4.setOnClickListener(this);
        mImgContent4.setOnClickListener(this);
        mImgText4.setOnClickListener(this);
        mImgPosition5.setOnClickListener(this);
        mImgContent5.setOnClickListener(this);
        mImgText5.setOnClickListener(this);
        mImgPosition6.setOnClickListener(this);
        mImgContent6.setOnClickListener(this);
        mImgText6.setOnClickListener(this);
        mImgPosition7.setOnClickListener(this);
        mImgContent7.setOnClickListener(this);
        mImgText7.setOnClickListener(this);
        mImgPosition8.setOnClickListener(this);
        mImgContent8.setOnClickListener(this);
        mImgText8.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {

    }


    @Override
    public void sendToServer(String content) {
        super.sendToServer(content);
        if (content.contains("调解")) {
            JumpActivity(1);
        } else if (content.contains("立案")) {
            JumpActivity(2);
        } else if (content.contains("开庭")) {
            JumpActivity(3);
        } else if (content.contains("诉讼费")) {
            JumpActivity(4);
        } else if (content.contains("执行")) {
            JumpActivity(5);
        } else if (content.contains("上诉")) {
            JumpActivity(6);
        } else if (content.contains("保全")) {
            JumpActivity(7);
        } else if (content.contains("审限")) {
            JumpActivity(8);
        }

    }

    @Override
    public void onClick(View v) {
        int position = -1;
        switch (v.getId()) {
            case R.id.imgPosition1:
            case R.id.mImg_content1:
            case R.id.img_text1:
                position = 1;
                break;
            case R.id.imgPosition2:
            case R.id.mImg_content2:
            case R.id.img_text2:
                position = 2;
                break;
            case R.id.imgPosition3:
            case R.id.mImg_content3:
            case R.id.img_text3:
                position = 3;
                break;
            case R.id.imgPosition4:
            case R.id.mImg_content4:
            case R.id.img_text4:
                position = 4;
                break;
            case R.id.imgPosition5:
            case R.id.mImg_content5:
            case R.id.img_text5:
                position = 5;
                break;
            case R.id.imgPosition6:
            case R.id.mImg_content6:
            case R.id.img_text6:
                position = 6;
                break;
            case R.id.imgPosition7:
            case R.id.mImg_content7:
            case R.id.img_text7:
                position = 7;
                break;
            case R.id.imgPosition8:
            case R.id.mImg_content8:
            case R.id.img_text8:
                position = 8;
                break;
        }
        JumpActivity(position);
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
        rotation.setAnimationListener(new DisplayNextView(view, position));

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

    private void JumpActivity(int position) {

        Intent intent = new Intent(SecondGuideActivity.this, ChatActivity.class);
        intent.putExtra("item", 1);
        intent.putExtra("position", position);
        intent.putExtra("Data", datas);
        //   intent.putExtra("Ids",Ids);
        startActivity(intent);
        overridePendingTransition(R.anim.in, R.anim.out);
        finish();
    }
}
