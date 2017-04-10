package com.info.aegis.lawpush4android.model.bean.entity.law;
/**
 * Created by meissen on 2016/11/3.
 * 模板
 */
public class DocTemplate {


    private String id;
    /**
     * 文书模板id
     */
    private Integer docId;
    /**
     * 文书模板名字
     */
    private String name;
    /**
     * 文书模板下载链接
     */
    private String url;

    public DocTemplate(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
