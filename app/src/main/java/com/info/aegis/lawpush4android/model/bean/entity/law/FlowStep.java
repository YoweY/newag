package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.List;

/**
 * Created by SFS on 2017/1/12.
 * Description : 流程步骤
 */

public class FlowStep {
    private String detail;
    private String title;
    private List<FlowSubStep> subSteps;
    private List<Template> templates;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FlowSubStep> getSubSteps() {
        return subSteps;
    }

    public void setSubSteps(List<FlowSubStep> subSteps) {
        this.subSteps = subSteps;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    @Override
    public String toString() {
        return "FlowStep{" +
                "detail='" + detail + '\'' +
                ", title='" + title + '\'' +
                ", subSteps=" + subSteps +
                ", templates=" + templates +
                '}';
    }
}
