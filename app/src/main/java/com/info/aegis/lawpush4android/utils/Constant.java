package com.info.aegis.lawpush4android.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.aegis.lawpush4android.LawPushApp;
import com.info.aegis.lawpush4android.R;
import com.info.aegis.lawpush4android.model.bean.Msg;
import com.info.aegis.lawpush4android.model.bean.ScenesBean;
import com.info.aegis.lawpush4android.presenter.adapter.ChatAdapter;
import com.info.aegis.lawpush4android.view.iml.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import aegis.aegisxunfei.MyLawPushApp;

/**
 * 作者：jksfood on 2017/1/12 15:13
 */

public class Constant {

    public static final int NEW_MESSAGE = 1;
    public static final int SON = 2;
    public static final int MORE = 3;
    public static final int RESULT = 4;
    public static final int NEW = 5;
    public static final int BACK = 6;
    public static final int HOME = 7;
    public static final int FATHER = 8;
    public static final int LAW = 9;
    public static final int GUIDE = 10;
    public static final int POINT = 11;
    public static final int CASE = 12;
    //用户说出更多时，展开剩余标签
    public static final int SAY_MORE = 13;
    public static final int HELP = 14;
    public static final int DETAIL = 15;
    public static final int CHOICE_SEND = 17;
    public static final int CHAT_TYPE =18;
    //语音朗读
    public static final int VOICE_READ =118;

    /**
     * 连续两次救场后，或连续三次随聊后提示的类型
     */
    public static final int CHAT_SAVE_SCENE = 19;

    /**
     * 相关案例
     */
    public static int LAW_CASE = 1;

    /**
     * 法律知识
     */
    public static int LAW_INFO = 2;

    /**
     * 司法观点
     */
    public static int LAW_POINTS = 3;

    /**
     * 指导案例
     */
    public static int LAW_GUIDE_CASE = 4;

    /**
     * 参考答案
     */
    public static int REFERENCE_ANSWER = 5;

    /**
     * 接收服务器推送来的消息
     */
    public static final int RECEICE_SERVER_MESSAGE = 16;


    /**
     * 1 为非标问题
     * 其它为标准问题
     */
    public static int HUMAN_CHAT = 0;



    /**
     * 随机返回 机器人动作 问候语
     * @return
     */
    public static String randomRobbotAction(){
        String [] stringArray = {"OK","好的"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 随机返回 程序引导 问候语
     * @return
     */
    public static String randomProgramQuestion(){
        String [] stringArray = {"欢迎来到诉讼服务中心，请问你具体要办理什么业务？","欢迎您的到来，请您详细叙说您的问题。","很荣幸为您服务，请问您有什么具体问题？","请您详细说说您的要求，我将竭诚为您服务。","你具体要办什么业务，我将尽力帮你解答。"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 随机返回 法律法规 问候语
     * @return
     */
    public static String randomLawInfo(){
        String [] stringArray = {"请问你要查询什么法律法规，别看我小我懂得蛮多的。","欢迎欢迎，你要查询什么法律法规？","请问你要查询什么法条，我努力给你找找啊。","你要查询什么法律法规，我马上找给你看看。","你要查什么法条啊，我肚子里有好多货呢。","虽然我知道很多法律法规，但是我不知道你的想法，你要查什么法条？","你查询哪方面的法律法规呢，小法会知无不尽的。","小法就是为您提供法律服务而生的，您具体要查什么法条，尽管开口。"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 随机返回 法律问答 问候语
     * @return
     */
    public static String randomLawQA(){
        String [] stringArray = {"你好，请问你有什么法律问题？","你好，你要咨询什么类型的法律问题啊？","你好，请您告诉我具体的法律问题，我将知无不言。","好呀，很荣幸能为您服务，你能说具体些吗？","好的，请您详细叙说，我竭尽全力帮您解答。"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 本地救场
     * @return
     */
    public static String randomChat_Chat(){
        String [] stringArray = {"暂时还不知道","暂时还不太明白您的意思 "};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    public static String randomChatUI(){
        String [] stringArray = {"你好，让我们聊点轻松的吧~","咱们来欢快的聊聊天吧 "};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    private static CountDownTimer countDownTimer;
    /**
     * 开始计时
     */
    public static void startCountTime(final ChatActivity chatActivity, final ChatAdapter chatAdapter, final ArrayList<Msg> msgList) {
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                MyLog.e("lawPush","onTick-----"+millisUntilFinished);
                if (millisUntilFinished > 20000 && millisUntilFinished < 21000){
                    String content = randomHunmanHelp1();
                    Msg msg = new Msg(content, Msg.CHAT_SOS_TEXT);
                    msgList.add(msg);
                    chatAdapter.notifyDataSetChanged();
                    chatActivity.msgListView.setSelection(msgList.size() -1);
                    chatActivity.robbotRead(content);
                    return;
                }
                if (millisUntilFinished > 10000 && millisUntilFinished < 11000){
                    String content = randomHunmanHelp2();
                    Msg msg = new Msg(content, Msg.CHAT_SOS_TEXT);
                    msgList.add(msg);
                    chatAdapter.notifyDataSetChanged();
                    chatActivity.msgListView.setSelection(msgList.size() -1);
                    chatActivity.robbotRead(content);
                    return;
                }

            }
            @Override
            public void onFinish() {
                String content = randomHunmanHelp3();
                Msg msg = new Msg(content, Msg.CHAT_SOS_TEXT);
                msgList.add(msg);
                chatAdapter.notifyDataSetChanged();
                chatActivity.msgListView.setSelection(msgList.size() -1);
                chatActivity.robbotRead(content);
                return;

            }
        };
        countDownTimer.start();

    }

    /**
     * 停止计时
     */
    public static void stopCountTime() {
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    /**
     * 人工救场语1
     * @return
     */
    public static String randomHunmanHelp1(){
        String [] stringArray = {"这个问题有点难，我需要多思考一下","嗯，你这个问题问的好有水平，让我想想看哈",
                "我明白了，让我思考一会儿","我明白你的问题了，容我的小脑袋思考一下","好像是个蛮有挑战性的问题，我来想想看"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 人工救场语2
     * @return
     */
    public static String randomHunmanHelp2(){
        String [] stringArray = {"脑子突然有点不太好使了，再等我一小会","今天脑子有点晕，让我多思考一会",
                "我有点慌了，这个问题好像蛮难的，让我再想想","我正在我的法律大脑里面查找答案，请给我一点时间"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 人工救场语2
     * @return
     */
    public static String randomHunmanHelp3(){
        String [] stringArray = {"对不起，我暂时回答不了你这个问题","很抱歉，我没有找到太满意的答案，我会记录下来后面问问我的法官爸爸们",
                "非常抱歉，我可能暂时还不会回答你的这个问题","真的不好意思，我可能今天脑子用太多了，不好使了，不过我记下来了，后面就会了"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 开启演示模式或者上帝模式后，传送的文本内容
     * @param content
     * @param type
     * @param isSendByUser
     * @param itemType      0 讯飞，1 用户说话  ， 2 cause接口返回   ,3  结果页，    4多轮问答模式中
     */
    public static void setGodTextContent(String content,int type, boolean isSendByUser,int itemType){
        String deviceId = MyLawPushApp.getCurrentDeviceId();//设备id
        //content    文本内容
        //isSendByUser    是用户说的内容，还是服务端返回的
        //type      0---聊天文本， 1---演示模式，2----上帝模式
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId",deviceId);
            String temp = "";
            switch (itemType){
                case 0:
                    temp = "来自本地救场答案";
                    break;
                case 1:
                    temp = "来自用户说话";
                    break;
                case 2:
                    temp = "服务端对用户提问的初步解析";
                    break;
                case 3:
                    temp = "来自结果页";
                    break;
                case 4:
                    temp = "来自多轮问答模式中的";
                    break;
            }
            jsonObject.put("text","{"+temp+":"+content+"}");
            jsonObject.put("sendByUser",isSendByUser);
            jsonObject.put("type",type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LawPushApp.setCurrentShowModeUpContent(jsonObject.toString());
    }

    private static int compareStringSimilar(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public static float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compareStringSimilar(str, target) / Math.max(str.length(), target.length());
    }

    //判断是不是有意义的一句话
    public static boolean isMeaningFul(String result){
        if (result.length() < 2){
            return false;
        }
        return true;
    }



    //┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃ 　
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//┃　　　┃  神兽保佑　　　　　　　　
//┃　　　┃  代码无BUG！
//┃　　　┗━━━┓
//┃　　　　　　　┣┓
//┃　　　　　　　┏┛
//┗┓┓┏━┳┓┏┛
// ┃┫┫　┃┫┫
// ┗┻┛　┗┻┛
}
