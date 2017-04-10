package com.info.aegis.lawpush4android.presenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.presenter.ChatPresenter;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.MyToast;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;
import com.info.aegis.lawpush4android.view.iml.LawDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static com.info.aegis.lawpush4android.LawPushApp.USER;

/**
 * Created by mcs on 2016/12/12.
 */

public class MsgAdapter extends ArrayAdapter<Msg> {
    private static String TAG = "MsgAdapter";
    private static int TAG_LIMIT = 5;
    private int resourceId;
    private ViewPager viewPager;
    private Context context;
    private ChatPresenter presenter;
    private List<View> pageViewList;
    //    private MViewPageAdapter resultAdapter;
    private View resultShowView;
    //是否是因为滑动而更新数据
    private boolean isScroll = false;
    private float textSize = 26;


    private static int checkBoxColor = Color.parseColor("#3EA1FC");

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects, ChatPresenter chatPresenter) {
        super(context, textViewResourceId, objects);// 初始化合成对象
        resourceId = textViewResourceId;
        this.context = context;
        presenter = chatPresenter;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Msg msg = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) convertView.findViewById(R.id.right_layout);
            viewHolder.checkLayout = (LinearLayout) convertView.findViewById(R.id.check_layout);
            viewHolder.robotMsg = (LinearLayout) convertView.findViewById(R.id.robot_msg);
            viewHolder.robotCheckMsg = (LinearLayout) convertView.findViewById(R.id.robot_check_msg);
//            viewHolder.meMsg = (LinearLayout) convertView.findViewById(R.id.me_msg);
            viewHolder.leftMsg = (TextView) convertView.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) convertView.findViewById(R.id.right_msg);
            viewHolder.checkGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
            viewHolder.resultView = (LinearLayout) convertView.findViewById(R.id.result_layout);
            viewHolder.checkButtonLayout = (LinearLayout) convertView.findViewById(R.id.check_button_layout);
//            viewHolder.dotLayout = (LinearLayout) convertView.findViewById(R.id.dot_layout);
            viewHolder.checkText = (TextView) convertView.findViewById(R.id.check_text);
            viewHolder.moreRelay = (RelativeLayout) convertView.findViewById(R.id.moreRelay);
            viewHolder.moreRelay.setVisibility(View.GONE);
            viewHolder.nextButton = (Button) convertView.findViewById(R.id.nextButton);
//            viewHolder.resultButton = (Button) convertView.findViewById(R.id.resultButton);
            viewHolder.robotMoreLayout = (LinearLayout) convertView.findViewById(R.id.robot_more_msg);
            viewHolder.moreTagButton = (Button) convertView.findViewById(R.id.moreTagButton);
            viewHolder.noButton = (Button) convertView.findViewById(R.id.noButton);
            viewHolder.qrCodeLay = (LinearLayout) convertView.findViewById(R.id.id_qr_code_lay);
            viewHolder.subResultLay = (LinearLayout) convertView.findViewById(R.id.left_layout55);
            viewHolder.getMoreInfoText = (TextView) convertView.findViewById(R.id.id_getMoreInfo);
            convertView.setTag(viewHolder);
        } else {
//            parentView = convertView;
//            parentView.setTag(R.id.result, position);
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        Constant.hideProgressDialog();
        switch (msg.getType()) {
            case Msg.TYPE_RECEIVED:
                //接收类型
                viewHolder.meMsg.setVisibility(View.GONE);  //右侧“我的提问”
                viewHolder.robotMoreLayout.setVisibility(View.GONE);    //   需要or不需要的布局"
                viewHolder.qrCodeLay.setVisibility(View.GONE);      // “二维码的布局”
                viewHolder.resultView.setVisibility(View.GONE);     // "viewpager部分的布局，包括底部点点"
                if (msg.getChoiceMsgs() == null) {
                    //您好，我是擎盾问法机器人，擎问有什么可以帮助您的吗？您可以问我任何法律相关的问题
                    viewHolder.robotMsg.setVisibility(View.VISIBLE);    //左侧纯文本布局
                    viewHolder.robotCheckMsg.setVisibility(View.GONE);  //此布局包含了   继续追问、查看结果布局、更多区域按钮、radiobutton、CheckBox部分的布局
                    viewHolder.leftMsg.setText(msg.getContent());
                } else {
                    viewHolder.robotCheckMsg.setVisibility(View.VISIBLE);
                    viewHolder.checkText.setText(msg.getContent());
                    viewHolder.moreRelay.setVisibility(View.GONE);  // 更多按钮区域
                    viewHolder.robotMsg.setVisibility(View.GONE);
                    viewHolder.checkGroup.removeAllViews();     // RadioGroup控件清空
                    viewHolder.checkButtonLayout.setVisibility(View.GONE);  //   继续追问、查看结果子布局
                    int radioButtonIndex = 0;
                    for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                        //
                        RadioButton radioButton = new RadioButton(context);
                        radioButton.setButtonDrawable(R.drawable.my_radio_box);
                        radioButton.setId(Integer.parseInt("" + position + radioButtonIndex));
                        radioButton.setChecked(choiceMsg.isChecked());
                        radioButton.setText(choiceMsg.getContent());
                        radioButton.setTextColor(checkBoxColor);
                        radioButton.setTextSize(textSize);
                        radioButton.setPadding(15, 0, 0, 0);
                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 15, 0, 15);
                        radioButton.setLayoutParams(layoutParams);
                        radioButton.setTop(50);
                        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                                if (isChecked) {
                                    presenter.getExample(compoundButton.getText().toString());
                                }
                            }
                        });
                        if (radioButtonIndex >= TAG_LIMIT && !msg.isMore()) {
                            //tag数大于5而且未展示更多
                            //最多展示5个tag，多余的隐藏
                            radioButton.setVisibility(View.GONE);
                        }
                        viewHolder.checkGroup.addView(radioButton);
                        radioButtonIndex++;
                    }
                    if (msg.getChoiceMsgs().length > TAG_LIMIT && !msg.isMore()) {
                        //显示更多按钮区域
                        viewHolder.moreRelay.setVisibility(View.VISIBLE);
                        viewHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (int i = TAG_LIMIT; i < msg.getChoiceMsgs().length; i++) {
                                    RadioButton radioButton = (RadioButton) viewHolder.checkGroup.findViewById(Integer.parseInt("" + position + i));
                                    radioButton.setVisibility(View.VISIBLE);
                                    view.setVisibility(View.GONE);
                                    msg.setMore(true);
                                    notifyDataSetChanged();
                                    //
                                    presenter.more(msg);
                                }
                            }
                        });
                    } else {
                        viewHolder.moreRelay.setVisibility(View.GONE);
                    }
                }
                break;
            case Msg.TYPE_SEND:
                //发送类型
                viewHolder.meMsg.setVisibility(View.VISIBLE);
                viewHolder.robotMoreLayout.setVisibility(View.GONE);
                viewHolder.qrCodeLay.setVisibility(View.GONE);
                viewHolder.robotMsg.setVisibility(View.GONE);
                viewHolder.resultView.setVisibility(View.GONE);
                viewHolder.robotCheckMsg.setVisibility(View.GONE);
                viewHolder.rightMsg.setText(msg.getContent());
                break;
            case Msg.TYPE_CAUSE:
            case Msg.TYPE_TAG:
                viewHolder.robotCheckMsg.setVisibility(View.VISIBLE);
                viewHolder.checkText.setText(msg.getContent() == null ? "请结合实际情况进行选择:" : msg.getContent());
                viewHolder.meMsg.setVisibility(View.GONE);
                viewHolder.robotMoreLayout.setVisibility(View.GONE);
                viewHolder.qrCodeLay.setVisibility(View.GONE);
                viewHolder.robotMsg.setVisibility(View.GONE);
                viewHolder.resultView.setVisibility(View.GONE);
                viewHolder.checkGroup.removeAllViews();
                viewHolder.checkButtonLayout.setVisibility(View.GONE);
                if (msg.getType() == Msg.TYPE_CAUSE) {
                    //案例
                    viewHolder.moreRelay.setVisibility(View.GONE);
                    int radioButtonIndex = 0;
                    for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                        RadioButton radioButton = new RadioButton(context);
                        radioButton.setButtonDrawable(R.drawable.my_radio_box);
                        radioButton.setId(Integer.parseInt("" + position + radioButtonIndex));
                        radioButton.setChecked(choiceMsg.isChecked());
                        radioButton.setText(choiceMsg.getContent());
                        radioButton.setTextColor(checkBoxColor);
                        radioButton.setTextSize(textSize);
                        radioButton.setPadding(15, 0, 0, 0);
                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 15, 0, 15);
                        radioButton.setLayoutParams(layoutParams);

                        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                                if (isChecked) {
                                    String caseCauseId = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(compoundButton.getText().toString(), "");
                                    presenter.getFather(caseCauseId);
                                }
                            }
                        });
                        if (radioButtonIndex >= TAG_LIMIT && !msg.isMore()) {
                            //tag数大于5而且未展示更多
                            //最多展示5个tag，多余的隐藏
                            radioButton.setVisibility(View.GONE);
                        }
                        viewHolder.checkGroup.addView(radioButton);
                        radioButtonIndex++;
                    }
                    if (msg.getChoiceMsgs().length > TAG_LIMIT && !msg.isMore()) {
                        //显示更多按钮区域
                        viewHolder.moreRelay.setVisibility(View.VISIBLE);
                        viewHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (int i = TAG_LIMIT; i < msg.getChoiceMsgs().length; i++) {
                                    RadioButton radioButton = (RadioButton) viewHolder.checkGroup.findViewById(Integer.parseInt("" + position + i));
                                    radioButton.setVisibility(View.VISIBLE);
                                    view.setVisibility(View.GONE);
                                    msg.setMore(true);
                                    notifyDataSetChanged();
                                    //
                                    presenter.more(msg);
                                }
                            }
                        });
                    } else {
                        viewHolder.moreRelay.setVisibility(View.GONE);
                    }
                } else if (msg.getType() == Msg.TYPE_TAG) {
                    //  勾选项
                    int checkBoxIndex = 0;
                    for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                        CheckBox checkBox = new CheckBox(context);
                        checkBox.setButtonDrawable(R.drawable.my_check_box);
                        checkBox.setId(Integer.parseInt("" + position + checkBoxIndex));
                        checkBox.setChecked(choiceMsg.isChecked());
                        checkBox.setText(choiceMsg.getContent());
                        checkBox.setTextColor(checkBoxColor);
                        checkBox.setTextSize(textSize);
                        checkBox.setPadding(15, 0, 0, 6);  //调整文本的高度
                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 15, 0, 15);
                        checkBox.setLayoutParams(layoutParams);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                            }
                        });
                        if (checkBoxIndex >= TAG_LIMIT && !msg.isMore()) {
                            //tag数大于5而且未展示更多
                            //最多展示5个tag，多余的隐藏
                            checkBox.setVisibility(View.GONE);
                        }
                        viewHolder.checkGroup.addView(checkBox);
                        checkBoxIndex++;
                    }
                    if (msg.getChoiceMsgs().length > TAG_LIMIT && !msg.isMore()) {
                        //显示更多按钮区域
                        viewHolder.moreRelay.setVisibility(View.VISIBLE);
                        viewHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                for (int i = TAG_LIMIT; i < msg.getChoiceMsgs().length; i++) {
                                    CheckBox checkBox = (CheckBox) viewHolder.checkGroup.findViewById(Integer.parseInt("" + position + i));
                                    checkBox.setVisibility(View.VISIBLE);
                                    view.setVisibility(View.GONE);
                                    msg.setMore(true);
                                    notifyDataSetChanged();
                                    //
                                    presenter.more(msg);
                                }
                            }
                        });
                    } else {
                        viewHolder.moreRelay.setVisibility(View.GONE);
                    }

                    //继续追问
                    viewHolder.nextButton.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    msg.setFlag(1);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    msg.setFlag(1);
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    msg.setFlag(0);
                                    break;
                            }
                            changeButtonSelector(msg, viewHolder);
                            return false;
                        }
                    });

                    //继续追问
                    viewHolder.nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<String> tagIds = new ArrayList<>();
                            for (Msg.ChoiceMsg choice : msg.getChoiceMsgs()) {
                                if (choice.isChecked()) {
                                    //以content为key，从SP中取出value：id
                                    tagIds.add(context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(choice.getContent(), ""));
                                }
                            }
                            if (tagIds.size() != 0){
                                //转为字符串数组样式
                                String[] tagStr = new String[tagIds.size()];
                                for (int i = 0; i < tagIds.size(); i++) {
                                    tagStr[i] = tagIds.get(i);
                                }
                         //       presenter.getSon(tagStr);
                                msg.setFlag(1);
                            }else{
                                msg.setFlag(0);
                                MyToast.show(getContext(),"请至少勾选一项",2000);
                            }
                            changeButtonSelector(msg,viewHolder);
                        }
                    });

                    //查看结果
                    viewHolder.resultButton.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    msg.setFlag(2);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    msg.setFlag(2);
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    msg.setFlag(0);
                                    break;
                            }
                            changeButtonSelector(msg, viewHolder);
                            return false;
                        }
                    });

                    //查看结果
                    viewHolder.resultButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String tagStr = "";
                            for (Msg.ChoiceMsg choice : msg.getChoiceMsgs()) {
                                if (choice.isChecked()) {
                                    //将被勾选的tag压入栈中
                                    String tagId = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(choice.getContent(), "");
                                    tagStr += tagId + ",";
                                    presenter.getTagStack().push(tagId);
                                }
                            }
                            if (tagStr.length() > 0) {
                                //去掉最后一个逗号
                                if (tagStr.substring(tagStr.length() - 1).equals(",")) {
                                    tagStr = tagStr.substring(0, tagStr.length() - 1);
                                }
                            }
                            //存储用户已勾选的标签
                            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String curTagChosen = sharedPreferences.getString("tagChosen", "");
                            if (!curTagChosen.equals("") && !tagStr.equals("")) {
                                curTagChosen += ",";
                            }
                            editor.putString("tagChosen", curTagChosen + tagStr);
                            editor.commit();
                            presenter.getTag();
                            msg.setFlag(2);

                        }
                    });
                    //查看结果
                    viewHolder.checkButtonLayout.setVisibility(View.VISIBLE);

                    changeButtonSelector(msg, viewHolder);
                }
                break;
            case Msg.TYPE_RESULT:
                //结果，部分
                viewHolder.resultView.setVisibility(View.VISIBLE);
                viewHolder.robotMoreLayout.setVisibility(View.GONE);
                viewHolder.qrCodeLay.setVisibility(View.GONE);
                viewHolder.robotCheckMsg.setVisibility(View.GONE);
                viewHolder.meMsg.setVisibility(View.GONE);
                viewHolder.robotMsg.setVisibility(View.GONE);

                viewHolder.subResultLay.removeAllViews();
                if (msg.getLaw() != null) {
                    //法律法规
                    View subView = LayoutInflater.from(getContext()).inflate(R.layout.chat_result_item_view,null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.falvfagui);
                    textType.setText("法律法规");
                    titleText.setText(msg.getLaw().getTitle());
                    contentText.setText(msg.getLaw().getContent());
                    viewHolder.subResultLay.addView(subView);

                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToDetail(0);
                        }
                    });

                    contentText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToLawDetail(msg.getLaw().getId(), msg.getLaw().getTitle(), Constant.LAW_INFO);
                        }
                    });
                }
                if (msg.getPoint() != null) {
                    View subView = LayoutInflater.from(getContext()).inflate(R.layout.chat_result_item_view,null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.sifaguandian);
                    textType.setText("司法观点");
                    titleText.setText(msg.getPoint().getTitle());
                    contentText.setText(msg.getPoint().getContent());
                    viewHolder.subResultLay.addView(subView);

                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToDetail(4);
                        }
                    });

                    contentText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToLawDetail(msg.getPoint().getId(), msg.getPoint().getTitle(), Constant.LAW_POINTS);
                        }
                    });
                }
                if (msg.getCaseInfo() != null) {
                    //相关案例
                    View subView = LayoutInflater.from(getContext()).inflate(R.layout.chat_result_item_view,null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.jingxuananli);
                    textType.setText("精选案例");
                    titleText.setText(msg.getCaseInfo().getTitle());
                    contentText.setText(msg.getCaseInfo().getContent());
                    viewHolder.subResultLay.addView(subView);

                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToDetail(3);
                        }
                    });

                    contentText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToLawDetail(msg.getCaseInfo().getId(), msg.getCaseInfo().getTitle(), Constant.LAW_CASE);
                        }
                    });

                }else if (msg.getGuidanceCase() != null) {
                    //指导案例
                    View subView = LayoutInflater.from(getContext()).inflate(R.layout.chat_result_item_view,null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.jingxuananli);
                    textType.setText("精选案例");
                    titleText.setText(msg.getGuidanceCase().getTitle());
                    contentText.setText(msg.getGuidanceCase().getContent());
                    viewHolder.subResultLay.addView(subView);

                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToDetail(3);
                        }
                    });

                    contentText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            skipToLawDetail(msg.getGuidanceCase().getId(), msg.getGuidanceCase().getTitle(), Constant.LAW_GUIDE_CASE);
                        }
                    });
                }

                viewHolder.getMoreInfoText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getContext().startActivity(new Intent(getContext(),DataDetailActivity.class));
                    }
                });
                break;
            case Msg.TYPE_MORE:
                //需要 or 不需要
                viewHolder.robotMoreLayout.setVisibility(View.VISIBLE);
                viewHolder.qrCodeLay.setVisibility(View.GONE);

                viewHolder.resultView.setVisibility(View.GONE);
                viewHolder.robotCheckMsg.setVisibility(View.GONE);
                viewHolder.meMsg.setVisibility(View.GONE);
                viewHolder.robotMsg.setVisibility(View.GONE);

                viewHolder.moreTagButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                msg.setFlag(1);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                msg.setFlag(1);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                msg.setFlag(0);
                                break;
                        }
                        changeButtonSelector2(msg,viewHolder);
                        return false;
                    }
                });
                //需要
                viewHolder.moreTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.getPredictCause(presenter.getQuestion());
                    }
                });

                viewHolder.noButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                msg.setFlag(2);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                msg.setFlag(2);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                msg.setFlag(0);
                                break;
                        }
                        changeButtonSelector2(msg,viewHolder);
                        return false;
                    }
                });
                //不需要
                viewHolder.noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.getTagStack().clear();
                        presenter.showQR();
                    }
                });
                changeButtonSelector2(msg,viewHolder);
                break;
            case Msg.TYPE_QR:
                //二维码
//                if (presenter.getTagStack().empty()){
                viewHolder.qrCodeLay.setVisibility(View.VISIBLE);
                viewHolder.robotMoreLayout.setVisibility(View.GONE);
                viewHolder.resultView.setVisibility(View.GONE);
                viewHolder.robotCheckMsg.setVisibility(View.GONE);
                viewHolder.meMsg.setVisibility(View.GONE);
                viewHolder.robotMsg.setVisibility(View.GONE);
//                }
                break;

        }
        return convertView;
    }


    static class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout robotMoreLayout;
        LinearLayout robotMsg;
        LinearLayout robotCheckMsg;
        LinearLayout meMsg;
        TextView leftMsg;
        TextView rightMsg;
        LinearLayout resultView;
        LinearLayout checkLayout;
        RadioGroup checkGroup;
        LinearLayout checkButtonLayout;
        //        LinearLayout dotLayout;
        TextView lawContent;
        TextView pointContent;
        TextView caseContent;
        TextView guideContent;
        TextView lawTitle;
        TextView pointTitle;
        TextView caseTitle;
        TextView guideTitle;
        //        LinearLayout detailLay;
        TextView checkText;
        RelativeLayout moreRelay;
        Button nextButton;
        Button resultButton;
        Button moreTagButton;
        Button noButton;

        //四个查看更多
        LinearLayout caseDetailRelay;     //精选案例
        LinearLayout guideDetailRelay;    //指导案列
        LinearLayout lawDetailRelay;      //相关法条
        LinearLayout pointDDetailRelay;     //司法观点

        //二维码跟布局
        LinearLayout qrCodeLay;

        //结果页子布局
        LinearLayout subResultLay;

        //结果页查看更多
        TextView getMoreInfoText;
    }

    private class MViewPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
        private List<View> mViewList;
        private View convertView;
        private Msg msg;

        MViewPageAdapter(List<View> views, View convertView, Msg msg) {
            mViewList = views;
            this.convertView = convertView;
            this.msg = msg;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ViewGroup parent = (ViewGroup) mViewList.get(position).getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            container.addView(mViewList.get(position), 0);

            mViewList.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            //法律知识
                            skipToLawDetail(msg.getLaw().getId(), msg.getLaw().getTitle(), Constant.LAW_INFO);
                            break;
                        case 1:
                            //司法观点
                            skipToLawDetail(msg.getPoint().getId(), msg.getPoint().getTitle(), Constant.LAW_POINTS);
                            break;
                        case 2:
                            //相关案例
                            skipToLawDetail(msg.getCaseInfo().getId(), msg.getCaseInfo().getTitle(), Constant.LAW_CASE);
                            break;
                        case 3:
                            //指导案列
                            skipToLawDetail(msg.getGuidanceCase().getId(), msg.getGuidanceCase().getTitle(), Constant.LAW_GUIDE_CASE);
                            break;
                    }
                }
            });
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (convertView != null) {
//                int msgPosition = (int) convertView.getTag(R.id.result);
//                Msg curMsg = MsgAdapter.this.getItem(msgPosition);
//                curMsg.setCurPage(position);
//                Log.d(TAG, "onPageSelected: position:" + convertView.getTag(R.id.result));

            }
            MsgAdapter.this.notifyDataSetChanged(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }

    public void notifyDataSetChanged(boolean isScroll) {
        this.isScroll = isScroll;
        super.notifyDataSetChanged();
    }

    /**
     * 跳转到详情页
     */
    private void skipToDetail(int item) {
        Intent intent = new Intent(context, DataDetailActivity.class);
        intent.putExtra("item", item);
        context.startActivity(intent);
    }

    /**
     * 跳转到详情页2
     */
    private void skipToLawDetail(String id, String name, int type) {

        Intent intent = new Intent(context, LawDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    /**
     * 更新 "继续追问"、"查看结果" 按钮状态
     * 更新按钮状态
     * @param msg
     * @param viewHolder
     */
    private void changeButtonSelector(Msg msg, ViewHolder viewHolder) {
        if (msg.getFlag() == 1) {
            viewHolder.nextButton.setBackgroundColor(Color.parseColor("#227CCE"));
            viewHolder.nextButton.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.resultButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.resultButton.setTextColor(Color.parseColor("#111111"));
        } else if (msg.getFlag() == 2) {
            viewHolder.resultButton.setBackgroundColor(Color.parseColor("#227CCE"));
            viewHolder.resultButton.setTextColor(Color.parseColor("#eeeeee"));
            viewHolder.nextButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.nextButton.setTextColor(Color.parseColor("#111111"));
        } else {
            viewHolder.resultButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.resultButton.setTextColor(Color.parseColor("#111111"));
            viewHolder.nextButton.setTextColor(Color.parseColor("#111111"));
            viewHolder.nextButton.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
    }

    /**
     * 更新 "需要"、"不需要" 按钮状态
     * @param msg
     * @param viewHolder
     */
    private void changeButtonSelector2(Msg msg, ViewHolder viewHolder){
        if (msg.getFlag() == 1) {
            viewHolder.moreTagButton.setBackgroundColor(Color.parseColor("#227CCE"));
            viewHolder.moreTagButton.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.noButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.noButton.setTextColor(Color.parseColor("#111111"));
        } else if (msg.getFlag() == 2) {
            viewHolder.noButton.setBackgroundColor(Color.parseColor("#227CCE"));
            viewHolder.noButton.setTextColor(Color.parseColor("#eeeeee"));
            viewHolder.moreTagButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.moreTagButton.setTextColor(Color.parseColor("#111111"));
        } else {
            viewHolder.noButton.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.noButton.setTextColor(Color.parseColor("#111111"));
            viewHolder.moreTagButton.setTextColor(Color.parseColor("#111111"));
            viewHolder.moreTagButton.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
    }
}
