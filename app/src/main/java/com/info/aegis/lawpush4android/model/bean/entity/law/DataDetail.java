package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.List;

/**
 * Created by 申富生 on 2016/12/22.
 * Description :
 */

public class DataDetail {
    //答案
    private Answer answer;
    //案由
    private List<CaseCause> cases;
    // 断言
    private List<Claim> claims;
    //证据
    private List<Evidence> evidences;

    private List<Focus> focus;
    //指导案例
    private List<GuidanceCase> guides;
    private List<LawTag> law_tags;
    //法条信息
    private List<Law> laws;
    //法律观点
    private List<JudicialPoint> points;
    //排斥原因
    private List<RejectReason> reject_cause;


    //诉讼流程
    private LitigationFlow litigationFlowFlow;
    //非诉讼流程
    private UnLitigationFlow unLitigationFlow;

    private UserQuestion userQuestion;

    public UserQuestion getUserQuestion() {
        return userQuestion;
    }

    public void setUserQuestion(UserQuestion userQuestion) {
        this.userQuestion = userQuestion;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public List<CaseCause> getCases() {
        return cases;
    }

    public void setCases(List<CaseCause> cases) {
        this.cases = cases;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }

    public List<Focus> getFocus() {
        return focus;
    }

    public void setFocus(List<Focus> focus) {
        this.focus = focus;
    }

    public List<GuidanceCase> getGuides() {
        return guides;
    }

    public void setGuides(List<GuidanceCase> guides) {
        this.guides = guides;
    }

    public List<LawTag> getLaw_tags() {
        return law_tags;
    }

    public void setLaw_tags(List<LawTag> law_tags) {
        this.law_tags = law_tags;
    }

    public List<Law> getLaws() {
        return laws;
    }

    public void setLaws(List<Law> laws) {
        this.laws = laws;
    }

    public List<JudicialPoint> getPoints() {
        return points;
    }

    public void setPoints(List<JudicialPoint> points) {
        this.points = points;
    }

    public List<RejectReason> getReject_cause() {
        return reject_cause;
    }

    public void setReject_cause(List<RejectReason> reject_cause) {
        this.reject_cause = reject_cause;
    }
    public LitigationFlow getLitigationFlowFlow() {
        return litigationFlowFlow;
    }

    public void setLitigationFlowFlow(LitigationFlow litigationFlowFlow) {
        this.litigationFlowFlow = litigationFlowFlow;
    }

    public UnLitigationFlow getUnLitigationFlow() {
        return unLitigationFlow;
    }

    public void setUnLitigationFlow(UnLitigationFlow unLitigationFlow) {
        this.unLitigationFlow = unLitigationFlow;
    }

    @Override
    public String toString() {
        return "DataDetail{" +
                "answer=" + answer +
                ", cases=" + cases +
                ", claims=" + claims +
                ", evidences=" + evidences +
                ", focus=" + focus +
                ", guides=" + guides +
                ", law_tags=" + law_tags +
                ", laws=" + laws +
                ", points=" + points +
                ", reject_cause=" + reject_cause +
                ", userQuestion=" + userQuestion +
                '}';
    }
}
