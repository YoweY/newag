package com.info.aegis.lawpush4android.presenter.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.MsgTagBean;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.model.bean.entity.law.MsgCauseBean;
import com.info.aegis.lawpush4android.presenter.ChatPresenter;
import com.info.aegis.lawpush4android.utils.Constant;
import com.info.aegis.lawpush4android.utils.HttpHelper;
import com.info.aegis.lawpush4android.utils.MyToast;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;
import com.info.aegis.lawpush4android.view.iml.ChooseActivity;
import com.info.aegis.lawpush4android.view.iml.DataDetailActivity;
import com.info.aegis.lawpush4android.view.iml.DialogActivity;
import com.info.aegis.lawpush4android.view.iml.LawDetailActivity;
import com.info.aegis.lawpush4android.view.iml.ProgramQuestionDialogActivity;
import com.info.aegis.lawpush4android.view.iml.StandardQuestionDialogActivity;

import java.util.ArrayList;
import java.util.List;

import static com.info.aegis.lawpush4android.LawPushApp.USER;

/**
 * 作者：jksfood on 2017/1/24 14:20
 */

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<Msg> msgs;
    private ChatPresenter chatPresenter;
    private static int checkBoxColor = Color.parseColor("#3EA1FC");
    private float textSize = 26;
    private ListView listView;
    boolean isFrist = false;

    public ChatAdapter(Context context, List<Msg> msgs, ChatPresenter chatPresenter, ListView listView) {
        this.context = context;
        this.msgs = msgs;
        this.chatPresenter = chatPresenter;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return msgs.get(position).getType();
    }

    // TODO: 2017/2/10   多种行视图的个数 ////以后修改
    @Override
    public int getViewTypeCount() {
        return 20;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final Msg msg = msgs.get(position);
        ReceiveHolder receiveHolder = null;
        ChatAnswerHolder chatAnswerHolder = null;
        SendHolder sendHolder = null;
        CauseHolder causeHolder = null;
        CauseDemoHolder causeDemoHolder = null;
        TagHolder tagHolder = null;
        SimilarCauseHolder similarCauseHolder = null;
        HotCauseHolder hotCauseHolder = null;
        ResultHolder resultHolder = null;
        NeedMoreHolder needMoreHolder = null;
        QRHolder qrHolder = null;
        ProgramQuestionHolder programQuestionHolder = null;
        SaticfactionHolder saticfactionHolder = null;
        //    TipHolder tipHolder = null;
        CHAT_CHATHolder chatHolder = null;
        CHAT_WEATHERHolder chatWeatherHolder = null;
        ChatLawsHolder chatLawsHolder = null;
        AnswerPicHolder answerPicHolder = null;
        if (convertView == null) {
            switch (type) {
                case Msg.TYPE_RECEIVED:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_receive_type, null);
                    receiveHolder = new ReceiveHolder();
                    receiveHolder.leftImg = (ImageView) convertView.findViewById(R.id.left_img);

                    receiveHolder.leftText = (TextView) convertView.findViewById(R.id.left_msg);
                    convertView.setTag(receiveHolder);
                    break;
                case Msg.CHAT_SOS_TEXT:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_receive_type, null);
                    chatAnswerHolder = new ChatAnswerHolder();
                    chatAnswerHolder.leftImg = (ImageView) convertView.findViewById(R.id.left_img);
                    chatAnswerHolder.leftText = (TextView) convertView.findViewById(R.id.left_msg);
                    convertView.setTag(chatAnswerHolder);
                    break;
                case Msg.CHAT_CHAT:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_receive_type_chat_chat, null);
                    chatHolder = new CHAT_CHATHolder();
                    chatHolder.answerLeftText = (TextView) convertView.findViewById(R.id.answer_left_text);
                    chatHolder.answerRightText = (TextView) convertView.findViewById(R.id.answer_right_text);
                    chatHolder.answerLeftImg = (ImageView) convertView.findViewById(R.id.answer_left_img);
                    chatHolder.answerMidImg = (ImageView) convertView.findViewById(R.id.answer_mid_img);
                    chatHolder.answerRightImg = (ImageView) convertView.findViewById(R.id.answer_right_img);
                    chatHolder.titleLayout = (LinearLayout) convertView.findViewById(R.id.title_layout);
                    convertView.setTag(chatHolder);
                    break;
                case Msg.CHAT_WEATHER:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_receive_type_chat_weather, null);
                    chatWeatherHolder = new CHAT_WEATHERHolder();
                    chatWeatherHolder.leftText = (TextView) convertView.findViewById(R.id.left_msg);
                    convertView.setTag(chatWeatherHolder);
                    break;
                case Msg.TYPE_SEND:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_send_type, null);
                    sendHolder = new SendHolder();
                    sendHolder.rightText = (TextView) convertView.findViewById(R.id.right_msg);
                    sendHolder.rightBackGround = (LinearLayout) convertView.findViewById(R.id.right_layout);
                    convertView.setTag(sendHolder);
                    break;
                case Msg.TYPE_CAUSE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_cause_type, null);
                    causeHolder = new CauseHolder();
                    causeHolder.layout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                    causeHolder.tipText = (TextView) convertView.findViewById(R.id.check_text);
                    causeHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
                    causeHolder.moreRelay = (RelativeLayout) convertView.findViewById(R.id.moreRelay);
                    convertView.setTag(causeHolder);
                    break;
                case Msg.TYPE_TAG:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_tag_type, null);
                    tagHolder = new TagHolder();
                    tagHolder.layout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                    tagHolder.tipText = (TextView) convertView.findViewById(R.id.check_text);
                    tagHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
                    tagHolder.nextBtn = (Button) convertView.findViewById(R.id.nextButton);
//                    tagHolder.resultBtn = (Button) convertView.findViewById(R.id.resultButton);
                    tagHolder.moreRelay = (RelativeLayout) convertView.findViewById(R.id.moreRelay);
                    convertView.setTag(tagHolder);
                    break;
                case Msg.SIMILAR_CAUSE_TYPE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_similar_cause_type, null);
                    similarCauseHolder = new SimilarCauseHolder();
                    similarCauseHolder.layout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                    similarCauseHolder.tipText = (TextView) convertView.findViewById(R.id.check_text);
                    similarCauseHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
                    convertView.setTag(similarCauseHolder);
                    break;
                case Msg.HOT_CAUSE_TYPE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_similar_cause_type, null);
                    hotCauseHolder = new HotCauseHolder();
                    hotCauseHolder.layout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                    hotCauseHolder.tipText = (TextView) convertView.findViewById(R.id.check_text);
                    hotCauseHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
                    convertView.setTag(hotCauseHolder);
                    break;
                case Msg.TYPE_RESULT:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_result_type, null);
                    resultHolder = new ResultHolder();
                    resultHolder.checkLayout = (LinearLayout) convertView.findViewById(R.id.check_layout);
                    resultHolder.tipText = (TextView) convertView.findViewById(R.id.id_result_tip);
                    resultHolder.itemLay = (LinearLayout) convertView.findViewById(R.id.left_layout55);
                    resultHolder.resultLay = (RelativeLayout) convertView.findViewById(R.id.item_result_rl);
                    convertView.setTag(resultHolder);
                    break;
                case Msg.TYPE_MORE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_needmore_type, null);
                    needMoreHolder = new NeedMoreHolder();
                    needMoreHolder.needBtn = (Button) convertView.findViewById(R.id.moreTagButton);
                    needMoreHolder.noNeedBtn = (Button) convertView.findViewById(R.id.noButton);
                    convertView.setTag(needMoreHolder);
                    break;
                case Msg.TYPE_QR:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_qr_type, null);
                    qrHolder = new QRHolder();
                    qrHolder.qrImg = (ImageView) convertView.findViewById(R.id.id_qrImg);
                    convertView.setTag(qrHolder);
                    break;
                case Msg.PROGRAMER_QUESTION:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_program_question_type, null);
                    programQuestionHolder = new ProgramQuestionHolder();
                    programQuestionHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.left_layout66);
                    convertView.setTag(programQuestionHolder);
                    break;
                case Msg.SATISFACTION:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_saticfaction_type, null);
                    saticfactionHolder = new SaticfactionHolder();
                    saticfactionHolder.yesBtn = (Button) convertView.findViewById(R.id.satisfaction_btn);
                    saticfactionHolder.noBtn = (Button) convertView.findViewById(R.id.no_satisfaction_btn);
                    convertView.setTag(saticfactionHolder);
                    break;
//                case Msg.TIP_LAYOUT:
//                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_tip_type, null);
//                    tipHolder = new TipHolder();
//                    convertView.setTag(tipHolder);
//                    break;
                case Msg.CAUSE_DEMO_TYPE:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_cause_type, null);
                    causeDemoHolder = new CauseDemoHolder();
                    causeDemoHolder.tipText = (TextView) convertView.findViewById(R.id.check_text);
                    causeDemoHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.check_group);
                    causeDemoHolder.moreRelay = (RelativeLayout) convertView.findViewById(R.id.moreRelay);
                    convertView.setTag(causeDemoHolder);
                    break;
                case Msg.CHAT_LAWS:
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_laws_result, null);
                    chatLawsHolder = new ChatLawsHolder();
                    chatLawsHolder.tipText = (TextView) convertView.findViewById(R.id.id_result_tip);
                    chatLawsHolder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.layoutChat);
                    convertView.setTag(chatLawsHolder);
                    break;

                case Msg.ANSWER_PICTURES:
                    convertView = LayoutInflater.from(context).inflate(R.layout.answer_pictures, null);
                    answerPicHolder = new AnswerPicHolder();
                    answerPicHolder.answerLeftText = (TextView) convertView.findViewById(R.id.answer_left_text);
                    answerPicHolder.answerRightText = (TextView) convertView.findViewById(R.id.answer_right_text);
                    answerPicHolder.answerLeftImg = (ImageView) convertView.findViewById(R.id.answer_left_img);
                    answerPicHolder.answerMidImg = (ImageView) convertView.findViewById(R.id.answer_mid_img);
                    answerPicHolder.answerRightImg = (ImageView) convertView.findViewById(R.id.answer_right_img);
                    answerPicHolder.gridView = (GridView) convertView.findViewById(R.id.answer_gridView);
                    answerPicHolder.titleLayout = (LinearLayout) convertView.findViewById(R.id.title_layout);
                    convertView.setTag(answerPicHolder);
                    break;

            }
        } else {
            switch (type) {
                case Msg.TYPE_RECEIVED:
                    receiveHolder = (ReceiveHolder) convertView.getTag();
                    break;
                case Msg.CHAT_SOS_TEXT:
                    chatAnswerHolder = (ChatAnswerHolder) convertView.getTag();
                    break;
                case Msg.CHAT_CHAT:
                    chatHolder = (CHAT_CHATHolder) convertView.getTag();
                    break;
                case Msg.CHAT_WEATHER:
                    chatWeatherHolder = (CHAT_WEATHERHolder) convertView.getTag();
                    break;
                case Msg.TYPE_SEND:
                    sendHolder = (SendHolder) convertView.getTag();
                    break;
                case Msg.TYPE_CAUSE:
                    causeHolder = (CauseHolder) convertView.getTag();
                    break;
                case Msg.TYPE_TAG:
                    tagHolder = (TagHolder) convertView.getTag();
                    break;
                case Msg.SIMILAR_CAUSE_TYPE:
                    similarCauseHolder = (SimilarCauseHolder) convertView.getTag();
                    break;
                case Msg.HOT_CAUSE_TYPE:
                    hotCauseHolder = (HotCauseHolder) convertView.getTag();
                    break;
                case Msg.TYPE_RESULT:
                    resultHolder = (ResultHolder) convertView.getTag();
                    break;
                case Msg.TYPE_MORE:
                    needMoreHolder = (NeedMoreHolder) convertView.getTag();
                    break;
                case Msg.TYPE_QR:
                    qrHolder = (QRHolder) convertView.getTag();
                    break;
                case Msg.PROGRAMER_QUESTION:
                    programQuestionHolder = (ProgramQuestionHolder) convertView.getTag();
                    break;
                case Msg.SATISFACTION:
                    saticfactionHolder = (SaticfactionHolder) convertView.getTag();
                    break;
//                case Msg.TIP_LAYOUT:
//                    tipHolder = (TipHolder) convertView.getTag();
//                    break;
                case Msg.CAUSE_DEMO_TYPE:
                    causeDemoHolder = (CauseDemoHolder) convertView.getTag();
                    break;
                case Msg.CHAT_LAWS:
                    chatLawsHolder = (ChatLawsHolder) convertView.getTag();
                    break;
                case Msg.ANSWER_PICTURES:
                    answerPicHolder = (AnswerPicHolder) convertView.getTag();
            }
        }

        switch (type) {
            case Msg.TYPE_RECEIVED:
                receiveHolder.leftText.setText(msg.getContent());
//                if (context instanceof ChatActivity){
//                    ChatActivity baseActivity= (ChatActivity) context;
//                    baseActivity.loadImage(R.drawable.y,receiveHolder.leftImg,50,50);
//                }
                break;
            case Msg.CHAT_SOS_TEXT:
                chatAnswerHolder.leftText.setText(msg.getContent());
                //  我要立案中怎么立案 的布局文件需要修改
//                if (context instanceof ChatActivity){
//                    ChatActivity baseActivity= (ChatActivity) context;
//                    baseActivity.loadImage(R.drawable.y,chatAnswerHolder.leftImg,50,50);
//                }
                break;
            case Msg.CHAT_CHAT:
//                chatHolder.leftText.setText(msg.getContent());
//                loadImage(R.drawable.y, chatHolder.leftImg, 50, 50);
                String content = msg.getContent();
                //         String content = "我是测试啊!!!@@没听懂、没听清1.png##我是测试";
                String[] chatImg = content.split("@@");
                if (chatImg.length > 1) {
                    final String[] img = chatImg[1].split("##");

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(3000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            destroyDialog();
//                        }
//                    }).start();
                    if (chatImg[0] != null && img.length == 1) {
                        chatHolder.answerRightImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], chatHolder.answerRightImg);
                        chatHolder.answerLeftText.setText(chatImg[0]);
                        chatHolder.answerMidImg.setVisibility(View.GONE);
                        chatHolder.answerLeftImg.setVisibility(View.GONE);
                        chatHolder.answerRightImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                    if (img.length > 1 && img[1] != null) {
                        chatHolder.answerMidImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], chatHolder.answerMidImg);
                        chatHolder.answerLeftText.setText(chatImg[0]);
                        chatHolder.answerRightImg.setVisibility(View.GONE);
                        chatHolder.answerLeftImg.setVisibility(View.GONE);
                        chatHolder.answerRightText.setText(img[1]);
                        chatHolder.answerMidImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                    if (chatImg[0] == null && img.length > 1 && img[1] != null) {
                        chatHolder.answerLeftImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], answerPicHolder.answerLeftImg);
                        chatHolder.answerLeftText.setText(chatImg[0]);
                        chatHolder.answerRightImg.setVisibility(View.GONE);
                        chatHolder.answerMidImg.setVisibility(View.GONE);
                        chatHolder.answerRightText.setText(img[1]);
                        chatHolder.answerLeftImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }

                    Log.e("ChatAdapter", img[0] + "////////////////" + chatImg[0] + "////" + content.length());
//                    answerPicHolder.answerLeftImg.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            switch (event.getAction()) {
//                                case MotionEvent.ACTION_DOWN:
//                                    createDialog(HttpHelper.BASEEMOJIUrl+img[0]);
//                                    break;
//                                case MotionEvent.ACTION_UP:
//                                    destroyDialog();
//                                    break;
//                            }
//                            return true;
//                        }
//                    });
                    if (content.length() > 40) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                        chatHolder.titleLayout.setLayoutParams(layoutParams);
                    }
                } else {
                    chatHolder.answerLeftText.setText(content);
                    chatHolder.answerLeftText.measure(0, 0);
                    int height = chatHolder.answerLeftText.getMeasuredHeight();
                    int lineCount = chatHolder.answerLeftText.getLineCount();
                    int height1 = chatHolder.answerLeftText.getHeight();
                    Log.e("ChatAdapter", height + "////////////////" + lineCount + "//////" + height1);
                    if (content.length() > 38) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                        chatHolder.titleLayout.setLayoutParams(layoutParams);
                    }
                }
                break;
            case Msg.CHAT_WEATHER:
                //天气
                chatWeatherHolder.leftText.setText(msg.getContent());
                break;
            case Msg.CHAT_LAWS:
                //法律法规场景
                chatLawsHolder.mLinearLayout.removeAllViews();
                if (msg.getLawList().size() > 0) {
                    chatLawsHolder.tipText.setText("我为您找到了如下法律法规。");
                    chatLawsHolder.mLinearLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < msg.getLawList().size(); i++) {
                        //法律法规
                        final Law law = msg.getLawList().get(i);
                        View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                        ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                        TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                        TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                        TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                        imageView.setImageResource(R.mipmap.falvfagui);
                        textType.setText("法律法规");
                        titleText.setText(law.getTitle());
                        contentText.setText(law.getContent());
                        chatLawsHolder.mLinearLayout.addView(subView);
                        contentText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                skipToLawDetail(law.getId(), law.getTitle(), Constant.LAW_INFO);
                            }
                        });
                    }
                } else {
                    chatLawsHolder.tipText.setText("对不起,这个问题有点难哦,我现在还不会呦!");
                    chatLawsHolder.mLinearLayout.setVisibility(View.GONE);
                }

                break;
            case Msg.TYPE_SEND:
                sendHolder.rightText.setText(msg.getContent());
                if (LawPushApp.getChatTypt() == 3) {
                    sendHolder.rightBackGround.setBackgroundResource(R.drawable.mygreen);
                    sendHolder.rightText.setTextColor(Color.parseColor("#000000"));
                } else {
                    sendHolder.rightBackGround.setBackgroundResource(R.drawable.my_self_char);
                    sendHolder.rightText.setTextColor(Color.parseColor("#ffffff"));
                }
                break;
            case Msg.TYPE_CAUSE:
//                causeHolder.tipText.setText(msg.getContent() == null ? "请结合实际情况进行选择:" : msg.getContent());
//                causeHolder.radioGroup.removeAllViews();
//                int radioButtonIndex = 0;
//                for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
//                    RadioButton radioButton = new RadioButton(context);
//                    radioButton.setButtonDrawable(R.drawable.my_radio_box);
//                    radioButton.setId(Integer.parseInt("" + position + radioButtonIndex));
//                    radioButton.setChecked(choiceMsg.isChecked());
//                    radioButton.setText(choiceMsg.getContent());
//                    radioButton.setTextColor(checkBoxColor);
//                    radioButton.setTextSize(textSize);
//                    radioButton.setPadding(15, 0, 0, 0);
//                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.setMargins(0, 15, 0, 15);
//                    radioButton.setLayoutParams(layoutParams);
//
//                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                            choiceMsg.setChecked(isChecked);
//                            if (isChecked) {
//                                String caseCauseId = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(compoundButton.getText().toString(), "");
//                                chatPresenter.getFather(caseCauseId);
//                            }
//                        }
//                    });
//                    if (radioButtonIndex >= 5 && !msg.isMore()) {
//                        //tag数大于5而且未展示更多
//                        //最多展示5个tag，多余的隐藏
//                        radioButton.setVisibility(View.GONE);
//                    }
//                    causeHolder.radioGroup.addView(radioButton);
//                    radioButtonIndex++;
//                }
//                if (msg.getChoiceMsgs().length > 5 && !msg.isMore()) {
//                    //显示更多按钮区域
//                    causeHolder.moreRelay.setVisibility(View.VISIBLE);
//                    causeHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            msg.setMore(true);
//                            notifyDataSetChanged();
//                        }
//                    });
//                } else {
//                    causeHolder.moreRelay.setVisibility(View.GONE);
//                }
                causeHolder.radioGroup.removeAllViews();

                Log.d("ChatAdapter", "msg.getPictures().size():" + msg.getPictures().size());
                if (msg.getContent() != null)
                    causeHolder.tipText.setText(msg.getContent());
                int checkBoxIndexCaseCause = 0;
                for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setButtonDrawable(R.drawable.my_check_box);
                    checkBox.setId(Integer.parseInt("" + position + checkBoxIndexCaseCause));
                    checkBox.setChecked(choiceMsg.isChecked());
                    checkBox.setText(choiceMsg.getContent());
                    checkBox.setTextColor(checkBoxColor);
                    checkBox.setTextSize(textSize);
                    checkBox.setPadding(15, 0, 0, 6);  //调整文本的高度
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 15, 0, 15);
                    checkBox.setLayoutParams(layoutParams);
                    if (!checkBox.isChecked()) {
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                                choiceMsg.setChecked(isChecked);
                                String caseCauseId = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(compoundButton.getText().toString(), "");
                                chatPresenter.getFather(caseCauseId);

                                Msg msg1 = new Msg();
                                msg1.setContent(compoundButton.getText().toString());
                                msg1.setType(Msg.TYPE_SEND);
                                msgs.add(msg1);
                                notifyDataSetChanged();
                                listView.setSelection(msgs.size() - 1);
                                MyToast.show(context, "请稍等...", 2000);
                            }
                        });
                    } else {
                        checkBox.setClickable(false);
                    }

                    if (checkBoxIndexCaseCause >= 5 && !msg.isMore()) {
                        //tag数大于5而且未展示更多
                        //最多展示5个tag，多余的隐藏
                        checkBox.setVisibility(View.GONE);
                    }
                    causeHolder.radioGroup.addView(checkBox);
                    checkBoxIndexCaseCause++;
                }
                if (msg.getChoiceMsgs().length > 5 && !msg.isMore()) {
                    //显示更多按钮区域
                    causeHolder.moreRelay.setVisibility(View.VISIBLE);
                    causeHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msg.setMore(true);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    causeHolder.moreRelay.setVisibility(View.GONE);
                }


                causeHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (msg.getChoiceMsgs().length == 2) {
                            //跳横排界面，单选
                            Intent intent = new Intent(context, ChooseActivity.class);
                            intent.putExtra("title", msg.getContent());
                            intent.putExtra("isOne", true);
                            intent.putExtra("type", "cause");
                            if (msg.getMotionsString() != null)
                                intent.putExtra("motion", msg.getMotionsString());
                            List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                            for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                MsgCauseBean msgCauseBean = new MsgCauseBean();
                                msgCauseBean.setName(msg.getChoiceMsgs()[i].getContent());
                                msgCauseBean.setValue(msg.getChoiceMsgs()[i].getCaseCauseId());
                                msgCauseBeanList.add(msgCauseBean);
                            }
                            intent.putExtra("tagArray", msgCauseBeanList.toString());
                            if (context instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) context;
                                activity.startActivityForResult(intent, 12);
                            }
                        }
                        if (msg.getChoiceMsgs().length > 2) {
                            //跳转竖排页面，而且是单选
                            Intent intent = new Intent(context, DialogActivity.class);
                            intent.putExtra("title", msg.getContent());
                            intent.putExtra("isOne", true);
                            intent.putExtra("type", "cause");
                            if (msg.getMotionsString() != null)
                                intent.putExtra("motion", msg.getMotionsString());
                            List<MsgCauseBean> msgCauseBeanList = new ArrayList<>();
                            for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                MsgCauseBean msgCauseBean = new MsgCauseBean();
                                msgCauseBean.setName(msg.getChoiceMsgs()[i].getContent());
                                msgCauseBean.setValue(msg.getChoiceMsgs()[i].getCaseCauseId());
                                msgCauseBeanList.add(msgCauseBean);
                            }
                            intent.putExtra("tagArray", msgCauseBeanList.toString());
                            if (context instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) context;
                                activity.startActivityForResult(intent, 11);
                            }
                        }

                    }
                });

                break;
            case Msg.TYPE_TAG:
                tagHolder.radioGroup.removeAllViews();
                if (msg.getContent() != null)
                    tagHolder.tipText.setText(msg.getContent());
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
                    if (checkBoxIndex >= 5 && !msg.isMore()) {
                        //tag数大于5而且未展示更多
                        //最多展示5个tag，多余的隐藏
                        checkBox.setVisibility(View.GONE);
                    }
                    tagHolder.radioGroup.addView(checkBox);
                    checkBoxIndex++;
                }
                if (msg.getChoiceMsgs().length > 5 && !msg.isMore()) {
                    //显示更多按钮区域
                    tagHolder.moreRelay.setVisibility(View.VISIBLE);
                    tagHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msg.setMore(true);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    tagHolder.moreRelay.setVisibility(View.GONE);
                }
                //继续追问
                final TagHolder finalTagHolder = tagHolder;
                tagHolder.nextBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        changeButtonSelectorTagHolder(msg, finalTagHolder);
                        return false;
                    }
                });

                //下一步
                tagHolder.nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<MsgTagBean> choiceMsgArrayList = new ArrayList<>();
                        for (Msg.ChoiceMsg choice : msg.getChoiceMsgs()) {
                            if (choice.isChecked()) {
                                //以content为key，从SP中取出value：id
                                MsgTagBean msgTagBean = new MsgTagBean();
                                msgTagBean.setZhName(choice.getContent());
                                msgTagBean.setEnName(choice.getEnName());
                                msgTagBean.setType(choice.getType());
                                choiceMsgArrayList.add(msgTagBean);
                            }
                        }
                        boolean isHasFatherTag = false;
                        String[] tagChosen, tagFatherChoosen;
                        int totalNum = 0;
                        if (choiceMsgArrayList.size() != 0) {
                            //有选中的项
                            //判断标签类型，存在父标签，则继续getSon获取子标签，不存在，则直接拿结果
                            for (int i = 0; i < choiceMsgArrayList.size(); i++) {
                                MsgTagBean msgTagBean = choiceMsgArrayList.get(i);
                                if (msgTagBean != null) {
                                    if (msgTagBean.getType() == 0) {
                                        //存在父标签
                                        isHasFatherTag = true;
                                    }
                                    totalNum++;
                                }
                            }
                            if (isHasFatherTag) {
                                //存在父标签
                                tagFatherChoosen = new String[totalNum];
                                for (int i = 0; i < choiceMsgArrayList.size(); i++) {
                                    MsgTagBean msgTagBean = choiceMsgArrayList.get(i);
                                    if (msgTagBean != null) {
                                        //将所有被勾选的标签都传过去
                                        tagFatherChoosen[i] = choiceMsgArrayList.get(i).getEnName();
                                    }
                                }
                                chatPresenter.getSon(tagFatherChoosen);
                            } else {
                                //直接拿子标签获取结果
                                tagChosen = new String[totalNum];
                                for (int i = 0; i < choiceMsgArrayList.size(); i++) {
                                    MsgTagBean msgTagBean = choiceMsgArrayList.get(i);
                                    if (msgTagBean != null) {
                                        //子标签
                                        tagChosen[i] = choiceMsgArrayList.get(i).getEnName();
                                    }
                                }

                                String tagStr = "";
                                for (int i = 0; i < tagChosen.length; i++) {
                                    tagStr = tagChosen[i] + "," + tagStr;
                                }
                                if (tagStr.length() > 0) {
                                    //去掉最后一个逗号
                                    if (tagStr.substring(tagStr.length() - 1).equals(",")) {
                                        tagStr = tagStr.substring(0, tagStr.length() - 1);
                                    }
                                }
                                //存储用户已勾选的标签
                                SharedPreferences sharedPreferences = context.getSharedPreferences(USER, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String curTagChosen = sharedPreferences.getString("tagChosen", "");
                                if (!curTagChosen.equals("") && !tagStr.equals("")) {
                                    curTagChosen += ",";
                                }
                                editor.putString("tagChosen", curTagChosen + tagStr);
                                editor.commit();
                                chatPresenter.getTag();
                            }
                            msg.setFlag(1);
                        } else {
                            msg.setFlag(0);
                            MyToast.show(context, "请至少勾选一项", 2000);
                        }
                        notifyDataSetChanged();
                    }
                });

//                //查看结果
//                tagHolder.resultBtn.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                msg.setFlag(2);
//                                break;
//                            case MotionEvent.ACTION_MOVE:
//                                msg.setFlag(2);
//                                break;
//                            case MotionEvent.ACTION_CANCEL:
//                                msg.setFlag(0);
//                                break;
//                        }
//                        changeButtonSelectorTagHolder(msg, finalTagHolder);
//                        return false;
//                    }
//                });
//
//                //查看结果
//                tagHolder.resultBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String tagStr = "";
//                        for (Msg.ChoiceMsg choice : msg.getChoiceMsgs()) {
//                            if (choice.isChecked()) {
//                                //将被勾选的tag压入栈中
//                                String tagId = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE).getString(choice.getContent(), "");
//                                tagStr += tagId + ",";
//                                chatPresenter.getTagStack().push(tagId);
//                            }
//                        }
//                        if (tagStr.length() > 0) {
//                            //去掉最后一个逗号
//                            if (tagStr.substring(tagStr.length() - 1).equals(",")) {
//                                tagStr = tagStr.substring(0, tagStr.length() - 1);
//                            }
//                        }
//                        //存储用户已勾选的标签
//                        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(USER, Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        String curTagChosen = sharedPreferences.getString("tagChosen", "");
//                        if (!curTagChosen.equals("") && !tagStr.equals("")) {
//                            curTagChosen += ",";
//                        }
//                        editor.putString("tagChosen", curTagChosen + tagStr);
//                        editor.commit();
//                        chatPresenter.getTag();
//                        msg.setFlag(2);
//
//                    }
//                });
                changeButtonSelectorTagHolder(msg, tagHolder);
                tagHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (msg.getChoiceMsgs().length == 2) {
                            //跳横排界面，多选
                            Intent intent = new Intent(context, ChooseActivity.class);
                            intent.putExtra("title", msg.getContent());
                            intent.putExtra("isOne", false);
                            intent.putExtra("type", "tag");
                            if (msg.getMotionsString() != null)
                                intent.putExtra("motion", msg.getMotionsString());
                            List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                            for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                MsgTagBean msgTagBean = new MsgTagBean();
                                msgTagBean.setZhName(msg.getChoiceMsgs()[i].getContent());
                                msgTagBean.setEnName(msg.getChoiceMsgs()[i].getEnName());
                                msgTagBean.setType(msg.getChoiceMsgs()[i].getType());
                                msgTagBeanList.add(msgTagBean);
                            }
                            intent.putExtra("tagArray", msgTagBeanList.toString());
                            if (context instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) context;
                                activity.startActivityForResult(intent, 13);
                            }
                        }

                        if (msg.getChoiceMsgs().length > 2) {
                            //跳竖排全屏界面，多选
                            Intent intent = new Intent(context, DialogActivity.class);
                            intent.putExtra("title", msg.getContent());
                            intent.putExtra("isOne", false);
                            intent.putExtra("type", "tag");
                            if (msg.getMotionsString() != null)
                                intent.putExtra("motion", msg.getMotionsString());
                            List<MsgTagBean> msgTagBeanList = new ArrayList<>();
                            for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                MsgTagBean msgTagBean = new MsgTagBean();
                                msgTagBean.setEnName(msg.getChoiceMsgs()[i].getEnName());
                                msgTagBean.setType(msg.getChoiceMsgs()[i].getType());
                                msgTagBean.setZhName(msg.getChoiceMsgs()[i].getContent());
                                msgTagBeanList.add(msgTagBean);
                            }
                            intent.putExtra("tagArray", msgTagBeanList.toString());
                            if (context instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) context;
                                activity.startActivityForResult(intent, 10);
                            }
                        }
                    }

                });
                break;
            case Msg.SIMILAR_CAUSE_TYPE:
//                try {
//                    similarCauseHolder.radioGroup.removeAllViews();
//                    similarCauseHolder.tipText.setText(msg.getContent());
//                    int radioButtonIndex2 = 0;
//                    for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
//                        final RadioButton radioButton = new RadioButton(context);
//                        radioButton.setButtonDrawable(R.drawable.my_radio_box);
//                        radioButton.setId(Integer.parseInt("" + position + radioButtonIndex2));
//                        radioButton.setChecked(choiceMsg.isChecked());
//                        radioButton.setText(choiceMsg.getContent());
//                        radioButton.setTextColor(checkBoxColor);
//                        radioButton.setTextSize(textSize);
//                        radioButton.setPadding(15, 0, 0, 0);
//                        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.setMargins(0, 15, 0, 15);
//                        radioButton.setLayoutParams(layoutParams);
//
//                        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                                if (!choiceMsg.isChecked()) {
//                                    switch (msg.getSIMILAR_QUESTION_TYPE()) {
//                                        case 1:
//                                            chatPresenter.getExample(radioButton.getText().toString());
//                                            break;
//                                        case 2:
//                                            chatPresenter.getPredictCause(radioButton.getText().toString());
//                                            break;
//                                        case 3:
//                                            chatPresenter.getMultiple(LawPushApp.getResultId(), radioButton.getText().toString());
//                                            break;
//                                    }
//                                    MyToast.show(context, "请稍后。。。", 2000);
//                                    chatPresenter.choiceShowOnUI(radioButton.getText().toString());
//                                    choiceMsg.setChecked(isChecked);
//                                } else {
//                                    MyToast.show(context, "你已经选过了，不能再选了。", 2000);
//                                }
//                            }
//                        });
//
//                        similarCauseHolder.radioGroup.addView(radioButton);
//                        radioButtonIndex2++;
//                    }
//                } catch (Exception e) {
//                    MyLog.e("lawPush", " java.lang.NullPointerException similarCauseHolder.radioGroup");
//                }
                similarCauseHolder.radioGroup.removeAllViews();
                if (msg.getContent() != null)
                    similarCauseHolder.tipText.setText(msg.getContent());
                for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setButtonDrawable(R.drawable.my_check_box);
//                    checkBox.setId(Integer.parseInt("" + position + checkBoxIndexCaseCause));
                    checkBox.setChecked(choiceMsg.isChecked());
                    checkBox.setText(choiceMsg.getContent());
                    checkBox.setTextColor(checkBoxColor);
                    checkBox.setTextSize(textSize);
                    checkBox.setPadding(15, 0, 0, 6);  //调整文本的高度
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 15, 0, 15);
                    checkBox.setLayoutParams(layoutParams);
                    if (!checkBox.isChecked()) {
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                                switch (msg.getSIMILAR_QUESTION_TYPE()) {
                                    case 1:
                                        chatPresenter.getExample(compoundButton.getText().toString());
                                        break;
                                    case 2:
                                        chatPresenter.getPredictCause(compoundButton.getText().toString());
                                        break;
                                    case 3:
                                        chatPresenter.getMultiple(msg.getResultId(), compoundButton.getText().toString());
                                        break;
                                }
                                Msg msg1 = new Msg();
                                msg1.setContent(compoundButton.getText().toString());
                                msg1.setType(Msg.TYPE_SEND);
                                msgs.add(msg1);
                                notifyDataSetChanged();
                                listView.setSelection(msgs.size() - 1);
                                MyToast.show(context, "请稍等...", 2000);
                            }
                        });
                    } else {
                        checkBox.setClickable(false);
                    }

                    similarCauseHolder.radioGroup.addView(checkBox);

                    similarCauseHolder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (msg.getChoiceMsgs().length == 1) {
                                //跳横排界面，单选
                                Intent intent = new Intent(context, ChooseActivity.class);
                                intent.putExtra("title", msg.getContent());
                                intent.putExtra("isOne", true);
                                intent.putExtra("type", "choicesOne");
                                if (msg.getMotionsString() != null)
                                    intent.putExtra("motion", msg.getMotionsString());
                                ArrayList<String> stringArrayList = new ArrayList<>();
                                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                    stringArrayList.add(msg.getChoiceMsgs()[i].getContent());
                                }
                                intent.putStringArrayListExtra("tagArray", stringArrayList);
                                if (context instanceof ChatActivity) {
                                    ChatActivity activity = (ChatActivity) context;
                                    activity.startActivityForResult(intent, 16);
                                }
                            }
                            if (msg.getChoiceMsgs().length == 2) {
                                //跳横排界面，单选
                                Intent intent = new Intent(context, ChooseActivity.class);
                                intent.putExtra("title", msg.getContent());
                                intent.putExtra("isOne", true);
                                intent.putExtra("type", "choices");
                                if (msg.getMotionsString() != null)
                                    intent.putExtra("motion", msg.getMotionsString());
                                ArrayList<String> stringArrayList = new ArrayList<>();
                                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                    stringArrayList.add(msg.getChoiceMsgs()[i].getContent());
                                }
                                intent.putStringArrayListExtra("tagArray", stringArrayList);
                                if (context instanceof ChatActivity) {
                                    ChatActivity activity = (ChatActivity) context;
                                    activity.startActivityForResult(intent, 15);
                                }
                            }

                            if (msg.getChoiceMsgs().length > 2) {
                                //跳转竖排页面，而且是单选
                                Intent intent = new Intent(context, DialogActivity.class);
                                intent.putExtra("title", msg.getContent());
                                intent.putExtra("isOne", true);
                                intent.putExtra("type", "choices");
                                if (msg.getMotionsString() != null)
                                    intent.putExtra("motion", msg.getMotionsString());
                                ArrayList<String> stringList = new ArrayList<>();
                                for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                    stringList.add(msg.getChoiceMsgs()[i].getContent());
                                }
                                intent.putStringArrayListExtra("tagArray", stringList);
                                if (context instanceof ChatActivity) {
                                    ChatActivity activity = (ChatActivity) context;
                                    activity.startActivityForResult(intent, 14);
                                }
                            }
                        }
                    });

                }

                break;
            case Msg.HOT_CAUSE_TYPE:
//
                hotCauseHolder.radioGroup.removeAllViews();
                if (msg.getContent() != null)
                    hotCauseHolder.tipText.setText(msg.getContent());
                for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setButtonDrawable(R.drawable.my_check_box);
//                    checkBox.setId(Integer.parseInt("" + position + checkBoxIndexCaseCause));
                    checkBox.setChecked(choiceMsg.isChecked());
                    checkBox.setText(choiceMsg.getContent());
                    checkBox.setTextColor(checkBoxColor);
                    checkBox.setTextSize(textSize);
                    checkBox.setPadding(15, 0, 0, 6);  //调整文本的高度
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 15, 0, 15);
                    checkBox.setLayoutParams(layoutParams);
                    if (!checkBox.isChecked()) {
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                                switch (msg.getSIMILAR_QUESTION_TYPE()) {
                                    case 1:
                                        chatPresenter.getExample(compoundButton.getText().toString());
                                        break;
                                    case 2:
                                        chatPresenter.getPredictCause(compoundButton.getText().toString());
                                        break;
                                    case 3:
                                        chatPresenter.getMultiple(msg.getResultId(), compoundButton.getText().toString());
                                        break;
                                }
                                Msg msg1 = new Msg();
                                msg1.setContent(compoundButton.getText().toString());
                                msg1.setType(Msg.TYPE_SEND);
                                msgs.add(msg1);
                                notifyDataSetChanged();
                                listView.setSelection(msgs.size() - 1);
                                MyToast.show(context, "请稍等...", 2000);
                            }
                        });
                    } else {
                        checkBox.setClickable(false);
                    }

                    hotCauseHolder.radioGroup.addView(checkBox);

                    hotCauseHolder.layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //跳转竖排页面，而且是单选
                            Intent intent = new Intent(context, DialogActivity.class);
                            intent.putExtra("title", msg.getContent());
                            intent.putExtra("isOne", true);
                            intent.putExtra("type", "choices");
                            if (msg.getMotionsString() != null)
                                intent.putExtra("motion", msg.getMotionsString());
                            ArrayList<String> stringList = new ArrayList<>();
                            for (int i = 0; i < msg.getChoiceMsgs().length; i++) {
                                stringList.add(msg.getChoiceMsgs()[i].getContent());
                            }
                            intent.putStringArrayListExtra("tagArray", stringList);
                            if (context instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) context;
                                activity.startActivityForResult(intent, 20);
                            }
                        }
                    });

                }

                break;
            case Msg.TYPE_RESULT:
                resultHolder.itemLay.removeAllViews();
                resultHolder.tipText.setText(msg.getContent());
                final Intent intent = new Intent(context, StandardQuestionDialogActivity.class);
                if (msg.getLaw() != null) {
                    //法律法规
                    View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    final TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.falvfagui);
                    textType.setText("法律法规");
                    titleText.setText(msg.getLaw().getTitle());
                    contentText.setText(msg.getLaw().getContent());
                    resultHolder.itemLay.addView(subView);
                    intent.putExtra("title", msg.getLaw().getTitle());           //  法律标题
                    intent.putExtra("content", msg.getLaw().getContent());       //  法律 内容
                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //     skipToDetail(0);
                            intent.putExtra("type", 0);
                            context.startActivity(intent);

                        }
                    });
                    contentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    break;
                                case MotionEvent.ACTION_UP:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    // skipToLawDetail(msg.getLaw().getId(), msg.getLaw().getTitle(), Constant.LAW_INFO);
                                    intent.putExtra("type", 0);

                                    context.startActivity(intent);
                                    break;

                            }

                            return true;
                        }
                    });

                }
                if (msg.getPoint() != null) {
                    View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    final TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.sifaguandian);
                    textType.setText("司法观点");
                    titleText.setText(msg.getPoint().getTitle());
                    contentText.setText(msg.getPoint().getContent());
                    resultHolder.itemLay.addView(subView);
                    intent.putExtra("pointTitle", msg.getPoint().getTitle());  //    司法观点标题
                    intent.putExtra("pointContent", msg.getPoint().getContent());//  司法观点内容
                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //      skipToDetail(4);
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        }
                    });

                    contentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:

                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    break;
                                case MotionEvent.ACTION_UP:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    intent.putExtra("type", 1);
                                    context.startActivity(intent);
                                    break;

                            }
                            return true;
                        }
                    });
                }
                if (msg.getCaseInfo() != null) {
                    //相关案例
                    View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    final TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.jingxuananli);
                    textType.setText("精选案例");
                    titleText.setText(msg.getCaseInfo().getTitle());
                    contentText.setText(msg.getCaseInfo().getContent());
                    resultHolder.itemLay.addView(subView);
                    // TODO: 2017/3/13    添加 相关案例
                    intent.putExtra("caseInfoTitle", msg.getCaseInfo().getTitle());  //    相关案例标题
                    intent.putExtra("caseInfoContent", msg.getCaseInfo().getContent());//  相关案例内容
                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //   skipToDetail(3);
                            intent.putExtra("type", 2);
                            context.startActivity(intent);
                        }
                    });

                    contentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:

                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    break;
                                case MotionEvent.ACTION_UP:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    //  skipToLawDetail(msg.getCaseInfo().getId(), msg.getCaseInfo().getTitle(), Constant.LAW_CASE);
                                    intent.putExtra("type", 2);
                                    context.startActivity(intent);
                                    break;

                            }

                            return true;
                        }
                    });


                } else if (msg.getGuidanceCase() != null) {
                    //指导案例
                    View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                    ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                    TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                    TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                    final TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                    imageView.setImageResource(R.mipmap.jingxuananli);
                    textType.setText("精选案例");
                    titleText.setText(msg.getGuidanceCase().getTitle());
                    contentText.setText(msg.getGuidanceCase().getContent());
                    intent.putExtra("guidanceCaseTitle", msg.getGuidanceCase().getTitle());  //    指导案例标题
                    intent.putExtra("guidanceCaseContent", msg.getGuidanceCase().getContent());//  指导案例内容
                    resultHolder.itemLay.addView(subView);

                    textType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //    skipToDetail(3);
                            intent.putExtra("type", 2);
                            context.startActivity(intent);
                        }
                    });

                    contentText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_MOVE:

                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    break;
                                case MotionEvent.ACTION_UP:
                                    contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                    //    skipToLawDetail(msg.getGuidanceCase().getId(), msg.getGuidanceCase().getTitle(), Constant.LAW_GUIDE_CASE);
                                    intent.putExtra("type", 2);
                                    context.startActivity(intent);
                                    break;

                            }

                            return true;
                        }
                    });

                }
                resultHolder.resultLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, DataDetailActivity.class));
                    }
                });

                // TODO: 2017/3/13   点击跳转到全屏的实体问题对话框
                resultHolder.checkLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(intent);
                    }
                });

                break;
            case Msg.TYPE_MORE:
                final NeedMoreHolder finalNeedMoreHolder = needMoreHolder;
                needMoreHolder.needBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        changeButtonSelectorNeedMore(msg, finalNeedMoreHolder);
                        return false;
                    }
                });
                //需要
                needMoreHolder.needBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatPresenter.getPredictCause(chatPresenter.getQuestion());
                    }
                });

                needMoreHolder.noNeedBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        changeButtonSelectorNeedMore(msg, finalNeedMoreHolder);
                        return false;
                    }
                });
                //不需要
                needMoreHolder.noNeedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatPresenter.getTagStack().clear();
                        chatPresenter.showQR();
                    }
                });
                changeButtonSelectorNeedMore(msg, needMoreHolder);
                break;
            case Msg.TYPE_QR:
                qrHolder.qrImg.setImageBitmap(msg.getQrBitmap());
                break;
            case Msg.PROGRAMER_QUESTION:
                programQuestionHolder.linearLayout.removeAllViews();

                final Intent answer = new Intent(context, ProgramQuestionDialogActivity.class);
                if (msg.getProgramQuestionAnswer() != null) {
                    if (!msg.getProgramQuestionAnswer().equals("")) {

                        String programQuestionAnswer = msg.getProgramQuestionAnswer();
              //          String programQuestionAnswer = "您的问题属于@@没听懂、没听清1.png##机动车交通师傅纠纷,小法为你找到以下相似问题";;
                        String[] splitProgramQuestionAnswer = programQuestionAnswer.split("@@");

                        View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                        ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                        TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                        TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                        final TextView contentText = (TextView) subView.findViewById(R.id.id_content);
                        View subViewssss = subView.findViewById(R.id.id_sub_view);

                        if (splitProgramQuestionAnswer.length > 1) {
                            final String[] img = splitProgramQuestionAnswer[1].split("##");
                            if (splitProgramQuestionAnswer[0] != null && img.length == 1) {
                                contentText.setText(splitProgramQuestionAnswer[0]);
                                answer.putExtra("answer", splitProgramQuestionAnswer[0]);

                            }
                            if (img.length > 1 && img[1] != null) {
                                contentText.setText(splitProgramQuestionAnswer[0]+img[1]);
                                answer.putExtra("answer", splitProgramQuestionAnswer[0]+img[1]);

                            }
                            if (splitProgramQuestionAnswer[0] == null && img.length > 1 && img[1] != null) {
                                contentText.setText(splitProgramQuestionAnswer[0]+img[1]);
                                answer.putExtra("answer", splitProgramQuestionAnswer[0]+img[1]);
                            }

                            Log.e("ChatAdapter", img[0] + "/////>>>>//////" + splitProgramQuestionAnswer[0] + "////" + programQuestionAnswer.length());

                        }else{
                            contentText.setText(splitProgramQuestionAnswer[0]);
                            answer.putExtra("answer", splitProgramQuestionAnswer[0]);

                            Log.e("ChatAdapter", "/////>>>>//////" + splitProgramQuestionAnswer[0] + "////" + programQuestionAnswer.length());

                        }
//                        if (splitProgramQuestionAnswer.length>1){
//                            String[] splitImageAnswer = splitProgramQuestionAnswer[0].split("##");
//                            if (splitImageAnswer.length==2){
//                                contentText.setText(splitProgramQuestionAnswer[0]+splitImageAnswer[1]);
//                    //           answer.putExtra("answer", splitProgramQuestionAnswer[0]+splitImageAnswer[1]);
//
//                            }else{
//                                contentText.setText(splitProgramQuestionAnswer[0]);
//                          //      answer.putExtra("answer", splitProgramQuestionAnswer[0]);
//
//                            }
//                        }else{
//                            contentText.setText(programQuestionAnswer);
//                         //   answer.putExtra("answer", programQuestionAnswer);
//
//                        }


                        subViewssss.setVisibility(View.GONE);
                        answer.putExtra("isAnswer", true);
                        //      answer.putExtra("answer", msg.getProgramQuestionAnswer());
                        imageView.setImageResource(R.mipmap.falvfagui);
                        textType.setText("参考答案");
                        titleText.setText("");

                        programQuestionHolder.linearLayout.addView(subView);

                        textType.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                answer.putExtra("isAnswer", true);
                                context.startActivity(answer);
                            }
                        });
                        contentText.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                    case MotionEvent.ACTION_MOVE:

                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                        break;
                                    case MotionEvent.ACTION_CANCEL:
                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                        //    skipToLawDetail(msg.getGuidanceCase().getId(), msg.getGuidanceCase().getTitle(), Constant.LAW_GUIDE_CASE);
                                        answer.putExtra("isAnswer", true);

                                        context.startActivity(answer);
                                        break;

                                }

                                return true;
                            }
                        });

                    }
                }
                if (msg.getLawList() != null) {
                    for (int i = 0; i < msg.getLawList().size(); i++) {
                        //法律法规
                        final Law law = msg.getLawList().get(i);
                        View subView = LayoutInflater.from(context).inflate(R.layout.chat_result_item_view, null);
                        ImageView imageView = (ImageView) subView.findViewById(R.id.id_imgType);
                        TextView textType = (TextView) subView.findViewById(R.id.id_textType);
                        TextView titleText = (TextView) subView.findViewById(R.id.id_title);
                        final TextView contentText = (TextView) subView.findViewById(R.id.id_content);

                        imageView.setImageResource(R.mipmap.falvfagui);
                        textType.setText("法律法规");
                        titleText.setText(law.getTitle());
                        contentText.setText(law.getContent());
                        programQuestionHolder.linearLayout.addView(subView);
                        answer.putExtra("content", msg.getLawList().get(0).getContent());
                        answer.putExtra("title", msg.getLawList().get(0).getTitle());
                        textType.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                answer.putExtra("isAnswer", false);
                                context.startActivity(answer);
                            }
                        });
                        contentText.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                    case MotionEvent.ACTION_MOVE:

                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_select));
                                        break;
                                    case MotionEvent.ACTION_CANCEL:
                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        contentText.setTextColor(context.getResources().getColor(R.color.item_chat_normal));
                                        //    skipToLawDetail(msg.getGuidanceCase().getId(), msg.getGuidanceCase().getTitle(), Constant.LAW_GUIDE_CASE);
                                        answer.putExtra("isAnswer", false);
                                        context.startActivity(answer);
                                        break;
                                }

                                return true;
                            }
                        });
                    }
                }
                //  点击跳转全屏
                programQuestionHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        context.startActivity(answer);
                    }
                });


                break;
            case Msg.SATISFACTION:

                final SaticfactionHolder finalSaticfactionHolder = saticfactionHolder;
                saticfactionHolder.yesBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        changeButtonSelectorSatic(msg, finalSaticfactionHolder);
                        return false;
                    }
                });
                //满意
                saticfactionHolder.yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatPresenter.getTagStack().clear();
                        chatPresenter.showQR();
                    }
                });

                final SaticfactionHolder finalSaticfactionHolder1 = saticfactionHolder;
                saticfactionHolder.noBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        changeButtonSelectorSatic(msg, finalSaticfactionHolder1);
                        return false;
                    }
                });
                //不需要
                saticfactionHolder.noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatPresenter.getTagStack().clear();
                        chatPresenter.showQR();
                    }
                });
                changeButtonSelectorSatic(msg, saticfactionHolder);
                break;
//            case Msg.TIP_LAYOUT:
//                break;
            case Msg.CAUSE_DEMO_TYPE:
                causeDemoHolder.radioGroup.removeAllViews();
                if (msg.getContent() != null)
                    causeDemoHolder.tipText.setText(msg.getContent());
                int checkBoxIndex2 = 0;
                for (final Msg.ChoiceMsg choiceMsg : msg.getChoiceMsgs()) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setButtonDrawable(R.drawable.my_check_box);
                    checkBox.setId(Integer.parseInt("" + position + checkBoxIndex2));
                    checkBox.setChecked(choiceMsg.isChecked());
                    checkBox.setText(choiceMsg.getContent());
                    checkBox.setTextColor(checkBoxColor);
                    checkBox.setTextSize(textSize);
                    checkBox.setPadding(15, 0, 0, 6);  //调整文本的高度
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 15, 0, 15);
                    checkBox.setLayoutParams(layoutParams);
                    if (!choiceMsg.isChecked()) {
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                choiceMsg.setChecked(isChecked);
                                chatPresenter.getPredictCause(compoundButton.getText().toString());
                                Msg msg1 = new Msg();
                                msg1.setContent(compoundButton.getText().toString());
                                msg1.setType(Msg.TYPE_SEND);
                                msgs.add(msg1);
                                notifyDataSetChanged();
                                listView.setSelection(msgs.size() - 1);
                                MyToast.show(context, "请稍等...", 2000);
                            }
                        });
                    } else {
                        checkBox.setClickable(false);
                    }

                    if (checkBoxIndex2 >= 5 && !msg.isMore()) {
                        //tag数大于5而且未展示更多
                        //最多展示5个tag，多余的隐藏
                        checkBox.setVisibility(View.GONE);
                    }
                    causeDemoHolder.radioGroup.addView(checkBox);
                    checkBoxIndex2++;
                }
                if (msg.getChoiceMsgs().length > 5 && !msg.isMore()) {
                    //显示更多按钮区域
                    causeDemoHolder.moreRelay.setVisibility(View.VISIBLE);
                    causeDemoHolder.moreRelay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            msg.setMore(true);
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    causeDemoHolder.moreRelay.setVisibility(View.GONE);
                }
                break;


            case Msg.ANSWER_PICTURES:

                String answerContent = msg.getContent();
                //     String content = "您的问题属于@@没听懂、没听清1.png##机动车交通师傅纠纷,小法为你找到以下相似问题";
                String[] answerImg = answerContent.split("@@");
                if (answerImg.length > 1) {
                    final String[] img = answerImg[1].split("##");
                    if (answerImg[0] != null && img.length == 1) {
                        answerPicHolder.answerRightImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], answerPicHolder.answerRightImg);
                        answerPicHolder.answerLeftText.setText(answerImg[0]);
                        answerPicHolder.answerMidImg.setVisibility(View.GONE);
                        answerPicHolder.answerLeftImg.setVisibility(View.GONE);
                        answerPicHolder.answerRightImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                    if (img.length > 1 && img[1] != null) {
                        answerPicHolder.answerMidImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], answerPicHolder.answerMidImg);
                        answerPicHolder.answerLeftText.setText(answerImg[0]);
                        answerPicHolder.answerRightImg.setVisibility(View.GONE);
                        answerPicHolder.answerLeftImg.setVisibility(View.GONE);
                        answerPicHolder.answerRightText.setText(img[1]);
                        answerPicHolder.answerMidImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }
                    if (answerImg[0] == null && img.length > 1 && img[1] != null) {
                        answerPicHolder.answerLeftImg.setVisibility(View.VISIBLE);
                        loadImage(HttpHelper.BASEEMOJIUrl + img[0], answerPicHolder.answerLeftImg);
                        answerPicHolder.answerLeftText.setText(answerImg[0]);
                        answerPicHolder.answerRightImg.setVisibility(View.GONE);
                        answerPicHolder.answerMidImg.setVisibility(View.GONE);
                        answerPicHolder.answerRightText.setText(img[1]);
                        answerPicHolder.answerLeftImg.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        createDialog(HttpHelper.BASEEMOJIUrl + img[0]);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        destroyDialog();
                                        break;
                                }
                                return true;
                            }
                        });
                    }

                    Log.e("ChatAdapter", img[0] + "////////////////" + answerImg[0] + "////" + answerContent.length());

                    if (answerContent.length() > 38) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                        answerPicHolder.titleLayout.setLayoutParams(layoutParams);
                    }
                } else {
                    answerPicHolder.answerLeftText.setText(answerContent);
                    answerPicHolder.answerLeftImg.setVisibility(View.GONE);
                    answerPicHolder.answerMidImg.setVisibility(View.GONE);
                    answerPicHolder.answerRightImg.setVisibility(View.GONE);
                    answerPicHolder.answerLeftText.measure(0, 0);
                    int height = answerPicHolder.answerLeftText.getMeasuredHeight();
                    int lineCount = answerPicHolder.answerLeftText.getLineCount();
                    int height1 = answerPicHolder.answerLeftText.getHeight();
                    Log.e("ChatAdapter", height + "////////////////" + lineCount + "//////" + height1);
                    if (answerContent.length() > 38) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 130);
                        answerPicHolder.titleLayout.setLayoutParams(layoutParams);
                    }
                }
                if (msg.getPictures() != null && msg.getPictures().size() > 0) {
                    ArrayList<String> pictures = msg.getPictures();

                    AnswerPicAdapter adapter = new AnswerPicAdapter(pictures, context);
                    answerPicHolder.gridView.setAdapter(adapter);
                    LinearLayout.LayoutParams layoutParams = null;
                    if (msg.getPictures().size() == 1) {
                        layoutParams = new LinearLayout.LayoutParams(1118, 866);
                        answerPicHolder.gridView.setNumColumns(1);
                    } else if (msg.getPictures().size() == 2) {
                        layoutParams = new LinearLayout.LayoutParams(1118, 420);
                        answerPicHolder.gridView.setNumColumns(2);
                    } else if (msg.getPictures().size() == 3) {
                        layoutParams = new LinearLayout.LayoutParams(1118, 300);
                        answerPicHolder.gridView.setNumColumns(3);
                    } else if (msg.getPictures().size() == 4) {
                        layoutParams = new LinearLayout.LayoutParams(1118, 866);
                        answerPicHolder.gridView.setNumColumns(2);
                    } else if (msg.getPictures().size() < 7) {
                        layoutParams = new LinearLayout.LayoutParams(1118, 600);
                    } else {
                        layoutParams = new LinearLayout.LayoutParams(1118, 866);

                    }

                    answerPicHolder.gridView.setLayoutParams(layoutParams);
                }

                break;
        }

        return convertView;
    }

    class ReceiveHolder {
        TextView leftText;
        ImageView leftImg;
    }

    /**
     * 只有answer
     */
    class ChatAnswerHolder {
        TextView leftText;
        ImageView leftImg;
    }

    class CHAT_CHATHolder {

        TextView answerLeftText;
        TextView answerRightText;
        ImageView answerLeftImg;
        ImageView answerMidImg;
        ImageView answerRightImg;
        LinearLayout titleLayout;
    }

    class CHAT_WEATHERHolder {
        TextView leftText;
    }

    class ChatLawsHolder {
        TextView tipText;
        ListView mLawList;
        LinearLayout mLinearLayout;
    }

    class SendHolder {
        TextView rightText;
        LinearLayout rightBackGround;
    }

    class CauseHolder {
        LinearLayout layout;

        TextView tipText;
        RadioGroup radioGroup;
        RelativeLayout moreRelay;
    }

    class CauseDemoHolder {
        TextView tipText;
        RadioGroup radioGroup;
        RelativeLayout moreRelay;
    }

    class TagHolder {
        LinearLayout layout;
        TextView tipText;
        RadioGroup radioGroup;
        Button nextBtn;
        RelativeLayout moreRelay;
    }

    class SimilarCauseHolder {
        LinearLayout layout;
        TextView tipText;
        RadioGroup radioGroup;
    }

    class HotCauseHolder {
        LinearLayout layout;
        TextView tipText;
        RadioGroup radioGroup;
    }

    class ResultHolder {
        TextView tipText;
        LinearLayout itemLay;
        RelativeLayout resultLay;
        LinearLayout checkLayout;
    }

    class NeedMoreHolder {
        Button needBtn, noNeedBtn;
    }

    class QRHolder {
        ImageView qrImg;
    }

    class ProgramQuestionHolder {
        LinearLayout linearLayout;
    }

    class SaticfactionHolder {
        Button yesBtn, noBtn;
    }

    class TipHolder {
    }

    class AnswerPicHolder {

        GridView gridView;
        TextView answerRightText;
        ImageView answerMidImg;
        ImageView answerRightImg;
        TextView answerLeftText;
        ImageView answerLeftImg;
        LinearLayout titleLayout;
    }

    /**
     * 更新 "继续追问"、"查看结果" 按钮状态
     * 更新按钮状态
     */
    private void changeButtonSelectorTagHolder(Msg msg, TagHolder tagHolder) {
        if (msg.getFlag() == 1) {
            tagHolder.nextBtn.setBackgroundColor(Color.parseColor("#227CCE"));
            tagHolder.nextBtn.setTextColor(Color.parseColor("#ffffff"));
//            tagHolder.resultBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
//            tagHolder.resultBtn.setTextColor(Color.parseColor("#111111"));
        } else if (msg.getFlag() == 2) {
//            tagHolder.resultBtn.setBackgroundColor(Color.parseColor("#227CCE"));
//            tagHolder.resultBtn.setTextColor(Color.parseColor("#eeeeee"));
            tagHolder.nextBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            tagHolder.nextBtn.setTextColor(Color.parseColor("#111111"));
        } else {
//            tagHolder.resultBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
//            tagHolder.resultBtn.setTextColor(Color.parseColor("#111111"));
            tagHolder.nextBtn.setTextColor(Color.parseColor("#111111"));
            tagHolder.nextBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
    }

    /**
     * 更新 "需要"、"不需要" 按钮状态
     */
    private void changeButtonSelectorNeedMore(Msg msg, NeedMoreHolder needMoreHolder) {
        if (msg.getFlag() == 1) {
            needMoreHolder.needBtn.setBackgroundColor(Color.parseColor("#227CCE"));
            needMoreHolder.needBtn.setTextColor(Color.parseColor("#ffffff"));
            needMoreHolder.noNeedBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            needMoreHolder.noNeedBtn.setTextColor(Color.parseColor("#111111"));
        } else if (msg.getFlag() == 2) {
            needMoreHolder.needBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            needMoreHolder.needBtn.setTextColor(Color.parseColor("#111111"));
            needMoreHolder.noNeedBtn.setBackgroundColor(Color.parseColor("#227CCE"));
            needMoreHolder.noNeedBtn.setTextColor(Color.parseColor("#eeeeee"));
        } else {
            needMoreHolder.needBtn.setTextColor(Color.parseColor("#111111"));
            needMoreHolder.needBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            needMoreHolder.noNeedBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            needMoreHolder.noNeedBtn.setTextColor(Color.parseColor("#111111"));
        }
    }

    /**
     * 更新 "满意"、"不满意" 按钮状态
     */
    private void changeButtonSelectorSatic(Msg msg, SaticfactionHolder saticfactionHolder) {
        if (msg.getFlag() == 1) {
            saticfactionHolder.yesBtn.setBackgroundColor(Color.parseColor("#227CCE"));
            saticfactionHolder.yesBtn.setTextColor(Color.parseColor("#ffffff"));
            saticfactionHolder.noBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            saticfactionHolder.noBtn.setTextColor(Color.parseColor("#111111"));
        } else if (msg.getFlag() == 2) {
            saticfactionHolder.yesBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            saticfactionHolder.yesBtn.setTextColor(Color.parseColor("#111111"));
            saticfactionHolder.noBtn.setBackgroundColor(Color.parseColor("#227CCE"));
            saticfactionHolder.noBtn.setTextColor(Color.parseColor("#eeeeee"));
        } else {
            saticfactionHolder.yesBtn.setTextColor(Color.parseColor("#111111"));
            saticfactionHolder.yesBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            saticfactionHolder.noBtn.setBackgroundColor(Color.parseColor("#eeeeee"));
            saticfactionHolder.noBtn.setTextColor(Color.parseColor("#111111"));
        }
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

//    /**
//     * 测量 listView 子控件的高度
//     *
//     * @param listView
//     */
//    public void fixListViewHeight(ListView listView) {
//
//        // 如果没有设置数据适配器，则ListView没有子项，返回。
//        ListAdapter listAdapter = listView.getAdapter();
//
//        if (listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            listItem.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        // TODO: 2016/12/21   数据源是paeams的 1.5 倍  就是 数据源为3 实际上现实的是2个
//        params.height = (int) (((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))) * 1.4);
//
//        //这是正常的适配
//        //   params.height = ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));
//
//        //   params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)))*1.5);
//        listView.setLayoutParams(params);
//
//    }

    /**
     * 默认宽和高的加载图片
     *
     * @param o  可以是  url 可以是 Drawable  可以是文件
     * @param iv 需要显示的控件
     */
    private void loadImage(Object o, ImageView iv) {
        Glide.with(context)   //使得glide更容易使用，因为能接收context，activity，fragment对象
                .load(o)
                .priority(Priority.HIGH)    //设置优先级
                //    .placeholder(R.drawable.load)    //加载中显示的图片
                .error(R.drawable.fail)//加载失败显示的图片
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)

                .skipMemoryCache(true)
                .fitCenter()    //缩放图像，整个显示在控件，尽可能的填满
                .into(iv);
    }

    /**
     * 有默认宽高的Glide加载图片
     *
     * @param o      可以是  url 可以是 Drawable  可以是文件
     * @param iv     需要显示的控件
     * @param width  宽
     * @param height 高
     */
    private void loadImage(Object o, ImageView iv, int width, int height) {
        Glide.with(context)   //使得glide更容易使用，因为能接收context，activity，fragment对象
                .load(o)
                .priority(Priority.HIGH)    //设置优先级
//                .placeholder(R.drawable.load)    //加载中显示的图片
                .error(R.drawable.y)//加载失败显示的图片
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()    //缩放图像，整个显示在控件，尽可能的填满
                .into(iv);
    }

    private Dialog guideDialog;

    private void createDialog(Object o) {
        if (guideDialog != null && guideDialog.isShowing()) {
            return;
        }
        guideDialog = new Dialog(context, R.style.loading_dialog2);
        View view = LayoutInflater.from(context).inflate(R.layout.img_dialog, null);
        ImageView mDialogImg = (ImageView) view.findViewById(R.id.Dialog_img);
        loadImage(o, mDialogImg);
        guideDialog.setContentView(view);
        guideDialog.setCanceledOnTouchOutside(true);
        guideDialog.show();
        WindowManager.LayoutParams params = guideDialog.getWindow().getAttributes();
        params.width = 250;
        params.height = 250;
        guideDialog.getWindow().setAttributes(params);
        mDialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideDialog.dismiss();
            }
        });
    }

    private void destroyDialog() {
        if (guideDialog != null && guideDialog.isShowing()) {
            guideDialog.dismiss();
            guideDialog = null;
        }
    }
}
