package com.info.aegis.lawpush4android.model.bean.entity.law;


import java.util.List;

/**
 * Created by meissen on 2016/11/3.
 */
public class LawFlow {

    private String id;
    /**
     * 案由id
     */
    private Integer caseCauseId;
    /**
     * 顶级流程类型，1为诉讼流程，0为非诉流程
     */
    private Integer type;
    /**
     * 一级流程
     */
    private List<Step> steps;
    /**
     * 文书模板，0表示没有，其他数字为各自的模板，具体见索引DocTemplate
     */
    private Integer[] template;

    public LawFlow(){

    }

    public Integer getCaseCauseId() {
        return caseCauseId;
    }

    public void setCaseCauseId(Integer caseCauseId) {
        this.caseCauseId = caseCauseId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer[] getTemplate() {
        return template;
    }

    public void setTemplate(Integer[] template) {
        this.template = template;
    }

    public static class Step {
        /**
         * 一级流程标题
         */
        private String title;
        /**
         * 二级流程
         */
        private List<SubStep> subSteps;
        private List<Template> templates;
        /**
         * 一级流程详细内容
         */
        private String detail;
        /**
         * 文书模板，0表示没有，其他数字为各自的模板，具体见索引DocTemplate
         */
        private Integer[] template;

        public Step(){}

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SubStep> getSubSteps() {
            return subSteps;
        }

        public void setSubSteps(List<SubStep> subSteps) {
            this.subSteps = subSteps;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public List<Template> getTemplates() {
            return templates;
        }

        public void setTemplates(List<Template> templates) {
            this.templates = templates;
        }

        public Integer[] getTemplate() {
            return template;
        }

        public void setTemplate(Integer[] template) {
            this.template = template;
        }
    }

    public static class SubStep {

        /**
         * 二级流程标题
         */
        private String title;
        /**
         * 二级流程详细内容
         */
        private String detail;
        /**
         * 文书模板，0表示没有，其他数字为各自的模板，具体见索引DocTemplate
         */
        private Integer template[];

        public SubStep(){}

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public Integer[] getTemplate() {
            return template;
        }

        public void setTemplate(Integer[] template) {
            this.template = template;
        }
    }
}
