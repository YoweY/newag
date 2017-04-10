package com.info.aegis.lawpush4android.view.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.entity.law.FlowStep;
import com.info.aegis.lawpush4android.model.bean.entity.law.FlowSubStep;
import com.info.aegis.lawpush4android.model.bean.entity.law.LitigationFlow;
import com.info.aegis.lawpush4android.model.bean.entity.law.UnLitigationFlow;
import com.info.aegis.lawpush4android.utils.HttpHelper;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.List;

/**
 * Created by 申富生 on 2016/12/27.
 * Description :  诉讼 流程的holder
 */

public class LitigationFlowHolder implements View.OnClickListener {

    private View convertView;
    private DataDetailActivity dataDetailActivity;
    private LitigationFlow Flow;
    private UnLitigationFlow unFlow;
    private int width;
    private Button mFlowNo;
    private Button mFlowYes;
    private TextView mUnFlagPeople;
    private TextView mUnFlagPay;
    private TextView mUnFlagArbitration;
    private FrameLayout mFrameLayout;
    private TextView mItemSubFlowTitle;
    private TextView mItemSubFlowName;
    private TextView mItemSubFlowDetail;
    private ListView mItemSubFlowListView;
    private SeekBar mUnFlowSeekBar;
    private ViewPager mUnFlowViewPager;
    private ViewPager mFlowViewPager;
    private ListView mUnFlowListView;
    private myListViewAdapter mListViewAdapter;
    private FrameLayout mFramLayout;
    private TextView mFlowBtn1;
    private TextView mFlowBtn2;
    private TextView mFlowBtn3;
    private TextView mFlowBtn4;
    private TextView mFlowBtn5;
    private TextView mFlowBtn6;
    private LinearLayout mFlowLin1;
    private LinearLayout mFlowLin2;
    private LinearLayout mFlowLin3;
    private LinearLayout mFlowLin4;
    private LinearLayout mFlowLin6;
    private LinearLayout mFlowLin5;
    private ListView mFlowListView;
    private SeekBar mFlowSeekBar;
    private LinearLayout mUnFlowPeopleLinear;
    private LinearLayout mUnFlowPayLinear;
    private LinearLayout mUnFlowArvitrationLinear;
    private ImageView mUnFlowPeopleImg;
    private ImageView mUnFlowPayImg;
    private ImageView mUnFlowArbitrationImg;
    private ImageView mFlowImg1;
    private ImageView mFlowImg2;
    private ImageView mFlowImg3;
    private ImageView mFlowImg4;
    private ImageView mFlowImg5;
    private ImageView mFlowImg6;
    private RelativeLayout mFlowRea1;
    private RelativeLayout mFlowRea2;
    private RelativeLayout mFlowRea3;
    private RelativeLayout mFlowRea4;
    private RelativeLayout mFlowRea5;
    private RelativeLayout mFlowRea6;
    private View mLines;
    private TextView mNoContent;
    private LinearLayout mFlowTitle;

    public LitigationFlowHolder(View view, DataDetailActivity dataDetailActivity, LitigationFlow litigationFlowFlow, UnLitigationFlow unLitigationFlow, int width) {
        this.convertView = view;
        this.dataDetailActivity = dataDetailActivity;
        Flow = litigationFlowFlow;
        unFlow = unLitigationFlow;
        this.width = width;
        initView();
        onListener();
    }


    /**
     * 初始化View
     */
    private void initView() {
        mFlowNo = (Button) convertView.findViewById(R.id.flow_no);
        mFlowYes = (Button) convertView.findViewById(R.id.flow_yes);
        mFrameLayout = (FrameLayout) convertView.findViewById(R.id.litigation_flow_frameLayout);
        mLines = convertView.findViewById(R.id.item_flow_lines);
        mNoContent = (TextView) convertView.findViewById(R.id.item_no_text);
        mFlowTitle = (LinearLayout) convertView.findViewById(R.id.item_flow_title);
        if (Flow==null||unFlow==null){
            mLines.setVisibility(View.GONE);
            mNoContent.setVisibility(View.VISIBLE);
            mFlowTitle.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.GONE);
        }else{
            mLines.setVisibility(View.VISIBLE);
            mNoContent.setVisibility(View.GONE);
            mFlowTitle.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.VISIBLE);
            addView();

        }
    }

    /**
     * 设置监听
     */
    private void onListener() {
        mFlowNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFlowNo.setTextColor(dataDetailActivity.getResources().getColor(R.color.white));
                mFlowNo.setBackgroundColor(dataDetailActivity.getResources().getColor(R.color.button_pressed_true_bg));
                mFlowYes.setTextColor(dataDetailActivity.getResources().getColor(R.color.text_color_38));
                mFlowYes.setBackgroundColor(dataDetailActivity.getResources().getColor(R.color.button_pressed_false_bg));
                addView();
            }
        });
        mFlowYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataDetailActivity.startSpeak("诉讼流程");
                mFlowYes.setTextColor(dataDetailActivity.getResources().getColor(R.color.white));
                mFlowYes.setBackgroundColor(dataDetailActivity.getResources().getColor(R.color.button_pressed_true_bg));
                mFlowNo.setTextColor(dataDetailActivity.getResources().getColor(R.color.text_color_38));
                mFlowNo.setBackgroundColor(dataDetailActivity.getResources().getColor(R.color.button_pressed_false_bg));
                if (Flow.getSteps().size() == 6) {
                    addFlowView();
                } else {
                    addFlowView();
                }
            }
        });
    }

    /**
     * 添加诉讼流程View
     */
    private void addFlowView() {
        mFrameLayout.removeAllViews();
        View contentView = View.inflate(dataDetailActivity, R.layout.item_litigation_flow_sub, null);
        mFlowSeekBar = (SeekBar) contentView.findViewById(R.id.seekBar3);
        contentView.findViewById(R.id.item_flow_seekBar1).setEnabled(false);
        contentView.findViewById(R.id.item_flow_seekBar2).setEnabled(false);
        contentView.findViewById(R.id.item_flow_seekBar3).setEnabled(false);
        contentView.findViewById(R.id.item_flow_seekBar4).setEnabled(false);
        contentView.findViewById(R.id.item_flow_seekBar5).setEnabled(false);
        contentView.findViewById(R.id.item_flow_seekBar6).setEnabled(false);
        mFlowViewPager = (ViewPager) contentView.findViewById(R.id.item_sub_flow_viewpager);
        //添加文字
        mFlowBtn1 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn1);
        mFlowBtn2 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn2);
        mFlowBtn3 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn3);
        mFlowBtn4 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn4);
        mFlowBtn5 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn5);
        mFlowBtn6 = (TextView) contentView.findViewById(R.id.item_sub_flow_btn6);
        //添加父控件
        mFlowLin1 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin1);
        mFlowLin2 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin2);
        mFlowLin3 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin3);
        mFlowLin4 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin4);
        mFlowLin5 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin5);
        mFlowLin6 = (LinearLayout) contentView.findViewById(R.id.item_sub_flow_lin6);
        //添加文字图片
        mFlowImg1 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img1);
        mFlowImg2 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img2);
        mFlowImg3 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img3);
        mFlowImg4 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img4);
        mFlowImg5 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img5);
        mFlowImg6 = (ImageView) contentView.findViewById(R.id.item_sub_flow_img6);
        //添加关闭视图
        mFlowRea1 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea1);
        mFlowRea2 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea2);
        mFlowRea3 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea3);
        mFlowRea4 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea4);
        mFlowRea5 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea5);
        mFlowRea6 = (RelativeLayout) contentView.findViewById(R.id.item_sub_flow_rea6);
        //设置监听
        mFlowLin1.setOnClickListener(this);
        mFlowLin2.setOnClickListener(this);
        mFlowLin3.setOnClickListener(this);
        mFlowLin4.setOnClickListener(this);
        mFlowLin5.setOnClickListener(this);
        mFlowLin6.setOnClickListener(this);
        mFlowRea1.setOnClickListener(this);
        mFlowRea2.setOnClickListener(this);
        mFlowRea3.setOnClickListener(this);
        mFlowRea4.setOnClickListener(this);
        mFlowRea5.setOnClickListener(this);
        mFlowRea6.setOnClickListener(this);
        FlowViewPagerAdapter mFlowAdapter = new FlowViewPagerAdapter(Flow.getSteps());
        mFlowViewPager.setAdapter(mFlowAdapter);
        mFlowViewPager.setOffscreenPageLimit(0);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2050);
        mFlowViewPager.setLayoutParams(lp);
        mFrameLayout.addView(contentView);
        mFlowSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("LitigationFlowHolder", "progress:" + progress);

                if (fromUser) {
                    if (progress < 15) {
                        setBtn1Bg();
                        mFlowViewPager.setCurrentItem(0);
                        dataDetailActivity.startSpeak("起诉");
                    } else if (progress < 34) {
                        setBtn2Bg();
                        mFlowViewPager.setCurrentItem(1);
                        dataDetailActivity.startSpeak("受理");
                    } else if (progress < 50) {
                        setBtn3Bg();
                        mFlowViewPager.setCurrentItem(2);
                        dataDetailActivity.startSpeak("一审");
                    } else if (progress < 68) {
                        setBtn4Bg();
                        mFlowViewPager.setCurrentItem(3);
                        dataDetailActivity.startSpeak("二审");
                    } else if (progress < 85) {
                        setBtn5Bg();
                        mFlowViewPager.setCurrentItem(4);
                        dataDetailActivity.startSpeak("再审");
                    } else {
                        setBtn6Bg();
                        mFlowViewPager.setCurrentItem(5);
                        dataDetailActivity.startSpeak("强制执行");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 15) {
                    seekBar.setProgress(8);
                } else if (seekBar.getProgress() < 34) {
                    seekBar.setProgress(24);
                } else if (seekBar.getProgress() < 50) {
                    seekBar.setProgress(40);
                } else if (seekBar.getProgress() < 68) {
                    seekBar.setProgress(56);
                } else if (seekBar.getProgress() < 85) {
                    seekBar.setProgress(72);
                } else {
                    seekBar.setProgress(90);
                }
            }
        });
        mFlowViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                LinearLayout.LayoutParams lp;
                switch (position) {
                    case 0:
                        setBtn1Bg();
                        mFlowSeekBar.setProgress(8);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2050);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("起诉");
                        break;
                    case 1:
                        setBtn2Bg();
                        mFlowSeekBar.setProgress(24);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 420);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("受理");

                        break;
                    case 2:
                        setBtn3Bg();
                        mFlowSeekBar.setProgress(40);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2800);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("一审");

                        break;
                    case 3:
                        setBtn4Bg();
                        mFlowSeekBar.setProgress(56);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("二审");

                        break;
                    case 4:
                        setBtn5Bg();
                        mFlowSeekBar.setProgress(72);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1350);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("再审");

                        break;
                    case 5:
                        setBtn6Bg();
                        mFlowSeekBar.setProgress(90);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 750);
                        mFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("强制执行");

                        break;

                }
            }
        });
    }

    /**
     * 添加非诉流程View
     */
    private void addView() {
        mFrameLayout.removeAllViews();
        View contentView = View.inflate(dataDetailActivity, R.layout.item_litigation_unflow_sub, null);
        contentView.findViewById(R.id.seekBar1).setEnabled(false);
        contentView.findViewById(R.id.seekBar2).setEnabled(false);
        contentView.findViewById(R.id.seekBar3).setEnabled(false);
        mUnFlowSeekBar = (SeekBar) contentView.findViewById(R.id.seekBar4);
        //添加文字
        mUnFlagPeople = (TextView) contentView.findViewById(R.id.litigation_flag_people);
        mUnFlagPay = (TextView) contentView.findViewById(R.id.litigation_flag_pay);
        mUnFlagArbitration = (TextView) contentView.findViewById(R.id.litigation_flag_arbitration);
        //添加父控件
        mUnFlowPeopleLinear = (LinearLayout) contentView.findViewById(R.id.litigation_flag_people_linear);
        mUnFlowPayLinear = (LinearLayout) contentView.findViewById(R.id.litigation_flag_pay_linear);
        mUnFlowArvitrationLinear = (LinearLayout) contentView.findViewById(R.id.litigation_flag_arbitration_linear);
        //添加数字图片
        mUnFlowPeopleImg = (ImageView) contentView.findViewById(R.id.litigation_flag_people_img);
        mUnFlowPayImg = (ImageView) contentView.findViewById(R.id.litigation_flag_pay_img);
        mUnFlowArbitrationImg = (ImageView) contentView.findViewById(R.id.litigation_flag_arbitration_img);
        //添加ViewPager
        mUnFlowViewPager = (ViewPager) contentView.findViewById(R.id.item_sub_flow_viewpager);
        mUnFlowPeopleLinear.setOnClickListener(this);
        mUnFlowPayLinear.setOnClickListener(this);
        mUnFlowArvitrationLinear.setOnClickListener(this);
        UnFlowViewPagerAdapter adapter = new UnFlowViewPagerAdapter(unFlow.getSteps());
        mUnFlowViewPager.setAdapter(adapter);
        //设置默认打开
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 320);
        mUnFlowViewPager.setLayoutParams(lp);
        mFrameLayout.addView(contentView);



        mUnFlowSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
                if (fromUser) {
                    Log.d("LitigationProcedureHold", "progress:" + progress);
                    if (progress < 35) {
                        setPeopleBg();
                        mUnFlowViewPager.setCurrentItem(0);
                        dataDetailActivity.startSpeak("人民调解");

                    } else if (progress < 70) {
                        setPayBg();
                        mUnFlowViewPager.setCurrentItem(1);
                        dataDetailActivity.startSpeak("支付令");

                    } else {
                        setArbBg();
                        mUnFlowViewPager.setCurrentItem(2);
                        dataDetailActivity.startSpeak("申请仲裁");

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 35) {
                    seekBar.setProgress(15);
                } else if (seekBar.getProgress() < 70) {
                    seekBar.setProgress(50);
                } else {
                    seekBar.setProgress(85);
                }
            }
        });

        mUnFlowViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout.LayoutParams lp;
                switch (position) {
                    case 0:
                        setPeopleBg();
                        mUnFlowSeekBar.setProgress(15);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 320);
                        mUnFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("人民调解");

                        break;
                    case 1:
                        setPayBg();
                        mUnFlowSeekBar.setProgress(50);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1500);
                        mUnFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("支付令");

                        break;
                    case 2:
                        setArbBg();
                        mUnFlowSeekBar.setProgress(85);
                        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3350);
                        mUnFlowViewPager.setLayoutParams(lp);
                        dataDetailActivity.startSpeak("申请仲裁");

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class FlowViewPagerAdapter extends PagerAdapter {

        private List<FlowStep> steps;
        private FlowListViewAdapter mFlowAdapter;

        public FlowViewPagerAdapter(List<FlowStep> steps) {

            this.steps = steps;
        }

        @Override
        public int getCount() {
            return steps.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = View.inflate(dataDetailActivity, R.layout.item_sub_flow_pager, null);
            mFlowListView = (ListView) view.findViewById(R.id.item_sub_flow_listView);
            mFlowAdapter = new FlowListViewAdapter(Flow.getSteps().get(position), position);
            mFlowListView.setAdapter(mFlowAdapter);
            fixListViewHeight(mFlowListView);
            container.addView(view);
            return view;
        }
    }

    class FlowListViewAdapter extends BaseAdapter {

        private FlowStep flowStep;
        private int pager;

        public FlowListViewAdapter(FlowStep flowStep, int position) {

            this.flowStep = flowStep;
            pager = position;
        }

        @Override
        public int getCount() {

            switch (pager) {
                case 3:
                case 4:
                case 5:
                    return 1;
            }
            return flowStep.getSubSteps().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FlowSubHolder flowSubHolder = null;
            FlowTwoHolder twoHolder = null;
            FlowRetrialHolder retrialHolder = null;
            FlowExecutionHolder execuHolder = null;
            Log.d("FlowListViewAdapter", "position:" + position);
            if (convertView == null) {
                switch (pager) {
                    case 0:
                    case 1:
                    case 2:
                        convertView = View.inflate(dataDetailActivity, R.layout.item_sub_flow_listview, null);
                        flowSubHolder = new FlowSubHolder(convertView, flowStep.getSubSteps(), position);
                        convertView.setTag(flowSubHolder);
                        break;
                    case 3:
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_two, null);
                        twoHolder = new FlowTwoHolder(convertView, position);
                        convertView.setTag(twoHolder);
                        break;
                    case 4:
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_retrial, null);
                        retrialHolder = new FlowRetrialHolder(convertView, position);
                        convertView.setTag(retrialHolder);
                        break;
                    case 5:
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_execution, null);
                        execuHolder = new FlowExecutionHolder(convertView, position);
                        convertView.setTag(execuHolder);
                        break;
                }
            } else {
                switch (pager) {
                    case 0:
                    case 1:
                    case 2:
                        flowSubHolder = (FlowSubHolder) convertView.getTag();
                        break;
                    case 3:
                        twoHolder = (FlowTwoHolder) convertView.getTag();
                        break;
                    case 4:
                        retrialHolder = (FlowRetrialHolder) convertView.getTag();
                        break;
                    case 5:
                        execuHolder = (FlowExecutionHolder) convertView.getTag();
                        break;
                }
            }
            return convertView;
        }
    }

    class FlowSubHolder {

        public TextView mTitle;
        public TextView mName;
        public TextView mDetail;
        private List<FlowSubStep> flowSubStep;
        private int position;

        public FlowSubHolder(View convertView, List<FlowSubStep> flowSubStep, int position) {
            this.mTitle = (TextView) convertView.findViewById(R.id.item_sub_flow_title);
            this.mName = (TextView) convertView.findViewById(R.id.item_sub_flow_name);
            this.mDetail = (TextView) convertView.findViewById(R.id.item_sub_flow_detail);
            this.flowSubStep = flowSubStep;
            this.position = position;
            refreshView();
        }

        private void refreshView() {
            mTitle.setText(flowSubStep.get(position).getTitle());
            mDetail.setText(flowSubStep.get(position).getDetail());

            if (flowSubStep.get(position).getTemplates().size() > 0) {
                mName.setText("[" + flowSubStep.get(position).getTemplates().get(0).getName() + "]");
                final String URL=flowSubStep.get(position).getTemplates().get(0).getUrl();
                mName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread() {
                            @Override
                            public void run() {
                                final Response<Bitmap> response = getBitmapResponse(URL);
                                mName.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCodeWindow(mName, response.get());
                                    }
                                });
                            }
                        }.start();


                    }
                });
            } else {
                mName.setVisibility(View.GONE);
            }



        }
    }

    class FlowTwoHolder {

        public FlowTwoHolder(View convertView, int position) {
            LinearLayout mLinear = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
            if (position == 0) {
                mLinear.setVisibility(View.VISIBLE);
            } else {
                mLinear.setVisibility(View.GONE);
            }
        }
    }

    class FlowRetrialHolder {

        public FlowRetrialHolder(View convertView, int position) {
            LinearLayout mLinear = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
            final TextView mName= (TextView) convertView.findViewById(R.id.item_flow_template);
            if (position == 0) {
                mLinear.setVisibility(View.VISIBLE);
            } else {
                mLinear.setVisibility(View.GONE);
            }
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.RETRAL);
                            mName.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
        }
    }

    class FlowExecutionHolder {

        public FlowExecutionHolder(View convertView, int position) {
            LinearLayout mLinear = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
     final TextView mName= (TextView) convertView.findViewById(R.id.item_flow_template1);
     final TextView mName2= (TextView) convertView.findViewById(R.id.item_flow_template2);
            if (position == 0) {
                mLinear.setVisibility(View.VISIBLE);
            } else {
                mLinear.setVisibility(View.GONE);
            }
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION7);
                            mName.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.EXECUTION);
                            mName2.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName2, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
        }
    }


    class UnFlowViewPagerAdapter extends PagerAdapter {


        private List<FlowStep> steps;

        public UnFlowViewPagerAdapter(List<FlowStep> steps) {

            this.steps = steps;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(dataDetailActivity, R.layout.item_sub_flow_pager, null);
            mUnFlowListView = (ListView) view.findViewById(R.id.item_sub_flow_listView);
            mListViewAdapter = new myListViewAdapter(steps, position);
            mUnFlowListView.setAdapter(mListViewAdapter);
            fixListViewHeight(mUnFlowListView);
            container.addView(view);

            return view;
        }
    }

    @Override
    public void onClick(View v) {
        LinearLayout.LayoutParams lp;
        switch (v.getId()) {
            case R.id.litigation_flag_people_linear:
                Toast.makeText(dataDetailActivity, "人民调解", Toast.LENGTH_SHORT).show();
                setPeopleBg();
                mUnFlowSeekBar.setProgress(15);
                mUnFlowViewPager.setCurrentItem(0);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 320);
                mUnFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("人民调解");
                break;
            case R.id.litigation_flag_pay_linear:
                Toast.makeText(dataDetailActivity, "支付令", Toast.LENGTH_SHORT).show();
                setPayBg();
                mUnFlowSeekBar.setProgress(50);
                mUnFlowViewPager.setCurrentItem(1);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1500);
                mUnFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("支付令");
                break;
            case R.id.litigation_flag_arbitration_linear:
                Toast.makeText(dataDetailActivity, "申请仲裁", Toast.LENGTH_SHORT).show();
                setArbBg();
                mUnFlowSeekBar.setProgress(85);
                mUnFlowViewPager.setCurrentItem(2);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3350);
                mUnFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("申请仲裁");
                break;
            case R.id.item_sub_flow_rea1:
            case R.id.item_sub_flow_lin1:
                setBtn1Bg();
                mFlowSeekBar.setProgress(8);
                mFlowViewPager.setCurrentItem(0);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2050);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("起诉");
                break;
            case R.id.item_sub_flow_rea2:
            case R.id.item_sub_flow_lin2:
                setBtn2Bg();
                mFlowSeekBar.setProgress(24);
                mFlowViewPager.setCurrentItem(1);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 420);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("受理");
                break;
            case R.id.item_sub_flow_rea3:
            case R.id.item_sub_flow_lin3:
                setBtn3Bg();
                mFlowSeekBar.setProgress(40);
                mFlowViewPager.setCurrentItem(2);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2800);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("一审");
                break;
            case R.id.item_sub_flow_rea4:
            case R.id.item_sub_flow_lin4:
                setBtn4Bg();
                mFlowSeekBar.setProgress(56);
                mFlowViewPager.setCurrentItem(3);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("二审");
                break;
            case R.id.item_sub_flow_rea5:
            case R.id.item_sub_flow_lin5:
                setBtn5Bg();
                mFlowSeekBar.setProgress(72);
                mFlowViewPager.setCurrentItem(4);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1350);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("再审");
                break;
            case R.id.item_sub_flow_rea6:
            case R.id.item_sub_flow_lin6:
                setBtn6Bg();
                mFlowViewPager.setCurrentItem(5);
                mFlowSeekBar.setProgress(90);
                lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 750);
                mFlowViewPager.setLayoutParams(lp);
                dataDetailActivity.startSpeak("强制执行");
                break;


        }


    }

    class myListViewAdapter extends BaseAdapter {


        private List<FlowStep> steps;
        private int current;

        public myListViewAdapter(List<FlowStep> steps, int position) {

            this.steps = steps;
            this.current = position;
        }

        @Override
        public int getCount() {
//            if (steps.get(current).getSubSteps() == null) {
//                return steps.get(current).getTemplates().size();
//            } else if (steps.get(current).getTemplates() == null) {
//                return steps.get(current).getSubSteps().size();
//            } else {
//                return steps.get(current).getSubSteps().size() + steps.get(current).getTemplates().size();
//            }
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PeopleHolder peopleHolder = null;
            PayHolder payHolder = null;
            AriHolder ariHolder = null;
            if (convertView == null) {
                switch (current) {
                    case 0:
                        //人民调解页面
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_people, null);
                        peopleHolder = new PeopleHolder(convertView, position);
                        convertView.setTag(peopleHolder);
                        break;
                    case 1:
                        //支付令页面
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_pay, null);
                        payHolder = new PayHolder(convertView, current, position);
                        convertView.setTag(payHolder);
                        break;
                    case 2:
                        // 申请仲裁页面
                        convertView = View.inflate(dataDetailActivity, R.layout.item_litigation_flag_arbitration, null);
                        ariHolder = new AriHolder(convertView, steps, current, position);
                        convertView.setTag(ariHolder);
                        break;
                }
            } else {
                switch (current) {
                    case 0:
                        //人民调解页面
                        peopleHolder = (PeopleHolder) convertView.getTag();
                        break;
                    case 1:
                        //支付令页面
                        payHolder = (PayHolder) convertView.getTag();
                        break;
                    case 2:
                        // 申请仲裁页面
                        ariHolder = (AriHolder) convertView.getTag();
                        break;
                }
            }


            return convertView;
        }
    }

    class PeopleHolder {

        public PeopleHolder(View convertView, int position) {
            LinearLayout mLinearLayout
                    = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
            final TextView mName = (TextView) convertView.findViewById(R.id.item_sub_flow_name);
            if (position == 0) {
                mLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mLinearLayout.setVisibility(View.GONE);
            }


            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.PEOPLE);
                            mName.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });

        }
    }

    class PayHolder {

        public PayHolder(View convertView, int current, int position) {
            LinearLayout mLinearLayout
                    = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
            final TextView mName = (TextView) convertView.findViewById(R.id.item_flow_template);
            if (position == 0) {
                mLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mLinearLayout.setVisibility(View.GONE);
            }
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.PAY);
                            mName.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
        }
    }

    class AriHolder {

        public AriHolder(View convertView, List<FlowStep> steps, int current, int position) {

            LinearLayout mLinearLayout
                    = (LinearLayout) convertView.findViewById(R.id.item_sub_flow_linearlayout);
            final TextView mName = (TextView) convertView.findViewById(R.id.item_flow_template);
            final TextView mName1 = (TextView) convertView.findViewById(R.id.item_flow_template1);
            final TextView mName2 = (TextView) convertView.findViewById(R.id.item_flow_template2);
            final TextView mName3 = (TextView) convertView.findViewById(R.id.item_flow_template3);
            final TextView mName4 = (TextView) convertView.findViewById(R.id.item_flow_template4);
            final TextView mName5 = (TextView) convertView.findViewById(R.id.item_flow_template5);
            final TextView mName6 = (TextView) convertView.findViewById(R.id.item_flow_template6);
            if (position == 0) {
                mLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mLinearLayout.setVisibility(View.GONE);
            }
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION1);
                            mName.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION2);
                            mName1.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName1, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION3);
                            mName2.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName2, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION4);
                            mName3.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName3, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION5);
                            mName4.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName4, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION6);
                            mName5.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName5, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
            mName6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread() {
                        @Override
                        public void run() {
                            final Response<Bitmap> response = getBitmapResponse(HttpHelper.ARBITRATION7);
                            mName6.post(new Runnable() {
                                @Override
                                public void run() {
                                    showCodeWindow(mName6, response.get());
                                }
                            });
                        }
                    }.start();


                }
            });
        }

    }


    private void setArbBg() {
        mUnFlagPeople.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowPeopleLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowPeopleImg.setImageResource(R.mipmap.one);
        mUnFlagPay.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowPayLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowPayImg.setImageResource(R.mipmap.two);
        mUnFlagArbitration.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_bg));
        mUnFlowArvitrationLinear.setBackgroundResource(R.drawable.open);
        mUnFlowArbitrationImg.setImageResource(R.mipmap.three_b);
    }

    private void setPayBg() {
        mUnFlagPeople.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowPeopleLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowPeopleImg.setImageResource(R.mipmap.one);
        mUnFlagPay.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_bg));
        mUnFlowPayLinear.setBackgroundResource(R.drawable.open);
        mUnFlowPayImg.setImageResource(R.mipmap.two_b);
        mUnFlagArbitration.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowArvitrationLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowArbitrationImg.setImageResource(R.mipmap.three);
    }

    private void setPeopleBg() {
        mUnFlagPeople.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_bg));
        mUnFlowPeopleLinear.setBackgroundResource(R.drawable.open);
        mUnFlowPeopleImg.setImageResource(R.mipmap.one_b);
        mUnFlagPay.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowPayLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowPayImg.setImageResource(R.mipmap.two);
        mUnFlagArbitration.setTextColor(dataDetailActivity.getResources().getColor(R.color.flow_item_flag_text_normal));
        mUnFlowArvitrationLinear.setBackgroundResource(R.drawable.flow_close);
        mUnFlowArbitrationImg.setImageResource(R.mipmap.three);
    }

    private void setBtn1Bg() {

        mFlowRea1.setVisibility(View.INVISIBLE);
        mFlowRea2.setVisibility(View.VISIBLE);
        mFlowRea3.setVisibility(View.VISIBLE);
        mFlowRea4.setVisibility(View.VISIBLE);
        mFlowRea5.setVisibility(View.VISIBLE);
        mFlowRea6.setVisibility(View.VISIBLE);
        mFlowLin1.setVisibility(View.VISIBLE);
        mFlowLin2.setVisibility(View.INVISIBLE);
        mFlowLin3.setVisibility(View.INVISIBLE);
        mFlowLin4.setVisibility(View.INVISIBLE);
        mFlowLin5.setVisibility(View.INVISIBLE);
        mFlowLin6.setVisibility(View.INVISIBLE);
    }

    private void setBtn2Bg() {

        mFlowRea1.setVisibility(View.VISIBLE);
        mFlowRea2.setVisibility(View.INVISIBLE);
        mFlowRea3.setVisibility(View.VISIBLE);
        mFlowRea4.setVisibility(View.VISIBLE);
        mFlowRea5.setVisibility(View.VISIBLE);
        mFlowRea6.setVisibility(View.VISIBLE);
        mFlowLin1.setVisibility(View.INVISIBLE);
        mFlowLin2.setVisibility(View.VISIBLE);
        mFlowLin3.setVisibility(View.INVISIBLE);
        mFlowLin4.setVisibility(View.INVISIBLE);
        mFlowLin5.setVisibility(View.INVISIBLE);
        mFlowLin6.setVisibility(View.INVISIBLE);
    }

    private void setBtn3Bg() {

        mFlowRea1.setVisibility(View.VISIBLE);
        mFlowRea2.setVisibility(View.VISIBLE);
        mFlowRea3.setVisibility(View.INVISIBLE);
        mFlowRea4.setVisibility(View.VISIBLE);
        mFlowRea5.setVisibility(View.VISIBLE);
        mFlowRea6.setVisibility(View.VISIBLE);
        mFlowLin1.setVisibility(View.INVISIBLE);
        mFlowLin2.setVisibility(View.INVISIBLE);
        mFlowLin3.setVisibility(View.VISIBLE);
        mFlowLin4.setVisibility(View.INVISIBLE);
        mFlowLin5.setVisibility(View.INVISIBLE);
        mFlowLin6.setVisibility(View.INVISIBLE);
    }

    private void setBtn4Bg() {

        mFlowRea1.setVisibility(View.VISIBLE);
        mFlowRea2.setVisibility(View.VISIBLE);
        mFlowRea3.setVisibility(View.VISIBLE);
        mFlowRea4.setVisibility(View.INVISIBLE);
        mFlowRea5.setVisibility(View.VISIBLE);
        mFlowRea6.setVisibility(View.VISIBLE);
        mFlowLin1.setVisibility(View.INVISIBLE);
        mFlowLin2.setVisibility(View.INVISIBLE);
        mFlowLin3.setVisibility(View.INVISIBLE);
        mFlowLin4.setVisibility(View.VISIBLE);
        mFlowLin5.setVisibility(View.INVISIBLE);
        mFlowLin6.setVisibility(View.INVISIBLE);
    }

    private void setBtn5Bg() {

        mFlowRea1.setVisibility(View.VISIBLE);
        mFlowRea2.setVisibility(View.VISIBLE);
        mFlowRea3.setVisibility(View.VISIBLE);
        mFlowRea4.setVisibility(View.VISIBLE);
        mFlowRea5.setVisibility(View.INVISIBLE);
        mFlowRea6.setVisibility(View.VISIBLE);
        mFlowLin1.setVisibility(View.INVISIBLE);
        mFlowLin2.setVisibility(View.INVISIBLE);
        mFlowLin3.setVisibility(View.INVISIBLE);
        mFlowLin4.setVisibility(View.INVISIBLE);
        mFlowLin5.setVisibility(View.VISIBLE);
        mFlowLin6.setVisibility(View.INVISIBLE);
    }

    private void setBtn6Bg() {

        mFlowRea1.setVisibility(View.VISIBLE);
        mFlowRea2.setVisibility(View.VISIBLE);
        mFlowRea3.setVisibility(View.VISIBLE);
        mFlowRea4.setVisibility(View.VISIBLE);
        mFlowRea5.setVisibility(View.VISIBLE);
        mFlowRea6.setVisibility(View.INVISIBLE);
        mFlowLin1.setVisibility(View.INVISIBLE);
        mFlowLin2.setVisibility(View.INVISIBLE);
        mFlowLin3.setVisibility(View.INVISIBLE);
        mFlowLin4.setVisibility(View.INVISIBLE);
        mFlowLin5.setVisibility(View.INVISIBLE);
        mFlowLin6.setVisibility(View.VISIBLE);

    }

    /**
     * 测量 listView 子控件的高度
     *
     * @param listView
     */
    public void fixListViewHeight(ListView listView) {

        // 如果没有设置数据适配器，则ListView没有子项，返回。
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
// TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
        params.height = (int) (((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))) * 3);

        //这是正常的适配
        //     params.height = ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));

        //   params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))*1.5);
        listView.setLayoutParams(params);
    }


    /**
     * 网络请求生成二维码BITMAP
     *
     * @param url
     * @return
     */
    private Response<Bitmap> getBitmapResponse(String url) {
        Request<Bitmap> request = NoHttp.createImageRequest("http://180.96.11.69:8500/api/tools/qr_code?" +
                "url=" + HttpHelper.BASEURL + url, RequestMethod.GET);
        return NoHttp.startRequestSync(request);
    }


    /**
     * 二维码对话框
     *
     * @param parent
     * @param bt
     */
    private void showCodeWindow(View parent, Bitmap bt) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(dataDetailActivity);
        View windowView = mLayoutInflater.inflate(R.layout.two_code_window, null);
        ImageView mImg = (ImageView) windowView.findViewById(R.id.item_two_code);

        PopupWindow mImgWindow = new PopupWindow(windowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mImg.setImageBitmap(bt);
        ColorDrawable cd = new ColorDrawable(0x000000);
        mImgWindow.setBackgroundDrawable(cd);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = ((DataDetailActivity) dataDetailActivity).getWindow().getAttributes();
        lp.alpha = 0.6f;
        ((DataDetailActivity) dataDetailActivity).getWindow().setAttributes(lp);
        ((DataDetailActivity) dataDetailActivity).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mImgWindow.setOutsideTouchable(true);
        mImgWindow.setFocusable(true);
        mImgWindow.showAtLocation((View) parent.getParent(), Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);

        mImgWindow.update();
        mImgWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((DataDetailActivity) dataDetailActivity).getWindow().getAttributes();
                lp.alpha = 1f;
                ((DataDetailActivity) dataDetailActivity).getWindow().setAttributes(lp);
                ((DataDetailActivity) dataDetailActivity).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });
    }

}
