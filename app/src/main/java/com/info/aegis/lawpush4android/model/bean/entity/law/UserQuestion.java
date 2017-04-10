package com.info.aegis.lawpush4android.model.bean.entity.law;


import java.util.Date;
import java.util.List;

public class UserQuestion {

    //问题内容
    private String title;
    //问题类型，0-程序问题，1-实体问题
    private Integer type;
    //案由
    private CaseCause caseCause;
    //法条
    private List<Law> laws;
    private String answer;
    private List<CaseInfo> cases;


    private String caseName;

    private List<Claim> claims;
    private String id;
    private List<String> keywords;
    private String lawKeywords;
    private List<LawTag> lawTagNames;
    private List<String> pointIds;
    private List<RejectReason> rejectReasons;
    private String sessionId;
    private List<String> similarQuestionIds;
    private Date time;
    private List<String> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }


    public List<LawTag> getLawTagNames() {
        return lawTagNames;
    }

    public void setLawTagNames(List<LawTag> lawTagNames) {
        this.lawTagNames = lawTagNames;
    }

    public String getLawKeywords() {
        return lawKeywords;
    }

    public void setLawKeywords(String law_tags) {
        lawKeywords = law_tags;
    }

    public List<RejectReason> getRejectReasons() {
        return rejectReasons;
    }

    public void setRejectReasons(List<RejectReason> reject_reasons) {
        rejectReasons = reject_reasons;
    }

    public List<String> getPointIds() {
        return pointIds;
    }

    public void setPointIds(List<String> relate_point_ids) {
        pointIds = relate_point_ids;
    }

    public List<String> getSimilarQuestionIds() {
        return similarQuestionIds;
    }

    public void setSimilarQuestionIds(List<String> similar_question_ids) {
        similarQuestionIds = similar_question_ids;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Law> getLaws() {
        return laws;
    }

    public void setLaws(List<Law> laws) {
        this.laws = laws;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<CaseInfo> getCases() {
        return cases;
    }

    public void setCases(List<CaseInfo> cases) {
        this.cases = cases;
    }

    public CaseCause getCaseCause() {
        return caseCause;
    }

    public void setCaseCause(CaseCause caseCause) {
        this.caseCause = caseCause;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

}
