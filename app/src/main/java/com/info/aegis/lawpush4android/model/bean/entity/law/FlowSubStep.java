package com.info.aegis.lawpush4android.model.bean.entity.law;

import java.util.List;

/**
 * Created by SFS on 2017/1/12.
 * Description : 流程子步骤
 */

public class FlowSubStep {
    private String detail;
    private String title;

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

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}
