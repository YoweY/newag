package com.info.aegis.lawpush4android.model.bean.entity.law;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by wuhao_000 on 2015/5/12.
 */
public class Law {

    //法律名称
    protected String name;
    //第多少条
    private String item;
    private String content;
    // 标签
    private String tag;


    private Integer[] caseCauseIds;
    private String id;
    private Set<String> keywords;
    private Date lastUpdateTime = new Date();
    private String sourceId;
    private List<BookInfo> bookInfos;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Law() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getDisplayName() {
        return this.name + this.item;
    }

    public Integer[] getCaseCauseIds() {
        return caseCauseIds;
    }

    public void setCaseCauseIds(Integer[] caseCauseIds) {
        this.caseCauseIds = caseCauseIds;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public List<BookInfo> getBookInfos() {
        return bookInfos;
    }

    public void setBookInfos(List<BookInfo> bookInfos) {
        this.bookInfos = bookInfos;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Law{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", item='" + item + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
