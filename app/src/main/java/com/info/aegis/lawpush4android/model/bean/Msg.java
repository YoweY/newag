package com.info.aegis.lawpush4android.model.bean;

import android.graphics.Bitmap;

import com.info.aegis.lawpush4android.model.bean.entity.law.CaseInfo;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */

public class Msg {
    /**
     * receive msg
     */
    public static final int TYPE_RECEIVED = 0;
    /**
     * send msg
     */
    public static final int TYPE_SEND = 1;
    /**
     * cause
     */
    public static final int TYPE_CAUSE = 2;
    /**
     * tag
     */
    public static final int TYPE_TAG = 3;
    /**
     * tag2
     */
    public static final int SIMILAR_CAUSE_TYPE = 4;

    public static final int HOT_CAUSE_TYPE = 16;
    /**
     * result
     */
    public static final int TYPE_RESULT = 5;
    /**
     * more
     */
    public static final int TYPE_MORE = 6;
    /**
     * qr
     */
    public static final int TYPE_QR = 7;
    /**
     * program problem question
     */
    public static final int PROGRAMER_QUESTION = 8;
    /**
     * tip
     */
    public static final int TIP_LAYOUT = 9;
    /**
     * satisfaction
     */
    public static final int SATISFACTION = 10;
    /**
     * cause demo
     */
    public static final int CAUSE_DEMO_TYPE = 11;
    /**
     * 随便聊聊
     */
    public static final int CHAT_CHAT = 12;
    /**
     * 法律问答
     */
    public static final int CHAT_LAWS=13;

    /**
     * 服务器只返回了answer
     */
    public static final int CHAT_SOS_TEXT = 14;

    /**
     * 连续两次救场后，或连续三次随聊后提示的类型
     */
    public static final int CHAT_SAVE_SCENE = 15;

    //天气
    public static final int CHAT_WEATHER = 15;
    /**
     * 参考答案带图片的类型
     */
    public static final int ANSWER_PICTURES=17;


    /**
     * 是否已经展示了更多
     */
    private boolean isMore = false;


    private String content;
    private int type;
    private ChoiceMsg[] choiceMsgs;
    private int curPage = 0;


    //法律知识
    private Law law;
    //司法观点
    private JudicialPoint point;
    //案例
    private CaseInfo caseInfo;
    //指导案例
    private GuidanceCase guidanceCase;

    /**
     * 二维码图片
     */
    private Bitmap qrBitmap;

    /**
     * 一组相关法律法规
     */
    private List<Law> lawList;


    private ArrayList<String> pictures;

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> picture) {
        this.pictures = picture;
    }


    /**
     * 相似问题列表调用不同接口
     * example  1
     * cause 2
     */
    private int SIMILAR_QUESTION_TYPE;

    private String resultId;

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public int getSIMILAR_QUESTION_TYPE() {
        return SIMILAR_QUESTION_TYPE;
    }

    public void setSIMILAR_QUESTION_TYPE(int SIMILAR_QUESTION_TYPE) {
        this.SIMILAR_QUESTION_TYPE = SIMILAR_QUESTION_TYPE;
    }

    public List<Law> getLawList() {
        return lawList;
    }

    public void setLawList(List<Law> lawList) {
        this.lawList = lawList;
    }

    private String programQuestionAnswer;

    public String getProgramQuestionAnswer() {
        return programQuestionAnswer;
    }

    public void setProgramQuestionAnswer(String programQuestionAnswer) {
        this.programQuestionAnswer = programQuestionAnswer;
    }

    public Bitmap getQrBitmap() {
        return qrBitmap;
    }

    public void setQrBitmap(Bitmap qrBitmap) {
        this.qrBitmap = qrBitmap;
    }

    public Msg() {

    }

    private String motionsString;

    public String getMotionsString() {
        return motionsString;
    }

    public void setMotionsString(String motionsString) {
        this.motionsString = motionsString;
    }

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    //记录两个按钮选择标志
    //1为左边，2为右边
    private int flag = 0;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Law getLaw() {
        return law;
    }

    public void setLaw(Law law) {
        this.law = law;
    }

    public JudicialPoint getPoint() {
        return point;
    }

    public void setPoint(JudicialPoint point) {
        this.point = point;
    }

    public ChoiceMsg[] getChoiceMsgs() {
        return choiceMsgs;
    }

    public void setChoiceMsgs(ChoiceMsg[] choiceMsgs) {
        this.choiceMsgs = choiceMsgs;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public CaseInfo getCaseInfo() {
        return caseInfo;
    }

    public void setCaseInfo(CaseInfo caseInfo) {
        this.caseInfo = caseInfo;
    }

    public GuidanceCase getGuidanceCase() {
        return guidanceCase;
    }

    public void setGuidanceCase(GuidanceCase guidanceCase) {
        this.guidanceCase = guidanceCase;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public  static class ChoiceMsg {

        private String content;//标签或案由的中文名
        private boolean isChecked = false;//标签是否被选中
        private int type;//* 叶子标签为1，* 父标签为0
        private String enName;  //标签的英文名
        private String caseCauseId; //案由的id

        public String getCaseCauseId() {
            return caseCauseId;
        }

        public void setCaseCauseId(String caseCauseId) {
            this.caseCauseId = caseCauseId;
        }

        public String getEnName() {
            return enName;
        }

        public void setEnName(String enName) {
            this.enName = enName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "{" +
                    "content='" + content + '\'' +
                    ", isChecked=" + isChecked +
                    ", type=" + type +
                    ", enName='" + enName + '\'' +
                    ", caseCauseId=" + caseCauseId +
                    '}';
        }
    }
}
