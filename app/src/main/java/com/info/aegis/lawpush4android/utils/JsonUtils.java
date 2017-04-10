package com.info.aegis.lawpush4android.utils;



import com.info.aegis.lawpush4android.model.bean.entity.law.CaseCause;
import com.info.aegis.lawpush4android.model.bean.entity.law.Claim;
import com.info.aegis.lawpush4android.model.bean.entity.law.DataDetail;
import com.info.aegis.lawpush4android.model.bean.entity.law.Evidence;
import com.info.aegis.lawpush4android.model.bean.entity.law.FlowStep;
import com.info.aegis.lawpush4android.model.bean.entity.law.FlowSubStep;
import com.info.aegis.lawpush4android.model.bean.entity.law.Focus;
import com.info.aegis.lawpush4android.model.bean.entity.law.GuidanceCase;
import com.info.aegis.lawpush4android.model.bean.entity.law.JudicialPoint;
import com.info.aegis.lawpush4android.model.bean.entity.law.Law;
import com.info.aegis.lawpush4android.model.bean.entity.law.LawTag;
import com.info.aegis.lawpush4android.model.bean.entity.law.LitigationFlow;
import com.info.aegis.lawpush4android.model.bean.entity.law.RejectReason;
import com.info.aegis.lawpush4android.model.bean.entity.law.Template;
import com.info.aegis.lawpush4android.model.bean.entity.law.UnLitigationFlow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by 申富生 on 2016/12/21.
 * Description :
 */
public class JsonUtils {
    public static DataDetail parserDataDetailJson(String json) {

        DataDetail data = new DataDetail();
        try {
            JSONObject result = new JSONObject(json);
            JSONObject answerObj = result.optJSONObject("answer");
            //案由
            JSONArray casesArray = result.optJSONArray("cases");
            List<CaseCause> caseCauses = new ArrayList<>();
            CaseCause caseCause;
            JSONObject caseObj;
            for (int i = 0; i < casesArray.length(); i++) {
                caseCause = new CaseCause();
                Iterator<String> keys = casesArray.optJSONObject(i).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    caseObj = casesArray.optJSONObject(i).optJSONObject(key);
                    caseCause.setCId(caseObj.optString("id"));
                    caseCause.setName(caseObj.optString("title"));
                    caseCause.setDescription(caseObj.optString("content"));
                }
                caseCauses.add(caseCause);
            }
            data.setCases(caseCauses);  // 添加案由
            //请求
            JSONArray claimsArray = result.optJSONArray("claims");
            List<Claim> claims = new ArrayList<>();
            Claim claim;
            JSONObject claimObj;
            for (int i = 0; i < claimsArray.length(); i++) {
                claim = new Claim();
                claimObj = claimsArray.optJSONObject(i);
                claim.setName(claimObj.optString("tag"));
                claim.setReject(claimObj.optInt("reject"));
                claim.setSupport(claimObj.optInt("support"));
                claims.add(claim);
            }
            data.setClaims(claims);//添加断言
            //证据
            JSONArray evidencesArray = result.optJSONArray("evidences");
            List<Evidence> evidences = new ArrayList<>();
            Evidence evidence;
            JSONObject evidenceObj;
            for (int i = 0; i < evidencesArray.length(); i++) {
                evidence = new Evidence();
                evidenceObj = evidencesArray.optJSONObject(i);
                evidence.setTag(evidenceObj.optString("tag"));
                evidence.setCount(evidenceObj.optInt("count"));
                evidences.add(evidence);
            }

            data.setEvidences(evidences);//添加证据
            //  focus
            JSONArray focusArray = result.optJSONArray("focus");
            List<Focus> focuses = new ArrayList<>();
            Focus focus;
            JSONObject focusObj;
            for (int i = 0; i < focusArray.length(); i++) {
                focus = new Focus();
                focusObj = focusArray.optJSONObject(i);
                focus.setCount(focusObj.optInt("count"));
                focus.setTag(focusObj.optString("tag"));
                focuses.add(focus);
            }
            data.setFocus(focuses);
            //指导案例
            JSONArray guidesArray = result.optJSONArray("guides");
            List<GuidanceCase> guidanceCases = new ArrayList<>();
            GuidanceCase guide;
            JSONObject guideObj;
            for (int i = 0; i < guidesArray.length(); i++) {
                guide = new GuidanceCase();
                Iterator<String> keys = guidesArray.optJSONObject(i).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    guideObj = guidesArray.optJSONObject(i).optJSONObject(key);
                    guide.setId(guideObj.optString("id"));
                    guide.setGist(guideObj.optString("gist"));
                    guide.setTitle(guideObj.optString("title"));
                }
                guidanceCases.add(guide);
            }
            data.setGuides(guidanceCases);//添加指导案例
            //添加法律标签
            JSONArray lawTagsArray = result.optJSONArray("law_tags");
            List<LawTag> lawTags = new ArrayList<>();
            LawTag lawTag;
            JSONObject lawTagObj;
            for (int i = 0; i < lawTagsArray.length(); i++) {
                lawTag = new LawTag();
                lawTagObj = lawTagsArray.optJSONObject(i);
                lawTag.setId(lawTagObj.optString("count"));
                lawTag.setZhName(lawTagObj.optString("tag"));
                lawTags.add(lawTag);
            }
            data.setLaw_tags(lawTags);//添加法律标签
            //  法律知识
            JSONArray lawsArray = result.optJSONArray("laws");
            List<Law> lists = new ArrayList<>();
            Law law;
            JSONObject lawObj;
            for (int i = 0; i < lawsArray.length(); i++) {
                law = new Law();
                Iterator<String> keys = lawsArray.optJSONObject(i).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    lawObj = lawsArray.optJSONObject(i).optJSONObject(key);
                    law.setName(lawObj.optString("title"));
                    law.setItem(lawObj.optString("item"));
                    law.setId(lawObj.optString("id"));
                    law.setContent(lawObj.optString("content"));
                    law.setTag(lawObj.optString("tag"));
                }
                lists.add(law);
            }
            data.setLaws(lists);//添加法律知识
            //司法观点
            JSONArray pointsArray = result.optJSONArray("points");
            List<JudicialPoint> points = new ArrayList<JudicialPoint>();
            JudicialPoint point;
            JSONObject pointObj;
            for (int i = 0; i < pointsArray.length(); i++) {
                point = new JudicialPoint();
                Iterator<String> keys = pointsArray.optJSONObject(i).keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    pointObj = pointsArray.optJSONObject(i).optJSONObject(key);
                    point.setTitle(pointObj.optString("title"));
                    point.setId(pointObj.optString("id"));
                    point.setContent(pointObj.optString("content"));
                }
                points.add(point);
            }
            data.setPoints(points);//添加司法观点
            //驳回原因
            JSONArray rejectCauseArray = result.optJSONArray("reject_cause");
            List<RejectReason> reasons = new ArrayList<>();
            RejectReason reason;
            JSONObject reasonObj;
            for (int i = 0; i < rejectCauseArray.length(); i++) {
                reason = new RejectReason();
                reasonObj = rejectCauseArray.optJSONObject(i);
                reason.setCount(reasonObj.optLong("count"));
                reason.setName(reasonObj.optString("tag"));
                reasons.add(reason);
            }
            data.setReject_cause(reasons);//添加驳回原因
            //添加诉讼流程
            JSONObject lawFlowObj = result.optJSONObject("law_flow");
            //诉讼流程
            JSONObject FlowObj = lawFlowObj.optJSONObject("flow1");
            JSONArray flowArray = FlowObj.optJSONArray("steps");
            LitigationFlow flow = new LitigationFlow();
            List<FlowStep> steps = new ArrayList<>();
            FlowStep step;
            FlowSubStep subStep;
            Template template;
            JSONObject stepObj;
            JSONObject subStepObj;
            JSONObject templateObj;
            JSONObject flowSubTemplateObj;
            JSONArray subStepsArray;
            JSONArray templateArray;
            JSONArray flowSubTemplateArray;
            List<FlowSubStep> subSteps;
            List<Template> templates;
            for (int i = 0; i < flowArray.length(); i++) {
                step = new FlowStep();
                stepObj = flowArray.optJSONObject(i);
                step.setDetail(stepObj.optString("detail"));
                step.setTitle(stepObj.optString("title"));
                subStepsArray = stepObj.optJSONArray("sub_steps");
                templateArray = stepObj.optJSONArray("template");
                subSteps = new ArrayList<>();
                //子流程
                if (subStepsArray!=null){
                    for (int j = 0; j < subStepsArray.length(); j++) {
                        subStep = new FlowSubStep();
                        subStepObj = subStepsArray.optJSONObject(j);
                        subStep.setTitle(subStepObj.optString("title"));
                        subStep.setDetail(subStepObj.optString("detail"));
                        flowSubTemplateArray = subStepObj.optJSONArray("template");

                        List<Template> flowSubTemplates=new ArrayList<>();
                        if (flowSubTemplateArray!=null){
                            for (int k = 0; k < flowSubTemplateArray.length(); k++) {
                                Template   flowSubTemplate=new Template();
                                flowSubTemplateObj = flowSubTemplateArray.optJSONObject(k);
                                flowSubTemplate.setDocId(flowSubTemplateObj.optInt("docId"));
                                flowSubTemplate.setId(flowSubTemplateObj.optString("id"));
                                flowSubTemplate.setName(flowSubTemplateObj.optString("name"));
                                flowSubTemplate.setUrl(flowSubTemplateObj.optString("url"));
                                flowSubTemplates.add(flowSubTemplate);
                            }
                        }

                        subStep.setTemplates(flowSubTemplates);
                        subSteps.add(subStep);
                    }
                }
                templates = new ArrayList<>();
                //模板
                if (templateArray!=null){
                    for (int j = 0; j < templateArray.length(); j++) {
                        template = new Template();
                        templateObj = templateArray.optJSONObject(j);
                        template.setDocId(templateObj.optInt("docId"));
                        template.setId(templateObj.optString("id"));
                        template.setName(templateObj.optString("name"));
                        template.setUrl(templateObj.optString("url"));
                        templates.add(template);
                    }
                }
                step.setTemplates(templates);
                step.setSubSteps(subSteps);
                steps.add(step);
            }
            flow.setSteps(steps);
            data.setLitigationFlowFlow(flow);
            //非诉讼流程
            JSONObject unFlowObj = lawFlowObj.optJSONObject("flow2");
            JSONArray unFlowArray = unFlowObj.optJSONArray("steps");
            UnLitigationFlow unFlow = new UnLitigationFlow();
            List<FlowStep> unSteps = new ArrayList<>();
            FlowStep unStep;
            FlowSubStep unSubStep;
            Template unTemplate;
            Template unSubTemplate;
            JSONObject unStepObj;
            JSONObject unSubStepObj;
            JSONObject unTemplateObj;
            JSONObject unSubTempObj;
            JSONArray unSubStepsArray;
            JSONArray unTemplateArray;
            List<FlowSubStep> unSubSteps;
            List<Template> unTemplates;
            List<Template> unFlowSubTemplates;
            for (int i = 0; i < unFlowArray.length(); i++) {
                unStep = new FlowStep();
                unStepObj = unFlowArray.optJSONObject(i);
                unStep.setDetail(unStepObj.optString("detail"));
                unStep.setTitle(unStepObj.optString("title"));
                unSubStepsArray = unStepObj.optJSONArray("sub_steps");
                unTemplateArray = unStepObj.optJSONArray("template");
                unSubSteps = new ArrayList<>();
                if (unSubStepsArray != null){
                    for (int j = 0; j < unSubStepsArray.length(); j++) {
                        unSubStep = new FlowSubStep();
                        unSubStepObj = unSubStepsArray.optJSONObject(j);
                        unSubStep.setTitle(unSubStepObj.optString("title"));
                        unSubStep.setDetail(unSubStepObj.optString("detail"));
                        JSONArray unFlowSubTemplateArray = unSubStepObj.optJSONArray("template");
                        unFlowSubTemplates = new ArrayList<>();
                        //子流程
                        if (unFlowSubTemplateArray!=null){
                            for (int k = 0; k < unFlowSubTemplateArray.length(); k++) {
                                unSubTemplate = new Template();
                                unSubTempObj = unFlowSubTemplateArray.optJSONObject(k);
                                unSubTemplate.setDocId(unSubTempObj.optInt("docId"));
                                unSubTemplate.setId(unSubTempObj.optString("id"));
                                unSubTemplate.setName(unSubTempObj.optString("name"));
                                unSubTemplate.setUrl(unSubTempObj.optString("url"));
                                unFlowSubTemplates.add(unSubTemplate);
                            }
                        }
                        unSubStep.setTemplates(unFlowSubTemplates);
                        unSubSteps.add(unSubStep);
                    }
                }
                unTemplates = new ArrayList<>();
                if (unTemplateArray!=null){
                    for (int j = 0; j < unTemplateArray.length(); j++) {
                        unTemplate = new Template();
                        unTemplateObj = unTemplateArray.optJSONObject(j);
                        unTemplate.setDocId(unTemplateObj.optInt("docId"));
                        unTemplate.setId(unTemplateObj.optString("id"));
                        unTemplate.setName(unTemplateObj.optString("name"));
                        unTemplate.setUrl(unTemplateObj.optString("url"));
                        unTemplates.add(unTemplate);
                    }
                }
                unStep.setTemplates(unTemplates);
                unStep.setSubSteps(unSubSteps);
                unSteps.add(unStep);
            }
            unFlow.setSteps(unSteps);
            data.setUnLitigationFlow(unFlow);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
