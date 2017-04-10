package com.info.aegis.lawpush4android.model.bean.entity.law;


import java.util.Date;

/**
 * Created by Administrator on 2016/10/31.
 * 指导案例
 */
public class GuidanceCase {
    private int proceeding;
    //审判
    private String judgeResult;
    private int score;
    private int court;

    private String title;  //解析需要的字段
    //案情
    private String content;
    private String[] relation;
    private int caseCauseId;
    //裁判要旨
    private String gist;//解析需要的字段

    private String caseCause;
    private String caseNumber;
    //专家评审
    private String comment;
    private Date decideTime;

    private String id;          // 解析需要的字段

    private String[] tag;




    public int getProceeding() {
        return proceeding;
    }

    public void setProceeding(int proceeding) {
        this.proceeding = proceeding;
    }

    public String getJudgeResult() {
        return judgeResult;
    }

    public void setJudgeResult(String judgeResult) {
        this.judgeResult = judgeResult;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCourt() {
        return court;
    }

    public void setCourt(int court) {
        this.court = court;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getRelation() {
        return relation;
    }

    public void setRelation(String[] relation) {
        this.relation = relation;
    }

    public int getCaseCauseId() {
        return caseCauseId;
    }

    public void setCaseCauseId(int caseCauseId) {
        this.caseCauseId = caseCauseId;
    }

    public String getGist() {
        return gist;
    }

    public void setGist(String gist) {
        this.gist = gist;
    }

    public String getCaseCause() {
        return caseCause;
    }

    public void setCaseCause(String caseCause) {
        this.caseCause = caseCause;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDecideTime() {
        return decideTime;
    }

    public void setDecideTime(Date decideTime) {
        this.decideTime = decideTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "GuidanceCase{" +
                "id='" + id + '\'' +
                ", gist='" + gist + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
