package com.info.aegis.lawpush4android.model.bean;

import java.util.List;

/**
 * 作者：jksfood on 2017/2/27 21:35
 */

public class ScenesBean {
    private int grade;
    private String id;
    private String name;
    private String parent;
    private int priority;
    private List<String> sonStringList;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getSonStringList() {
        return sonStringList;
    }

    public void setSonStringList(List<String> sonStringList) {
        this.sonStringList = sonStringList;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "grade=" + grade +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parent='" + parent + '\'' +
                ", priority=" + priority +
                ", sonStringList=" + sonStringList +
                '}';
    }
}
